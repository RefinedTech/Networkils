package dev.refinedtech.networkils.http.threading;

import dev.refinedtech.networkils.exceptions.UnexpectedSizeException;
import dev.refinedtech.networkils.http.messaging.HttpReadableMessage;
import dev.refinedtech.networkils.sockets.messaging.ReadableMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

public class HttpReadThread extends Thread {

    private final InputStream inputStream;

    private Runnable onClose;
    private Consumer<HttpReadableMessage> onMessage;

    public HttpReadThread(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public HttpReadThread onClose(Runnable onClose) {
        this.onClose = onClose;
        return this;
    }

    public HttpReadThread onMessage(Consumer<HttpReadableMessage> onMessage) {
        this.onMessage = onMessage;
        return this;
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (this.inputStream.available() <= 0) continue;
                byte[] data = this.inputStream.readNBytes(Integer.MAX_VALUE);

                if(this.onMessage != null) {
                    this.onMessage.accept(new HttpReadableMessage(ByteBuffer.wrap(data)));
                }
            } catch (SocketException ignored) {
                if(this.onClose != null) {
                    this.onClose.run();
                    interrupt();
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
