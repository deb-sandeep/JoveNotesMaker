package com.sandy.jnmaker.ui.panels;

import static com.sandy.jnmaker.ui.helper.UIUtil.getActionBtn ;
import static com.sandy.jnmaker.util.ObjectRepository.getMainFrame ;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.Font ;
import java.awt.Rectangle ;
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
import javax.swing.JLabel ;
import javax.swing.JOptionPane ;
import javax.swing.JPanel ;
import javax.swing.JScrollPane ;
import javax.swing.JTextPane ;
import javax.swing.SwingUtilities ;
import javax.swing.filechooser.FileFilter ;
import javax.swing.text.BadLocationException ;
import javax.swing.text.Document ;

import org.apache.commons.io.FileUtils ;
import org.apache.commons.lang.StringUtils ;
import org.apache.log4j.Logger ;

import com.sandy.common.util.StringUtil ;
import com.sandy.jnmaker.ui.MainFrame ;
import com.sandy.jnmaker.ui.actions.Actions ;
import com.sandy.jnmaker.ui.helper.UIUtil ;
import com.sandy.jnmaker.util.NoteType ;
import com.sandy.jnmaker.util.ObjectRepository ;
import com.sandy.jnmaker.util.WordRepository.WordSource ;

public class RawTextPanel extends JPanel implements WordSource {

    private static final long serialVersionUID = -6820796056331113968L ;
    private static final Logger logger = Logger.getLogger( RawTextPanel.class ) ;
    
    private static final String BOOKMARK_MARKER = "// here" ;

    @SuppressWarnings( "serial" )
    private JTextPane textPane = new JTextPane() { 
        // This is a work around for the bizzare wrapping behavior of JTextPane.
        // This fix seems to be working, but in future if we see problems 
        // follow the advise here - https://community.oracle.com/message/10692405
        public boolean getScrollableTracksViewportWidth() {
            return true ;
        }        
    } ;
    private JLabel       fileNameLabel = new JLabel() ;
    private JFileChooser fileChooser   = new JFileChooser() ;
    
    private RawTextPanelPopupMenu popup = new RawTextPanelPopupMenu( this ) ;
    
    private String originalText = "" ;
    
    private int  fontSize    = 12 ;
    private File currentFile = null ;
    private File currentDir  = new File( System.getProperty( "user.home" ) ) ;
    
    public RawTextPanel() {
        setUpUI() ;
        setUpFileChooser() ;
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
        this.textPane.setFont( new Font( "Courier", Font.PLAIN, fontSize ) ) ;
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
                String content = FileUtils.readFileToString( file, "UTF-8" ) ;
                this.textPane.setText( content ) ;
                this.originalText = content ;
                this.currentFile  = file ;
                this.currentDir   = file.getParentFile() ;
                
                ObjectRepository.getWordRepository().offer( this.originalText ) ;
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
        
        JPanel  panel   = new JPanel() ;
        Actions actions = ObjectRepository.getUiActions() ;
        
        panel.setLayout( new BoxLayout(panel, BoxLayout.Y_AXIS) ) ;
        
        panel.add( getActionBtn( actions.getNewRawFileAction() ) ) ;
        panel.add( getActionBtn( actions.getOpenRawFileAction() ) ) ;
        panel.add( getActionBtn( actions.getSaveRawFileAction() ) ) ;
        panel.add( getActionBtn( actions.getSaveAsRawFileAction() ) ) ;
        panel.add( getActionBtn( actions.getCloseRawFileAction() ) ) ;
        panel.add( getActionBtn( actions.getZoomInRawAction() ) ) ;
        panel.add( getActionBtn( actions.getZoomOutRawAction() ) ) ;
        
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
    
    private JLabel getFileNameLabel() {
        
        this.fileNameLabel.setBackground( Color.BLACK ) ;
        this.fileNameLabel.setForeground( Color.YELLOW ) ;
        this.fileNameLabel.setText( " " ) ;
        return this.fileNameLabel ;
    }
    
    private void configureTextArea() {
        
        textPane.setFont( new Font( "Courier", Font.PLAIN, fontSize ) ) ;
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
                case KeyEvent.VK_W:
                    closeFile() ;
                    break ;
            }
        }
        else if( modifiers == ( KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK ) ) {
            String selectedText = textPane.getSelectedText() ;
            MainFrame mainFrame = getMainFrame() ;
            
            switch( keyCode ) {
                case KeyEvent.VK_Q: 
                    mainFrame.createNote( selectedText, NoteType.QA ) ;
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
                case KeyEvent.VK_SLASH: 
                    mainFrame.createNote( selectedText, NoteType.COMMENT ) ;
                    break ;
                case KeyEvent.VK_B: 
                    reviseBookmark() ;
                    break ;
                case KeyEvent.VK_N:
                    mainFrame.shiftFocusToNotes() ;
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
        if( !this.textPane.getText().equals( this.originalText ) ) {
            isDirty = true ;
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
                textPane.setCaretPosition( pos ) ;
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

    @Override
    public String getTextForWordRepository() {
        return textPane.getText() ;
    }
}
