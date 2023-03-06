package com.sandy.jnmaker.util;

import org.apache.commons.lang.WordUtils ;

import com.sandy.common.util.StringUtil ;

public class NoteTextUtil {

    public static String escapeQuotes( String input ) {
        return input.replaceAll( "\\\"", "\\\\\"" ) ;
    }

    public static String formatText( String input, boolean escapeQuote ) {
        
        StringBuilder buffer = new StringBuilder() ;
        String[] lines = input.split( "\n" ) ;
        
        for( int i=0; i<lines.length; i++ ) {
            String line = lines[i] ;
            if( !StringUtil.isEmptyOrNull( line ) ) {
                if( escapeQuote ) {
                    line = escapeQuotes( line ) ;
                }
                line = WordUtils.wrap( line, 80, "\n", false ) ;
                buffer.append( line ) ;
                
                if( i < (lines.length - 1) ) {
                    buffer.append( "  \n" ) ;
                }
            }
            else {
                buffer.append( "\n" ) ;
            }
        }
        return buffer.toString() ;
    }
    
    public static String formatText( String input ) {
        return formatText( input, true ) ;
    }
}
