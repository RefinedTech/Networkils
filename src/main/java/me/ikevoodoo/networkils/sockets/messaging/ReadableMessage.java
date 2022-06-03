package me.ikevoodoo.networkils.sockets.messaging;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class ReadableMessage {

    private final ByteBuffer message;
    private final int magicNumber;

    public ReadableMessage(ByteBuffer message) {
        this.message = message;
        if(message.remaining() < Integer.BYTES) {
            throw new IllegalArgumentException("Message is too short to contain a magic number");
        }
        magicNumber = message.getInt();
    }

    public byte next() {
        return message.get();
    }

    public byte[] nextBytes(int size) {
        return nextBytes(new byte[size]);
    }

    public byte[] nextBytes(byte[] bytes) {
        message.get(bytes);
        return bytes;
    }

    public byte[] getBytes() {
        return message.array();
    }

    public short nextShort() {
        return message.getShort();
    }

    public int nextInt() {
        return message.getInt();
    }

    public long nextLong() {
        return message.getLong();
    }

    public float nextFloat() {
        return message.getFloat();
    }

    public double nextDouble() {
        return message.getDouble();
    }

    public boolean nextBoolean() {
        return message.get() >= 1;
    }

    public String nextString() {
        return nextString(StandardCharsets.UTF_8);
    }

    public String nextString(Charset charset) {
        int length = nextInt();
        byte[] bytes = new byte[length];
        message.get(bytes);
        return new String(bytes, charset);
    }

    public boolean hasData() {
        return message.hasRemaining();
    }

    public String toString() {
        return "ReadableMessage(magicNumber=" + magicNumber + ", message=" + new String(getBytes(), StandardCharsets.UTF_8) + ")";
    }

    public int getMagicNumber() {
        return magicNumber;
    }

    public ReadableMessage copy() {
        return new ReadableMessage(ByteBuffer.wrap(getBytes()));
    }
}
