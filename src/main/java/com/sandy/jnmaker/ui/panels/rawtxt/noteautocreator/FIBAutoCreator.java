package com.sandy.jnmaker.ui.panels.rawtxt.noteautocreator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sandy.jnmaker.util.NoteTextUtil.escapeQuotes;
import static com.sandy.jnmaker.util.NoteTextUtil.formatText;

public class FIBAutoCreator {

    private final String input ;

    public FIBAutoCreator( String input ) {
        this.input = input ;
    }

    public String createNote() {

        String fibPattern = "_([^_]+)_" ;
        Pattern pattern = Pattern.compile( fibPattern, Pattern.DOTALL ) ;
        Matcher matcher = pattern.matcher( input ) ;

        int blankIndex = 0 ;
        int stringMark = 0 ;

        String retVal = null ;
        StringBuilder questionStr = new StringBuilder() ;
        StringBuilder answerStr   = new StringBuilder() ;

        while( matcher.find() ) {

            int matchStart = matcher.start() ;
            String matchString = matcher.group( 0 ) ;
            String blankContent = matcher.group( 1 ) ;

            questionStr.append( input.subSequence( stringMark, matchStart ) ) ;
            questionStr.append( "{" ).append( blankIndex ).append( "}" );

            answerStr.append( "\"" ).append( escapeQuotes( blankContent ) ).append( "\"\n" );

            stringMark = matchStart + matchString.length() ;
            blankIndex++ ;
        }

        if( stringMark > 0 && stringMark < input.length() ) {
            questionStr.append( input.subSequence( stringMark, input.length() ) ) ;
        }

        if( blankIndex > 0 ) {
            retVal = "@fib \"" + formatText( questionStr.toString(), true ) + "\"\n" ;
            retVal += answerStr.toString() ;
            retVal += "\n" ;
        }

        return retVal ;
    }
}
