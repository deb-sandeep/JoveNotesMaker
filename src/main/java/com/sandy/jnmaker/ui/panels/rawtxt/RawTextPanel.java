package com.sandy.jnmaker.ui.panels.rawtxt;

import static com.sandy.common.ui.SwingUtils.getScreenHeight ;
import static com.sandy.jnmaker.ui.helper.UIUtil.getActionBtn ;
import static com.sandy.jnmaker.util.JNSrcTokenizer.FIB ;
import static com.sandy.jnmaker.util.ObjectRepository.getCWD ;
import static com.sandy.jnmaker.util.ObjectRepository.getMainFrame ;
import static com.sandy.jnmaker.util.ObjectRepository.getWordRepository ;
import static com.sandy.jnmaker.util.ObjectRepository.setCWD ;

import java.awt.*;
import java.awt.event.KeyAdapter ;
import java.awt.event.KeyEvent ;
import java.awt.event.MouseAdapter ;
import java.awt.event.MouseEvent ;
import java.io.File ;
import java.io.IOException ;
import java.nio.file.Files ;
import java.util.List;

import javax.swing.BoxLayout ;
import javax.swing.JComponent ;
import javax.swing.JFileChooser ;
import javax.swing.JLabel ;
import javax.swing.JOptionPane ;
import javax.swing.JPanel ;
import javax.swing.JScrollPane ;
import javax.swing.JSplitPane ;
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
import com.sandy.jnmaker.ui.helper.UIUtil ;
import com.sandy.jnmaker.ui.menu.ToggleInputEditorMenu.InputEditorMode ;
import com.sandy.jnmaker.ui.menu.actions.Actions ;
import com.sandy.jnmaker.util.NoteType ;
import com.sandy.jnmaker.util.ObjectRepository ;
import com.sandy.jnmaker.util.WordRepository.WordSource ;

import static javax.swing.JOptionPane.* ;

@SuppressWarnings( {"deprecation"} )
public class RawTextPanel extends JPanel implements WordSource {

    private static final Logger log = Logger.getLogger( RawTextPanel.class ) ;
    
    private static final String BOOKMARK_MARKER = "// here" ;

    private final JTextPane textPane = new JTextPane() {
        // This is a work around for the bizzare wrapping behavior of JTextPane.
        // This fix seems to be working, but in future if we see problems 
        // follow the advise here - https://community.oracle.com/message/10692405
        public boolean getScrollableTracksViewportWidth() {
            return true ;
        }        
    } ;
    private final JLabel fileNameLabel = new JLabel() ;
    private final JFileChooser fileChooser   = new JFileChooser() ;
    private final RawTextPanelPopupMenu popup = new RawTextPanelPopupMenu( this ) ;
    private final ScratchTextPanel scratchPanel = new ScratchTextPanel( this ) ;

    private String originalText = "" ;
    private int  fontSize = 12 ;
    private File currentFile = null ;
    private String lastSearchString = null ;
    
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
        
