package com.example.jsonnotes.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Scanner;

/**
 * Helper class for interacting with files.
 */
public class FileHelper {

    /**
     * Create a blank file
     * @param file The {@link File} to create
     * @return TRUE if the file is created, FALSE if it is not.
     */
    public static boolean createFile(File file) {
        if (file == null) {
            throw new IllegalArgumentException("file cannot be null");
        }
        try {
            return file.createNewFile();
        } catch (IOException e) {
            //Cannot create file
            e.printStackTrace();
            System.out.printf("Cannot create file %s", file.getAbsolutePath());
            return false;
        }
    }

    /**
     * Create a blank file
     * @param filePath The full path to the {@link File} to create
     * @return TRUE if the file is created, FALSE if it is not.
     */
    public static boolean createFile(String filePath) {
        if (filePath == null || filePath.equals("")) {
            throw new IllegalArgumentException("filePath cannot be blank");
        }
        try {
            var file = new File(filePath);
            return createFile(file);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.printf("Cannot create file %s", filePath);
            return false;
        }
    }

    /**
     * Create a file and write a string of data to it
     * @param file The {@link File} to create
     * @param data The string of data to write to the file
     * @return TRUE if the file is created, FALSE if it is not.
     */
    public static boolean createFile(File file, String data) {
        if (file == null) {
            throw new IllegalArgumentException("file cannot be null");
        }
        if (data == null) {
            throw new IllegalArgumentException("data cannot be null");
        }
        if (!createFile(file)) {
            return false;
        }
        try {
            var writer = new FileWriter(file.getAbsolutePath());
            writer.write(data);
            writer.close();
            return true;
        } catch (IOException e) {
            //Cannot write to file
            e.printStackTrace();
            System.out.printf("Cannot write to file %s", file.getAbsolutePath());
            return false;
        }
    }

    /**
     * Create a file and write a string of data to it
     * @param filePath The full path to the {@link File} to create
     * @param data The string of data to write to the file
     * @return TRUE if the file is created, FALSE if it is not.
     */
    public static boolean createFile(String filePath, String data) {
        if (filePath == null || filePath.equals("")) {
            throw new IllegalArgumentException("filePath cannot be blank");
        }
        try {
            var file = new File(filePath);
            return createFile(file, data);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.printf("Cannot create file %s", filePath);
            return false;
        }
    }

    /**
     * Replace all the data in a file
     * @param file The {@link File} to replace the data in
     * @param data The new data to write to the file
     * @return TRUE if the file was rewritten, FALSE if it wasn't
     */
    public static boolean replaceFileData(File file, String data) {
        if (file == null) {
            throw new IllegalArgumentException("file cannot be null");
        }
        if (data == null) {
            throw new IllegalArgumentException("data cannot be null");
        }
        //Define a backup file
        var backupFile = new File(String.format("%s.bak", file.getAbsolutePath()));
        if (file.exists()) {
            //create a backup copy of the file
            if (backupFile.delete()) {
                System.out.printf("Cannot delete backup file %s - likely other issues will occur.%n", backupFile.getAbsolutePath());
            }
            try {
                Files.move(file.toPath(), backupFile.toPath());
            } catch (IOException e) {
                //Failed to create the backup file
                e.printStackTrace();
                System.out.printf("Cannot create a backup file %s - likely other issues will occur - proceeding with increased data volatility%n", backupFile.getAbsolutePath());
            }
            if (file.delete()) {
                //Cannot delete the file, so we probably don't have access to the file.
                System.out.printf("Cannot delete %s - exiting.%n", file.getAbsolutePath());
                return false;
            }
        }
        //Write the file
        if (!createFile(file, data)) {
            //Cannot create the file - try restoring from backup?
            if (!file.exists() && backupFile.exists()) {
                //Backup file exists and file does not exist - restore
                try {
                    Files.move(backupFile.toPath(), file.toPath());
                } catch (IOException e) {
                    //Failed to restore file from backup
                    e.printStackTrace();
                    System.out.printf("Cannot restore file from backup %s%n", backupFile.getAbsolutePath());
                }
            }
            //The file was not rewritten
            return false;
        }
        //The file was successfully rewritten
        //Delete the backup file - whether it exists or not, a call to delete produces the desired result
        if (backupFile.exists() && !backupFile.delete()) {
            System.out.printf("Cannot delete backup file %s.%n", backupFile.getAbsolutePath());
        }
        return true;
    }

    /**
     * Get the data from a file as a string
     * @param file The {@link File} to get data from
     * @return A string containing the file's data
     */
    public static String getFileAsString(File file) {
        if (file == null) {
            throw new IllegalArgumentException("file cannot be null");
        }
        var fileData = new StringBuilder();
        try {
            //Read the file
            var fileReader = new Scanner(file);
            while (fileReader.hasNextLine()) {
                fileData.append(fileReader.nextLine());
            }
            fileReader.close();
            return fileData.toString();
        } catch (FileNotFoundException e) {
            //No matching file found
            e.printStackTrace();
            System.out.printf("Cannot read file %s", file.getAbsolutePath());
            return null;
            //Could rethrow exception instead of returning null - indicating that this is a non-recoverable error.
        }
    }
}
