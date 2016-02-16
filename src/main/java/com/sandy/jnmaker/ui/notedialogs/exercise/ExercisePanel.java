package com.sandy.jnmaker.ui.notedialogs.exercise ;

import java.awt.Component ;
import java.awt.Font ;
import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;
import java.awt.event.KeyAdapter ;
import java.awt.event.KeyEvent ;
import java.util.ArrayList ;
import java.util.List ;

import javax.swing.JTextArea ;
import javax.swing.text.BadLocationException ;

import com.sandy.common.util.StringUtil ;
import com.sandy.jnmaker.ui.helper.UIUtil ;

@SuppressWarnings( "serial" )
public class ExercisePanel extends ExercisePanelUI implements ActionListener {
    
    public ExercisePanel( String selectedText ) {
        
        UIUtil.associateEditMenu( this.questionTA ) ;
        UIUtil.associateEditMenu( this.answerTA ) ;
        setUpListeners() ;
        
        super.questionTA.setText( selectedText ) ;
    }

    private void setUpListeners() {
        super.bindOkPressEventCapture( this.questionTA ) ;
        super.bindOkPressEventCapture( this.answerTA ) ;
        
        super.newHintBtn.addActionListener( this ) ; 
        super.deleteHintBtn.addActionListener( this ) ;
        super.moveLeftBtn.addActionListener( this ) ;
        super.moveRightBtn.addActionListener( this ) ;
        
        super.questionTA.addKeyListener( new KeyAdapter() {
            public void keyPressed( KeyEvent e ) {
                if( e.getModifiers() == KeyEvent.CTRL_MASK && 
                    e.getKeyCode() == KeyEvent.VK_M ) {
                    
                    moveSelTextFromQuestionsFieldToAnsField() ;
                }
            }
        } ) ;
    }
    
    @Override
    public String getFormattedNote() {
        
        String questionText = this.questionTA.getText() ;
        String answerText   = this.answerTA.getText() ;
        
        if( StringUtil.isEmptyOrNull( questionText ) || 
            StringUtil.isEmptyOrNull( answerText ) ) {
        
            showErrorMsg( "Question or Answer can't be empty." ) ;
            return null ;
        }
        
        int marks = getMarks() ;
        if( marks == -1 ) {
            return null ;
        }
        
        StringBuilder buffer = new StringBuilder() ;
        buffer.append( "@exercise marks=" )
              .append( marks )
              .append( "\n" )
              .append( " \"" )
              .append( formatText( questionText ) )
              .append( "\"\n" ) ;
        
        List<String> hints = getHints() ;
        if( !hints.isEmpty() ) {
            buffer.append( "hints {\n"  ) ;
            for( String hint : hints ) {
                buffer.append( "\"" )
                      .append( formatText( hint ) )
                      .append( "\"\n" ) ;
            }
            buffer.append( "}\n" ) ;
        }
        
        buffer.append( "answer \"" )
              .append( formatText( answerText ) ) 
              .append( "\"" ) ; 
        
        return buffer.toString() ;
    }

    @Override
    protected void captureFocus() {
        super.questionTA.requestFocus() ;
    }

    @Override
    public void actionPerformed( ActionEvent e ) {
        Object src = e.getSource() ;
        if( src == super.newHintBtn ) {
            createNewHint() ;
        }
        else if( src == super.deleteHintBtn ) {
            deleteHint() ;
        }
        else if( src == super.moveLeftBtn ) {
            moveHintLeft() ;
        }
        else if( src == super.moveRightBtn ) {
            moveHintRight() ;
        }
    }
    
    private void createNewHint() {
        
        JTextArea ta = new JTextArea() ;
        ta.setFont( new Font( "Trebuchet MS", 0, 14 ) );
        ta.setLineWrap( true ) ; 
        ta.setWrapStyleWord( true ) ;
        
        int numHints = super.hintsTabbedPane.getComponentCount() ;
        super.hintsTabbedPane.add( "Hint " + (numHints + 1), ta ) ;
        super.hintsTabbedPane.setSelectedIndex( numHints ) ;
        
        ta.requestFocus() ;
    }
    
    private void deleteHint() {
        
        int selIndex = super.hintsTabbedPane.getSelectedIndex() ;
        if( selIndex != -1 ) {
            super.hintsTabbedPane.remove( selIndex ) ;
            refreshHintTabTitles() ;
        }
    }
    
    private void moveHintLeft() {
        
        int selIndex = super.hintsTabbedPane.getSelectedIndex() ;
        if( selIndex != -1 && selIndex > 0 ) {
            
            Component comp = super.hintsTabbedPane.getSelectedComponent() ;
            super.hintsTabbedPane.remove( selIndex ) ;
            super.hintsTabbedPane.insertTab( "Hint", null, comp, null, (selIndex-1) ) ;
            super.hintsTabbedPane.setSelectedIndex( selIndex-1 ) ;
            refreshHintTabTitles() ;
        }
    }
    
    private void moveHintRight() {
        
        int selIndex = super.hintsTabbedPane.getSelectedIndex() ;
        if( selIndex != -1 && selIndex < (super.hintsTabbedPane.getComponentCount()-1) ) {
            
            Component comp = super.hintsTabbedPane.getSelectedComponent() ;
            super.hintsTabbedPane.remove( selIndex ) ;
            super.hintsTabbedPane.insertTab( "Hint", null, comp, null, (selIndex+1) ) ;
            super.hintsTabbedPane.setSelectedIndex( selIndex+1 ) ;
            refreshHintTabTitles() ;
        }
    }
    
    private void refreshHintTabTitles() {
        
        for( int i=0; i<super.hintsTabbedPane.getComponentCount(); i++ ) {
            super.hintsTabbedPane.setTitleAt( i, "Hint " + (i+1) ) ;
        }
    }
    
    private List<String> getHints() {
        
        List<String> hints = new ArrayList<>() ;
        int numHintsTA = super.hintsTabbedPane.getComponentCount() ; 
        if( numHintsTA > 0 ) {
            for( int i=0; i<numHintsTA; i++ ) {
                JTextArea ta = ( JTextArea )super.hintsTabbedPane.getComponentAt( i ) ;
                String hintTxt = ta.getText() ;
                
                if( StringUtil.isNotEmptyOrNull( hintTxt ) ) {
                    hints.add( hintTxt.trim() ) ;
                }
            }
        }
        return hints ;
    }
    
    private int getMarks() {
        int marks = -1 ;
        String marksText = super.marksTF.getText() ;
        if( StringUtil.isEmptyOrNull( marksText ) ) {
            showErrorMsg( "Marks can't be empty." ) ;
        }
        else {
            try {
                marks = Integer.parseInt( marksText.trim() ) ;
            }
            catch( Exception e ) {
                showErrorMsg( "Marks should be a valid integer." ) ;
            }
        }
        return marks ;
    }
    
    private void moveSelTextFromQuestionsFieldToAnsField() {
        
        String selText = questionTA.getSelectedText() ;
        if( StringUtil.isNotEmptyOrNull( selText ) ) {
            int caretPos = answerTA.getCaretPosition() ;
            answerTA.insert( selText, caretPos ) ;
            answerTA.requestFocus() ;
            
            try {
                questionTA.getDocument().remove( questionTA.getSelectionStart(), 
                                                 selText.length() ) ;
            }
            catch( BadLocationException e ) {
                e.printStackTrace();
            }
        }
    }    
}
