/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.io.IOException;
import static model.DBConnection.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import model.DBConnection;
import model.Query;
import model.Customer;
import model.Repo;
import model.City;
import java.net.URL;
import java.sql.ResultSet;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author mian
 */
public class CustomerEditController implements Initializable {

    @FXML
    private TextField CustomerEditFieldID;
    @FXML
    private TextField CustomerEditFieldName;
    @FXML
    private TextField CustomerEditFieldAddr;
    @FXML
    private TextField CustomerEditFieldAddr2;
    @FXML
    private TextField CustomerEditFieldZip;
    @FXML
    private TextField CustomerEditFieldPhone;
    @FXML
    private TableView<Customer> CustomerEditCustomerTable;
    @FXML
    private TableColumn<Customer, Integer> CustomerEditCustIDCol;
    @FXML
    private TableColumn<Customer, String> CustomerEditCustNameCol;
    @FXML
    private TableView<City> CustomerEditCityTable;
    @FXML
    private TableColumn<City, Integer> CustomerEditCityIDCol;
    @FXML
    private TableColumn<City, String> CustomerEditCityCol;
    @FXML
    private Button CustomerEditEditButton;
    @FXML
    private Button CustomerEditRemoveButton;
    @FXML
    private Button CustomerEditSaveButton;
    @FXML
    private Button CustomerEditCancelButton;
    
    ObservableList<Customer> custList = FXCollections.observableArrayList();
    ObservableList<City>cityList = FXCollections.observableArrayList();
    private Repo currentRepo;
    private boolean editMe = false;
    
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // Populate custList
        try {
            DBConnection.makeConnection();
            String sqlStatement = "SELECT c.customerId, c.customerName, a.addressId, a.address, a.address2, y.cityId, y.city, t.country, a.postalCode, a.phone FROM customer c INNER JOIN address a ON c.addressID = a.addressID JOIN city y ON y.cityId = a.addressID JOIN country t ON y.countryId = t.countryId";
            Query.makeQuery(sqlStatement);
            ResultSet result = Query.getResult();
            System.out.println(result);
            while(result.next()){
                Integer dbCustID = result.getInt("c.customerId");
                String dbCustName = result.getString("c.customerName");
                Integer dbAddressId = result.getInt("a.addressId");
                String dbAddress = result.getString("a.address");
                String dbAddress2 = result.getString("a.address2");
                Integer dbCityId = result.getInt("y.cityId");
                String dbCity = result.getString("y.city");
                String dbCountry = result.getString("t.country");
                String dbPost = result.getString("a.postalCode");
                String dbPhone = result.getString("a.phone");
                Customer customerEdit = new Customer(dbCustID, dbCustName, dbAddressId, dbAddress, dbAddress2, dbCityId, dbCity, dbCountry, dbPost, dbPhone);
                custList.add(customerEdit);               
            }
            String sqltwo = "SELECT y.cityId, y.city FROM city y";
            Query.makeQuery(sqltwo);
            ResultSet resultTwo = Query.getResult();
            System.out.println(resultTwo);
            while(resultTwo.next()){
                Integer dbPopCityId = resultTwo.getInt("y.cityId");
                String dbPopCity = resultTwo.getString("y.city");
                City city = new City(dbPopCityId, dbPopCity);
                cityList.add(city);                
            }
            
            
            DBConnection.closeConnection();
        } catch (SQLException sqe){
            //Show SQL connection messages
            System.out.println("Error: " + sqe.getMessage());
        } catch (Exception ex) {
            System.out.println("Code Barfed " + ex.getMessage());
        }

        CustomerEditCustNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        CustomerEditCustIDCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        CustomerEditCustomerTable.getItems().setAll(custList);
        CustomerEditCityIDCol.setCellValueFactory(new PropertyValueFactory<>("cityId"));
        CustomerEditCityCol.setCellValueFactory(new PropertyValueFactory<>("city"));
        CustomerEditCityTable.getItems().setAll(cityList);
    }

    @FXML
    private void CustomerEditFieldIDHandler(ActionEvent event) {
    }

    @FXML
    private void CustomerEditFieldNameHandler(ActionEvent event) {
    }

    @FXML
    private void CustomerEditFieldAddr2Handler(ActionEvent event) {
    }

    @FXML
    private void CustomerEditFieldZipHandler(ActionEvent event) {
    }

    @FXML
    private void CustomerEditFieldPhoneHandler(ActionEvent event) {
    }

    @FXML
    private void CustomerEditEditButtonHandler(ActionEvent event) {
        
        Customer selCustomer = CustomerEditCustomerTable.getSelectionModel().getSelectedItem();
        
        if (selCustomer != null){
        CustomerEditFieldID.setText(selCustomer.getCustomerId().toString());
        CustomerEditFieldName.setText(selCustomer.getCustomerName());
        CustomerEditFieldAddr.setText(selCustomer.getAddress());
        CustomerEditFieldAddr2.setText(selCustomer.getAddress2());
        CustomerEditCityTable.getSelectionModel().select(selCustomer.getCityId() - 1);
        CustomerEditFieldZip.setText(selCustomer.getPostalCode());
        CustomerEditFieldPhone.setText(selCustomer.getPhone());
        editMe = true;
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Nothing Selected");
            alert.setContentText("Click a Customer to edit");
            alert.showAndWait();
        }
        
    }
    
    @FXML
    private void CustomerEditSaveButtonHandler(ActionEvent event) {
        Integer nextCustomerId;
        //Save fields to db
        //check if this is a new record or not - grab the next customer ID from the database
        if (editMe == true){
            
        } else {
            try {
                DBConnection.makeConnection();
                //Create Statement object
                String sqlStatementOne = "SELECT MAX(customerId) FROM customer ";
                Query.makeQuery(sqlStatementOne);
                ResultSet resultOne = Query.getResult();
                while(resultOne.next()){
                    nextCustomerId = (resultOne.getInt("customerId") + 1);
                }
                //need to make add cityID
                //one statement for customer and one for address
                String sqlStatementTwo ="";
                Query.makeQuery(sqlStatementTwo);
                DBConnection.closeConnection();
            } catch (SQLException sqe){
            //Show SQL connection messages
            System.out.println("Error: " + sqe.getMessage());
            } catch (Exception ex) {
                System.out.println("Delete Code Barfed " + ex.getMessage());
            }      
        }
    }

    @FXML
    private void CustomerEditRemoveButtonHandler(ActionEvent event) {
        Customer selCustomer = CustomerEditCustomerTable.getSelectionModel().getSelectedItem();
        
        if (selCustomer != null){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Deletion");
            alert.setHeaderText("Are you sure you want to delete " + selCustomer.getCustomerName() + "?");
            Optional<ButtonType> x = alert.showAndWait();
        //if the OK button is clicked then go ahead and remove the appt
        if (x.get() == ButtonType.OK){
            try{
                DBConnection.makeConnection();
            //Create Statement object
            String sqlStatementOne = "DELETE address.* FROM address WHERE address.addressId = " + selCustomer.getAddressId().toString();
            Query.makeQuery(sqlStatementOne);
            String sqlStatement ="DELETE customer.* FROM appointment WHERE customer.customerId = " + selCustomer.getCustomerId().toString();
            Query.makeQuery(sqlStatement);
                
        DBConnection.closeConnection();
        } catch (SQLException sqe){
            //Show SQL connection messages
            System.out.println("Error: " + sqe.getMessage());
            } catch (Exception ex) {
                System.out.println("Delete Code Barfed " + ex.getMessage());
            }      
            
            
        } else {
            Alert alertTwo = new Alert(Alert.AlertType.WARNING);
            alertTwo.setHeaderText("Nothing Selected");
            alertTwo.setContentText("Click a Customer to delete");
            alertTwo.showAndWait();
            }
        }
    }

    

    

     
    @FXML
    private void CustomerEditCancelButtonHandler(ActionEvent event) throws IOException {
                //confirm that the user wants to exit the form
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Are you sure you want to cancel?");
        Optional<ButtonType> x = alert.showAndWait();
        //if the user clicks ok then go ahead and load the main screen
        if (x.get() == ButtonType.OK) {
            Stage stage; 
            Parent root;
            //get reference to the button's stage         
            stage=(Stage) CustomerEditCancelButton.getScene().getWindow();
            //load up OTHER FXML document
            root = FXMLLoader.load(getClass().getResource("Calendar.fxml"));
            //create a new scene with root and set the stage
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }

    @FXML
    private void CustomerEditFieldAddrHandler(ActionEvent event) {
    }

    
}
