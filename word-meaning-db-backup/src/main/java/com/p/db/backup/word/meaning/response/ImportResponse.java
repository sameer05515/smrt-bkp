package com.p.db.backup.word.meaning.response;

import java.util.ArrayList;
import java.util.List;

import com.p.db.backup.word.meaning.pojo.Plan;

public class ImportResponse {

	private String message;
	private String status;
	private String zipFileToExtract;
	private int batchSize;
	private long record;
	private long duration;
	private boolean insertIntoTargetDBEnabled;
	private List<ImportExtractedFilesResponse> details=new ArrayList<ImportResponse.ImportExtractedFilesResponse>();
	private List<Plan> importPlan=new ArrayList<Plan>();
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getZipFileToExtract() {
		return zipFileToExtract;
	}
	public void setZipFileToExtract(String zipFileToExtract) {
		this.zipFileToExtract = zipFileToExtract;
	}
	public int getBatchSize() {
		return batchSize;
	}
	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}
	public long getRecord() {
		return record;
	}
	public void setRecord(long count) {
		this.record = count;
	}
	public long getDuration() {
		return duration;
	}
	public void setDuration(long duration) {
		this.duration = duration;
	}
	public List<ImportExtractedFilesResponse> getDetails() {
		return details;
	}
	public void setDetails(List<ImportExtractedFilesResponse> details) {
		this.details = details;
	}
	public boolean isInsertIntoTargetDBEnabled() {
		return insertIntoTargetDBEnabled;
	}
	public void setInsertIntoTargetDBEnabled(boolean insertIntoTargetDBEnabled) {
		this.insertIntoTargetDBEnabled = insertIntoTargetDBEnabled;
	}
	public List<Plan> getImportPlan() {
		return importPlan;
	}
	public void setImportPlan(List<Plan> importPlan) {
		this.importPlan = importPlan;
	}
	
	public static class ImportExtractedFilesResponse{
		
		private String fileName;
		private long record;
		private long duration;
		private String message;
		private String status;
		public String getFileName() {
			return fileName;
		}
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
		public long getRecord() {
			return record;
		}
		public void setRecord(long record) {
			this.record = record;
		}
		public long getDuration() {
			return duration;
		}
		public void setDuration(long duration) {
			this.duration = duration;
		}
		public String getMessage() {
			return message;
		}
		public void setMessage(String message) {
			this.message = message;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
	}

}


