package com.sandy.jnmaker.ui.panels.image;

import java.awt.event.ActionListener ;
import java.io.File ;

import org.apache.log4j.Logger ;

import com.sandy.common.ui.CloseableTabbedPane.TabCloseListener ;
import com.sandy.common.ui.ScalableImagePanel.ScalableImagePanelListener ;
import com.sandy.jeecoach.util.AbstractQuestion ;

@SuppressWarnings( { "serial", "rawtypes" } )
public class DefaultImagePanel extends AbstractImagePanel
    implements ActionListener, TabCloseListener, ScalableImagePanelListener {

    static final Logger log = Logger.getLogger( DefaultImagePanel.class ) ;
    
    public DefaultImagePanel() {
        super() ;
    }
    
    public File getRecommendedSaveDir( File imgFile ) {
        return imgFile.getParentFile().getParentFile() ;
    }

    @Override
    protected AbstractQuestion constructQuestion( File file ) {
        return null ;
    }
}
