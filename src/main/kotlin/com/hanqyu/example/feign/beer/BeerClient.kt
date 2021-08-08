package com.hanqyu.example.feign.beer

import com.hanqyu.example.feign.beer.response.BeerItems
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(
    name = "beerClient",
    url = "\${beer-api.domain}",
    configuration = [BeerClientConfiguration::class]
)
interface BeerClient {
    @GetMapping("/beers")
    fun getBeer(@RequestParam size: Int): BeerItems
}