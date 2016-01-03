package com.sandy.jnmaker.junit.util;

import static org.hamcrest.Matchers.* ;
import static org.junit.Assert.* ;

import java.util.List ;

import org.junit.Before ;
import org.junit.Test ;

import com.sandy.jnmaker.util.WordRepository ;

public class WordRepositoryTest {

    private WordRepository repository = null ;
    
    @Before 
    public void setUp() {
        repository = new WordRepository() ;
    }
    
    @Test public void offer() {
        
        repository.offer( "A quick\nbrown\r\nfox\t jumps over a lazy dog" ) ;
        assertThat( repository.getWordList(), hasItem( "quick" ) ) ;
        assertThat( repository.getWordList(), hasItem( "brown" ) ) ;
        assertThat( repository.getWordList(), hasItem( "jumps" ) ) ;
        
        assertThat( repository.getWordList(), not( hasItem( "A" ) ) ) ;
        assertThat( repository.getWordList(), not( hasItem( "fox" ) ) ) ;
        assertThat( repository.getWordList(), not( hasItem( "over" ) ) ) ;
        assertThat( repository.getWordList(), not( hasItem( "lazy" ) ) ) ;
        assertThat( repository.getWordList(), not( hasItem( "dog" ) ) ) ;
    }
    
    @Test public void fuzzyMatches() {
        
        repository.offer( "A quick\nbrown\r\nfox\t jumps over a lazy dog" ) ;
        List<String> matches = repository.getFuzzyMatches( "bn" ) ;
        
        assertEquals( "brown", matches.get( 0 ) ) ;
    }
}
