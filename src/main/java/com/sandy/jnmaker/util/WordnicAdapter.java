package com.sandy.jnmaker.util ;

import java.net.URLEncoder;
import java.util.ArrayList ;
import java.util.List ;

import com.wordnik.client.api.WordApi ;
import com.wordnik.client.model.Definition ;
import com.wordnik.client.model.TextPron;

public class WordnicAdapter {

    private String apiKey = null ;
    
    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey( String apiKey ) {
        this.apiKey = apiKey;
    }

    public List<String> getDefinitions( String word ) 
            throws Exception {
        return getDefinitions( word, 3 ) ;
    }
    
    private List<String> getDefinitions( String word, int maxNumDefs ) 
            throws Exception {
        
        List<String> definitions = new ArrayList<>() ;
        
        WordApi api = new WordApi() ;
        api.getInvoker().addDefaultHeader( "api_key", getApiKey() ) ;
        
        List<Definition> defs = api.getDefinitions(
                URLEncoder.encode( word, "UTF-8" ),     
                null,     
                "all",    
                maxNumDefs,        
                "false",  
                "true",   
                "false"   
        ) ;
        
        for( Definition definition : defs ) {
            definitions.add( definition.getText() ) ;
        }
        
        return definitions ;
    }
    
    public String getPronounciation( String word ) throws Exception {
        
        WordApi api = new WordApi() ;
        api.getInvoker().addDefaultHeader( "api_key", getApiKey() ) ; 
        
        List<TextPron> pronounciations = api.getTextPronunciations( 
                URLEncoder.encode( word, "UTF-8" ), null, null, "true", 1 ) ;
        if( !pronounciations.isEmpty() ) {
            return pronounciations.get(0).getRaw() ;
        }
        return null ;
    }
}
