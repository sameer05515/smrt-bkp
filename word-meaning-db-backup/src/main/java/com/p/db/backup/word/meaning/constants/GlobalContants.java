package com.p.db.backup.word.meaning.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GlobalContants {

	@Value("${wm.global.constants.jsonDataDirectory}")
	private String JSON_DATA_DIRECTORY;
	
	@Value("${wm.global.constants.outputDirectory}")
	private String OUTPUT_DIRECTORY;
	
	@Value("${wm.global.constants.zipDirectory}")
	private String ZIP_DIRECTORY;
	
	@Value("${wm.global.constants.maxPageSize}")
	private int MAX_PAGE_SIZE;

	public static final int MIN_BATCH_INSERT_SIZE = 50;
	public static final int MAX_BATCH_INSERT_SIZE = 1200;
	public static final int DEFAULT_BATCH_INSERT_SIZE = 300;

	public static final String wordMeaningJsonFilesPrefix = "word-meaning";
	public static final String wordMeaningOutputZipFilePrefix = "word-meaning-data_";
	public static final String jsonExtension = ".json";
	public static final boolean insertIntoTargetDBEnabled = false;
	
	
	
	public String getJSON_DATA_DIRECTORY() {
		return JSON_DATA_DIRECTORY;
	}
	public String getOUTPUT_DIRECTORY() {
		return OUTPUT_DIRECTORY;
	}
	public String getZIP_DIRECTORY() {
		return ZIP_DIRECTORY;
	}
	public int getMAX_PAGE_SIZE() {
		return MAX_PAGE_SIZE;
	}

}
