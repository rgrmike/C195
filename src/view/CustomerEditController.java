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
import java.net.URL;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.ResourceBundle;
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
    private Label CustomerEditLabelCountry;
    @FXML
    private TableView<?> CustomerEditCustomerTable;
    @FXML
    private TableColumn<?, ?> CustomerEditCustIDCol;
    @FXML
    private TableColumn<?, ?> CustomerEditCustNameCol;
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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        try {
        DBConnection.makeConnection();
        //Create Statement object
        //Statement stmt = conn.createStatement();
            

        //Write SQL Statement
        String sqlStatement = "SELECT * FROM user";
        //use Query class to check sql statement for type of query
        Query.makeQuery(sqlStatement);
        
        //Execute Statement and Create ResultSet object
        ResultSet result = Query.getResult();
        
        
        //Get all records from result set object
        while(result.next()){
        System.out.print(result.getInt("userID") + ", ");
        System.out.print(result.getString("userName") + ", ");
        System.out.print(result.getString("password") + ", ");
        System.out.print(result.getInt("active") + ", ");
        System.out.print(result.getString("createBy") + ", ");
        System.out.print(result.getDate("createDate") + ", ");
        System.out.print(result.getDate("lastUpdate") + ", ");
        System.out.println(result.getString("lastUpdatedBy"));
        }
        
        DBConnection.closeConnection();
        } catch (Exception ex){
            System.out.println("Error: " + ex.getMessage());
        } 
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
    private void CustomerEditComboCityHandler(ActionEvent event) {
    }
    
}
