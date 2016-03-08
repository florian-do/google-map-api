package com.example.do_f.taxiaz.api.json;

import java.util.List;

/**
 * Created by do_f on 28/01/16.
 */
public class TaxiResponse
{

    private boolean     error;
    private List<Taxi>  infos;

    public TaxiResponse(boolean error, List<Taxi> infos) {
        this.error = error;
        this.infos = infos;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public List<Taxi> getInfos() {
        return infos;
    }

    public void setInfos(List<Taxi> infos) {
        this.infos = infos;
    }

    public class Taxi
    {
        private String  type;
        private String  value;

        public Taxi(String type, String value) {
            this.type = type;
            this.value = value;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
