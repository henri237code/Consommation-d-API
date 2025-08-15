package model;

public class Actor {
    private int id;
    private String name;
    private String bio;
    private String picture;

    public Actor() {}

    public Actor(int id, String name, String bio, String picture) {
        this.id = id; this.name = name; this.bio = bio; this.picture = picture;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getBio() { return bio; }
    public String getPicture() { return picture; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setBio(String bio) { this.bio = bio; }
    public void setPicture(String picture) { this.picture = picture; }
}
