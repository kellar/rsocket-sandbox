package com.blakekellar.rsocketconsumer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class RsocketConsumer

fun main(args: Array<String>) {
	runApplication<RsocketConsumer>(*args)
}
