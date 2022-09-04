package com.sandy.jnmaker.ui.panels.rawtxt;

import static com.sandy.jnmaker.util.ObjectRepository.getMainFrame ;
import static com.sandy.jnmaker.util.ObjectRepository.getWordRepository ;
import static com.sandy.jnmaker.util.ObjectRepository.setCWD ;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.Font ;
import java.awt.event.KeyAdapter ;
import java.awt.event.KeyEvent ;
import java.io.File ;

import javax.swing.JComponent ;
import javax.swing.JOptionPane ;
import javax.swing.JPanel ;
import javax.swing.JScrollPane ;
import javax.swing.JTextPane ;
import javax.swing.text.BadLocationException ;
import javax.swing.text.Document ;

import org.apache.commons.io.FileUtils ;
import org.apache.log4j.Logger ;

import com.sandy.common.util.StringUtil ;
import com.sandy.jnmaker.ui.MainFrame ;
import com.sandy.jnmaker.ui.helper.UIUtil ;
import com.sandy.jnmaker.util.NoteType ;
import com.sandy.jnmaker.util.ObjectRepository ;
import com.sandy.jnmaker.util.WordRepository.WordSource ;

@SuppressWarnings( {"serial", "deprecation"} )
public class ScratchTextPanel extends JPanel implements WordSource {

    private static final Logger logger = Logger.getLogger( ScratchTextPanel.class ) ;
    
    private JTextPane textPane = new JTextPane() { 
        // This is a work around for the bizzare wrapping behavior of JTextPane.
        // This fix seems to be working, but in future if we see problems 
        // follow the advise here - https://community.oracle.com/message/10692405
        public boolean getScrollableTracksViewportWidth() {
            return true ;
        }        
    } ;

    private String originalText = "" ;
    
    private int  fontSize    = 12 ;
    private File currentFile = null ;
    
    private RawTextPanel rawTextPanel = null ;
    
    public ScratchTextPanel( RawTextPanel rawTextPanel ) {
        
        this.rawTextPanel = rawTextPanel ;
        setUpUI() ;
        ObjectRepository.getWordRepository().addWordSource( this ) ;
    }
    
    JTextPane getTextPane() {
        return this.textPane ;
    }
    
    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize( int fontSize ) {
        this.fontSize = fontSize;
        this.textPane.setFont( new Font( "Courier New", Font.PLAIN, fontSize ) ) ;
    }

    public File getCurrentFile() {
        return currentFile;
    }
    
    public void captureFocus() {
        textPane.requestFocus() ;
    }

    public void setCurrentFile( File file ) {
        
        if( file == null ) {
            this.textPane.setText( "" ) ;
            this.originalText = "" ;
            this.currentFile = null ;
        }
        else {
            try {
                if( !file.exists() ) {
                    FileUtils.touch( file ) ;
                }
                
                String content = FileUtils.readFileToString( file, "UTF-8" ) ;
                this.textPane.setText( content ) ;
                this.originalText = content ;
                this.currentFile  = file ;
                
                setCWD( file.getParentFile() ) ;
                getWordRepository().offer( this.originalText ) ;
            }
            catch( Exception e ) {
                logger.error( "Error while opening file.", e ) ;
                JOptionPane.showConfirmDialog( this, 
                        "Could not open file. " + e.getMessage() ) ;
            }
        }
    }
    
    private void setUpUI() {
        
        setLayout( new BorderLayout() ) ;
        add( getDocumentEditorPanel(), BorderLayout.CENTER ) ;
        UIUtil.setPanelBackground( Color.BLACK, this ) ;
    }
    
    private JComponent getDocumentEditorPanel() {
        
        configureTextArea() ;
        JScrollPane scrollPane = new JScrollPane( textPane, 
                                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER ) ;
        
        scrollPane.getVerticalScrollBar().setUnitIncrement( 5 ) ;
        UIUtil.setScrollBarBackground( UIUtil.EDITOR_BG_COLOR, 
                                       scrollPane.getVerticalScrollBar() ) ;
        return scrollPane ;
    }
    
    private void configureTextArea() {
        
        textPane.setFont( new Font( "Courier", Font.PLAIN, fontSize ) ) ;
        textPane.addKeyListener( new KeyAdapter() {
            @Override public void keyPressed( KeyEvent e ) {
                int keyCode   = e.getKeyCode() ;
                int modifiers = e.getModifiers() ;
                handleEditorControlKeyStrokes( modifiers, keyCode ) ;
            }
        } ) ;
        
        UIUtil.setTextPaneBackground( UIUtil.EDITOR_BG_COLOR, textPane ) ;
        
        textPane.setForeground( UIUtil.STRING_COLOR ) ;
        textPane.setCaretColor( Color.GREEN ) ;
        textPane.setSelectedTextColor( Color.BLACK ) ;
        textPane.setSelectionColor( Color.GRAY );
    }
    
