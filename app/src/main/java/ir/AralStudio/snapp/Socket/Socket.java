package ir.AralStudio.snapp.Socket;

import java.io.IOException;

import io.nats.client.Connection;
import io.nats.client.MessageHandler;
import io.nats.client.Nats;
import io.nats.client.Options;

public class Socket {
    protected Options options;
    protected Connection nats;

    public Socket(String host, int port) throws IOException, InterruptedException {
        this.options = new Options.Builder().server(host + ":" + port).build();
        this.nats = Nats.connect(this.options);
    }

    public Socket(Options options) throws IOException, InterruptedException {
        this.options = options;
        this.nats = Nats.connect(this.options);
    }

    public void Subscribe(String channel, MessageHandler handler) {
        nats.createDispatcher(handler).subscribe(channel);
    }

}
