package com.hanqyu.example.feign

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FeignApplication

fun main(args: Array<String>) {
    runApplication<FeignApplication>(*args)
}
