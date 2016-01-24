package com.sandy.jnmaker.poc;

import java.net.URL ;
import java.util.regex.Matcher ;
import java.util.regex.Pattern ;

import org.apache.commons.io.IOUtils ;
import org.apache.log4j.Logger ;

import com.sandy.common.util.ReflectionUtil ;

public class RegexPOC {

    private static final Logger logger = Logger.getLogger( RegexPOC.class ) ;
    
    public static void main( String[] args ) throws Exception {
        new RegexPOC().runPOC() ;
    }
    
    private void runPOC() throws Exception {
        
        Pattern pattern = Pattern.compile( getConfigParamPattern(), Pattern.DOTALL ) ;
        String  input   = getConfigParamString() ;
        Matcher matcher = pattern.matcher( input ) ;
        
        while( matcher.find() ) {
            logger.debug( "Found a match @position = " + matcher.start() ) ;
            for( int i=0; i<matcher.groupCount()+1; i++ ) {
                logger.debug( "Group[" + i + "] = " + matcher.group( i ) );
            }
        }
    }
    
    String getKeywordPattern() {
        return "@\\b(definition|qa|multi_choice)\\b" ;
    }
    
    String getStringPattern() {
        return "'([^\\\\']+|\\\\([btnfr\"'\\\\]|[0-3]?[0-7]{1,2}|u[0-9a-fA-F]{4}))*'|\"([^\\\\\"]+|\\\\([btnfr\"'\\\\]|[0-3]?[0-7]{1,2}|u[0-9a-fA-F]{4}))*\"" ;
    }
    
    String getStringPattern1() {
        return "\\\"[^\\\"]*\\\"" ;
    }
    
    String getBoldPattern() {
        return "\\*\\*.*?\\*\\*" ;
    }
    
    String getConfigParamPattern() {
        return "^@([a-zA-Z0-9_]+)\\s*=?\\s*(.*)$" ;
    }
    
    String getStringForMatching() throws Exception {
        URL url = ReflectionUtil.getResource( RegexPOC.class, "sample.jn" ) ;
        return IOUtils.toString( url ) ;
    }
    
    String getConfigParamString() {
        return "@test = 20  " ;
    }
}
