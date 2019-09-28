package com.sandy.jnmaker.ui.panels.image;

import static com.sandy.jnmaker.ui.helper.UIUtil.getActionBtn ;
import static com.sandy.jnmaker.util.ObjectRepository.getCWD ;
import static com.sandy.jnmaker.util.ObjectRepository.setCWD ;

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
import java.util.HashMap ;
import java.util.List ;
import java.util.Map ;

import javax.imageio.ImageIO ;
import javax.swing.AbstractAction ;
import javax.swing.ActionMap ;
import javax.swing.BoxLayout ;
import javax.swing.InputMap ;
import javax.swing.JComponent ;
import javax.swing.JFileChooser ;
import javax.swing.JPanel ;
import javax.swing.KeyStroke ;
import javax.swing.filechooser.FileFilter ;

import org.apache.log4j.Logger ;

import com.sandy.common.ui.CloseableTabbedPane ;
import com.sandy.common.ui.CloseableTabbedPane.TabCloseListener ;
import com.sandy.common.ui.ScalableImagePanel ;
import com.sandy.common.ui.ScalableImagePanel.ScalableImagePanelListener ;
import com.sandy.common.util.StringUtil ;
import com.sandy.jnmaker.ui.helper.UIUtil ;
import com.sandy.jnmaker.ui.helper.seqgen.SequenceGenerator ;
import com.sandy.jnmaker.ui.helper.seqgen.Sequencer ;

public class ImagePanel extends JPanel 
    implements ActionListener, TabCloseListener, ScalableImagePanelListener {

    static final Logger log = Logger.getLogger( ImagePanel.class ) ;
    
    private static final long serialVersionUID = -6820796056331113968L ;
    
    private static final String[] Q_TYPES = { "_SCA_", "_MCA_", "_LCT_", 
                                              "_CMT_", "_MMT_", "_NT_",
                                              "_ART_" } ; 
    private static final Map<String, String> Q_TYPE_CYCLE = new HashMap<>() ;
    static {
        Q_TYPE_CYCLE.put( "_SCA_", "_ART_" ) ;
        Q_TYPE_CYCLE.put( "_ART_", "_MCA_" ) ;
        Q_TYPE_CYCLE.put( "_MCA_", "_LCT_" ) ;
        Q_TYPE_CYCLE.put( "_LCT_", "_CMT_" ) ;
        Q_TYPE_CYCLE.put( "_CMT_", "_MMT_" ) ;
        Q_TYPE_CYCLE.put( "_MMT_", "_NT_"  ) ;
        Q_TYPE_CYCLE.put( "_NT_" , "_SCA_" ) ;
    }
    
    private static final String AC_OPEN_FILES = "OPEN_FILES" ;
    private static final String AC_ZOOM_IN    = "ZOOM_IN" ;
    private static final String AC_ZOOM_OUT   = "ZOOM_OUT" ;
    private static final String AC_CLOSE_ALL  = "CLOSE_ALL" ;
    
    private CloseableTabbedPane tabbedPane    = null ;
    private List<File>          openedFiles   = new ArrayList<>() ;
    private List<File>          originalFiles = new ArrayList<>() ;
    
    private JFileChooser openFileChooser = new JFileChooser() ;
    private JFileChooser saveFileChooser = new JFileChooser() ;
    
    private SequenceGenerator sequenceGenerator = null ;
    
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
        tabbedPane.setForeground( Color.BLUE ) ;
        return tabbedPane ;
    }
    
    private void setUpFileChooser() {
        
        openFileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY ) ;
        openFileChooser.setMultiSelectionEnabled( true ) ;
        openFileChooser.setFileFilter( new FileFilter() {
            
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
        
        bindKeyStrokesForSaveDialog() ;
    }
    
    @SuppressWarnings( "serial" )
    private void bindKeyStrokesForSaveDialog() {
        
        KeyStroke f1 = KeyStroke.getKeyStroke( KeyEvent.VK_F1, 0 ) ;
        KeyStroke f2 = KeyStroke.getKeyStroke( KeyEvent.VK_F2, 0 ) ;
        KeyStroke f3 = KeyStroke.getKeyStroke( KeyEvent.VK_F3, 0 ) ;
        KeyStroke backQuote = KeyStroke.getKeyStroke( KeyEvent.VK_BACK_QUOTE, KeyEvent.VK_SHIFT ) ;
        
        InputMap map = saveFileChooser.getInputMap( JFileChooser.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT ) ;
        map.put( f1, "approveSelection" ) ;
        map.put( backQuote, "approveSelection" ) ;
        map.put( f2, "incrementSequence" ) ;
        map.put( f3, "changeQType" ) ;
        
        ActionMap actionMap = saveFileChooser.getActionMap() ;
        actionMap.put( "incrementSequence", new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                saveFileChooser.setSelectedFile( new File( getNextImageFileName() ) );
            }
        } ) ;
        
        actionMap.put( "changeQType", new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                File selFile = saveFileChooser.getSelectedFile() ;
                saveFileChooser.setSelectedFile( changeQTypeInSelectedFileName( selFile ) );
            }
        } ) ;
    }
    
    private File changeQTypeInSelectedFileName( File selFile ) {
        
        String fileName = selFile.getName() ;
        for( String qType : Q_TYPES ) {
            if( fileName.contains( qType ) ) {
                String nextQType = Q_TYPE_CYCLE.get( qType ) ;
                fileName = fileName.replace( qType, nextQType ) ;
                break ;
            }
        }

        return new File( selFile.getParent(), fileName ) ;
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
    public void subImageSelected( BufferedImage image ) {
        
        saveFileChooser.setSelectedFile( new File( getNextImageFileName() ) );

        int userChoice = saveFileChooser.showSaveDialog( this ) ;
        if( userChoice == JFileChooser.APPROVE_OPTION ) {
        
            File outputFile = saveFileChooser.getSelectedFile() ;
            
            if( !outputFile.getName().toLowerCase().endsWith( ".png" ) ) {
                outputFile = new File( outputFile.getParentFile(), outputFile.getName() + ".png" ) ;
            }
            
            try {
                ImageIO.write( image, "png", outputFile ) ;
                
                String fileName = outputFile.getName() ;
                fileName = fileName.substring( 0, fileName.length()-4 ) ;
                
                if( this.sequenceGenerator == null ) {
                    this.sequenceGenerator = Sequencer.identifySequence( fileName ) ;
                }
                else {
                    if( !this.sequenceGenerator.isMatchingSequence( fileName ) ) {
                        this.sequenceGenerator = Sequencer.identifySequence( fileName ) ;
                    }
                }
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
        else {
            if( sequenceGenerator != null ) {
                this.sequenceGenerator.rollbackSequence() ;
            }
        }
    }
    
    private String getNextImageFileName() {
        if( this.sequenceGenerator != null ) {
            return this.sequenceGenerator.getNextSequence() ;
        }
        return "" ;
    }
}
