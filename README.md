# rsocket-java11-sandbox
```
./gradlew clean build
java -jar kafka_producer/build/libs/kafka_producer-0.0.1-SNAPSHOT.jar --spring.config.location=kafka_producer/application.yml
java -jar rsocket_server/build/libs/rsocket_server-0.0.1-SNAPSHOT.jar --spring.config.location=rsocket_server/application.yml 
java -jar kafka_consumer_rsocket_client/build/libs/kafka_consumer_rsocket_client-0.0.1-SNAPSHOT.jar --spring.config.location=kafka_consumer_rsocket_client/application.yml 
```
