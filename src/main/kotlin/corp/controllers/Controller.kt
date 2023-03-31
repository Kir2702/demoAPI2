package corp.controllers

import corp.services.PointService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
open class Controller @Autowired constructor(
    private val pointService: PointService
) {


    @GetMapping(value = ["/"])
    open fun default(): String{
        return "http://217.197.116.117:84/point?type=area&country=russia&state=Москва"
    }

    @GetMapping(value = ["/point"])
    @Cacheable("getPoint")
    open fun getPoint(
        @RequestParam("country") country: String,
        @RequestParam("state") state: String,
        @RequestParam("type") type: String
    ): String {
        return pointService.getPoint(country, state, type)
    }
}