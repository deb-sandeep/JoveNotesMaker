package com.sandy.jnmaker.poc;

import java.util.regex.Matcher ;
import java.util.regex.Pattern ;

import org.apache.log4j.Logger ;

public class FIBAutoExtract {

    private static final Logger logger = Logger.getLogger( FIBAutoExtract.class ) ;
    
    public static void main( String[] args ) throws Exception {
        new FIBAutoExtract().runPOC() ;
    }
    
    public void runPOC() throws Exception {
        
        Pattern pattern = Pattern.compile( getFIBPattern(), Pattern.DOTALL ) ;
        String  input   = getFIBString() ;
        Matcher matcher = pattern.matcher( input ) ;
        
        int blankIndex = 0 ;
        int stringMark = 0 ;

        StringBuilder questionStr = new StringBuilder( "@fib \"" ) ;
        StringBuilder answerStr   = new StringBuilder() ;

        while( matcher.find() ) {
            
            int matchStart = matcher.start() ;
            String matchString = matcher.group( 0 ) ;
            String blankContent = matcher.group( 1 ) ;
            
            questionStr.append( input.subSequence( stringMark, matchStart ) ) ;
            questionStr.append( "{" + blankIndex + "}" ) ;
            
            answerStr.append( "\"" + blankContent + "\"\n" ) ;
            
            stringMark = matchStart + matchString.length() ;
            blankIndex++ ;
        }
        
        if( stringMark > 0 && stringMark < input.length() ) {
            questionStr.append( input.subSequence( stringMark, input.length() ) ) ;
        }
        questionStr.append( "\"" ) ;
        
        logger.debug( questionStr ) ;
        logger.debug( answerStr ) ;
        logger.debug( "Blanks found = " + (blankIndex>0) ) ;
    }
    
    String getFIBString() {
        //return "_Bronze_ is harder and more ductile than _copper_ and is, therefore, more suitable for the manufacture of tools and weapons." ;
        return "This is a line without auto marked FIB" ;
    }
    
    String getFIBPattern() {
        return "_([^_]+)_" ;
    }
}
