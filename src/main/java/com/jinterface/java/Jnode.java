package com.jinterface.java;

import com.ericsson.otp.erlang.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Jnode {
    public static void main(String[] args) throws InterruptedException {
        String java_node = "";
        String cookie = "";
        Properties pro = new Properties();
        FileInputStream in;
        try {
            in = new FileInputStream(args[0]);
            pro.load(in);
            java_node = pro.getProperty("java_node");
            cookie = pro.getProperty("cookie");
            System.out.println("java_node:" + java_node);
            System.out.println("cookie:" + cookie);
            in.close();
        } catch (IOException e) {
            System.out.println("java config read error:" + e);
        };

        OtpNode node = null;
        try {
            node = new OtpNode(java_node, cookie);
        } catch (IOException e) {
            System.out.println("erlang node init error:" + e);
        };
        OtpMbox mbox = node.createMbox("java_node");
        OtpErlangObject o;
        OtpErlangTuple msg;
        OtpErlangPid from;
        OtpErlangObject[] s;
        OtpErlangTuple smsg;
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
                        System.out.println("recv msg:" + data);
                        s = new OtpErlangObject[2];
                        s[0] = mbox.self();
                        s[1] = new OtpErlangString(data);
                        smsg = new OtpErlangTuple(s);
                        mbox.send(from, smsg);
                        s = null;
                        smsg = null;
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("java receive message:" + e);
        }

    }
}
