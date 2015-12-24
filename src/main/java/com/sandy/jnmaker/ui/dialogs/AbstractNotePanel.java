package com.sandy.jnmaker.ui.dialogs;

import java.awt.event.KeyAdapter ;
import java.awt.event.KeyEvent ;

import javax.swing.JOptionPane ;
import javax.swing.JPanel ;
import javax.swing.text.JTextComponent ;

import org.apache.commons.lang.WordUtils ;

import com.sandy.common.util.StringUtil ;

public abstract class AbstractNotePanel extends JPanel {

    private static final long serialVersionUID = -2589969430412971534L ;
    
    protected NotesCreatorDialog parent = null ;
    
    public abstract String getFormattedNote() ;

    public void setParentDialog( NotesCreatorDialog parent ) {
        this.parent = parent ;
    }
    
    protected String escapeQuotes( String input ) {
        return input.replaceAll( "\"", "\\\"" ) ;
    }

    protected String formatText( String input ) {
        
        StringBuilder buffer = new StringBuilder() ;
        String[] lines = input.split( "\n" ) ;
        
        for( int i=0; i<lines.length; i++ ) {
            String line = lines[i] ;
            if( !StringUtil.isEmptyOrNull( line ) ) {
                line = escapeQuotes( line ) ;
                line = WordUtils.wrap( line, 80, "\n", false ) ;
                buffer.append( line ) ;
                
                if( i < (lines.length - 1) ) {
                    buffer.append( "  \n" ) ;
                }
            }
            else {
                buffer.append( "\n" ) ;
            }
        }
        return buffer.toString() ;
    }
    
    protected void bindOkPressEventCapture( JTextComponent textArea ) {
        
        textArea.addKeyListener( new KeyAdapter() {
            @Override
            public void keyPressed( KeyEvent e ) {
                if( e.getKeyCode()   == KeyEvent.VK_ENTER && 
                    e.getModifiers() == KeyEvent.CTRL_MASK ) {
                    parent.okPressed() ;
                }            
            }
        });
    }
    
    protected void showErrorMsg( String msg ) {
        JOptionPane.showMessageDialog( this, msg, "Input error", 
                                       JOptionPane.ERROR_MESSAGE ) ; 
    }
}
