package bartil.oussama.reconnaissancefaciale1.service;

import bartil.oussama.reconnaissancefaciale1.dao.UserDaoImplementation;
import bartil.oussama.reconnaissancefaciale1.dao.entities.User;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class UserServiceImplementation implements UserService {

    private final UserDaoImplementation userDao = new UserDaoImplementation();

    @Override
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    @Override
    public User getUserById(int id) {
        return userDao.findById(id);
    }

    @Override
    public boolean addUser(User user) {
        return userDao.insert(user);
    }

    @Override
    public boolean updateUser(User user) {
        return userDao.update(user); // Pass the user object to DAO
    }

    @Override
    public boolean deleteUser(int userId) {
        return userDao.delete(userId); // Auto-boxes int to Integer automatically
    }

    // Method to crop and save the image
    public String saveCroppedImage(File file, int width, int height) throws IOException {
        BufferedImage originalImage = ImageIO.read(file);

        // Crop image to fit the specified size (160x160)
        BufferedImage croppedImage = originalImage.getSubimage(0, 0, width, height);

        // Save the cropped image
        String outputDir = "images/users/"; // Folder path for saving images
        File directory = new File(outputDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File outputFile = new File(outputDir + System.currentTimeMillis() + ".png");
        ImageIO.write(croppedImage, "png", outputFile);

        return outputFile.getAbsolutePath(); // Return saved path
    }
}
