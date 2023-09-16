package com.sandy.jnmaker.ui.panels.rawtxt.noteautocreator.mcqgroup;

import com.sandy.common.util.StringUtil;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MultiChoiceGroupAutoCreator {

    private static final Logger log = Logger.getLogger( MultiChoiceGroupAutoCreator.class ) ;

    private static final int MAX_OPTIONS = 6 ;

    private final String input ;

    private String captionLHS2RHS = "" ;
    private String captionRHS2LHS = "" ;

    public MultiChoiceGroupAutoCreator( String input ) {
        this.input = input ;
        parseMatchingInput() ;
        createDataStructures() ;
    }

    private Map<String, List<String>> rawData = new HashMap<>() ;
    private List<MCQ> mcqs = new ArrayList<>() ;
    private List<String> allOptions = new ArrayList<>() ;

    private void parseMatchingInput() {

        String line;
        BufferedReader br = new BufferedReader( new StringReader( input ) ) ;
        boolean isFirstLine = true ;

        try {
            while( ( line = br.readLine() ) != null ) {
                line = line.trim() ;

                if( StringUtil.isEmptyOrNull( line ) ) continue ;

                if( isFirstLine ) {
                    extractCaptions( line ) ;
                    isFirstLine = false ;
                    continue ;
                }

                if( line.contains( "=" ) ) {

                    String[] keywords = null ;
                    String[] values = null ;
                    String[] parts = line.split( "=" ) ;

                    keywords = parts[0].split( "," ) ;
                    values = parts[1].split( "," ) ;

                    List<String> valueList = new ArrayList<>() ;
                    for( String value : values ) {
                        valueList.add( value.trim() ) ;
                        allOptions.add( value.trim() ) ;
                    }

                    for( String keyword : keywords ) {
                        rawData.put( keyword.trim(), valueList ) ;
                    }
                }
            }
        }
        catch( IOException e ) {
            log.error( "Unanticipated error.", e ) ;
        }
    }

    private void extractCaptions( String line ) {
        String[] parts = line.split("\\|") ;
        captionLHS2RHS = parts[0].trim() ;
        captionRHS2LHS = parts[1].trim() ;
    }

    private void createDataStructures() {

        List<String> keywords = new ArrayList<String>( rawData.keySet() ) ;

        for( String keyword : rawData.keySet() ) {

            List<String> values = rawData.get( keyword ) ;

            for( String value : values ) {

                MCQ mcq = new MCQ( createLHS2RHSCaption( keyword ) ) ;
                mcq.addOption( new MCQOption( value, true ) ) ;
                addRandomWrongValuesOptions( mcq, values ) ;
                mcqs.add( mcq ) ;

                mcq = new MCQ( createRHS2LHSCaption( value ) ) ;
                mcq.addOption( new MCQOption( keyword, true ) ) ;
                addRandomWrongKeywordOptions( mcq, keywords ) ;
                mcqs.add( mcq ) ;

            }
        }
    }

    private String createLHS2RHSCaption( String lhs ) {
        return captionLHS2RHS.replaceAll( "\\{lhs}", "**"+lhs.trim()+"**" )  ;
    }

    private String createRHS2LHSCaption( String rhs ) {
        return captionRHS2LHS.replaceAll( "\\{rhs}", "**" + rhs + "**" )  ;
    }

    private void addRandomWrongValuesOptions( MCQ mcq, List<String> meanings ) {

        while( mcq.getNumOptions() < MAX_OPTIONS ) {

            int randomIdx = ThreadLocalRandom.current().nextInt( 0, allOptions.size() ) ;
            String randomAnswer = allOptions.get( randomIdx ) ;

            if( !meanings.contains( randomAnswer ) ) {
                if( !mcq.containsOption( randomAnswer ) ) {
                    mcq.addOption( new MCQOption( randomAnswer ) ) ;
                }
            }
        }
    }

    private void addRandomWrongKeywordOptions( MCQ mcq, List<String> keywords ) {

        while( mcq.getNumOptions() < MAX_OPTIONS ) {

            int randomIdx = ThreadLocalRandom.current().nextInt( 0, keywords.size() ) ;
            String randomAnswer = keywords.get( randomIdx ) ;

            if( !mcq.containsOption( randomAnswer ) ) {
                mcq.addOption( new MCQOption( randomAnswer ) ) ;
            }
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

        String input = "Identify the animal belonging to phylum {lhs} | Match the phylum of {rhs}\n" +
                "Porifera        = Sycon, Bathsponge\n" +
                "Cnidaria        = Coral, Sea Anemone, Jellyfish, Hydra\n" +
                "Platyhelminthes = Liverfluke, Tapeworm, Planarian\n" +
                "Nematoda        = Hookworm, Ascaris, Eelworms, Roundworm\n" +
                "Annelida        = Leech, Earthworm, Nereis\n" +
                "Arthropoda      = Crayfish, Crab, Millipede, Centipede, Scorpion, Spider, Beetle, Butterfly\n" +
                "Mollusca        = Mussel, Chiton, Loligo, Snail, Octopus, Slug, Cuttlefish\n" +
                "Echinodermata   = Starfish, Brittle-star, Sea-urchin, Sea-cucumber" ;

        MultiChoiceGroupAutoCreator creator = new MultiChoiceGroupAutoCreator( input ) ;
        System.out.println( creator.createNote() ) ;
    }
}
