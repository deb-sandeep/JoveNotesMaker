package com.sandy.jnmaker.ui.panels.rawtxt.noteautocreator.mcqbulk;

import com.sandy.jnmaker.ui.panels.rawtxt.noteautocreator.mcqgroup.MCQ;
import com.sandy.jnmaker.ui.panels.rawtxt.noteautocreator.mcqgroup.MCQOption;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.sandy.common.util.StringUtil.isEmptyOrNull;

public class MultiChoiceBulkAutoCreator {

    private static final Logger log = Logger.getLogger( MultiChoiceBulkAutoCreator.class ) ;

    private static final int MAX_OPTIONS = 4 ;
    private static final int MAX_WRONG_OPTION_ITERATIONS = 100 ;
    
    private static final String ALSO_KEYWORD = "@also " ;
    private static final int ALSO_KEYWORD_LEN = ALSO_KEYWORD.length() ;

    private final String input ;
    
    // Key = Option
    // Value = List of categories for which this option is valid.
    private Map<String, Set<String>> rawData = new HashMap<>() ;
    
    private String caption ;
    private List<MCQ>    mcqs          = new ArrayList<>() ;
    private List<String> allCategories = new ArrayList<>() ;
    
    public MultiChoiceBulkAutoCreator( String input ) {
        this.input = input ;
        parseMatchingInput() ;
        createDataStructures() ;
    }

    private void parseMatchingInput() {

        String line;
        String currentCategory = null ;
        
        BufferedReader br = new BufferedReader( new StringReader( input ) ) ;
        
        boolean firstLine = true ;
        
        try {
            while( ( line = br.readLine() ) != null ) {
                if( isEmptyOrNull( line ) ) continue ;
                line = line.trim() ;
                
                if( firstLine ) {
                    caption = line ;
                    firstLine = false ;
                    continue ;
                }

                if( line.startsWith( "# " ) ) {
                    currentCategory = line.substring( 2 ).trim() ;
                    allCategories.add( currentCategory ) ;
                }
                else if( line.startsWith( "* " ) ) {
                    line = line.substring( 2 ).trim() ;

                    if( !line.contains( ALSO_KEYWORD ) ) {
                        line = line.trim() ;
                        addRawData( line, currentCategory ) ;
                    }
                    else {
                        int alsoIndex = line.indexOf( ALSO_KEYWORD ) ;
                        String alsoCatString = line.substring( alsoIndex + ALSO_KEYWORD_LEN ) ;
                        String[] alsoCategories = alsoCatString.split( "," ) ;
                        line = line.substring( 0, alsoIndex ) ;
                        
                        addRawData( line, currentCategory ) ;
                        for( String cat : alsoCategories ) {
                            addRawData( line, cat.trim() ) ;
                        }
                    }
                }
            }
        }
        catch( IOException e ) {
            log.error( "Unanticipated error.", e ) ;
        }
    }
    
    private void addRawData( String validOption, String category ) {
        if( isEmptyOrNull( category ) ) {
            throw new IllegalArgumentException( "Can't add valid option for empty category." ) ;
        }
        
        Set<String> categories = rawData.computeIfAbsent( validOption, k -> new HashSet<>() ) ;
        categories.add( category ) ;
    }
    
    private void createDataStructures() {

        for( String option : rawData.keySet() ) {

            Set<String> correctCategories = rawData.get( option ) ;
            int numCorrectCategories = 1 ;
            if( correctCategories.size() > 1 ) {
                numCorrectCategories = ThreadLocalRandom.current().nextInt( 0, correctCategories.size()-1 )+1 ;
            }
            
            for( int i=0; i<correctCategories.size(); i++ ) {
                MCQ mcq = new MCQ( caption + "\n\n**" + option.trim() + "**", 1 ) ;
                mcq.addOption( new MCQOption( correctCategories.toArray( new String[0] )[i], true ) ) ;
                addRandomCorrectCategories( mcq, correctCategories, Math.min( numCorrectCategories, MAX_OPTIONS ) ) ;
                addRandomWrongCategories( mcq, correctCategories ) ;
                mcqs.add( mcq ) ;
            }
        }
    }
    
    private void addRandomCorrectCategories( MCQ mcq, Set<String> correctCategories, int numCorrectCategories ) {
        int iterNo = 0 ;
        List<String> correctCategoryList = List.of( correctCategories.toArray( new String[0] ) ) ;
        while( mcq.getNumOptions() < numCorrectCategories &&
               iterNo < MAX_WRONG_OPTION_ITERATIONS ) {
            
            int randomIdx = ThreadLocalRandom.current().nextInt( 0, correctCategories.size() ) ;
            String randomAnswer = correctCategoryList.get( randomIdx ) ;
            
            if( !mcq.containsOption( randomAnswer ) ) {
                mcq.addOption( new MCQOption( randomAnswer, true ) ) ;
            }
            iterNo++ ;
        }
    }
    
    private void addRandomWrongCategories( MCQ mcq, Set<String> correctCategories ) {

        int iterNo = 0 ;
        while( mcq.getNumOptions() < MAX_OPTIONS &&
               iterNo < MAX_WRONG_OPTION_ITERATIONS ) {

            int randomIdx = ThreadLocalRandom.current().nextInt( 0, allCategories.size() ) ;
            String randomAnswer = allCategories.get( randomIdx ) ;

            if( !correctCategories.contains( randomAnswer ) ) {
                if( !mcq.containsOption( randomAnswer ) ) {
                    mcq.addOption( new MCQOption( randomAnswer ) ) ;
                }
            }
            iterNo++ ;
        }
    }

    public String createNote() {
        StringBuilder sb = new StringBuilder() ;
        Collections.shuffle( mcqs ) ;
        for( MCQ mcq : mcqs ) {
            sb.append( mcq.toString() ) ;
            sb.append( "\n\n" ) ;
        }
        return sb.toString() ;
    }

    public static void main(String[] args) {

        String input = "Identify the species: \n" +
                "# Ramapithecus\n" +
                "* Oldest of man's ancestors. @also Australopithecus, Homo habilis\n" +
                "* Immediate predessor of Australopithecus.\n" +
                "\n" +
                "# Australopithecus\n" +
                "* Small, statured, averaging about 120 cm tall.\n" +
                "* Cranial capacity ranged from 450-600 cm^3.\n" +
                "\n" +
                "# Homo habilis\n" +
                "* First man-like ancestor.\n" +
                "* 150 cm tall.\n" +
                "* Cranial capacity ranged from 680-735 cm^3.\n" +
                "* Immediate successor of Australopithecus.\n" +
                "* Immediate predessor of Homo erectus.\n" ;
        
        MultiChoiceBulkAutoCreator creator = new MultiChoiceBulkAutoCreator( input ) ;
        System.out.println( creator.createNote() ) ;
    }
}
