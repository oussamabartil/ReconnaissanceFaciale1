package bartil.oussama.reconnaissancefaciale1.dao.entities;

import java.sql.Date;

public class AccessLogs {
    private int log_id;
    private Integer user_id;
    private Date accessDate;
    private Boolean accessStatus;
    private String errorMessage;
    private String imagePaths;


    public AccessLogs() {
    }

    public AccessLogs(Integer user_id, Date accessDate, Boolean accessStatus, String errorMessage, String imagePaths) {
        this.user_id = user_id;
        this.accessDate = accessDate;
        this.accessStatus = accessStatus;
        this.errorMessage = errorMessage;
        this.imagePaths = imagePaths;
    }

    // Getters et Setters
    public int getLog_id() {
        return log_id;
    }

    public void setLog_id(int log_id) {
        this.log_id = log_id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Date getAccessDate() {
        return accessDate;
    }

    public void setAccessDate(Date accessDate) {
        this.accessDate = accessDate;
    }

    public Boolean getAccessStatus() {
        return accessStatus;
    }

    public void setAccessStatus(Boolean accessStatus) {
        this.accessStatus = accessStatus;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(String imagePaths) {
        this.imagePaths = imagePaths;
    }
}
