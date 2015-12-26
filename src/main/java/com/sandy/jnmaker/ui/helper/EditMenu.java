package com.sandy.jnmaker.ui.helper;

import java.awt.Toolkit ;
import java.awt.datatransfer.Clipboard ;
import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;
import java.awt.event.KeyAdapter ;
import java.awt.event.KeyEvent ;

import javax.swing.JMenu ;
import javax.swing.JMenuItem ;
import javax.swing.JPopupMenu ;
import javax.swing.event.PopupMenuEvent ;
import javax.swing.event.PopupMenuListener ;
import javax.swing.text.DefaultEditorKit ;
import javax.swing.text.DefaultEditorKit.CopyAction ;
import javax.swing.text.DefaultEditorKit.CutAction ;
import javax.swing.text.DefaultEditorKit.PasteAction ;
import javax.swing.text.JTextComponent ;
import javax.swing.undo.UndoManager ;

import org.apache.log4j.Logger ;

import com.sandy.common.util.StringUtil ;

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
    
    private class ParentMenuListener implements PopupMenuListener {
        
        @Override
        public void popupMenuWillBecomeVisible( PopupMenuEvent e ) {
            undoMI.setEnabled( undoManager.canUndo() ) ;
            redoMI.setEnabled( undoManager.canRedo() ) ;
            cutMI .setEnabled( StringUtil.isNotEmptyOrNull( editor.getSelectedText() ) ) ;
            copyMI.setEnabled( StringUtil.isNotEmptyOrNull( editor.getSelectedText() ) ) ;
            
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard() ;
            pasteMI.setEnabled( clipboard.getContents( null ) != null ) ;
        }
        
        @Override
        public void popupMenuWillBecomeInvisible( PopupMenuEvent e ) {}
        
        @Override
        public void popupMenuCanceled( PopupMenuEvent e ) {}
    }
    
    private JTextComponent editor ;
    private UndoManager    undoManager ;
    private JPopupMenu     popupMenu ;
    
    private JMenuItem copyMI  = new JMenuItem() ;
    private JMenuItem cutMI   = new JMenuItem() ;
    private JMenuItem pasteMI = new JMenuItem() ;
    
    private JMenuItem undoMI = new JMenuItem() ;
    private JMenuItem redoMI = new JMenuItem() ;
    
    private JMenuItem boldMI    = new JMenuItem() ;
    private JMenuItem italicsMI = new JMenuItem() ;
    
    public EditMenu( JPopupMenu popupMenu, JTextComponent textComponent ) {
        
        super( "Edit" ) ;
        this.popupMenu = popupMenu ;
        this.editor = textComponent ;
        this.undoManager = new UndoManager() ;
        this.editor.getDocument().addUndoableEditListener( undoManager ) ;
        setUpUI() ;
        setUpListeners() ;
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
    
    private void setUpListeners() {
        popupMenu.addPopupMenuListener( new ParentMenuListener() ) ;
        editor.addKeyListener( new KeyAdapter() {
            @Override public void keyPressed( KeyEvent e ) {
                
                int keyCode   = e.getKeyCode() ;
                int modifiers = e.getModifiers() ;
                
                if( modifiers == KeyEvent.CTRL_MASK ) { 
                    SelectedContent sel = new SelectedContent() ;
                    
                    try {
                        if     ( keyCode == KeyEvent.VK_Z ) { doUndo() ; }
                        else if( keyCode == KeyEvent.VK_Y ) { doRedo() ; }
                        else if( keyCode == KeyEvent.VK_B ) { doBold( sel ) ; }
                        else if( keyCode == KeyEvent.VK_I ) { doItalics( sel ) ; }
                    }
                    catch( Exception e1 ) {
                        logger.error( "Error performing edit action.", e1 ) ;
                    }
                }
            }
        } ) ;
    }
    
    @Override
    public void actionPerformed( ActionEvent e ) {
        
        SelectedContent sel = new SelectedContent() ;
        Object src = e.getSource() ;
        
        try {
            if     ( src == copyMI    ) { doCopy( e ) ;      }
            else if( src == cutMI     ) { doCut( e ) ;       }
            else if( src == pasteMI   ) { doPaste( e ) ;     }
            else if( src == undoMI    ) { doUndo() ;         }
            else if( src == redoMI    ) { doRedo() ;         }
            else if( src == boldMI    ) { doBold( sel ) ;    }
            else if( src == italicsMI ) { doItalics( sel ) ; }
        }
        catch( Exception e1 ) {
            logger.error( "Error performing edit action.", e1 ) ;
        }
    }
    
    private void doCopy( ActionEvent e ) throws Exception {
        CopyAction action = DefaultEditorKit.CopyAction.class.newInstance() ;
        action.actionPerformed( e ) ;
    }
    
    private void doCut( ActionEvent e ) throws Exception  {
        CutAction action = DefaultEditorKit.CutAction.class.newInstance() ;
        action.actionPerformed( e ) ;
    }
    
    private void doPaste( ActionEvent e ) throws Exception  {
        PasteAction action = DefaultEditorKit.PasteAction.class.newInstance() ;
        action.actionPerformed( e ) ;
    }
    
    public void doUndo() {
        if( undoManager.canUndo() ) {
            undoManager.undo() ;
        }
    }
    
    public void doRedo() {
        if( undoManager.canRedo() ) {
            undoManager.redo() ;
        }
    }

    private void doBold( SelectedContent sel ) throws Exception {
        if( StringUtil.isNotEmptyOrNull( sel.content ) ) {
            replaceContent( sel, "**" + sel.content + "**" ) ;
        }
    }

    private void doItalics( SelectedContent sel ) throws Exception  {
        if( StringUtil.isNotEmptyOrNull( sel.content ) ) {
            replaceContent( sel, "_" + sel.content + "_" ) ;
        }
    }
    
    private void replaceContent( SelectedContent sel, String replacementTxt ) 
        throws Exception {
        
        editor.getDocument().remove( sel.startPos, sel.length ) ;
        editor.getDocument().insertString( sel.startPos, replacementTxt, null ) ;
    }
    
    public void disengageUndoManager() {
        this.editor.getDocument().removeUndoableEditListener( undoManager ) ;
    }
    
    public void reengageUndoManager() {
        this.editor.getDocument().addUndoableEditListener( undoManager ) ;
    }
}
