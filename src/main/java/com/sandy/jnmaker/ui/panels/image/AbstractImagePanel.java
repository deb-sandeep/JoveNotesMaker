package com.sandy.jnmaker.ui.panels.image;

import static com.sandy.jnmaker.ui.helper.UIUtil.getActionBtn ;
import static com.sandy.jnmaker.util.ObjectRepository.getCWD ;
import static com.sandy.jnmaker.util.ObjectRepository.setCWD ;
import static java.awt.event.InputEvent.CTRL_DOWN_MASK ;
import static java.awt.event.KeyEvent.VK_0 ;
import static java.awt.event.KeyEvent.VK_9 ;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.Component ;
import java.awt.Dimension ;
import java.awt.Point ;
import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;
import java.awt.image.BufferedImage ;
import java.io.File ;
import java.io.IOException ;
import java.nio.file.Files ;
import java.util.ArrayList ;
import java.util.List ;

import javax.imageio.ImageIO ;
import javax.swing.ActionMap ;
import javax.swing.BoxLayout ;
import javax.swing.InputMap ;
import javax.swing.JComponent ;
import javax.swing.JFileChooser ;
import javax.swing.JOptionPane ;
import javax.swing.JPanel ;
import javax.swing.KeyStroke ;
import javax.swing.filechooser.FileFilter ;

import com.sandy.jnmaker.ui.panels.image.k12.K12ExerciseQuestion;
import org.apache.log4j.Logger ;

import com.sandy.common.ui.CloseableTabbedPane ;
import com.sandy.common.ui.CloseableTabbedPane.TabCloseListener ;
import com.sandy.common.ui.DrawingCanvas ;
import com.sandy.common.ui.ScalableImagePanel ;
import com.sandy.common.ui.ScalableImagePanel.ScalableImagePanelListener ;
import com.sandy.common.util.StringUtil ;
import com.sandy.jeecoach.util.AbstractQuestion ;
import com.sandy.jnmaker.ui.helper.UIUtil ;

