package com.example.angeloaustria.buddylending;

/**
 * Abstract class defining a method to set error text.
 *
 * @author nickstulov.
 */
public abstract class ResultCallback implements Runnable {
    private String err = "";
    private boolean success = false;
    private Object data;

    public void setErr(String str) {
        err = str;
    }

    public String getErr() {
        return err;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getData() {
        return data;
    }
}
