package com.scalahome.net;


import com.scalahome.common.closablepool.CloseablePool;
import lombok.Setter;

import java.net.InetSocketAddress;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author fuqing.xu
 * @date 2020-07-11 17:51
 */
public class ClientPool extends CloseablePool {

    public ClientPool(long duration, TimeUnit unit) {
        super(duration, unit);
    }

    @Setter
    private int connectTimeout = 3000;

    public Client getClient(InetSocketAddress remoteAddress) throws ExecutionException {
        return super.getResource(Client.class.getName() + "_" + remoteAddress.toString(), new Callable<Client>() {
            @Override
            public Client call() throws Exception {
                Client client = new ClientImpl(connectTimeout);
                client.connect(remoteAddress);
                return client;
            }
        });
    }

    public void closeClient(InetSocketAddress remoteAddress) {
        super.closeResource(Client.class.getName() + "_" + remoteAddress.toString());
    }
}
