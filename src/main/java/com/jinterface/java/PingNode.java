package com.jinterface.java;

import com.ericsson.otp.erlang.OtpNode;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PingNode {
    private ExecutorService executor = Executors.newFixedThreadPool(1);
    private OtpNode node;
    private String remote_node;
    private int count = 0;

    public PingNode(OtpNode node, String remote_node) {
        this.node = node;
        this.remote_node = remote_node;
    }

    public void asynTask() throws InterruptedException {

        executor.submit(new Runnable() {

            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                        if (node.ping(remote_node,2000)) {
                            count = 0;
                        }
                        else {
                            System.out.println("remote is not up");
                            count = count + 1;
                            if (count > 5){
                                System.out.println("remote is down");
                                System.exit(0);
                            }
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        System.out.println("remote ping error");
                    }
                }
            }
        });

    }
}
