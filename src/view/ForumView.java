/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.MainController;
import entity.DiscussionThread;
import entity.User;
import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author Jirka_
 */
public class ForumView extends VBox {
    
    private final double width = 500;
    private final double height = 500;
    
    private Stage primaryStage;
    private Scene scene;
    
    // komponenty
    private Label loginLabel = new Label("Registrace");
    
    private Label usernameLabel = new Label("Uživatelské jméno");
    private Label emailLabel = new Label("Email");
    private Label passwordLabel = new Label("Heslo");
    
    
    private TextField usernameTextField = new TextField();
    private TextField emailTextField = new TextField();
    private PasswordField passwordTextField = new PasswordField();
    
    
    private Button registerButton = new Button("Registrovat se");
    
    private MainController controller;
    
    
    public ForumView(MainController controller) {

        this.controller = controller;
        
        init();
    }
    

    private void init() {
        ArrayList<DiscussionThread> discussionThreadsList = controller.getForum().getDiscussionThreadsList();
        
        discussionThreadsList.forEach(thread -> {
            
        });
        
        
        
        
        this.getChildren().addAll(
                new HBox(usernameLabel, usernameTextField),
                new HBox(emailLabel, emailTextField),
                new HBox(passwordLabel, passwordTextField),
                new HBox(registerButton));
        
        
        
    }
    
    
}
