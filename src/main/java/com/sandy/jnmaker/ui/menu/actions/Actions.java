package com.sandy.jnmaker.ui.menu.actions;

import static com.sandy.jnmaker.util.ObjectRepository.getMainFrame ;
import static com.sandy.jnmaker.util.ObjectRepository.getProjectManager ;
import static java.awt.event.InputEvent.ALT_DOWN_MASK ;
import static java.awt.event.InputEvent.CTRL_DOWN_MASK ;
import static java.awt.event.KeyEvent.*;

import java.awt.Component ;
import java.awt.event.ActionEvent ;
import java.lang.reflect.Method ;

import javax.swing.JFrame ;
import javax.swing.JOptionPane ;
import javax.swing.SwingUtilities ;

import org.apache.log4j.Logger ;

import com.sandy.common.util.ReflectionUtil ;
import com.sandy.jcmap.JCMap ;
import com.sandy.jnmaker.tools.AbstractTool ;
import com.sandy.jnmaker.tools.cmap.CMapper ;
import com.sandy.jnmaker.tools.mapping.MatrixMappingTool ;
import com.sandy.jnmaker.util.ObjectRepository ;

public class Actions {

    private static final Logger logger = Logger.getLogger( Actions.class ) ;
    
    private CMapper      cMapper     = null ;
    private AbstractTool mappingTool = new MatrixMappingTool() ;
    
    private AbstractBaseAction exitAppAction       = null ;
    
    private AbstractBaseAction newRawFileAction    = null ;
    private AbstractBaseAction openRawFileAction   = null ;
    private AbstractBaseAction closeRawFileAction  = null ;
    private AbstractBaseAction saveRawFileAction   = null ;
    private AbstractBaseAction zoomInRawAction     = null ;
    private AbstractBaseAction zoomOutRawAction    = null ;
    
    private AbstractBaseAction newJNFileAction    = null ;
    private AbstractBaseAction openJNFileAction   = null ;
    private AbstractBaseAction closeJNFileAction  = null ;
    private AbstractBaseAction saveJNFileAction   = null ;
    private AbstractBaseAction zoomInJNAction     = null ;
    private AbstractBaseAction zoomOutJNAction    = null ;
    
    private AbstractBaseAction newProjectAction   = null ;
    private AbstractBaseAction openProjectAction  = null ;
    private AbstractBaseAction saveProjectAction  = null ;
    private AbstractBaseAction closeProjectAction = null ;
    
    private AbstractBaseAction mappingToolAction  = null ;
    private AbstractBaseAction jcMapToolAction    = null ;
    
    private Object[][] menuConfig = {
        { "exitApp",       "Exit",             null,          VK_X,     -1  , -1 },
        
        { "newRawFile",    "New raw file",     "file_new",    VK_N,     VK_1, CTRL_DOWN_MASK },
        { "openRawFile",   "Open raw file",    "file_open",   VK_O,     VK_2, CTRL_DOWN_MASK },
        { "saveRawFile",   "Save raw file",    "file_save",   VK_S,     VK_3, CTRL_DOWN_MASK },
        { "closeRawFile",  "Close raw file",   "file_close",  VK_C,     VK_5, CTRL_DOWN_MASK },
        { "zoomInRaw",     "Zoom in",          "zoom_in",     VK_PLUS,  VK_6, CTRL_DOWN_MASK },
        { "zoomOutRaw",    "Zoom out",         "zoom_out",    VK_MINUS, VK_7, CTRL_DOWN_MASK },
        
        { "newJNFile",     "New JN file",     "file_new",    VK_N,      VK_1, ALT_DOWN_MASK },
        { "openJNFile",    "Open JN file",    "file_open",   VK_O,      VK_2, ALT_DOWN_MASK },
        { "saveJNFile",    "Save JN file",    "file_save",   VK_S,      VK_3, ALT_DOWN_MASK },
        { "closeJNFile",   "Close JN file",   "file_close",  VK_C,      VK_5, ALT_DOWN_MASK },
        { "zoomInJN",      "Zoom in",         "zoom_in",     VK_PLUS,   VK_6, ALT_DOWN_MASK },
        { "zoomOutJN",     "Zoom out",        "zoom_out",    VK_MINUS,  VK_7, ALT_DOWN_MASK },
        
        { "newProject",    "New project",     "file_new",    VK_N,      VK_F1, 0 },
        { "openProject",   "Open project",    "file_open",   VK_O,      VK_F2, 0 },
        { "saveProject",   "Save project",    "file_save",   VK_S,      VK_F3, 0 },
        { "closeProject",  "Close project",   "file_close",  VK_C,      VK_F4, 0 },
        
        { "mappingTool",   "Matrix Mapping",  "mapping",     VK_M,      VK_M, CTRL_DOWN_MASK | ALT_DOWN_MASK },
        { "jcMapTool",     "Concept Map",     "mapping",     VK_P,      VK_P, CTRL_DOWN_MASK | ALT_DOWN_MASK },
    } ;
    
