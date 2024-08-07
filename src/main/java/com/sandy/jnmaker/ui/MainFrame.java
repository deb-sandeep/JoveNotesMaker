package com.sandy.jnmaker.ui;

import static com.sandy.common.ui.SwingUtils.getScreenWidth ;
import static com.sandy.jnmaker.ui.helper.UIUtil.getIcon ;
import static com.sandy.jnmaker.ui.panels.rawtxt.NotesAutoCreator.*;
import static com.sandy.jnmaker.util.Events.EDITOR_TYPE_CHANGED ;
import static com.sandy.jnmaker.util.ObjectRepository.getBus ;

import java.awt.CardLayout ;
import java.awt.Color ;
import java.awt.Component ;
import java.awt.event.WindowEvent ;
import java.awt.event.WindowFocusListener ;

import javax.swing.JMenuBar ;
import javax.swing.JOptionPane ;
import javax.swing.JPanel ;
import javax.swing.JSplitPane ;
import javax.swing.SwingUtilities ;

import com.wordnik.client.model.Note;
import org.apache.commons.lang.StringUtils ;
import org.apache.log4j.Logger ;

import com.sandy.common.ui.AbstractMainFrame ;
import com.sandy.common.util.StringUtil ;
import com.sandy.jeecoach.util.AbstractQuestion ;
import com.sandy.jnmaker.ui.helper.UIUtil ;
import com.sandy.jnmaker.ui.menu.AppMenu ;
import com.sandy.jnmaker.ui.menu.ToggleInputEditorMenu.InputEditorMode ;
import com.sandy.jnmaker.ui.notedialogs.NotesCreatorDialog ;
import com.sandy.jnmaker.ui.panels.image.AbstractImagePanel ;
import com.sandy.jnmaker.ui.panels.image.DefaultImagePanel ;
import com.sandy.jnmaker.ui.panels.image.jee.JEEQuestionsImagePanel ;
import com.sandy.jnmaker.ui.panels.image.k12.K12QuestionsImagePanel ;
import com.sandy.jnmaker.ui.panels.jn.JoveNotesPanel ;
import com.sandy.jnmaker.ui.panels.rawtxt.RawTextPanel ;
import com.sandy.jnmaker.ui.panels.search.SearchInputPanel ;
import com.sandy.jnmaker.util.AppConfig ;
import com.sandy.jnmaker.util.NoteType ;
import com.sandy.jnmaker.util.ObjectRepository ;

public class MainFrame extends AbstractMainFrame {

    private static final Logger log = Logger.getLogger( MainFrame.class ) ;
    
    private JPanel             inputEditorPanel = null ;
    private SearchInputPanel   searchPanel      = null ;
    private RawTextPanel       rawTextPanel     = null ;
    private JoveNotesPanel     jnPanel          = null ;
    
    private final NotesCreatorDialog notesCreator ;
    
    @SuppressWarnings( "rawtypes" )
    private AbstractImagePanel<? extends AbstractQuestion> imagePanel = null ;

    public MainFrame() throws Exception {
        super( "JoveNotes Maker - []", getIcon( "app_icon" ) ) ;
        notesCreator = new NotesCreatorDialog() ;
    }

    @Override
    protected Component getCenterComponent() {
        
        JSplitPane baseSP = null ;
        try {
            JSplitPane bottomSP = createBottomSplitPane() ;
            baseSP = createBaseSplitPane( bottomSP ) ;
        }
        catch( Exception e ) {
            JOptionPane.showMessageDialog( this, "Error in creating UI." + 
                                           "Msg = " + e.getMessage() ) ;
            log.error( "Error creating UI", e ) ;
        } 
        
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

    @Override
    protected JMenuBar getFrameMenu() {
        return new AppMenu() ;
    }

    private JSplitPane createBottomSplitPane() throws Exception {
        
        this.jnPanel = new JoveNotesPanel() ;
        this.inputEditorPanel = getInputEditorPanel() ;
        
        JSplitPane splitPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT ) ;
        
        splitPane.setDividerLocation( (int)(0.5 * getScreenWidth()) ) ;
        splitPane.add( this.inputEditorPanel ) ;
        splitPane.add( this.jnPanel ) ;
        setCommonScrollPaneAttributes( splitPane ) ;
        
        return splitPane ;
    }
    
    private JPanel getInputEditorPanel() throws Exception {
        
        this.rawTextPanel     = new RawTextPanel() ;
        this.searchPanel      = new SearchInputPanel() ;

        JPanel panel = new JPanel( new CardLayout( 0, 0 ) ) ;
        
        panel.add( InputEditorMode.RAW_TEXT.toString(), this.rawTextPanel ) ;
        panel.add( InputEditorMode.SEARCH.toString(), this.searchPanel ) ;
        
        return panel ;
    }
    
