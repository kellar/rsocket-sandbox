package com.blakekellar.kafkaproducer

import mu.KotlinLogging
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.common.serialization.StringSerializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant

@SpringBootApplication
class KafkaProducer

fun main(args: Array<String>) {
    runApplication<KafkaProducer>(*args)
}

@Configuration
@EnableScheduling
class AppBeans {

    private val logger = KotlinLogging.logger {}

    @Bean
    fun embeddedKafkaBroker(): EmbeddedKafkaBroker {
        return EmbeddedKafkaBroker(1, true, 1, ScheduledKafkaProducerService.TOPIC).kafkaPorts(9092)
    }

    @Bean
    fun producerConfig(broker: EmbeddedKafkaBroker): Map<String, Any> {
        val producerConfigs = mutableMapOf<String, Any>()
        producerConfigs[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = broker.brokersAsString
        producerConfigs[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        producerConfigs[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        producerConfigs[ProducerConfig.CLIENT_ID_CONFIG] = ScheduledKafkaProducerService.PRODUCER_CLIENT_ID
        return producerConfigs
    }

    @Bean
    fun producerFactory(producerConfig: Map<String, Any>): ProducerFactory<String, String> {
        return DefaultKafkaProducerFactory(producerConfig)
    }

    @Bean
    fun kafkaTemplate(producerFactory: ProducerFactory<String, String>): KafkaTemplate<String, String> {
        return KafkaTemplate(producerFactory)
    }
}

@Component
class ScheduledKafkaProducerService(
        @Autowired private val kafkaTemplate: KafkaTemplate<String, String>
) {

    private val logger = KotlinLogging.logger {}

    @Scheduled(fixedRate = 10000L)
    fun schedluledProduce() {
        logger.info("Writing a message to kafka")
        kafkaTemplate.send(ScheduledKafkaProducerService.TOPIC, Instant.now().toString())
        logger.info("Wrote a message to kafka")
    }

    companion object {
        const val TOPIC: String = "topic"
        const val PRODUCER_CLIENT_ID: String = "client_id"
    }
}