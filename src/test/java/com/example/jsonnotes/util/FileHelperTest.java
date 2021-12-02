package com.example.jsonnotes.util;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FileHelper util tests")
class FileHelperTest {

    private File _testFile;
    private String _testData;

    @BeforeEach
    void setUp() {
        _testFile = new File(String.format("%s/test.txt", _getTestDataPath()));
        _testData = UUID.randomUUID().toString();
        if (!_testFile.exists()) {
            return;
        }
        if (_testFile.delete()) {
            System.out.println("Deleted test file");
        }
        else {
            System.out.println("Failed to delete test file");
        }
    }

    @AfterEach
    void tearDown() {
        if (_testFile.delete()) {
            System.out.println("Deleted test file");
        }
        else {
            System.out.println("Failed to delete test file");
        }
    }

    @DisplayName("Create a file")
    @Nested
    public class FileCreation {

        @DisplayName("blank file from File reference")
        @Test
        void createFile() {
            System.out.println("Testing FileHelper.createFile(File)");
            assertTrue(FileHelper.createFile(_testFile));
            System.out.println("createFile() reports success");
            assertTrue(_testFile.exists());
            System.out.printf("File %s exists%n", _testFile.getAbsolutePath());
        }

        @DisplayName("blank file from path string")
        @Test
        void createFileFromPath() {
            System.out.println("Testing FileHelper.createFile(String)");
            assertTrue(FileHelper.createFile(_testFile.getAbsolutePath()));
            System.out.println("createFile() reports success");
            assertTrue(_testFile.exists());
            System.out.printf("File %s exists%n", _testFile.getAbsolutePath());
        }

        @DisplayName("file with data from File reference")
        @Test
        void createFileWithData() {
            System.out.println("Testing FileHelper.createFile(File, String)");
            assertTrue(FileHelper.createFile(_testFile, _testData));
            System.out.println("createFile() reports success");
            assertTrue(_testFile.exists());
            System.out.printf("File %s exists%n", _testFile.getAbsolutePath());
        }

        @DisplayName("file with data from path string")
        @Test
        void createFileWithDataFromPath() {
            System.out.println("Testing FileHelper.createFile(String, String)");
            assertTrue(FileHelper.createFile(_testFile.getAbsolutePath(), _testData));
            System.out.println("createFile() reports success");
            assertTrue(_testFile.exists());
            System.out.printf("File %s exists%n", _testFile.getAbsolutePath());
        }
    }

    @DisplayName("Replace the data in a file")
    @Test
    void replaceFileData() {
        System.out.println("Testing FileHelper.replaceFileData(File, String)");
        var backupFile = new File(String.format("%s.bak", _testFile.getAbsolutePath()));
        String _replacementData = UUID.randomUUID().toString();
        assertTrue(FileHelper.replaceFileData(_testFile, _replacementData));
        System.out.println("replaceFileData() reports success");
        //Test file exists
        assertTrue(_testFile.exists());
        System.out.printf("File %s exists%n", _testFile.getAbsolutePath());
        //Test file data is accurate
        var fileData = new StringBuilder();
        try {
            //Read the file
            var fileReader = new Scanner(_testFile);
            while (fileReader.hasNextLine()) {
                fileData.append(fileReader.nextLine());
            }
            fileReader.close();
        } catch (FileNotFoundException e) {
            //No matching file found
            e.printStackTrace();
            System.out.printf("Cannot read file %s", _testFile.getAbsolutePath());
        }
        assertEquals(_replacementData, fileData.toString());
        System.out.printf("File content %s matches %s%n", fileData, _replacementData);
        //Backup file does not exist
        assertFalse(backupFile.exists());
        System.out.printf("Backup file %s does not exist%n", backupFile.getAbsolutePath());
    }

    @DisplayName("Get file data as a string")
    @Test
    void getFileAsString() {
        System.out.println("Testing FileHelper.getFileAsString(File)");
        try {
            System.out.println("Writing test file");
            var writer = new FileWriter(_testFile.getAbsolutePath());
            writer.write(_testData);
            writer.close();
        } catch (IOException e) {
            //Cannot write to file
            e.printStackTrace();
            System.out.printf("Cannot write to file %s", _testFile.getAbsolutePath());
        }
        var fileData = FileHelper.getFileAsString(_testFile);
        assertEquals(_testData, fileData);
        System.out.printf("File content %s matches %s%n", fileData, _testData);
    }

    private static String _getTestDataPath() {
        var currentRelativePath = Paths.get("").toAbsolutePath().resolve("test-data").normalize();
        return currentRelativePath.toString();
    }
}