    private void handleEditorControlKeyStrokes( int modifiers, int keyCode ) {
        
        if( modifiers == KeyEvent.CTRL_MASK ) {
            switch( keyCode ) {
                case KeyEvent.VK_S:
                    saveFile() ;
                    break ;
            }
        }
        else if( modifiers == ( KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK ) ) {
            String selectedText = textPane.getSelectedText() ;
            MainFrame mainFrame = getMainFrame() ;
            
            selectedText = getProcessedText( selectedText ) ;
            
            switch( keyCode ) {
                case KeyEvent.VK_A: 
                    mainFrame.createNote( selectedText, NoteType.QA ) ;
                    break ;
                case KeyEvent.VK_Z: 
                    selectedText = collatePossibleAnswerText( selectedText ) ;
                    mainFrame.createNote( selectedText, NoteType.QA_Q ) ;
                    break ;
                case KeyEvent.VK_F: 
                    mainFrame.createNote( selectedText, NoteType.FIB ) ;
                    break ;
                case KeyEvent.VK_T: 
                    mainFrame.createNote( selectedText, NoteType.TRUE_FALSE ) ;
                    break ;
                case KeyEvent.VK_W: 
                    mainFrame.createNote( selectedText, NoteType.WORD_MEANING ) ;
                    break ;
                case KeyEvent.VK_S: 
                    mainFrame.createNote( selectedText, NoteType.SPELLBEE ) ;
                    break ;
                case KeyEvent.VK_D: 
                    mainFrame.createNote( selectedText, NoteType.DEFINITION ) ;
                    break ;
                case KeyEvent.VK_E: 
                    mainFrame.createNote( selectedText, NoteType.EVENT ) ;
                    break ;
                case KeyEvent.VK_X: 
                    mainFrame.createNote( selectedText, NoteType.EXERCISE ) ;
                    break ;
                case KeyEvent.VK_SLASH: 
                    mainFrame.createNote( selectedText, NoteType.COMMENT ) ;
                    break ;
                case KeyEvent.VK_M: 
                    mainFrame.createNote( selectedText, NoteType.MATCHING ) ;
                    break ;
                case KeyEvent.VK_C: 
                    mainFrame.createNote( selectedText, NoteType.MULTI_CHOICE ) ;
                    break ;
                case KeyEvent.VK_N:
                    mainFrame.shiftFocusToNotes() ;
                    break ;
                case KeyEvent.VK_R:
                    mainFrame.shiftFocusToRawText() ;
                    break ;
            }
        }
    }
    
    private String collatePossibleAnswerText( String selectedText ) {
        
        String possibleAnsText = null ;
        String collatedText = selectedText ;
        
        possibleAnsText = this.rawTextPanel.getTextPane().getSelectedText() ;
        if( StringUtil.isNotEmptyOrNull( possibleAnsText ) ) {
            
            if( StringUtil.isNotEmptyOrNull( selectedText ) ) {
                collatedText  = "Question:" + selectedText + "\n\n" ;
                collatedText += "Answer:" + getProcessedText( possibleAnsText.trim() ) ;
            }
        }
        return collatedText ;
    }
    
    private String getProcessedText( String text ) {
        
        text = ( text != null ) ? text.trim() : "" ;
        if( text.length() > 1 ) {
            text = text.substring( 0, 1 ).toUpperCase() + text.substring( 1 ) ;
        }
        return text ;
    }
    
    public boolean isEditorDirty() {
        
        boolean isDirty = false ;
        if( !this.textPane.getText().equals( this.originalText ) ) {
            isDirty = true ;
        }
        return isDirty ;
    }
    
    public void saveFile() {
        
        if( this.currentFile != null ) {
            if( isEditorDirty() ) {
                try {
                    Document doc = this.textPane.getDocument() ; 
                    String txt = doc.getText( 0, doc.getLength() ) ;
                    FileUtils.write( this.currentFile, txt, "UTF-8" ) ;
                    this.originalText = txt ;
                }
                catch( Exception e ) {
                    logger.error( "Could not save file contents", e ) ;
                    JOptionPane.showConfirmDialog( this,  
                          "Could not save file contents. " + e.getMessage() ) ;
                }
            }
        }
    }
    
    public void zoom( boolean zoomIn ) {
        
        if( zoomIn ) {
            this.fontSize += 1 ;
        }
        else {
            this.fontSize -= 1 ;
            if( this.fontSize < 8 ) {
                this.fontSize = 8 ;
            }
        }
        this.textPane.setFont( new Font( "Courier", Font.PLAIN, fontSize ) ) ;
    }

    @Override
    public String getTextForWordRepository() {
        return textPane.getText() ;
    }

    public void closeFile() {
        
        if( isEditorDirty() ) {
            saveFile() ;
        }
        setCurrentFile( null ) ;
    }
    
    public void addText( String text ) {

        if( StringUtil.isNotEmptyOrNull( text ) ) {
            try {
                Document doc = textPane.getDocument() ;
                doc.insertString( textPane.getText().length(), 
                                  text.trim() + "\n", 
                                  null ) ;
            }
            catch( BadLocationException e ) {
                e.printStackTrace();
            }
        }
    }
}
