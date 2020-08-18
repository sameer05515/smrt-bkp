package com.p.db.backup.word.meaning.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//import com.p.db.backup.word.meaning.constants.GlobalContants;
import com.p.db.backup.word.meaning.exception.InvalidInputSuppliedException;
import com.p.db.backup.word.meaning.jpa.JDBCTemplateRepository;
import com.p.db.backup.word.meaning.pojo.Word;
import com.p.db.backup.word.meaning.response.ResponseHandler;
import com.p.db.backup.word.meaning.service.CurdService;
import com.p.db.backup.word.meaning.service.ReadService;
import com.p.db.backup.word.meaning.service.WordService;

@RestController
public class ReportController {

	private static final Logger log = LoggerFactory.getLogger(ReportController.class);

	@Autowired
	ReadService readService;

	@Autowired
	CurdService curdService;

	@Autowired
	WordService wordService;

	@Autowired
	JDBCTemplateRepository jdbcTemplateRepository;

	@GetMapping("/words")
	public ResponseEntity<Object> findByName(@RequestParam("name") String name) {
		ResponseEntity<Object> response = null;
		try {
			log.info("Inside com.p.db.backup.word.meaning.controller.ReportController.findByName(String) method ...");

			List<Word> listWords = readService.findByNameNative(name);

			response = ResponseHandler.generateResponse(HttpStatus.OK, false, "Success", listWords);

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

	@GetMapping("/findPagedData")
	public ResponseEntity<Object> findPagedData(@RequestParam("pageSize") int pageSize,
			@RequestParam("pageNo") int pageNo, @RequestParam(value = "daysBack", required = false) Integer daysBack) {
		ResponseEntity<Object> response = null;
		try {
			log.info("Inside com.p.db.backup.word.meaning.controller.ReportController.findByName(String) method ...");

			if (daysBack == null || daysBack.intValue() < 0) {
				daysBack = 0;
				System.out.println("Allowed daysBack should be greater than 0 ");
			} else {
				System.out.println("daysBack == " + daysBack);
			}

			List<Word> listWords = wordService.findPagedData(pageNo, pageSize, daysBack);

			response = ResponseHandler.generateResponse(HttpStatus.OK, false, "Success", listWords);

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

	@PostMapping("/words")
	public ResponseEntity<Object> save(@RequestBody Word word) {
		ResponseEntity<Object> response = null;
		try {
			log.info("Inside com.p.db.backup.word.meaning.controller.ReportController.save(Word) method ...");

			System.out.println("Word : " + word);

			Word retWord = curdService.saveWord(word);

			response = ResponseHandler.generateResponse(HttpStatus.OK, false, "Success", retWord);

		} catch (Exception e) {
			if (e instanceof InvalidInputSuppliedException) {
				response = ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, true,
						((InvalidInputSuppliedException) e).getCustomMessage(), "File Created : null");
			} else {
				response = ResponseHandler.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, true, e.getMessage(),
						"File Created : null");
			}
			e.printStackTrace();
		}
		return response;
	}

	@PutMapping("/words")
	public ResponseEntity<Object> update(@RequestBody Word word) {
		ResponseEntity<Object> response = null;
		try {
			log.info("Inside com.p.db.backup.word.meaning.controller.ReportController.update(Word) method ...");

			System.out.println("Word : " + word);

			Word retWord = curdService.updateWord(word);

			response = ResponseHandler.generateResponse(HttpStatus.OK, false, "Success", retWord);

		} catch (Exception e) {
			if (e instanceof InvalidInputSuppliedException) {
				response = ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, true,
						((InvalidInputSuppliedException) e).getCustomMessage(), "File Created : null");
			} else {
				response = ResponseHandler.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, true, e.getMessage(),
						"File Created : null");
			}
			e.printStackTrace();
		}
		return response;
	}

	@GetMapping("/reports/datewise/LastUpdated")
	public ResponseEntity<Object> reportByDatewiseOnLastupdate(
			@RequestParam(value = "columnName", required = false, defaultValue = "LastUpdated") String columnName) {
		ResponseEntity<Object> response = null;
		try {
			log.info(
					"Inside com.p.db.backup.word.meaning.controller.ReportController.reportByDatewiseOnLastupdate() method ...");

			List<String> validColumns = Arrays.asList("LASTUPDATED", "CREATEDON");
			if (columnName != null && !columnName.trim().equalsIgnoreCase("")
					&& validColumns.contains(columnName.toUpperCase())) {
				List<Map<String, Object>> retWord = jdbcTemplateRepository
						.getDatewiseUpdateData(columnName.toUpperCase());

				response = ResponseHandler.generateResponse(HttpStatus.OK, false, "Success", retWord);
			} else {
				throw new InvalidInputSuppliedException("Invalid columnName = " + "\"" + columnName + " \"");
			}

		} catch (Exception e) {
			if (e instanceof InvalidInputSuppliedException) {
				response = ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, true,
						((InvalidInputSuppliedException) e).getCustomMessage(), "{}");
			} else {
				response = ResponseHandler.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, true, e.getMessage(),
						"File Created : null");
			}
			e.printStackTrace();
		}
		return response;
	}

}