    public Actions() {
        exitAppAction       = constructAction( "exitApp"       ) ;
        
        newRawFileAction    = constructAction( "newRawFile"    ) ;
        openRawFileAction   = constructAction( "openRawFile"   ) ;
        closeRawFileAction  = constructAction( "closeRawFile"  ) ;
        saveRawFileAction   = constructAction( "saveRawFile"   ) ;
        zoomInRawAction     = constructAction( "zoomInRaw"     ) ;
        zoomOutRawAction    = constructAction( "zoomOutRaw"    ) ;

        newJNFileAction    = constructAction( "newJNFile"    ) ;
        openJNFileAction   = constructAction( "openJNFile"   ) ;
        saveJNFileAction   = constructAction( "saveJNFile"   ) ;
        closeJNFileAction  = constructAction( "closeJNFile"  ) ;
        zoomInJNAction     = constructAction( "zoomInJN"     ) ;
        zoomOutJNAction    = constructAction( "zoomOutJN"    ) ;
        
        newProjectAction   = constructAction( "newProject"   ) ;
        openProjectAction  = constructAction( "openProject"  ) ;
        saveProjectAction  = constructAction( "saveProject"  ) ;
        closeProjectAction = constructAction( "closeProject" ) ;
        
        mappingToolAction  = constructAction( "mappingTool" ) ;
        jcMapToolAction    = constructAction( "jcMapTool"   ) ;
    }
    
    public AbstractBaseAction getNewRawFileAction() {
        return newRawFileAction ;
    }
    
    public AbstractBaseAction getOpenRawFileAction() {
        return openRawFileAction;
    }

    public AbstractBaseAction getCloseRawFileAction() {
        return closeRawFileAction;
    }

    public AbstractBaseAction getSaveRawFileAction() {
        return saveRawFileAction;
    }

    public AbstractBaseAction getExitAppAction() {
        return exitAppAction ;
    }
    
    public AbstractBaseAction getZoomInRawAction() {
        return zoomInRawAction;
    }

    public AbstractBaseAction getZoomOutRawAction() {
        return zoomOutRawAction;
    }
    
    public AbstractBaseAction getNewJNFileAction() {
        return newJNFileAction;
    }

    public AbstractBaseAction getOpenJNFileAction() {
        return openJNFileAction;
    }

    public AbstractBaseAction getCloseJNFileAction() {
        return closeJNFileAction;
    }

    public AbstractBaseAction getSaveJNFileAction() {
        return saveJNFileAction;
    }

    public AbstractBaseAction getZoomInJNAction() {
        return zoomInJNAction;
    }

    public AbstractBaseAction getZoomOutJNAction() {
        return zoomOutJNAction;
    }
    
    public AbstractBaseAction getNewProjectAction() {
        return newProjectAction;
    }

    public AbstractBaseAction getOpenProjectAction() {
        return openProjectAction;
    }

    public AbstractBaseAction getSaveProjectAction() {
        return saveProjectAction;
    }

    public AbstractBaseAction getCloseProjectAction() {
        return closeProjectAction;
    }
    
    public AbstractBaseAction getMatrixMappingToolAction() {
        return mappingToolAction ;
    }
    
    public AbstractBaseAction getJCMapToolAction() {
        return jcMapToolAction ;
    }
    
