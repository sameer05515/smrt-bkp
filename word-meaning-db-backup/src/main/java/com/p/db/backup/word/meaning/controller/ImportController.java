package com.p.db.backup.word.meaning.controller;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.p.db.backup.word.meaning.constants.GlobalContants;
import com.p.db.backup.word.meaning.exception.InvalidInputSuppliedException;
import com.p.db.backup.word.meaning.jpa.WordRepository;
import com.p.db.backup.word.meaning.pojo.Plan;
import com.p.db.backup.word.meaning.pojo.Word;
import com.p.db.backup.word.meaning.response.ImportResponse;
import com.p.db.backup.word.meaning.response.ImportResponse.ImportExtractedFilesResponse;
import com.p.db.backup.word.meaning.response.ResponseHandler;
import com.p.db.backup.word.meaning.service.ReadService;
import com.p.db.backup.word.meaning.service.WriteService;

@RestController
public class ImportController {

	private static final Logger log = LoggerFactory.getLogger(ImportController.class);

	@Autowired
	ReadService readService;

	@Autowired
	WriteService writeService;

	@Autowired
	WordRepository wordRepository;
	
	@Autowired
	GlobalContants globalContants;

	@GetMapping("/import")
	public ResponseEntity<Object> importData(@RequestParam("zipfilepath") String zipFilePath,
			@RequestParam(value = "batchSize", required = false) Integer batchSize) {

		ImportResponse ir = new ImportResponse();
		ResponseEntity<Object> response = null;
		boolean exceptionOccuredWhileDataProcessing = false;
		List<ImportExtractedFilesResponse> details = new ArrayList<ImportResponse.ImportExtractedFilesResponse>();

		ir.setZipFileToExtract(zipFilePath);

		if (batchSize == null || batchSize.intValue() < GlobalContants.MIN_BATCH_INSERT_SIZE
				|| batchSize.intValue() > GlobalContants.MAX_BATCH_INSERT_SIZE) {
			batchSize = GlobalContants.DEFAULT_BATCH_INSERT_SIZE;
			System.out.println("Allowed batch size between " + GlobalContants.MIN_BATCH_INSERT_SIZE + " to "
					+ GlobalContants.MAX_BATCH_INSERT_SIZE + ". Using default batch size :- "
					+ GlobalContants.DEFAULT_BATCH_INSERT_SIZE);
		} else {
			System.out.println("batchSize == " + batchSize);
		}
		ir.setBatchSize(batchSize);
		ir.setInsertIntoTargetDBEnabled(GlobalContants.insertIntoTargetDBEnabled);

		System.out.println("GlobalContants.insertIntoTargetDBEnabled == " + GlobalContants.insertIntoTargetDBEnabled);

		try {
			// StringBuffer sb = new StringBuffer();
			// sb.append("Will import data from given location. ");
			// ----- //
			// sb.append("\n1. Unzip, ");
			// ----- //

			File[] targetFiles = writeService.unzip(zipFilePath, globalContants.getZIP_DIRECTORY());
			log.debug("Zip file extracted successfully!!");

			//////////////////////////////////////////////////////////////
			System.out.println("targetFiles.length == " + (targetFiles != null ? targetFiles.length : "0"));

			ObjectMapper objectMapper = new ObjectMapper();

			Instant start = Instant.now();
			if (targetFiles != null) {

				// Collections.sort(targetFiles, new Comparator<T>() {
				// });

				// ----- //
				// sb.append("\n1.1 Sort file names based on index prefixes of files, ");
				// ----- //
				targetFiles = sortFileNames(targetFiles, GlobalContants.wordMeaningJsonFilesPrefix,
						GlobalContants.jsonExtension);

				for (File f : targetFiles) {

					ImportExtractedFilesResponse importExtractedFilesResponse = new ImportExtractedFilesResponse();
					importExtractedFilesResponse.setFileName(f.getName());
					try {
						List<Word> wordList = objectMapper.readValue(f, new TypeReference<List<Word>>() {
						});

						importExtractedFilesResponse.setRecord((wordList != null ? wordList.size() : 0));

						System.out.println(f.getName() + " is being processed. "
								+ (wordList != null ? wordList.size() : 0) + " rows are being inserted.");

						Instant start1 = Instant.now();
						if (GlobalContants.insertIntoTargetDBEnabled) {
							batchInsert(wordList, batchSize);
						}

						// for (Word w : wordList) {
						// wordRepository.insertWord(w.getId(), w.getUnique_name(), w.getWord(),
						// w.getType(),
						// w.getDetails(), w.getCreated_on(), w.getUpdated_on(), w.getLast_read());
						// }

						Instant finish1 = Instant.now();
						long timeElapsed = Duration.between(start1, finish1).toMillis();

						importExtractedFilesResponse.setDuration(timeElapsed);
						importExtractedFilesResponse.setStatus("Sucess");
						importExtractedFilesResponse.setMessage(
								"Total time elapsed for writing data into db:- " + (timeElapsed) + " milli-seconds");
						System.out.println(
								"Total time elapsed for writing data into db:- " + (timeElapsed) + " milli-seconds");
					} catch (Exception e) {

						importExtractedFilesResponse.setStatus("Exception");
						importExtractedFilesResponse.setMessage("Exception occured : " + e.getMessage());

						exceptionOccuredWhileDataProcessing = true;

						e.printStackTrace();
					}

					// wordRepository.saveAll(wordList);

					details.add(importExtractedFilesResponse);

				}
			}
			Instant finish = Instant.now();
			long timeElapsed = Duration.between(start, finish).toMillis();

			long count = wordRepository.count();

			ir.setRecord(count);
			ir.setDuration(timeElapsed);
			String message = "Total time elapsed for writing all (" + count + ") data into db:- " + (timeElapsed)
					+ " milli-seconds";
			ir.setMessage(message);

			System.out.println(message);

			//////////////////////////////////////////////////////////////

			// sb.append("\n2. Read json, ");
			// writeService.pro
			// sb.append("\n3. validate for duplicacy/conflicts, ");
			// sb.append("\n3.1 Validation for :- duplicate id , duplicate unique_string,
			// duplicate word");
			// sb.append("\n4. write in target DB ");

			List<Plan> listPlan = getPlan();
			ir.setImportPlan(listPlan);
			ir.setDetails(details);
			if (exceptionOccuredWhileDataProcessing) {
				ir.setStatus("Fail");
			} else {
				ir.setStatus("Success");
			}

			// System.out.println("================\n" + sb.toString() +
			// "\n================\n");

			// log.debug("From logger " + sb.toString());
		} catch (Exception e) {
			ir.setStatus("Exception");
			if (e instanceof InvalidInputSuppliedException) {
				// obj.put("message", "Exception : " + ((InvalidInputSuppliedException)
				// e).getCustomMessage());
				ir.setMessage(((InvalidInputSuppliedException) e).getCustomMessage());
				response = ResponseHandler.generateResponse(HttpStatus.BAD_REQUEST, true, "Fail", ir);
			} else {
				// obj.put("message", "Exception : " + e.getMessage());
				ir.setMessage((e.getMessage()));
				response = ResponseHandler.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, true, "Fail", ir);
			}

			e.printStackTrace();
			return response;
		}

