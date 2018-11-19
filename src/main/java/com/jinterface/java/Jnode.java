package com.jinterface.java;

import com.ericsson.otp.erlang.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Jnode {
    public static void main(String[] args) throws InterruptedException {
        String java_node = "";
        String cookie = "";
        String remote_node = "";
        Properties pro = new Properties();
        FileInputStream in;
        try {
            in = new FileInputStream(args[0]);
            pro.load(in);
            java_node = pro.getProperty("java_node");
            cookie = pro.getProperty("cookie");
            remote_node = pro.getProperty("remote_node");
            System.out.println("java_node:" + java_node);
            System.out.println("cookie:" + cookie);
            System.out.println("remote_node:" + remote_node);
            in.close();
        } catch (IOException e) {
            System.out.println("java config read error:" + e);
        };

        OtpNode node = null;
        try {
            node = new OtpNode(java_node, cookie);
        } catch (IOException e) {
            System.out.println("otp node init error:" + e);
        };
        OtpMbox mbox = node.createMbox("java_node");
        OtpErlangObject o;
        OtpErlangTuple msg;
        OtpErlangPid from;
        OtpErlangObject[] s;
        OtpErlangTuple send;

        PingNode remote = new PingNode(node, remote_node);
        remote.asynTask();

        while (true) try {
            o = mbox.receive();
            if (o instanceof OtpErlangTuple) {
                msg = (OtpErlangTuple) o;
                from = (OtpErlangPid) msg.elementAt(0);
                String operate = ((OtpErlangString) msg.elementAt(1)).stringValue();
                String data;
                switch (operate) {
                    case "msg_type":
                        data = ((OtpErlangString) msg.elementAt(2)).stringValue();
                        System.out.println("receive msg:" + data);
                        s = new OtpErlangObject[1];
                        s[0] = new OtpErlangString(data);
                        send = new OtpErlangTuple(s);
                        mbox.send(from, send);
                        break;
                    default:
                        System.out.println("receive exception msg_type:" + operate);
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("java receive message:" + e);
        }

    }
}
