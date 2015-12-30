package com.sandy.jnmaker.ui.helper;

import java.awt.event.KeyEvent ;

import javax.swing.JMenu ;
import javax.swing.JMenuBar ;

import com.sandy.jnmaker.ui.actions.Actions ;
import com.sandy.jnmaker.util.ObjectRepository ;

@SuppressWarnings( "serial" )
public class AppMenu extends JMenuBar {

    private Actions actions = null ;
    
    public AppMenu() {
        actions = ObjectRepository.getUiActions() ;
        setUpMenus() ;
    }
    
    private void setUpMenus() {
        add( buildAppMenu() ) ;
        add( buildRawFileMenu() ) ;
        add( buildJNFileMenu() ) ;
    }
    
    private JMenu buildAppMenu() {
        JMenu menu = new JMenu( "App" ) ;
        menu.setMnemonic( KeyEvent.VK_A ) ;
        
        menu.add( actions.getExitAppAction() ) ;
        return menu ;
    }
    
    private JMenu buildRawFileMenu() {
        JMenu menu = new JMenu( "Raw" ) ;
        menu.setMnemonic( KeyEvent.VK_R ) ;
        
        menu.add( actions.getNewRawFileAction() ) ;
        menu.add( actions.getOpenRawFileAction() ) ;
        menu.add( actions.getSaveRawFileAction() ) ;
        menu.add( actions.getSaveAsRawFileAction() ) ;
        menu.add( actions.getCloseRawFileAction() ) ;
        menu.addSeparator() ;
        menu.add( actions.getZoomInRawAction() ) ;
        menu.add( actions.getZoomOutRawAction() ) ;
        
        return menu ;
    }
    
    private JMenu buildJNFileMenu() {
        JMenu menu = new JMenu( "JoveNotes" ) ;
        menu.setMnemonic( KeyEvent.VK_J ) ;
        
        menu.add( actions.getNewJNFileAction() ) ;
        menu.add( actions.getOpenJNFileAction() ) ;
        menu.add( actions.getSaveJNFileAction() ) ;
        menu.add( actions.getSaveAsJNFileAction() ) ;
        menu.add( actions.getCloseJNFileAction() ) ;
        menu.addSeparator() ;
        menu.add( actions.getZoomInJNAction() ) ;
        menu.add( actions.getZoomOutJNAction() ) ;
        
        return menu ;
    }
}
