package org.kjt.kafka.consumer;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;


public class MessageDeserializer implements KafkaDeserializationSchema<String> {

	private static final Logger logger = LoggerFactory.getLogger(MessageDeserializer.class);
	private String singularityHeader;
	private ConsumerRecord<byte[],byte[]> consumerRecord;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public TypeInformation<String> getProducedType() {
		return TypeInformation.of(String.class);
	}

	@Override
	public String deserialize(ConsumerRecord<byte[],byte[]> consumerRecord) throws IOException {
		
		//processRecord(consumerRecord);
		this.consumerRecord = consumerRecord;
		
		this.extractSingularityHeader();
		
		//Headers myMessageHeaders = consumerRecord.headers();
		//String singularityHeader = new String(myMessageHeaders.lastHeader("singularityheader").value());
		System.out.println("this.getSingularityHeader() = " + this.getSingularityHeader());
		
		/*
		for (Header header : consumerRecord.headers()) {
			System.out.println("header key: " + header.key());
			System.out.println("header value: " + new String(header.value()));
		}
		*/

		return new String(consumerRecord.value());
	}

	@Override
	public boolean isEndOfStream(String nextElement) {
		return false;
	}
	
	public void processRecord(ConsumerRecord<byte[],byte[]> consumerRecord) {
		// Do nothing!
	}
	
	//public String getSingularityHeader(ConsumerRecord<byte[],byte[]> consumerRecord) {
	public void extractSingularityHeader() {
		
		String singularityHeader = "";
		for (Header header : this.consumerRecord.headers()) {
			if (header.key().equalsIgnoreCase("singularityheader")) {
				singularityHeader = new String(header.value());
			}
		}
		
		this.singularityHeader = singularityHeader;
		
	}
	
	public String getSingularityHeader() {
		return this.singularityHeader;
	}

}
