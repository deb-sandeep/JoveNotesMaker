package com.sandy.jnmaker.ui.panels.image;

import org.apache.log4j.Logger ;

@SuppressWarnings( "serial" )
public class NoOpFnKeyHandler extends SaveFnKeyHandler {

    private static final Logger log = Logger.getLogger( NoOpFnKeyHandler.class ) ;
    
    public NoOpFnKeyHandler() {
        super( "NoOp" ) ;
    }
    
    @Override
    public void handleEvent() {
        log.debug( "No action performed. This is the base function key handler." ) ;
    }
}
