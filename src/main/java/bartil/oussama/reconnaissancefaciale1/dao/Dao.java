package bartil.oussama.reconnaissancefaciale1.dao;

import java.util.List;

public interface Dao<T, U> {
 boolean insert(T t);
 boolean update(T t);
 boolean delete(T t);
 boolean deleteById(U u);
 T findById(U u);
 List<T> findAll();
}
