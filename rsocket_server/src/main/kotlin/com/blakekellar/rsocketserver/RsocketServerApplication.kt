package com.blakekellar.rsocketserver

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.rsocket.kotlin.DefaultPayload
import io.rsocket.kotlin.Payload
import io.rsocket.kotlin.RSocketFactory
import io.rsocket.kotlin.transport.netty.server.TcpServerTransport
import io.rsocket.kotlin.util.AbstractRSocket
import mu.KotlinLogging
import org.reactivestreams.Publisher
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component

@SpringBootApplication
class RsocketServerApplication

fun main(args: Array<String>) {
    runApplication<RsocketServerApplication>(*args)
    Thread.currentThread().join()
}

@Component
class RsocketServer() {

    private val logger = KotlinLogging.logger {}
    private val server: io.reactivex.disposables.Disposable
    private val rSocketHandler: RSocketHandler

    init {
        this.rSocketHandler = RSocketHandler()
        this.server = RSocketFactory
                .receive()
                .acceptor { { _, _ -> Single.just(rSocketHandler) } }
                .transport(TcpServerTransport.create("127.0.0.1", 4242))
                .start()
                .subscribe()
    }

    internal class RSocketHandler : AbstractRSocket() {

        private val logger = KotlinLogging.logger {}

        private fun payloadFactory(payload: Payload): Payload {
            return DefaultPayload(payload.dataUtf8.reversed(), payload.metadataUtf8)
        }

        override fun fireAndForget(payload: Payload): Completable {
            logger.info("<< FF metadata=${payload.metadataUtf8} data=${payload.dataUtf8}")
            logger.info(">> FF")
            return Completable.complete()
        }

        override fun requestResponse(payload: Payload): Single<Payload> {
            logger.info("<< RR metadata=${payload.metadataUtf8} data=${payload.dataUtf8}")
            logger.info(">> RR")
            return Single.just(payloadFactory(payload))
        }

        override fun requestStream(payload: Payload): Flowable<Payload> {
            logger.info("<< RS metadata=${payload.metadataUtf8} data=${payload.dataUtf8}")
            val reversedPayload = payloadFactory(payload)
            logger.info(">> RS metadata=${reversedPayload.metadataUtf8} data=${reversedPayload.dataUtf8}")
            return Flowable.just(reversedPayload)
        }

        override fun requestChannel(payloads: Publisher<Payload>): Flowable<Payload> {
            return Flowable.fromPublisher(payloads).flatMap { payload ->
                logger.info("<< RC metadata=${payload.metadataUtf8} data=${payload.dataUtf8}")
                val reversedPayload = payloadFactory(payload)
                logger.info(">> RC metadata=${reversedPayload.metadataUtf8} data=${reversedPayload.dataUtf8}")
                Flowable.just(reversedPayload)
            }
        }

        override fun metadataPush(payload: Payload): Completable {
            logger.info("<< MP metadata=${payload.metadataUtf8} data=${payload.dataUtf8}")
            logger.info(">> MP")
            return Completable.complete()
        }
    }
}