    @SuppressWarnings( "serial" )
    private AbstractBaseAction constructAction( String actionId ) {
        
        AbstractBaseAction action = null ;
        int i = 0 ;
        
        for( i=0; i<menuConfig.length; i++ ) {
            if( menuConfig[i][0].toString().equals( actionId ) ) {
                break ;
            }
        }
        
        if( i == menuConfig.length ) {
            throw new IllegalArgumentException( "Menu with id " + actionId +
                                                " is not configured." ) ;
        }
        else {
            String fnName      = (String)menuConfig[i][0] ;
            String displayName = (String)menuConfig[i][1] ;
            String iconName    = (String)menuConfig[i][2] ;
            int    mnemonic    = (int)menuConfig[i][3] ;
            int    accelerator = (int)menuConfig[i][4] ;
            int    accMods     = (int)menuConfig[i][5] ;
            
            final Method m = ReflectionUtil.findMethod( Actions.class, fnName, null ) ;
            if( m == null ) {
                throw new IllegalArgumentException( "Method with name " + 
                                           actionId + " is not implemented." ) ;
            }
            else {
                action = new AbstractBaseAction( displayName, iconName, 
                                                 mnemonic, accelerator, 
                                                 accMods ) {
                    
                    @Override public void actionPerformed( ActionEvent e ) {
                        try {
                            m.setAccessible( true ) ;
                            m.invoke( Actions.this, (Object[])null ) ;
                        }
                        catch( Exception e1 ) {
                            JOptionPane.showMessageDialog( getMainFrame(), 
                                    "Error in invoking action - " + e1.getMessage() ) ;
                            logger.error( "Error invoking action", e1 ) ;
                        }
                    }
                } ;
            }
        }
        return action ;
    }
    
    @SuppressWarnings( "unused" )
    private void newRawFile() {
        getMainFrame().getRawTextPanel().newFile() ;
    }
    
    @SuppressWarnings( "unused" )
    private void openRawFile() {
        getMainFrame().getRawTextPanel().openFile() ;
    }
    
    @SuppressWarnings( "unused" )
    private void closeRawFile() {
        getMainFrame().getRawTextPanel().closeFile() ;
    }
    
    @SuppressWarnings( "unused" )
    private void saveRawFile() {
        getMainFrame().getRawTextPanel().saveFile() ;
    }
    
    @SuppressWarnings( "unused" )
    private void zoomInRaw() {
        getMainFrame().getRawTextPanel().zoom( true ) ;
    }
    
    @SuppressWarnings( "unused" )
    private void zoomOutRaw() {
        getMainFrame().getRawTextPanel().zoom( false ) ;
    }
    
    @SuppressWarnings( "unused" )
    private void newJNFile() {
        getMainFrame().getJNPanel().newFile() ;
    }
    
    @SuppressWarnings( "unused" )
    private void openJNFile() {
        getMainFrame().getJNPanel().openFile() ;
    }
    
    @SuppressWarnings( "unused" )
    private void closeJNFile() {
        getMainFrame().getJNPanel().closeFile() ;
    }
    
    @SuppressWarnings( "unused" )
    private void saveJNFile() {
        getMainFrame().getJNPanel().saveFile() ;
    }
    
    @SuppressWarnings( "unused" )
    private void zoomInJN() {
        getMainFrame().getJNPanel().zoom( true ) ;
    }
    
    @SuppressWarnings( "unused" )
    private void zoomOutJN() {
        getMainFrame().getJNPanel().zoom( false ) ;
    }
    
    @SuppressWarnings( "unused" )
    private void exitApp() {
        getMainFrame().handleWindowClosing() ;
    }
    
    @SuppressWarnings( "unused" )
    private void newProject() {
        getProjectManager().newProject() ;
    }
    
    @SuppressWarnings( "unused" )
    private void openProject() {
        getProjectManager().openProject( null ) ;
    }
    
    @SuppressWarnings( "unused" )
    private void saveProject() {
        getProjectManager().saveProject() ;
    }
    
    @SuppressWarnings( "unused" )
    private void closeProject() {
        getProjectManager().closeProject() ;
    }
    
    @SuppressWarnings( "unused" )
    private void mappingTool() {
        executeTool( mappingTool ) ;
    }
    
    @SuppressWarnings( "unused" )
    private void jcMapTool() throws Exception {
        if( cMapper == null ) {
            cMapper = new CMapper() ;
        }

        JCMap tool = cMapper.getCMapper();
        tool.setVisible( true ) ;
        tool.getEditor().requestFocus() ;
    }
    
    private void executeTool( final AbstractTool tool ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                JFrame    mainFrame = ObjectRepository.getMainFrame() ;
                Component glassPane = mainFrame.getGlassPane() ;
                
                if( tool.shouldFreezeMainFrame() ) {
                    glassPane.setVisible( true ) ;
                }
                try {
                    tool.execute() ;
                }
                catch( Exception e ) {
                    logger.error( "Tool " + tool.getDisplayName() + " failed.", e ) ;
                    JOptionPane.showMessageDialog( mainFrame, e.getMessage(), 
                                   "Tool failure", JOptionPane.ERROR_MESSAGE ) ;
                }
                finally {
                    if( tool.shouldFreezeMainFrame() ) {
                        glassPane.setVisible( false ) ;
                    }
                }
            }
        } ) ;
    }
}
