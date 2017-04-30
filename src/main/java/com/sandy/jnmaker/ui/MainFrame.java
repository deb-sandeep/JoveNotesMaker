package com.sandy.jnmaker.ui;

import static com.sandy.common.ui.SwingUtils.getScreenWidth ;
import static com.sandy.jnmaker.ui.helper.UIUtil.getIcon ;

import java.awt.CardLayout ;
import java.awt.Color ;
import java.awt.Component ;
import java.awt.event.WindowEvent ;
import java.awt.event.WindowFocusListener ;

import javax.swing.JMenuBar ;
import javax.swing.JPanel ;
import javax.swing.JSplitPane ;

import com.sandy.common.ui.AbstractMainFrame ;
import com.sandy.jnmaker.ui.helper.UIUtil ;
import com.sandy.jnmaker.ui.menu.AppMenu ;
import com.sandy.jnmaker.ui.menu.ToggleInputEditorMenu.InputEditorMode ;
import com.sandy.jnmaker.ui.notedialogs.NotesCreatorDialog ;
import com.sandy.jnmaker.ui.panels.ImagePanel ;
import com.sandy.jnmaker.ui.panels.JoveNotesPanel ;
import com.sandy.jnmaker.ui.panels.RawTextPanel ;
import com.sandy.jnmaker.ui.panels.SearchInputPanel ;
import com.sandy.jnmaker.util.NoteType ;
import com.sandy.jnmaker.util.ObjectRepository ;

public class MainFrame extends AbstractMainFrame {

    private static final long serialVersionUID = -793491630867632079L ;
    
    private JPanel           inputEditorPanel = null ;
    private SearchInputPanel searchPanel      = null ;
    private RawTextPanel     rawTextPanel     = null ;
    private ImagePanel       imagePanel       = null ;
    private JoveNotesPanel   jnPanel          = null ;
    
    private NotesCreatorDialog notesCreator = null ;

    public MainFrame() throws Exception {
        super( "JoveNotes Maker - []", getIcon( "app_icon" ) ) ;
        notesCreator = new NotesCreatorDialog() ;
    }

    @Override
    protected Component getCenterComponent() {
        
        JSplitPane bottomSP = createBottomSplitPane() ;
        JSplitPane baseSP   = createBaseSplitPane( bottomSP ) ; 
        
        return baseSP ;
    }

    @Override
    public void handleWindowClosing() {
        if( ObjectRepository.getProjectManager().closeProject() ) {
            ObjectRepository.getIndexingDaemon().shutDown() ;
            super.handleWindowClosing() ;
        }
    }
    
    @Override
    protected void setUpListeners() {
        super.setUpListeners() ;
        addWindowFocusListener( new WindowFocusListener() {
            @Override public void windowLostFocus( WindowEvent e ) { }
            @Override public void windowGainedFocus( WindowEvent e ) {
                rawTextPanel.captureFocus() ;
            }
        } );
    }
    
    protected JMenuBar getFrameMenu() {
        return new AppMenu() ;
    }

    private JSplitPane createBottomSplitPane() {
        
        this.jnPanel = new JoveNotesPanel() ;
        this.inputEditorPanel = getInputEditorPanel() ;
        
        JSplitPane splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT ) ;
        
        splitPane.setDividerLocation( (int)(0.5 * getScreenWidth()) ) ;
        splitPane.add( this.inputEditorPanel ) ;
        splitPane.add( this.jnPanel ) ;
        setCommonScrollPaneAttributes( splitPane ) ;
        
        return splitPane ;
    }
    
    private JPanel getInputEditorPanel() {
        
        this.rawTextPanel     = new RawTextPanel() ;
        this.searchPanel      = new SearchInputPanel() ;

        JPanel panel = new JPanel( new CardLayout( 0, 0 ) ) ;
        
        panel.add( InputEditorMode.RAW_TEXT.toString(), this.rawTextPanel ) ;
        panel.add( InputEditorMode.SEARCH.toString(), this.searchPanel ) ;
        
        return panel ;
    }
    
    private JSplitPane createBaseSplitPane( Component bottomComponent ) {
        
        this.imagePanel = new ImagePanel() ;
        
        JSplitPane splitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT ) ;
        
        splitPane.setDividerLocation( 0.0d ) ;
        splitPane.add( this.imagePanel ) ;
        splitPane.add( bottomComponent ) ;
        setCommonScrollPaneAttributes( splitPane ) ;
        splitPane.setOneTouchExpandable( true ) ;
        splitPane.setDividerSize( 7 ) ;
        
        return splitPane ;
    }
    
    private void setCommonScrollPaneAttributes( JSplitPane sp ) {
        
        sp.setOneTouchExpandable( false ) ; 
        sp.setDividerSize( 2 ) ;
        sp.setContinuousLayout( true ) ;
        UIUtil.setSplitPaneBackground( Color.BLACK, sp ) ;
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

    public JoveNotesPanel getJNPanel() {
        return this.jnPanel;
    }

    public void setJNPanel( JoveNotesPanel jnPanel ) {
        this.jnPanel = jnPanel;
    }
    
    public void createNote( String selectedText, NoteType noteType ) {
        notesCreator.createNote( selectedText, noteType, jnPanel ) ;
    }
    
    public void shiftFocusToNotes() {
        this.jnPanel.captureFocus() ;
    }
    
    public void shiftFocusToRawText() {
        this.rawTextPanel.captureFocus() ;
    }

    public void switchInputEditor( InputEditorMode currentMode ) {
        CardLayout cl = ( CardLayout )inputEditorPanel.getLayout() ;
        cl.show( inputEditorPanel, currentMode.toString() );
    }
}
