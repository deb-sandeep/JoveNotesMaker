package com.sandy.jnmaker.ui.helper;

import static com.sandy.jnmaker.util.ObjectRepository.getWordRepository ;

import java.awt.event.KeyAdapter ;
import java.awt.event.KeyEvent ;
import java.util.List ;

import javax.swing.text.BadLocationException ;
import javax.swing.text.Document ;
import javax.swing.text.JTextComponent ;

import org.apache.log4j.Logger ;

import com.sandy.common.util.StringUtil ;

public class AutocompleteManager extends KeyAdapter {
    
    private static final Logger logger = Logger.getLogger( AutocompleteManager.class ) ;
    
    private JTextComponent control = null ;
    private Document       doc     = null ;
    
    private boolean        inAutoCompleteMode = false ;
    private List<String>   potentialMatches   = null ;
    private int            nextMatchPos       = 0 ;
    private String         autocompletePrefix = null ;
    
    
    public AutocompleteManager( JTextComponent textComponent ) {
        this.control = textComponent ;
        this.doc = textComponent.getDocument() ;
    }

    private String getTextAtCaret() {
        
        StringBuffer buffer = new StringBuffer() ;
        int pos = control.getCaretPosition() ;
        
        int p = pos-1 ;
        try {
            while( p>-1 ) {
                char ch = control.getDocument().getText(p, 1).charAt(0) ;
                if( isDelimitingChar( ch ) ) {
                    break ;
                }
                else {
                    buffer.insert( 0, ch ) ;
                }
                p-- ;
            }
        }
        catch( BadLocationException e ) {
            logger.error( "Error while getting text at caret.", e ) ;
        }
                
        return buffer.toString().trim() ;
    }
    
    private boolean isDelimitingChar( char ch ) {
        if( Character.isAlphabetic( ch ) ||
            Character.isDigit( ch ) ) {
            return false ;
        }
        return true ;
    }

    public void insertTab() {
        try {
            doc.insertString( control.getCaretPosition(), "    ", null ) ;
        }
        catch( BadLocationException e ) {
            logger.error( "Error while inserting tab at caret.", e ) ;
        }
    }
    
    @Override 
    public void keyPressed( KeyEvent e ) {
        
        int keyCode = e.getKeyCode() ;
        
        try {
            if( !inAutoCompleteMode ) {
                if( keyCode == KeyEvent.VK_TAB && e.isShiftDown() ) {
                    insertTab() ;
                }
                else if( keyCode == KeyEvent.VK_TAB ) {
                    e.consume() ;
                    showAutoComplete() ;
                }
                else if( Character.isWhitespace( keyCode ) ) {
                    getWordRepository().offer( getTextAtCaret() ) ;
                }
            }
            else {
                e.consume() ;
                if( keyCode == KeyEvent.VK_TAB && e.isShiftDown() ) {
                    removeSelectedText() ;
                    insertAutocompletePrefix() ;
                    insertTab() ;
                    inAutoCompleteMode = false ;
                }
                else if( keyCode == KeyEvent.VK_TAB ) {
                    showNextPotentialMatch() ;
                }
                else if( keyCode == KeyEvent.VK_ENTER ) {
                    control.moveCaretPosition( control.getSelectionEnd() ) ;
                    inAutoCompleteMode = false ;
                }
                else if( keyCode == KeyEvent.VK_SPACE ) {
                    control.moveCaretPosition( control.getSelectionEnd() ) ;
                    doc.insertString( control.getCaretPosition(), "", null ) ;
                    inAutoCompleteMode = false ;
                }
                else if( keyCode == KeyEvent.VK_ESCAPE ) {
                    removeSelectedText() ;
                    insertAutocompletePrefix() ;
                    inAutoCompleteMode = false ;
                }
                else if( keyCode == KeyEvent.VK_BACK_SPACE ) {
                    removeSelectedText() ;
                    inAutoCompleteMode = false ;
                }
            }
        }
        catch( BadLocationException e1 ) {
            logger.error( "Error in autocompletion.", e1 ) ;
        }
    }
    
    private void removeSelectedText() throws BadLocationException {
        if( control.getSelectedText() != null ) {
            doc.remove( control.getSelectionStart(), 
                        control.getSelectedText().length() ) ;
        }
    }
    
    private void insertAutocompletePrefix() throws BadLocationException {
        doc.insertString( control.getCaretPosition(), autocompletePrefix, null ) ;
    }

    private void showAutoComplete() {
        
        String textAtPos = getTextAtCaret() ;
        
        if( StringUtil.isNotEmptyOrNull( textAtPos ) ) {
            textAtPos = textAtPos.trim() ;
            
            potentialMatches = getWordRepository().getFuzzyMatches( textAtPos, 10 ) ;
            if( !potentialMatches.isEmpty() ) {
                nextMatchPos = 0 ;
                inAutoCompleteMode = true ;
                autocompletePrefix = textAtPos ;
                
                showNextPotentialMatch() ;
            }
        }
    }

    private void showNextPotentialMatch() {
        
        int caretPos = control.getCaretPosition() ;
        String match = potentialMatches.get( nextMatchPos ) ;
        
        try {
            if( nextMatchPos == 0 ) {
                caretPos = caretPos - autocompletePrefix.length() ;
                doc.remove( caretPos, autocompletePrefix.length() ) ;
            }
            else {
                if( StringUtil.isNotEmptyOrNull( control.getSelectedText() ) ) {
                    doc.remove( control.getSelectionStart(), 
                                control.getSelectedText().length() ) ;
                }
            }
            
            doc.insertString( caretPos, match, null ) ;
            control.moveCaretPosition( caretPos ) ;
        }
        catch( BadLocationException e ) {
            logger.error( "Error while showing potential matches.", e ) ;
        }
        
        nextMatchPos++ ;
        if( nextMatchPos > potentialMatches.size()-1 ) {
            nextMatchPos = 0 ;
        }
    }
}
