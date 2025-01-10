package projet.java.reconnaissancefaciale1.dao.entities;

import java.sql.Date;

public class User {
    private Integer id;
    private String name;
    private String email;
    private String password;
    private String imagePath;
    private boolean accessStatus;
    private Date createdAt;

    public User() {
    }
    public User(Integer id,Date createdAt, boolean accessStatus, String imagePath, String password, String email, String name) {
       this.id = id;
        this.createdAt = createdAt;
        this.accessStatus = accessStatus;
        this.imagePath = imagePath;
        this.password = password;
        this.email = email;
        this.name = name;
    }
    public User(Date createdAt, boolean accessStatus, String imagePath, String password, String email, String name) {
        this.createdAt = createdAt;
        this.accessStatus = accessStatus;
        this.imagePath = imagePath;
        this.password = password;
        this.email = email;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public boolean isAccessStatus() {
        return accessStatus;
    }

    public void setAccessStatus(boolean accessStatus) {
        this.accessStatus = accessStatus;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }


}
