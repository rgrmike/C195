/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.io.IOException;
import java.sql.SQLException;
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
    Customer selCustomer = new Customer();
    
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // Populate custList
        try {
            DBConnection.makeConnection();
            String sqlStatement = "SELECT c.customerId, c.customerName, a.addressId, a.address, a.address2, a.cityId, y.city, t.country, a.postalCode, a.phone FROM customer c INNER JOIN address a ON c.addressID = a.addressID JOIN city y ON y.cityId = a.cityId JOIN country t ON y.countryId = t.countryId";
            Query.makeQuery(sqlStatement);
            ResultSet result = Query.getResult();
            System.out.println(result);
            while(result.next()){
                Integer dbCustID = result.getInt("c.customerId");
                String dbCustName = result.getString("c.customerName");
                Integer dbAddressId = result.getInt("a.addressId");
                String dbAddress = result.getString("a.address");
                String dbAddress2 = result.getString("a.address2");
                Integer dbCityId = result.getInt("a.cityId");
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

    public void setRepo(Repo moveRepo){
        this.currentRepo = moveRepo;
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
        
        selCustomer = CustomerEditCustomerTable.getSelectionModel().getSelectedItem();
        
        if (selCustomer != null){
        CustomerEditFieldID.setText(selCustomer.getCustomerId().toString());
        CustomerEditFieldName.setText(selCustomer.getCustomerName());
        CustomerEditFieldAddr.setText(selCustomer.getAddress());
        CustomerEditFieldAddr2.setText(selCustomer.getAddress2());
        Integer cityIdHolder = selCustomer.getCityId();
        CustomerEditCityTable.getSelectionModel().select(cityIdHolder - 1);
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
    private void CustomerEditSaveButtonHandler(ActionEvent event) throws IOException {
        String formCustId = "";
        //Save fields to db
        //check if this is a new record or not - grab the next customer ID from the database
        if (CustomerEditFieldID.getText() != null){
            //can't edit the ID field so if it is null then it is a new record and we generate the ID
            formCustId = CustomerEditFieldID.getText();
        }
        String fromCustName = CustomerEditFieldName.getText();
        String fromCustAddr = CustomerEditFieldAddr.getText();
        String fromCustAddr2 = CustomerEditFieldAddr2.getText();
        Integer fromCustCityId = CustomerEditCityTable.getSelectionModel().getSelectedItem().getCityId();
        String fromCustZip = CustomerEditFieldZip.getText();
        String fromCustPhone = CustomerEditFieldPhone.getText();
        String errMsg = "noErr";
        //check for any null fields
        if (fromCustName == null || fromCustName.isEmpty()){
            errMsg += "Customer name missing. ";
        }
        if (fromCustAddr == null || fromCustAddr.isEmpty()){
            errMsg += "Customer Address missing. ";
        }
        if (fromCustZip == null || fromCustZip.isEmpty()){
            errMsg += "Customer Zip missing. ";
        } else if (fromCustZip.length() > 10 || fromCustZip.length() < 5){
            errMsg += "Zip code must be valid. ";
        }
        if (fromCustPhone == null || fromCustPhone.isEmpty()){
            errMsg += "Customer phone missing. ";
        } else if (fromCustPhone.length() < 10 || fromCustPhone.length() > 15){
            errMsg += "Phone Number be valid. ";
        }
        if (errMsg.equals("noErr")){
            //if there are no errors found then save the customer data
            if (editMe == true){
                String fromAddressId = selCustomer.getAddressId().toString();
                try{
                   DBConnection.makeConnection();
                   String sqlSOne ="Update address SET address = " + fromCustAddr + ", address2 = " + fromCustAddr2 + ", cityId = " + fromCustCityId.toString() + ", postalCode = " + fromCustZip + ", phone = " + fromCustPhone + ", lastUpdate = CURRENT_TIMESTAMP, lastUpdateBy = " + currentRepo.getrepoUserName() + " WHERE addressId = " + fromAddressId;
                    Query.makeQuery(sqlSOne);
                    String sqlSTwo ="Update customer SET customerName =" + fromCustName + ", lastUpdate = CURRENT_TIMESTAMP, lastUpdateBy = " + currentRepo.getrepoUserName() + " WHERE customerId = " + formCustId;
                    Query.makeQuery(sqlSTwo);
                }catch (SQLException sqe){
                //Show SQL connection messages
                System.out.println("Error: " + sqe.getMessage());
                } catch (Exception ex) {
                    System.out.println("Delete Code Barfed " + ex.getMessage());
                }   
            } else {
                try {
                    //this is a new customer - we check the DB for the next ID's and insert everything into the DB
                    Integer nextCustomerId = 0;
                    Integer nextAddressId = 0;
                    DBConnection.makeConnection();
                    String sqlStatementOne = "SELECT MAX(customerId) FROM customer ";
                    Query.makeQuery(sqlStatementOne);
                    ResultSet resultOne = Query.getResult();
                    while(resultOne.next()){
                        nextCustomerId = (resultOne.getInt("customerId") + 1);
                    }
                    String sqlStatementTwo = "SELECT MAX(addressId) FROM address ";
                    Query.makeQuery(sqlStatementTwo);
                    ResultSet resultTwo = Query.getResult();
                    while(resultTwo.next()){
                        nextAddressId = (resultOne.getInt("addressId") + 1);
                    }

                    String sqlStatementThree ="INSERT INTO address (addressId, address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES ("+ nextAddressId +", "+ fromCustAddr +", " + fromCustAddr2 +", " + fromCustCityId.toString() +", " + fromCustZip +", " + fromCustPhone +", CURRENT_TIMESTAMP," + currentRepo.getrepoUserName() + ", CURRENT_TIMESTAMP," + currentRepo.getrepoUserName() +")";
                    Query.makeQuery(sqlStatementThree);
                    String sqlStatementFour ="INSERT INTO customer (customerId, customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES (" + nextCustomerId + ", " + fromCustName + ", " + nextAddressId + ", 1, CURRENT_TIMESTAMP, " + currentRepo.getrepoUserName() + ", CURRENT_TIMESTAMP, " + currentRepo.getrepoUserName() + ")";
                    Query.makeQuery(sqlStatementFour);

                    DBConnection.closeConnection();
                } catch (SQLException sqe){
                //Show SQL connection messages
                System.out.println("Error: " + sqe.getMessage());
                } catch (Exception ex) {
                    System.out.println("Delete Code Barfed " + ex.getMessage());
                }      
            }
            //after saving close the form and go back to calendar
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
        } else {
            //if errors on the form are found then report what they are
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Form Error");
            alert.setContentText(errMsg);
            alert.showAndWait();
        }
        }

    @FXML
    private void CustomerEditRemoveButtonHandler(ActionEvent event) {
        selCustomer = CustomerEditCustomerTable.getSelectionModel().getSelectedItem();
        
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
