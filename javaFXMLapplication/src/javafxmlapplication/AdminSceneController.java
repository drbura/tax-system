package javafxmlapplication;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AdminSceneController implements Initializable {

    @FXML
    private Button registerClient;
    @FXML
    private Button logOutButton;
    @FXML
    private Button RquestBtn;
    @FXML
    private Button Tax_Amount;
    @FXML
    private Button ViewBtn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void registerActionHandler(ActionEvent event) throws Exception {
        utility.changeToScene(getClass(), event, "registration.fxml");
    }
     

    @FXML
    private void logOutButtonActionHandler(ActionEvent event) throws Exception {
        utility.changeToScene(getClass(), event, "FXMLDocument.fxml");
    }


    @FXML
    private void RquestBtnActionHandler(ActionEvent event) throws Exception{
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TableView.fxml"));
            Parent root = loader.load();
            
            TableViewController controller = loader.getController();
            controller.displayRequestedData();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void Tax_AmountActionHandler(ActionEvent event) throws Exception {
        utility.changeToScene(getClass(), event, "TaxAmountDialog.fxml");
    }


     @FXML
    private void ViewBtnActionHandler(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TableView.fxml"));
            Parent root = loader.load();
            
            TableViewController controller = loader.getController();
            controller.displayAllData();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}