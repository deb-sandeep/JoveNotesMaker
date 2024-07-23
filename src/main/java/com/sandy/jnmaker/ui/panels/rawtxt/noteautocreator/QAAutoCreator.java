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

        String[] parts = extractQuestionAndAnswer() ;

        if( parts.length == 2 ) {

            String questionText = parts[0] ;
            String answerText = parts[1] ;

            String[] answerParts = answerText.split( "\\R", 2 ) ;
            String ansLHSImg = null ;

            if( answerParts.length > 1 ) {
                if( answerParts[0].startsWith( "<{{@img " ) &&
                    answerParts[0].endsWith( "}}" ) ) {
                    ansLHSImg = answerParts[0].substring( 1 ) ;
                    answerText = answerParts[1] ;
                }
            }

            String retVal = "@qa \"" + formatText( questionText ) + "\"\n" ;
            if( ansLHSImg != null ) {
                retVal += "\"" + ansLHSImg + "\"\n" ;
            }
            retVal += "\"" + formatText( answerText ) + "\"\n\n" ;

            return  retVal ;
        }
        else {
            log.info( "Auto generation for QA failed. " +
                    "Q and A could not be deduced." ) ;
        }

        return null ;
    }
    
    private String[] extractQuestionAndAnswer() {
        if( input.contains( "\n~~\n" ) ) {
            return input.split( "\n~~\n" ) ;
        }
        return input.split( "\\R", 2 ) ;
    }
}
