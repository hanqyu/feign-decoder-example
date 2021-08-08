package com.hanqyu.example.feign.beer

import com.fasterxml.jackson.databind.type.TypeFactory
import feign.Response
import feign.codec.Decoder
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder
import org.springframework.core.ResolvableType
import java.lang.reflect.Type

class BeerClientDecoder(decoder: Decoder) : ResponseEntityDecoder(decoder) {
    override fun decode(response: Response, type: Type): Any? {
        val returnType = TypeFactory.rawClass(type)
        val forClassWithGenerics = ResolvableType.forClassWithGenerics(BeerCommonResponse::class.java, returnType)
        return runCatching {
            (super.decode(response, forClassWithGenerics.type) as BeerCommonResponse<*>).data
        }.getOrDefault(
            super.decode(response, type)
        )
    }
}