package com.sandy.jnmaker.ui.dialogs.qa;

import javax.swing.JOptionPane ;

import org.apache.commons.lang.StringEscapeUtils ;

import com.sandy.common.util.StringUtil ;

public class QAPanel extends QAPanelUI {

    private static final long serialVersionUID = 3958957198034168755L ;

    public QAPanel( String selectedText ) {
        
        this.answerTextArea.setText( selectedText ) ;
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
              .append( StringEscapeUtils.escapeJava( questionText ) )
              .append( "\"\n" )
              .append( "\"" )
              .append( StringEscapeUtils.escapeJava( answerText ) )
              .append( "\"\n\n" ) ; 
        
        return buffer.toString() ;
    }
}
