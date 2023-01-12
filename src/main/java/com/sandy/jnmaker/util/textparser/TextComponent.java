package com.sandy.jnmaker.util.textparser;

import lombok.Data ;

@Data
public class TextComponent {

    public static enum Type {
        SPACE, WORD, PUNCTUATION, UNKNOWN
    } ;
    
    private TextComponent prev = null ;
    private TextComponent next = null ;
    
    private int start = -1 ;
    private int end = -1 ;
    private StringBuilder value = new StringBuilder() ;
    
    private Type type = null ;
    private ParsedText parsedText = null ;
    
    TextComponent( Type type, int start ) {
        this.type = type ;
        this.start = start ;
        this.end = start ;
    }
    
    void addChar( char ch ) {
        value.append( ch ) ;
        this.end++ ;
    }
    
    public boolean isCurrentToken() {
        return parsedText.getCurrentComponent() == this ;
    }
}
