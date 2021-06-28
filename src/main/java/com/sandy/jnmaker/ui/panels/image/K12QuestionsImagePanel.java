package com.sandy.jnmaker.ui.panels.image;

import static com.sandy.jnmaker.ui.helper.UIUtil.* ;
import static com.sandy.jnmaker.util.ObjectRepository.* ;

import java.awt.BorderLayout ;
import java.awt.Color ;
import java.awt.Component ;
import java.awt.Dimension ;
import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;
import java.awt.event.KeyEvent ;
import java.awt.image.BufferedImage ;
import java.io.File ;
import java.io.IOException ;
import java.nio.file.Files ;
import java.util.ArrayList ;
import java.util.List ;

import javax.imageio.ImageIO ;
import javax.swing.AbstractAction ;
import javax.swing.ActionMap ;
import javax.swing.BoxLayout ;
import javax.swing.InputMap ;
import javax.swing.JComponent ;
import javax.swing.JFileChooser ;
import javax.swing.JOptionPane ;
import javax.swing.JPanel ;
import javax.swing.KeyStroke ;
import javax.swing.filechooser.FileFilter ;

import org.apache.log4j.Logger ;

import com.sandy.common.ui.CloseableTabbedPane ;
import com.sandy.common.ui.CloseableTabbedPane.TabCloseListener ;
import com.sandy.common.ui.DrawingCanvas ;
import com.sandy.common.ui.ScalableImagePanel ;
import com.sandy.common.ui.ScalableImagePanel.ScalableImagePanelListener ;
import com.sandy.common.util.StringUtil ;
import com.sandy.jnmaker.ui.helper.UIUtil ;
import com.sandy.jnmaker.ui.helper.seqgen.ExQuestion ;

