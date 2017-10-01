package com.ttdn.memonote.data;

import java.io.Serializable;

/**
 * Created by ttdn1 on 9/29/2017.
 */

public class Note  implements Serializable{
    private String id;
    private String title;
    private String content;
    private String date;
    private String color;
    public String picture;
    public String alarm;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getAlarm() {
        return alarm;
    }

    public void setAlarm(String alarm) {
        this.alarm = alarm;
    }

    public Note(String id, String title, String content, String date, String color) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.color = color;
    }
    public Note(String id, String title, String content,
                String date, String color,String alarm, String picture) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.color = color;
        this.alarm=alarm;
        this.picture=picture;
    }
    public Note(String title, String content,String date, String color, String alarm,String picture) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.color = color;
        this.picture=picture;
        this.alarm=alarm;
    }

    public Note() {
    }
}
