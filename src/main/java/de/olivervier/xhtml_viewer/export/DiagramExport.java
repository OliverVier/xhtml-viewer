package de.olivervier.xhtml_viewer.export;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.regex.Pattern;

import de.olivervier.xhtml_viewer.cli.UserInteraction;
import de.olivervier.xhtml_viewer.model.Page;
import de.olivervier.xhtml_viewer.model.Param;
import de.olivervier.xhtml_viewer.model.Relation;
import de.olivervier.xhtml_viewer.model.Relation.RelationType;

public class DiagramExport {
    
    private final String[] DIAGRAM_OPTIONS       = {"set namespaceSeparator none", 
                                                    "skinparam linetype polyline", 
                                                    "skinparam linetype ortho",
                                                    "top to bottom direction"};
    private final String START_DIAGRAM_FORMAT    = "@startuml %s";
    private final String END_DIAGRAM_FORMAT      = "@enduml";
    private final String OBJECT_NAME_FORMAT  = "object \"%s\" as %s";
    private final String OBJECT_PARAMETER_FORMAT = "%s : %s";
    private final String OBJECT_COMPOSITION_FORMAT  = "%s ---> %s";
    private final String OBJECT_INCLUDE_FORMAT  = "%s ---> %s : INCLUDE";
    private final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    public void handleExport(List<Page> pagesList, Path outputPath) {
        String fileContent = "";

        LocalDateTime localDateTime = LocalDateTime.now();
        String datetime = DATE_TIME_FORMATTER.format(localDateTime);
        String filename = "plantuml-"+datetime+".plantuml";

        List<Page> pages = pagesList; 
        
        pages = sortAfterName(pages);

        //Start uml file, add options
        fileContent = addFormattedLine(fileContent, START_DIAGRAM_FORMAT, filename);
        for(String option : DIAGRAM_OPTIONS) {
            fileContent = addLine(fileContent, option);
        }

        //Add content
        
        //1. Create objects
        for(Page page : pages) {
            fileContent = addFormattedLine(fileContent, 
            							   OBJECT_NAME_FORMAT, 
            							   page.getName() + " - " + page.getFilePath().toString().replace("\\", "/"), 
            							   replaceInvalidCharacters(page.getName() + "_" + page.getFilePath().toString()));
        }

        //2. Add object parameters
        for(Page page : pages) {
            for(Param param : page.getParameters()) {
                fileContent = addFormattedLine(fileContent, 
                                               OBJECT_PARAMETER_FORMAT, 
                                               replaceInvalidCharacters(page.getName() + "_" + page.getFilePath().toString()), 
                                               "%s = \"%s\"".formatted(param.getName(), param.getValue()));
            }
        }

        //3. Draw object relations
        for(Page page : pages) {
            for(Relation relation : page.getRelations()) {
                fileContent = addFormattedLine(fileContent, 
                                               relation.getType().equals(RelationType.COMPOSITION) ? OBJECT_COMPOSITION_FORMAT : OBJECT_INCLUDE_FORMAT, 
                                               replaceInvalidCharacters(page.getName() + "_" + page.getFilePath().toString()),
                                               replaceInvalidCharacters(relation.getRelation().getFilePath().toString()));
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

    private void createFileHandler(Path filePath, String filename, String fileContent) {
        if(filePath == null) {
            Path execPath = Path.of(System.getProperty("user.dir"));
            createFile(execPath, filename, fileContent);
        } 
        else {
            createFile(filePath, filename, fileContent);
        }
    }

    private void createFile(Path filePath, String filename, String fileContent) {
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
        UserInteraction.sendMessage("Output file to " + filePath.toAbsolutePath().toString());
    }

    /**
     * Sort pages after their name
     * @param pages
     * @return new list of sorted pages
     */
    private List<Page> sortAfterName(List<Page> pages) {
        ArrayList<Page> newPages = new ArrayList<Page>();
        newPages.addAll(pages);
        newPages.sort((o1, o2) -> o1.getFilePath().toString().compareTo(o2.getFilePath().toString()));
        return newPages;
    }
    
    /**
     * Get all relations recursively without considering the possibility
     * of doubles.
     * @param pagesList
     * @param currentPage
     * @return new list of relations
     */
    private List<Page> getRelationPagesRec(List<Page> pagesList, Page currentPage) {
        List<Page> pages = new ArrayList<>();
        List<Relation> relations = currentPage.getRelations();
        if(relations != null) {
            for(Relation relation : relations) {
                pages.addAll(getRelationPagesRec(pagesList, relation.getRelation()));
                pages.add(relation.getRelation());
            }
        } 

        return pages;
    }

    private List<Page> removeDoubles(List<Page> pagesList) {
        ArrayList<Page> pages = new ArrayList<>(pagesList);

        for(int i = 0; i < pages.size(); i++) {
            Page currentPage = pages.get(i);
            for(int z = i+1; z < pages.size(); z++) {
                if(pages.get(z).equals(currentPage)) {
                    pages.remove(z);
                    z--;
                }
            }
        }
        return pages;
    }
    
    private String replaceInvalidCharacters(String text) {
    	return text.replace("$", "")
    				.replace("-", "")
                	.replace("\\", "_")
                	.replace(":", "");
    }
}