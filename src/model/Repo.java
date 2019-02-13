/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author mian
 */
public class Repo {
    
    private Integer repoUserId;
    private String repoUserName;
    private boolean repoIsEdit;
    private Appt repoSelectEditApt;
    
    
    public Repo () {}
    
    public Repo (Integer repoUserId, String repoUserName){
        this.repoUserId = repoUserId;
        this.repoUserName = repoUserName;
    }
    
    public Repo (boolean repoIsEdit){
        this.repoIsEdit = repoIsEdit;
    }
    
    public Repo (Appt repoSelectEditApt){
        this.repoSelectEditApt = repoSelectEditApt;
    }
    
    public Integer getrepoUserId() {
        return repoUserId;
    }

    public void setrepoUserId(Integer repoUserId) {
        this.repoUserId = repoUserId;
    }
    
    public String getrepoUserName() {
        return repoUserName;
    }

    public void setrepoUserName(String repoUserName) {
        this.repoUserName = repoUserName;
    }
    
    public boolean getrepoIsEdit() {
        return repoIsEdit;
    }

    public void setrepoIsEdit(boolean repoIsEdit) {
        this.repoIsEdit = repoIsEdit;
    }
    
    public Appt getRepoSelectEditApt(){
        return repoSelectEditApt;
    }
    
    public void setrepoSelectEditApt(Appt repoSelectEditApt) {
        this.repoSelectEditApt = repoSelectEditApt;
    }
    
    
}
