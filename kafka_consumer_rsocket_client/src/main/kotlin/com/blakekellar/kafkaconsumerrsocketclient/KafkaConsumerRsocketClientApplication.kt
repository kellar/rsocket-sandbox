package com.blakekellar.kafkaconsumerrsocketclient

import io.reactivex.Flowable
import io.rsocket.kotlin.DefaultPayload
import io.rsocket.kotlin.RSocket
import io.rsocket.kotlin.RSocketFactory
import io.rsocket.kotlin.transport.netty.client.TcpClientTransport
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
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Headers
import org.springframework.stereotype.Component

@SpringBootApplication
class KafkaConsumerRsocketClientApplication

fun main(args: Array<String>) {
    runApplication<KafkaConsumerRsocketClientApplication>(*args)
}

@Configuration
@EnableKafka
class AppBeans {

    @Bean
    fun consumerFactory(): ConsumerFactory<Any, Any> {
        val props = mutableMapOf<String, Any>()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        props[ConsumerConfig.GROUP_ID_CONFIG] = "group"
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        return DefaultKafkaConsumerFactory(props)
    }

    @Bean
    fun concurrentKafkaListenerContainerFactory(consumerFactory: ConsumerFactory<Any, Any>): ConcurrentKafkaListenerContainerFactory<String, String> {
        val factory = ConcurrentKafkaListenerContainerFactory<String, String>()
        factory.consumerFactory = consumerFactory
        return factory
    }
}

@Component
class KafkaConsumer(
        @Autowired val rsocketClient: RsocketClient
) {

    private val logger = KotlinLogging.logger {}

    @KafkaListener(topics = ["topic"], groupId = "group")
    fun receive(@org.springframework.messaging.handler.annotation.Payload payload: String,
                @Headers headers: Map<String, Any>) {
        logger.info("Received kafka message $payload with headers $headers")
        rsocketClient.exchange(payload, headers[KafkaHeaders.RECEIVED_MESSAGE_KEY] as String?)
    }
}

@Component
class RsocketClient {

    private val logger = KotlinLogging.logger {}
    private val rSocket: RSocket = RSocketFactory
            .connect()
            .transport(TcpClientTransport.create("127.0.0.1", 4242))
            .start()
            .blockingGet()

    fun exchange(payloadText: String, keyText: String?) {
        val payload = DefaultPayload.text(payloadText, keyText)

        logger.info(">> FF metadata=${payload.metadataUtf8} data=${payload.dataUtf8}")
        val ffComplete = rSocket.fireAndForget(payload)
        ffComplete.subscribe {
            logger.info("<< FF")
        }

        logger.info(">> RR metadata=${payload.metadataUtf8} data=${payload.dataUtf8}")
        val response = rSocket.requestResponse(payload)
        response.subscribe { it ->
            logger.info("<< RR metadata=${it.metadataUtf8} data=${it.dataUtf8}")
        }

        logger.info(">> RS metadata=${payload.metadataUtf8} data=${payload.dataUtf8}")
        val stream = rSocket.requestStream(payload)
        stream.subscribe { it ->
            logger.info("<< RS metadata=${it.metadataUtf8} data=${it.dataUtf8}")
        }

        logger.info(">> RC metadata=${payload.metadataUtf8} data=${payload.dataUtf8}")
        val channel = rSocket.requestChannel(Flowable.just(payload))
        channel.subscribe { it ->
            logger.info("<< RC metadata=${it.metadataUtf8} data=${it.dataUtf8}")
        }

        logger.info(">> MP metadata=${payload.metadataUtf8} data=${payload.dataUtf8}")
        val metadata = rSocket.metadataPush(payload)
        metadata.subscribe {
            logger.info("<< MP metadata=${payload.metadataUtf8} data=${payload.dataUtf8}")
        }
    }
}
