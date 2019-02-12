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
public class Customer {
    
    private Integer customerId;
    private String customerName;
    private String address;
    private String address2;
    private String city;
    private String country;
    private String postalCode;
    private String phone;
    
    public Customer (){
    }
    
    public Customer (Integer customerID, String customerName, String address, String address2, String city, String country, String postalCode, String phone){
        this.customerId = customerID;
        this.customerName = customerName;
        this.address = address;
        this.address2 = address2;
        this.city = city;
        this.country = country;
        this.postalCode = postalCode;
        this.phone = phone;    
    }
    public Customer (Integer customerId, String customerName){
        this.customerId = customerId;
        this.customerName = customerName;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public String getAddress() {
        return address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getAddress2() {
        return address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
    
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
    //This Overrid returns the customer name by default instead of the object name
    
    
    
    @Override
    public String toString() {
        return customerName;
    }

}
