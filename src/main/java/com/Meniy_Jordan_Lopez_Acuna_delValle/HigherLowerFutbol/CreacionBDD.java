package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class CreacionBDD {

    public static void crearBaseDatos() {
        String url = "jdbc:mysql://172.16.1.64/"; //Cambiar ip de acuerdo al lugar
        String usuario = "bdd1";
        String password = "bdd1";

        try (Connection conexion = DriverManager.getConnection(url, usuario, password);
             Statement stmt = conexion.createStatement()) {

            String sql = "CREATE DATABASE IF NOT EXISTS futbolFantasy";
            stmt.executeUpdate(sql);
            System.out.println("Base de datos creada o ya existía.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
