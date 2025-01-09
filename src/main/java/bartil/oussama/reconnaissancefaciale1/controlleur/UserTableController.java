package bartil.oussama.reconnaissancefaciale1.controlleur;

import bartil.oussama.reconnaissancefaciale1.dao.entities.User;
import bartil.oussama.reconnaissancefaciale1.service.UserService;
import bartil.oussama.reconnaissancefaciale1.service.UserServiceImplementation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class UserTableController implements Initializable {

    @FXML
    private TableColumn<User, Boolean> accessStatusUser;

    @FXML
    private TableColumn<User, Date> dateCreationUser;

    @FXML
    private Label dateToday;

    @FXML
    private TableColumn<User, String> emailUser;

    @FXML
    private TableColumn<User, String> nameUser;

    @FXML
    private Label nbrTotalUser;

    @FXML
    private TableColumn<User, Integer> numUser;

    @FXML
    private TableColumn<User, String> imageUser;

    @FXML
    private TableView<User> tableUsers;

    private UserTableController parentController;

    @FXML
    private TableColumn<User, Void> modifyUser;

    @FXML
    private TableColumn<User, Void> deleteUser;

    public void setParentController(UserTableController parentController) {
        this.parentController = parentController;
    }
    @FXML
    void addUser(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/bartil/oussama/reconnaissancefaciale1/addUsers.fxml"));
            Parent root = fxmlLoader.load();

            // Pass a reference of the UserTableController to the UserController
            UserController userController = fxmlLoader.getController();
            userController.setParentController(this); // Pass reference

            Stage stage = new Stage();
            stage.setTitle("Ajouter un Utilisateur");
            stage.setScene(new Scene(root, 800, 700)); // Set size
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private UserService userService;
    private final ObservableList<User> userList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userService = new UserServiceImplementation();
        numUser.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameUser.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailUser.setCellValueFactory(new PropertyValueFactory<>("email"));
        dateCreationUser.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        accessStatusUser.setCellValueFactory(new PropertyValueFactory<>("accessStatus"));

        accessStatusUser.setCellFactory(column -> new TableCell<User, Boolean>() {
            private final Label iconLabel = new Label();

            @Override
            protected void updateItem(Boolean accessStatus, boolean empty) {
                super.updateItem(accessStatus, empty);

                if (empty || accessStatus == null) {
                    setGraphic(null);
                } else {
                    // Apply FontAwesome icons dynamically based on access status
                    if (accessStatus) {
                        iconLabel.setText("\uf00c"); // Unicode for FontAwesome 'check' icon
                        iconLabel.setStyle("-fx-font-family: 'FontAwesome'; -fx-text-fill: green; -fx-font-size: 16px;");
                    } else {
                        iconLabel.setText("\uf00d"); // Unicode for FontAwesome 'times' icon
                        iconLabel.setStyle("-fx-font-family: 'FontAwesome'; -fx-text-fill: red; -fx-font-size: 16px;");
                    }
                    setGraphic(iconLabel);
                }
            }
        });


        // Set up image column to show user image
        imageUser.setCellValueFactory(new PropertyValueFactory<>("imagePath"));
        imageUser.setCellFactory(column -> new TableCell<User, String>() {
            private final ImageView imageView = new ImageView();

            {
                imageView.setFitHeight(50);
                imageView.setFitWidth(50);
            }

            @Override
            protected void updateItem(String imagePath, boolean empty) {
                super.updateItem(imagePath, empty);

                if (empty || imagePath == null || imagePath.isEmpty()) {
                    setGraphic(null);
                } else {
                    // Display the first image from comma-separated paths
                    String[] paths = imagePath.split(",");
                    File file = new File(paths[0]);
                    if (file.exists()) {
                        Image image = new Image(file.toURI().toString());
                        imageView.setImage(image);
                        setGraphic(imageView);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        // Load data into the table
        loadUsers();

        // Set today's date
        dateToday.setText(LocalDate.now().toString());

        modifyUser.setCellFactory(column -> new TableCell<User, Void>() {
            private final Button modifyButton = new Button("Modifier");

            {
                modifyButton.setOnAction(event -> {
                    User selectedUser = getTableView().getItems().get(getIndex());
                    if (selectedUser != null) {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bartil/oussama/reconnaissancefaciale1/updateUser.fxml"));
                            Parent root = loader.load();

                            UpdateUserController controller = loader.getController();
                            controller.setParentController(UserTableController.this); // Pass current UserTableController
                            controller.setSelectedUser(selectedUser); // Pass selected user

                            Stage stage = new Stage();
                            stage.setTitle("Modifier Utilisateur");
                            stage.setScene(new Scene(root, 800, 700)); // Set size
                            stage.show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(modifyButton);
                }
            }
        });



        deleteUser.setCellFactory(TableButtonFactory.createDeleteButton());
    }

    private void loadUsers() {
        // Fetch users from the service
        List<User> users = userService.getAllUsers();
        userList.setAll(users);

        // Set data to the table
        tableUsers.setItems(userList);

        // Update total user count
        nbrTotalUser.setText(String.valueOf(users.size()));
    }
    @FXML
    void refreshTable(ActionEvent event) {
        // Reload user data
        loadUsers();
    }

    public void refreshTable() {
        loadUsers();
    }
}
