package com.sandy.jnmaker.ui;

import static com.sandy.jnmaker.util.ObjectRepository.getMainFrame ;

import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;

import javax.swing.JMenuItem ;
import javax.swing.JPopupMenu ;
import javax.swing.JTextPane ;

import com.sandy.jnmaker.ui.helper.EditMenu ;
import com.sandy.jnmaker.util.NoteType ;

public class MakeNotesPopupMenu extends JPopupMenu implements ActionListener {

    private static final long serialVersionUID = -7265818723778988508L ;

    private JTextPane rawTextArea  = null ;
    private String    selectedText = null ;
    
    private JMenuItem qaMI         = new JMenuItem() ;
    private JMenuItem fibMI        = new JMenuItem() ;
    private JMenuItem trueFalseMI  = new JMenuItem() ;
    private JMenuItem wmMI         = new JMenuItem() ;
    private JMenuItem spellbeeMI   = new JMenuItem() ;
    private JMenuItem definitionMI = new JMenuItem() ;
    private JMenuItem eventMI      = new JMenuItem() ;    
    
    public MakeNotesPopupMenu( JTextPane rawTextPane ) {
        
        super( "Make notes" ) ;
        this.rawTextArea = rawTextPane ;
        setUpUI() ;
    }
    
    private void setUpUI() {
        
        add( new EditMenu( rawTextArea ) ) ;
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
    }
    
    private JMenuItem prepareMenuItem( JMenuItem mi, String label, NoteType noteType ) {
        
        if( mi == null ) {
            mi = new JMenuItem() ;
        }
        
        mi.setText( label ) ;
        mi.addActionListener( this ) ;
        mi.setActionCommand( noteType.toString() ) ;
        return mi ;
    }

    @Override
    public void actionPerformed( ActionEvent e ) {
        
        String   actionCmd = e.getActionCommand() ;
        NoteType noteType  = NoteType.valueOf( actionCmd ) ;
        
        getMainFrame().createNote( this.selectedText, noteType ) ;
    }
    
    public void show( String selText, int x, int y ) {
        
        this.selectedText = ( selText != null ) ? selText.trim() : selText ;
        super.show( this.rawTextArea, x, y ) ;
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
}
