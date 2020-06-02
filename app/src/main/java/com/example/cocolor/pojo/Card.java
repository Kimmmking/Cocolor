package com.example.cocolor.pojo;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Card implements Serializable,Comparable{

    private int id;
    private int category;
    private String picture;
    private String title;
    private String description;

    private int vibrant;
    private int darkVibrant;
    private int lightVibrant;
    private int muted;
    private int darkMuted;
    private int lightMuted;
    private int textColor;

    private int collection;

    public Card() {

    }

    public Card(int id, int category, String picture, String title, String description, int vibrant, int darkVibrant, int lightVibrant, int muted, int darkMuted, int lightMuted, int textColor, int collection) {
        this.id = id;
        this.category = category;
        this.picture = picture;
        this.title = title;
        this.description = description;
        this.vibrant = vibrant;
        this.darkVibrant = darkVibrant;
        this.lightVibrant = lightVibrant;
        this.muted = muted;
        this.darkMuted = darkMuted;
        this.lightMuted = lightMuted;
        this.textColor = textColor;
        this.collection = collection;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getVibrant() {
        return vibrant;
    }

    public void setVibrant(int vibrant) {
        this.vibrant = vibrant;
    }

    public int getDarkVibrant() {
        return darkVibrant;
    }

    public void setDarkVibrant(int darkVibrant) {
        this.darkVibrant = darkVibrant;
    }

    public int getLightVibrant() {
        return lightVibrant;
    }

    public void setLightVibrant(int lightVibrant) {
        this.lightVibrant = lightVibrant;
    }

    public int getMuted() {
        return muted;
    }

    public void setMuted(int muted) {
        this.muted = muted;
    }

    public int getDarkMuted() {
        return darkMuted;
    }

    public void setDarkMuted(int darkMuted) {
        this.darkMuted = darkMuted;
    }

    public int getLightMuted() {
        return lightMuted;
    }

    public void setLightMuted(int lightMuted) {
        this.lightMuted = lightMuted;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getCollection() {
        return collection;
    }

    public void setCollection(int collection) {
        this.collection = collection;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }

    @NonNull
    @Override
    public String toString() {
        return "{" +
                "id:" + id +
                ", title:'" + title + '\'' +
                ", description:'" + description + '\'' +
                ", picture:" + picture + '\'' +
                ", category:" + category +
                ", vibrant:" + vibrant +
                ", darkVibrant:" + darkVibrant +
                ", lightVibrant:" + lightVibrant +
                ", muted:" + muted +
                ", darkMuted:" + darkMuted +
                ", lightMuted:" + lightMuted +
                ", textColor:" + textColor +
                ", collection:" + collection +
                '}';
    }
}
