package com;

public class TestConsumer {
    public static void main(String[] args) throws CloneNotSupportedException {
        BuyLogConsumer blc=new BuyLogConsumer();
        try {
            while (true) {
                blc.poll();
            }
        }
        catch (Exception e){
            System.out.println("file not found");
        }
        finally {
            blc.shutdown();
        }
    }
}
