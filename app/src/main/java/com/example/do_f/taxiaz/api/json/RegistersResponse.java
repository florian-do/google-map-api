package com.example.do_f.taxiaz.api.json;

/**
 * Created by do_f on 28/01/16.
 */
public class RegistersResponse
{
    private boolean error;

    public RegistersResponse(boolean error) {
        this.error = error;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
