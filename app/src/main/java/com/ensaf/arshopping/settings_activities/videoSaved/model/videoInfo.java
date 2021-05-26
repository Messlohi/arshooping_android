package com.ensaf.arshopping.settings_activities.videoSaved.model;

import java.io.File;

public class videoInfo {

    String title ="titre";
    String path =null;
    boolean dowloaded = false;
    String url = "";
    String key  ="";
    String nameStorage ="";
    public videoInfo() {
    }



    public videoInfo(String title, String path) {
        this.title = title;
        this.path = path;
    }

    public String getNameStorage() {
        return nameStorage;
    }


    public void setNameStorage(String nameStorage) {
        this.nameStorage = nameStorage;
    }

    public boolean isDowloaded() {
        return dowloaded;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setDowloaded(boolean dowloaded) {
        this.dowloaded = dowloaded;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
