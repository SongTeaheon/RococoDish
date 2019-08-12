package com.rococodish.front_ui.Search;

public class RecordData {
    int id;
    String record;

    RecordData(int id, String data){
        this.id = id;
        this.record = data;
    }

    public int getId() {
        return id;
    }

    public String getRecord() {
        return record;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setRecord(String record) {
        this.record = record;
    }
}
