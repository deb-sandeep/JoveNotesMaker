package com.sandy.jnmaker.ui.panels;

import static com.sandy.jnmaker.ui.helper.UIUtil.getActionBtn ;

import java.awt.BorderLayout ;
import java.awt.Component ;
import java.awt.Dimension ;
import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;
import java.awt.image.BufferedImage ;
import java.io.File ;
import java.io.IOException ;
import java.nio.file.Files ;
import java.util.ArrayList ;
import java.util.List ;

import javax.imageio.ImageIO ;
import javax.swing.BoxLayout ;
import javax.swing.JComponent ;
import javax.swing.JFileChooser ;
import javax.swing.JPanel ;
import javax.swing.filechooser.FileFilter ;

import com.sandy.common.ui.CloseableTabbedPane ;
import com.sandy.common.ui.CloseableTabbedPane.TabCloseListener ;
import com.sandy.common.ui.ScalableImagePanel.ScalableImagePanelListener ;
import com.sandy.common.ui.ScalableImagePanel ;
import com.sandy.common.util.StringUtil ;
import com.sandy.jnmaker.ui.helper.UIUtil ;

public class ImagePanel extends JPanel 
    implements ActionListener, TabCloseListener, ScalableImagePanelListener {

    private static final long serialVersionUID = -6820796056331113968L ;
    
    private static final String AC_OPEN_FILES = "OPEN_FILES" ;
    private static final String AC_ZOOM_IN    = "ZOOM_IN" ;
    private static final String AC_ZOOM_OUT   = "ZOOM_OUT" ;
    private static final String AC_CLOSE_ALL  = "CLOSE_ALL" ;
    
    private CloseableTabbedPane tabbedPane    = null ;
    private File                currentDir    = new File( System.getProperty( "user.home" ) ) ;
    private List<File>          openedFiles   = new ArrayList<>() ;
    private List<File>          originalFiles = new ArrayList<>() ;
    private JFileChooser        fileChooser   = new JFileChooser() ;
    
    public ImagePanel() {
        
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
        return tabbedPane ;
    }
    
    private void setUpFileChooser() {
        
        fileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY ) ;
        fileChooser.setMultiSelectionEnabled( true ) ;
        fileChooser.setFileFilter( new FileFilter() {
            
            @Override
            public String getDescription() {
                return "Image files only" ;
            }
            
            @Override
            public boolean accept( File file ) {
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
        
        fileChooser.setCurrentDirectory( this.currentDir ) ;
        int userChoice = fileChooser.showOpenDialog( this ) ;
        if( userChoice == JFileChooser.APPROVE_OPTION ) {
            this.currentDir = fileChooser.getCurrentDirectory() ;
            selectedFiles = fileChooser.getSelectedFiles() ;
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

    public File getCurrentDir() {
        return currentDir;
    }

    public void setCurrentDir( File currentDir ) {
        this.currentDir = currentDir;
    }

    @Override
    public void tabClosing( ActionEvent e ) {
        
        ScalableImagePanel imgPanel = ( ScalableImagePanel )e.getSource() ;
        imgPanel.removeListener( this ) ;
        File file = imgPanel.getCurImgFile() ;
        this.openedFiles.remove( file ) ;
    }

    @Override
    public void subImageSelected( BufferedImage image ) {
        
        fileChooser.setCurrentDirectory( this.currentDir ) ;
        fileChooser.setSelectedFile( new File( "" ) );
        int userChoice = fileChooser.showSaveDialog( this ) ;
        if( userChoice == JFileChooser.APPROVE_OPTION ) {
            this.currentDir = fileChooser.getCurrentDirectory() ;
            File outputFile = fileChooser.getSelectedFile() ;
            if( !outputFile.getName().toLowerCase().endsWith( ".png" ) ) {
                outputFile = new File( this.currentDir, outputFile.getName() + ".png" ) ;
            }
            try {
                ImageIO.write( image, "png", outputFile ) ;
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
    }
}
