package com.sandy.jnmaker.ui.notedialogs.qa;

import static com.sandy.jnmaker.util.ObjectRepository.getMainFrame ;
import static com.sandy.jnmaker.util.NoteTextUtil.* ;

import java.awt.Dimension ;
import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;
import java.awt.event.KeyAdapter ;
import java.awt.event.KeyEvent ;
import java.io.File ;
import java.util.StringTokenizer ;

import javax.swing.JFileChooser ;

import com.sandy.common.ui.ImageFilter ;
import com.sandy.common.ui.ImagePreview ;
import com.sandy.common.util.StringUtil ;
import com.sandy.jnmaker.ui.helper.UIUtil ;
import com.sandy.jnmaker.ui.notedialogs.NotesCreatorDialog ;
import com.sandy.jnmaker.util.ObjectRepository ;

@SuppressWarnings( {"serial", "deprecation"} )
public class QAPanel extends QAPanelUI {

    private static final String ANS_LABEL_TEMPLATE = "Answer [word count = {wc}]" ;
    
    private File ansImgFile = null ;

    public QAPanel( String selectedText, boolean asQuestion ) {
        
        UIUtil.associateEditMenu( this.questionTextArea ) ;
        UIUtil.associateEditMenu( this.answerTextArea ) ;
        setUpListeners() ;
        
        if( selectedText.startsWith( "Question:" ) ) {
            populateQuestionAnswerText( selectedText ) ;
        }
        else {
            if( asQuestion ) {
                this.questionTextArea.setText( selectedText ) ;
            }
            else {
                this.answerTextArea.setText( selectedText ) ;
            }
        }
        updateWordCount() ;
    }
    
    private void populateQuestionAnswerText( String selectedText ) {
        
        int ansIndex = selectedText.indexOf( "Answer:" ) ;
        
        String question = selectedText.substring( 0, ansIndex ) ;
        String answer   = selectedText.substring( ansIndex ) ;
        
        question = question.substring( "Question:".length() ) ;
        answer   = answer.substring( "Answer:".length() ) ;
        
        question = question.trim() ;
        answer   = answer.trim() ;
        
        this.questionTextArea.setText( question ) ;
        this.answerTextArea.setText( answer ) ;
    }

    protected void updateWordCount() {
        String ansText = super.answerTextArea.getText() ;
        int wordCount = 0 ;
        
        if( StringUtil.isNotEmptyOrNull( ansText ) ) {
            StringTokenizer tokens = new StringTokenizer( ansText ) ;
            wordCount = tokens.countTokens() ;
        }
        
        String newLabel = ANS_LABEL_TEMPLATE.replace( "{wc}", "" + wordCount ) ;
        super.enterALabel.setText( newLabel ) ;
    }
    
    private void setUpListeners() {
        super.bindOkPressEventCapture( this.questionTextArea ) ;
        super.bindOkPressEventCapture( this.answerTextArea ) ;
        
        this.answerTextArea.addKeyListener( new KeyAdapter() {
            public void keyPressed( KeyEvent e ) {
                if( e.getModifiers() == ( KeyEvent.CTRL_MASK | KeyEvent.ALT_MASK )&& 
                    e.getKeyCode() == KeyEvent.VK_M ) {
                    
                    moveSelTextFromAnsFieldToQuestionField() ;
                }
                else if( e.getModifiers() == KeyEvent.CTRL_MASK && 
                         e.getKeyCode() == KeyEvent.VK_M ) {
                    
                    copySelTextFromAnsFieldToQuestionField() ;
                }
                updateWordCount() ;
            }
        } ) ;
        
        ansImgBtn.addActionListener( new ActionListener() {
            @Override public void actionPerformed( ActionEvent e ) {
                loadAnsImageFile() ;
            }
        } ) ;
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
              .append( "\"\n" ) ;
        if( ansImgFile != null ) {
            buffer.append( "\"" )
                  .append( "{{@img " + ansImgFile.getName() + "}}" ) 
                  .append( "\"\n" ) ; 
        }
        buffer.append( "\"" )
              .append( formatText( answerText ) ) 
              .append( "\"" ) ; 
        
        return buffer.toString() ;
    }
    
    public void appendNoteToAnswer( String text ) {
        this.answerTextArea.append( text ) ;
    }
    
    private void moveSelTextFromAnsFieldToQuestionField() {
        
        String selText = answerTextArea.getSelectedText() ;
        answerTextArea.replaceSelection( "" ) ;
        
        if( StringUtil.isNotEmptyOrNull( selText ) ) {
            int caretPos = questionTextArea.getCaretPosition() ;
            questionTextArea.insert( selText, caretPos ) ;
            questionTextArea.requestFocus() ;
        }
    }
    
    private void copySelTextFromAnsFieldToQuestionField() {
        
        String selText = answerTextArea.getSelectedText() ;
        
        if( StringUtil.isNotEmptyOrNull( selText ) ) {
            int caretPos = questionTextArea.getCaretPosition() ;
            questionTextArea.insert( selText, caretPos ) ;
            questionTextArea.requestFocus() ;
        }
    }
    
    private void loadAnsImageFile() {
        
        JFileChooser       fc    = getImageFileChooser() ;
        NotesCreatorDialog ncDlg = ObjectRepository.getCurNotesDialog() ;
        
        if( ncDlg != null ) { ncDlg.setAlwaysOnTop( false ) ; }
        int choice = fc.showOpenDialog( this ) ;
        if( ncDlg != null ) { ncDlg.setAlwaysOnTop( true ) ; }
        
        if( choice == JFileChooser.APPROVE_OPTION ) {
            
            File imgFile = fc.getSelectedFile() ;
            if( imgFile != null && imgFile.exists() ) {
                ansImgLabel.setText( imgFile.getName() ) ;
                ansImgFile = imgFile ;
            }
        }
    }
    
    private JFileChooser getImageFileChooser() {
        
        File curJNFile = getMainFrame().getJNPanel().getCurrentFile() ;
        File startDir  = new File( System.getProperty( "user.home" ) ) ;
        
        if( curJNFile != null ) {
            File imgDir = new File( curJNFile.getParentFile(), "img" ) ;
            if( imgDir.exists() ) {
                startDir = imgDir ;
            }
        }
        
        JFileChooser fileChooser = new JFileChooser( startDir ) ;
        fileChooser.setAccessory( new ImagePreview( fileChooser ) ) ;
        fileChooser.setFileFilter( new ImageFilter() ) ;
        fileChooser.setPreferredSize( new Dimension( 700, 400 ) ) ;
        fileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY ) ;
        fileChooser.setMultiSelectionEnabled( false ) ;
        
        return fileChooser ;
    }
}
