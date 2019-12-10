/**
 * 
 */
package com.gionee.client.business.shareTool;

import android.graphics.Bitmap;

/**
 * @author xiaojun
 * 
 */
public class ShareModel {
    private String title;
    private String text;
    private String url;
    private String imageUrl;
    private Bitmap bitmap;
    

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public ShareModel() {
    }

    public ShareModel(String title, String text, String url, String imageUrl) {
        super();
        this.title = title;
        this.text = text;
        this.url = url;
        this.imageUrl = imageUrl;
    }
    
    public ShareModel(String title, String text, String url, Bitmap bitmap) {
        super();
        this.title = title;
        this.text = text;
        this.url = url;
        this.bitmap = bitmap;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
