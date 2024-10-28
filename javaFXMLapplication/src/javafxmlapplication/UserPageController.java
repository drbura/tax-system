package javafxmlapplication;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class UserPageController {

    @FXML
    private TextField Tin_Number;

    @FXML
    private PasswordField password;

    @FXML
    private TextField tinNumber;

    @FXML
    private TextField firstName;

    @FXML
    private ImageView IMG_url;

    @FXML
    private TextField lastName;

    @FXML
    private TextField PHONE;

    @FXML
    private Button sendButton;

    @FXML
    private Button bankRecipt;

    @FXML
    private Button logout;

    @FXML
    private TabPane tabPane;

    private File selectedFile;
    
    @FXML
    private TextField tinNumberField; 

    @FXML
    private Button statusButton; 

    public void initialize() {
       
        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getText().equals("Tax Amount")) {
                addButtonToTaxAmountTab();
            }
        });

        
        bankRecipt.setOnAction(this::bankReciptActionHandler);
        
        
        sendButton.setOnAction(this::sendButtonActionHandler);

        
        statusButton.setOnAction(this::statusActionHandler);

       
        addButtonToTaxAmountTab();
    }

    private void addButtonToTaxAmountTab() {
        
        Tab taxAmountTab = tabPane.getTabs().stream()
                .filter(tab -> "Tax Amount".equals(tab.getText()))
                .findFirst()
                .orElse(null);

        if (taxAmountTab != null) {
            
            AnchorPane content = (AnchorPane) taxAmountTab.getContent();

           
            boolean buttonExists = content.getChildren().stream()
                    .anyMatch(node -> node instanceof Button && "Ok".equals(((Button) node).getText()));

            if (!buttonExists) {
                
                Button okButton = new Button("Ok");

                okButton.setLayoutX(100);
                okButton.setLayoutY(200);
                okButton.setPrefHeight(31);
                okButton.setPrefWidth(100);

               
                okButton.setOnAction(event -> {
                    try {
                        String tin = Tin_Number.getText();
                        String pwd = password.getText();
                        if (!tin.isEmpty() && !pwd.isEmpty()) {
                            
                            double amount = getAmountForTIN(tin);
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Tax Amount");
                            alert.setHeaderText(null);
                            alert.setContentText("The tax amount for TIN " + tin + " is: " + amount);
                            alert.showAndWait();
                        } else {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText(null);
                            alert.setContentText("TIN number or password is empty");
                            alert.showAndWait();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                
                content.getChildren().add(okButton);
            }
        }
    }

    @FXML
    private void bankReciptActionHandler(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            IMG_url.setImage(image);
        }
    }

    @FXML
    private void sendButtonActionHandler(ActionEvent event) {
        try {
            String tin = tinNumber.getText();
            String fname = firstName.getText();
            String lname = lastName.getText();
            String phone = PHONE.getText();

            if (!tin.isEmpty() && !fname.isEmpty() && !lname.isEmpty() && !phone.isEmpty() && selectedFile != null) {
                String status = getStatusForTIN(tin);
                if (status.equals("waiting")) {
                    if (!requestExistsForTIN(tin)) {
                        
                        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/taxsystemdatabase", "root", "")) {
                            
                            String sql = "INSERT INTO requestable (tinNumber, firstName, lastName, PHONE, IMG_url) VALUES (?, ?, ?, ?, ?)";
                            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                                stmt.setString(1, tin);
                                stmt.setString(2, fname);
                                stmt.setString(3, lname);
                                stmt.setString(4, phone);
                                stmt.setString(5, selectedFile.getAbsolutePath());
                                stmt.executeUpdate();
                            }
                            
                            
                            updateStatusToRequested(tin);

                           
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Success");
                            alert.setHeaderText(null);
                            alert.setContentText("Request sent successfully!");
                            alert.showAndWait();
                        }
                    } else {
                      
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Warning");
                        alert.setHeaderText(null);
                        alert.setContentText("A request has already been made for this TIN.");
                        alert.showAndWait();
                    }
                } else if (status.equals("not found")) {
                  
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("TIN number not found in the database.");
                    alert.showAndWait();
                } else {
                    
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Request cannot be made for the current status: " + status);
                    alert.showAndWait();
                }
            } else {
                // Show error alert
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Please fill in all fields and select a bank receipt image.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   

    @FXML
    private void logOutActionHandler(ActionEvent event) throws Exception {
       utility.changeToScene(getClass(), event, "FXMLDocument.fxml");
    }

    @FXML
    private void statusActionHandler(ActionEvent event) {
        try {
            String tin = tinNumberField.getText(); // Use the new TextField for TIN number
            if (!tin.isEmpty()) {
                String status = getStatusForTIN(tin);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("User Status");
                alert.setHeaderText(null);
                alert.setContentText("The status for TIN " + tin + " is: " + status);
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("TIN number is empty");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private double getAmountForTIN(String tin) throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/taxsystemdatabase", "root", "");
             PreparedStatement stmt = conn.prepareStatement("SELECT Tax_Amount FROM userdatatable WHERE Tin_number = ?")) {
            stmt.setString(1, tin);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("Tax_Amount");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return 0.0;
    }

    private String getStatusForTIN(String tin) throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/taxsystemdatabase", "root", "");
             PreparedStatement stmt = conn.prepareStatement("SELECT status FROM userdatatable WHERE Tin_number = ?")) {
            stmt.setString(1, tin);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
                } else {
                    return "not found";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private boolean requestExistsForTIN(String tin) throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/taxsystemdatabase", "root", "");
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM requestable WHERE tinNumber = ?")) {
            stmt.setString(1, tin);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return false;
    }

    private void updateStatusToRequested(String tin) throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/taxsystemdatabase", "root", "");
             PreparedStatement stmt = conn.prepareStatement("UPDATE userdatatable SET status = ? WHERE Tin_number = ?")) {
            stmt.setString(1, "requested");
            stmt.setString(2, tin);
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}

