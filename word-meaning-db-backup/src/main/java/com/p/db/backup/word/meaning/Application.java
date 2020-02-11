package com.p.db.backup.word.meaning;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.p.db.backup.word.meaning.service.ReadService;

@SpringBootApplication(scanBasePackages = "com.p.db.backup.word.meaning")
@EnableJpaRepositories
public class Application extends SpringBootServletInitializer implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	@Autowired
	ReadService readService;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder app) {
		return app.sources(Application.class);
	}

	@Override
	public void run(String... args) {

		log.info("Application Started...");

		// try {
		// log.info("StartApplication...");
		//
		// readService.startWM();
		// } catch (InvalidInputSuppliedException e) {
		//
		// e.printStackTrace();
		// } catch (IOException e) {
		//
		// e.printStackTrace();
		// }

	}

}