        Font newFont = new Font( "Trebuchet MS", Font.PLAIN, fontSize ) ;
        this.textPane.setFont( newFont ) ;
        this.scratchPanel.setFontSize( this.fontSize ) ;
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
            this.scratchPanel.setCurrentFile( null ) ;
        }
        else {
            try {
                String content = FileUtils.readFileToString( file, "UTF-8" ) ;
                this.textPane.setText( content ) ;
                this.originalText = content ;
                this.currentFile  = file ;
                
                setCWD( file.getParentFile() ) ;
                getWordRepository().offer( this.originalText ) ;
                scrollToBookmarkPosition() ;
                
                setScratchFile() ;
            }
            catch( Exception e ) {
                log.error( "Error while opening file.", e ) ;
                JOptionPane.showConfirmDialog( this, 
                        "Could not open file. " + e.getMessage() ) ;
            }
        }
        displayFileName() ;
    }
    
    private void setScratchFile() {
        
        File parentDir = this.currentFile.getParentFile() ;
        File scratchFile = new File( parentDir, "scratch.txt" ) ;
        this.scratchPanel.setCurrentFile( scratchFile ) ;
    }
    
    private void displayFileName() {
        
        String labelText ;
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
        panel.setOpaque( true ) ;
        panel.setBackground( Color.GRAY ) ;
        
        panel.add( getActionBtn( actions.getNewRawFileAction() ) ) ;
        panel.add( getActionBtn( actions.getOpenRawFileAction() ) ) ;
        panel.add( getActionBtn( actions.getSaveRawFileAction() ) ) ;
        panel.add( getActionBtn( actions.getCloseRawFileAction() ) ) ;
        panel.add( getActionBtn( actions.getZoomInRawAction() ) ) ;
        panel.add( getActionBtn( actions.getZoomOutRawAction() ) ) ;
        
        UIUtil.setPanelBackground( UIUtil.EDITOR_BG_COLOR, panel ) ;
        
        return panel ;
    }
    
    private JComponent getDocumentEditorPanel() {
        
        JSplitPane splitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT ) ; 
        splitPane.setDividerSize( 5 ) ;
        splitPane.setOneTouchExpandable( true ) ;
        splitPane.add( getRawTextEditorPanel() ) ;
        splitPane.add( scratchPanel ) ;
        splitPane.setDividerLocation( getScreenHeight() );

        return splitPane ;
    }
    
    private JComponent getRawTextEditorPanel() {
        
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
        textPane.setSelectedTextColor( Color.BLACK ) ;
        textPane.setSelectionColor( Color.GRAY );
    }
    
    private String getProcessedText( String text ) {
        
        text = ( text != null ) ? text.trim() : "" ;
        if( text.length() > 1 ) {
            text = text.substring( 0, 1 ).toUpperCase() + text.substring( 1 ) ;
        }
        return text ;
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
                case KeyEvent.VK_F:
                    find() ;
                    break ;
                case KeyEvent.VK_N:
                    findNext() ;
                    break ;
                case KeyEvent.VK_G:
                    findNextSelected() ;
                    break ;
            }
        }
        else if( modifiers == ( KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK ) ) {
            
            String selectedText = textPane.getSelectedText() ;
            MainFrame mainFrame = getMainFrame() ;
            
            selectedText = getProcessedText( selectedText ) ;
            
            switch( keyCode ) {
                case KeyEvent.VK_Q: 
                    deduceAndCreateNoteType( selectedText ) ;
                    break ;
                case KeyEvent.VK_A: 
                    mainFrame.createNote( selectedText, NoteType.QA_A ) ;
                    break ;
                case KeyEvent.VK_Z: 
                    mainFrame.createNote( selectedText, NoteType.QA_Q ) ;
                    break ;
                case KeyEvent.VK_F: 
                    if( StringUtil.isEmptyOrNull( selectedText ) ) {
                        selectedText = getSanitizedCurrentLine() ;
                    }
                    mainFrame.createNote( selectedText, NoteType.FIB ) ;
                    break ;
                case KeyEvent.VK_T: 
                    if( StringUtil.isEmptyOrNull( selectedText ) ) {
                        selectedText = getSanitizedCurrentLine() ;
                    }
                    mainFrame.createNote( selectedText, NoteType.TRUE_FALSE ) ;
                    break ;
                case KeyEvent.VK_W: 
                    mainFrame.createNote( selectedText, NoteType.WORD_MEANING ) ;
                    break ;
                case KeyEvent.VK_P: 
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
                case KeyEvent.VK_B: 
                    reviseBookmark() ;
                    break ;
                case KeyEvent.VK_G: 
                    scrollToBookmarkPosition() ;
                    break ;
                case KeyEvent.VK_N:
                    mainFrame.shiftFocusToNotes() ;
                    break ;
                case KeyEvent.VK_I:
                    this.scratchPanel.captureFocus() ;
                    break ;
                case KeyEvent.VK_DOWN:
                    this.scratchPanel.addText( selectedText ) ;
                    break ;
                case KeyEvent.VK_S:
                    mainFrame.switchInputEditor( InputEditorMode.SEARCH, 
                                                 "\"" + selectedText + "\"" ) ;
                    break ;
            }
        }
        else if( modifiers == ( KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK | KeyEvent.ALT_MASK ) ) {
            if( keyCode == KeyEvent.VK_Q ) {
                parseAndDeduceNotes() ;
            }
        }
        else if( keyCode == KeyEvent.VK_F5 ) {
            setCurrentFile( this.currentFile ) ;
        }
    }

    private void parseAndDeduceNotes() {
        String rawText = textPane.getText() ;
        RawTextParser rawTextParser = new RawTextParser( rawText ) ;
        List<String> metaNotes = rawTextParser.getParsedMetaNotes() ;

        for( String metaNote : metaNotes ) {
            deduceAndCreateNoteType( metaNote ) ;
        }
    }
    
    private void deduceAndCreateNoteType( String selectedText ) {
        
        try {
            selectedText = selectedText.trim() ;
            if( StringUtil.isEmptyOrNull( selectedText ) ) {
                selectedText = getCurrentLine() ;
                selectedText = getProcessedText( selectedText ) ;
            }
            
            MainFrame main = getMainFrame() ;

            if( selectedText.startsWith( "@as-is" ) ) {
                selectedText = selectedText.substring( "@as-is".length() ).trim() ;
                main.createNote( selectedText, NoteType.AS_IS ) ;
            }
            else if( selectedText.startsWith( FIB ) ) {
                selectedText = selectedText.substring( FIB.length() ).trim() ;
                main.createNote( selectedText, NoteType.FIB ) ;
            }
            else if( selectedText.startsWith( "@tf" ) ) {
                selectedText = selectedText.substring( "@tf".length() ).trim() ;
                main.createNote( selectedText, NoteType.TRUE_FALSE ) ;
            }
            else if( selectedText.startsWith( "@true" ) ) {
                selectedText = selectedText.substring( "@true".length() ).trim() ;
                main.createNote( selectedText, NoteType.TRUE_FALSE ) ;
            }
            else if( selectedText.startsWith( "@false" ) ) {
                selectedText = selectedText.substring( "@false".length() ).trim() ;
                main.createNote( selectedText, NoteType.TRUE_FALSE ) ;
            }
            else if( selectedText.startsWith( "@section" ) ) {
                selectedText = selectedText.substring( "@section".length() ).trim() ;
                main.createNote( selectedText, NoteType.SECTION ) ;
            }
            else if( selectedText.startsWith( "@def" ) ) {
                selectedText = selectedText.substring( "@def".length() ).trim() ;
                main.createNote( selectedText, NoteType.DEFINITION ) ;
            }
            else if( selectedText.startsWith( "@qa" ) ) {
                selectedText = selectedText.substring( "@qa".length() ).trim() ;
                main.createNote( selectedText, NoteType.QA ) ;
            }
            else if( selectedText.startsWith( "@match" ) ) {
                selectedText = selectedText.substring( "@match".length() ).trim() ;
                main.createNote( selectedText, NoteType.MATCHING ) ;
            }
            else if( selectedText.startsWith( "@choice_group" ) ) {
                selectedText = selectedText.substring( "@choice_group".length() ).trim() ;
                main.createNote( selectedText, NoteType.CHOICE_GROUP ) ;
            }
            else if( selectedText.startsWith( "@choice" ) ) {
                selectedText = selectedText.substring( "@choice".length() ).trim() ;
                main.createNote( selectedText, NoteType.MULTI_CHOICE ) ;
            }
            else if( selectedText.startsWith( "@chem_equation" ) ) {
                selectedText = selectedText.substring( "@chem_equation".length() ).trim() ;
                main.createNote( selectedText, NoteType.CHEM_EQUATION ) ;
            }
            else if( selectedText.startsWith( "@chem_compound" ) ) {
                selectedText = selectedText.substring( "@chem_compound".length() ).trim() ;
                main.createNote( selectedText, NoteType.CHEM_COMPOUND ) ;
            }
        }
        catch( Exception e ) {
            log.error( "Error in deducing question", e ) ;
        }
    }
    
    private String getCurrentLine() throws Exception {
        
        int      caretPos = textPane.getCaret().getDot() ;
        Document doc      = textPane.getDocument() ;
        String   text     = doc.getText( 0, doc.getLength() ) ;
        int      textLen  = text.length() ;
        
        int startPos = caretPos ;
        int endPos   = caretPos ;
        
        char currentChar = text.charAt( startPos ) ;
        while( startPos >= 0 && currentChar != '\n' ) {
            startPos-- ;
            if( startPos >= 0 ) {
                currentChar = text.charAt( startPos ) ;
            }
        }
        
        currentChar = text.charAt( endPos ) ;
        while( endPos < textLen && currentChar != '\n' ) {
            endPos++ ;
            if( endPos < textLen ) {
                currentChar = text.charAt( endPos ) ;
            }
        }
        
        startPos = Math.max( startPos, 0 );
        endPos = Math.min( endPos, textLen );

        return text.substring( startPos, endPos );
    }
    
    private String getSanitizedCurrentLine() {
        
        String currentLine = "" ;
        
        try {
            currentLine = getCurrentLine() ;
            if( StringUtil.isNotEmptyOrNull( currentLine ) ) {
                currentLine = currentLine.trim() ;
                if( currentLine.startsWith( "@" ) ) {
                    int firstSpaceIndex = currentLine.indexOf( ' ' ) ;
                    if( firstSpaceIndex != -1 ) {
                        currentLine = currentLine.substring( firstSpaceIndex+1 ) ;
                        currentLine = currentLine.trim() ;
                    }
                }
            }
        }
        catch( Exception e ) {
            log.debug( "Error getting current line", e ) ;
        }
        
        return currentLine ;
    }
    
    private void setUpFileChooser() {
        
        fileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY ) ;
        fileChooser.setMultiSelectionEnabled( false ) ;
        fileChooser.setFileFilter( new FileFilter() {
            
            @Override
            public String getDescription() {
                return "JN OCR Files Oonly" ;
            }
            
            @Override
            public boolean accept( File file ) {
                try {
                    if( file.getName().endsWith( ".jn-ocr" ) ) {
                        return true ;
                    }
                    else {
                        String contentType =  Files.probeContentType( file.toPath() ) ;
                        if( file.isDirectory() || 
                                ( contentType != null && 
                                contentType.startsWith( "text/" ) ) ) {
                            return true ;
                        }
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
                log.error( "Error while opening file.", e ) ;
                JOptionPane.showConfirmDialog( this, 
                                   "Could not open file. " + e.getMessage() ) ;
            }
        }
    }
    
    public boolean isEditorDirty() {
        
        boolean isDirty = !this.textPane.getText().equals( this.originalText );
        return isDirty | this.scratchPanel.isEditorDirty() ;
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
        
        fileChooser.setCurrentDirectory( getCWD() ) ;
        int userChoice = fileChooser.showOpenDialog( this ) ;
        if( userChoice == JFileChooser.APPROVE_OPTION ) {
            setCWD( fileChooser.getCurrentDirectory() ) ;
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
                    
                    this.scratchPanel.saveFile() ;
                }
                catch( Exception e ) {
                    log.error( "Could not save file contents", e ) ;
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
        
        fileChooser.setCurrentDirectory( getCWD() ) ;
        fileChooser.setDialogTitle( "Save file as" );
        int userChoice = fileChooser.showSaveDialog( this ) ;
        if( userChoice == JFileChooser.APPROVE_OPTION ) {
            setCWD( fileChooser.getCurrentDirectory() ) ;
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
                log.error( "Could not save file contents", e ) ;
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
        
        this.setFontSize( this.fontSize ) ;
    }

    private void handleMakeNotesTrigger( MouseEvent e ) {
        String selectedText = textPane.getSelectedText() ;
        popup.enableJNMenuItems( StringUtil.isNotEmptyOrNull( selectedText ) ) ;
        popup.show( selectedText, e.getX(), e.getY() ) ;
    }
    
    public void scrollToBookmarkPosition() {
        
        SwingUtilities.invokeLater( () -> {
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
        } );
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
    
    private void find() {
        
        String selText = textPane.getSelectedText() ;
        if( StringUtil.isEmptyOrNull( selText ) ) {
            selText = this.lastSearchString ;
        }
        
        final String searchString = showInputDialog( this, 
                                                     "Input search phrase", 
                                                     selText ) ;
        
        if( StringUtil.isNotEmptyOrNull( searchString ) ) {
           scrollToText( searchString, textPane.getCaretPosition() ) ;
           lastSearchString = searchString ;
        }
    }
    
    private void findNext() {
        
        if( StringUtil.isNotEmptyOrNull( lastSearchString ) ) {
            scrollToText( lastSearchString, textPane.getCaretPosition() ) ;
        }
    }
    
    private void findNextSelected() {
        
        String selText = textPane.getSelectedText() ;
        if( StringUtil.isNotEmptyOrNull( selText ) ) {
            scrollToText( selText, textPane.getCaretPosition() ) ;
        }
    }
    
    private void scrollToText( final String text, final int fromPos ) {
        
        SwingUtilities.invokeLater( () -> {
            Document document = textPane.getDocument() ;
            try {
                String docText = document.getText( 0, document.getLength() )
                                         .toLowerCase() ;
                String searchText = text.toLowerCase() ;

                int pos = docText.indexOf( searchText, fromPos ) ;

                if( pos == -1 ) {
                    pos = docText.indexOf( searchText ) ;
                }

                if( pos != -1 ) {
                    scrollToPosition( pos ) ;
                    textPane.setCaretPosition( pos + text.length() ) ;
                    textPane.select( pos, pos + text.length() ) ;
                }
            }
            catch ( Exception e ) {
                e.printStackTrace() ;
            }
        } ) ;
    }
    
    private void scrollToPosition( int pos ) throws Exception {
        
        if( pos > -1 ){
            Rectangle viewRect = textPane.modelToView( pos ) ;
            viewRect.y += textPane.getVisibleRect().height - 20 ;
            textPane.scrollRectToVisible( viewRect ) ;
        }
    }
}