    @SuppressWarnings( "unchecked" )
    private JSplitPane createBaseSplitPane( Component bottomComponent ) {
        
        AppConfig appCfg = ObjectRepository.getAppConfig() ;
        String imgPanelType = appCfg.getImagePanelType() ; 
        
        if( StringUtil.isEmptyOrNull( imgPanelType ) ) {
            this.imagePanel = new DefaultImagePanel() ;
        }
        else if( imgPanelType.equals( K12QuestionsImagePanel.ID ) ) {
            this.imagePanel = new K12QuestionsImagePanel() ;
        }
        else if( imgPanelType.equals( JEEQuestionsImagePanel.ID ) ) {
            this.imagePanel = new JEEQuestionsImagePanel() ;
        }
        
        JSplitPane splitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT ) ;
        
        splitPane.setDividerLocation( 1.0d ) ;
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

    @SuppressWarnings( "rawtypes" )
    public AbstractImagePanel<? extends AbstractQuestion> getImagePanel() {
        return this.imagePanel;
    }

    public JoveNotesPanel getJNPanel() {
        return this.jnPanel;
    }

    public void createNote( String selectedText, NoteType noteType ) {
        
        if( !processNoteWithoutHumanIntervention( selectedText, noteType ) ) {
            notesCreator.createNote( selectedText, noteType, jnPanel ) ;
        }
    }
    
    private boolean processNoteWithoutHumanIntervention( String selectedText,
                                                         NoteType noteType ) {
        
        String autoCreatedNote = null ;

        if( noteType == NoteType.AS_IS ) {
            autoCreatedNote = selectedText + "\n\n" ;
        }
        else if( noteType == NoteType.SECTION ) {
            if( selectedText.startsWith( "\"" ) ) {
                selectedText = selectedText.substring( 1 ) ;
            }
            
            if( selectedText.endsWith( "\"" ) ) {
                selectedText = selectedText.substring( 0, selectedText.length()-1 ) ;
            }
            
            String text = StringUtils.rightPad( "//", 80, '-' ) ;
            text += "\n@section \"" + selectedText + "\"\n\n" ; 
            autoCreatedNote = text ;
        }
        else if( noteType == NoteType.FIB ) {
            autoCreatedNote = autoCreateFIBNote( selectedText ) ;
        }
        else if( noteType == NoteType.DEFINITION ) {
            autoCreatedNote = autoCreateDefinitionNote( selectedText ) ;
        }
        else if( noteType == NoteType.QA ) {
            autoCreatedNote = autoCreateQANote( selectedText ) ;
        }
        else if( noteType == NoteType.MATCHING ) {
            autoCreatedNote = autoCreateMatchingNote( selectedText ) ;
        }
        else if( noteType == NoteType.MULTI_CHOICE ) {
            autoCreatedNote = autoCreateMultiChoiceNote( selectedText ) ;
        }
        else if( noteType == NoteType.TRUE_FALSE ) {
            autoCreatedNote = autoCreateTrueFalseNote( selectedText ) ;
        }
        else if( noteType == NoteType.CHOICE_GROUP ) {
            autoCreatedNote = autoCreateMultiChoiceGroupNote( selectedText ) ;
        }
        else if( noteType == NoteType.CHOICE_BULK ) {
            autoCreatedNote = autoCreateMultiChoiceBulkNote( selectedText ) ;
        }
        else if( noteType == NoteType.CHEM_COMPOUND ) {
            autoCreatedNote = autoCreateChemCompoundNote( selectedText ) ;
        }
        else if( noteType == NoteType.CHEM_EQUATION ) {
            autoCreatedNote = autoCreateChemEquationNote( selectedText ) ;
        }
        else if( noteType == NoteType.RTC ) {
            autoCreatedNote = autoCreateRTCNode( selectedText ) ;
        }

        if( StringUtil.isNotEmptyOrNull( autoCreatedNote ) ) {
            this.jnPanel.addNote( autoCreatedNote ) ;
            return true ;
        }
        
        return false ;
    }
    
    public void shiftFocusToNotes() {
        this.jnPanel.captureFocus() ;
    }
    
    public void shiftFocusToRawText() {
        this.rawTextPanel.captureFocus() ;
    }

    public void switchInputEditor( InputEditorMode currentMode, 
                                   final String defaultSearchString ) {
        
        CardLayout cl = ( CardLayout )inputEditorPanel.getLayout() ;
        cl.show( inputEditorPanel, currentMode.toString() ) ;
        
        if( currentMode == InputEditorMode.SEARCH ) {
            SwingUtilities.invokeLater( () -> searchPanel.setQueryAndSearch( defaultSearchString ) ) ;
        }
        
        getBus().publishEvent( EDITOR_TYPE_CHANGED, currentMode );
    }
}
