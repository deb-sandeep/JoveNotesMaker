package com.sandy.jnmaker.ui;

import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;

import javax.swing.JMenuItem ;
import javax.swing.JPopupMenu ;
import javax.swing.JTextArea ;

import com.sandy.jnmaker.NoteType ;

import static com.sandy.jnmaker.util.ObjectRepository.* ;

public class MakeNotesPopupMenu extends JPopupMenu implements ActionListener {

    private static final long serialVersionUID = -7265818723778988508L ;

    private JTextArea rawTextArea  = null ;
    private String    selectedText = null ;
    
    public MakeNotesPopupMenu( JTextArea rawTextArea ) {
        
        super( "Make notes" ) ;
        this.rawTextArea = rawTextArea ;
        setUpUI() ;
    }
    
    private void setUpUI() {
        
        add( createMenuItem( "@qa",         NoteType.QA ) ) ;
        add( createMenuItem( "@fib",        NoteType.FIB ) ) ;
        add( createMenuItem( "@true_false", NoteType.TRUE_FALSE ) ) ;
    }
    
    private JMenuItem createMenuItem( String label, NoteType noteType ) {
        
        JMenuItem menuItem = new JMenuItem( label ) ;
        menuItem.addActionListener( this ) ;
        menuItem.setActionCommand( noteType.toString() ) ;
        return menuItem ;
    }

    @Override
    public void actionPerformed( ActionEvent e ) {
        
        String   actionCmd = e.getActionCommand() ;
        NoteType noteType  = NoteType.valueOf( actionCmd ) ;
        
        getMainFrame().createNote( this.selectedText, noteType ) ;
    }
    
    public void show( String selectedText, int x, int y ) {
        
        this.selectedText = selectedText ;
        super.show( this.rawTextArea, x, y ) ;
    }
}
