# kafka-spark-flink-example

Used [this](https://github.com/hands-on-tech/kafka-spark-flink-example) repo as a starter.

The goal was to be able to use AppDynamics to instrument a simple messaging pipeline where messages route through a Kafka Topic and are consumed by Flink.

## Prerequisites

- Java 1.8+
- Docker Compose (v3.6 Compose file compliant)

## App Setup

1. Rename the file `.env_public` to `.env` and update the AppD Access Key in that file.

2. Update the `environment` variables that start with `APPDYNAMICS_` in the `docker-compose.yml` file.

3. Build and run, e.g.

```bash
mvn clean package && docker build --no-cache -t kafka-spark-flink-example . && docker-compose up -d
```

## AppDynamics Setup

1. Create POJO rule to capture the beginning of the end-to-end transaction starting at the Producer

| Config | Implementation                                  |
| ------ | --------------                                  |
| Match Class | that Implements an Interface which         |
| Equals      | org.apache.kafka.clients.producer.Producer |
| Method Name |                                            |
| Equals | send                                            |


### Notes on how it works

The AppDynamics Java agent supports intercepting Kafka Producer clients OOB, and handles injecting AppDynamics Propagation Context (AKA SingularityHeader) into the Kafka message Headers (to be later extracted by a downstream Consumer).  Since this is more-or-less a console app, we had to create the entry point for the inception of the Producer behavior, which is done above in [AppDynamics Setup](#appdynamics-setup).

For the Flink Kafka Consumer to extract the AppDynamics Propagated Context, it requires some custom correlation.

First off, in order for Flink to be able to read the Kafka message Headers, it has to use version 1.8+ of the `flink-connector-kafka_2.12` package.  Otherwise, Flink abstracts away the Kafka message, and it's Headers are not accessible.  But, with version 1.8+, the Flink Connector gives direct access to the ConsumerRecord, which contains the message Headers.

In order to get access to the ConsumerRecord, the `KafkaDeserializationSchema` interface has to be implemented, which was done in the [MessageDeserializer](/src/main/java/org/kjt/kafka/consumer/MessageDeserializer.java) class.

Once that was in place, the [custom-activity-correlation.xml](custom-activity-correlation.xml) file specifies how to extract and utilize the AppDynamics Propagation Context to continue the transaction and trace it end-to-end.
