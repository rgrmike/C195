/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Appt;
import model.Customer;
import model.DBConnection;
import model.Query;
import model.Repo;

/**
 * FXML Controller class
 *
 * @author mian
 */
public class UserLoginController implements Initializable {

    Repo repo = new Repo();
    private final Locale myLocale = Locale.getDefault();
    @FXML
    private TextField UserLoginUserNameField;
    @FXML
    private TextField UserLoginPasswordField;
    @FXML
    private Button UserLoginSigninButton;
    @FXML
    private Label UserLoginErrorLabel;
    @FXML
    private Button UserLoginExitButton;
    ObservableList<Appt> appointmentList = FXCollections.observableArrayList();
    
    String qContents = null;
    ZoneId locationHolder;
    ZoneId myLocationZone = ZoneId.systemDefault();
    private Integer dbUserID = null;
    private String dbUserName = null;
    private String dbUserPw = null;
    private String localUserPW = null;
    private String localUserName = null;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            DBConnection.makeConnection();
            //Create Statement object
            String sqlStatement = "SELECT appointment.title, appointment.location, appointment.start, appointment.end, appointment.contact, appointment.customerID, customer.customerName, appointment.description, appointment.appointmentID FROM appointment INNER JOIN customer ON appointment.customerId=customer.customerId";
  
