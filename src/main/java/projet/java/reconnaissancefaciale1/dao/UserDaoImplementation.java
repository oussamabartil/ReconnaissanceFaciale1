package projet.java.reconnaissancefaciale1.dao;

import projet.java.reconnaissancefaciale1.dao.entities.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImplementation implements UserDao {

    @Override
    public boolean insert(User user) {
        Connection connection= DBSingleton.getConnection();
        String query = "INSERT INTO Users (name, email, image_path, access_status,password) VALUES (?, ?, ?, ?,?)";
        try {
            PreparedStatement stmt=connection.prepareStatement(query);
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getImagePath());
            stmt.setBoolean(4, user.isAccessStatus());
            stmt.setString(5, user.getPassword());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(User user) {
        Connection connection = DBSingleton.getConnection();
        String query = "UPDATE Users SET name = ?, email = ?, image_path = ?, access_status = ? , password = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getImagePath());
            stmt.setBoolean(4, user.isAccessStatus());
            stmt.setString(5, user.getPassword());
            stmt.setInt(6, user.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public boolean delete(User user) {
        return false;
    }

    @Override
    public boolean deleteById(Integer integer) {
        return false;
    }

    @Override
    public boolean delete(Integer userId) {  // Match Integer, not int
        Connection connection = DBSingleton.getConnection();
        String query = "DELETE FROM Users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId); // Use userId directly
            return stmt.executeUpdate() > 0; // Return true if rows were deleted
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if an exception occurs
        }
    }


    @Override
    public User findById(Integer userId) {
        Connection connection=DBSingleton.getConnection();
        String query = "SELECT * FROM Users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getDate("created_at"),
                        rs.getBoolean("access_status"),
                        rs.getString("image_path"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("name")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public List<User> findAll() {
        Connection connection=DBSingleton.getConnection();
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                System.out.println(rs.getDate("created_at"));

                users.add(new User(
                        rs.getInt("id"),
                        rs.getDate("created_at"),
                        rs.getBoolean("access_status"),
                        rs.getString("image_path"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}
