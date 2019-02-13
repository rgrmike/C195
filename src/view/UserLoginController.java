/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.util.Locale;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.DBConnection;
import model.Query;
import model.Repo;

/**
 * FXML Controller class
 *
 * @author mian
 */
public class UserLoginController implements Initializable {
    private Integer dbUserID = null;
    private String dbUserName = null;
    private String dbUserPw = null;
    private String localUserPW = null;
    private String localUserName = null;
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

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println(myLocale);
        // TODO
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
        
    }
    
    private void LogToFile(){
        
    }
    
    
}
