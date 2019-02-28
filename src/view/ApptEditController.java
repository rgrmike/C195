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
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
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
public class ApptEditController implements Initializable {

    @FXML
    private Button ApptEditSave;
    @FXML
    private Button ApptEditCancel;
    @FXML
    private TableView<Customer> ApptEditCustTable;
    @FXML
    private TableColumn<Customer, String> ApptEditCol;
    @FXML
    private TableColumn<Customer, Integer> ApptEditCustIDCol;
    @FXML
    private DatePicker ApptEditDatePicker;
    @FXML
    private ComboBox<String> ApptEditStart;
    @FXML
    private ComboBox<String> ApptEditEnd;
    @FXML
    private ComboBox<String> ApptEditTypeCombo;
    @FXML
    private TextField ApptEditTypeField;
    @FXML
    private ComboBox<String> ApptEditLocation;
    @FXML
    private ComboBox<String> ApptEditContact;
    
    private Repo currentRepo;
    ObservableList<Customer> custList = FXCollections.observableArrayList();
    ObservableList<String>apptTypeList = FXCollections.observableArrayList();
    ObservableList<String>apptLocationList = FXCollections.observableArrayList();
    ObservableList<String>apptStartList = FXCollections.observableArrayList();
    ObservableList<String>apptEndList = FXCollections.observableArrayList();
    ObservableList<String>apptUserList = FXCollections.observableArrayList();
    Integer newMaxApptId = 0;
    ZoneId saveLocationHolder;
    ZoneId saveLocationZone = ZoneId.systemDefault();
    Integer editApptId;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //populate the customer table
        custTableFill();
        //populate the combo boxes
        apptTypeList.addAll("First Meeting", "First Consultation", "Follow-up");
        ApptEditTypeCombo.setItems(apptTypeList);
        ApptEditContact.setItems(apptUserList);
        ApptEditTypeCombo.getSelectionModel().select(0);
        apptLocationList.addAll("New York, New York", "Phoenix, Arizona", "London, England", "Online");
        ApptEditLocation.setItems(apptLocationList);
        //prevent user from entering meeting outside business hours
        ApptEditLocation.getSelectionModel().select(0);
        apptStartList.addAll("08:00:00","09:00:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00");
        ApptEditStart.setItems(apptStartList);
        ApptEditStart.getSelectionModel().select(0);
        apptEndList.addAll("09:00:00","10:00:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00");
        ApptEditEnd.setItems(apptEndList);
        ApptEditEnd.getSelectionModel().select(0);
        ApptEditCustTable.getSelectionModel().select(0);
        //wait for the form to fully load before trying to check the repo class to prevent null pointer exception
        Platform.runLater(() -> {
        if(currentRepo.getrepoIsEdit()==true){
            //if isEdit is true then grab the selected appointment and populate the list
            Appt transfer = currentRepo.getRepoSelectEditApt();
            editApptId = transfer.getAppointmentID();
            //customer ID is off by 1 so we are subtracting one to get it to show up correctly
            ApptEditCustTable.getSelectionModel().select((transfer.getCustomer().getCustomerId()-1));
            ApptEditTypeField.setText(transfer.getAppointmentTitle());
            ApptEditTypeCombo.setValue(transfer.getDescription());
            ApptEditDatePicker.setValue(ZonedDateTime.parse(transfer.getStart()).toLocalDate());
            ApptEditStart.setValue(ZonedDateTime.parse(transfer.getStart()).toLocalTime().toString());
            ApptEditEnd.setValue(ZonedDateTime.parse(transfer.getEnd()).toLocalTime().toString());
            ApptEditLocation.setValue(transfer.getLocation());
            ApptEditContact.setValue(transfer.getContact());
        }
        }
        );
    } 
    
    public void setRepo(Repo moveRepo){
        this.currentRepo = moveRepo;
    }
    
    private void custTableFill (){
        // Populate custList
        try {
            DBConnection.makeConnection();
            String sqlStatement = "SELECT c.customerId, c.customerName, a.addressId, a.address, a.address2, a.cityId, y.city, t.country, a.postalCode, a.phone FROM customer c INNER JOIN address a ON c.addressID = a.addressID JOIN city y ON y.cityId = a.cityId JOIN country t ON y.countryId = t.countryId";
            Query.makeQuery(sqlStatement);
            ResultSet resultOne = Query.getResult();
            while(resultOne.next()){
                Integer dbCustID = resultOne.getInt("c.customerId");
                String dbCustName = resultOne.getString("c.customerName");
                Integer dbAddressId = resultOne.getInt("a.addressId");
                String dbAddress = resultOne.getString("a.address");
                String dbAddress2 = resultOne.getString("a.address2");
                Integer dbCityId = resultOne.getInt("a.cityId");
                String dbCity = resultOne.getString("y.city");
                String dbCountry = resultOne.getString("t.country");
                String dbPost = resultOne.getString("a.postalCode");
                String dbPhone = resultOne.getString("a.phone");
                Customer cust = new Customer(dbCustID, dbCustName, dbAddressId, dbAddress, dbAddress2, dbCityId, dbCity, dbCountry, dbPost, dbPhone);
                custList.add(cust);               
            }
            
            String sqltwo = "SELECT userName from user";
            Query.makeQuery(sqltwo);
            ResultSet resulttwo = Query.getResult();
            while(resulttwo.next()){
                String dbUsrName = resulttwo.getString("userName");
                apptUserList.add(dbUsrName);
                
            }
            DBConnection.closeConnection();
        } catch (SQLException sqe){
            //Show SQL connection messages
            System.out.println("Error: " + sqe.getMessage());
        } catch (Exception ex) {
            System.out.println("Code Barfed " + ex.getMessage());
        }

        ApptEditCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        ApptEditCustIDCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        ApptEditCustTable.getItems().setAll(custList);
        
    }

    @FXML
    private void ApptEditSaveHandler(ActionEvent event) throws IOException {
        //grab the values from the form
        String errorMsg = "";
        String dbUpdateCustId;
        dbUpdateCustId = ApptEditCustTable.getSelectionModel().getSelectedItem().getCustomerId().toString();
        String dbUpdateTitle = ApptEditTypeField.getText();
        String dbUpdateDesc = ApptEditTypeCombo.getValue();
        String dbUpdateContact = ApptEditContact.getValue();
        LocalDate dbUpdateDate = ApptEditDatePicker.getValue();
        String dbUpdateLocation = ApptEditLocation.getValue();
        LocalTime dbUpdateStartTime = LocalTime.parse(ApptEditStart.getValue());
        LocalTime dbUpdateEndTime = LocalTime.parse(ApptEditEnd.getValue());
        if (dbUpdateTitle == null || dbUpdateTitle.isEmpty()){
            errorMsg += "Please enter Title. ";
        } 
        if (dbUpdateDesc == null || dbUpdateDesc.isEmpty()){
            errorMsg += "Please enter Description. ";
        }
        if (dbUpdateContact == null || dbUpdateContact.isEmpty()){
            errorMsg += "Please enter Contact. ";
        }
         if (dbUpdateLocation == null || dbUpdateLocation.isEmpty()){
            errorMsg += "Please enter Location. ";
        } else if (dbUpdateEndTime.equals(dbUpdateStartTime) || dbUpdateEndTime.isBefore(dbUpdateStartTime)){
            errorMsg += "The Appointment End time must be after the start time. ";
        }
        //if the error is blank then run the Save code
         if (errorMsg == ""){
            //figure out which location the appt is from
            if (dbUpdateLocation.equals("New York, New York")){
                    saveLocationHolder = ZoneId.of("US/Eastern");
                } else if (dbUpdateLocation.equals("Online")){
                    saveLocationHolder = ZoneId.of("US/Eastern");
                } else if (dbUpdateLocation.equals("Phoenix, Arizona")){
                    saveLocationHolder = ZoneId.of("US/Arizona");
                } else if (dbUpdateLocation.equals("London, England")){
                    saveLocationHolder = ZoneId.of("Europe/London");
                }
            //get the time from the combo box and convert it to local time
            //create a zoned date time entry as a local time zone and convert the local time to the time zone of where the appointment is
            ZonedDateTime dbUpdateZOneStart = ZonedDateTime.of(dbUpdateDate, dbUpdateStartTime, saveLocationZone).withZoneSameInstant(saveLocationHolder);
            ZonedDateTime dbUpdateZOneEnd = ZonedDateTime.of(dbUpdateDate, dbUpdateEndTime, saveLocationZone).withZoneSameInstant(saveLocationHolder);
            //transition to timestamp and then string for mysql
            //look up the .isafter setting to validate the code
            Timestamp transferStart = Timestamp.valueOf(dbUpdateZOneStart.toLocalDateTime());
            Timestamp transferEnd = Timestamp.valueOf(dbUpdateZOneEnd.toLocalDateTime());
            String dbUStart = transferStart.toString();
            String dbUEnd = transferEnd.toString();
            
            //check to see if there is a conflict for our appointment
            if (checkConflict(dbUStart, dbUEnd) == true) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Conflict");
                alert.setContentText("Conflicting appointment found - please check times");
                alert.showAndWait();
            } else {
                //If all error conditions are ok then go ahead and update the database
                if(currentRepo.getrepoIsEdit()==true){
                    System.out.println("Executing appointment edit save.");
                    //convert the apptID to a string for and update
                    String dbApptId = editApptId.toString();
                    //if isEdit is true then we are updating an existing record
                    try {
                        DBConnection.makeConnection();
                        String sqlStatement = "UPDATE appointment SET customerId = " + dbUpdateCustId + " , userid = " + currentRepo.getrepoUserId().toString() +", title = '" + dbUpdateTitle + "', description = '" + dbUpdateDesc + "', location = '" + dbUpdateLocation + "', contact = '"+ dbUpdateContact +"',start = '"+ dbUStart +"', end = '"+ dbUEnd +"', lastUpdate = CURRENT_TIMESTAMP, lastUpdateBy ='" + currentRepo.getrepoUserName() + "' WHERE appointmentId = " + dbApptId;
                        Query.makeQuery(sqlStatement);
                        ResultSet result = Query.getResult();
                        System.out.println(result);

                    DBConnection.closeConnection();
                    } catch (SQLException sqe){
                        //Show SQL connection messages
                        System.out.println("Error: " + sqe.getMessage());
                    } catch (Exception ex) {
                        System.out.println("Code Barfed " + ex.getMessage());
                    }
                    //make sure we pass repo back to the calendar form           
                    Stage stage;
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Calendar.fxml"));     
                    Parent root = (Parent)fxmlLoader.load();          
                    //initialize the ApptEditController page as an fxml loader so we can pass values
                    CalendarController controller;
                        controller = fxmlLoader.<CalendarController>getController();
                    //send the repo class to CalendarController
                    controller.setRepo(currentRepo);
                    Scene scene = new Scene(root); 
                    stage=(Stage) ApptEditCancel.getScene().getWindow();
                    stage.setScene(scene);    
                    stage.show(); 
                }else if (currentRepo.getrepoIsEdit()==false){
                    System.out.println("Executing new appointment save.");
                    //this is a new appointment so we are going to insert it into the apppointments table
                    //first grab the last appointmentId from the database
                    try {
                        DBConnection.makeConnection();
                        String sqlStatement = "SELECT MAX(appointmentId) as appointmentId FROM appointment";
                        Query.makeQuery(sqlStatement);
                        ResultSet result = Query.getResult();
                        while(result.next()){
                        Integer dbMaxApptId = result.getInt("appointmentId");
                        //increment the appointmentId by 1
                        newMaxApptId = dbMaxApptId +1;
                        System.out.println("New Appointment ID: " + newMaxApptId );
                        }
                        //replace admin with user from user class
                        String sqlStatementtwo = "INSERT INTO appointment (appointmentId,customerId,userid,title,description,location,contact,type,url,start,end,createDate,createdBy,lastUpdate,lastUpdateBy) VALUES (" + newMaxApptId.toString() +", " +dbUpdateCustId +", " + currentRepo.getrepoUserId().toString()+ ", '" + dbUpdateTitle + "', '" + dbUpdateDesc + "', '" + dbUpdateLocation + "', '" + dbUpdateContact + "', 'URL/NA', 'Type/NA', '" + dbUStart + "', '" + dbUEnd + "', CURRENT_TIMESTAMP,'" + currentRepo.getrepoUserName() + "', CURRENT_TIMESTAMP,'" + currentRepo.getrepoUserName() +"')";
                        Query.makeQuery(sqlStatementtwo);
                        ResultSet resulttwo = Query.getResult();
                        while(resulttwo.next()){
                            System.out.println("New Appointment result: " + resulttwo.toString());
                        }
                    DBConnection.closeConnection();
                    } catch (SQLException sqe){
                        //Show SQL connection messages
                        System.out.println("Error: " + sqe.getMessage());
                    }   catch (Exception ex) {
                        System.out.println("Code Barfed " + ex.getMessage());
                    }
                    //make sure we pass repo back to the calendar form           
                    Stage stage;
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Calendar.fxml"));     
                    Parent root = (Parent)fxmlLoader.load();          
                    //initialize the ApptEditController page as an fxml loader so we can pass values
                    CalendarController controller;
                        controller = fxmlLoader.<CalendarController>getController();
                    //send the repo class to CalendarController
                    controller.setRepo(currentRepo);
                    Scene scene = new Scene(root); 
                    stage=(Stage) ApptEditCancel.getScene().getWindow();
                    stage.setScene(scene);    
                    stage.show(); 
                }
            }
        } else {
            //Show the error
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Form Error");
            alert.setContentText(errorMsg);
            alert.showAndWait();
        }
        
    }
    
    private boolean checkConflict(String checkStart, String checkEnd) {
        String dbAppointmentId;
        if(currentRepo.getrepoIsEdit()==true){
            //if true this is an edit
            dbAppointmentId = currentRepo.getRepoSelectEditApt().getAppointmentID().toString();                   
        } else {
            //this is a new appoinment
            dbAppointmentId = "0";
        }
        
        try {
                    DBConnection.makeConnection();
                    String sqlCheckConflict = "SELECT * FROM appointment WHERE ('" + checkStart + "' BETWEEN start AND end OR '" + checkEnd + "' BETWEEN start AND end) AND appointmentID != " + dbAppointmentId;
                    Query.makeQuery(sqlCheckConflict);
                    ResultSet resultCheckConflict = Query.getResult();
                    if (resultCheckConflict.next()){
                        return true;
                    }
                   

                DBConnection.closeConnection();
                } catch (SQLException sqe){
                    //Show SQL connection messages
                    System.out.println("Error: " + sqe.getMessage());
                } catch (Exception ex) {
                    System.out.println("Code Barfed " + ex.getMessage());
                }
        return false;
        
        
    }

    @FXML
    private void ApptEditCancelHandler(ActionEvent event) throws IOException {
            //confirm that the user wants to exit the form
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Are you sure you want to cancel?");
        Optional<ButtonType> x = alert.showAndWait();
        //if the user clicks ok then go ahead and load the main screen
        if (x.get() == ButtonType.OK) {
            //make sure we pass repo back to the calendar form           
            Stage stage;
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Calendar.fxml"));     
        Parent root = (Parent)fxmlLoader.load();          
        //initialize the ApptEditController page as an fxml loader so we can pass values
        CalendarController controller;
            controller = fxmlLoader.<CalendarController>getController();
        //send the repo class to CalendarController
        controller.setRepo(currentRepo);
        Scene scene = new Scene(root); 
        stage=(Stage) ApptEditCancel.getScene().getWindow();
        stage.setScene(scene);    
        stage.show(); 
        }
    }
    
   
    
    @FXML
    private void ApptEditDatePickerHandler(ActionEvent event) {
    }

    @FXML
    private void ApptEditStartHandler(ActionEvent event) {
    }

    @FXML
    private void ApptEditEndHandler(ActionEvent event) {
    }


    @FXML
    private void ApptEditTypeFieldHandler(ActionEvent event) {
    }

    @FXML
    private void ApptEditTypeComboHandler(ActionEvent event) {
    }

    @FXML
    private void ApptEditLocationHandler(ActionEvent event) {
    }

    @FXML
    private void ApptEditContactHandler(ActionEvent event) {
    }
    
}
