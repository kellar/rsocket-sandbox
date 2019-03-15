package com.blakekellar.kafkaconsumerrsocketclient

import io.reactivex.disposables.Disposable
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
import reactor.core.publisher.Flux
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
import reactor.kafka.receiver.ReceiverRecord
import java.util.*

@SpringBootApplication
class KafkaConsumerRsocketClientApplication

fun main(args: Array<String>) {
    runApplication<KafkaConsumerRsocketClientApplication>(*args)
    Thread.currentThread().join()
}

@Configuration
class AppBeans {

    private val logger = KotlinLogging.logger {}

    @Bean
    fun rSocketClient(): RSocket {
        return RSocketFactory
                .connect()
                .transport(TcpClientTransport.create("127.0.0.1", 4242))
                .start()
                .blockingGet()
    }

    @Bean
    fun kafkaPublisher(): Flux<ReceiverRecord<String, String>> {
        val properties = mutableMapOf<String, Any>()
        properties[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        properties[ConsumerConfig.CLIENT_ID_CONFIG] = "clientid"
        properties[ConsumerConfig.GROUP_ID_CONFIG] = "group"
        properties[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java // TODO: Int keys
        properties[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        properties[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "latest"
        val receiverOptions = ReceiverOptions.create<String, String>(properties).subscription(Collections.singleton("topic"))
        return KafkaReceiver.create(receiverOptions).receive()
    }

    @Bean
    fun disposableSubscriptionFromServer(@Autowired rSocket: RSocket,
                                         @Autowired kafkaPublisher: Flux<ReceiverRecord<String, String>>): Disposable {
        return rSocket.requestChannel(
                kafkaPublisher
                        .map { it ->
                            val payload = DefaultPayload.text(it.value(), it.key().toString())
                            logger.info(">> metadata=${payload.metadataUtf8} data=${payload.dataUtf8}")
                            payload
                        })
                .subscribe { it ->
                    logger.info("<< metadata=${it.metadataUtf8} data=${it.dataUtf8}")
                }
    }

}
