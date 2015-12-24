package com.sandy.jnmaker.ui.dialogs.wm;

import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;
import java.util.List ;

import org.apache.commons.lang.WordUtils ;

import com.sandy.common.util.StringUtil ;
import com.sandy.jnmaker.util.ObjectRepository ;
import com.sandy.jnmaker.util.WordnicAdapter ;

public class WMPanel extends WMPanelUI implements ActionListener {

    private static final long serialVersionUID = -739754519735814624L;

    public WMPanel( String selectedText ) {
        initComponents( selectedText ) ;
        initListeners() ;
        new Thread( new Runnable() {
            @Override
            public void run() {
                fetchAndPopulateMeaning() ;
            }
        } ).start() ;
    }
    
    private void initComponents( String selectedText ) {
        wordTF.setText( WordUtils.capitalize( selectedText ) ) ;
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
        fetchAndPopulateMeaning() ;
    }
    
    private void fetchAndPopulateMeaning() {
        
        String word          = wordTF.getText().trim() ;
        String pronunciation = null ;
        
        StringBuilder  buffer         = new StringBuilder() ;
        WordnicAdapter wordnicAdapter = ObjectRepository.getWordnicAdapter() ;
        
        try {
            meaningLabel.setText( "Meaning (downloading...)" ) ;
            meaningTF.setEnabled( false ) ;
            pronunciation = wordnicAdapter.getPronounciation( word ) ;
            buffer.append( "Pronunciation : " )
                  .append( pronunciation )
                  .append( "\n\n" ) ;
            
            List<String> definitions = wordnicAdapter.getDefinitions( word ) ;
            for( int i=0; i<definitions.size(); i++ ) {
                buffer.append( i+1 ).append(  " ) " )
                      .append( formatText( definitions.get( i ) ) ) ;
                
                if( i < ( definitions.size()-1) ) {
                    buffer.append( "\n" ) ;
                }
            }
            
            meaningTF.append( buffer.toString() ) ;
        }
        catch( Exception e1 ) {
            showErrorMsg( "Word meaning could not be downloaded.\n" +  
                          e1.getMessage() ) ;
        }
        finally {
            meaningLabel.setText( "Meaning" ) ;
            meaningTF.setEnabled( true ) ;
        }
    }
}
