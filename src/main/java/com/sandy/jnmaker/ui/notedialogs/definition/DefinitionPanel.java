package com.sandy.jnmaker.ui.notedialogs.definition;

import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;
import java.awt.event.KeyAdapter ;
import java.awt.event.KeyEvent ;
import java.awt.event.MouseAdapter ;
import java.awt.event.MouseEvent ;

import javax.swing.JMenuItem ;
import javax.swing.JPopupMenu ;

import org.apache.commons.lang.WordUtils ;

import com.sandy.common.util.StringUtil ;
import com.sandy.jnmaker.ui.helper.PopupEditMenu ;
import com.sandy.jnmaker.ui.helper.UIUtil ;

public class DefinitionPanel extends DefinitionPanelUI 
    implements ActionListener {

    private static final long serialVersionUID = -270534844782994062L;
    
    private JPopupMenu popupMenu  = null ;
    private JMenuItem  markTermMI = null ;

    public DefinitionPanel( String selectedText ) {
        initComponents( selectedText ) ;
        initListeners() ;
    }
    
    private void initComponents( String selectedText ) {
        
        initPopupMenu() ;
        definitionTF.setText( selectedText ) ;
        UIUtil.associateEditMenu( termTF ) ;
    }
    
    private void initPopupMenu() {
        
        markTermMI = new JMenuItem( "Extract term" ) ;
        markTermMI.addActionListener( this ) ;
        
        popupMenu = new JPopupMenu() ;
        popupMenu.add( markTermMI ) ;
        popupMenu.add( new PopupEditMenu( popupMenu, definitionTF ) ) ;
    }
    
    private void initListeners() {
        
        bindOkPressEventCapture( termTF ) ;
        bindOkPressEventCapture( definitionTF ) ;
        
        definitionTF.addMouseListener( new MouseAdapter() {
            @Override 
            public void mouseClicked( MouseEvent e ) {
                if( e.getButton() == MouseEvent.BUTTON3 ) {
                    popupMenu.show( definitionTF, e.getX(), e.getY() ) ;
                }
            }
        } );
        
        definitionTF.addKeyListener( new KeyAdapter() {
            public void keyPressed( KeyEvent e ) {
                handleKeyShortcutPressed( e.getModifiers(), e.getKeyCode() ) ;
            }
        } ) ;
    }

    protected void captureFocus() {
        definitionTF.requestFocus() ;
    }

    @Override
    public String getFormattedNote() {
        
        if( StringUtil.isEmptyOrNull( definitionTF.getText() ) || 
            StringUtil.isEmptyOrNull( termTF.getText() ) ) {
            showErrorMsg( "Definition or term can't be empty" ) ;
            return null ;
        }
        
        StringBuilder buffer = new StringBuilder() ;
        buffer.append( "@definition \"" )
              .append( termTF.getText().trim() )
              .append( "\"\n" )
              .append( "\"" )
              .append( formatText( definitionTF.getText() ) )
              .append( "\"" ) ;
        return buffer.toString() ;
    }

    @Override
    public void actionPerformed( ActionEvent ae ) {
        
        if( ae.getSource() == markTermMI ) {
            extractTerm() ;
        }
    }

    private void handleKeyShortcutPressed( int mod, int code ) {
        
        if( mod == KeyEvent.CTRL_MASK ) {
            switch( code ) {
                case KeyEvent.VK_E:
                    extractTerm() ;
                    break ;
            }
        }
    }
    
    private void extractTerm() {
        
        String selText = definitionTF.getSelectedText().trim() ;
        if( StringUtil.isNotEmptyOrNull( selText ) ) {
            termTF.setText( WordUtils.capitalize( selText ) ) ;
        }
    }
}
