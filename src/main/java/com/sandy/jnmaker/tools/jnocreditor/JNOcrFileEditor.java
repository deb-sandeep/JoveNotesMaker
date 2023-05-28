package com.sandy.jnmaker.tools.jnocreditor;

import com.sandy.common.ui.AbstractMainFrame;
import com.sandy.jnmaker.ui.helper.UIUtil;
import com.sandy.jnmaker.ui.menu.AppMenu;
import com.sandy.jnmaker.ui.panels.jn.JoveNotesPanel;
import com.sandy.jnmaker.util.ObjectRepository;

import javax.swing.*;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import static com.sandy.common.ui.SwingUtils.getScreenWidth;
import static com.sandy.jnmaker.ui.helper.UIUtil.getIcon;

public class JNOcrFileEditor extends AbstractMainFrame {

    private JPanel treePanel ;
    private JPanel editorPanel ;

    public JNOcrFileEditor() throws Exception {
        super( "JN-OCR Editor - []", getIcon( "app_icon" ) ) ;
    }

    @Override
    protected Component getCenterComponent() {

        // TODO Initialize treePanel
        // TODO Initialize editorPanel

        JSplitPane splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT ) ;

        splitPane.setDividerLocation( (int)(0.2 * getScreenWidth()) ) ;
        splitPane.add( this.treePanel ) ;
        splitPane.add( this.editorPanel ) ;

        splitPane.setOneTouchExpandable( false ) ;
        splitPane.setDividerSize( 2 ) ;
        splitPane.setContinuousLayout( true ) ;
        UIUtil.setSplitPaneBackground( Color.BLACK, splitPane ) ;

        return splitPane ;
    }

    @Override
    public void handleWindowClosing() {
        // TODO Check on dirty file
    }

    @Override
    protected void setUpListeners() {
        super.setUpListeners() ;
        addWindowFocusListener( new WindowFocusListener() {
            @Override public void windowLostFocus( WindowEvent e ) { }
            @Override public void windowGainedFocus( WindowEvent e ) {
                // TODO Capture the focus on the editor
            }
        } );
    }

    @Override
    protected JMenuBar getFrameMenu() {
        // TODO Create JMenuBar (ref AppMenu) and return
        return null ;
    }
}
