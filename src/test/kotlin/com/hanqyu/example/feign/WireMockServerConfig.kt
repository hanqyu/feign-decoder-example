package com.hanqyu.example.feign

import com.github.tomakehurst.wiremock.WireMockServer
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class WireMockServerConfig {
    @Bean(initMethod = "start", destroyMethod = "stop")
    fun mockServer(): WireMockServer {
        return WireMockServer(8081)
    }
}