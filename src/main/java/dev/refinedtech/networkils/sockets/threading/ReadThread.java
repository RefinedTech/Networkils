package dev.refinedtech.networkils.sockets.threading;

import dev.refinedtech.networkils.exceptions.UnexpectedSizeException;
import dev.refinedtech.networkils.sockets.messaging.ReadableMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

public class ReadThread extends Thread {

    private final InputStream inputStream;

    private Runnable onClose;
    private Consumer<ReadableMessage> onMessage;

    public ReadThread(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public ReadThread onClose(Runnable onClose) {
        this.onClose = onClose;
        return this;
    }

    public ReadThread onMessage(Consumer<ReadableMessage> onMessage) {
        this.onMessage = onMessage;
        return this;
    }

    @Override
    public void run() {
        while (true) {
            try {
                byte[] size = new byte[4];
                if(this.inputStream.read(size) == -1) {
                    // Client has sent all the data and closed the connection
                    if(this.onClose != null) {
                        this.onClose.run();
                    }
                    interrupt();
                    break;
                }

                int len = ByteBuffer.wrap(size).getInt();
                if(len > 1_073_741_824) {
                    throw new UnexpectedSizeException(
                            "Message cannot be larger than 1GB, either split the message or an attacker is trying to send a large message",
                            len
                    );
                }

                if (len < 0) {
                    throw new UnexpectedSizeException(
                            "Message cannot be smaller than 0 bytes, either the wrong size was sent or an attacker is trying to send a wrong message",
                            len
                    );
                }

                byte[] data = new byte[len];
                if(this.inputStream.read(data) == -1) {
                    // Client has sent all the data and closed the connection
                    if(this.onClose != null) {
                        this.onClose.run();
                    }
                    interrupt();
                    break;
                }



                if(this.onMessage != null) {
                    this.onMessage.accept(new ReadableMessage(ByteBuffer.wrap(data)));
                }
            } catch (SocketException ignored) {
                if(this.onClose != null) {
                    this.onClose.run();
                    interrupt();
                    break;
                }
            } catch (IOException | UnexpectedSizeException e) {
                e.printStackTrace();
            }
        }
    }
}
