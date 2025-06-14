package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class CreacionBDD {

    public static void crearBaseDatos() {
        String url = "jdbc:mysql://localhost:3306/HigherLowerFutbol"; //Cambiar ip de acuerdo al lugar
        String usuario = "root";
        String password = "root";

        try (Connection conexion = DriverManager.getConnection(url, usuario, password);
             Statement stmt = conexion.createStatement()) {

            String sql = "CREATE DATABASE IF NOT EXISTS HigherLowerFutbol";
            stmt.executeUpdate(sql);
            System.out.println("Base de datos creada o ya existía.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
