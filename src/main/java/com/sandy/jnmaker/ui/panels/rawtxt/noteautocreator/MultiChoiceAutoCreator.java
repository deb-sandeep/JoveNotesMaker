package com.sandy.jnmaker.ui.panels.rawtxt.noteautocreator;

import com.sandy.common.util.StringUtil;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class MultiChoiceAutoCreator {

    private static final Logger log = Logger.getLogger( MultiChoiceAutoCreator.class ) ;

    private final String input ;

    private String caption = "" ;
    private final List<String[]> candidates = new ArrayList<>() ;

    public MultiChoiceAutoCreator( String input ) {
        this.input = input ;
        parseMatchingInput() ;
    }

    private void parseMatchingInput() {

        String line;
        BufferedReader br = new BufferedReader( new StringReader( input ) ) ;
        boolean isFirstLine = true ;

        try {
            while( ( line = br.readLine() ) != null ) {
                line = line.trim() ;

                if( StringUtil.isEmptyOrNull( line ) ) continue ;

                if( isFirstLine ) {
                    caption = line ;
                    isFirstLine = false ;
                    continue ;
                }

                if( line.startsWith( "*" ) ) {
                    line = line.substring( 1 ).trim() ;
                }

                boolean isCorrect = line.endsWith( "$" ) ;
                if( isCorrect ) {
                    line = line.substring( 0, line.length()-1 ) ;
                    line = line.trim() ;
                }

                String[] candidate = new String[2] ;

                candidate[0] = line ;
                candidate[1] = isCorrect ? " correct" : "" ;

                candidates.add( candidate ) ;
            }
        }
        catch( IOException e ) {
            log.error( "Unanticipated error.", e ) ;
        }
    }

    public String createNote() {

        StringBuilder sb = new StringBuilder() ;

        sb.append( "@multi_choice " )
          .append( "\"" )
          .append( caption )
          .append( "\" {\n" )
          .append( "    @options {\n" ) ;

        for( String[] candidate : candidates ) {
            sb.append( "      \"" )
              .append( candidate[0] )
              .append( "\"" )
              .append( candidate[1] )
              .append( ",\n" );
        }

        sb.deleteCharAt( sb.length()-2 ) ;

        sb.append( "    }\n" )
          .append( "    @numOptionsPerRow 1\n" )
          .append( "}\n\n" ) ;

        return sb.toString() ;
    }
}
