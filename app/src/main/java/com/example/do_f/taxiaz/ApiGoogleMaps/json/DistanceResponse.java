package com.example.do_f.taxiaz.ApiGoogleMaps.json;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by do_f on 29/01/16.
 */
public class DistanceResponse {

    @SerializedName("destination_addresses")
    private List<String>    dest;

    @SerializedName("origin_addresses")
    private List<String>    origin;

    private List<Rows>      rows;

    private String          status;

    @SerializedName("error_message")
    private String          error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<String> getDest() {
        return dest;
    }

    public void setDest(List<String> dest) {
        this.dest = dest;
    }

    public List<String> getOrigin() {
        return origin;
    }

    public void setOrigin(List<String> origin) {
        this.origin = origin;
    }

    public List<Rows> getRows() {
        return rows;
    }

    public void setRows(List<Rows> rows) {
        this.rows = rows;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public class    Rows
    {
        private List<Elements>  elements;

        public Rows(List<Elements> elements) {
            this.elements = elements;
        }

        public List<Elements> getElements() {
            return elements;
        }

        public void setElements(List<Elements> elements) {
            this.elements = elements;
        }
    }

    public class    Elements
    {
        private Info    distance;
        private Info    duration;
        private String  status;

        public Elements(Info distance, Info duration, String status) {
            this.distance = distance;
            this.duration = duration;
            this.status = status;
        }

        public Info getDistance() {
            return distance;
        }

        public void setDistance(Info distance) {
            this.distance = distance;
        }

        public Info getDuration() {
            return duration;
        }

        public void setDuration(Info duration) {
            this.duration = duration;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public class    Info
    {
        private String  text;
        private int  value;

        public Info(String text, int value) {
            this.text = text;
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }



}
