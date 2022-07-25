package dev.refinedtech.networkils.http.messaging;

import dev.refinedtech.networkils.http.HttpMethod;
import dev.refinedtech.networkils.sockets.messaging.ReadableMessage;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;

public class HttpReadableMessage {

    private final ByteBuffer message;

    private final HttpMethod method;

    public HttpReadableMessage(ByteBuffer message) {
        this.message = message;
        byte[] data = message.array();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(data, 0, data.length)));
        try {
            System.out.println(reader.readLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        method = HttpMethod.GET;
    }

    public HttpMethod method() {
        return method;
    }

    public boolean hasData() {
        return message.hasRemaining();
    }

    public ReadableMessage copy() {
        return new ReadableMessage(ByteBuffer.wrap(message.array()));
    }

}
