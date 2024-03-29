package com.sandy.jnmaker.ui.panels.common;

import java.awt.Color ;
import java.util.regex.Matcher ;
import java.util.regex.Pattern ;

import javax.swing.JTextPane ;
import javax.swing.text.BadLocationException ;
import javax.swing.text.DefaultStyledDocument ;
import javax.swing.text.Style ;
import javax.swing.text.StyleConstants ;
import javax.swing.text.StyleContext ;
import javax.swing.text.StyledDocument ;

import org.apache.log4j.Logger ;

import com.sandy.jnmaker.ui.helper.PopupEditMenu ;
import com.sandy.jnmaker.ui.helper.UIUtil ;
import com.sandy.jnmaker.util.JNSrcTokenizer ;
import com.sandy.jnmaker.util.JNSrcTokenizer.Token ;
import com.sandy.jnmaker.util.JNSrcTokenizer.TokenType ;
import com.sandy.jnmaker.util.WordRepository.WordSource ;

@SuppressWarnings( "serial" )
public class JoveNotesTextPane extends JTextPane implements WordSource {

    static final Logger log = Logger.getLogger( JoveNotesTextPane.class ) ;
    
    private static String[] JN_KEYWORDS = {
            "@img", 
            "@table",
            "@th",
            "@td",
            "@math",
            "@imath",
            "@chem",
            "@ichem",
            "@audio",
            "@youtube",
            "@doc",
            "@var",
            "@compiler_break",
            "\""
    } ;
    
    private StyledDocument doc = null ;
    private PopupEditMenu editMenu = null ;
    
    private String lastHighlightedContent = null ;
    
    public JoveNotesTextPane() {
        
        prepareAndSetStyledDocument() ;
        editMenu = UIUtil.associateEditMenu( this ) ;
        
        decorateUI() ;
        startDaemonHighlightThread() ;
    }
    
    private void decorateUI() {
        UIUtil.setTextPaneBackground( UIUtil.EDITOR_BG_COLOR, this ) ;
        setForeground( UIUtil.STRING_COLOR ) ;
        setCaretColor( Color.GREEN ) ;
    }
    
    private void startDaemonHighlightThread() {
        Thread thread = new Thread() {
            @Override public void run() {
                while( true ) {
                    try {
                        Thread.sleep( 500 ) ;
                        String currentContent = doc.getText( 0, doc.getLength() ) ;
                        if( lastHighlightedContent == null ||
                            !lastHighlightedContent.equals( currentContent ) ) {
                            highlightDocument() ;
                            lastHighlightedContent = currentContent ;
                        }
                    }
                    catch( Exception e ) {
                        lastHighlightedContent = null ;
                    }
                }
            }
        } ;
        thread.setDaemon( true ) ;
        thread.start() ;
    }
    
    private void prepareAndSetStyledDocument() {

        doc  = new DefaultStyledDocument() ;
        Style base = StyleContext.getDefaultStyleContext()
                           .getStyle( StyleContext.DEFAULT_STYLE ) ;
        
        Style keyword = doc.addStyle( TokenType.KEYWORD.toString(), base ) ;
        StyleConstants.setBold( keyword, true ) ;
        StyleConstants.setForeground( keyword, UIUtil.KEYWORD_COLOR ) ;
        
        Style nestedKeyword = doc.addStyle( TokenType.NESTED_KEYWORDS.toString(), base ) ;
        StyleConstants.setBold( nestedKeyword, true ) ;
        StyleConstants.setForeground( nestedKeyword, Color.YELLOW ) ;
        
        Style punctuation = doc.addStyle( TokenType.PUNCTUATION.toString(), base ) ;
        StyleConstants.setBold( punctuation, true ) ;
        StyleConstants.setForeground( punctuation, Color.PINK ) ;
        
        Style string = doc.addStyle( TokenType.STRING.toString(), base ) ;
        StyleConstants.setForeground( string, UIUtil.STRING_COLOR ) ;
        
        Style tnString = doc.addStyle( TokenType.TN_STRING.toString(), base ) ;
        StyleConstants.setForeground( tnString, UIUtil.TN_STRING_COLOR ) ;
        
        Style number = doc.addStyle( TokenType.INT.toString(), base ) ;
        StyleConstants.setForeground( number, UIUtil.NUMBER_COLOR ) ;
        
        Style comment = doc.addStyle( TokenType.COMMENT.toString(), base ) ;
        StyleConstants.setItalic( comment, true ) ;
        StyleConstants.setForeground( comment, Color.GRAY ) ;
        
        Style unknown = doc.addStyle( TokenType.UNKNOWN.toString(), base ) ;
        StyleConstants.setForeground( unknown, Color.RED ) ;
        StyleConstants.setBold( unknown, true ) ;
        
        Style mdBold = doc.addStyle( TokenType.MD_BOLD.toString(), base ) ;
        StyleConstants.setBold( mdBold, true ) ;
        
        Style mdItalic = doc.addStyle( TokenType.MD_ITALIC.toString(), base ) ;
        StyleConstants.setItalic( mdItalic, true ) ;
        
        Style jnMarker = doc.addStyle( TokenType.JN_MARKER.toString(), base ) ;
        StyleConstants.setForeground( jnMarker, new Color(205, 206, 192) ) ;
        
        Style jnKeyword = doc.addStyle( TokenType.JN_KEYWORD.toString(), base ) ;
        StyleConstants.setBold( jnKeyword, true ) ;
        
        setDocument( doc );
    }

