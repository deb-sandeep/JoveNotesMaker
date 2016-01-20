package com.sandy.jnmaker.ui.panels ;

import static com.sandy.jnmaker.ui.helper.UIUtil.getActionBtn ;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.Font ;
import java.awt.event.KeyAdapter ;
import java.awt.event.KeyEvent ;
import java.io.File ;

import javax.swing.BoxLayout ;
import javax.swing.JComponent ;
import javax.swing.JFileChooser ;
import javax.swing.JLabel ;
import javax.swing.JOptionPane ;
import javax.swing.JPanel ;
import javax.swing.JScrollPane ;
import javax.swing.filechooser.FileFilter ;
import javax.swing.text.BadLocationException ;
import javax.swing.text.DefaultCaret ;
import javax.swing.text.Document ;

import org.apache.commons.io.FileUtils ;
import org.apache.commons.lang.StringUtils ;
import org.apache.log4j.Logger ;

import com.sandy.common.util.StringUtil ;
import com.sandy.jnmaker.ui.actions.Actions ;
import com.sandy.jnmaker.ui.helper.UIUtil ;
import com.sandy.jnmaker.util.ObjectRepository ;

public class JoveNotesPanel extends JPanel {

    private static final long serialVersionUID = -6820796056331113968L;
    private static final Logger logger = Logger.getLogger( RawTextPanel.class ) ;

    private JFileChooser      fileChooser   = new JFileChooser() ;
    private JLabel            fileNameLabel = new JLabel() ;
    private JoveNotesTextPane textPane      = new JoveNotesTextPane() ;
    
    private String originalText = null ;
    
    private int  fontSize    = 12 ;
    private File currentFile = null ;
    private File currentDir  = new File( System.getProperty( "user.home" ) ) ;
    
    public JoveNotesPanel() {
        setUpUI() ;
        setUpFileChooser() ;
    }
    
    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize( int fontSize ) {
        this.fontSize = fontSize;
        this.textPane.setFont( new Font( "Courier", Font.PLAIN, fontSize ) ) ;
    }

    public File getCurrentFile() {
        return currentFile;
    }

    public void setCurrentFile( File file ) {
        
        if( file == null ) {
            this.textPane.setText( "" ) ;
            this.originalText = "" ;
            this.currentFile = null ;
            this.fileNameLabel.setText( "** Scratch file **" ) ;
        }
        else {
            try {
                String content = FileUtils.readFileToString( file, "UTF-8" ) ;
                this.textPane.setText( content ) ;
                this.originalText = content ;
                this.currentFile  = file ;
                this.currentDir   = file.getParentFile() ;
                
                ObjectRepository.getWordRepository().offer( this.originalText ) ;
            }
            catch( Exception e ) {
                logger.error( "Error while opening file.", e ) ;
                JOptionPane.showConfirmDialog( this, 
                        "Could not open file. " + e.getMessage() ) ;
            }
        }
        displayFileName() ;
    }
    
    private void displayFileName() {
        
        String labelText = "" ;
        if( this.currentFile == null ) {
            labelText = "** Scratch file **" ;
        }
        else {
            labelText = this.currentFile.getAbsolutePath() ;
            if( labelText.length() > 80 ) {
                labelText = "... " + StringUtils.right( labelText, 75 ) ; 
            }
        }
        
        this.fileNameLabel.setText( " [File] " + labelText ) ;
    }
    
    private void setUpUI() {
        
        setLayout( new BorderLayout() ) ;
        add( getToolbar(), BorderLayout.WEST ) ;
        add( getDocumentEditorPanel(), BorderLayout.CENTER ) ;
        add( getFileNameLabel(), BorderLayout.NORTH ) ;
        
        UIUtil.setPanelBackground( Color.BLACK, this ) ;
        displayFileName() ;
    }
    
    private JComponent getToolbar() {
        
        JPanel panel = new JPanel() ;
        Actions actions = ObjectRepository.getUiActions() ;
        
        panel.setLayout( new BoxLayout(panel, BoxLayout.Y_AXIS) ) ;
        
        panel.add( getActionBtn( actions.getNewJNFileAction() ) ) ;
        panel.add( getActionBtn( actions.getOpenJNFileAction() ) ) ;
        panel.add( getActionBtn( actions.getSaveJNFileAction() ) ) ;
        panel.add( getActionBtn( actions.getSaveAsJNFileAction() ) ) ;
        panel.add( getActionBtn( actions.getCloseJNFileAction() ) ) ;
        panel.add( getActionBtn( actions.getZoomInJNAction() ) ) ;
        panel.add( getActionBtn( actions.getZoomOutJNAction() ) ) ;
        
        UIUtil.setPanelBackground( UIUtil.EDITOR_BG_COLOR, panel ) ;
        
        return panel ;
    }
    
    private JComponent getDocumentEditorPanel() {
        
        configureTextArea() ;
        JScrollPane sp = new JScrollPane( textPane, 
                                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER ) ;
        UIUtil.setScrollBarBackground( UIUtil.EDITOR_BG_COLOR, 
                                       sp.getVerticalScrollBar() ) ;
        return sp ;
    }
    
