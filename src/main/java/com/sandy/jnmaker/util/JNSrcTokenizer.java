package com.sandy.jnmaker.util;

public class JNSrcTokenizer {

    public static enum TokenType { 
        KEYWORD, 
        NESTED_KEYWORDS,
        STRING, 
        INT, 
        PUNCTUATION,
        COMMENT,
        UNKNOWN,
        MD_BOLD,
        MD_ITALIC,
        JN_MARKER,
        JN_KEYWORD
    } ;
    
    private static final String[] KEYWORDS = {
            "@character" ,
            "@chem_compound" ,
            "@chem_equation" ,
            "@definition" ,
            "@equation" ,
            "@event" ,
            "@explanation" ,
            "@fib" ,
            "@forwardCaption" ,
            "@image_label" ,
            "@match" ,
            "@mcq_config" ,
            "@multi_choice" ,
            "@numOptionsPerRow" ,
            "@numOptionsToShow" ,
            "@options" ,
            "@qa" ,
            "@reverseCaption" ,
            "@rtc" ,
            "@skip_generation_in_production" ,
            "@skip_generation" ,
            "@spellbee" ,
            "@exercise_bank" ,
            "@exercise" ,
            "@tn" ,
            "@true_false" ,
            "@wm",
    } ;
    
    private static final String[] PUNCTUATIONS = {
            ",",
            "=",
            ">",
            "{",
            "}",
            "."
    } ;
    
    private static final String[] NESTED_KEYWORDS = {
            "chapterName" ,
            "chapterNumber" ,
            "cmap" ,
            "context" ,
            "correct" ,
            "hide" ,
            "script" ,
            "script_expressions" ,
            "skip_reverse_question" ,
            "subject" ,
            "where" ,
            "true" ,
            "false",
            "imageName",
            "marks",
            "hints",
            "answer"
    } ;

    public static class Token {
        
        public String    token     = null ;
        public int       start     = -1 ;
        public int       end       = -1 ;
        public TokenType tokenType = null ;
        
        Token ( TokenType type, int start, int end, String token ) {
            this.token     = token ;
            this.start     = start ;
            this.end       = end ;
            this.tokenType = type ;
        }
        
        public String toString() {
            return "Token [type=" + this.tokenType.toString() + 
                   ", start = " + this.start + 
                   ", end = " + this.end + 
                   ", content = " + this.token + 
                   "]" ;
        }
    }
    
    @SuppressWarnings( "serial" )
    private class EOSException extends Exception {}
    
    private String content    = null ;
    private int    curPos     = 0 ;
    private int    contentLen = 0 ;
    
    public JNSrcTokenizer( String content ) {
        this.content = content ;
        this.contentLen = ( content != null ) ? content.length() : 0 ; 
    }

    public int getContentLength() {
        return this.contentLen ;
    }
    
    public Token getNextToken() {
        
        int   lookaheadPos = this.curPos ;
        Token token        = null ;
        
        if( lookaheadPos >= this.contentLen ) {
            return null ;
        }
        
        try {
            while( lookaheadPos < this.contentLen ) {
                lookaheadPos = skipWhiteSpaces( lookaheadPos ) ;
                token = extractToken( lookaheadPos ) ;
                this.curPos = token.end+1 ;
                return token ;
            }
        }
        catch( EOSException e ) {
        }
        return null ;
    }
    
    private int skipWhiteSpaces( int fromPos ) throws EOSException {
        
        int toPos = fromPos ;
        while( toPos < this.contentLen ) {
            char ch = this.content.charAt( toPos ) ;
            if( Character.isWhitespace( ch ) ) {
                toPos++ ;
            }
            else {
                return toPos ;
            }
        }
        throw new EOSException() ;
    }
    
    private Token extractToken( int pos ) {
        
        Token token = null ;
        char ch = this.content.charAt( pos ) ;
        
        if( ch == '"' ) {
            token = extractString( pos ) ;
        }
        else if( Character.isDigit( ch ) ) {
            token = extractNumber( pos ) ;
        }
        else {
            token = extractKeyword( pos ) ;
            if( token == null ) {
                token = extractNestedKeyword( pos ) ;
                if( token == null ) {
                    token = extractPunctuation( pos ) ;
                    if( token == null ) {
                        token = extractComment( pos ) ;
                    }
                }
            }
        }
        
        if( token == null ) {
            token = new Token( TokenType.UNKNOWN, pos, this.contentLen - 1,
                               this.content.substring( pos ) ) ;
        }
        
        return token ;
    }
    
