package com.musicapp.songplaylistmanager;

import com.musicapp.songplaylistmanager.console.ConsoleMenuManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class SongPlaylistManagerApplication implements CommandLineRunner {

	@Autowired
	private ApplicationContext applicationContext;

	public static void main(String[] args) {
		System.out.println("🎵 Starting Song & Playlist Management System (Console Version)...");
		SpringApplication.run(SongPlaylistManagerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println("\n" + "=".repeat(60));
		System.out.println("🎵 WELCOME TO SONG & PLAYLIST MANAGEMENT SYSTEM 🎵");
		System.out.println("=".repeat(60));

		// Get the console menu manager and start the application
		ConsoleMenuManager menuManager = applicationContext.getBean(ConsoleMenuManager.class);
		menuManager.startApplication();

		// Exit the application after menu closes
		System.out.println("\n👋 Thank you for using Song & Playlist Management System!");
		System.exit(0);
	}
}



