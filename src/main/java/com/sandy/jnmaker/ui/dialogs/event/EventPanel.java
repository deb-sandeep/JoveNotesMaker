package com.sandy.jnmaker.ui.dialogs.event ;

import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;
import java.awt.event.MouseAdapter ;
import java.awt.event.MouseEvent ;

import javax.swing.JMenuItem ;
import javax.swing.JPopupMenu ;

import org.apache.commons.lang.WordUtils ;

import com.sandy.common.util.StringUtil ;

public class EventPanel extends EventPanelUI 
    implements ActionListener {

    private static final long serialVersionUID = -270534844782994062L;
    
    private JPopupMenu popupMenu  = null ;
    private JMenuItem  markTimeMI = null ;

    public EventPanel( String selectedText ) {
        initComponents( selectedText ) ;
        initListeners() ;
    }
    
    private void initComponents( String selectedText ) {
        
        initPopupMenu() ;
        
        String eventTxt =  selectedText ;
        if( !eventTxt.endsWith( "." ) ) {
            eventTxt += "." ;
        }
        eventTF.setText( eventTxt ) ;
    }
    
    private void initPopupMenu() {
        
        markTimeMI = new JMenuItem( "Extract time" ) ;
        markTimeMI.addActionListener( this ) ;
        
        popupMenu = new JPopupMenu() ;
        popupMenu.add( markTimeMI ) ;
    }
    
    private void initListeners() {
        
        bindOkPressEventCapture( timeTF ) ;
        bindOkPressEventCapture( eventTF ) ;
        
        eventTF.addMouseListener( new MouseAdapter() {
            @Override 
            public void mouseClicked( MouseEvent e ) {
                if( e.getButton() == MouseEvent.BUTTON3 ) {
                    popupMenu.show( eventTF, e.getX(), e.getY() ) ;
                }
            }
        } );
    }

    @Override
    public String getFormattedNote() {
        
        if( StringUtil.isEmptyOrNull( eventTF.getText() ) || 
            StringUtil.isEmptyOrNull( timeTF.getText() ) ) {
            showErrorMsg( "Definition or term can't be empty" ) ;
            return null ;
        }
        
        StringBuilder buffer = new StringBuilder() ;
        buffer.append( "@event \"" )
              .append( formatText( eventTF.getText().trim() ) )
              .append( "\"\n" )
              .append( "\"" )
              .append( timeTF.getText() )
              .append( "\"" ) ;
        return buffer.toString() ;
    }

    @Override
    public void actionPerformed( ActionEvent ae ) {
        
        if( ae.getSource() == markTimeMI ) {
            String selText = eventTF.getSelectedText().trim() ;
            if( StringUtil.isNotEmptyOrNull( selText ) ) {
                timeTF.setText( WordUtils.capitalize( selText ) ) ;
            }
        }
    }
}
