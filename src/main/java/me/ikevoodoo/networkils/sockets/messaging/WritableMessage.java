package me.ikevoodoo.networkils.sockets.messaging;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class WritableMessage {

    private final int magicNumber;

    private ByteBuffer message;

    public WritableMessage(int magicNumber) {
        this(magicNumber, 512);
    }

    public WritableMessage(int magicNumber, int size) {
        this.magicNumber = magicNumber;
        message = ByteBuffer.allocate(size);
        writeInt(magicNumber);
    }

    public WritableMessage(int magicNumber, ByteBuffer message) {
        if(message.capacity() < 4) {
            throw new IllegalArgumentException("Message must be at least 4 bytes long");
        }
        this.magicNumber = magicNumber;
        this.message = message;
        writeInt(magicNumber);
    }

    public WritableMessage write(byte b) {
        expandIfNeeded(Byte.BYTES);
        message.put(b);
        return this;
    }

    public WritableMessage writeBytes(byte... b) {
        expandIfNeeded(Byte.BYTES * b.length);
        message.put(b);
        return this;
    }

    public WritableMessage writeShort(short s) {
        expandIfNeeded(Short.BYTES);
        message.putShort(s);
        return this;
    }

    public WritableMessage writeInt(int i) {
        expandIfNeeded(Integer.BYTES);
        message.putInt(i);
        return this;
    }

    public WritableMessage writeLong(long l) {
        expandIfNeeded(Long.BYTES);
        message.putLong(l);
        return this;
    }

    public WritableMessage writeFloat(float f) {
        expandIfNeeded(Float.BYTES);
        message.putFloat(f);
        return this;
    }

    public WritableMessage writeDouble(double d) {
        expandIfNeeded(Double.BYTES);
        message.putDouble(d);
        return this;
    }

    public WritableMessage writeBoolean(boolean b) {
        expandIfNeeded(Byte.BYTES);
        message.put((byte) (b ? 1 : 0));
        return this;
    }

    public WritableMessage writeString(String s) {
        writeString(StandardCharsets.UTF_8, s);
        return this;
    }

    public WritableMessage writeString(Charset charset, String s) {
        byte[] bytes = s.getBytes(charset);
        expandIfNeeded(Integer.BYTES + bytes.length * Byte.BYTES);
        writeInt(bytes.length);
        writeBytes(bytes);
        return this;
    }

    public WritableMessage writeStrings(String... strings) {
        for(String s : strings) {
            writeString(s);
        }
        return this;
    }

    public WritableMessage writeStrings(Charset charset, String... strings){
        for(String s : strings) {
            writeString(charset, s);
        }
        return this;
    }

    public WritableMessage writeTo(OutputStream stream) throws IOException {
        message.flip();
        byte[] data = message.array();
        stream.write(ByteBuffer.allocate(4).putInt(data.length).array());
        stream.write(data);
        message.flip();
        return this;
    }

    public WritableMessage copy() {
        ByteBuffer copy = message.isDirect() ? ByteBuffer.allocateDirect(message.capacity()) : ByteBuffer.allocate(message.capacity());
        int pos = message.position();
        message.rewind();
        copy.put(message);
        message.rewind();
        copy.flip();
        message.position(pos);
        return new WritableMessage(this.magicNumber, copy);
    }

    public WritableMessage clear() {
        message.clear();
        writeInt(magicNumber);
        return this;
    }

    public ReadableMessage toReadable() {
        message.flip();
        return new ReadableMessage(message);
    }

    private void expandIfNeeded(int bytes) {
        if (message.remaining() < bytes) {
            ByteBuffer newMessage = ByteBuffer.allocate(message.capacity() * 2);
            newMessage.put(message);
            message = newMessage;
        }
    }

}
