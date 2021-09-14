package com.alam.eathub.Model.My;

public class PushOrder {
    boolean err;
    String message;

    public boolean isErr() {
        return err;
    }

    public void setErr(boolean err) {
        this.err = err;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
