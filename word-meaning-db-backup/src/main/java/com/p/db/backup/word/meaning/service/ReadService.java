package com.p.db.backup.word.meaning.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.p.db.backup.word.meaning.constants.GlobalContants;
import com.p.db.backup.word.meaning.exception.InvalidInputSuppliedException;
import com.p.db.backup.word.meaning.jpa.WordRepository;
import com.p.db.backup.word.meaning.pojo.Word;

@Service
public class ReadService {

	@Autowired
	JdbcTemplate jdbcTemplate;

	// @Autowired
	// CustomerRepository customerRepository;

	@Autowired
	WordRepository wordRepository;

	@Autowired
	WordService wordService;

	@Autowired
	WriteService writeService;

	@Autowired
	UtilityService utilityService;
	
	@Autowired
	GlobalContants globalContants;

	private boolean deleteDirectory(File dir) {
		if (dir.isDirectory()) {
			File[] children = dir.listFiles();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDirectory(children[i]);
				if (!success) {
					return false;
				}
			}
		}

		// either file or an empty directory
		System.out.println("removing file or directory : " + dir.getName());
		return dir.delete();
	}

	public String startWM() throws InvalidInputSuppliedException, IOException {

		long count = wordRepository.count();

		System.out.println("Total values in DB :" + count);

		int pageSize = 1200;

		int pages = (int) (count / pageSize);

		int pageNo = 0;

		System.out.println("pageSize : " + pageSize + " pages : " + pages);

		List<Word> authors = wordService.findPagedData(pageNo, pageSize,0);
		System.out.println(authors != null ? authors.size() : "null");
		// ObjectMapper objectMapper = new
		// ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

		String jsonDataDirectory = globalContants.getJSON_DATA_DIRECTORY();
		String outputDirectory = globalContants.getOUTPUT_DIRECTORY();

		///
		String pattern = "yyyy-MM-dd_HH_mm_ss";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		String date = simpleDateFormat.format(new Date());
		///
		String generatedFileName = GlobalContants.wordMeaningOutputZipFilePrefix + date + ".zip";

		////
		if (!(Files.exists(Paths.get(jsonDataDirectory)))) {
			Files.createDirectories(Paths.get(jsonDataDirectory));
		} else {
			// Files.deleteIfExists(Paths.get(jsonDataDirectory));
			deleteDirectory(new File(jsonDataDirectory));
			Files.createDirectories(Paths.get(jsonDataDirectory));
		}
		///

		while (authors != null && authors.size() > 0) {

			// try {

			String jsonStr = utilityService.toJsonString(authors, true);// objectMapper.writeValueAsString(authors);

			String filePath = jsonDataDirectory + "\\" + GlobalContants.wordMeaningJsonFilesPrefix + (pageNo + 1)
					+ ".json";
			System.out.print(filePath);

			writeService.printData(filePath, jsonStr);

			// }
			//
			// catch (IOException e) {
			// e.printStackTrace();
			// }

			authors = wordService.findPagedData(++pageNo, pageSize,0);
			System.out.println(authors != null ? authors.size() : "null");

		}

		System.out.println("All data fetched from DB Successfully!!");

		// try {
		writeService.zip(jsonDataDirectory, outputDirectory, generatedFileName);
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

		System.out
				.println("All data zipped in file (" + outputDirectory + "\\" + generatedFileName + ") Successfully!!");

//		Response rep=new Response();
//		rep.setStatus("Success");
//		rep.
//		JSONObject obj = new JSONObject();
//		obj.put("fileName", generatedFileName);
//		obj.put("status", "Success");
//		obj.put("message", "All data fetched from DB Successfully!!");

		return outputDirectory+generatedFileName;

	}
	
	public List<Word> findByNameNative(String name){
		
		return wordRepository.findByNameNative(name);
	}

}
