package com.sandy.common.jnmaker ;

import org.apache.log4j.Logger ;

public class App {
    
    private static final Logger logger = Logger.getLogger( App.class ) ;
    
    public static final String APP_ID = "jnmaker" ;
    
    public void launch( String[] args ) throws Exception {
        
        // Process command line
        
        // Initialize the object factory
        
        // Run the initializer logic
        
        // Configurator?
    }   
    
    public static void main( String[] args ) {
        
        logger.info( "Starting JoveNotesMaker application" ) ;
        App app = new App() ;
        try {
            app.launch( args ) ;
        }
        catch( Exception e ) {
            logger.error( "JoveNotesMaker exitted with an exception", e ) ;
        }
    }
}
