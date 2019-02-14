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
public class MonthRpt {
    private String month;
    private String year;
    private String type;
    private String sum;
    
    public MonthRpt(){
        
    }
    
    public MonthRpt (String month, String year, String type, String sum){
        this.month = month;
        this.year = year;
        this.type = type;
        this.sum = sum;
    }
    
    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
    
    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
    
    public String getType() {
        return type;
    }

    public void setAptType(String type) {
        this.type = type;
    }
    
    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }
    
}