            //use Query class to check sql statement for type of query
            Query.makeQuery(sqlStatement);
            //Execute Statement and Create ResultSet object
            ResultSet result = Query.getResult();
            System.out.println(result);
            //Get all records from result set object
            while(result.next()){
                String dbApptTitle = result.getString("appointment.title");
                String dbApptLocation = result.getString("appointment.location");
                //Based on the meeting location generate the time zone ID
                switch(dbApptLocation){
                    case "New York, New York":
                    locationHolder = ZoneId.of("US/Eastern");
                    case "Online":
                    locationHolder = ZoneId.of("US/Eastern");
                    case "Phoenix, Arizona":
                    locationHolder = ZoneId.of("US/Arizona");
                    case "London, England":
                    locationHolder = ZoneId.of("Europe/London");
                
                }
                //grab the start time as a timestamp
                //Using zoneddatetime atuomatically adjusts for daylight savings
                Timestamp localApptStart = result.getTimestamp("appointment.start");
                //set the tsStart to the location zoned time
                ZonedDateTime localZoneApptStart = ZonedDateTime.ofInstant(localApptStart.toInstant(), locationHolder);
                //now convert the ZonedDateTime to the local time zone
                ZonedDateTime transitStartTime = localZoneApptStart.withZoneSameInstant(myLocationZone);
                //Convert the local time zone to a string to store in 
                String dbApptStart = DateTimeFormatter.ISO_ZONED_DATE_TIME.format(transitStartTime);
                //Grab the appt end - convert to local time zone and string
                Timestamp localApptEnd = result.getTimestamp("appointment.end");
                ZonedDateTime localZoneApptEnd = ZonedDateTime.ofInstant(localApptEnd.toInstant(), locationHolder);
                ZonedDateTime transitEndTime = localZoneApptEnd.withZoneSameInstant(myLocationZone);
                String dbApptEnd = DateTimeFormatter.ISO_ZONED_DATE_TIME.format(transitEndTime);
                String dbApptContact = result.getString("appointment.contact");
                Customer dbCustomer = new Customer(result.getInt("appointment.customerID"),result.getString("customer.customerName"));
                String dbDescription = result.getString("appointment.description");
                Integer DBApptID = result.getInt("appointment.appointmentID");
                Appt appt = new Appt(DBApptID, dbApptTitle, dbApptStart, dbApptEnd, dbApptContact, dbCustomer, dbDescription, dbApptLocation);
                //debug messages to let us know when a new Appt is created
                System.out.println("Created appt " + appt.getAppointmentTitle());
                //add the array to the observable list
                appointmentList.add(appt);
                System.out.println("Added AppointmentList");
                
        }
        DBConnection.closeConnection();
        } catch (SQLException sqe){
            //Show SQL connection messages
            System.out.println("Error: " + sqe.getMessage());
        } catch (Exception ex) {
            System.out.println("Code Barfed " + ex.getMessage());
        }
        
        
    }   
    
    @FXML
    private void UserLoginSigninButtonHandler(ActionEvent event) throws IOException {
        //check for empty fields
        localUserName = UserLoginUserNameField.getText();
        localUserPW = UserLoginPasswordField.getText();
        System.out.println("my locale= " + myLocale);
        try {
            DBConnection.makeConnection();
            //Create Statement object
            //SQL Statement to grab the user login
            String sqlStatement = "SELECT userID, UserName, password FROM user WHERE userName = '" + localUserName + "'";

            //use Query class to check sql statement for type of query
            Query.makeQuery(sqlStatement);

            //Execute Statement and Create ResultSet object
            ResultSet result = Query.getResult();

            //userId,userName,password,active,createBy,createDate,lastUpdatedBy
            //Get all records from result set object
            while(result.next()){
            dbUserID = result.getInt("userID");
            repo.setrepoUserId(dbUserID);
            //Debug - Print UserID
            System.out.print(dbUserID + ", ");
            dbUserName = result.getString("userName");
            repo.setrepoUserName(dbUserName);
            //Debug - Print UserID
            System.out.print(dbUserName + ", ");
            dbUserPw = result.getString("password");
            //Debug - Print UserID
            System.out.println(dbUserPw);
            
        }
        
        DBConnection.closeConnection();
        } catch (Exception ex){
            //Show SQL connection messages
            System.out.println("Error: " + ex.getMessage());
        } 
        
        //check to see if the user is in the database
        if (dbUserName == null){
            //set the login box to not found
            //Check for Russian or English and default to english
            if (myLocale.getLanguage().equals("Russian")){
                //Translate error to Russian
                UserLoginErrorLabel.setText("Пользователя " + localUserName + " не найден");
            } else if(myLocale.getLanguage().equals("English")){
                UserLoginErrorLabel.setText("User: " + localUserName + " Not Found");
            } else {
                UserLoginErrorLabel.setText("User: " + localUserName + " Not Found");
            }
        } else {
            //if the user is found then
            //check to see if the password matches what is in the database        
            if (localUserPW.equals(dbUserPw)){
                //Set the error box to welcome
                if (myLocale.getLanguage().equals("Russian")){
                //Translate error to Russian
                    UserLoginErrorLabel.setText("Приветствовать!");
                } else if(myLocale.getLanguage().equals("English")){
                    UserLoginErrorLabel.setText("Welcome!");
                } else {
                    UserLoginErrorLabel.setText("Welcome!");
                }
            
            //open the calendar page
            //get reference to the button's stage         
            Stage stage; 
            Parent root;
            stage=(Stage) UserLoginSigninButton.getScene().getWindow();
            //load up OTHER FXML document
            root = FXMLLoader.load(getClass().getResource("Calendar.fxml"));
            //create a new scene with root and set the stage
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            } else {
                //bad password - set the error box to bad password
                if (myLocale.getLanguage().equals("Russian")){
                    //Translate error to Russian
                    UserLoginErrorLabel.setText("Неверный пароль");
                } else if(myLocale.getLanguage().equals("English")){
                    UserLoginErrorLabel.setText("Password Incorrect");
                } else {
                    UserLoginErrorLabel.setText("Password Incorrect");
                }
                
                
            }
        }
    }


    @FXML
    private void UserLoginExitButtonHandler(ActionEvent event) {
        //put up an alert pop up window asking for confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Are you sure you want to exit?");
        //record which button was clicked
        Optional<ButtonType> x = alert.showAndWait();
        //if the OK button is clicked then go ahead and remove the part
        if (x.get() == ButtonType.OK){
            System.exit(0);
            
        }
    }
    
    private void Remind(){
        //Lamda expression to filter the array list for user and appointment
        /* This is the check for time
        ZonedDateTime myMonthDateNow = ZonedDateTime.now(myLocationZone).minusDays(1);
        //grab the system date and add a Month
        ZonedDateTime mySystemMonth = ZonedDateTime.now(myLocationZone).plusMonths(1);
        ObservableList<Appt> displayMon = appointmentList.stream()
                .filter(p -> ZonedDateTime.parse(p.getStart()).isAfter(myMonthDateNow) && ZonedDateTime.parse(p.getStart()).isBefore(mySystemMonth))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        System.out.println("The Month is: "+ displayMon.toString());
        //set the table view to display the filtered list
        CalendarTable.setItems(displayMon);
        */
        
        /*Lamda to check for user - filter appts into the next few minutes - if there are not any then set the message to skip the user check
        String userHolder = ReportConsultantPicker.getValue();
        displayUser = apptReportList.stream().filter(p -> p.getContact().equals(userHolder)).collect(Collectors.toCollection(FXCollections::observableArrayList));
        ReportTable.setItems(displayUser);
        */
        //pop up an alert box with the appointments in 15 min or no appt
        
        
        
    }
    
    private void LogToFile(){
        
    }
    
    
}
