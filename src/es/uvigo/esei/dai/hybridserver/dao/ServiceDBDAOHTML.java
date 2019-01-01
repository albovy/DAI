package es.uvigo.esei.dai.hybridserver.dao;


import es.uvigo.esei.dai.hybridserver.Configuration;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;


public class ServiceDBDAOHTML implements ServiceDAO {

    private Configuration conf;


    public ServiceDBDAOHTML(Configuration conf){
        this.conf = conf;
    }


    @Override
    public void createPage(String uuid,String content) {
        try (Connection connection = DriverManager.getConnection(this.conf.getDbURL(),this.conf.getDbUser(),this.conf.getDbPassword())){
            String query = "INSERT INTO HTML(uuid,content) " + "VALUES(?,?)";
            try(PreparedStatement statement = connection.prepareStatement(query)){
                statement.setString(1,uuid);
                statement.setString(2,content);
                int correcto = statement.executeUpdate();
                if(correcto == 0){
                    throw new SQLException("Error al insertar");
                }
            }


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public String getPage(String uuid) {
        String content = null;
        try(Connection connection =  DriverManager.getConnection(this.conf.getDbURL(),this.conf.getDbUser(),this.conf.getDbPassword())){
            String query = "SELECT * FROM HTML WHERE uuid = ?";
            try(PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1,uuid);
                try (ResultSet resultSet = statement.executeQuery()){
                    while(resultSet.next()){
                        content = resultSet.getString(2);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return content;
    }

    @Override
    public void deletePage(String uuid) {
        try(Connection connection = DriverManager.getConnection(this.conf.getDbURL(),this.conf.getDbUser(),this.conf.getDbPassword())){
            String query = "DELETE FROM HTML WHERE uuid = ?";
            try(PreparedStatement statement = connection.prepareStatement(query)){
                statement.setString(1,uuid);
                int correcto = statement.executeUpdate();
                if(correcto == 0){
                    throw new SQLException("Error en el borrado");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Set<String> listPages() {
        Set<String> pages = new HashSet<>();
        try(Connection connection = DriverManager.getConnection(this.conf.getDbURL(),this.conf.getDbUser(),this.conf.getDbPassword())){

            String query = "SELECT * FROM HTML";
            try(Statement statement = connection.createStatement()){
                try(ResultSet resultSet = statement.executeQuery(query)){
                    while(resultSet.next()){
                        pages.add(resultSet.getString(1));

                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return pages;

    }

}
