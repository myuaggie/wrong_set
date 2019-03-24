package com;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;


import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;

import java.io.File;
import java.io.FileWriter;

public class BuyLogConsumer {
    Consumer<Integer,Integer> consumer;

    BuyLogConsumer(){
        init();
    }

    private void init() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("group.id", "test");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.IntegerDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.IntegerDeserializer");
        consumer = new KafkaConsumer<Integer,Integer>(props);
        consumer.subscribe(Arrays.asList("buyrecord"));
    }

    public void poll() throws Exception{
        ConsumerRecords<Integer, Integer> records = consumer.poll(100);
        for (ConsumerRecord<Integer, Integer> record : records) {
            System.out.printf("offset = %d, key = %s, value = %s%n", record.offset(), record.key(), record.value());
            writeFile(record.key(), record.value());
        }
    }

    private void writeFile(int key, int value) throws Exception{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        File file =new File("buylog-"+sdf.format( new Date())+".txt");

        //if file doesnt exists, then create it
        if(!file.exists()){
            file.createNewFile();
        }

        FileWriter fileWritter = new FileWriter(file.getName(),true);
        fileWritter.write(String.format("%s: user id: %d, external library id: %d\n",sdf2.format(new Date()),key,value));
        fileWritter.close();
    }

    public void shutdown(){
        if (consumer != null) {
            consumer.close();
        }
    }

}
