package com.blakekellar.kafkaproducer

import com.fasterxml.jackson.databind.ser.std.StringSerializer
import org.apache.kafka.clients.producer.ProducerConfig
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant

@SpringBootApplication
class KafkaProducer

fun main(args: Array<String>) {
    runApplication<KafkaProducer>(*args)
}

@Configuration
class AppBeans {

    // Makes demo easy as broker is app configured.
    @Bean
    fun kafkaBroker(): EmbeddedKafkaBroker {
        return EmbeddedKafkaBroker(1, false, 10, ScheduledKafkaProducerService.TOPIC)
    }

    @Bean
    fun producerConfigs(): Map<String, Any> {
        val producerConfigs = mutableMapOf<String, Any>()
        // list of host:port pairs used for establishing the initial connections to the Kakfa cluster
        producerConfigs[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost"
        producerConfigs[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        producerConfigs[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        return producerConfigs
    }

    @Bean
    fun producerFactory(): ProducerFactory<String, String> {
        return DefaultKafkaProducerFactory(producerConfigs())
    }

    @Bean
    fun kafkaTemplate(): KafkaTemplate<String, String> {
        return KafkaTemplate(producerFactory())
    }
}

@Component
class ScheduledKafkaProducerService {

    @Autowired
    private lateinit var kafkaTemplate: KafkaTemplate<String, String>

    val logger : Logger = LoggerFactory.getLogger(ScheduledKafkaProducerService::class.java)

    @Scheduled(fixedRate = 10000L)
    fun schedluledProduce() {
        logger.info("Writing a message to kafka")
        kafkaTemplate.send(ScheduledKafkaProducerService.TOPIC, Instant.now().toString())
        logger.info("Wrote a message to kafka")
    }

    companion object {
        const val TOPIC: String = "topic"
    }
}