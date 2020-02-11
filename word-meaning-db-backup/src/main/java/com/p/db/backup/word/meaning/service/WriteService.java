package com.p.db.backup.word.meaning.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.springframework.stereotype.Service;

import com.p.db.backup.word.meaning.constants.GlobalContants;
import com.p.db.backup.word.meaning.exception.InvalidInputSuppliedException;
import com.p.db.backup.word.meaning.filefilter.DynamicFileNameFilter;

@Service
public class WriteService {

	public String printData(String filePath, String json) throws InvalidInputSuppliedException, IOException {

		if (filePath == null) {
			throw new InvalidInputSuppliedException(
					"Invalid file path ( " + filePath + " ) supplied. File path is null");
		}

		if (!(new File(filePath).getParentFile()).exists()) {
			throw new InvalidInputSuppliedException(
					"Invalid file path ( " + filePath + " ) supplied. Parent folder non-existant ");
		}

		if (json == null) {
			throw new InvalidInputSuppliedException("Invalid json data ( " + json + " ) supplied.");
		}

		Files.write(Paths.get(filePath), json.getBytes(), StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING);

		return "Success";
	}

	public String zip(String sourceFolder, String targetFolder, String zipFileName) throws IOException {
		final String sourceFile = sourceFolder;
		final FileOutputStream fos = new FileOutputStream(targetFolder + File.separator + zipFileName);
		final ZipOutputStream zipOut = new ZipOutputStream(fos);
		final File fileToZip = new File(sourceFile);

		zipFile(fileToZip, fileToZip.getName(), zipOut);
		zipOut.close();
		fos.close();
		return "Success";
	}

	private void zipFile(final File fileToZip, final String fileName, final ZipOutputStream zipOut) throws IOException {
		if (fileToZip.isHidden()) {
			return;
		}
		if (fileToZip.isDirectory()) {
			if (fileName.endsWith("/")) {
				zipOut.putNextEntry(new ZipEntry(fileName));
				zipOut.closeEntry();
			} else {
				zipOut.putNextEntry(new ZipEntry(fileName + "/"));
				zipOut.closeEntry();
			}
			final File[] children = fileToZip.listFiles();
			for (final File childFile : children) {
				zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
			}
			return;
		}
		final FileInputStream fis = new FileInputStream(fileToZip);
		final ZipEntry zipEntry = new ZipEntry(fileName);
		zipOut.putNextEntry(zipEntry);
		final byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zipOut.write(bytes, 0, length);
		}
		fis.close();
	}

	// public void unzip(String zipFilePath, String destDir) {
	// File dir = new File(destDir);
	// // create output directory if it doesn't exist
	// if(!dir.exists()) dir.mkdirs();
	// FileInputStream fis;
	// //buffer for read and write data to file
	// byte[] buffer = new byte[1024];
	// try {
	// fis = new FileInputStream(zipFilePath);
	// ZipInputStream zis = new ZipInputStream(fis);
	// ZipEntry ze = zis.getNextEntry();
	// while(ze != null){
	// String fileName = ze.getName();
	// File newFile = new File(destDir + File.separator + fileName);
	// System.out.println("Unzipping to "+newFile.getAbsolutePath());
	// //create directories for sub directories in zip
	// new File(newFile.getParent()).mkdirs();
	// FileOutputStream fos = new FileOutputStream(newFile);
	// int len;
	// while ((len = zis.read(buffer)) > 0) {
	// fos.write(buffer, 0, len);
	// }
	// fos.close();
	// //close this ZipEntry
	// zis.closeEntry();
	// ze = zis.getNextEntry();
	// }
	// //close last ZipEntry
	// zis.closeEntry();
	// zis.close();
	// fis.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// }

	public File[] unzip(final String zipFilePath, final String unzipLocation) throws IOException {
		unzipToTarget(zipFilePath, unzipLocation);

		// File[] targetFiles=new File(unzipLocation).listFiles(new
		// DynamicFileNameFilter(GlobalContants.wordMeaningJsonFilesPrefix));

		File[] targetFiles = getFileStartWith(unzipLocation, GlobalContants.wordMeaningJsonFilesPrefix);

		return targetFiles;
	}

	private File[] getFileStartWith(String location, String searchString) {

		List<File> files = new ArrayList<File>();

		File[] targetFiles = new File(location).listFiles(new DynamicFileNameFilter(searchString));

		if (targetFiles != null) {
			for (File f : targetFiles) {
				if (f.isDirectory()) {
					File[] child = getFileStartWith(f.getAbsolutePath(), searchString);
					if (child != null) {
						for (File cf : child) {
							files.add(cf);
						}

					}
				} else {
					files.add(f);
				}
			}
		}

		return files.toArray(new File[files.size()]);
	}

	private void unzipToTarget(final String zipFilePath, final String unzipLocation) throws IOException {

		if (!(Files.exists(Paths.get(unzipLocation)))) {
			Files.createDirectories(Paths.get(unzipLocation));
		}
		try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFilePath))) {
			ZipEntry entry = zipInputStream.getNextEntry();
			while (entry != null) {
				Path filePath = Paths.get(unzipLocation, entry.getName());
				if (!entry.isDirectory()) {
					unzipFiles(zipInputStream, filePath);
				} else {
					Files.createDirectories(filePath);
				}

				zipInputStream.closeEntry();
				entry = zipInputStream.getNextEntry();
			}
		}
	}

	private void unzipFiles(final ZipInputStream zipInputStream, final Path unzipFilePath) throws IOException {

		try (BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(unzipFilePath.toAbsolutePath().toString()))) {
			byte[] bytesIn = new byte[1024];
			int read = 0;
			while ((read = zipInputStream.read(bytesIn)) != -1) {
				bos.write(bytesIn, 0, read);
			}
		}

	}

}
