import java.io.File;

public class RenameFilesRecursively {
    public static void main(String[] args) {
        // Specify the parent directory
        File parentDirectory = new File("path/to/your/parent/directory");
        
        // Define the extensions
        String oldExt = ".png";
        String newExt = ".txt";
        
        // Start the renaming process
        renameFilesInDirectory(parentDirectory, oldExt, newExt);
    }

    public static void renameFilesInDirectory(File directory, String oldExt, String newExt) {
        // Get all the files and subdirectories
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // If it's a directory, recurse into it
                    renameFilesInDirectory(file, oldExt, newExt);
                } else {
                    // If it's a file, check and rename if it matches the old extension
                    if (file.getName().endsWith(oldExt)) {
                        String newName = file.getName().substring(0, file.getN