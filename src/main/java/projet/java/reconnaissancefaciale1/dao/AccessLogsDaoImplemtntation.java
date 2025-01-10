package projet.java.reconnaissancefaciale1.dao;

import projet.java.reconnaissancefaciale1.dao.entities.AccessLogs;

import java.util.List;

public class AccessLogsDaoImplemtntation implements AccessLogsDao {

    @Override
    public boolean insert(AccessLogs accessLogs) {
        return false;
    }

    @Override
    public boolean update(AccessLogs accessLogs) {
        return false;
    }

    @Override
    public boolean delete(AccessLogs accessLogs) {
        return false;
    }

    @Override
    public boolean deleteById(Integer integer) {
        return false;
    }

    @Override
    public AccessLogs findById(Integer integer) {
        return null;
    }

    @Override
    public List<AccessLogs> findAll() {
        return List.of();
    }
}
