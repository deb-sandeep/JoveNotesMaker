package com.sandy.jnmaker.ui.menu.actions;

import com.sandy.jnmaker.util.ObjectRepository;
import org.apache.log4j.Logger;

import java.awt.event.ActionEvent;
import java.io.File;

public class OpenProjectAction extends AbstractBaseAction {

    private static final Logger log = Logger.getLogger( OpenProjectAction.class ) ;

    private final File projectFile ;

    public OpenProjectAction( File projectFile ) {
        super( "Open : " + pruneFileName( projectFile.getName() ) ) ;
        this.projectFile = projectFile ;
    }

    private static String pruneFileName( String name ) {
        return name.substring( 0, name.length()-5 ) ;
    }

    @Override
    public void actionPerformed( ActionEvent e ) {
        ObjectRepository.getProjectManager().openProject( projectFile ) ;
    }
}
