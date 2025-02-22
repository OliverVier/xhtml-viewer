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
import java.util.regex.Pattern;

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

    public void handleExport(List<Page> pagesList, String outputPath, String includePattern) {
        String fileContent = "";

        LocalDateTime localDateTime = LocalDateTime.now();
        String datetime = DATE_TIME_FORMATTER.format(localDateTime);
        String filename = "plantuml-"+datetime+".plantuml";

        List<Page> pages = pagesList; 

        //Filter pages, then sort
        if(!includePattern.equals(".")) {
            pages = filterAfterPattern(pages, includePattern);
        }
        pages = sortAfterName(pages);

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

    private List<Page> sortAfterName(List<Page> pages) {
        ArrayList<Page> newPages = new ArrayList<Page>();
        newPages.addAll(pages);
        newPages.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
        return newPages;
    }

    /**
     * Filters pages only at the first level. All pages, that a page is related to, are still shown
     * @param includePattern regex expression
     * @return new filtered list with pages, including all their relationships without regard to
     * the given pattern.
     */
    private List<Page> filterAfterPattern(List<Page> pagesList, String includePattern) {
        
        List<Page> pages = new ArrayList<>();
        pages.addAll(pagesList);

        List<Page> filteredList = new ArrayList<Page>();

        Pattern pattern = Pattern.compile(includePattern);

        //Filter at first level
        for(Page page : pages) {
            if(pattern.matcher(page.getName()).find()) {
                filteredList.add(page);
            }
        }

        //Include all pages having relationship to given page
        for(Page page: pages) {
            for(Page relpage : page.getRelations()) {
                if (filteredList.contains(relpage)) {
                    filteredList.add(page);
                }
            }
        }   

        //Include all relations from given pages
        int size = filteredList.size();
        for(int i = 0; i < size; i++) {
            filteredList.addAll(getRelationsRec(pages, filteredList.get(i)));
        }

        //Remove doubles
        filteredList = removeDoubles(filteredList);

        return filteredList;
    }

    private List<Page> getRelationsRec(List<Page> pagesList, Page currentPage) {
        List<Page> pages = new ArrayList<>();
        List<Page> relations = currentPage.getRelations();
        if(relations != null) {
            for(Page page : relations) {
                pages.addAll(getRelationsRec(pagesList, page));
                pages.add(page);
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
                    pages.remove(z); //might have to set index - 1
                    z--;
                }
            }
        }
        return pages;
    }
}