package com.sandy.jnmaker.ui.dialogs.qa;

import java.awt.event.KeyEvent ;
import java.awt.event.KeyListener ;

import javax.swing.JOptionPane ;

import com.sandy.common.util.StringUtil ;

public class QAPanel extends QAPanelUI implements KeyListener {

    private static final long serialVersionUID = 3958957198034168755L ;

    public QAPanel( String selectedText ) {
        this.answerTextArea.setText( selectedText ) ;
        this.questionTextArea.addKeyListener( this ) ;
        this.answerTextArea.addKeyListener( this ) ;
    }
    
    @Override
    public String getFormattedNote() {
        
        String questionText = this.questionTextArea.getText() ;
        String answerText   = this.answerTextArea.getText() ;
        
        if( StringUtil.isEmptyOrNull( questionText ) || 
            StringUtil.isEmptyOrNull( answerText ) ) {
        
            JOptionPane.showMessageDialog( this, 
                    "Question or Answer can't be empty.", 
                    "Invalid entry", JOptionPane.ERROR_MESSAGE ) ;
            return null ;
        }
        
        StringBuilder buffer = new StringBuilder() ;
        buffer.append( "@qa \"" )
              .append( formatText( questionText ) )
              .append( "\"\n" )
              .append( "\"" )
              .append( formatText( answerText ) ) 
              .append( "\"" ) ; 
        
        return buffer.toString() ;
    }
    
    @Override
    public void keyPressed( KeyEvent e ) {
        
        if( e.getKeyCode()   == KeyEvent.VK_ENTER && 
            e.getModifiers() == KeyEvent.CTRL_MASK ) {
            
            super.parent.okPressed() ;
        }
    }

    @Override public void keyReleased( KeyEvent e ) {}
    @Override public void keyTyped( KeyEvent e ) {}
}
