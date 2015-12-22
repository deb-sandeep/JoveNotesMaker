package com.sandy.jnmaker.ui.panels;

import static com.sandy.jnmaker.ui.helper.UIUtil.getActionBtn ;

import java.awt.BorderLayout ;
import java.awt.Font ;
import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;
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

public class RawTextPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = -6820796056331113968L ;
    private static final Logger logger = Logger.getLogger( RawTextPanel.class ) ;

    private static final String AC_OPEN_FILE  = "OPEN_FILE" ;
    private static final String AC_CLOSE_FILE = "CLOSE_FILE" ;
    private static final String AC_SAVE_FILE  = "SAVE_FILE" ;
    private static final String AC_ZOOM_IN    = "ZOOM_IN" ;
    private static final String AC_ZOOM_OUT   = "ZOOM_OUT" ;
    
    private File lastOpenedDirectory = new File( System.getProperty( "user.home" ) ) ;
    private JFileChooser fileChooser = new JFileChooser() ;
    private JTextArea textArea       = new JTextArea() ;
    
    private int    fontSize     = 12 ;
    private String originalText = null ;
    private File   currentFile  = null ;
    
    public RawTextPanel() {
        setUpUI() ;
        setUpFileChooser() ;
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
        JScrollPane sp = new JScrollPane( textArea, 
                                          JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, 
                                          JScrollPane.HORIZONTAL_SCROLLBAR_NEVER ) ;
        return sp ;
    }
    
    private void configureTextArea() {
        textArea.setEditable( true ) ;
        textArea.setFont( new Font( "Tahoma", Font.PLAIN, fontSize ) );
        textArea.setWrapStyleWord( true ) ;
        textArea.setLineWrap( true ) ;
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
    
    public void actionPerformed( ActionEvent e ) {
        String actionCmd = e.getActionCommand() ;
        switch( actionCmd ) {
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
            }
            catch( IOException e ) {
                logger.error( "Could not read file contents", e ) ;
                JOptionPane.showConfirmDialog( this, 
                                   "Could not open file. " + e.getMessage() ) ;
            }
        }
    }
    
    private boolean isEditorDirty() {
        
        boolean isDirty = false ;
        if( this.currentFile != null ) {
            if( !this.textArea.getText().equals( this.originalText ) ) {
                isDirty = true ;
            }
        }
        return isDirty ;
    }
    
    private boolean userConsentToDiscardChanges() {
        
        int choice = JOptionPane.showConfirmDialog( this,  
                                 "There are unsaved changes. Ok to discard?" ) ;
        return choice == JOptionPane.OK_OPTION ;
    }
    
    private File getSelectedFile() {
        
        File selectedFile = null ;
        
        fileChooser.setCurrentDirectory( this.lastOpenedDirectory ) ;
        int userChoice = fileChooser.showOpenDialog( this ) ;
        if( userChoice == JFileChooser.APPROVE_OPTION ) {
            this.lastOpenedDirectory = fileChooser.getCurrentDirectory() ;
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
    }
    
    private void saveFile() {
        
        if( this.currentFile != null ) {
            if( isEditorDirty() ) {
                try {
                    FileUtils.write( this.currentFile, this.textArea.getText() ) ;
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
        this.textArea.setFont( new Font( "Tahoma", Font.PLAIN, fontSize ) );
    }
}
