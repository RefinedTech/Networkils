package me.ikevoodoo.networkils.sockets.threading;

import me.ikevoodoo.networkils.exceptions.SizeTooLargeException;
import me.ikevoodoo.networkils.sockets.messaging.ReadableMessage;

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
                    throw new SizeTooLargeException(
                            "Message cannot be larger than 1GB, either split the message or an attacker is trying to send a large message",
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
            } catch (IOException | SizeTooLargeException e) {
                e.printStackTrace();
            }
        }
    }
}
