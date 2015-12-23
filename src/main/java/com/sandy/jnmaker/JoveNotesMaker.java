package com.sandy.jnmaker ;

import static com.sandy.jnmaker.util.ObjectRepository.* ;

import org.apache.log4j.Logger ;

import com.sandy.common.bus.EventBus ;
import com.sandy.common.objfactory.SpringObjectFactory ;
import com.sandy.common.ui.SwingUtils ;
import com.sandy.common.util.Configurator ;
import com.sandy.common.util.StateManager ;
import com.sandy.common.util.WorkspaceManager ;
import com.sandy.jnmaker.ui.MainFrame ;
import com.sandy.jnmaker.util.ConfiguratorBuilder ;
import com.sandy.jnmaker.util.JNMCommandLine ;

public class JoveNotesMaker {
    
    private static final Logger logger = Logger.getLogger( JoveNotesMaker.class ) ;
    
    public static final String APP_ID = "jnmaker" ;
    
    public void launch( String[] args ) throws Exception {
        
        preInitialize( args ) ;
        SwingUtils.setNimbusLookAndFeel() ;
        setUpAndShowMainFrame() ;
        postInitialize() ;
    }
    
    private void preInitialize( String[] args ) throws Exception {
        
        // Process command line
        JNMCommandLine cmdLine = new JNMCommandLine() ;
        cmdLine.parse( args ) ;
        
        // Initialize the object factory
        SpringObjectFactory objFactory = new SpringObjectFactory() ;
        objFactory.addResourcePath( "classpath:com/sandy/jnmaker/objfactory.xml" ) ;
        objFactory.initialize() ;
        setObjectFactory( objFactory ) ;
        
        // Create the event bus and register it with the object repository
        EventBus eventBus = new EventBus() ;
        setBus( eventBus ) ;
        
        // Initialize the workspace
        WorkspaceManager wkspMgr = new WorkspaceManager( APP_ID ) ;
        setWkspManager( wkspMgr ) ;
        
        // Configure the system components
        ConfiguratorBuilder builder = new ConfiguratorBuilder( APP_ID, cmdLine ) ;
        Configurator configurator = builder.createConfigurator() ;
        configurator.initialize() ;
        
    }
    
    private void postInitialize() throws Exception {
        // Initialize the event bus registrations

        // Initialize the state manager
        initializeStateManager() ;
    }
    
    private void initializeStateManager() throws Exception {
        
        StateManager stateManager = new StateManager( this, getWkspManager() ) ;
        stateManager.registerObject( "ImagePanel",     getMainFrame().getImagePanel() ) ;
        stateManager.registerObject( "RawTextPanel",   getMainFrame().getRawTextPanel() ) ;
        stateManager.registerObject( "JoveNotesPanel", getMainFrame().getJnPanel() ) ;
        stateManager.initialize() ;
        stateManager.loadState() ;
        
        setStateMgr( stateManager ) ;
    }
    
    private void setUpAndShowMainFrame() throws Exception {
        
        MainFrame mainFrame = new MainFrame() ;
        mainFrame.setUp() ;
        setMainFrame( mainFrame ) ;
        mainFrame.setVisible( true ) ;
    }
    
    public static void main( String[] args ) {
        
        logger.info( "Starting JoveNotesMaker application" ) ;
        JoveNotesMaker app = new JoveNotesMaker() ;
        try {
            setApp( app ) ;
            app.launch( args ) ;
        }
        catch( Exception e ) {
            logger.error( "JoveNotesMaker exitted with an exception", e ) ;
        }
    }
}
