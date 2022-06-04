package me.ikevoodoo.networkils.http.connection;

public enum StatusType {

    INFO,
    SUCCESS,
    REDIRECT,
    CLIENT_ERROR,
    SERVER_ERROR;

    public static StatusType getStatusType(int statusCode) {
        if (statusCode >= 100 && statusCode < 200) {
            return INFO;
        }

        if (statusCode >= 200 && statusCode < 300) {
            return SUCCESS;
        }

        if (statusCode >= 300 && statusCode < 400) {
            return REDIRECT;
        }

        if (statusCode >= 400 && statusCode < 500) {
            return CLIENT_ERROR;
        }

        return SERVER_ERROR;
    }

}
