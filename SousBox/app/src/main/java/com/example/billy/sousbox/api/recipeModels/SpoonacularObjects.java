package com.example.billy.sousbox.api.recipeModels;

/**
 * Created by Billy on 4/29/16.
 */
public class SpoonacularObjects {
    private int id;
    private String title;
    private String readyInMinutes;
    private String image;
    private String[] imageUrls;


    public SpoonacularObjects(int id, String title, String readyInMinutes, String image, String[] imageUrls) {
        this.id = id;
        this.title = title;
        this.readyInMinutes = readyInMinutes;
        this.image = image;
        this.imageUrls = imageUrls;
    }




    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String[] getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(String[] imageUrls) {
        this.imageUrls = imageUrls;
    }
}
