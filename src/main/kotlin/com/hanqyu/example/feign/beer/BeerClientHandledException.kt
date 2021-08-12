package com.hanqyu.example.feign.beer

class BeerClientHandledException(
    val errorCode: String,
    message: String?
) : RuntimeException(message)