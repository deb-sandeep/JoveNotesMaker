package com.sandy.jnmaker.ui.dialogs.qa;

import java.awt.event.KeyAdapter ;
import java.awt.event.KeyEvent ;

import com.sandy.common.util.StringUtil ;
import com.sandy.jnmaker.ui.helper.UIUtil ;

public class QAPanel extends QAPanelUI {

    private static final long serialVersionUID = 3958957198034168755L ;

    public QAPanel( String selectedText ) {
        
        UIUtil.associateEditMenu( this.questionTextArea ) ;
        UIUtil.associateEditMenu( this.answerTextArea ) ;
        setUpListeners() ;
        
        this.answerTextArea.setText( selectedText ) ;
    }
    
    private void setUpListeners() {
        super.bindOkPressEventCapture( this.questionTextArea ) ;
        super.bindOkPressEventCapture( this.answerTextArea ) ;
        
        this.answerTextArea.addKeyListener( new KeyAdapter() {
            public void keyPressed( KeyEvent e ) {
                if( e.getModifiers() == KeyEvent.CTRL_MASK && 
                    e.getKeyCode() == KeyEvent.VK_M ) {
                    
                    moveSelTextFromAnsFieldToQuestionField() ;
                }
            }
        } );
    }
    
    protected void captureFocus() {
        this.questionTextArea.requestFocus() ;
    }
    
    @Override
    public String getFormattedNote() {
        
        String questionText = this.questionTextArea.getText() ;
        String answerText   = this.answerTextArea.getText() ;
        
        if( StringUtil.isEmptyOrNull( questionText ) || 
            StringUtil.isEmptyOrNull( answerText ) ) {
        
            showErrorMsg( "Question or Answer can't be empty." ) ;
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
    
    private void moveSelTextFromAnsFieldToQuestionField() {
        
        String selText = answerTextArea.getSelectedText() ;
        
        if( StringUtil.isNotEmptyOrNull( selText ) ) {
            int caretPos = questionTextArea.getCaretPosition() ;
            questionTextArea.insert( selText, caretPos ) ;
            questionTextArea.requestFocus() ;
        }
    }
}
