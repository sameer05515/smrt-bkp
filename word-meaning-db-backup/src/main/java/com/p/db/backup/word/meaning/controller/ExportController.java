package com.p.db.backup.word.meaning.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.p.db.backup.word.meaning.exception.InvalidInputSuppliedException;
import com.p.db.backup.word.meaning.response.ResponseHandler;
import com.p.db.backup.word.meaning.service.ReadService;

@RestController
public class ExportController {

	private static final Logger log = LoggerFactory.getLogger(ExportController.class);

	@Autowired
	ReadService readService;

	@GetMapping("/")
	public String healthCheck() {
		return "OK";
	}

	@GetMapping("/export")
	public ResponseEntity<Object> exportData() {

		ResponseEntity<Object> response = null;
		try {
			log.info("StartApplication...");

			String zipFileName = readService.startWM();

			response = ResponseHandler.generateResponse(HttpStatus.OK, false, "Success",
					"File Created :" + zipFileName);

		} catch (Exception e) {

			if (e instanceof InvalidInputSuppliedException) {

				response = ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, true, "Fail",
						"File Created : null");
			} else {

				response = ResponseHandler.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, true, "Fail",
						"File Created : null");
			}

			e.printStackTrace();

		}

		return response;

	}
}
