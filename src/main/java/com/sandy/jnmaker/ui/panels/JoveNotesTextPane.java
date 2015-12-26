package com.sandy.jnmaker.ui.panels;

import java.util.regex.Matcher ;
import java.util.regex.Pattern ;

import javax.swing.JTextPane ;
import javax.swing.SwingUtilities ;
import javax.swing.event.DocumentEvent ;
import javax.swing.event.DocumentListener ;
import javax.swing.text.Style ;
import javax.swing.text.StyleConstants ;
import javax.swing.text.StyleContext ;
import javax.swing.text.StyledDocument ;

import org.apache.log4j.Logger ;

import com.sandy.jnmaker.ui.helper.EditMenu ;
import com.sandy.jnmaker.ui.helper.UIUtil ;

public class JoveNotesTextPane extends JTextPane implements DocumentListener {

    private static final long serialVersionUID = 1L ;
    private static final Logger logger = Logger.getLogger( JoveNotesTextPane.class ) ;

    private static final String KEYWORD_PATTERN = "@\\b(" + 
                                "definition"                    + "|" + 
                                "qa"                            + "|" +
                                "fib"                           + "|" +
                                "event"                         + "|" +
                                "true_false"                    + "|" +
                                "wm"                            + "|" +
                                "spellbee"                      + "|" +
                                "skip_generation"               + "|" +
                                "skip_generation_in_production" + "|" +
                                ")\\b" ;
    
    private static final String KEYWORD1_PATTERN = "\\n(subject|chapterNumber|chapterName).*\\n" ;
    
    private static final String STRING_PATTERN =
            "'([^\\\\']+|\\\\([btnfr\"'\\\\]|[0-3]?[0-7]{1,2}|u[0-9a-fA-F]{4}))*'|\"([^\\\\\"]+|\\\\([btnfr\"'\\\\]|[0-3]?[0-7]{1,2}|u[0-9a-fA-F]{4}))*\"" ;    
    
    private enum TokenType { KEYWORD, KEYWORD1, STRING } ;
    
    private StyledDocument doc = null ;
    private boolean processingHighlight = false ;
    private EditMenu editMenu = null ;
    
    public JoveNotesTextPane() {
        
        UIUtil.setTextPaneBackground( UIUtil.EDITOR_BG_COLOR, this ) ;
        prepareAndSetStyledDocument() ;
        editMenu = UIUtil.associateEditMenu( this ) ;
    }
    
    private void prepareAndSetStyledDocument() {

        Style base = null ;
        
        doc  = this.getStyledDocument() ;
        base = StyleContext.getDefaultStyleContext()
                           .getStyle( StyleContext.DEFAULT_STYLE ) ;
        
        Style keyword = doc.addStyle( TokenType.KEYWORD.toString(), base ) ;
        StyleConstants.setBold( keyword, true ) ;
        StyleConstants.setForeground( keyword, UIUtil.KEYWORD_COLOR ) ;
        
        Style keyword1 = doc.addStyle( TokenType.KEYWORD1.toString(), base ) ;
        StyleConstants.setBold( keyword1, true ) ;
        StyleConstants.setForeground( keyword1, UIUtil.KEYWORD_COLOR ) ;
        
        Style string = doc.addStyle( TokenType.STRING.toString(), base ) ;
        StyleConstants.setForeground( string, UIUtil.STRING_COLOR ) ;
        
        doc.addDocumentListener( this ) ;
    }

    @Override public void insertUpdate ( DocumentEvent e ){ highlightDocument(); }
    @Override public void removeUpdate ( DocumentEvent e ){ highlightDocument(); }
    @Override public void changedUpdate( DocumentEvent e ){ highlightDocument(); }

    private void highlightDocument() {
        if( !processingHighlight ) {
            doc.removeDocumentListener( this ) ;
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    try {
                        editMenu.disengageUndoManager() ;
                        highlightPattern( TokenType.KEYWORD ) ;
                        highlightPattern( TokenType.KEYWORD1 ) ;
                        highlightPattern( TokenType.STRING ) ;
                        doc.addDocumentListener( JoveNotesTextPane.this ) ;
                        editMenu.reengageUndoManager() ;
                    }
                    catch( Exception e ) {
                        logger.error( "Error highlighting document.", e ) ;
                    }
                }
            });
        }
    }
    
    private void highlightPattern( TokenType tokenType ) 
        throws Exception {
        
        String patternStr = null ;
        if( tokenType == TokenType.KEYWORD ) {
            patternStr = KEYWORD_PATTERN ;
        }
        else if( tokenType == TokenType.KEYWORD1 ) {
            patternStr = KEYWORD1_PATTERN ;
        }
        else if( tokenType == TokenType.STRING ) {
            patternStr = STRING_PATTERN ;
        }
        
        Pattern pattern = Pattern.compile( patternStr, Pattern.DOTALL ) ;
        String  input   = doc.getText( 0, doc.getLength() ) ;
        Matcher matcher = pattern.matcher( input ) ;
        
        while( matcher.find() ) {
            int start = matcher.start() ;
            int end   = matcher.end() ;
            
            doc.setCharacterAttributes( start, (end-start), 
                                        doc.getStyle( tokenType.toString() ), 
                                        true ) ; 
        }
        processingHighlight = false ;
    }
}
