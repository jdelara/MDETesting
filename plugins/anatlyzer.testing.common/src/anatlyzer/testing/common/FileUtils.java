package anatlyzer.testing.common;

import java.io.File;

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


}
