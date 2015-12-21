package com.sandy.jnmaker ;

import org.apache.log4j.Logger ;

import com.sandy.common.bus.EventBus ;
import com.sandy.common.objfactory.SpringObjectFactory ;
import com.sandy.common.util.Configurator ;
import com.sandy.common.util.WorkspaceManager ;
import com.sandy.jnmaker.util.ConfiguratorBuilder ;
import com.sandy.jnmaker.util.JNMCommandLine ;

import static com.sandy.jnmaker.util.ObjectRepository.* ;

public class JoveNotesMaker {
    
    private static final Logger logger = Logger.getLogger( JoveNotesMaker.class ) ;
    
    public static final String APP_ID = "jnmaker" ;
    
    public void launch( String[] args ) throws Exception {
        
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
        configurator.registerConfigurableObject( "JoveNotesMaker", this ) ;
        configurator.initialize() ;
    }
    
    public static void main( String[] args ) {
        
        logger.info( "Starting JoveNotesMaker application" ) ;
        JoveNotesMaker app = new JoveNotesMaker() ;
        try {
            app.launch( args ) ;
        }
        catch( Exception e ) {
            logger.error( "JoveNotesMaker exitted with an exception", e ) ;
        }
    }
}
