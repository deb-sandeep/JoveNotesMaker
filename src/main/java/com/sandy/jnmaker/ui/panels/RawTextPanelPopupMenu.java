package com.sandy.jnmaker.ui.panels;

import static com.sandy.jnmaker.util.ObjectRepository.getMainFrame ;

import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;

import javax.swing.JMenuItem ;
import javax.swing.JPopupMenu ;
import javax.swing.JTextPane ;

import com.sandy.jnmaker.ui.helper.EditMenu ;
import com.sandy.jnmaker.util.NoteType ;

public class RawTextPanelPopupMenu extends JPopupMenu implements ActionListener {

    private static final long serialVersionUID = -7265818723778988508L ;

    private RawTextPanel panel        = null ;
    private JTextPane    textPane     = null ;
    private String       selectedText = null ;
    
    private JMenuItem qaMI         = new JMenuItem() ;
    private JMenuItem fibMI        = new JMenuItem() ;
    private JMenuItem trueFalseMI  = new JMenuItem() ;
    private JMenuItem wmMI         = new JMenuItem() ;
    private JMenuItem spellbeeMI   = new JMenuItem() ;
    private JMenuItem definitionMI = new JMenuItem() ;
    private JMenuItem eventMI      = new JMenuItem() ;
    private EditMenu  editMenu     = null ;
    private JMenuItem bookmarkMI   = new JMenuItem() ;
    
    public RawTextPanelPopupMenu( RawTextPanel rawTextPanel ) {
        
        super( "Make notes" ) ;
        this.panel = rawTextPanel ;
        this.textPane = rawTextPanel.textPane ;
        this.editMenu = new EditMenu( this, this.textPane ) ;
        setUpUI() ;
    }
    
    private void setUpUI() {
        
        add( editMenu ) ;
        add( new Separator() ) ;
        add( prepareMenuItem( qaMI,         "@qa",         NoteType.QA ) ) ;
        add( prepareMenuItem( fibMI,        "@fib",        NoteType.FIB ) ) ;
        add( prepareMenuItem( trueFalseMI,  "@true_false", NoteType.TRUE_FALSE ) ) ;
        add( prepareMenuItem( wmMI,         "@wm",         NoteType.WORD_MEANING ) ) ;
        add( prepareMenuItem( spellbeeMI,   "@spellbee",   NoteType.SPELLBEE ) ) ;
        add( prepareMenuItem( definitionMI, "@definition", NoteType.DEFINITION ) ) ;
        add( prepareMenuItem( eventMI,      "@event",      NoteType.EVENT ) ) ;
        add( new Separator() ) ;
        add( prepareMenuItem( null, "// comment",  NoteType.COMMENT ) ) ;
        add( new Separator() ) ;
        add( prepareMenuItem( bookmarkMI,   "Bookmark", null ) ) ;
    }
    
    private JMenuItem prepareMenuItem( JMenuItem mi, String label, NoteType noteType ) {
        
        if( mi == null ) {
            mi = new JMenuItem() ;
        }
        
        mi.setText( label ) ;
        mi.addActionListener( this ) ;
        if( noteType != null ) {
            mi.setActionCommand( noteType.toString() ) ;
        }
        
        return mi ;
    }

    @Override
    public void actionPerformed( ActionEvent e ) {
        
        if( e.getSource() == bookmarkMI ) {
            panel.reviseBookmark() ;
        }
        else {
            String   actionCmd = e.getActionCommand() ;
            NoteType noteType  = NoteType.valueOf( actionCmd ) ;
            
            getMainFrame().createNote( this.selectedText, noteType ) ;
        }
    }
    
    public void show( String selText, int x, int y ) {
        
        this.selectedText = ( selText != null ) ? selText.trim() : selText ;
        super.show( this.textPane, x, y ) ;
    }
    
    public void enableJNMenuItems( boolean enabled ) {
        
        qaMI.setEnabled( enabled ) ;
        fibMI.setEnabled( enabled ) ;
        trueFalseMI.setEnabled( enabled ) ;
        wmMI.setEnabled( enabled ) ;
        spellbeeMI.setEnabled( enabled ) ;
        definitionMI.setEnabled( enabled ) ;
        eventMI.setEnabled( enabled ) ;
    }
    
    public void doUndo() {
        editMenu.doUndo() ; 
    }
    
    public void doRedo() {
        editMenu.doRedo() ;
    }
}
