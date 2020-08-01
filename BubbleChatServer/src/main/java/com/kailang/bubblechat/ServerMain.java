package com.kailang.bubblechat;

import com.kailang.bubblechat.server.NettyServer;

public class ServerMain {
    public static void main(String[] args) throws InterruptedException {
        new NettyServer(8888).run();
    }
}
