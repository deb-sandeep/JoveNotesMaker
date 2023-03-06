package com.sandy.jnmaker.util.textparser;

import javax.swing.JTextArea ;

import com.sandy.jnmaker.util.textparser.TextComponent.Type ;

public class TextParser {

    public static ParsedText parseText( JTextArea textArea ) {
        return parseText( textArea.getText(), textArea.getCaretPosition() ) ;
    }
    
    public static ParsedText parseText( String text, int currentPos ) {
        
        ParsedText parsedText = new ParsedText() ;
        
        if( text != null ) {
            
            TextComponent component = null ;
            for( int i=0; i<text.length(); i++ ) {
                
                char ch = text.charAt( i ) ;
                Type type = getCharType( ch ) ;
                
                assert type != Type.UNKNOWN ;
                
                if( component == null || component.getType() != type ) {
                    component = new TextComponent( type, i ) ;
                    parsedText.addComponent( component ) ;
                }
                
                if( i == currentPos ) {
                    parsedText.setCurrentComponent( component ) ;
                }

                component.addChar( ch ) ;
            }
        }
        return parsedText ;
    }
    
    private static Type getCharType( char ch ) {
        if( Character.isWhitespace( ch ) ) 
            return Type.SPACE ;
        else if( isWordChar( ch ) )
            return Type.WORD ;
        else if( isPunctuationChar( ch ) )
            return Type.PUNCTUATION ;
        
        return Type.UNKNOWN ;
    }

    private static boolean isWordChar( char ch ) {
        
        char[] validChars = { '-', '_' } ;
        
        if( ch >= 'a' && ch <= 'z' ) { return true ; }
        if( ch >= 'A' && ch <= 'Z' ) { return true ; }
        if( ch >= '0' && ch <= '9' ) { return true ; }
        
        return isValidChar( validChars, ch ) ; 
    }
    
    private static boolean isPunctuationChar( char ch ) {
        
        char[] validChars = { '!', ',', ';', '.', '?', '\\', '/', '\"', ':' } ;
        return isValidChar( validChars, ch ) ; 
    }
    
    private static boolean isValidChar( char[] validChars, char ch ) {
        
        for( int i=0; i<validChars.length; i++ ) {
            if( ch == validChars[i] ) {
                return true ;
            }
        }
        return false ;
    }
}
