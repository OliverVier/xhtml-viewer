package de.olivervier.xhtml_viewer.diagram;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;

import de.olivervier.xhtml_viewer.model.Page;
import de.olivervier.xhtml_viewer.model.Param;

public class DiagramExport {
    
    private final String[] DIAGRAM_OPTIONS       = {"set namespaceSeparator none", 
                                                    "skinparam linetype polyline", 
                                                    "skinparam linetype ortho",
                                                    "top to bottom direction"};
    private final String START_DIAGRAM_FORMAT    = "@startuml %s";
    private final String END_DIAGRAM_FORMAT      = "@enduml";
    private final String OBJECT_NAME_FORMAT  = "object %s";
    private final String OBJECT_PARAMETER_FORMAT = "%s : %s";
    private final String OBJECT_RELATION_FORMAT  = "%s ---> %s";
    private final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    public void handleExport(List<Page> pages, String outputPath) {
        String fileContent = "";

        LocalDateTime localDateTime = LocalDateTime.now();
        String datetime = DATE_TIME_FORMATTER.format(localDateTime);
        String filename = "plantuml-"+datetime+".plantuml";

        //Sort pages after name
        ArrayList<Page> newPages = new ArrayList<Page>();
        newPages.addAll(pages);
        newPages.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
        pages = newPages;

        //Start uml file, add options
        fileContent = addFormattedLine(fileContent, START_DIAGRAM_FORMAT, filename);
        for(String option : DIAGRAM_OPTIONS) {
            fileContent = addLine(fileContent, option);
        }

        //Add content
        
        //1. Create objects
        for(Page page : pages) {
            fileContent = addFormattedLine(fileContent, OBJECT_NAME_FORMAT, page.getName());
        }

        //2. Add object parameters
        for(Page page : pages) {
            for(Param param : page.getParameters()) {
                fileContent = addFormattedLine(fileContent, 
                                               OBJECT_PARAMETER_FORMAT, 
                                               page.getName(), param.getName()+"-"+param.getValue());
            }
        }

        //3. Draw object relations
        for(Page page : pages) {
            for(Page relation : page.getRelations()) {
                fileContent = addFormattedLine(fileContent, 
                                               OBJECT_RELATION_FORMAT, 
                                               page.getName(),
                                               relation.getName());
            }
        }

        //End uml file
        fileContent = addLine(fileContent, END_DIAGRAM_FORMAT);

        //Create and export file;
        createFileHandler(outputPath, filename, fileContent);
    }

    private String addLine(String currentString, String content) {
        return currentString.concat(content + "\n");
    }

    private String addFormattedLine(String currentString, String format, Object... args) {
        String formattedLine = "";
        try {
            formattedLine = String.format(format, args);
        } catch(IllegalFormatException e) {
            e.printStackTrace();
        }
        return currentString.concat(formattedLine + "\n");
    }

    private void createFileHandler(String filePath, String filename, String fileContent) {
        if(filePath == null || filePath.isBlank() || filePath.equals(".")) {
            String execPath = System.getProperty("user.dir");
            createFile(execPath, filename, fileContent);
        } 
        else {
            createFile(filePath, filename, fileContent);
        }
    }

    private void createFile(String filePath, String filename, String fileContent) {
        File newFile = new File(filePath+File.separator+filename);
        try (FileOutputStream stream = new FileOutputStream(newFile)) {
            stream.write(fileContent.getBytes());
        } catch (FileNotFoundException e) {
            System.err.println(String.format("Could not write file to %s. Details: %s", filePath, e.getMessage()));
        } catch (SecurityException e) {
            System.err.println(String.format("No permission to write file to %s.", filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}