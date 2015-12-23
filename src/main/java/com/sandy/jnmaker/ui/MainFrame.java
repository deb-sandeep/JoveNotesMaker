package com.sandy.jnmaker.ui;

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
import com.sandy.jnmaker.NoteType ;
import com.sandy.jnmaker.ui.dialogs.NotesCreatorDialog ;
import com.sandy.jnmaker.ui.panels.ImagePanel ;
import com.sandy.jnmaker.ui.panels.JoveNotesPanel ;
import com.sandy.jnmaker.ui.panels.RawTextPanel ;

public class MainFrame extends AbstractMainFrame {

    private static final long serialVersionUID = -793491630867632079L ;
    
    private RawTextPanel   rawTextPanel = null ;
    private ImagePanel     imagePanel   = null ;
    private JoveNotesPanel jnPanel      = null ;
    
    private NotesCreatorDialog notesCreator = null ;

    public MainFrame() throws Exception {
        super( "JoveNotes Maker", getIcon( "app_icon" ) ) ;
        notesCreator = new NotesCreatorDialog() ;
    }

    @Override
    protected Component getCenterComponent() {
        
        JSplitPane bottomSP = createBottomScrollPane() ;
        JSplitPane baseSP   = createBaseScrollPane( bottomSP ) ; 
        
        return baseSP ;
    }

    @Override
    protected void handleWindowClosing() {
        if( rawTextPanel.isEditorDirty() ) {
            if( !rawTextPanel.userConsentToDiscardChanges() ) {
                return ;
            }
        }
        
        if( jnPanel.isEditorDirty() ) {
            if( !jnPanel.userConsentToDiscardChanges() ) {
                return ;
            }
        }
        super.handleWindowClosing() ;
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
        
        this.rawTextPanel = new RawTextPanel() ;
        this.jnPanel      = new JoveNotesPanel() ;
        
        JSplitPane splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT ) ;
        
        splitPane.setDividerLocation( (int)(0.5 * getScreenWidth()) ) ;
        splitPane.add( this.rawTextPanel ) ;
        splitPane.add( this.jnPanel ) ;
        setCommonScrollPaneAttributes( splitPane ) ;
        
        return splitPane ;
    }
    
    private JSplitPane createBaseScrollPane( Component bottomComponent ) {
        
        this.imagePanel = new ImagePanel() ;
        
        JSplitPane splitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT ) ;
        
        splitPane.setDividerLocation( 0.0d ) ;
        splitPane.add( this.imagePanel ) ;
        splitPane.add( bottomComponent ) ;
        setCommonScrollPaneAttributes( splitPane ) ;
        
        return splitPane ;
    }
    
    private void setCommonScrollPaneAttributes( JSplitPane sp ) {
        
        sp.setOneTouchExpandable( true ) ; 
        sp.setDividerSize( 7 ) ;
        sp.setContinuousLayout( true ) ;
    }

    public RawTextPanel getRawTextPanel() {
        return this.rawTextPanel;
    }

    public void setRawTextPanel( RawTextPanel rawTextPanel ) {
        this.rawTextPanel = rawTextPanel;
    }

    public ImagePanel getImagePanel() {
        return this.imagePanel;
    }

    public void setImagePanel( ImagePanel imagePanel ) {
        this.imagePanel = imagePanel;
    }

    public JoveNotesPanel getJnPanel() {
        return this.jnPanel;
    }

    public void setJnPanel( JoveNotesPanel jnPanel ) {
        this.jnPanel = jnPanel;
    }
    
    public void createNote( String selectedText, NoteType noteType ) {
        notesCreator.createNote( selectedText, noteType, jnPanel ) ;
    }
}
