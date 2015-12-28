package com.sandy.jnmaker.ui.panels;

import static com.sandy.jnmaker.ui.helper.UIUtil.getActionBtn ;
import static com.sandy.jnmaker.util.ObjectRepository.* ;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.Font ;
import java.awt.Rectangle ;
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
import javax.swing.JTextPane ;
import javax.swing.SwingUtilities ;
import javax.swing.filechooser.FileFilter ;
import javax.swing.text.BadLocationException ;
import javax.swing.text.Document ;

import org.apache.commons.io.FileUtils ;
import org.apache.log4j.Logger ;

import com.sandy.common.util.StringUtil ;
import com.sandy.jnmaker.ui.MainFrame ;
import com.sandy.jnmaker.ui.helper.UIUtil ;
import com.sandy.jnmaker.util.NoteType ;

public class RawTextPanel extends JPanel implements ActionListener {

    private static final long serialVersionUID = -6820796056331113968L ;
    private static final Logger logger = Logger.getLogger( RawTextPanel.class ) ;
    
    private static final String BOOKMARK_MARKER = "// here" ;

    private static final String AC_OPEN_FILE  = "OPEN_FILE" ;
    private static final String AC_CLOSE_FILE = "CLOSE_FILE" ;
    private static final String AC_SAVE_FILE  = "SAVE_FILE" ;
    private static final String AC_ZOOM_IN    = "ZOOM_IN" ;
    private static final String AC_ZOOM_OUT   = "ZOOM_OUT" ;
    
    JTextPane textPane = new JTextPane() ;
    private JFileChooser fileChooser = new JFileChooser() ;
    
    private RawTextPanelPopupMenu popup = new RawTextPanelPopupMenu( this ) ;
    
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
        this.textPane.setFont( new Font( "Tahoma", Font.PLAIN, fontSize ) ) ;
    }

    public File getCurrentFile() {
        return currentFile;
    }

    public void setCurrentFile( File file ) {
        
        try {
            String content = FileUtils.readFileToString( file, "UTF-8" ) ;
            this.textPane.setText( content ) ;
            this.originalText = content ;
            this.currentFile  = file ;
            this.currentDir   = file.getParentFile() ;
            SwingUtilities.invokeLater( new Runnable() {
                @Override
                public void run() {
                    scrollToLastOpPosition() ;
                }
            } );
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
        UIUtil.setPanelBackground( Color.BLACK, this ) ;
    }
    
    private JComponent getToolbar() {
        
        JPanel panel = new JPanel() ;
        panel.setLayout( new BoxLayout(panel, BoxLayout.Y_AXIS) ) ;
        panel.add( getActionBtn( "file_open",  AC_OPEN_FILE,  this ) ) ;
        panel.add( getActionBtn( "file_close", AC_CLOSE_FILE, this ) ) ;
        panel.add( getActionBtn( "file_save",  AC_SAVE_FILE,  this ) ) ;
        panel.add( getActionBtn( "zoom_in",    AC_ZOOM_IN,    this ) ) ;
        panel.add( getActionBtn( "zoom_out",   AC_ZOOM_OUT,   this ) ) ;
        
        UIUtil.setPanelBackground( UIUtil.EDITOR_BG_COLOR, panel ) ;
        
        return panel ;
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
        
        textPane.setEditable( true ) ;
        textPane.setFont( new Font( "Tahoma", Font.PLAIN, fontSize ) ) ;
        textPane.addMouseListener( new MouseAdapter() {
            public void mouseClicked( MouseEvent e ) {
                if( e.getButton() == MouseEvent.BUTTON3 ) {
                    handleMakeNotesTrigger( e ) ;
                }
            }
        } ) ;
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
            
            switch( keyCode ) {
                case KeyEvent.VK_Q: // @qa
                    mainFrame.createNote( selectedText, NoteType.QA ) ;
                    break ;
                case KeyEvent.VK_F: // @fib
                    mainFrame.createNote( selectedText, NoteType.FIB ) ;
                    break ;
                case KeyEvent.VK_T: // @true_false
                    mainFrame.createNote( selectedText, NoteType.TRUE_FALSE ) ;
                    break ;
                case KeyEvent.VK_W: // @wm
                    mainFrame.createNote( selectedText, NoteType.WORD_MEANING ) ;
                    break ;
                case KeyEvent.VK_S: // @spellbee
                    mainFrame.createNote( selectedText, NoteType.SPELLBEE ) ;
                    break ;
                case KeyEvent.VK_D: // @definition
                    mainFrame.createNote( selectedText, NoteType.DEFINITION ) ;
                    break ;
                case KeyEvent.VK_E: // @event
                    mainFrame.createNote( selectedText, NoteType.EVENT ) ;
                    break ;
                case KeyEvent.VK_C: // comment
                    mainFrame.createNote( selectedText, NoteType.COMMENT ) ;
                    break ;
                case KeyEvent.VK_B: // Bookmark
                    reviseBookmark() ;
                    break ;
            }
        }
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
                setCurrentFile( file ) ;
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
            if( !this.textPane.getText().equals( this.originalText ) ) {
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
        this.textPane.setText( "" ) ;
        this.currentFile = null ;
        saveState() ;
    }
    
    private void saveFile() {
        
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
        this.textPane.setFont( new Font( "Tahoma", Font.PLAIN, fontSize ) ) ;
        saveState() ;
    }

    private void saveState() {
        
        try {
            if( getStateMgr() != null ) {
                getStateMgr().saveState() ;
            }
        }
        catch( Exception e ) {
            logger.error( "Could not save state", e ) ;
        }
    }
    
    private void handleMakeNotesTrigger( MouseEvent e ) {
        String selectedText = textPane.getSelectedText() ;
        popup.enableJNMenuItems( StringUtil.isNotEmptyOrNull( selectedText ) ) ;
        popup.show( selectedText, e.getX(), e.getY() ) ;
    }
    
    public void scrollToLastOpPosition() {
        
        Document document = textPane.getDocument() ;
        
        try {
            int pos = document.getText( 0, document.getLength() )
                              .toLowerCase()
                              .indexOf( BOOKMARK_MARKER ) ;
            if( pos > -1 ){
                Rectangle viewRect = textPane.modelToView( pos ) ;
                viewRect.y += textPane.getVisibleRect().height - 20 ;
                textPane.scrollRectToVisible( viewRect ) ;
            }
        } 
        catch ( Exception e ) {
            e.printStackTrace() ;
        }
    }
    
    public void reviseBookmark() {
        
        try {
            Document document = textPane.getDocument() ;
            int pos = document.getText( 0, document.getLength() )
                    .toLowerCase()
                    .indexOf( BOOKMARK_MARKER ) ;
            
            if( pos > -1 ) {
                document.remove( pos, BOOKMARK_MARKER.length() ) ;
            }
            
            document.insertString( textPane.getCaretPosition(), 
                                   BOOKMARK_MARKER, null ) ;
        }
        catch( BadLocationException e ) {
            e.printStackTrace();
        }
    }
}
