package com.blakekellar.rsocketproducer

import mu.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.messaging.handler.annotation.Headers
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@SpringBootApplication
class RsocketProducerApplication

fun main(args: Array<String>) {
    runApplication<RsocketProducerApplication>(*args)
}

@Configuration
@EnableKafka
class AppBeans {

    @Bean
    fun consumerFactory(): ConsumerFactory<Any,Any> {
        val props = mutableMapOf<String, Any>()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        props[ConsumerConfig.GROUP_ID_CONFIG] = "group"
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        return DefaultKafkaConsumerFactory(props)
    }

    @Bean
    fun concurrentKafkaListenerContainerFactory(consumerFactory: ConsumerFactory<Any,Any>): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = consumerFactory
        return factory
    }
}

@Component
class KafkaConsumer(
        @Autowired val rsocketProducer: RsocketProducer
) {

    private val logger = KotlinLogging.logger {}

    @KafkaListener(topics = ["topic"], groupId = "group")
    fun receive(@Payload payload: String, @Headers headers: Map<String, Any>) {
        logger.info("Received message $payload with headers $headers")
        rsocketProducer.produce()
    }
}

@Component
class RsocketProducer() {

    private val logger = KotlinLogging.logger {}

    fun produce() {
        logger.info("Rsocket producer stub produce()")
    }

}