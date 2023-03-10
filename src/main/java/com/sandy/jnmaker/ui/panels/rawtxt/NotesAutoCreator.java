package com.sandy.jnmaker.ui.panels.rawtxt;

import static com.sandy.jnmaker.util.NoteTextUtil.escapeQuotes ;
import static com.sandy.jnmaker.util.NoteTextUtil.formatText ;

import java.util.regex.Matcher ;
import java.util.regex.Pattern ;

public class NotesAutoCreator {

    public static String autoCreateFIBNote( String input ) {
        
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
            questionStr.append( "{" + blankIndex + "}" ) ;
            
            answerStr.append( "\"" + escapeQuotes( blankContent ) + "\"\n" ) ;
            
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
    
    public static String autoCreateDefinitionNote( String input ) {
        
        String aoiPattern = "_([^_]+)_" ;
        Pattern pattern = Pattern.compile( aoiPattern, Pattern.DOTALL ) ;
        Matcher matcher = pattern.matcher( input ) ;
        
        int stringMark = 0 ;
        
        String retVal = null ;
        String termStr = null ;
        String definitionStr = null ;
        
        if( matcher.find() ) {
            
            int matchStart = matcher.start() ;
            String matchString = matcher.group( 0 ) ;

            termStr = matcher.group( 1 ) ;
            stringMark = matchStart + matchString.length() ;
        }
        
        if( stringMark > 0 && stringMark < input.length() ) {
            definitionStr = input.subSequence( stringMark, input.length() )
                                 .toString()
                                 .trim() ;
        }
        
        if( termStr != null && definitionStr != null ) {
            retVal = "@definition \"" + escapeQuotes( termStr ) + "\"\n" ;
            retVal += "\"" + formatText( definitionStr, true ) + "\"\n" ;
            retVal += "\n" ;
        }
        
        return retVal ;
    }
    
    public static String autoCreateQANote( String input ) {

        String[] parts = input.split( "\\R", 2 ) ;
        String questionText = parts[0] ;
        String answerText = parts[1] ;
        
        StringBuilder buffer = new StringBuilder() ;
        buffer.append( "@qa \"" )
              .append( formatText( questionText ) )
              .append( "\"\n" ) ;
        buffer.append( "\"" )
              .append( formatText( answerText ) ) 
              .append( "\"\n" )
              .append( "\n" ) ; 
        
        return buffer.toString() ;
    }
}
