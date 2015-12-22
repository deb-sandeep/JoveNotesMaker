package com.sandy.jnmaker.ui;

import static com.sandy.common.ui.SwingUtils.getScreenHeight ;
import static com.sandy.common.ui.SwingUtils.getScreenWidth ;
import static com.sandy.jnmaker.ui.helper.UIUtil.getIcon ;

import java.awt.Component ;

import javax.swing.BorderFactory ;
import javax.swing.JMenuBar ;
import javax.swing.JSplitPane ;
import javax.swing.border.BevelBorder ;

import com.sandy.common.ui.AbstractMainFrame ;
import com.sandy.common.ui.statusbar.ClockSBComponent ;
import com.sandy.common.ui.statusbar.StatusBar ;
import com.sandy.common.ui.statusbar.StatusBar.Direction ;
import com.sandy.jnmaker.ui.panels.ImagePanel ;
import com.sandy.jnmaker.ui.panels.JoveNotesPanel ;
import com.sandy.jnmaker.ui.panels.RawTextPanel ;

public class MainFrame extends AbstractMainFrame {

    private static final long serialVersionUID = -793491630867632079L;

    public MainFrame() throws Exception {
        super( "JoveNotes Maker", getIcon( "app_icon" ) ) ;
    }

    @Override
    protected Component getCenterComponent() {
        
        JSplitPane bottomSP = createBottomScrollPane() ;
        JSplitPane baseSP   = createBaseScrollPane( bottomSP ) ; 
        
        return baseSP ;
    }

    @Override
    protected void handleWindowClosing() {
        super.handleWindowClosing();
    }

    @Override
    protected JMenuBar getFrameMenu() {
        return super.getFrameMenu();
    }

    @Override
    protected StatusBar getStatusBar() {
        StatusBar statusBar = new StatusBar() ;
        statusBar.addStatusBarComponent( new ClockSBComponent(), Direction.EAST ) ;
        statusBar.setBorder( BorderFactory.createBevelBorder( BevelBorder.LOWERED ) );
        return statusBar ;
    }
    
    private JSplitPane createBottomScrollPane() {
        
        JSplitPane splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT ) ;
        
        splitPane.setDividerLocation( (int)(0.5 * getScreenWidth()) ) ;
        splitPane.add( new RawTextPanel() ) ;
        splitPane.add( new JoveNotesPanel() ) ;
        setCommonScrollPaneAttributes( splitPane ) ;
        
        return splitPane ;
    }
    
    private JSplitPane createBaseScrollPane( Component bottomComponent ) {
        
        JSplitPane splitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT ) ;
        
        splitPane.setDividerLocation( (int)(0.3 * getScreenHeight()) ) ;
        splitPane.add( new ImagePanel() ) ;
        splitPane.add( bottomComponent ) ;
        setCommonScrollPaneAttributes( splitPane ) ;
        
        return splitPane ;
    }
    
    private void setCommonScrollPaneAttributes( JSplitPane sp ) {
        
        sp.setOneTouchExpandable( true ) ; 
        sp.setDividerSize( 7 ) ;
        sp.setContinuousLayout( true ) ;
    }
}
