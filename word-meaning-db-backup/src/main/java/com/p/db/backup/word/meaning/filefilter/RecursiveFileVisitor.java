package com.p.db.backup.word.meaning.filefilter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class RecursiveFileVisitor {
	public static void main(String args[]) /* throws FileNotFoundException */ {

		visitFiles("C:\\Users\\premendra.kumar\\Desktop\\DUMP\\file-visit-output.txt");
	}

	public static void visitFiles(String outputTxtFileLocation) {

		try (PrintStream ps = new PrintStream(outputTxtFileLocation);) {
			// PrintStream ps = new
			// PrintStream("C:\\Users\\premendra.kumar\\Desktop\\DUMP\\file-visit-output.txt");
			FileVisitor<Path> simpleFileVisitor = new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					ps./* System.out */println("-------------------------------------");
					ps./* System.out */println(
							"DIRECTORY NAME:" + dir.getFileName() + " LOCATION:" + dir.toFile().getPath());
					ps./* System.out */println("-------------------------------------");
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult visitFile(Path visitedFile, BasicFileAttributes fileAttributes)
						throws IOException {
					ps./* System.out */println("FILE NAME: " + visitedFile.getFileName());
					return FileVisitResult.CONTINUE;
				}
			};
			FileSystem fileSystem = FileSystems.getDefault();
			Path rootPath = fileSystem.getPath("D:\\Prem");
			// try {
			Files.walkFileTree(rootPath, simpleFileVisitor);
			// } catch (IOException ioe) {
			// ioe.printStackTrace();
			// }
		} catch (FileNotFoundException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}
}