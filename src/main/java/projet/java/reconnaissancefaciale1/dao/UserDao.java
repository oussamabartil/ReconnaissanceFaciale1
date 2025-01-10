package projet.java.reconnaissancefaciale1.dao;

import projet.java.reconnaissancefaciale1.dao.entities.User;

public interface UserDao extends Dao<User,Integer> {

    boolean delete(Integer userId);
}
