package com.sandy.jnmaker.ui.dialogs.wm;

import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;

import org.apache.commons.lang.WordUtils ;

import com.sandy.common.util.StringUtil ;
import com.sandy.jnmaker.ui.helper.UIUtil ;

public class WMPanel extends WMPanelUI implements ActionListener {

    private static final long serialVersionUID = -739754519735814624L;

    public WMPanel( String selectedText ) {
        initComponents( selectedText ) ;
        initListeners() ;
        new Thread( new Runnable() {
            @Override
            public void run() {
                fetchAndPopulateMeaning( wordTF.getText(), msgLabel, meaningTF,
                                         pronunciationTF ) ;
            }
        } ).start() ;
    }
    
    private void initComponents( String selectedText ) {
        wordTF.setText( WordUtils.capitalize( selectedText ) ) ;
        UIUtil.associateEditMenu( meaningTF ) ;
        UIUtil.associateEditMenu( wordTF ) ;
    }
    
    private void initListeners() {
        bindOkPressEventCapture( meaningTF ) ;
        bindOkPressEventCapture( wordTF ) ;
        getMeaningBtn.addActionListener( this ) ;
    }
    
    @Override
    public String getFormattedNote() {
        if( StringUtil.isEmptyOrNull( meaningTF.getText() ) ) {
            showErrorMsg( "Meaning can't be empty" ) ;
            return null ;
        }
        
        StringBuilder buffer = new StringBuilder() ;
        
        buffer.append( "@wm \"" )
              .append( wordTF.getText() )
              .append( "\"\n" ) ;
        
        buffer.append( "\"" )
              .append( formatText( meaningTF.getText() ) )
              .append( "\"" ) ;
        
        return buffer.toString() ;
    }

    @Override
    public void actionPerformed( ActionEvent e ) {
        if( StringUtil.isEmptyOrNull( wordTF.getText() ) ) {
            showErrorMsg( "Word can't be empty." ) ;
        }
        fetchAndPopulateMeaning( wordTF.getText(), msgLabel, meaningTF,
                                 pronunciationTF ) ;
    }
}
