package anatlyzer.testing.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FileUtils {


	/**
	 * It deletes a directory.
	 * @param folder name of directory
	 * @param recursive it deletes the subdirectories recursively
	 */
	public static void deleteDirectory (String directory, boolean recursive) {
		File folder = new File(directory);
		if (folder.exists())
			for (File file : folder.listFiles()) {				
				if (file.isDirectory()) deleteDirectory(file.getPath(), recursive);
				file.delete();
			}
		folder.delete();
	}
	
	/**
	 * It creates a directory.
	 * @param folder name of directory
	 */
	public static void createDirectory (String directory) {
		new File(directory).mkdirs();
	}

	public static <T> List<T> getFiles(File folder, String extension, Predicate<File> pred, Function<File, T> mapper) throws IOException {
		return Files.list(folder.toPath())
				.filter(p -> p.toString().endsWith(extension))
				.map(p -> p.toFile())
				.filter(pred)
				.map(p -> mapper.apply(p))
				.collect(Collectors.toList());
	}

	/**
	 * Creates the required folders to store the given file.
	 * @param f0
	 */
	public static void mkFolderForFile(File f) {
		createDirectory(f.getParentFile().getAbsolutePath());
	}

}
