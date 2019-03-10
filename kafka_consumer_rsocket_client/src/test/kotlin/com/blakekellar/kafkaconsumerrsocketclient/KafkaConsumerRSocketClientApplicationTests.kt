package com.blakekellar.kafkaconsumerrsocketclient

import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.rsocket.kotlin.RSocketFactory
import io.rsocket.kotlin.transport.netty.server.TcpServerTransport
import io.rsocket.kotlin.util.AbstractRSocket
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
@EmbeddedKafka(ports = [9092])
class KafkaConsumerRSocketClientApplicationTests {

    private val rSocketServer: Disposable

    internal class RSocketHandler : AbstractRSocket()

    init {
        this.rSocketServer = RSocketFactory
                .receive()
                .acceptor { { _, _ -> Single.just(RSocketHandler()) } }
                .transport(TcpServerTransport.create("127.0.0.1", 4242))
                .start()
                .subscribe()
    }

    @Test
    fun contextLoads() {
    }

}
