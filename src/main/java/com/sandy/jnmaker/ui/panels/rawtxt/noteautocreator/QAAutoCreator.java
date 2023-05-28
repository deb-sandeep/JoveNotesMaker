package com.sandy.jnmaker.ui.panels.rawtxt.noteautocreator;

import org.apache.log4j.Logger;

import static com.sandy.jnmaker.util.NoteTextUtil.formatText;

public class QAAutoCreator {

    private static final Logger log = Logger.getLogger( QAAutoCreator.class ) ;

    private final String input ;

    public QAAutoCreator( String input ) {
        this.input = input ;
    }

    public String createNote() {

        String[] parts = input.split( "\\R", 2 ) ;

        if( parts.length == 2 ) {

            String questionText = parts[0] ;
            String answerText = parts[1] ;

            return "@qa \"" +
                    formatText( questionText ) +
                    "\"\n" +
                    "\"" +
                    formatText( answerText ) +
                    "\"\n" +
                    "\n";
        }
        else {
            log.info( "Auto generation for QA failed. " +
                    "Q and A could not be deduced." ) ;
        }

        return null ;
    }
}
