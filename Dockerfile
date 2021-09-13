FROM openjdk:8u151-jdk-alpine3.7

# Install Bash
RUN apk add --no-cache bash libc6-compat

# Install AppD Agent Fetch Dependencies, download script, and custom configs
RUN apk --no-cache add curl unzip jq
ADD downloadJavaAgentLatest.sh .
ADD custom-activity-correlation.xml .
ADD log4j.xml .
ADD log4j2.xml .
RUN ./downloadJavaAgentLatest.sh

# Copy resources
WORKDIR /
COPY wait-for-it.sh wait-for-it.sh
COPY target/kafka-spark-flink-example-1.0-SNAPSHOT-jar-with-dependencies.jar kafka-spark-flink-example.jar

# Wait for Zookeeper and Kafka to be available and run application
#CMD ./wait-for-it.sh -s -t 30 $EXAMPLE_ZOOKEEPER_SERVER -- ./wait-for-it.sh -s -t 30 $EXAMPLE_KAFKA_SERVER -- java -Xmx512m -jar kafka-spark-flink-example.jar
CMD ./wait-for-it.sh -s -t 30 $EXAMPLE_ZOOKEEPER_SERVER -- ./wait-for-it.sh -s -t 30 $EXAMPLE_KAFKA_SERVER -- java -Xmx512m -javaagent:/opt/appdynamics/java/javaagent.jar -Dappdynamics.agent.log4j2.disabled=true -jar kafka-spark-flink-example.jar

