package com.sandy.jnmaker.ui.notedialogs.rawnotes ;

import java.io.BufferedReader ;
import java.io.IOException ;
import java.io.StringReader ;
import java.util.ArrayList ;
import java.util.List ;

import org.apache.log4j.Logger ;

import com.sandy.common.util.StringUtil ;
import com.sandy.jnmaker.ui.helper.UIUtil ;

public class MultiChoicePanel extends RawNotesPanelUI {

    private static final long serialVersionUID = 1L ;
    private static final Logger logger = Logger.getLogger( MultiChoicePanel.class ) ;
    
    private String caption = "" ;
    private List<String[]> candidates = new ArrayList<>() ;
    
    public MultiChoicePanel( String selectedText ) {
        
        UIUtil.associateEditMenu( rawNotesTF ) ;
        setUpListeners() ;
        parseMatchingInput( selectedText ) ;
        rawNotesTF.setText( constructMultiChoiceNote() ) ;
    }
    
    private void setUpListeners() {
        super.bindOkPressEventCapture( this.rawNotesTF ) ;
    }
    
    protected void captureFocus() {
        rawNotesTF.requestFocus() ;
    }

    @Override
    public String getFormattedNote() {
        
        return rawNotesTF.getText().trim() ;
    }
    
    private void parseMatchingInput( String text ) {
        
        String line = null ;
        BufferedReader br = new BufferedReader( new StringReader( text ) ) ;
        boolean isFirstLine = true ;
        
        try {
            while( ( line = br.readLine() ) != null ) {
                line = line.trim() ;
                
                if( StringUtil.isEmptyOrNull( line ) ) continue ;
                
                if( isFirstLine ) {
                    caption = line ;
                    isFirstLine = false ;
                    continue ;
                }
                
                if( line.startsWith( "*" ) ) {
                    line = line.substring( 1 ).trim() ;
                }
                
                boolean isCorrect = line.endsWith( "$" ) ;
                if( isCorrect ) {
                    line = line.substring( 0, line.length()-1 ) ;
                    line = line.trim() ;
                }
                
                String[] candidate = new String[2] ;
                
                candidate[0] = line ;
                candidate[1] = isCorrect ? " correct" : "" ;
                
                candidates.add( candidate ) ;
            }
        }
        catch( IOException e ) {
            showErrorMsg( "Exception while processing" ) ;
            logger.error( "Unanticipated error.", e ) ;
        }
    }
    
    private String constructMultiChoiceNote() {
        
        StringBuilder sb = new StringBuilder() ;
        
        sb.append( "@multi_choice " )
          .append( "\"" + caption + "\" {\n" )
          .append( "\t@options {\n" ) ;
        
        for( String[] candidate : candidates ) {
            sb.append( "\t   \"" + candidate[0] + "\"" + candidate[1] + ",\n" ) ; 
        }
        
        sb.deleteCharAt( sb.length()-2 ) ;

        sb.append( "\t}\n" )
          .append( "}" ) ;
        
        return sb.toString() ;
    }
}
