package com;

public class TestProducer {
    public static void main(String[] args) throws CloneNotSupportedException {
        BuyLogProducer blp=new BuyLogProducer();
        for (int i=0;i<10;i++){
            blp.produce(i,i*10);
        }
    }
}
