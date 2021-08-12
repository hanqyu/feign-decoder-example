package com.hanqyu.example.feign.beer

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.hanqyu.example.feign.CommonResponse
import feign.Response
import feign.codec.ErrorDecoder
import java.lang.Exception

class BeerClientErrorDecoder: ErrorDecoder {
    override fun decode(methodKey: String, response: Response): Exception {
        val errorData = parse(response)
        errorData?.code?.let {
            return BeerClientHandledException(it, errorData.message)
        }
        return ErrorDecoder.Default().decode(methodKey, response)
    }

    private fun parse(response: Response): CommonResponse.ErrorData? {
        return runCatching {
             objectMapper.readValue(response.body().asInputStream(), CommonResponse::class.java)?.error
        }.getOrNull()
    }

    private val objectMapper = jacksonObjectMapper()
        .findAndRegisterModules()
}
