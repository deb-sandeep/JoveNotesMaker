package com.sandy.jnmaker.ui.panels.image.jee;

import java.awt.event.ActionListener ;
import java.io.File ;

import org.apache.log4j.Logger ;

import com.sandy.common.ui.CloseableTabbedPane.TabCloseListener ;
import com.sandy.common.ui.ScalableImagePanel.ScalableImagePanelListener ;
import com.sandy.jnmaker.ui.panels.image.AbstractImagePanel ;

@SuppressWarnings( "serial" )
public class JEEQuestionsImagePanel extends AbstractImagePanel 
    implements ActionListener, TabCloseListener, ScalableImagePanelListener {

    static final Logger log = Logger.getLogger( JEEQuestionsImagePanel.class ) ;

    @Override
    protected File getUserApprovedOutputFile( int selMod ) {
        return null ;
    }

    @Override
    protected void handlePostImageSave() {
    }
}