@SuppressWarnings( "serial" )
public abstract class AbstractImagePanel<T extends AbstractQuestion<T>> extends JPanel 
    implements ActionListener, TabCloseListener, ScalableImagePanelListener {

    static final Logger log = Logger.getLogger( AbstractImagePanel.class ) ;
    
    private static final String AC_OPEN_FILES = "OPEN_FILES" ;
    private static final String AC_ZOOM_IN    = "ZOOM_IN" ;
    private static final String AC_ZOOM_OUT   = "ZOOM_OUT" ;
    private static final String AC_CLOSE_ALL  = "CLOSE_ALL" ;
    
    private CloseableTabbedPane tabbedPane    = null ;
    private List<File>          openedFiles   = new ArrayList<>() ;
    private List<File>          originalFiles = new ArrayList<>() ;
    
    protected JFileChooser openFileChooser = new JFileChooser() ;
    protected JFileChooser saveFileChooser = new JFileChooser() ;
    
    protected File lastSavedDir = null ;
    protected File lastSavedFile = null ;
    
    protected T lastQuestion = null ;
    protected T currentlyDisplayedQuestion = null ;
    protected T nextQuestion = null ;
    
    public AbstractImagePanel() {
        
        setUpUI() ;
        setUpFileChooser() ;
    }
    
    private void setUpUI() {
        
        setLayout( new BorderLayout() ) ;
        add( getToolbar(), BorderLayout.WEST ) ;
        add( getTabbedPane(), BorderLayout.CENTER ) ;
        
        UIUtil.setPanelBackground( UIUtil.EDITOR_BG_COLOR, this ) ;
    }
    
    private JComponent getToolbar() {
        
        JPanel panel = new JPanel() ;
        panel.setLayout( new BoxLayout(panel, BoxLayout.Y_AXIS) ) ;
        panel.setOpaque( true ) ;
        panel.setBackground( Color.GRAY ) ;

        panel.add( getActionBtn( "file_open", AC_OPEN_FILES, this ) ) ;
        panel.add( getActionBtn( "zoom_in",   AC_ZOOM_IN,    this ) ) ;
        panel.add( getActionBtn( "zoom_out",  AC_ZOOM_OUT,   this ) ) ;
        panel.add( getActionBtn( "close_all", AC_CLOSE_ALL,  this ) ) ;
        panel.setMinimumSize( new Dimension( 0, 0 ) ) ;
        
        UIUtil.setPanelBackground( UIUtil.EDITOR_BG_COLOR, panel ) ;
        
        return panel ;
    }
    
    private JComponent getTabbedPane() {
        
        tabbedPane = new CloseableTabbedPane() ;
        tabbedPane.addTabCloseListener( this ) ;
        tabbedPane.setMinimumSize( new Dimension( 0, 0 ) ) ;
        tabbedPane.setForeground( Color.BLUE ) ;
        return tabbedPane ;
    }
    
    private void setUpFileChooser() {
        
        openFileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY ) ;
        openFileChooser.setMultiSelectionEnabled( true ) ;
        openFileChooser.setFileFilter( new FileFilter() {
            
            @Override public String getDescription() {
                return "Image files only" ;
            }
            
            @Override public boolean accept( File file ) {
                try {
                    String contentType =  Files.probeContentType( file.toPath() ) ;
                    if( file.isDirectory() || 
                        ( contentType != null && 
                          contentType.startsWith( "image/" ) ) ) {
                        return true ;
                    }
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }
                return false ;
            }
        } ) ;
        
        bindKeyStrokesForSaveDialog() ;
        
        JComponent accessory = getSaveFileChooserAccessory() ;
        if( accessory != null ) {
            saveFileChooser.setAccessory( accessory ) ;
        }
    }
    
    // There are ten keystroke bound for the input map of the save dialog.
    // Each are bound by Ctrl+[0,1,2,3...9]. By default they do nothing.
    // Each can be overridden by the subclass by attaching a new handler
    private void bindKeyStrokesForSaveDialog() {
        
        InputMap inputMap = saveFileChooser.getInputMap( JFileChooser.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT ) ;
        ActionMap actionMap = saveFileChooser.getActionMap() ;
        
        for( int i = 0; i <= 9; i++ ) {
            
            int keyCode = VK_0 + i ;
            String keyHandlerID = getKeyHandlerID( i ) ;
            
            KeyStroke keyStroke = KeyStroke.getKeyStroke( keyCode, CTRL_DOWN_MASK  ) ;
            SaveFnKeyHandler handler = new NoOpFnKeyHandler() ;
            
            inputMap.put( keyStroke, keyHandlerID ) ;
            actionMap.put( keyHandlerID, handler ) ;
        }
    }
     
    private String getKeyHandlerID( int keyIndex ) {
        
        if( keyIndex < 0 || keyIndex > 9 ) {
            throw new IllegalArgumentException( "VK not in set (VK_0 ... VK_9)" ) ;
        }
        return "SDHandler[Ctrl + VK_" + keyIndex + "]" ;
    }
    
    protected void registerSaveFnHandler( int vkCode, SaveFnKeyHandler handler ) {
        if( vkCode < VK_0 || vkCode > VK_9 ) {
            throw new IllegalArgumentException( "VK not in set (VK_0 ... VK_9)" ) ;
        }
        
        String keyHandlerID = getKeyHandlerID( vkCode - VK_0 ) ;
        ActionMap actionMap = saveFileChooser.getActionMap() ;
        
        actionMap.put( keyHandlerID, handler ) ;
        
        log.info( "Installed save fn key handler = " + handler.getName() ) ;
    }
    
    public void actionPerformed( ActionEvent e ) {
        
        String actionCmd = e.getActionCommand() ;
        switch( actionCmd ) {
            case AC_OPEN_FILES:
                openFiles() ;
                break ;
            case AC_ZOOM_IN :
                zoom( true ) ;
                break ;
            case AC_ZOOM_OUT :
                zoom( false ) ;
                break ;
            case AC_CLOSE_ALL :
                closeAll() ;
                break ;
        }
    }
    
    private void openFiles() {
        
        File[] files = getSelectedFiles() ;
        if(files != null) {
            for( File file : files ) {
                ScalableImagePanel imgPanel = new ScalableImagePanel() ;
                imgPanel.setImage( file );
                imgPanel.addListener( this ) ;
                this.tabbedPane.add( file.getName(), imgPanel ) ;
                this.openedFiles.add( file ) ;
            }
        }
    }
    
    private File[] getSelectedFiles() {
        
        File[] selectedFiles = null ;
        
        openFileChooser.setCurrentDirectory( getCWD() ) ;
        int userChoice = openFileChooser.showOpenDialog( this ) ;
        if( userChoice == JFileChooser.APPROVE_OPTION ) {
            setCWD( openFileChooser.getCurrentDirectory() ) ;
            selectedFiles = openFileChooser.getSelectedFiles() ;
        }
        
        return selectedFiles ;
    }
    
    private void zoom( boolean zoomIn ) {
        
        Component c = this.tabbedPane.getSelectedComponent() ;
        if( c != null ) {
            ScalableImagePanel imagePanel = ( ScalableImagePanel )c ;
            imagePanel.zoom( zoomIn ) ;
        }
    }
    
    public void closeAll() {
        
        this.tabbedPane.removeAll() ;
        this.openedFiles.clear() ;
        this.originalFiles.clear() ;
    }

    public String getOpenedFiles() {
        
        StringBuilder paths = new StringBuilder() ;
        for( File file : this.openedFiles ) {
            paths.append(file.getAbsolutePath())
                 .append(File.pathSeparator);
        }
        return paths.toString() ;
    }

    public void setOpenedFiles( String paths ) {
        
        if( StringUtil.isNotEmptyOrNull( paths ) ) {
            
            String[] openedFilePaths = paths.split( File.pathSeparator ) ;
            for( String filePath : openedFilePaths ) {
                
                if( StringUtil.isNotEmptyOrNull( filePath ) ) {
                    
                    File file = new File( filePath ) ;
                    ScalableImagePanel imgPanel = new ScalableImagePanel() ;
                    imgPanel.setImage( file );
                    imgPanel.addListener( this );
                    
                    this.tabbedPane.add( file.getName(), imgPanel ) ;
                    this.openedFiles.add( file ) ;
                    this.originalFiles.add( file ) ;
                }
            }
        }
    }
    
    public void saveFiles() {
        
        this.originalFiles.clear() ;
        this.originalFiles.addAll(this.openedFiles);
    }
    
    public boolean isEditorDirty() {
        return !this.originalFiles.equals( this.openedFiles ) ;
    }

    @Override
    public void tabClosing( ActionEvent e ) {
        
        ScalableImagePanel imgPanel = ( ScalableImagePanel )e.getSource() ;
        imgPanel.removeListener( this ) ;
        File file = imgPanel.getCurImgFile() ;
        this.openedFiles.remove( file ) ;
    }

    protected File getFileViaSaveDialog() {
        
        int userChoice = saveFileChooser.showSaveDialog( this ) ;
        if( userChoice == JFileChooser.APPROVE_OPTION ) {
            return saveFileChooser.getSelectedFile() ;
        }
        return null ;
    }
    
    protected void writeSelectedImageToFile( BufferedImage image,
                                             File outputFile ) {
        try {
            ImageIO.write( image, "png", outputFile ) ;
        }
        catch( IOException e ) {
            e.printStackTrace() ;
        }
    }

    @Override
    public void subImageSelected( BufferedImage image, int selMod ) {
        
        setRecommendedSaveDir() ;
        
        File outputFile = getRecommendedOutputFile( selMod ) ;
        
        if( outputFile != null ) {

            String fileName = outputFile.getName() ;
            boolean absoluteFileName = false ;

            if( fileName.startsWith( "`" ) ) {
                fileName = fileName.substring( 1 ) ;
                absoluteFileName = true ;
            }

            if( !fileName.toLowerCase().endsWith( ".png" ) ) {
                outputFile = new File( outputFile.getParentFile(),
                                       fileName + ".png" ) ;
            }
            else {
                outputFile = new File( outputFile.getParentFile(), fileName ) ;
            }

            try {
                // Check for a valid file name based on the type of image panel
                // If we are dealing with an absolute filename, treat it as is.
                if( !absoluteFileName ) {
                    constructQuestion( outputFile ) ;
                }

                if( outputFile.exists() ) {
                    int choice = JOptionPane.showConfirmDialog( 
                                                this, "File exists. Overwrite?" ) ;
                    
                    if( choice == JOptionPane.NO_OPTION || 
                        choice == JOptionPane.CANCEL_OPTION ) {
                        return ;
                    }
                }
                
                writeSelectedImageToFile( image, outputFile ) ;

                lastSavedFile = outputFile ;
                lastSavedDir = outputFile.getParentFile() ;

                if( !absoluteFileName ) {
                    lastQuestion = constructQuestion( lastSavedFile ) ;
                    if( lastQuestion != null ) {
                        nextQuestion = (T)lastQuestion.nextQuestion() ;
                    }
                    currentlyDisplayedQuestion = null ;
                    handleLastQuestionSave( lastQuestion ) ;
                }
            }
            catch( Exception e ) {
                JOptionPane.showMessageDialog( this,
                                               "Invalid file name. " + e.getMessage(),
                                               "Error saving file",
                                               JOptionPane.ERROR_MESSAGE ) ;
                log.debug( "Save issue. " + e.getMessage(), e ) ;
            }
        }
        else {
            currentlyDisplayedQuestion = null ;
        }
    }
    
    private void setRecommendedSaveDir() {
        
        ScalableImagePanel imgPanel = null ;
        imgPanel = ( ScalableImagePanel )tabbedPane.getSelectedComponent() ;
        
        if( imgPanel == null ) {
            return ;
        }
        else {
            imgPanel.setToolTipText( null ) ; 
            File imgFile = imgPanel.getCurImgFile() ;
            if( lastSavedDir == null ) {
                lastSavedDir = getRecommendedSaveDir( imgFile ) ;
                if( lastSavedDir != null ) {
                    lastSavedDir.mkdirs() ;
                    saveFileChooser.setCurrentDirectory( lastSavedDir ) ;
                }
            }
        }
    }
    
    protected void updateSaveDialogFileName( String fileName ) {
        
        File outputFile = new File( lastSavedDir, fileName ) ;
        saveFileChooser.setSelectedFile( outputFile ) ; 
    }
    
    protected File getRecommendedOutputFile( int selMod ) {
        
        File outputFile = null ;
        
        boolean interventionRequired = false ;
        
        if( lastQuestion == null ||
            selMod == DrawingCanvas.MARK_END_MODIFIER_RIGHT_BTN ) {
            interventionRequired = true ;
        }
        
        if( lastQuestion != null ) {
            if( lastQuestion.getFileName()
                            .equals( nextQuestion.getFileName() ) ) {
                interventionRequired = true ;
            }
            else {
                if( selMod == DrawingCanvas.MARK_END_MODIFIER_CENTER_BTN ) {
                    // FUTURE: In the future, move this to Abstract question
                    if( nextQuestion instanceof K12ExerciseQuestion ) {
                        K12ExerciseQuestion k12Q = ( K12ExerciseQuestion )nextQuestion ;
                        if( k12Q.getPartNum() == -1 ) {
                            k12Q.setPartNum( 1 ) ;
                        }
                    }
                }
                outputFile = new File( lastSavedDir, nextQuestion.getFileName() ) ;
                currentlyDisplayedQuestion = nextQuestion ;
            }
        }
        
        if( interventionRequired ) {
            if( outputFile != null ) {
                saveFileChooser.setSelectedFile( outputFile ) ;
            }
            outputFile = getFileViaSaveDialog() ;
        }
        return outputFile ;
    }

    protected abstract T constructQuestion( File file ) ;
    
    protected abstract File getRecommendedSaveDir( File imgFile ) ;
    
    protected JComponent getSaveFileChooserAccessory() { return null ; }
    
    protected void handleLastQuestionSave( T lastQuestion ) {}

    public void subImageBoundResized( Point anchor, Point hook ) {
        
        if( nextQuestion != null ) {
            ScalableImagePanel imgPanel = null ;
            imgPanel = ( ScalableImagePanel )tabbedPane.getSelectedComponent() ;
            imgPanel.setToolTipText( nextQuestion.getFileName() ) ;
        }
    }
}
