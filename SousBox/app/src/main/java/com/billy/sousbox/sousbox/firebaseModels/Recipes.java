package com.billy.sousbox.sousbox.firebaseModels;

/**
 * Created by Billy on 5/10/16.
 */
public class Recipes {

    private int id;
    private String image;
    private String title;
    private String readyInMinutes;
    private String[] imageUrls;

    public Recipes() {
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReadyInMinutes() {
        return readyInMinutes;
    }

    public void setReadyInMinutes(String readyInMinutes) {
        this.readyInMinutes = readyInMinutes;
    }

    public String[] getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(String[] imageUrls) {
        this.imageUrls = imageUrls;
    }

    @Override
    public String toString() {

    return "recipes {id='" + id + "', image='" + image + "', title='" + title + "',readyInMinutes='"+ readyInMinutes+"',imageUrls='" +imageUrls+ "'}";
    }

}
