package bartil.oussama.reconnaissancefaciale1.controlleur;

import bartil.oussama.reconnaissancefaciale1.dao.entities.User;
import bartil.oussama.reconnaissancefaciale1.service.UserService;
import bartil.oussama.reconnaissancefaciale1.service.UserServiceImplementation;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;

public class TableButtonFactory {

    // Create Modify Button
    public static <T> Callback<TableColumn<T, Void>, TableCell<T, Void>> createModifyButton() {
        return new Callback<>() {
            @Override
            public TableCell<T, Void> call(final TableColumn<T, Void> param) {
                return new TableCell<>() {
                    private final Button modifyButton = new Button("Modifier");

                    {
                        modifyButton.setOnAction(event -> {
                            // Obtenir l'utilisateur sélectionné dans la ligne
                            User selectedUser = (User) getTableView().getItems().get(getIndex());

                            try {
                                // Charger la page de modification
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/bartil/oussama/reconnaissancefaciale1/updateUser.fxml"));
                                Parent root = loader.load();

                                // Récupérer le contrôleur de la page de modification
                                UpdateUserController controller = loader.getController();

                                // Passer l'utilisateur sélectionné au contrôleur
                                controller.setSelectedUser(selectedUser);

                                // Créer une nouvelle fenêtre pour la modification
                                Stage stage = new Stage();
                                stage.setTitle("Modifier Utilisateur");
                                stage.setScene(new Scene(root));
                                stage.show();

                            } catch (IOException e) {
                                e.printStackTrace();
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
                };
            }
        };
    }

    // Create Delete Button
    public static <T> Callback<TableColumn<T, Void>, TableCell<T, Void>> createDeleteButton() {
        return new Callback<>() {
            @Override
            public TableCell<T, Void> call(final TableColumn<T, Void> param) {
                return new TableCell<>() {
                    private final Button deleteButton = new Button("Supprimer");

                    {
                        deleteButton.setOnAction(event -> {
                            T user = getTableView().getItems().get(getIndex());

                            // Confirm deletion with the user
                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Confirmation");
                            alert.setHeaderText("Supprimer Utilisateur");
                            alert.setContentText("Voulez-vous vraiment supprimer cet utilisateur ?");

                            alert.showAndWait().ifPresent(response -> {
                                if (response == ButtonType.OK) {
                                    // Proceed with deletion
                                    if (user instanceof User) {
                                        UserService userService = new UserServiceImplementation();
                                        User selectedUser = (User) getTableView().getItems().get(getIndex()); // Cast the object to User
                                        boolean success = userService.deleteUser(selectedUser.getId()); // Use the User object

                                        if (success) {
                                            // Remove user from the table
                                            getTableView().getItems().remove(user);
                                            System.out.println("Utilisateur supprimé avec succès !");
                                        } else {
                                            System.out.println("Erreur lors de la suppression de l'utilisateur !");
                                        }
                                    }
                                }
                            });
                        });

                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(deleteButton);
                        }
                    }
                };
            }
        };
    }
}
