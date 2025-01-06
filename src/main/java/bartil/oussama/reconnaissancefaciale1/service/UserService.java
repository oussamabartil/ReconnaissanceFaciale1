package bartil.oussama.reconnaissancefaciale1.service;

import bartil.oussama.reconnaissancefaciale1.dao.entities.User;

import java.util.List;

public interface UserService {

    List<User> getAllUsers(); // Récupérer tous les utilisateurs
    User getUserById(int id); // Récupérer un utilisateur par son ID
    boolean addUser(User user);  // Ajouter un nouvel utilisateur
    void updateUser(User user); // Mettre à jour un utilisateur existant
    void deleteUser(User user);
}
