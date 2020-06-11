package com.p.db.backup.word.meaning.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLConnection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
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
	public ResponseEntity<Object> exportData(HttpServletRequest request, HttpServletResponse response) {

		ResponseEntity<Object> responseEnitity = null;
		try {
			log.info("StartApplication...");

			String zipFileName = readService.startWM();
			

//			responseEnitity = ResponseHandler.generateResponse(HttpStatus.OK, false, "Success",
//					"File Created :" + zipFileName);
			
			File file=new File(zipFileName);
			///
			if (file.exists()) {

				//get the mimetype
				String mimeType = URLConnection.guessContentTypeFromName(file.getName());
				if (mimeType == null) {
					//unknown mimetype so set the mimetype to application/octet-stream
					mimeType = "application/octet-stream";
				}

				response.setContentType(mimeType);

				/**
				 * In a regular HTTP response, the Content-Disposition response header is a
				 * header indicating if the content is expected to be displayed inline in the
				 * browser, that is, as a Web page or as part of a Web page, or as an
				 * attachment, that is downloaded and saved locally.
				 * 
				 */

				/**
				 * Here we have mentioned it to show inline
				 */
				response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));

				 //Here we have mentioned it to show as attachment
				 //response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + file.getName() + "\""));

				response.setContentLength((int) file.length());

				InputStream inputStream = new BufferedInputStream(new FileInputStream(file));

				FileCopyUtils.copy(inputStream, response.getOutputStream());

			}
			///

		} catch (Exception e) {

			if (e instanceof InvalidInputSuppliedException) {

				responseEnitity = ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, true, "Fail",
						"File Created : null");
			} else {

				responseEnitity = ResponseHandler.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, true, "Fail",
						"File Created : null");
			}

			e.printStackTrace();

		}

		return responseEnitity;

	}
}
