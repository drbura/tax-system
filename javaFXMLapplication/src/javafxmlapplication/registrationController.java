
package javafxmlapplication;

import java.net.URL;
import java.sql.Connection;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;

public class registrationController implements Initializable {

    @FXML
    private TextField First_name;
    @FXML
    private TextField Last_name;
    @FXML
    private TextField Tin_Number;
    @FXML
    private TextField Phone;
    @FXML
    private TextField kebele;
    @FXML
    private TextField Level;
    @FXML
    private TextField Role;
    @FXML
    private TextField Password;
    @FXML
    private TextField Tax_Amount;
    @FXML
    private RadioButton sexFemale;
    @FXML
    private RadioButton sexMale;
    @FXML
    private Button registrationButton;

    private ToggleGroup sexToggleGroup;
    @FXML
    private Button returnToAdminScene;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setEnterKeyHandler(First_name, Last_name);
        setEnterKeyHandler(Last_name, Tin_Number);
        setEnterKeyHandler(Tin_Number, Phone);
        setEnterKeyHandler(Phone, kebele);
        setEnterKeyHandler(kebele, Role);
        setEnterKeyHandler(Role, Level);
        setEnterKeyHandler(Level, Password);
        setEnterKeyHandler(Password, Tax_Amount);

   
        sexToggleGroup = new ToggleGroup();
        sexMale.setToggleGroup(sexToggleGroup);
        sexFemale.setToggleGroup(sexToggleGroup);

       
        sexMale.setSelected(true);
    }

    private void setEnterKeyHandler(TextField currentTextField, TextField nextTextField) {
        currentTextField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                if (validateTextField(currentTextField)) {
                    nextTextField.requestFocus(); 
                }
            }
        });
    }

    private boolean validateTextField(TextField textField) {
        String fieldName = textField.getId();
        String fieldValue = textField.getText().trim();

        
        if (fieldValue.isEmpty()) {
            showAlert(AlertType.ERROR, "Validation Error", "Please enter a value for " + fieldName);
            return false;
        }

       
        switch (fieldName) {
            case "First_name":
            case "Last_name":
               
                if (!Pattern.matches("[a-zA-Z]+", fieldValue)) {
                    showAlert(AlertType.ERROR, "Validation Error", fieldName + " should contain only letters.");
                    return false;
                }
                break;
            case "Tin_Number":
               
                if (!Pattern.matches("\\d{10}", fieldValue)) {
                    showAlert(AlertType.ERROR, "Validation Error", "TIN Number should be 10 digits long.");
                    return false;
                }
                break;
            case "Phone":
                
                if (!Pattern.matches("(07|09)\\d{8}", fieldValue)) {
                    showAlert(AlertType.ERROR, "Validation Error", "Phone number should start with 07 or 09 and be 10 digits long.");
                    return false;
                }
                break;
            case "kebele":
             
                if (!Pattern.matches("\\d{2}", fieldValue)) {
                    showAlert(AlertType.ERROR, "Validation Error", "Kebele should be a 2-digit number.");
                    return false;
                }
                break;
            case "Role":
              
                if (!fieldValue.equals("user") && !fieldValue.equals("tradeAdmin") && !fieldValue.equals("taxAdmin")) {
                    showAlert(AlertType.ERROR, "Validation Error", "Role should be either user, tradeAdmin, or taxAdmin.");
                    return false;
                }
                break;
            case "Level":
               
                fieldValue = fieldValue.toUpperCase();

               
                if (!fieldValue.equals("A") && !fieldValue.equals("B") && !fieldValue.equals("C")) {
                    showAlert(AlertType.ERROR, "Validation Error", "Level should be either A, B, or C.");
                    

                    return false;
                }
                break;
            case "Password":
                
                if (!Pattern.matches("\\d{6}", fieldValue)) {
                    showAlert(AlertType.ERROR, "Validation Error", "Password should be a 6-digit number.");
                    return false;
                }
                break;
            case "Tax_Amount":
               
                if (!fieldValue.equals("0")) {
                    showAlert(AlertType.ERROR, "Validation Error", "Tax Amount should be exactly 0.");
                    return false;
                }
                break;
        }
        return true;
    }

    private void showAlert(AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void returnToAdminSceneActionHandler(ActionEvent event) throws Exception {
        utility.changeToScene(getClass(), event, "adminScene.fxml");
    }

    @FXML
    private void registrationButtonActionHandler(ActionEvent event) throws Exception {
        if (validateInputs()) {
            try (Connection connection = databaseConnection.getConnection()) {
                // Check if TIN Number already exists
                String checkQuery = "SELECT COUNT(*) FROM userdatatable WHERE tin_number = ?";
                PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
                checkStatement.setString(1, Tin_Number.getText().trim());
                ResultSet resultSet = checkStatement.executeQuery();

                resultSet.next();
                if (resultSet.getInt(1) > 0) {
                    showAlert(AlertType.ERROR, "Registration Error", "TIN Number already exists. Existing tax payer.");
                } else {
                    // Proceed with registration if TIN Number does not exist
                    String query = "INSERT INTO userdatatable (first_name, last_name, tin_number, phone, kebele, level, role, password, sex, tax_amount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setString(1, First_name.getText().trim());
                    preparedStatement.setString(2, Last_name.getText().trim());
                    preparedStatement.setString(3, Tin_Number.getText().trim());
                    preparedStatement.setString(4, Phone.getText().trim());
                    preparedStatement.setString(5, kebele.getText().trim());
                   preparedStatement.setString(6, Level.getText().trim().toUpperCase());

                    preparedStatement.setString(7, Role.getText().trim());
                    preparedStatement.setString(8, Password.getText().trim());
                    preparedStatement.setString(9, ((RadioButton) sexToggleGroup.getSelectedToggle()).getText());
                    preparedStatement.setString(10, Tax_Amount.getText().trim());

                    preparedStatement.executeUpdate();

                    showAlert(AlertType.INFORMATION, "Registration Successful", "User registered successfully!");
                    clearFields();
                }

            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(AlertType.ERROR, "Database Error", "Error occurred while inserting data: " + e.getMessage());
            }
        }
    }

    private boolean validateInputs() {
        return validateTextField(First_name)
                && validateTextField(Last_name)
                && validateTextField(Tin_Number)
                && validateTextField(Phone)
                && validateTextField(kebele)
                && validateTextField(Level)
                && validateTextField(Role)
                && validateTextField(Password)
                && validateTextField(Tax_Amount);
    }

    private void clearFields() {
        First_name.clear();
        Last_name.clear();
        Tin_Number.clear();
        Phone.clear();
        kebele.clear();
        Level.clear();
        Role.clear();
        Password.clear();
        Tax_Amount.clear();
        sexToggleGroup.selectToggle(null);
    }
}
