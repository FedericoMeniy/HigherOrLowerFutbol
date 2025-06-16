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

		CreacionBDD.crearBaseDatos();

		SpringApplication.run(HigherLowerFutbolApplication.class, args);

	}


	public void run(String... args) throws Exception {

		int leagueId = 128; // Liga Profesional Argentina
		int season = 2023;

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
