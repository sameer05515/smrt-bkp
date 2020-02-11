package com.p.db.backup.word.meaning.filefilter;

import java.io.File;
import java.io.FilenameFilter;

public class DynamicFileNameFilter implements FilenameFilter {

	private String comparingString;

	private CompareModes compareMode = CompareModes.StartsWith;

	private boolean caseSensitive = true;

	public enum CompareModes {
		Exact, StartsWith, EndsWith, Contains
	}

	public DynamicFileNameFilter(String comparingString) {
		this.comparingString = comparingString;
	}

	public DynamicFileNameFilter(String comparingString, CompareModes compareMode) {
		this.comparingString = comparingString;
		this.compareMode = compareMode;
	}

	@Override
	public boolean accept(File dir, String name) {

		boolean accept = false;
		File file = new File(dir.getParent()+File.separator+name);
		
//		System.out.println("comparingString == "+comparingString +" name == "+name +" file.getAbsolutePath() "+file.getAbsolutePath());
		
		if(file.isDirectory()) {
			System.out.println("Valid as this is directory");
			return true;
		}
		
		if(compareMode.equals(CompareModes.StartsWith)) {
			if (this.caseSensitive) {
				//
				accept = name.startsWith(comparingString);
			} else {
				accept = name.toLowerCase().startsWith(comparingString.toLowerCase());
			}
		}

//		switch (compareMode) {
//		case StartsWith: {
//			if (this.caseSensitive) {
//				//
//				accept = name.startsWith(comparingString);
//			} else {
//				accept = name.toLowerCase().startsWith(comparingString.toLowerCase());
//			}
//		}
//		default: {
//			accept = false;
//		}
//		}

		// if (name.equals(comparingString) && !file.isDirectory())
		// accept= false;
		//
		// else
		// accept= true;

		return accept;
	}

	public CompareModes getCompareMode() {
		return compareMode;
	}

	public void setCompareMode(CompareModes compareMode) {
		this.compareMode = compareMode;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

}