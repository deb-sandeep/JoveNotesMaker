package com.sandy.jnmaker.ui.helper ;

import static com.sandy.jnmaker.util.ObjectRepository.* ;

import java.io.File ;
import java.io.FileInputStream ;
import java.io.FileOutputStream ;
import java.util.Date ;
import java.util.HashMap ;
import java.util.Map ;
import java.util.Properties ;

import javax.swing.JFileChooser ;
import javax.swing.JOptionPane ;
import javax.swing.filechooser.FileFilter ;

import org.apache.commons.beanutils.BeanUtilsBean ;
import org.apache.log4j.Logger ;

import com.sandy.jnmaker.ui.panels.image.ImagePanel ;
import com.sandy.jnmaker.ui.panels.jn.JoveNotesPanel ;
import com.sandy.jnmaker.ui.panels.rawtxt.RawTextPanel ;
import com.sandy.jnmaker.util.ObjectRepository ;

public class ProjectManager {

    private static final Logger logger = Logger.getLogger( ProjectManager.class ) ;
    
    private static final String KEY_RAWTEXTPANEL   = "RawTextPanel" ;
    private static final String KEY_IMAGEPANEL     = "ImagePanel" ;
    private static final String KEY_JOVENOTESPANEL = "JoveNotesPanel" ;
    
    private static final String[] STATE_KEYS = {
            
        "RawTextPanel.fontSize",
        "RawTextPanel.currentFile",
        "ImagePanel.openedFiles",
        "JoveNotesPanel.fontSize",
        "JoveNotesPanel.currentFile"
    } ;
    
    private JFileChooser   fileChooser  = new JFileChooser() ;
    private BeanUtilsBean  beanUtils    = new BeanUtilsBean() ;
    
    private RawTextPanel   rawTextPanel = null ;
    private ImagePanel     imagePanel   = null ;
    private JoveNotesPanel jnPanel      = null ;
    
    private Map<String, Object> statefulObjects = new HashMap<String, Object>() ;
    
    private File projectConfigFile = null ;
    private File currentDir        = new File( System.getProperty( "user.home" ) ) ;
    
    public ProjectManager() {
        setUpFileChooser() ;
    }
    
    public void setProjectConfigFile( File file ) throws Exception {

        obtainObjectReferences() ;
        if( file.exists() ) {
            if( closeProject() ) {
                getWordRepository().clear() ;
                loadState( file ) ;
                this.projectConfigFile = file ;
                saveAppState() ;
                setMainFrameTitle() ;
            }
        }
    }
    
    public File getProjectConfigFile() {
        return this.projectConfigFile ;
    }
    
    public void newProject() {
        
        obtainObjectReferences() ;
        if( dirtyCurrentProjectHandled() ) {
            File file = getSelectedFile( "New project" ) ;
            if( file == null ) {
                return ;
            }
            else if( file.exists() ) {
                int choice = JOptionPane.showConfirmDialog( getMainFrame(), 
                                                   "File exists, overwrite?" ) ;
                if( choice != JOptionPane.OK_OPTION ) {
                    return ;
                }
            }
            
            closeProject() ;
            try {
                if( !file.getName().endsWith( ".jnmp" ) ) {
                    file = new File( file.getParentFile(), file.getName() + ".jnmp" ) ;
                    file.createNewFile() ;
                }
                setProjectConfigFile( file ) ;
            }
            catch( Exception e ) {
                logger.error( "Could not open new project.", e ) ;
            }
        }
    }
    
    public boolean openProject() {
        
        boolean projectOpened = false ;
        
        obtainObjectReferences() ;
        if( dirtyCurrentProjectHandled() ) {
            File file = getSelectedFile( "Open project" ) ;
            if( file != null ) {
                if( file.exists() ) {
                    try {
                        setProjectConfigFile( file ) ;
                        projectOpened = true ;
                    }
                    catch( Exception e ) {
                        logger.error( "Error opening project file", e ) ;
                        JOptionPane.showMessageDialog( getMainFrame(), 
                                "Error opening project file. \n'" + 
                                "Message = " + e.getMessage(), 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE ) ;
                    }
                }
                else {
                    JOptionPane.showMessageDialog( getMainFrame(), 
                            "Selected file '" + file.getAbsolutePath() + "'" + 
                            " does not exist", "File not found", 
                            JOptionPane.ERROR_MESSAGE ) ;
                }
            }
        }
        return projectOpened ;
    }
    
    public void saveProject() {
        
        obtainObjectReferences() ;
        if( isCurrentProjectDirty() ) {
            File file = this.projectConfigFile ;
            if( file == null ) {
                file = getSelectedFile( "Save project as" ) ;
                if( file == null ) return ;
                this.projectConfigFile = file ;
            }
            
            try {
                jnPanel.saveFile() ;
                rawTextPanel.saveFile() ;
                imagePanel.saveFiles() ;
                saveState( file ) ;
                saveAppState() ;
            }
            catch( Exception e ) {
                logger.error( "Error saving project file", e ) ;
                JOptionPane.showMessageDialog( getMainFrame(), 
                        "Error saving project file. \n'" + 
                        "Message = " + e.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE ) ;
            }
        }
    }
    
    public boolean closeProject() {
        
        boolean projectClosed = false ;
        
        obtainObjectReferences() ;
        if( dirtyCurrentProjectHandled() ) {
            try {
                saveState( this.projectConfigFile ) ;
            }
            catch( Exception e ) {
                logger.error( "Couldn't save state", e ) ;
            }
            
            jnPanel.closeFile() ;
            rawTextPanel.closeFile() ;
            imagePanel.closeAll() ;
            
            projectConfigFile = null ;
            setMainFrameTitle() ;
            
            projectClosed = true ;
        }
        return projectClosed ;
    }
    
