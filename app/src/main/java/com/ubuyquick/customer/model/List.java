package com.ubuyquick.customer.model;

import com.google.firebase.Timestamp;

public class List {

    private String list_name;
    private int list_count;
    private String list_id;
    private String list_created_at;

    public List(String list_name, int list_count, String list_id, String list_created_at) {
        this.list_name = list_name;
        this.list_count = list_count;
        this.list_id = list_id;
        this.list_created_at = list_created_at;
    }

    public String getListName() {
        return list_name;
    }

    public void setListName(String list_name) {
        this.list_name = list_name;
    }

    public int getListCount() {
        return list_count;
    }

    public void setListCount(int list_count) {
        this.list_count = list_count;
    }

    public String getListId() {
        return list_id;
    }

    public void setListId(String list_id) {
        this.list_id = list_id;
    }

    public String getListCreatedAt() {
        return list_created_at;
    }

    public void setListCreatedAt(String list_created_at) {
        this.list_created_at = list_created_at;
    }
}
