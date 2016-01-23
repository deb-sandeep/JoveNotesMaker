package com.sandy.jnmaker.ui.notedialogs.truefalse;

import javax.swing.event.ChangeEvent ;
import javax.swing.event.ChangeListener ;

import com.sandy.common.util.StringUtil ;
import com.sandy.jnmaker.ui.helper.UIUtil ;

public class TFPanel extends TFPanelUI {

    private static final long serialVersionUID = 3769022621138588835L ;
    
    private String savedJustification = null ;

    public TFPanel( String selectedText ) {
        
        if( !selectedText.endsWith( "." ) ) {
            selectedText += "." ;
        }
        
        savedJustification = selectedText ;
        initComponents( selectedText ) ;
        initListeners( selectedText ) ;
    }
    
    private void initComponents( String selectedText ) {
        
        stmtTextArea.setText( selectedText ) ;
        trueFalseCheckBox.setSelected( true ) ;
        justTextArea.setEnabled( false ) ;
        
        UIUtil.associateEditMenu( stmtTextArea ) ;
        UIUtil.associateEditMenu( justTextArea ) ;
    }
    
    private void initListeners( String selectedText ) {

        bindOkPressEventCapture( stmtTextArea ) ;
        bindOkPressEventCapture( justTextArea ) ;
        
        trueFalseCheckBox.addChangeListener( new ChangeListener() {
            @Override
            public void stateChanged( ChangeEvent e ) {
                if( trueFalseCheckBox.isSelected() ) {
                    if( StringUtil.isNotEmptyOrNull( justTextArea.getText() ) ) {
                        savedJustification = justTextArea.getText() ;
                    }
                    justTextArea.setText( "" ) ;
                    justTextArea.setEnabled( false ) ;
                }
                else {
                    justTextArea.setText( savedJustification ) ;
                    justTextArea.setEnabled( true ) ;
                }
            }
        } );
    }
    
    protected void captureFocus() {
        stmtTextArea.requestFocus() ;
    }

    @Override
    public String getFormattedNote() {
        StringBuilder buffer = new StringBuilder() ;
        
        buffer.append( "@true_false \"" )
              .append( formatText( stmtTextArea.getText() ) )
              .append( "\"\n" )
              .append( trueFalseCheckBox.isSelected() ) ;
        
        if( !trueFalseCheckBox.isSelected() ) {
            buffer.append( "\n" )
                  .append( "\"" )
                  .append( formatText( justTextArea.getText() ) )
                  .append( "\"" ) ;
        }
        
        return buffer.toString() ;
    }
}
