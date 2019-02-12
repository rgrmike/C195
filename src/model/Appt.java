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
public class Appt {
    private Integer appointmentID;
    private String appointmentTitle;
    private String start;
    private String end;
    private String contact;
    private Customer customer;
    private String description;
    private String location;
    
    public Appt(){
    }
     


public Appt (Integer appointmentID, String appointmentTitle, String start, String end, String contact, Customer customer, String description, String location){
    this.appointmentID = appointmentID;
    this.appointmentTitle = appointmentTitle;
    this.start = start;
    this.end = end;
    this.contact = contact;
    this.customer = customer;
    this.description = description;
    this.location = location;
    
}

public Integer getAppointmentID(){
    return appointmentID;
}

public void setAppointmentID(Integer appointmentID){
    this.appointmentID = appointmentID;
}

public String getAppointmentTitle(){
    return appointmentTitle;
}

public void setAppointmentTitle (String appointmentTitle){
    this.appointmentTitle = appointmentTitle;
}

public String getStart(){
    return start;
}

public void setStart (String start){
    this.start = start;
}

public String getEnd(){
    return end;
}

public void setEnd (String end){
    this.end = end;
}

public String getContact (){
    return contact;
}

public void setContact (String contact){
    this.contact = contact;
}

public Customer getCustomer (){
    return customer;
}

public void setContact (Customer customer){
    this.customer = customer;
}

public String getDescription (){
    return description;
}

public void setDescription (String description){
    this.description = description;
}

public String getLocation (){
    return location;
}

public void setLocation (String location){
    this.location = location;
}
        


}
