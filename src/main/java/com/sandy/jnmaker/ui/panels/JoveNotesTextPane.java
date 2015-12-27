package com.sandy.jnmaker.ui.panels;

import java.awt.Color ;

import javax.swing.JTextPane ;
import javax.swing.text.DefaultStyledDocument ;
import javax.swing.text.Style ;
import javax.swing.text.StyleConstants ;
import javax.swing.text.StyleContext ;
import javax.swing.text.StyledDocument ;

import com.sandy.jnmaker.ui.helper.EditMenu ;
import com.sandy.jnmaker.ui.helper.JNSrcTokenizer ;
import com.sandy.jnmaker.ui.helper.JNSrcTokenizer.Token ;
import com.sandy.jnmaker.ui.helper.JNSrcTokenizer.TokenType ;
import com.sandy.jnmaker.ui.helper.UIUtil ;

public class JoveNotesTextPane extends JTextPane {

    private static final long serialVersionUID = 1L ;
    
    private StyledDocument doc = null ;
    private EditMenu editMenu = null ;
    
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
                        Thread.sleep( 1000 ) ;
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
        
        Style number = doc.addStyle( TokenType.INT.toString(), base ) ;
        StyleConstants.setForeground( number, UIUtil.NUMBER_COLOR ) ;
        
        Style unknown = doc.addStyle( TokenType.UNKNOWN.toString(), base ) ;
        StyleConstants.setForeground( unknown, Color.RED ) ;
        StyleConstants.setBold( unknown, true ) ;
        
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
        
        tokenizer = new JNSrcTokenizer( doc.getText( 0, doc.getLength() ) ) ;
        
        while( ( token = tokenizer.getNextToken() ) != null ) {
            doc.setCharacterAttributes( token.start, (token.end-token.start)+1, 
                                        doc.getStyle( token.tokenType.toString() ), 
                                        true ) ;
        }
    }
}
