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
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.collections.transformation.FilteredList;
import model.Appt;
import model.Customer;
import model.DBConnection;
import model.Query;
import model.MonthRpt;
import model.Repo;

/**
 * FXML Controller class
 *
 * @author mian
 */
public class ReportsController implements Initializable {
    @FXML
    private ComboBox<String> ReportConsultantPicker;
    @FXML
    private DatePicker ReportDatePicker;
    @FXML
    private TableView<Appt> ReportTable;
    @FXML
    private TableColumn<Appt, String> ReportApptCol;
    @FXML
    private TableColumn<Appt, String> ReportStartCol;
    @FXML
    private TableColumn<Appt, String> ReportEndCol;
    @FXML
    private TableColumn<Appt, String> ReportContactCol;
    @FXML
    private TableColumn<Appt, Customer> ReportCustomerCol;
    @FXML
    private TableColumn<Appt, String> ReportDescriptionCol;
    @FXML
    private TableView<MonthRpt> ReportApptCountTable;
    @FXML
    private TableColumn<MonthRpt, String> ReportMonthCol;
    @FXML
    private TableColumn<MonthRpt, String> ReportYearCol;
    @FXML
    private TableColumn<MonthRpt, String> ReportTypeCol;
    @FXML
    private TableColumn<MonthRpt, String> ReportSumCol;
    @FXML
    private Button ReportCancel;
    
    ObservableList<MonthRpt> monthList = FXCollections.observableArrayList();
    ObservableList<Appt>displayDate = FXCollections.observableArrayList();
    ObservableList<Appt> apptReportList = FXCollections.observableArrayList();
    ObservableList<String>apptUserList = FXCollections.observableArrayList();
    ObservableList<Appt>displayUser = FXCollections.observableArrayList();
    private Repo currentRepo;
    ZoneId locationHolder;
    ZoneId myLocationZone = ZoneId.systemDefault();
     /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //populate the ReportConsultantPicker
        
        ReportDatePicker.setValue(LocalDate.now());
        try {
            //gather data for the summary report
            DBConnection.makeConnection();
            String sqlStatement = "SELECT MONTHNAME(start) as Month, YEAR(start) as Year, description, Count(*) as Sum FROM appointment GROUP BY MONTHNAME(start), YEAR(start), description;";
            Query.makeQuery(sqlStatement);
            ResultSet result = Query.getResult();
            while(result.next()){
                String dbMonth = result.getString("Month");
                String dbYear = result.getString("Year");
                String dbType = result.getString("description");
                String dbSum = result.getString("Sum");
                MonthRpt thisMonth = new MonthRpt(dbMonth, dbYear, dbType, dbSum);
                monthList.add(thisMonth);
                
            }
            
            DBConnection.closeConnection();
        } catch (SQLException sqe){
            //Show SQL connection messages
            System.out.println("Error: " + sqe.getMessage());
        } catch (Exception ex) {
            System.out.println("Code Barfed " + ex.getMessage());
        }
        try {
            DBConnection.makeConnection();
            String sqlTwo = "SELECT appointment.title, appointment.location, appointment.start, appointment.end, appointment.contact, appointment.customerID, customer.customerName, appointment.description, appointment.appointmentID FROM appointment INNER JOIN customer ON appointment.customerId=customer.customerId";
            Query.makeQuery(sqlTwo);
            ResultSet r2 = Query.getResult();
            while(r2.next()){
                String dbApptTitle = r2.getString("appointment.title");
                String dbApptLocation = r2.getString("appointment.location");
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
                /*
                //grab the start time as a string
                String dbStartTimeStr = result.getString("appointment.start");
                //Create a formatter that matches SQL
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                //parse the db string into a local date time object
                LocalDateTime tempDateTimeStart = LocalDateTime.parse(dbStartTimeStr, formatter);
                //Tell the ZonedDateTime that the time from the Db is in the time zone where the meeting is
                //Using zoneddatetime atuomatically adjusts for daylight savings
                ZonedDateTime localZoneApptStart = tempDateTimeStart.atZone(locationHolder);
                //now convert the ZonedDateTime to the local time zone
                ZonedDateTime transitStartTime = localZoneApptStart.withZoneSameInstant(myLocationZone);
                //Convert the local time zone to a string to store in 
                String dbApptStart = DateTimeFormatter.ISO_ZONED_DATE_TIME.format(transitStartTime);
                String dbEndTimeStr = result.getString("appointment.end");
                LocalDateTime tempDateTimeEnd = LocalDateTime.parse(dbEndTimeStr, formatter);
                ZonedDateTime localZoneApptEnd = tempDateTimeEnd.atZone(locationHolder);
                ZonedDateTime transitEndTime = localZoneApptEnd.withZoneSameInstant(myLocationZone);
                String dbApptEnd = DateTimeFormatter.ISO_ZONED_DATE_TIME.format(transitEndTime);
                */
                
                String localApptStartStr = r2.getString("appointment.start");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime localApptStart = LocalDateTime.parse(localApptStartStr, formatter);
                ZonedDateTime localZoneApptStart = localApptStart.atZone(locationHolder);
                ZonedDateTime transitStartTime = localZoneApptStart.withZoneSameInstant(myLocationZone);
                String dbApptStart = DateTimeFormatter.ISO_ZONED_DATE_TIME.format(transitStartTime);
                String localApptEndStr = r2.getString("appointment.end");
                LocalDateTime tempDateTimeEnd = LocalDateTime.parse(localApptEndStr, formatter);
                ZonedDateTime localZoneApptEnd = tempDateTimeEnd.atZone(locationHolder);
                ZonedDateTime transitEndTime = localZoneApptEnd.withZoneSameInstant(myLocationZone);
                String dbApptEnd = DateTimeFormatter.ISO_ZONED_DATE_TIME.format(transitEndTime);
                String dbApptContact = r2.getString("appointment.contact");
                Customer dbCustomer = new Customer(r2.getInt("appointment.customerID"),r2.getString("customer.customerName"));
                String dbDescription = r2.getString("appointment.description");
                Integer DBApptID = r2.getInt("appointment.appointmentID");
                Appt apptRpt = new Appt(DBApptID, dbApptTitle, dbApptStart, dbApptEnd, dbApptContact, dbCustomer, dbDescription, dbApptLocation);
                apptReportList.add(apptRpt);
            }
            
            DBConnection.closeConnection();
        } catch (SQLException sqe){
            //Show SQL connection messages
            System.out.println("Error: " + sqe.getMessage());
        } catch (Exception ex) {
            System.out.println("Code Barfed " + ex.getMessage());
        }
                
