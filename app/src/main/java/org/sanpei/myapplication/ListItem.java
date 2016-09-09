package org.sanpei.myapplication;

/**
 * Created by sanpei on 2016/09/09.
 */
public class ListItem {
    private int imageId;
    private String text;
    private int backgroundColor;

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setBackgroundColor(int color) { this.backgroundColor = color;}
    public int getBackgroundColor() { return backgroundColor;}
}
