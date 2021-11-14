package com.example.bookapptest2.models;

public class ModelCategory {

    private String id;
    private String category;
    private String uid;
    long timestamp;

    public  ModelCategory() {

    }

    public ModelCategory(String id, String category, String uid, long timestamp) {
        this.id = id;
        this.category = category;
        this.uid = uid;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getUid() {
        return uid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