    private boolean dirtyCurrentProjectHandled() {
        
        if( isCurrentProjectDirty() ) {
            int choice = JOptionPane.showConfirmDialog( getMainFrame(),  
                    "There are unsaved changes. Save before exit?\n" + 
                    "Yes to save, No to discard and Cancel to abort." ) ;
            
            if( choice == JOptionPane.CANCEL_OPTION ) {
                return false ;
            }
            else if( choice == JOptionPane.OK_OPTION ) {
                saveProject() ;
            }
        }
        return true ;
    }
    
    private boolean isCurrentProjectDirty() {
        
        boolean projectDirty = false ;
        boolean editorsDirty = false ;
        
        if( jnPanel.isEditorDirty() || 
            rawTextPanel.isEditorDirty() || 
            imagePanel.isEditorDirty() ) {
            
            editorsDirty = true ;
            projectDirty = true ;
        }
        
        if( !editorsDirty && projectConfigFile != null && !projectConfigFile.exists() ) {
            projectDirty = true ;
        }
        
        return projectDirty ;
    }
    
    private void obtainObjectReferences() {
        
        if( rawTextPanel == null ) {
            rawTextPanel = getMainFrame().getRawTextPanel() ;
            imagePanel   = getMainFrame().getImagePanel() ;
            jnPanel      = getMainFrame().getJNPanel() ;
            
            statefulObjects.put( KEY_JOVENOTESPANEL, jnPanel ) ;
            statefulObjects.put( KEY_RAWTEXTPANEL,   rawTextPanel ) ;
            statefulObjects.put( KEY_IMAGEPANEL,     imagePanel ) ;
        }
    }

    private void setUpFileChooser() {
        
        fileChooser.setFileSelectionMode( JFileChooser.FILES_ONLY ) ;
        fileChooser.setMultiSelectionEnabled( false ) ;
        fileChooser.setFileFilter( new FileFilter() {
            
            @Override
            public String getDescription() {
                return "JoveNote Maker Project" ;
            }
            
            @Override
            public boolean accept( File file ) {
                if( file.isDirectory() || file.getName().endsWith( ".jnmp" ) ) {
                    return true ;
                }
                return false ;
            }
        } );
    }
    
    private File getSelectedFile( String title ) {
        
        File selectedFile = null ;
        
        fileChooser.setCurrentDirectory( this.currentDir ) ;
        fileChooser.setDialogTitle( title ) ;
        
        int userChoice = fileChooser.showOpenDialog( getMainFrame() ) ;
        if( userChoice == JFileChooser.APPROVE_OPTION ) {
            this.currentDir = fileChooser.getCurrentDirectory() ;
            selectedFile = fileChooser.getSelectedFile() ;
        }
        
        return selectedFile ;
    }
    
    public void saveState( File file ) throws Exception {
        
        if( file == null )return ;
        
        Properties stateValues = new Properties() ;
        for( String path : STATE_KEYS ) {
            String objKey     = path.substring( 0, path.indexOf( '.' ) ) ;
            String attribPath = path.substring( path.indexOf( '.' ) + 1 ) ;
            
            String value = getValue( objKey, attribPath ) ;
            if( value != null ) {
                stateValues.put( path, value ) ;
            }
        }
        stateValues.store( new FileOutputStream( file ), 
                           "State saved on " + new Date().toString() ) ;
    }
    
    public void loadState( File file ) throws Exception {
        
        if( !file.exists() ) return ;
        
        Properties stateValues = new Properties() ;
        stateValues.load( new FileInputStream( file ) ) ;
        
        for( Object pathObj : stateValues.keySet() ) {
            
            String path = pathObj.toString() ;
            
            String objKey     = path.substring( 0, path.indexOf( '.' ) ) ;
            String attribPath = path.substring( path.indexOf( '.' ) + 1 ) ;
            String value      = stateValues.getProperty( path ) ;
            
            setValue( objKey, attribPath, value ) ;
        }
    }

    public String getValue( String objKey, String attribPath ) 
            throws Exception {
            
        if( !this.statefulObjects.containsKey( objKey ) ) {
            throw new Exception( "No object registered against the key " + objKey ) ;
        }
        
        Object obj   = this.statefulObjects.get( objKey ) ;
        String value = beanUtils.getProperty( obj, attribPath ) ;
        return value ;
    }
        
    public void setValue( String objKey, String attribPath, String value )
        throws Exception {
        
        if( !this.statefulObjects.containsKey( objKey ) ) {
            throw new Exception( "No object registered against the key " + objKey ) ;
        }
        
        Object obj = this.statefulObjects.get( objKey ) ;
        beanUtils.setProperty( obj, attribPath, value ) ;
    }
    
    private void saveAppState() {
        try {
            ObjectRepository.getStateMgr().saveState() ;
        }
        catch( Exception e ) {
            logger.error( "Can't save state", e ) ;
        }
    }

    private void setMainFrameTitle() {
        String titleText = "JoveNotes Maker - " ;
        if( this.projectConfigFile == null ) {
            titleText += "[]" ;
        }
        else {
            titleText += "[" + projectConfigFile.getAbsolutePath() + "]" ;
        }
        getMainFrame().setTitle( titleText ) ;
    }
}