    private Token extractString( int pos ) {
        
        Token token = new Token( TokenType.STRING, pos, 0, null ) ;
        StringBuilder buffer = new StringBuilder() ;
        
        buffer.append( this.content.charAt( pos ) ) ;
        
        int toPos = pos+1 ;
        boolean expectingEscapedChar = false ;
        
        while( toPos < this.contentLen ) {
            
            char ch = this.content.charAt( toPos ) ;
            buffer.append( ch ) ;
            toPos++ ;
            
            if( expectingEscapedChar ) {
                expectingEscapedChar = false ;
            }
            else if( ch == '\\' ) {
                expectingEscapedChar = true ;
            }
            else if( ch == '"' ) {
                break ;
            }
            else {
                // Do nothing
            }
        }
        
        token.end   = toPos - 1 ;
        token.token = buffer.toString() ;
        
        return token ;
    }
    
    private Token extractNumber( int pos ) {
        
        Token token = new Token( TokenType.INT, pos, 0, null ) ;
        StringBuilder buffer = new StringBuilder() ;
        
        buffer.append( this.content.charAt( pos ) ) ;
        int toPos = pos+1 ;
        
        while( toPos < this.contentLen ) {
            char ch = this.content.charAt( toPos ) ;
            if( Character.isDigit( ch ) ) {
                toPos++ ;
                buffer.append( ch ) ;
            }
            else {
                break ;
            }
        }
        
        token.end   = toPos-1 ;
        token.token = buffer.toString() ;
        
        return token ;
    }
    
    private Token extractComment( int initialPos ) {
        
        int pos = initialPos ;
        Token token = null ;
        StringBuilder buffer = new StringBuilder() ;
        
        if( pos < contentLen-1 ) {
            if( content.charAt( pos ) == '/' ) {
                
                if( content.charAt( pos+1 ) == '/' ) {

                    while( pos < contentLen && content.charAt( pos ) != '\n') {
                        buffer.append( content.charAt( pos ) ) ;
                        pos++ ;
                    }
                    
                    token = new Token( TokenType.COMMENT, initialPos, pos-1, 
                                       buffer.toString() ) ;
                }
                else if( content.charAt( pos+1 ) == '*' ) {
                    
                    while( pos < contentLen-2 ) {
                        char curChar        = content.charAt( pos ) ;
                        char nextChar       = content.charAt( pos+1 ) ;
                        char nextToNextChar = content.charAt( pos+2 ) ;
                        
                        buffer.append( curChar ) ;
                        if( nextChar == '*' && nextToNextChar == '/' ) {
                            buffer.append( nextChar ) ;
                            buffer.append( nextToNextChar ) ;
                            pos += 3 ;
                            break ;
                        }
                        else {
                            pos++ ;
                        }
                    }
                    
                    token = new Token( TokenType.COMMENT, initialPos, pos-1, 
                            buffer.toString() ) ;
                }
            }
        }
        
        return token ;
    }
    
    private Token extractKeyword( int pos ) {
        return getPredefinedToken( TokenType.KEYWORD, KEYWORDS, pos ) ;
    }
    
    private Token extractNestedKeyword( int pos ) {
        return getPredefinedToken( TokenType.NESTED_KEYWORDS, NESTED_KEYWORDS, pos ) ;
    }
    
    private Token extractPunctuation( int pos ) {
        return getPredefinedToken( TokenType.PUNCTUATION, PUNCTUATIONS, pos ) ;
    }
    
    private Token getPredefinedToken( TokenType type, String[] tokenSet, int pos ) {
        
        for( String keyword : tokenSet ) {
            if( this.content.indexOf( keyword, pos ) == pos ) {
                Token token = null ;
                token = new Token( type, pos, pos + keyword.length()-1, keyword ) ; 
                return token ;
            }
        }
        return null ;
    }
}
