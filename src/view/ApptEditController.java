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
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
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
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //populate the customer table
        custTableFill();
        //populate the combo boxes
        apptTypeList.addAll("First Meeting", "First Consultation", "Follow-up");
        ApptEditTypeCombo.setItems(apptTypeList);
        //later - populate using the user class
        apptUserList.addAll ("user1", "user2");
        ApptEditContact.setItems(apptUserList);
        
        ApptEditTypeCombo.getSelectionModel().select(0);
        apptLocationList.addAll("New York, NewYork", "Phoenix, Arizona", "London, England", "Online");
        ApptEditLocation.setItems(apptLocationList);
        ApptEditLocation.getSelectionModel().select(0);
        apptStartList.addAll("08:00:00","09:00:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00");
        ApptEditStart.setItems(apptStartList);
        ApptEditStart.getSelectionModel().select(0);
        apptEndList.addAll("09:00:00","10:00:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00");
        ApptEditEnd.setItems(apptEndList);
        ApptEditEnd.getSelectionModel().select(0);
        
        if(CalendarController.isEdit==true){
            //if isEdit is true then grab the selected appointment and populate the list
            Appt transfer = view.CalendarController.selEditApt;
            editApptId = transfer.getAppointmentID();
            //customer ID is off by 1 so we are subtracting one to get it to show up correctly
            ApptEditCustTable.getSelectionModel().select((transfer.getCustomer().getCustomerId()-1));
            ApptEditTypeField.setText(transfer.getAppointmentTitle());
            ApptEditTypeCombo.setValue(transfer.getDescription());
            ApptEditDatePicker.setValue(ZonedDateTime.parse(transfer.getStart()).toLocalDate());
            ApptEditStart.setValue(ZonedDateTime.parse(transfer.getStart()).toLocalTime().toString());
            ApptEditEnd.setValue(ZonedDateTime.parse(transfer.getEnd()).toLocalTime().toString());
            ApptEditLocation.setValue(transfer.getLocation());
        }
    } 

    private void custTableFill (){
        // Populate custList
        try {
            DBConnection.makeConnection();
            String sqlStatement = "SELECT c.customerId, c.customerName, a.address, a.address2, y.city, t.country, a.postalCode, a.phone FROM customer c INNER JOIN address a ON c.addressID = a.addressID JOIN city y ON y.cityId = a.addressID JOIN country t ON y.countryId = t.countryId";
            Query.makeQuery(sqlStatement);
            ResultSet result = Query.getResult();
            System.out.println(result);
            while(result.next()){
                Integer dbCustID = result.getInt("c.customerId");
                String dbCustName = result.getString("c.customerName");
                String dbAddress = result.getString("a.address");
                String dbAddress2 = result.getString("a.address2");
                String dbCity = result.getString("y.city");
                String dbCountry = result.getString("t.country");
                String dbPost = result.getString("a.postalCode");
                String dbPhone = result.getString("a.phone");
                Customer cust = new Customer(dbCustID, dbCustName, dbAddress, dbAddress2, dbCity, dbCountry, dbPost, dbPhone);
                //debug message
                System.out.println("Created appt " + cust.getCustomerName());
                custList.add(cust);               
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
    private void ApptEditSaveHandler(ActionEvent event) {
        //grab the values from the form
        //going to need to add userid
        String dbUpdateCustId = ApptEditCustTable.getSelectionModel().getSelectedItem().getCustomerId().toString();
        String dbUpdateTitle = ApptEditTypeField.getText();
        String dbUpdateDesc = ApptEditTypeCombo.getValue();
        String dbUpdateContact = ApptEditContact.getValue();
        LocalDate dbUpdateDate = ApptEditDatePicker.getValue();
        String dbUpdateLocation = ApptEditLocation.getValue();
        switch(dbUpdateLocation){
                    case "New York, New York":
                    saveLocationHolder = ZoneId.of("US/Eastern");
                    case "Online":
                    saveLocationHolder = ZoneId.of("US/Eastern");
                    case "Phoenix, Arizona":
                    saveLocationHolder = ZoneId.of("US/Arizona");
                    case "London, England":
                    saveLocationHolder = ZoneId.of("Europe/London");
                
                }
        //get the time from the combo box and convert it to local time
        
        LocalTime dbUpdateStartTime = LocalTime.parse(ApptEditStart.getValue());
        LocalTime dbUpdateEndTime = LocalTime.parse(ApptEditEnd.getValue());
        //create a zoned date time entry as a local time zone and convert the local time to the time zone of where the appointment is
        ZonedDateTime dbUpdateZOneStart = ZonedDateTime.of(dbUpdateDate, dbUpdateStartTime, saveLocationZone).withZoneSameInstant(saveLocationHolder);
        ZonedDateTime dbUpdateZOneEnd = ZonedDateTime.of(dbUpdateDate, dbUpdateEndTime, saveLocationZone).withZoneSameInstant(saveLocationHolder);
        //transition to timestamp and then string for mysql
        //look up the .isafter setting to validate the code
        Timestamp transferStart = Timestamp.valueOf(dbUpdateZOneStart.toLocalDateTime());
        Timestamp transferEnd = Timestamp.valueOf(dbUpdateZOneEnd.toLocalDateTime());

        String dbUStart = transferStart.toString();
        String dbUEnd = transferEnd.toString();
        //convert the apptID to a string for and update
        String dbApptId = editApptId.toString();
        
        if(CalendarController.isEdit==true){
        //if isEdit is true then we are updating an existing record
        try {
            DBConnection.makeConnection();
            String sqlStatement = "UPDATE appointment SET customerId = " + dbUpdateCustId + " , title = " + dbUpdateTitle + ", description = " + dbUpdateDesc + ", location = " + dbUpdateLocation + ", contact = "+ dbUpdateContact +",start = "+ dbUStart +", end = "+ dbUEnd +", lastUpdate = CURRENT_TIMESTAMP, lastUpdateBy = admin WERE appointmentId = " + dbApptId;
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
        }else{
            //this is a new appointment so we are going to insert it into the apppointments table
            //first grab the last appointmentId from the database
            try {
                DBConnection.makeConnection();
                String sqlStatement = "SELECT MAX(appointmentId) FROM appointment";
                Query.makeQuery(sqlStatement);
                ResultSet result = Query.getResult();
                while(result.next()){
                Integer dbMaxApptId = result.getInt("appointmentId");
                //increment the appointmentId by 1
                newMaxApptId = dbMaxApptId +1;
                //replace admin with user from user class
                String sqlStatementtwo = "INSERT INTO appointment appointmentId, customerId, title, description, location, contact, start, end, createDate, createdBy, lastUpdate, lastUpdateBy VALUES (" + newMaxApptId.toString() +", " +dbUpdateCustId +", " + dbUpdateTitle + ", " + dbUpdateDesc + ", " + dbUpdateLocation + ", " + dbUpdateContact + ", " + dbUStart + ", " + dbUEnd + ", CURRENT_TIMESTAMP," + " admin"+", CURRENT_TIMESTAMP," + "admin"+")";
                Query.makeQuery(sqlStatementtwo);
                ResultSet resulttwo = Query.getResult();
                }
            DBConnection.closeConnection();
            } catch (SQLException sqe){
                //Show SQL connection messages
                System.out.println("Error: " + sqe.getMessage());
            }   catch (Exception ex) {
                System.out.println("Code Barfed " + ex.getMessage());
            }
            
        }
        
    }

    @FXML
    private void ApptEditCancelHandler(ActionEvent event) throws IOException {
            //confirm that the user wants to exit the form
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Are you sure you want to cancel?");
        Optional<ButtonType> x = alert.showAndWait();
        //if the user clicks ok then go ahead and load the main screen
        if (x.get() == ButtonType.OK) {
            Stage stage; 
            Parent root;
            //get reference to the button's stage         
            stage=(Stage) ApptEditCancel.getScene().getWindow();
            //load up OTHER FXML document
            root = FXMLLoader.load(getClass().getResource("Calendar.fxml"));
            //create a new scene with root and set the stage
            Scene scene = new Scene(root);
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
    
}
