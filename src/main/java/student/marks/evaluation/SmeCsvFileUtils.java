package student.marks.evaluation;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/*******************************************************************************
 * Copyright (c) 2022 This project is belongs to 'Venkateswarlu Dhanala'
 * Created on : 02/01/2022
 * Last modified on : 02/01/2022
 * Owned by : Venkateswarlu Dhanala
 ******************************************************************************/

public class SmeCsvFileUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(SmeCsvFileUtils.class);
    FileWriter fileWriter;
    FileReader fileReader;
    CSVWriter csvWriter;
    CSVReader csvReader;

    public SmeCsvFileUtils() {
    }

    public void generateInitialSmeDataIntoCsvFile(String initialStudentMarksData, Integer numberOfRecords) {
        File newSmeDataCsvFile = new File(initialStudentMarksData);
        try {
            newSmeDataCsvFile.createNewFile();
            fileWriter = new FileWriter(newSmeDataCsvFile);
            csvWriter = new CSVWriter(fileWriter);
            String[] dataHeaders =
                {"Firstname", "Lastname", "Subject1", "Subject2", "Subject3", "Subject4", "Subject5"};
            csvWriter.writeNext(dataHeaders);
            for (int i = 1; i <= numberOfRecords; i++) {
                String[] studentDetails = {generateRandomFnLn(), generateRandomFnLn(), generateRandomSubjectMarks(),
                    generateRandomSubjectMarks(), generateRandomSubjectMarks(), generateRandomSubjectMarks(),
                    generateRandomSubjectMarks()};
                csvWriter.writeNext(studentDetails);
            }
            csvWriter.close();
        } catch (Exception e) {
            LOGGER.error("Error message thrown during data generation");
        }
    }

    public String generateRandomFnLn() {
        //  making sure FirstName/LastName below 25 characters
        return RandomStringUtils.randomAlphabetic((new Random()).nextInt(25) + 1);
    }

    public String generateRandomSubjectMarks() {
        //  making sure subject marks <= 100 &
        //  converting to String for csv storage purpose
        return String.valueOf(new Random().nextInt(100));
    }

    public boolean calculateAverageOfStudentMarksAndSaveIntoCsvFile(String initialStudentMarksData) {
        try {
            fileReader = new FileReader(initialStudentMarksData);
            csvReader = new CSVReader(fileReader);
            String[] nextRecord;
            // storing each line/record into string for writing back to csv file
            List<String> dataAfterAverageCalculation = new ArrayList<>();
            boolean isHeader = true;
            // reading each record from existing csv file
            while ((nextRecord = csvReader.readNext()) != null) {
                ArrayList<String> stringArrayList = new ArrayList<>();
                // if first record, then adding "Average" to header
                if (isHeader) {
                    // parsing to all values in existing header
                    for (String columnValue : nextRecord)
                        stringArrayList.add(columnValue);
                    // finally adding "Average" to header
                    stringArrayList.add("Average");
                    isHeader = false;
                } else {
                    // rest all records
                    int counter = 0;
                    int sum = 0;
                    int noOfSubjects = 0;
                    for (String columnValue : nextRecord) {
                        stringArrayList.add(columnValue);
                        // skipping first two columns as they are firstname & lastname
                        if (counter < 2) {
                            counter++;
                            continue;
                        } else {
                            sum += Integer.parseInt(columnValue);
                            noOfSubjects++;
                        }
                    }
                    stringArrayList.add(String.valueOf(sum / noOfSubjects));
                }
                dataAfterAverageCalculation.add(stringArrayList.toString().replace("[", "")
                    .replace("]", "").replace(" ", ""));
            }

            File newSmeDataCsvFile = new File(initialStudentMarksData);
            newSmeDataCsvFile.createNewFile();
            fileWriter = new FileWriter(newSmeDataCsvFile);
            csvWriter = new CSVWriter(fileWriter);
            AtomicInteger counter = new AtomicInteger();
            for (String eachLineWrite : dataAfterAverageCalculation) {
                LOGGER.debug("Average marks calculated for record at row : {}", counter.getAndIncrement());
                csvWriter.writeNext(eachLineWrite.split(","));
            }
            fileWriter.close();
            return true;
        } catch (FileNotFoundException e) {
            LOGGER.error("File not found exception thrown : {}", e.getMessage());
            return false;
        } catch (IOException e) {
            LOGGER.error("IO exception thrown : {}", e.getMessage());
            return false;
        }
    }
}
