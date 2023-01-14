package com.sandy.jnmaker.util;

import java.io.File ;
import java.io.IOException ;
import java.util.HashSet ;
import java.util.Set ;

import org.apache.commons.io.FileUtils ;

import com.sandy.common.util.StringUtil ;

public class SkipWords {

    private static SkipWords instance = null ;
    
    private static final String RES_PATH = "fib-skip-words.txt" ;
    
    private static SkipWords instance() {
        if( instance == null ) {
            instance = new SkipWords() ;
        }
        return instance ;
    }
    
    public static boolean isSkipWord( String word ) {
        return instance().skipWords.contains( word.trim().toLowerCase() ) ;
    }
    
    public static void addSkipWord( String word ) {
        
        if( StringUtil.isNotEmptyOrNull( word ) ) {
            
            word = word.trim().toLowerCase() ;
            
            Set<String> skipWords = instance().skipWords ;
            
            if( !skipWords.contains( word ) ) {
                skipWords.add( word.toLowerCase().trim() ) ;
                instance().persistSkipWords() ;
            }
        }
    }
    
    private Set<String> skipWords = new HashSet<>() ;
    
    private File persistFile = null ;
    
    private SkipWords() {
        
        try {
            persistFile = new File( ObjectRepository.getAppConfig()
                                                    .getWorkspaceDir(),
                                    RES_PATH ) ;
            
            FileUtils.readLines( persistFile ).forEach( w -> {
                skipWords.add( w.toLowerCase() ) ;
            } ) ;
        }
        catch( Exception e ) {
            e.printStackTrace() ;
        }
    }

    private void persistSkipWords() {

        try {
            FileUtils.writeLines( persistFile, skipWords ) ;
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }
}
