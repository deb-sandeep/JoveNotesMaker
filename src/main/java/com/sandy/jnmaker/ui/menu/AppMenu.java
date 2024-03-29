package com.sandy.jnmaker.ui.menu;

import java.awt.event.KeyEvent ;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

import javax.swing.JMenu ;
import javax.swing.JMenuBar ;

import com.sandy.jnmaker.ui.menu.actions.Actions ;
import com.sandy.jnmaker.ui.menu.actions.OpenProjectAction;
import com.sandy.jnmaker.util.AppConfig;
import com.sandy.jnmaker.util.ObjectRepository ;

public class AppMenu extends JMenuBar {

    private final Actions actions ;
    private final AppConfig config ;
    
    public AppMenu() {
        actions = ObjectRepository.getUiActions() ;
        config = ObjectRepository.getAppConfig() ;
        setUpMenus() ;
    }
    
    private void setUpMenus() {
        add( buildAppMenu() ) ;
        add( buildRawFileMenu() ) ;
        add( buildJNFileMenu() ) ;
        add( buildProjectMenu() ) ;
        add( buildToolsMenu() ) ;
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
        menu.add( actions.getCloseRawFileAction() ) ;
        menu.addSeparator() ;
        menu.add( actions.getZoomInRawAction() ) ;
        menu.add( actions.getZoomOutRawAction() ) ;
        menu.addSeparator() ;
        menu.add( new ToggleInputEditorMenu() ) ;
        
        return menu ;
    }
    
    private JMenu buildJNFileMenu() {
        JMenu menu = new JMenu( "JoveNotes" ) ;
        menu.setMnemonic( KeyEvent.VK_J ) ;
        
        menu.add( actions.getNewJNFileAction() ) ;
        menu.add( actions.getOpenJNFileAction() ) ;
        menu.add( actions.getSaveJNFileAction() ) ;
        menu.add( actions.getCloseJNFileAction() ) ;
        menu.addSeparator() ;
        menu.add( actions.getZoomInJNAction() ) ;
        menu.add( actions.getZoomOutJNAction() ) ;
        
        return menu ;
    }
    
    private JMenu buildProjectMenu() {
        JMenu menu = new JMenu( "Project" ) ;
        menu.setMnemonic( KeyEvent.VK_P ) ;
        
        menu.add( actions.getNewProjectAction() ) ;
        menu.add( actions.getOpenProjectAction() ) ;
        menu.add( actions.getSaveProjectAction() ) ;
        menu.add( actions.getCloseProjectAction() ) ;

        File jnmpDir = config.getJnmpDir() ;
        if( jnmpDir != null && jnmpDir.exists() ) {
            File[] jnmpFiles = jnmpDir.listFiles( (dir, name) -> {
                return name.endsWith( ".jnmp" ) ;
            } ) ;
            if( jnmpFiles != null && jnmpFiles.length > 0 ) {
                menu.addSeparator() ;
                Arrays.stream( jnmpFiles ).forEach( f -> menu.add( new OpenProjectAction( f ) ) ) ;
            }
        }
        
        return menu ;
    }
    
    private JMenu buildToolsMenu() {
        
        JMenu menu = new JMenu( "Tools" ) ;
        menu.setMnemonic( KeyEvent.VK_T ) ;
        
        menu.add( actions.getMatrixMappingToolAction() ) ;
        menu.add( actions.getJCMapToolAction() ) ;
        
        return menu ;
    }
}
