package com.sandy.jnmaker.ui.panels;

import static com.sandy.jnmaker.ui.helper.UIUtil.getActionBtn ;
import static com.sandy.jnmaker.util.ObjectRepository.getStateMgr ;

import java.awt.BorderLayout ;
import java.awt.Font ;
import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;
import java.awt.event.KeyAdapter ;
import java.awt.event.KeyEvent ;
import java.awt.event.MouseAdapter ;
import java.awt.event.MouseEvent ;
import java.io.File ;
import java.io.IOException ;
import java.nio.file.Files ;

import javax.swing.BoxLayout ;
import javax.swing.JComponent ;
import javax.swing.JFileChooser ;
import javax.swing.JOptionPane ;
import javax.swing.JPanel ;
import javax.swing.JScrollPane ;
import javax.swing.JTextArea ;
import javax.swing.filechooser.FileFilter ;

import org.apache.commons.io.FileUtils ;
import org.apache.log4j.Logger ;

import com.sandy.common.util.StringUtil ;
import com.sandy.jnmaker.ui.MakeNotesPopupMenu ;

public class RawTextPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = -6820796056331113968L ;
    private static final Logger logger = Logger.getLogger( RawTextPanel.class ) ;

    private static final String AC_OPEN_FILE  = "OPEN_FILE" ;
    private static final String AC_CLOSE_FILE = "CLOSE_FILE" ;
    private static final String AC_SAVE_FILE  = "SAVE_FILE" ;
    private static final String AC_ZOOM_IN    = "ZOOM_IN" ;
    private static final String AC_ZOOM_OUT   = "ZOOM_OUT" ;
    
    private JFileChooser fileChooser = new JFileChooser() ;
    private JTextArea    textArea    = new JTextArea() ;
    
    private MakeNotesPopupMenu popup = new MakeNotesPopupMenu( textArea ) ;
    
    private String originalText = null ;
    
    private int  fontSize    = 12 ;
    private File currentFile = null ;
    private File currentDir  = new File( System.getProperty( "user.home" ) ) ;
    
    public RawTextPanel() {
        setUpUI() ;
        setUpFileChooser() ;
    }
    
    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize( int fontSize ) {
        this.fontSize = fontSize;
        this.textArea.setFont( new Font( "Tahoma", Font.PLAIN, fontSize ) ) ;
    }

    public File getCurrentFile() {
        return currentFile;
    }

    public void setCurrentFile( File file ) {
        
        try {
            String content = FileUtils.readFileToString( file ) ;
            this.textArea.setText( content ) ;
            this.originalText = content ;
            this.currentFile  = file ;
            this.currentDir   = file.getParentFile() ;
        }
        catch( Exception e ) {
            logger.error( "Error while opening file.", e ) ;
            JOptionPane.showConfirmDialog( this, 
                               "Could not open file. " + e.getMessage() ) ;
        }
    }
    
    @Override
    public void actionPerformed( ActionEvent e ) {
        
        switch( e.getActionCommand() ) {
            case AC_OPEN_FILE:
                openFile() ;
                break ;
            case AC_CLOSE_FILE :
                closeFile() ;
                break ;
            case AC_SAVE_FILE :
                saveFile() ;
                break ;
            case AC_ZOOM_IN :
                zoom( true ) ;
                break ;
            case AC_ZOOM_OUT :
                zoom( false ) ;
                break ;
        }
    }
    
    private void setUpUI() {
        
        setLayout( new BorderLayout() ) ;
        add( getToolbar(), BorderLayout.WEST ) ;
        add( getDocumentEditorPanel(), BorderLayout.CENTER ) ;
    }
    
    private JComponent getToolbar() {
        
        JPanel panel = new JPanel() ;
        panel.setLayout( new BoxLayout(panel, BoxLayout.Y_AXIS) ) ;
        panel.add( getActionBtn( "file_open",  AC_OPEN_FILE,  this ) ) ;
        panel.add( getActionBtn( "file_close", AC_CLOSE_FILE, this ) ) ;
        panel.add( getActionBtn( "file_save",  AC_SAVE_FILE,  this ) ) ;
        panel.add( getActionBtn( "zoom_in",    AC_ZOOM_IN,    this ) ) ;
        panel.add( getActionBtn( "zoom_out",   AC_ZOOM_OUT,   this ) ) ;
        return panel ;
    }
    
    private JComponent getDocumentEditorPanel() {
        
        configureTextArea() ;
        return new JScrollPane( textArea, 
                                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER ) ;
    }
    
    private void configureTextArea() {
        
        textArea.setEditable( true ) ;
        textArea.setFont( new Font( "Tahoma", Font.PLAIN, fontSize ) );
        textArea.setWrapStyleWord( true ) ;
        textArea.setLineWrap( true ) ;
        textArea.addMouseListener( new MouseAdapter() {
            public void mouseClicked( MouseEvent e ) {
                if( e.getButton() == MouseEvent.BUTTON3 ) {
                    handleMakeNotesTrigger( e ) ;
                }
            }
        } ) ;
        textArea.addKeyListener( new KeyAdapter() {
            @Override public void keyPressed( KeyEvent e ) {
                if( e.getKeyCode()   == KeyEvent.VK_S && 
                    e.getModifiers() == KeyEvent.CTRL_MASK ) {
                    saveFile() ;
                }
            }
        } );
    }
    
    private void setUpFileChooser() {
        
        fileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY ) ;
        fileChooser.setMultiSelectionEnabled( false ) ;
        fileChooser.setFileFilter( new FileFilter() {
            
            @Override
            public String getDescription() {
                return "Text files onlly" ;
            }
            
            @Override
            public boolean accept( File file ) {
                try {
                    String contentType =  Files.probeContentType( file.toPath() ) ;
                    if( file.isDirectory() || contentType.startsWith( "text/" ) ) {
                        return true ;
                    }
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }
                return false ;
            }
        } );
    }
    
    private void openFile() {
        
        if( isEditorDirty() ) {
            if( !userConsentToDiscardChanges() ) {
                return ;
            }
        }
        
        File file = getSelectedFile() ;
        if( file != null ) {
            try {
                String content = FileUtils.readFileToString( file ) ;
                this.textArea.setText( content ) ;
                this.originalText = content ;
                this.currentFile = file ;
                this.currentDir = file.getParentFile() ;
                saveState() ;
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
            if( !this.textArea.getText().equals( this.originalText ) ) {
                isDirty = true ;
            }
        }
        return isDirty ;
    }
    
    public boolean userConsentToDiscardChanges() {
        
        int choice = JOptionPane.showConfirmDialog( this,  
                                 "There are unsaved changes. Ok to discard?" ) ;
        return choice == JOptionPane.OK_OPTION ;
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
    
    private void closeFile() {
        
        if( isEditorDirty() ) {
            if( !userConsentToDiscardChanges() ) {
                return ;
            }
        }
        this.textArea.setText( "" ) ;
        this.currentFile = null ;
        saveState() ;
    }
    
    private void saveFile() {
        
        if( this.currentFile != null ) {
            if( isEditorDirty() ) {
                try {
                    FileUtils.write( this.currentFile, this.textArea.getText() ) ;
                    this.originalText = this.textArea.getText() ;
                }
                catch( IOException e ) {
                    logger.error( "Could not save file contents", e ) ;
                    JOptionPane.showConfirmDialog( this,  
                          "Could not save file contents. " + e.getMessage() ) ;
                }
            }
        }
    }
    
    private void zoom( boolean zoomIn ) {
        
        if( zoomIn ) {
            this.fontSize += 1 ;
        }
        else {
            this.fontSize -= 1 ;
            if( this.fontSize < 8 ) {
                this.fontSize = 8 ;
            }
        }
        this.textArea.setFont( new Font( "Tahoma", Font.PLAIN, fontSize ) ) ;
        saveState() ;
    }

    private void saveState() {
        
        try {
            getStateMgr().saveState() ;
        }
        catch( Exception e ) {
            logger.error( "Could not save state", e ) ;
        }
    }
    
    private void handleMakeNotesTrigger( MouseEvent e ) {
        String selectedText = textArea.getSelectedText() ;
        if( StringUtil.isNotEmptyOrNull( selectedText ) ) {
            popup.show( selectedText.trim(), e.getX(), e.getY() ) ;
        }
    }
}
