/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package c195mand191;


import java.util.Locale;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Repo;

/**
 *
 * @author mand191
 */
public class C195Mand191 extends Application {
    
    
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/view/UserLogin.fxml"));
        Scene scene = new Scene(root);
        
        stage.setScene(scene);
        stage.show();
    }
    
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        //for testing - set Locale to Spanish es
        //Locale.setDefault(new Locale("es"));
        launch(args);
    }
    
}
