package com.hanqyu.example.feign.beer.response

data class Beer(
    val id: Long,
    val name: String,
    val type: Type
) {
    enum class Type {
        ALE,
        LARGER
        ;
    }
}
