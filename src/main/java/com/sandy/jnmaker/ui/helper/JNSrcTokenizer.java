package com.sandy.jnmaker.ui.helper;

public class JNSrcTokenizer {

    public static enum TokenType { KEYWORD, STRING, NUMBER, UNKNOWN } ;
    
    private static final String[] KEYWORDS = {
            "@definition",
            "@qa",
            "@fib",
            "@event",
            "@true_false",
            "@wm",
            "@spellbee",
            "@skip_generation",
            "@skip_generation_in_production",
            "subject",
            "chapterNumber",
            "chapterName",
            "true",
            "false"
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
                if( lookaheadPos >= this.contentLen ) {
                    break ;
                }
                else {
                    token = extractToken( lookaheadPos ) ;
                    this.curPos = token.end+1 ;
                    return token ;
                }
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
        else if( Character.isDigit( ch ) || ( ch == '.' ) ) {
            token = extractNumber( pos ) ;
        }
        else {
            token = extractKeyword( pos ) ;
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
        
        Token token = new Token( TokenType.NUMBER, pos, 0, null ) ;
        StringBuilder buffer = new StringBuilder() ;
        
        buffer.append( this.content.charAt( pos ) ) ;
        int toPos = pos+1 ;
        
        while( toPos < this.contentLen ) {
            char ch = this.content.charAt( toPos ) ;
            if( Character.isDigit( ch ) || ( ch == '.' )) {
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
    
    private Token extractKeyword( int pos ) {
        
        for( String keyword : KEYWORDS ) {
            if( this.content.indexOf( keyword, pos ) == pos ) {
                Token token = null ;
                token = new Token( TokenType.KEYWORD, 
                                   pos, pos + keyword.length()-1, keyword ) ; 
                return token ;
            }
        }
        return null ;
    }
}
