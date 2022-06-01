package com.sandy.jnmaker.ui.menu;

import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;

import javax.swing.JMenuItem ;

import com.sandy.common.bus.Event ;
import com.sandy.common.bus.EventSubscriber ;
import com.sandy.jnmaker.ui.MainFrame ;

import static com.sandy.jnmaker.util.ObjectRepository.* ;
import static com.sandy.jnmaker.util.Events.* ;

@SuppressWarnings( "serial" )
public class ToggleInputEditorMenu extends JMenuItem 
    implements ActionListener, EventSubscriber {

    public static enum InputEditorMode { RAW_TEXT, SEARCH } ;
    
    private static final String RAW_TEXT_MENU_LABEL = "Show raw text input" ;
    private static final String SEARCH_MENU_LABEL   = "Show search input" ;
    
    private InputEditorMode currentMode = InputEditorMode.RAW_TEXT ;
    
    public ToggleInputEditorMenu() {
        super.setText( getMenuLabel() ) ;
        addActionListener( this ) ;
        getBus().addSubscriberForEventTypes( this, false, EDITOR_TYPE_CHANGED ) ;
    }
    
    private String getMenuLabel() {
        if( currentMode == InputEditorMode.RAW_TEXT ) {
            return SEARCH_MENU_LABEL ;
        }
        return RAW_TEXT_MENU_LABEL ;
    }

    @Override
    public void actionPerformed( ActionEvent e ) {
        
        currentMode = ( currentMode == InputEditorMode.RAW_TEXT ) ? 
                      InputEditorMode.SEARCH : 
                      InputEditorMode.RAW_TEXT ;
        
        MainFrame mainFrame = getMainFrame() ;
        mainFrame.switchInputEditor( currentMode, "" ) ;
    }

    @Override
    public void handleEvent( Event event ) {
        
        currentMode = ( InputEditorMode )event.getValue() ;
        super.setText( getMenuLabel() ) ;
    }
}
