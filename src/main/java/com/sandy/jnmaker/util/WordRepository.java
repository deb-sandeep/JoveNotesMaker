package com.sandy.jnmaker.util;

import java.util.ArrayList ;
import java.util.Collection ;
import java.util.Collections ;
import java.util.Comparator ;
import java.util.HashSet ;
import java.util.List ;
import java.util.Set ;
import java.util.SortedSet ;
import java.util.TreeSet ;

import com.sandy.common.util.FuzzyScore ;
import com.sandy.common.util.StringUtil ;

public class WordRepository {
    
    public interface WordSource {
        String getTextForWordRepository() ;
    }

    private SortedSet<String> wordList = new TreeSet<>() ;
    
    private FuzzyScore comparator = new FuzzyScore() ; 
    private List<WordSource> wordSources = new ArrayList<WordRepository.WordSource>() ;
    private Object opLock = new Object() ;
    
    public WordRepository() {
        Thread daemon = new Thread( "WordRepository daemon" ) {
            public void run() {
                StringBuilder buffer  = new StringBuilder() ;
                String        srcText = null ;
                while( true ) {
                    try {
                        buffer.delete( 0, buffer.length() ) ;
                        for( WordSource src : wordSources ) {
                            srcText = src.getTextForWordRepository() ;
                            if( StringUtil.isNotEmptyOrNull( srcText ) ) {
                                buffer.append( srcText ) ;
                            }
                        }
                        updateWordList( getQualifedWords( buffer.toString() ) ) ;
                        sleep( 2500 ) ;
                    }
                    catch( Exception e ) {
                        // This should rarely occuur and if so, we just ignore.
                        e.printStackTrace() ;
                    }
                }
            }
        } ;
        daemon.setDaemon( true ) ;
        daemon.start() ;
    }
    
    public void addWordSource( WordSource source ) {
        if( !wordSources.contains( source ) ) {
            synchronized( opLock ) {
                wordSources.add( source ) ;
            }
        }
    }   
    
    public void offer( String line ) {
        updateWordList( getQualifedWords( line ) ) ;
    }
    
    public Collection<String> getWordList() {
        return this.wordList ;
    }
    
    public List<String> getFuzzyMatches( final String query, int limit ) {
        
        List<String> matches = null ;
        synchronized( opLock ) {
            matches = new ArrayList<String>( wordList ) ;
        }
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
        synchronized( opLock ) {
            this.wordList.clear() ;
        }
    }
    
    private Set<String> getQualifedWords( String text ) {
        
        Set<String> wordList = new HashSet<String>() ;
        if( StringUtil.isNotEmptyOrNull( text ) ) {
            String[] words = text.split( "\\b+" ) ;
            for( String word : words ) {
                word = word.trim() ;
                if( word.length() > 4 ) {
                    if( word.matches( "^([a-z]|[A-Z]|[0-9]|[_@.])+$" ) ) {
                        wordList.add( word ) ;
                    }
                }
            }
        }
        return wordList ;
    }
    
    private void updateWordList( Collection<String> list ) {
        if( !list.isEmpty() ) {
            synchronized( opLock ) {
                for( String word : list ) {
                    wordList.add( word ) ;
                }
            }
        }
    }
}