		// return "success";
		if (exceptionOccuredWhileDataProcessing) {
			response = ResponseHandler.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, true, "Fail", ir);
		} else {
			response = ResponseHandler.generateResponse(HttpStatus.OK, false, "Success", ir);
		}

		return response;

	}

	private List<Plan> getPlan() throws InvalidInputSuppliedException {

		List<Plan> planList = new ArrayList<Plan>();
		planList.add(new Plan("Will import data from given location."));
		planList.get(0).addChild(new Plan("0. Request parameters validations")
				.addChild(new Plan(
						"0.1 Failed to convert value of type 'java.lang.String' to required type 'java.lang.Integer'; "
								+ "nested exception is java.lang.NumberFormatException: For input string: '1200kk'"))
				.addChild(new Plan("0.2 Invalid file extension[] . Only zip file is required!"))
				.addChild(new Plan("0.3 Invalid zip file location")))
				.addChild(new Plan("1. Unzip,")
						.addChild(new Plan("1.1 Check if given zip file exists,").addChild(new Plan(
								"1.1.1 java.io.FileNotFoundException: <xxx>.zip (The system cannot find the file specified)")))
						.addChild(new Plan("1.2 Extract zip file,"))
						.addChild(new Plan("1.3 Sort file names based on index prefixes of files,")));
		planList.get(0).addChild(new Plan("2. Read json,"));
		planList.get(0).addChild(new Plan("3. validate for duplicacy/conflicts,")
				.addChild(new Plan("3.1 Validation for :- duplicate id ").addChild(
						new Plan("3.1.1 java.sql.BatchUpdateException: Duplicate entry 'xxx {id}' for key 'PRIMARY'")))
				.addChild(new Plan("3.2 Validation for :- duplicate unique_string"))
				.addChild(new Plan("3.3 Validation for :- duplicate word")));
		planList.get(0).addChild(new Plan("4. write in target DB"));

		return planList;

	}

	private File[] sortFileNames(File[] targetFiles, String jsonFileNamesPrefix, String fileExtension) {

		File[] temp = targetFiles;

		Arrays.sort(temp, new Comparator<File>() {

			@Override
			public int compare(File o1, File o2) {
				String n1 = o1.getName().substring(GlobalContants.wordMeaningJsonFilesPrefix.length());
				String n2 = o2.getName().substring(GlobalContants.wordMeaningJsonFilesPrefix.length());
				n1 = n1.substring(0, n1.length() - (GlobalContants.jsonExtension.length()));
				n2 = n2.substring(0, n2.length() - (GlobalContants.jsonExtension.length()));

				return ((new Integer(n1)).compareTo(new Integer(n2)));

			}
		});

		for (File f : temp) {
			System.out.print(f.getName() + " - ");
		}
		System.out.println("");

		return temp;
	}

	@Autowired
	JdbcTemplate jdbcTemplate;

	private int[][] batchInsert(List<Word> words, int batchSize) {

		int[][] updateCounts = jdbcTemplate.batchUpdate(
				"INSERT INTO t_word(id, unique_name, word, type, details, created_on, updated_on, last_read)"
						+ " values(?,?,?,?,?,?,?,?)",
				words, batchSize, new ParameterizedPreparedStatementSetter<Word>() {
					public void setValues(PreparedStatement ps, Word argument) throws SQLException {
						ps.setInt(1, argument.getId());
						ps.setString(2, argument.getUnique_name());
						ps.setString(3, argument.getWord());
						ps.setString(4, argument.getType());
						ps.setString(5, argument.getDetails());
						ps.setTimestamp(6, new Timestamp(argument.getCreated_on().getTime()));
						ps.setTimestamp(7, new Timestamp(argument.getUpdated_on().getTime()));
						ps.setTimestamp(8, new Timestamp(argument.getLast_read().getTime()));

					}
				});
		return updateCounts;

	}

}
