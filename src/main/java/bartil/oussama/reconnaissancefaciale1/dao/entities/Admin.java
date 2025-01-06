package bartil.oussama.reconnaissancefaciale1.dao.entities;

import java.sql.Date;

public class Admin {
    private int admin_id;
    private String userName;
    private String password;
    private String imagePath;
    private Date date;

    public Admin() {
    }

    public Admin(String userName, String password, String imagePath, Date date) {
        this.userName = userName;
        this.password = password;
        this.imagePath = imagePath;
        this.date = date;
    }

    public int getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(int admin_id) {
        this.admin_id = admin_id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
