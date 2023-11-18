package carrentalmanagementsystem;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.ResourceBundle;

public class SettingController implements Initializable {

    @FXML
    private Text currentUsernameText;

    @FXML
    private Text currentPasswordText;

    @FXML
    private TextArea newPasswordTextArea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Get the admin data from the database
        Admin admin = getAdminData();

        // Display admin data in the UI
        if (admin != null) {
            currentUsernameText.setText(admin.getUsername());
            currentPasswordText.setText(admin.getPassword());
        }
    }

    @FXML
    private void handleChangePassword() {
        // Ask the user if they want to change the password
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Change Password");
        confirmationAlert.setHeaderText("Do you want to change the password?");
        confirmationAlert.setContentText("Enter the new password below:");

        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            String newPassword = newPasswordTextArea.getText().trim();

            // Validate and update the password in the database
            if (isValidPassword(newPassword)) {
                updatePassword(newPassword);

                // Update the UI with the new password
                updateUI();

                // Show a success message
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Success");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Password updated successfully.");
                successAlert.showAndWait();

                // Clear the new password textarea
                newPasswordTextArea.clear();
            } else {
                // Show an error message for an invalid password
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Invalid password. Please enter a valid password.");
                errorAlert.showAndWait();
            }
        }
    }

    // Method to validate the new password (customize as needed)
    private boolean isValidPassword(String newPassword) {
        // Add your validation logic here
        return newPassword.length() >= 8; // For example, require a minimum of 8 characters
    }

    // Method to update the password in the database
    private void updatePassword(String newPassword) {
        String updateSql = "UPDATE admin SET password = ? WHERE username = 'admin'";

        try {
            Connection connect = database.connectDb();
            PreparedStatement updateStatement = connect.prepareStatement(updateSql);
            updateStatement.setString(1, newPassword);
            updateStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to retrieve admin data from the database
    private Admin getAdminData() {
        String sql = "SELECT * FROM admin WHERE username = 'admin'";

        try {
            Connection connect = database.connectDb();
            PreparedStatement prepare = connect.prepareStatement(sql);
            ResultSet result = prepare.executeQuery();

            if (result.next()) {
                int id = result.getInt("id");
                String adminUsername = result.getString("username");
                String adminPassword = result.getString("password");

                return new Admin(id, adminUsername, adminPassword);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Method to update the UI with the latest admin data
    private void updateUI() {
        Admin admin = getAdminData();

        if (admin != null) {
            currentPasswordText.setText(admin.getPassword()+ " (new password)");
        }
    }
}
