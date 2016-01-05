package com.sandy.jnmaker.junit;

import static org.junit.Assert.* ;

import org.junit.Test ;

public class QuoteReplaceTest {

    @Test
    public void quoteEscape() {
        assertEquals( "This \\\"is\\\" a quoted text",  
                "This \"is\" a quoted text".replaceAll( "\\\"", "\\\\\"" ) ) ;
    }
    
    @Test
    public void slashEscape() {
        assertEquals( "\\\\gamma",  
                      "\\gamma".replaceAll( "\\\\", "\\\\\\\\" ) ) ;
    }
}