public class K12QuestionsImagePanel extends JPanel 
    implements ActionListener, TabCloseListener, ScalableImagePanelListener {

    static final Logger log = Logger.getLogger( K12QuestionsImagePanel.class ) ;
    
    private static final long serialVersionUID = -6820796056331113968L ;
    
    private static final String AC_OPEN_FILES = "OPEN_FILES" ;
    private static final String AC_ZOOM_IN    = "ZOOM_IN" ;
    private static final String AC_ZOOM_OUT   = "ZOOM_OUT" ;
    private static final String AC_CLOSE_ALL  = "CLOSE_ALL" ;
    
    private CloseableTabbedPane tabbedPane    = null ;
    private List<File>          openedFiles   = new ArrayList<>() ;
    private List<File>          originalFiles = new ArrayList<>() ;
    
    private JFileChooser openFileChooser = new JFileChooser() ;
    private JFileChooser saveFileChooser = new JFileChooser() ;
    
    private ExQuestion lastQuestion = null ;
    private File lastSavedDir = null ;
    
    public K12QuestionsImagePanel() {
        
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
        } );
        
        bindKeyStrokesForSaveDialog() ;
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
        if( files != null && files.length > 0 ) {
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
        StringBuffer paths = new StringBuffer() ;
        for( File file : this.openedFiles ) {
            paths.append( file.getAbsolutePath() + File.pathSeparator ) ;
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
        for( File f : this.openedFiles ) {
            this.originalFiles.add( f ) ;
         }
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

    @Override
    public void subImageSelected( BufferedImage image, int selMod ) {
        
        File outputFile = getUserApprovedOutputFile( selMod ) ;
        
        if( outputFile != null ) {
            if( !outputFile.getName().toLowerCase().endsWith( ".png" ) ) {
                outputFile = new File( outputFile.getParentFile(), 
                                       outputFile.getName() + ".png" ) ;
            }
            
            if( outputFile.exists() ) {
                int choice = JOptionPane.showConfirmDialog( this, "File exists. Overwrite?" ) ;
                if( choice == JOptionPane.NO_OPTION || 
                    choice == JOptionPane.CANCEL_OPTION ) {
                    return ;
                }
            }
            
            try {
                lastQuestion = new ExQuestion( outputFile.getName() ) ;
            }
            catch( Exception e ) {
                lastQuestion = null ;
                log.debug(  "Not recognized as an exercise question. Saving as is." ) ;
            }

            lastSavedDir = outputFile.getParentFile() ;
            
            writeSelectedImageToFile( image, outputFile ) ;
        }
    }
    
    private void setSelectedFile( ExQuestion question ) {
        File outputFile = new File( lastSavedDir, question.getFileName() ) ;
        saveFileChooser.setSelectedFile( outputFile ) ; 
    }
    
    @SuppressWarnings( "serial" )
    private void bindKeyStrokesForSaveDialog() {
        
        KeyStroke f1 = KeyStroke.getKeyStroke( KeyEvent.VK_F1, 0 ) ;
        KeyStroke f2 = KeyStroke.getKeyStroke( KeyEvent.VK_F2, 0 ) ;
        KeyStroke f3 = KeyStroke.getKeyStroke( KeyEvent.VK_F3, 0 ) ;
        KeyStroke f4 = KeyStroke.getKeyStroke( KeyEvent.VK_F4, 0 ) ;
        KeyStroke f5 = KeyStroke.getKeyStroke( KeyEvent.VK_F5, 0 ) ;
        
        InputMap map = saveFileChooser.getInputMap( JFileChooser.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT ) ;
        map.put( f1, "nextMajorElement"    ) ;
        map.put( f2, "toggleHeader"   ) ;
        map.put( f3, "nextMajorElementWithHeader" ) ;
        map.put( f4, "incrementLCTPassage" ) ;
        map.put( f5, "stripLCTGroupNumber" ) ;
        
        ActionMap actionMap = saveFileChooser.getActionMap() ;
        actionMap.put( "nextMajorElement", new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                if( lastQuestion != null ) {
                    ExQuestion nextQ = lastQuestion.nextMajorElement() ;
                    setSelectedFile( nextQ ) ;
                }
            }
        } ) ;
        
        actionMap.put( "toggleHeader", new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                File selFile = saveFileChooser.getSelectedFile() ;
                if( selFile != null ) {
                    ExQuestion q = new ExQuestion( selFile.getName() ) ;
                    q.setHeader( !q.isHeader() ) ;
                    setSelectedFile( q ) ;
                }
            }
        } ) ;

        actionMap.put( "nextMajorElementWithHeader", new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                if( lastQuestion != null ) {
                    ExQuestion nextQ = lastQuestion.nextMajorElement() ;
                    nextQ.setHeader( true ) ;
                    setSelectedFile( nextQ ) ;
                }
            }
        } ) ;

        actionMap.put( "stripLCTGroupNumber", new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
            }
        } ) ;
    }
    
    private File getUserApprovedOutputFile( int selMod ) {
        
        ExQuestion nextQ = null ;
        File outputFile = null ;
        
        boolean interventionRequired = false ;
        
        if( lastQuestion == null || 
            lastQuestion.nextItemNeedsIntervention() || 
            selMod == DrawingCanvas.MARK_END_MODIFIER_RIGHT_BTN ) {
            
            interventionRequired = true ;
        }
        
        if( lastQuestion != null ) {
            nextQ = lastQuestion.nextElement() ;
            outputFile = new File( lastSavedDir, nextQ.getFileName() ) ;
        }
        
        if( interventionRequired ) {
            if( outputFile != null ) {
                saveFileChooser.setSelectedFile( outputFile ) ;
            }
            outputFile = getFileViaSaveDialog() ;
        }
        
        return outputFile ;
    }
    
    private File getFileViaSaveDialog() {
        int userChoice = saveFileChooser.showSaveDialog( this ) ;
        if( userChoice == JFileChooser.APPROVE_OPTION ) {
            return saveFileChooser.getSelectedFile() ;
        }
        return null ;
    }
    
    private void writeSelectedImageToFile( BufferedImage image,
                                           File outputFile ) {
        try {
            ImageIO.write( image, "png", outputFile ) ;
        }
        catch( IOException e ) {
            e.printStackTrace() ;
        }
    }
}