        try {
            DBConnection.makeConnection();
            String sqlThree = "SELECT userName from user";
            Query.makeQuery(sqlThree);
            ResultSet resultThree = Query.getResult();
            while(resultThree.next()){
                String dbUsrName = resultThree.getString("userName");
                apptUserList.add(dbUsrName);
            }
            
            DBConnection.closeConnection();
        } catch (SQLException sqe){
            //Show SQL connection messages
            System.out.println("Error: " + sqe.getMessage());
        } catch (Exception ex) {
            System.out.println("Code Barfed " + ex.getMessage());
        }
        //Populate the Monthly Report
        ReportMonthCol.setCellValueFactory(new PropertyValueFactory<>("month"));
        ReportYearCol.setCellValueFactory(new PropertyValueFactory<>("year"));
        ReportTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        ReportSumCol.setCellValueFactory(new PropertyValueFactory<>("sum"));
        ReportApptCountTable.getItems().setAll(monthList);
        //Populate the appt List
        ReportStartCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        ReportEndCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        ReportContactCol.setCellValueFactory(new PropertyValueFactory<>("contact"));
        ReportCustomerCol.setCellValueFactory(new PropertyValueFactory<>("customer"));
        ReportDescriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        ReportApptCol.setCellValueFactory(new PropertyValueFactory<>("appointmentTitle"));
        ReportTable.getItems().setAll(apptReportList);
        ReportConsultantPicker.setItems(apptUserList);
        System.out.println(apptUserList.toString() + "Loaded Users into User List.");
    }    

    public void setRepo(Repo moveRepo){
        this.currentRepo = moveRepo;
    }
    
    @FXML
    private void ReportConsultantPickerHandler(ActionEvent event) {
        String userHolder = ReportConsultantPicker.getValue();
        displayUser = apptReportList.stream().filter(p -> p.getContact().equals(userHolder)).collect(Collectors.toCollection(FXCollections::observableArrayList));
        if (displayUser == null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("No Appointments found for selected User.");
        } else {
            ReportTable.setItems(displayUser);
        }
        
        
        
    }

    @FXML
    private void ReportDatePickerHandler(ActionEvent event) {
        
        //grab the contents of the date tool
        LocalDate pickDate = ReportDatePicker.getValue();
        //ZonedDateTime dbUpdateZOneEnd = ZonedDateTime.of(dbUpdateDate, dbUpdateEndTime, saveLocationZone).withZoneSameInstant(saveLocationHolder);
        
        //lamda stream to compare date from the date picker to all of the appts
        //todo figure out how to extract date from zoned date time
        
        displayDate = apptReportList.stream()
                .filter(p -> LocalDate.from(ZonedDateTime.parse(p.getStart())).isEqual(pickDate))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        
        //set the table view to display the filtered list
        if (displayDate == null){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("No Appointments found for selected Date.");
        } else {
        ReportTable.setItems(displayDate);
        }
        }

    @FXML
    private void ReportCancelHandler(ActionEvent event) throws IOException {
                    //confirm that the user wants to exit the form
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Are you sure you want to cancel?");
        Optional<ButtonType> x = alert.showAndWait();
        //if the user clicks ok then go ahead and load the main screen
        if (x.get() == ButtonType.OK) {
            //pass repo back to calendar form
            Stage stage;
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Calendar.fxml"));     
            Parent root = (Parent)fxmlLoader.load();          
            //initialize the ApptEditController page as an fxml loader so we can pass values
            CalendarController controller;
                controller = fxmlLoader.<CalendarController>getController();
            //send the repo class to CalendarController
            controller.setRepo(currentRepo);
            Scene scene = new Scene(root); 
            stage=(Stage) ReportCancel.getScene().getWindow();
            stage.setScene(scene);    
            stage.show(); 
        }
    }
    
}
