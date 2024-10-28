
package javafxmlapplication;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

public class FXMLDocumentController implements Initializable {
    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    

    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField Phone;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Phone.requestFocus();
        Phone.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                validateAndProceed();
            }
        });
        passwordField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                loginButton.fire(); 
            }
        });
    }

    private void validateAndProceed() {
        String phoneNumber = Phone.getText();
        if (isValidPhoneNumber(phoneNumber)) {
            passwordField.requestFocus();
            errorLabel.setText(""); 
        } else {
            errorLabel.setText("Invalid phone number."); 
        }
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("(09|07)\\d{8}");
    }

    @FXML
    private void handleButtonAction(ActionEvent event) throws Exception {
        String phoneNumber = Phone.getText();
        String password = passwordField.getText();

      
        UserRole role = validateLogin(phoneNumber, password);

      
        if (role == UserRole.USER) {
            openUserPage(event);
        } else if (role == UserRole.ADMIN) {
            openAdminPage(event);
        } else {
            errorLabel.setText("Invalid phone number or password.");
        }
    }

    private UserRole validateLogin(String phoneNumber, String password) {
        String query = "SELECT role FROM userdatatable WHERE Phone = ? AND password = ?";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
             
            statement.setString(1, phoneNumber);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String role = resultSet.getString("role");
                if ("USER".equalsIgnoreCase(role)) {
                    return UserRole.USER;
                } else if ("ADMIN".equalsIgnoreCase(role)) {
                    return UserRole.ADMIN;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            errorLabel.setText("Database error: " + e.getMessage());
        }
        return UserRole.INVALID;
    }

    private void openUserPage(ActionEvent event) throws Exception {
        utility.changeToScene(getClass(), event, "userPage.fxml");
    }

    private void openAdminPage(ActionEvent event) throws Exception {
        utility.changeToScene(getClass(), event, "adminScene.fxml");
    }

    private static class UserRole {
        private static final UserRole USER = new UserRole(6);
        private static final UserRole ADMIN = new UserRole(8);
        private static final UserRole INVALID = new UserRole(0);

        private final int passwordLength;

        private UserRole(int passwordLength) {
            this.passwordLength = passwordLength;
        }

        private int getPasswordLength() {
            return passwordLength;
        }
    }
}

