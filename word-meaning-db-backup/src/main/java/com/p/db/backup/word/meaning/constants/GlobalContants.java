package com.p.db.backup.word.meaning.constants;

public final class GlobalContants {

	public static final String JSON_DATA_DIRECTORY = "C:/Users/premendra.kumar/Desktop/DUMP/wm-imp-exp/wm-json/";
	public static final String OUTPUT_DIRECTORY = "C:/Users/premendra.kumar/Desktop/DUMP/wm-imp-exp/json-data-output/";
	public static final String ZIP_DIRECTORY = "C:/Users/premendra.kumar/Desktop/DUMP/wm-imp-exp/json-zip/";
	public static final int MAX_PAGE_SIZE = 1500;

	public static final int MIN_BATCH_INSERT_SIZE = 50;
	public static final int MAX_BATCH_INSERT_SIZE = 1200;
	public static final int DEFAULT_BATCH_INSERT_SIZE = 300;

	public static final String wordMeaningJsonFilesPrefix = "word-meaning";
	public static final String wordMeaningOutputZipFilePrefix = "word-meaning-data_";
	public static final String jsonExtension = ".json";
	public static final boolean insertIntoTargetDBEnabled = true;

}
