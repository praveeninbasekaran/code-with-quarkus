import java.io.File;

public class RenameFilesRecursively {

    public static void main(String[] args) {
        // Specify the parent directory path here
        String directoryPath = "D:/path/to/your/folder";  // Change this to your directory
        File parentDirectory = new File(directoryPath);

        // Old and new extensions
        String oldExt = ".png";
        String newExt = ".txt";

        // Start renaming
        if (parentDirectory.exists() && parentDirectory.isDirectory()) {
            renameFilesInDirectory(parentDirectory, oldExt, newExt);
            System.out.println("Renaming completed.");
        } else {
            System.out.println("Directory not found: " + directoryPath);
        }
    }

    public static void renameFilesInDirectory(File directory, String oldExt, String newExt) {
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                // Recurse into subdirectory
                renameFilesInDirectory(file, oldExt, newExt);
            } else {
                if (file.getName().toLowerCase().endsWith(oldExt)) {
                    String newName = file.getName().substring(0, file.getName().length() - oldExt.length()) + newExt;
                    File renamedFile = new File(file.getParent(), newName);
                    if (file.renameTo(renamedFile)) {
                        System.out.println("Renamed: " + file.getAbsolutePath() + " -> " + renamedFile.getAbsolutePath());
                    } else {
                        System.out.println("Failed to rename: " + file.getAbsolutePath());
                    }
                }
            }
        }
    }
}