package com;

import org.apache.kafka.clients.producer.*;

import java.util.Properties;

public class BuyLogProducer {

    private static Producer<Integer, Integer> producer ;

    BuyLogProducer(){
        init();

    }

    private void init() {
        if (producer == null) {
            Properties props = new Properties() ;
            //此处配置的是kafka的端口
            props.put("bootstrap.servers", "localhost:9092");
            props.put("acks", "all");
            props.put("retries", 0);
            props.put("batch.size", 16384);
            props.put("linger.ms", 1);
            props.put("buffer.memory", 33554432);
            props.put("key.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");
            props.put("value.serializer", "org.apache.kafka.common.serialization.IntegerSerializer");

            producer = new KafkaProducer<Integer, Integer>(props);

        }
    }

    private void shutdown(){
        if (producer != null) {
            producer.close();
            producer=null;
        }
    }

    private void send(int user, int library){
        producer.send(new ProducerRecord<Integer, Integer>("buyrecord", user, library), new Callback() {
            public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                if (null != e) {
                    e.printStackTrace();
                }else {
                    System.out.println("callback: " + recordMetadata.topic() + " " + recordMetadata.offset());
                }
            }
        });
    }

    public void produce(int user, int library){
        init();
        send(user,library);
        shutdown();
    }


}