    private JLabel getFileNameLabel() {
        
        this.fileNameLabel.setBackground( Color.BLACK ) ;
        this.fileNameLabel.setForeground( Color.YELLOW ) ;
        this.fileNameLabel.setText( " " ) ;
        return this.fileNameLabel ;
    }
    
    private void configureTextArea() {
        
        UIUtil.setTextPaneBackground( UIUtil.EDITOR_BG_COLOR, textPane ) ;
        
        textPane.setFont( new Font( "Courier", Font.PLAIN, fontSize ) ) ;
        textPane.addKeyListener( new KeyAdapter() {
            @Override public void keyPressed( KeyEvent e ) {
                if( e.getKeyCode()   == KeyEvent.VK_S && 
                    e.getModifiers() == KeyEvent.CTRL_MASK ) {
                    saveFile() ;
                }
                else if( e.getKeyCode()   == KeyEvent.VK_R && 
                         e.getModifiers() == ( KeyEvent.CTRL_MASK | 
                                               KeyEvent.SHIFT_MASK ) ) {
                    ObjectRepository.getMainFrame().shiftFocusToRawText() ;
                }
            }
        } );
        
        DefaultCaret caret = ( DefaultCaret )textPane.getCaret() ;
        caret.setUpdatePolicy( DefaultCaret.ALWAYS_UPDATE ) ;    
    }
    
    private void setUpFileChooser() {
        
        fileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY ) ;
        fileChooser.setMultiSelectionEnabled( false ) ;
        fileChooser.setFileFilter( new FileFilter() {
            
            @Override
            public String getDescription() {
                return "JoveNotes source files" ;
            }
            
            @Override
            public boolean accept( File file ) {
                if( file.isDirectory() || file.getName().endsWith( ".jn" ) ) {
                    return true ;
                }
                return false ;
            }
        } );
    }
    
    public void newFile() {
        
        if( isEditorDirty() ) {
            if( !handleDirtyFileOnExit() ) {
                return ;
            }
        }
        setCurrentFile( null ) ;
    }
    
    public void openFile() {
        
        if( isEditorDirty() ) {
            if( !handleDirtyFileOnExit() ) {
                return ;
            }
        }
        
        File file = getSelectedFile() ;
        if( file != null ) {
            try {
                setCurrentFile( file ) ;
            }
            catch( Exception e ) {
                logger.error( "Error while opening file.", e ) ;
                JOptionPane.showConfirmDialog( this, 
                                   "Could not open file. " + e.getMessage() ) ;
            }
        }
    }
    
    public boolean isEditorDirty() {
        
        boolean isDirty = false ;
        if( this.currentFile != null ) {
            if( !this.textPane.getText().equals( this.originalText ) ) {
                isDirty = true ;
            }
        }
        else {
            if( StringUtil.isNotEmptyOrNull( this.textPane.getText() ) ) {
                isDirty = true ;
            }
        }
        return isDirty ;
    }
    
    public boolean handleDirtyFileOnExit() {
        
        int choice = JOptionPane.showConfirmDialog( this,  
                "There are unsaved changes. Save before exit?\n" + 
                "Yes to save, No to discard and Cancel to abort exit." ) ;
   
        if( choice == JOptionPane.CANCEL_OPTION ) {
            return false ;
        }
        else if( choice == JOptionPane.OK_OPTION ) {
            saveFile() ;
        }
        return true ;
    }
    
    private File getSelectedFile() {
        
        File selectedFile = null ;
        
        fileChooser.setCurrentDirectory( this.currentDir ) ;
        int userChoice = fileChooser.showOpenDialog( this ) ;
        if( userChoice == JFileChooser.APPROVE_OPTION ) {
            this.currentDir = fileChooser.getCurrentDirectory() ;
            selectedFile = fileChooser.getSelectedFile() ;
        }
        return selectedFile ;
    }
    
    public void closeFile() {
        
        if( isEditorDirty() ) {
            if( !handleDirtyFileOnExit() ) {
                return ;
            }
        }
        setCurrentFile( null ) ;
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
        else {
            saveFileAs() ;
        }
    }
    
    public void saveFileAs() {
        
        fileChooser.setCurrentDirectory( this.currentDir ) ;
        fileChooser.setDialogTitle( "Save file as" );
        int userChoice = fileChooser.showSaveDialog( this ) ;
        if( userChoice == JFileChooser.APPROVE_OPTION ) {
            this.currentDir = fileChooser.getCurrentDirectory() ;
            File selectedFile = fileChooser.getSelectedFile() ;
            
            try {
                Document doc = this.textPane.getDocument() ; 
                String txt = doc.getText( 0, doc.getLength() ) ;
                FileUtils.write( selectedFile, txt, "UTF-8" ) ;
                
                this.originalText = txt ;
                this.currentFile = selectedFile ;
                displayFileName() ;
            }
            catch( Exception e ) {
                logger.error( "Could not save file contents", e ) ;
                JOptionPane.showConfirmDialog( this,  
                      "Could not save file contents. " + e.getMessage() ) ;
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

    public void addNote( String fmtNote ) {
        
        try {
            Document doc = textPane.getDocument() ;
            doc.insertString( textPane.getCaretPosition(), fmtNote, null ) ;
        }
        catch( BadLocationException e ) {
            e.printStackTrace();
        }
    }

    public void captureFocus() {
        textPane.requestFocus() ;
    }
}
