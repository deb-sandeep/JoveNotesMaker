package com.sandy.jnmaker.ui.panels.rawtxt.noteautocreator;

import com.sandy.common.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static com.sandy.common.util.StringUtil.isNotEmptyOrNull;
import static com.sandy.jnmaker.util.NoteTextUtil.formatText;

public class MatchingAutoCreator {

    private static final Logger log = Logger.getLogger( MatchingAutoCreator.class ) ;

    private final String input ;

    private final List<String[]> candidates = new ArrayList<>() ;
    private String caption = "Match the following" ;
    private String forwardCaption = caption ;
    private String reverseCaption = caption ;
    private int maxLeftLen = 0 ;

    public MatchingAutoCreator( String input ) {
        this.input = input ;
        parseMatchingInput() ;
    }

    private void parseMatchingInput() {

        String line = null ;
        BufferedReader br = new BufferedReader( new StringReader( input ) ) ;
        boolean isFirstLine = true ;

        try {
            while( ( line = br.readLine() ) != null ) {
                line = line.trim() ;

                if( StringUtil.isEmptyOrNull( line ) ) continue ;

                if( isFirstLine ) {
                    if( !line.contains( "=" ) ) {
                        gatherCaptions( line ) ;
                        isFirstLine = false ;
                        continue ;
                    }
                }

                if( line.contains( "=" ) ) {
                    String[] parts = line.split( "=" ) ;
                    String[] candidate = new String[2] ;

                    candidate[0] = parts[0].trim() ;
                    candidate[1] = parts[1].trim() ;

                    maxLeftLen = Math.max( candidate[0].length(), maxLeftLen );

                    candidates.add( candidate ) ;
                }
            }
        }
        catch( IOException e ) {
            log.error( "Unanticipated error.", e ) ;
        }
    }
    
    private void gatherCaptions( String line ) {
        String[] captions = line.split( "\\|" ) ;
        if( captions.length > 0 ) {
            if( isNotEmptyOrNull( captions[0] ) ) {
                this.caption = captions[0].trim() ;
            }
            
            if( captions.length > 1 ) {
                if( isNotEmptyOrNull( captions[1] ) ) {
                    this.forwardCaption = captions[1].trim() ;
                }
            }
            
            if( captions.length > 2 ) {
                if( isNotEmptyOrNull( captions[2] ) ) {
                    this.reverseCaption = captions[2].trim() ;
                }
            }
        }
    }

    public String createNote() {

        StringBuilder sb = new StringBuilder() ;

        sb.append( "@match " )
                .append( "\"" )
                .append( caption )
                .append( "\" {\n\n" );

        for( String[] candidate : candidates ) {
            sb.append( "    " )
                    .append( getLeftCandidateStr( candidate[0] ) )
                    .append( " = \"" )
                    .append( candidate[1] )
                    .append( "\"\n" );
        }

        sb.append( "\n" )
                .append( "    @mcq_config {\n" )
                .append( "        @forwardCaption \"" + this.forwardCaption + "\"\n" )
                .append( "        @reverseCaption \"" + this.reverseCaption + "\"\n" )
                .append( "        @numOptionsToShow 4\n" )
                .append( "        @numOptionsPerRow 2\n" )
                .append( "    }\n" )
                .append( "}\n\n" ) ;

        return sb.toString() ;
    }

    private String getLeftCandidateStr( String candidate ) {
        String str = "\"" + candidate + "\"" ;
        return StringUtils.rightPad( str, maxLeftLen+2 ) ;
    }
}
