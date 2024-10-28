

package javafxmlapplication;

import javafx.fxml.FXML;
import java.sql.Connection;
import java.sql.PreparedStatement;

import java.sql.ResultSet;
import java.sql.DriverManager;

import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class TaxAmountDialogController {

    @FXML
    private TextField tinNumberField;

    @FXML
    private TextField dailyIncomeField;

    @FXML
    private void handleSubmit() {
        String tinNumber = tinNumberField.getText();
        String dailyIncomeStr = dailyIncomeField.getText();

        if (tinNumber.isEmpty() || dailyIncomeStr.isEmpty()) {
            showAlert("Error", "Please fill in all fields");
            return;
        }

        try {
            double dailyIncome = Double.parseDouble(dailyIncomeStr);
            if (isTINNumberInDatabase(tinNumber)) {
                String taxPayerClass = getTaxPayerClass(tinNumber);
                double annualTaxAmount = calculateAnnualTax(dailyIncome, taxPayerClass);
                updateTaxAmountInDatabase(tinNumber, annualTaxAmount);
                showAlert("Success", "Tax amount set successfully");
                
                closeDialog();
                        

            } else {
                showAlert("Error", "TIN number not found in database");
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid number for daily income");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancel() throws Exception {

        closeDialog();
        
    }

    private void closeDialog() throws Exception{
        Stage stage = (Stage) dailyIncomeField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean isTINNumberInDatabase(String tinNumber) throws Exception {
        String query = "SELECT COUNT(*) FROM userdatatable WHERE Tin_Number = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/taxsystemdatabase", "root", "");
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, tinNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private String getTaxPayerClass(String tinNumber) throws Exception {
        String query = "SELECT level FROM userdatatable WHERE Tin_Number = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/taxsystemdatabase", "root", "");
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, tinNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("level");
                } else {
                    throw new Exception("Tax payer  not found for TIN number: " + tinNumber);
                }
            }
        }
    }

    private double calculateAnnualTax(double dailyIncome, String taxPayerClass) throws Exception {
        double taxRate;
        switch (taxPayerClass) {
            case "A":
                taxRate = 0.15;
                break;
            case "B":
                taxRate = 0.10;
                break;
            case "C":
                taxRate = 0.05;
                break;
            default:
                throw new IllegalArgumentException("Unknown tax payer class: " + taxPayerClass);
        }
        return dailyIncome * taxRate * 365;
    }

    private void updateTaxAmountInDatabase(String tinNumber, double annualTaxAmount) throws Exception {
        String query = "UPDATE userdatatable SET Tax_Amount = ? WHERE Tin_Number = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/taxsystemdatabase", "root", "");
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, annualTaxAmount);
            stmt.setString(2, tinNumber);
            stmt.executeUpdate();
        }
    }
}


