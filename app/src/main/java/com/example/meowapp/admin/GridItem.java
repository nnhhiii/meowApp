package com.example.meowapp.admin;

public class GridItem {
    private int iconResId;
    private String text;

    public GridItem(int iconResId, String text) {
        this.iconResId = iconResId;
        this.text = text;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getText() {
        return text;
    }
}
