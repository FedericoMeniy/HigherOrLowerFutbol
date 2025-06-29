package com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol;

import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.entity.Jugador;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.repository.FutbolistaRepository;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.repository.JugadorRepository;
import com.Meniy_Jordan_Lopez_Acuna_delValle.HigherLowerFutbol.service.FutbolistaDataSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner; // Asegúrate de importar CommandLineRunner
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableScheduling
@SpringBootApplication
public class HigherLowerFutbolApplication implements CommandLineRunner {

	@Autowired
	private FutbolistaDataSyncService futbolistaDataSyncService;

	@Autowired
	private JugadorRepository jugadorRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private FutbolistaRepository futbolistaRepository;

	public static void main(String[] args) {
		SpringApplication.run(HigherLowerFutbolApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		crearAdminSiNoExiste();


		if (futbolistaRepository.count() == 0) {

			System.out.println("La base de datos de futbolistas está vacía. Iniciando carga inicial desde la API...");

			int leagueId = 39;
			int season = 2021;

			try {
				int count = futbolistaDataSyncService.initialLoadPlayersAndStats(leagueId, season);
				System.out.println("Sincronización de datos completada. Total de futbolistas procesados: " + count);
			} catch (Exception e) {
				System.err.println("Error durante la sincronización inicial de futbolistas: " + e.getMessage());
				e.printStackTrace();
			}

		} else {

			System.out.println("La base de datos ya contiene futbolistas. Omitiendo carga inicial.");
		}
	}


	private void crearAdminSiNoExiste(){
		String adminUsername = "admin";
		if(!jugadorRepository.existsByUsername(adminUsername)){
			System.out.println("Creando usuario ADMIN.");
			Jugador admin = new Jugador();
			admin.setUsername(adminUsername);
			admin.setEmail("adminHL@gmail.com");
			admin.setPassword(passwordEncoder.encode("admin123"));
			admin.setTipoRol("ADMIN");
			admin.setPuntaje(9999);
			jugadorRepository.save(admin);
			System.out.println("Usuario ADMIN creado exitosamente.");
		}else{
			System.out.println("El usuario ADMIN ya existe.");
		}
	}
}