    public void highlightDocument() throws Exception {
        try {
            editMenu.disengageUndoManager() ;
            parseDocumentAndHighlight() ;
        }
        finally {
            editMenu.reengageUndoManager() ;
        }
    }
    
    private void parseDocumentAndHighlight() throws Exception {

        Token          token     = null ;
        JNSrcTokenizer tokenizer = null ;
        boolean        inTNScope = false ;
        
        tokenizer = new JNSrcTokenizer( doc.getText( 0, doc.getLength() ) ) ;
        
        while( ( token = tokenizer.getNextToken() ) != null ) {
            
            if( token.tokenType == TokenType.KEYWORD ) {
                inTNScope = token.token.equals( "@tn" ) ;
            }
            
            int len = (token.end-token.start)+1 ;
            
            if( len > 0 ) {
                if( inTNScope && token.tokenType == TokenType.STRING ) {
                    doc.setCharacterAttributes( token.start, len, 
                                doc.getStyle( TokenType.TN_STRING.toString() ), 
                                true ) ;
                }
                else {
                    doc.setCharacterAttributes( token.start, len, 
                                    doc.getStyle( token.tokenType.toString() ), 
                                    true ) ;
                }
            }
            
            if( token.tokenType == TokenType.STRING ) {
                highlightString( token ) ;
            }
        }
    }
    
    private void highlightString( Token token ) {
        
        highlightRegexToken( token, TokenType.MD_BOLD   ) ;
        highlightRegexToken( token, TokenType.MD_ITALIC ) ;
        highlightRegexToken( token, TokenType.JN_MARKER ) ;
        highlightJNKeywords( token ) ;
    }
    
    private void highlightRegexToken( Token token, TokenType type ) {
        
        Pattern pattern = Pattern.compile( getRegexForToken( type ), 
                                           Pattern.DOTALL ) ;
        String  input   = token.token ;
        Matcher matcher = pattern.matcher( input ) ;
        
        while( matcher.find() ) {
            int start = matcher.start() ;
            int end   = matcher.end() ;
            int len   = end - start ;
            
            if( len > 0 ) {
                doc.setCharacterAttributes( token.start + start, len, 
                        doc.getStyle( type.toString() ), false ) ;
            }
        }
    }
    
    private String getRegexForToken( TokenType tokenType ) {
        
        if( tokenType == TokenType.MD_BOLD ) {
            return "\\*\\*.*?\\*\\*" ;
        }
        else if( tokenType == TokenType.MD_ITALIC ) {
            return "_.*?_" ;
        }
        else if( tokenType == TokenType.JN_MARKER ) {
            return "\\{\\{@([a-zA-Z0-9]*)\\s+((.(?!\\{\\{))*)\\}\\}" ;
        }
        return null ;
    }
    
    private void highlightJNKeywords( Token token ) {
        
        Style  style =  doc.getStyle( TokenType.JN_KEYWORD.toString() ) ;
        String input = token.token ;
        for( int i=0; i<input.length(); i++ ) {
            for( String jnKeyword : JN_KEYWORDS ) {
                if( input.indexOf( jnKeyword, i ) == i ) {
                    doc.setCharacterAttributes( token.start + i, 
                                                jnKeyword.length(), 
                                                style, 
                                                false ) ;
                    i += jnKeyword.length() ;
                    break ;
                }
            }
        }
    }

    @Override
    public String getTextForWordRepository() {
        String str = null ;
        try {
            str = doc.getText( 0, doc.getLength() ) ;
        }
        catch( BadLocationException e ) {
            // This should not be happening.
            e.printStackTrace() ;
        }
        return str ;
    }
}
