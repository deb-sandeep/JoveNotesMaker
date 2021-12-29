package com.sandy.jnmaker.ui.panels.image;

import java.awt.event.ActionEvent ;

import javax.swing.AbstractAction ;

import org.apache.log4j.Logger ;

import lombok.Data ;
import lombok.EqualsAndHashCode ;

@Data
@SuppressWarnings( "serial" )
@EqualsAndHashCode( callSuper = false )
public abstract class SaveFnKeyHandler extends AbstractAction {
    
    private static final Logger log = Logger.getLogger( SaveFnKeyHandler.class ) ;
    
    protected String name = null ;
    
    public SaveFnKeyHandler( String name ) {
        this.name = name ;
    }
    
    public final void actionPerformed( ActionEvent e ) {
        log.debug( "Invoking save function handler = " + name ) ;
        handleEvent() ;
    }
    
    protected abstract void handleEvent() ;
}
