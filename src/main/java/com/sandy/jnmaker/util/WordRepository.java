package com.sandy.jnmaker.util;

import java.util.ArrayList ;
import java.util.Collection ;
import java.util.Collections ;
import java.util.Comparator ;
import java.util.List ;
import java.util.SortedSet ;
import java.util.TreeSet ;

import com.sandy.common.util.FuzzyScore ;
import com.sandy.common.util.StringUtil ;

public class WordRepository {

    private SortedSet<String> wordList = new TreeSet<>() ;
    
    private FuzzyScore comparator = new FuzzyScore() ; 
    
    public void offer( String line ) {
        if( StringUtil.isNotEmptyOrNull( line ) ) {
            String[] words = line.split( "\\b+" ) ;
            for( String word : words ) {
                word = word.trim() ;
                if( word.length() > 4 ) {
                    if( word.matches( "^([a-z]|[A-Z]|[0-9]|[_@.])+$" ) ) {
                        wordList.add( word.trim() ) ;
                    }
                }
            }
        }
    }
    
    public Collection<String> getWordList() {
        return this.wordList ;
    }
    
    public List<String> getFuzzyMatches( final String query, int limit ) {
        
        List<String> matches = new ArrayList<String>( wordList ) ;
        Collections.sort( matches, new Comparator<String>() {
            @Override public int compare( String o1, String o2 ) {
                return comparator.fuzzyScore( o2, query ) - 
                       comparator.fuzzyScore( o1, query ) ;
            }
        } ) ;
        
        if( limit > matches.size() ) limit = matches.size() ;
        return matches.subList( 0, limit ) ;
    }
    
    public List<String> getFuzzyMatches( final String query ) {
        return getFuzzyMatches( query, Integer.MAX_VALUE ) ;
    }
    
    public void clear() {
        this.wordList.clear() ;
    }
}
