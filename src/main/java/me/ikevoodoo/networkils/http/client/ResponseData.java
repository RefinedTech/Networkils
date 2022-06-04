package me.ikevoodoo.networkils.http.client;

import me.ikevoodoo.networkils.http.connection.StatusType;
import me.ikevoodoo.networkils.util.InputStreamUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;

public class ResponseData {

    private final int statusCode;
    private final StatusType statusType;

    private byte[] body;

    public ResponseData(int statusCode, byte[] body) {
        this.statusCode = statusCode;
        this.statusType = StatusType.getStatusType(statusCode);
        this.body = body;
    }

    public ResponseData(HttpsURLConnection https) throws IOException {
        this(https.getResponseCode(), new byte[0]);
        if(this.getStatusType() != StatusType.CLIENT_ERROR && this.getStatusType() != StatusType.SERVER_ERROR) {
            this.body = InputStreamUtils.readAllBytes(https.getInputStream());
        }
    }

    public ResponseData(HttpURLConnection http) throws IOException {
        this(http.getResponseCode(), new byte[0]);
        if(this.getStatusType() != StatusType.CLIENT_ERROR && this.getStatusType() != StatusType.SERVER_ERROR) {
            this.body = InputStreamUtils.readAllBytes(http.getInputStream());
        }
    }

    public int getStatusCode() {
        return statusCode;
    }

    public StatusType getStatusType() {
        return statusType;
    }

    public void printResponse(PrintStream out) {
        out.println("Status: " + statusCode + " (" + statusType + ")");
        out.println("Body: \n" + new String(body));
    }

    public byte[] getBody() {
        return body;
    }

    public void writeTo(File file) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        writeTo(out);
        out.close();
    }

    public void writeTo(OutputStream out) throws IOException {
        out.write(body);
    }

    public void writeToSilent(File file) {
        try {
            FileOutputStream out = new FileOutputStream(file);
            writeToSilent(out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToSilent(OutputStream out) {
        try {
            writeTo(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
