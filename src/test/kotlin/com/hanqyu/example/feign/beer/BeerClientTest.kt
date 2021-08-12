package com.hanqyu.example.feign.beer

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.hanqyu.example.feign.FeignEnablingConfiguration
import com.hanqyu.example.feign.WireMockServerConfig
import com.hanqyu.example.feign.beer.response.Beer
import feign.FeignException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.openfeign.FeignAutoConfiguration
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@ImportAutoConfiguration(FeignAutoConfiguration::class, HttpMessageConvertersAutoConfiguration::class)
@SpringBootTest(classes = [FeignEnablingConfiguration::class, WireMockServerConfig::class])
internal class BeerClientTest {

    @Autowired
    private lateinit var sut: BeerClient

    @Autowired
    private lateinit var mockServer: WireMockServer

    @Test
    fun getBeer() {
        val responseBody: String = """
                {
                    "result": "SUCCESS",
                    "data": {
                        "items": [
                            {
                                "id": 1,
                                "name": "빅 웨이브",
                                "type": "ALE"
                            },
                            {
                                "id": 2,
                                "name": "카스",
                                "type": "LARGER"
                            }
                        ]
                    },
                    "error": null
                }
            """.trimIndent()
        mockServer.stubFor(
            get(urlPathEqualTo("/beers"))
                .withQueryParam("size", equalTo("2"))
                .withHeader("Authorization", equalTo("AUTH-KEY"))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse().withBody(responseBody).withHeader("Content-Type", "application/json"))
        )

        val then = sut.getBeer(2)
        then.items.size shouldBe 2
        then.items.map { it.name } shouldBe listOf("빅 웨이브", "카스")
        then.items.map { it.type } shouldBe listOf(Beer.Type.ALE, Beer.Type.LARGER)
    }

    @Test
    fun handledError() {
        val responseBody = """
                {
                    "result": "FAILED",
                    "data": null,
                    "error": {
                        "code": "SOME_THING",
                        "message": "error message",
                        "data": null
                    }
                }
            """.trimIndent()
        mockServer.stubFor(
            get(urlPathEqualTo("/beers"))
                .withQueryParam("size", equalTo("2"))
                .withHeader("Authorization", equalTo("AUTH-KEY"))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(
                    aResponse()
                        .withStatus(HttpStatus.BAD_REQUEST.value())
                        .withBody(responseBody)
                        .withHeader("Content-Type", "application/json")
                )
        )

        val then = shouldThrow<BeerClientHandledException> { sut.getBeer(2) }
        then.errorCode shouldBe "SOME_THING"
        then.message shouldBe "error message"
    }
    @Test
    fun unhandledError() {
        val responseBody = """
                response failed to be parsed
            """.trimIndent()
        mockServer.stubFor(
            get(urlPathEqualTo("/beers"))
                .withQueryParam("size", equalTo("2"))
                .withHeader("Authorization", equalTo("AUTH-KEY"))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(
                    aResponse()
                        .withStatus(HttpStatus.BAD_REQUEST.value())
                        .withBody(responseBody)
                        .withHeader("Content-Type", "application/json")
                )
        )

        shouldThrow<FeignException.FeignClientException> {
            sut.getBeer(2)
        }
    }
}