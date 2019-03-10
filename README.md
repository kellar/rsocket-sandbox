# rsocket-sandbox

Kafka producer / embedded kafka broker -> kafka consumer / rsocket client -> rsocket server

```
./gradlew clean build
java -jar kafka_producer/build/libs/kafka_producer-0.0.1-SNAPSHOT.jar --spring.config.location=kafka_producer/application.yml
java -jar rsocket_server/build/libs/rsocket_server-0.0.1-SNAPSHOT.jar --spring.config.location=rsocket_server/application.yml 
java -jar kafka_consumer_rsocket_client/build/libs/kafka_consumer_rsocket_client-0.0.1-SNAPSHOT.jar --spring.config.location=kafka_consumer_rsocket_client/application.yml 
```

Kafka Producer
```
2019-03-09 21:53:59.195  INFO 4896 --- [   scheduling-1] c.b.k.ScheduledKafkaProducerService      : >> key=5 payload=2019-03-10T03:53:59.195204Z
```

Kafka Client / RSocket Client
```
2019-03-09 21:53:59.201  INFO 5098 --- [ntainer#0-0-C-1] c.b.k.KafkaConsumer                      : Received kafka message 2019-03-10T03:53:59.195204Z with headers {kafka_offset=5, kafka_consumer=org.apache.kafka.clients.consumer.KafkaConsumer@14f412e4, kafka_timestampType=CREATE_TIME, kafka_receivedMessageKey=5, kafka_receivedPartitionId=0, kafka_receivedTopic=topic, kafka_receivedTimestamp=1552190039195}
2019-03-09 21:53:59.201  INFO 5098 --- [ntainer#0-0-C-1] c.b.k.RsocketClient                      : >> FF metadata=5 data=2019-03-10T03:53:59.195204Z
2019-03-09 21:53:59.201  INFO 5098 --- [ntainer#0-0-C-1] c.b.k.RsocketClient                      : << FF
2019-03-09 21:53:59.201  INFO 5098 --- [ntainer#0-0-C-1] c.b.k.RsocketClient                      : >> RR metadata=5 data=2019-03-10T03:53:59.195204Z
2019-03-09 21:53:59.202  INFO 5098 --- [ntainer#0-0-C-1] c.b.k.RsocketClient                      : >> RS metadata=5 data=2019-03-10T03:53:59.195204Z
2019-03-09 21:53:59.202  INFO 5098 --- [ntainer#0-0-C-1] c.b.k.RsocketClient                      : >> RC metadata=5 data=2019-03-10T03:53:59.195204Z
2019-03-09 21:53:59.203  INFO 5098 --- [ntainer#0-0-C-1] c.b.k.RsocketClient                      : >> MP metadata=5 data=2019-03-10T03:53:59.195204Z
2019-03-09 21:53:59.203  INFO 5098 --- [ntainer#0-0-C-1] c.b.k.RsocketClient                      : << MP metadata=5 data=2019-03-10T03:53:59.195204Z
2019-03-09 21:53:59.206  INFO 5098 --- [-client-epoll-8] c.b.k.RsocketClient                      : << RR metadata=5 data=Z402591.95:35:30T01-30-9102
2019-03-09 21:53:59.207  INFO 5098 --- [-client-epoll-8] c.b.k.RsocketClient                      : << RS metadata=5 data=Z402591.95:35:30T01-30-9102
2019-03-09 21:53:59.210  INFO 5098 --- [-client-epoll-8] c.b.k.RsocketClient                      : << RC metadata=5 data=Z402591.95:35:30T01-30-9102
```

RSocket Server
```

2019-03-09 21:53:59.203  INFO 5063 --- [-server-epoll-6] c.b.rsocketserver.RsocketServer          : << FF metadata=5 data=2019-03-10T03:53:59.195204Z
2019-03-09 21:53:59.204  INFO 5063 --- [-server-epoll-6] c.b.rsocketserver.RsocketServer          : >> FF
2019-03-09 21:53:59.205  INFO 5063 --- [-server-epoll-6] c.b.rsocketserver.RsocketServer          : << RR metadata=5 data=2019-03-10T03:53:59.195204Z
2019-03-09 21:53:59.205  INFO 5063 --- [-server-epoll-6] c.b.rsocketserver.RsocketServer          : >> RR
2019-03-09 21:53:59.206  INFO 5063 --- [-server-epoll-6] c.b.rsocketserver.RsocketServer          : << RS metadata=5 data=2019-03-10T03:53:59.195204Z
2019-03-09 21:53:59.206  INFO 5063 --- [-server-epoll-6] c.b.rsocketserver.RsocketServer          : >> RS metadata=5 data=Z402591.95:35:30T01-30-9102
2019-03-09 21:53:59.207  INFO 5063 --- [-server-epoll-6] c.b.rsocketserver.RsocketServer          : << RC metadata=5 data=2019-03-10T03:53:59.195204Z
2019-03-09 21:53:59.208  INFO 5063 --- [-server-epoll-6] c.b.rsocketserver.RsocketServer          : >> RC metadata=5 data=Z402591.95:35:30T01-30-9102
2019-03-09 21:53:59.209  INFO 5063 --- [-server-epoll-6] c.b.rsocketserver.RsocketServer          : << MP metadata=52019-03-10T03:53:59.195204Z data=
2019-03-09 21:53:59.209  INFO 5063 --- [-server-epoll-6] c.b.rsocketserver.RsocketServer          : >> MP
```
