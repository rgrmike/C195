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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.DBConnection;
import model.Query;
import model.Customer;
import model.Appt;
import model.Repo;
 

/**
 * FXML Controller class
 *
 * @author mian
 */
public class CalendarController implements Initializable {
    
    
    @FXML
    private TableView<Appt> CalendarTable;
    @FXML
    private TableColumn<Appt, String> CalendarApptCol;
    @FXML
    private TableColumn<Appt, String> CalendarStartCol;
    @FXML
    private TableColumn<Appt, String> CalendarEndCol;
    @FXML
    private TableColumn<Appt, String> CalendarContactCol;
    @FXML
    private TableColumn<Appt, Customer> CalendarCustCol;
    @FXML
    private TableColumn<Appt, String> CalendarDescCol;
    @FXML
    private TableColumn<Appt, Integer> CalendarApptID;
    @FXML
    private Button CalendarWeekButton;
    @FXML
    private Button CalendarMonthButton;
    @FXML
    private Button CalendarExitButton;
    @FXML
    private Button CalendarDeleteButton;
    @FXML
    private Button CalendarEditButton;
    @FXML
    private Button CalendarNewButton;
    @FXML
    private Button CalendarCustomersButton;
    @FXML
    private Button CalendarsReportsButton;
    @FXML
    private Button CalendarAllButton;
    //intitalize the observable list as an array list - otherwise populating the list with appt will barf
    
    ObservableList<Appt> appointmentList = FXCollections.observableArrayList();
   
    String qContents = null;
    ZoneId locationHolder;
    ZoneId myLocationZone = ZoneId.systemDefault();
    //used to pass which button is pressed eidit or new
    private Repo currentRepo;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        TableFill();
       
        
    }    

    private void TableFill() {
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
        
        //PropertyValueFactory uses the class initilization variables to populate the table
        CalendarApptCol.setCellValueFactory(new PropertyValueFactory<>("appointmentTitle"));
        CalendarStartCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        CalendarEndCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        CalendarContactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));
        //had to add an override string value to the customer class so that the table would populate the customer name and not the object
        CalendarCustCol.setCellValueFactory(new PropertyValueFactory<>("customer"));
        CalendarDescCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        CalendarApptID.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        CalendarTable.getItems().setAll(appointmentList);
        
    }  
    @FXML
    private void CalendarWeekButtonHandler(ActionEvent event) {
      
        //grab the system date and store it as yesterday - we do this so that we can display all records after yesterday
        ZonedDateTime myDateNow = ZonedDateTime.now(myLocationZone).minusDays(1);
        //grab the system date and add a week
        ZonedDateTime mySystemWeek = ZonedDateTime.now(myLocationZone).plusWeeks(1);
        //using the stream and lamda filter convert the appointmentlist start into a ZonedDateTime from a string
        //and create the displayWk list based on the sorted results
        ObservableList<Appt> displayWk = appointmentList.stream()
                .filter(p -> ZonedDateTime.parse(p.getStart()).isAfter(myDateNow) && ZonedDateTime.parse(p.getStart()).isBefore(mySystemWeek))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        System.out.println("The Week is: "+ displayWk.toString());
        //set the table view to display the filtered list
        CalendarTable.setItems(displayWk);
             
    }

    @FXML
    private void CalendarMonthButtonHandler(ActionEvent event) {
        
        ZonedDateTime myMonthDateNow = ZonedDateTime.now(myLocationZone).minusDays(1);
        //grab the system date and add a Month
        ZonedDateTime mySystemMonth = ZonedDateTime.now(myLocationZone).plusMonths(1);
        ObservableList<Appt> displayMon = appointmentList.stream()
                .filter(p -> ZonedDateTime.parse(p.getStart()).isAfter(myMonthDateNow) && ZonedDateTime.parse(p.getStart()).isBefore(mySystemMonth))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        System.out.println("The Month is: "+ displayMon.toString());
        //set the table view to display the filtered list
        CalendarTable.setItems(displayMon);
    }

    @FXML
    private void CalendarAllButtonHandler(ActionEvent event) {
        //set the Calendar Table contents to appointmentlist
        CalendarTable.setItems(appointmentList);
    }

    @FXML
    private void CalendarExitButtonHandler(ActionEvent event) {
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
    
    @FXML
    private void CalendarDeleteButtonHandler(ActionEvent event) {
        Appt selApt = CalendarTable.getSelectionModel().getSelectedItem();
        
        if (selApt != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Are you sure you want to delete " + selApt.getAppointmentTitle());
            Optional<ButtonType> x = alert.showAndWait();
        //if the OK button is clicked then go ahead and remove the part
        if (x.get() == ButtonType.OK){
            try{
                DBConnection.makeConnection();
            //Create Statement object
            String sqlStatement ="DELETE appointment.* FROM appointment WHERE appointment.appointmentId = " + selApt.getAppointmentID().toString();
            Query.makeQuery(sqlStatement);
                
        DBConnection.closeConnection();
        } catch (SQLException sqe){
            //Show SQL connection messages
            System.out.println("Error: " + sqe.getMessage());
        } catch (Exception ex) {
            System.out.println("Delete Code Barfed " + ex.getMessage());
        }      
            
        }
            
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Nothing Selected");
            alert.setContentText("Click an Appointment to delete");
            alert.showAndWait();
        }
    }

    @FXML
    private void CalendarEditButtonHandler(ActionEvent event) throws IOException {
    
    Appt selEditApt = CalendarTable.getSelectionModel().getSelectedItem();
    currentRepo.setrepoSelectEditApt(selEditApt);
    if (selEditApt != null) {
        currentRepo.setrepoIsEdit(true);
        Stage stage; 
        Parent root;
        stage=(Stage) CalendarEditButton.getScene().getWindow();
        //load up OTHER FXML document
        root = FXMLLoader.load(getClass().getResource("ApptEdit.fxml"));
        //create a new scene with root and set the stage
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
            
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Nothing Selected");
            alert.setContentText("Click an Appointment to edit");
            alert.showAndWait();
        }
    }

    @FXML
    private void CalendarNewButtonHandler(ActionEvent event) throws IOException {
        //make sure we tell the appt edit form that we are making a new appt and not editing an existing one
        //Each time the new button is executed it sets false
        currentRepo.setrepoIsEdit(true);
        //open the calendar page
        //get reference to the button's stage         
        Stage stage; 
        Parent root;
        stage=(Stage) CalendarNewButton.getScene().getWindow();
        //load up OTHER FXML document
        root = FXMLLoader.load(getClass().getResource("ApptEdit.fxml"));
        //create a new scene with root and set the stage
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void CalendarCustomersButtonHandler(ActionEvent event) throws IOException {
            //open the calendar page
            //get reference to the button's stage         
            Stage stage; 
            Parent root;
            stage=(Stage) CalendarCustomersButton.getScene().getWindow();
            //load up OTHER FXML document
            root = FXMLLoader.load(getClass().getResource("CustomerEdit.fxml"));
            //create a new scene with root and set the stage
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
    }

    @FXML
    private void CalendarsReportsButtonHandler(ActionEvent event) throws IOException {
        //open the calendar page
            //get reference to the button's stage         
            Stage stage; 
            Parent root;
            stage=(Stage) CalendarsReportsButton.getScene().getWindow();
            //load up OTHER FXML document
            root = FXMLLoader.load(getClass().getResource("Reports.fxml"));
            //create a new scene with root and set the stage
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
    }
}
