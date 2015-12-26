package com.sandy.jnmaker.ui.helper;

import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;

import javax.swing.JMenu ;
import javax.swing.JMenuItem ;
import javax.swing.text.DefaultEditorKit ;
import javax.swing.text.DefaultEditorKit.CopyAction ;
import javax.swing.text.DefaultEditorKit.CutAction ;
import javax.swing.text.DefaultEditorKit.PasteAction ;
import javax.swing.text.JTextComponent ;
import javax.swing.undo.UndoManager ;

import org.apache.log4j.Logger ;

public class EditMenu extends JMenu implements ActionListener {

    private static final long serialVersionUID = 1L ;
    private static final Logger logger = Logger.getLogger( EditMenu.class ) ;
    
    private class SelectedContent {
        
        String content  = null ;
        int    startPos = -1 ;
        int    endPos   = -1 ;
        int    length   = -1 ;
        
       SelectedContent() {
            if( editor.getSelectedText() != null ) {
                content  = editor.getSelectedText() ;
                startPos = editor.getSelectionStart() ;
                endPos   = editor.getSelectionEnd() ;
                length   = endPos - startPos ;
            }
        }
    }
    
    private JTextComponent editor ;
    private UndoManager    undoManager ;
    
    private JMenuItem copyMI  = new JMenuItem() ;
    private JMenuItem cutMI   = new JMenuItem() ;
    private JMenuItem pasteMI = new JMenuItem() ;
    
    private JMenuItem undoMI = new JMenuItem() ;
    private JMenuItem redoMI = new JMenuItem() ;
    
    private JMenuItem boldMI    = new JMenuItem() ;
    private JMenuItem italicsMI = new JMenuItem() ;
    
    public EditMenu( JTextComponent textComponent ) {
        super( "Edit" ) ;
        this.editor = textComponent ;
        this.undoManager = new UndoManager() ;
        this.editor.getDocument().addUndoableEditListener( undoManager ) ;
        setUpUI() ;
    }
    
    private void setUpUI() {
        
        add( prepareMenuItem( copyMI,  "Copy"  ) ) ;
        add( prepareMenuItem( cutMI,   "Cut"   ) ) ;
        add( prepareMenuItem( pasteMI, "Paste" ) ) ;
        addSeparator() ;
        add( prepareMenuItem( undoMI, "Undo" ) ) ;
        add( prepareMenuItem( redoMI, "Redo" ) ) ;
        addSeparator() ;
        add( prepareMenuItem( boldMI,    "Bold"    ) ) ;
        add( prepareMenuItem( italicsMI, "Italics" ) ) ;
    }
    
    private JMenuItem prepareMenuItem( JMenuItem mi, String label ) {
        
        mi.setText( label ) ;
        mi.addActionListener( this ) ;
        return mi ;
    }

    @Override
    public void actionPerformed( ActionEvent e ) {
        
        SelectedContent sel = new SelectedContent() ;
        Object src = e.getSource() ;
        
        try {
            if( src == copyMI ) {
                CopyAction action = DefaultEditorKit.CopyAction.class.newInstance() ;
                action.actionPerformed( e ) ;
            }
            else if( src == cutMI ) {
                CutAction action = DefaultEditorKit.CutAction.class.newInstance() ;
                action.actionPerformed( e ) ;
            }
            else if( src == pasteMI ) {
                PasteAction action = DefaultEditorKit.PasteAction.class.newInstance() ;
                action.actionPerformed( e ) ;
            }
            else if( src == undoMI ) {
                if( undoManager.canUndo() ) {
                    undoManager.undo() ;
                }
            }
            else if( src == redoMI ) {
                if( undoManager.canRedo() ) {
                    undoManager.redo() ;
                }
            }
            else if( src == boldMI ) {
                doBold( sel ) ;
            }
            else if( src == italicsMI ) {
                doItalics( sel ) ;
            }
        }
        catch( Exception e1 ) {
            logger.error( "Error performing edit action.", e1 ) ;
        }
    }

    private void doBold( SelectedContent sel ) {
    }

    private void doItalics( SelectedContent sel ) {
    }
}
