
package javafxmlapplication;

import javafx.scene.control.TableCell;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;

public class TableViewController implements Initializable {

    @FXML
    private TableView<user> myTableId;
    @FXML
    private TableColumn<user, String> tinNumberColumn;
    @FXML
    private TableColumn<user, String> firstNameColumn;
    @FXML
    private TableColumn<user, String> lastNameColumn;
    @FXML
    private TableColumn<user, String> levelColumn;
    @FXML
    private TableColumn<user, Double> taxAmountColumn;
    @FXML
    private TableColumn<user, String> phoneColumn;
    @FXML
    private TableColumn<user, String> statusColumn;

    private ObservableList<user> userData;
    private ObservableList<user> requestedData;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        tinNumberColumn.setCellValueFactory(new PropertyValueFactory<>("Tin_number"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("First_name"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("Last_name"));
        levelColumn.setCellValueFactory(new PropertyValueFactory<>("Level"));
        taxAmountColumn.setCellValueFactory(new PropertyValueFactory<>("Tax_Amount"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadDataFromDatabase();
    }


    private void loadDataFromDatabase() {
        userData = FXCollections.observableArrayList();
        String query = "SELECT Tin_number, First_name, Last_name, Level, Tax_Amount, Phone, status FROM userdatatable";

        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String tinNumber = resultSet.getString("Tin_number");
                String firstName = resultSet.getString("First_name");
                String lastName = resultSet.getString("Last_name");
                String level = resultSet.getString("Level");
                Double taxAmount = resultSet.getDouble("Tax_Amount");
                String phone = resultSet.getString("Phone");
                String status = resultSet.getString("status");

                user user = new user(tinNumber, firstName, lastName, level, taxAmount, phone, status, "");
                userData.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error occurred while fetching data: " + e.getMessage());
        }

        myTableId.setItems(userData);
    }


    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }


    public void displayRequestedData() {
        requestedData = FXCollections.observableArrayList();
        String query = "SELECT u.Tin_number, u.First_name, u.Last_name, u.Level, u.Tax_Amount, u.phone, u.status, r.IMG_url " +
                "FROM userdatatable u " +
                "JOIN requestable r ON u.Tin_number = r.tinNumber " +
                "WHERE u.status = 'requested'";

        try (Connection connection = databaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                String tinNumber = resultSet.getString("Tin_number");
                String firstName = resultSet.getString("First_name");
                String lastName = resultSet.getString("Last_name");
                String level = resultSet.getString("Level");
                Double taxAmount = resultSet.getDouble("Tax_Amount");
                String phone = resultSet.getString("phone");
                String status = resultSet.getString("status");
                String img = resultSet.getString("IMG_url");

                user user = new user(tinNumber, firstName, lastName, level, taxAmount, phone, status, img);
                requestedData.add(user);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error occurred while fetching requested data: " + e.getMessage());
        }

        myTableId.setItems(requestedData);

        
        TableColumn<user, Void> detailColumn = new TableColumn<>("Details");
        detailColumn.setCellFactory(new Callback<TableColumn<user, Void>, TableCell<user, Void>>() {
            @Override
            public TableCell<user, Void> call(final TableColumn<user, Void> param) {
                final TableCell<user, Void> cell = new TableCell<user, Void>() {
                    private final Button button = new Button("View Details");

                    {
                        button.setOnAction((event) -> {
                           
                            user selectedUser = getTableView().getItems().get(getIndex());
                          
                            showDetails(selectedUser);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(button);
                        }
                    }
                };
                return cell;
            }
        });
        myTableId.getColumns().add(detailColumn);
    }

    
    public void displayAllData() {
        myTableId.setItems(userData);
    }
    
    
    private void showDetails(user selectedUser) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("User Details");
        alert.setHeaderText("User Information");

        VBox content = new VBox();

        String userDetails = "TIN Number: " + selectedUser.getTin_number() + "\n" +
                             "First Name: " + selectedUser.getFirst_name() + "\n" +
                             "Last Name: " + selectedUser.getLast_name() + "\n" +
                             "Level: " + selectedUser.getLevel() + "\n" +
                             "Tax Amount: " + selectedUser.getTax_Amount() + "\n" +
                             "Phone: " + selectedUser.getPhone() + "\n" +
                             "Status: " + selectedUser.getStatus();

        ImageView imageView = new ImageView(new Image(selectedUser.getIMG_url()));
        imageView.setFitWidth(200);
        imageView.setFitHeight(200);

        content.getChildren().addAll(new javafx.scene.control.Label(userDetails), imageView);

        HBox buttonBox = new HBox();
        Button approveButton = new Button("Approve");
        Button rejectButton = new Button("Reject");

        approveButton.setOnAction(event -> {
            updateStatus(selectedUser, "approved");
            alert.close();
            removeUserFromRequestedData(selectedUser);
        });

        rejectButton.setOnAction(event -> {
            updateStatus(selectedUser, "rejected");
            alert.close();
            removeUserFromRequestedData(selectedUser);
        });

        buttonBox.getChildren().addAll(approveButton, rejectButton);
        content.getChildren().add(buttonBox);

        alert.getDialogPane().setContent(content);
        alert.showAndWait();
    }

    private void updateStatus(user selectedUser, String newStatus) {
        String updateQuery = "UPDATE userdatatable SET status = ? WHERE Tin_number = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            preparedStatement.setString(1, newStatus);
            preparedStatement.setString(2, selectedUser.getTin_number());
            preparedStatement.executeUpdate();

            selectedUser.setStatus(newStatus);
            showAlert(Alert.AlertType.INFORMATION, "Status Update", "Status updated to " + newStatus);

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error occurred while updating status: " + e.getMessage());
        }
    }

    private void removeUserFromRequestedData(user selectedUser) {
        requestedData.remove(selectedUser);
        myTableId.refresh();
    }
}


