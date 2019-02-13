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
    private ComboBox<?> CustomerEditComboCity;
    @FXML
    private TableView<Customer> CustomerEditCustomerTable;
    @FXML
    private TableColumn<Customer, Integer> CustomerEditCustIDCol;
    @FXML
    private TableColumn<Customer, String> CustomerEditCustNameCol;
    @FXML
    private Button CustomerEditAddButton;
    @FXML
    private Button CustomerEditEditButton;
    @FXML
    private Button CustomerEditRemoveButton;
    @FXML
    private Button CustomerEditSaveButton;
    @FXML
    private Button CustomerEditCancelButton;
    
    ObservableList<Customer> custList = FXCollections.observableArrayList();
    ObservableList<String>cityList = FXCollections.observableArrayList();
    private Repo currentRepo;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
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
                Customer customerEdit = new Customer(dbCustID, dbCustName, dbAddress, dbAddress2, dbCity, dbCountry, dbPost, dbPhone);
                custList.add(customerEdit);               
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
        
    }

    @FXML
    private void CustomerEditFieldIDHandler(ActionEvent event) {
    }

    @FXML
    private void CustomerEditFieldNameHandler(ActionEvent event) {
    }

    @FXML
    private void CustomerEditFieldAddrHandler(ActionEvent event) {
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
    private void CustomerEditAddButtonHandler(ActionEvent event) {
    }

    @FXML
    private void CustomerEditEditButtonHandler(ActionEvent event) {
    }

    @FXML
    private void CustomerEditRemoveButtonHandler(ActionEvent event) {
    }

    @FXML
    private void CustomerEditSaveButtonHandler(ActionEvent event) {
    }

    

    @FXML
    private void CustomerEditComboCityHandler(ActionEvent event) {
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

    
}
