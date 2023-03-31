package corp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.ComponentScan


@SpringBootApplication
@ComponentScan("corp.*")
@EnableCaching
open class Main
    fun main(args: Array<String>) {
        runApplication<Main>(*args)
    }




