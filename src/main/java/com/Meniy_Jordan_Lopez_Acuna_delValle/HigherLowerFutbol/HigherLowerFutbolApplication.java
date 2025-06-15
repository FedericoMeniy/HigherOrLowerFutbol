package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service.FutbolistaDataSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
@SpringBootApplication
public class HigherLowerFutbolApplication {
	// Inyecta el servicio de sincronización. Spring se encarga de crear la instancia y sus dependencias.
	@Autowired
	private FutbolistaDataSyncService futbolistaDataSyncService;

	public static void main(String[] args) {
		// La llamada a CreacionBDD.crearBaseDatos() se ejecuta ANTES de que Spring inicie.
		// Si CreacionBDD.crearBaseDatos() solo crea el esquema de la DB (CREATE DATABASE), puedes dejarlo.
		// Si también crea tablas, índices, etc. (CREATE TABLE), es mejor dejar que Hibernate lo haga
		// con 'spring.jpa.hibernate.ddl-auto=update' y QUITAR esta línea para evitar duplicidades/conflictos.
		CreacionBDD.crearBaseDatos();

		SpringApplication.run(HigherLowerFutbolApplication.class, args);
		// ¡Elimina la línea FutbolistaDataSyncService.initialLoadPlayersAndStats(128, 2023); de aquí!
		// No debes llamar a métodos de servicio directamente así desde el main.
	}


	public void run(String... args) throws Exception {
		// Este método se ejecuta automáticamente cuando la aplicación Spring Boot ha arrancado completamente.
		// Aquí, 'futbolistaDataSyncService' ya está inyectado y listo para ser usado.

		// Define el ID de la liga y la temporada
		int leagueId = 128; // Liga Profesional Argentina (ejemplo)
		int season = 2023; // Año 2023 (ejemplo)

		try {
			System.out.println("Iniciando la sincronización de futbolistas...");
			int count = futbolistaDataSyncService.initialLoadPlayersAndStats(leagueId, season);
			System.out.println("Sincronización de datos completada. Total de futbolistas procesados: " + count);
		} catch (Exception e) {
			System.err.println("Error durante la sincronización inicial de futbolistas: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
