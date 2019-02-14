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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Customer;
import model.DBConnection;
import model.Query;
import model.MonthRpt;

/**
 * FXML Controller class
 *
 * @author mian
 */
public class ReportsController implements Initializable {
    @FXML
    private ComboBox<?> ReportConsultantPicker;
    @FXML
    private DatePicker ReportDatePicker;
    @FXML
    private TableView<?> ReportTable;
    @FXML
    private TableColumn<?, ?> ReportApptCol;
    @FXML
    private TableColumn<?, ?> ReportStartCol;
    @FXML
    private TableColumn<?, ?> ReportEndCol;
    @FXML
    private TableColumn<?, ?> ReportContactCol;
    @FXML
    private TableColumn<?, ?> ReportCustomerCol;
    @FXML
    private TableColumn<?, ?> ReportDescriptionCol;
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
    
    
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            DBConnection.makeConnection();
            String sqlStatement = "SELECT MONTHNAME(start) as Month, YEAR(start) as Year, description, Count(*) as Sum FROM appointment GROUP BY MONTHNAME(start), YEAR(start), description;";
            Query.makeQuery(sqlStatement);
            ResultSet resultTwo = Query.getResult();
            while(resultTwo.next()){
                String dbMonth = resultTwo.getString("Month");
                String dbYear = resultTwo.getString("Year");
                String dbType = resultTwo.getString("description");
                String dbSum = resultTwo.getString("Sum");
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
        ReportMonthCol.setCellValueFactory(new PropertyValueFactory<>("month"));
        ReportYearCol.setCellValueFactory(new PropertyValueFactory<>("year"));
        ReportTypeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        ReportSumCol.setCellValueFactory(new PropertyValueFactory<>("sum"));
        ReportApptCountTable.getItems().setAll(monthList);
        
    }    

    @FXML
    private void ReportConsultantPickerHandler(ActionEvent event) {
    }

    @FXML
    private void ReportDatePickerHandler(ActionEvent event) {
    }

    @FXML
    private void ReportCancelHandler(ActionEvent event) throws IOException {
                    //confirm that the user wants to exit the form
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Are you sure you want to cancel?");
        Optional<ButtonType> x = alert.showAndWait();
        //if the user clicks ok then go ahead and load the main screen
        if (x.get() == ButtonType.OK) {
            Stage stage; 
            Parent root;
            //get reference to the button's stage         
            stage=(Stage) ReportCancel.getScene().getWindow();
            //load up OTHER FXML document
            root = FXMLLoader.load(getClass().getResource("Calendar.fxml"));
            //create a new scene with root and set the stage
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }
    
}
