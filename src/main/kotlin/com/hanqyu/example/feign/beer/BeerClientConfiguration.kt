package com.hanqyu.example.feign.beer

import feign.RequestInterceptor
import org.springframework.beans.factory.ObjectFactory
import org.springframework.boot.autoconfigure.http.HttpMessageConverters
import org.springframework.cloud.openfeign.support.SpringDecoder
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter

class BeerClientConfiguration {
    @Bean
    fun decoder(): BeerClientDecoder {
        val httpMessageConverters = ObjectFactory<HttpMessageConverters> {
            HttpMessageConverters()
        }
        return BeerClientDecoder(SpringDecoder(httpMessageConverters))
    }

    @Bean
    fun defaultRequestHeaders(): RequestInterceptor {
        return RequestInterceptor {
            it.header(HttpHeaders.AUTHORIZATION, "AUTH-KEY")
            it.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        }
    }
}