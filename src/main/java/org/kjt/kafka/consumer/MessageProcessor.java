package org.kjt.kafka.consumer;

import org.apache.flink.api.common.functions.MapFunction;

public class MessageProcessor implements MapFunction<String, String> {
	private static final long serialVersionUID = -6867736771747690202L;
    @Override
    public String map(String s) {
        return s.toUpperCase();
    }

}
