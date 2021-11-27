package com.sandy.jnmaker.ui.notedialogs.rawnotes ;

import java.io.BufferedReader ;
import java.io.IOException ;
import java.io.StringReader ;
import java.util.ArrayList ;
import java.util.List ;

import org.apache.commons.lang.StringUtils ;
import org.apache.log4j.Logger ;

import com.sandy.common.util.StringUtil ;
import com.sandy.jnmaker.ui.helper.UIUtil ;

public class MatchingPanel extends RawNotesPanelUI {

    private static final long serialVersionUID = 1L ;
    private static final Logger logger = Logger.getLogger( MatchingPanel.class ) ;
    
    private String caption = "Match the following" ;
    private List<String[]> candidates = new ArrayList<>() ;
    private int maxLeftLen = 0 ;
    
    public MatchingPanel( String selectedText ) {
        
        UIUtil.associateEditMenu( rawNotesTF ) ;
        setUpListeners() ;
        parseMatchingInput( selectedText ) ;
        rawNotesTF.setText( constructMatchingNote() ) ;
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
                    if( !line.contains( "=" ) ) {
                        caption = line ;
                        isFirstLine = false ;
                        continue ;
                    }
                }
                
                if( line.contains( "=" ) ) {
                    String[] parts = line.split( "=" ) ;
                    String[] candidate = new String[2] ;
                    
                    candidate[0] = parts[0].trim() ;
                    candidate[1] = parts[1].trim() ;
                    
                    maxLeftLen = candidate[0].length() > maxLeftLen ? 
                                 candidate[0].length() : maxLeftLen ;
                    
                    candidates.add( candidate ) ;
                }
            }
        }
        catch( IOException e ) {
            showErrorMsg( "Exception while processing" ) ;
            logger.error( "Unanticipated error.", e ) ;
        }
    }
    
    private String constructMatchingNote() {
        
        StringBuilder sb = new StringBuilder() ;
        
        sb.append( "@match " )
          .append( "\"" + caption + "\" {\n\n" ) ;
        
        for( String[] candidate : candidates ) {
            sb.append( "    " + getLeftCandidateStr( candidate[0] ) + 
                       " = \"" + candidate[1] + "\"\n" ) ;
        }

        sb.append( "\n" )
          .append( "    @mcq_config {\n" )
          .append( "        @forwardCaption \"Match the following\"\n" )
          .append( "        @reverseCaption \"Match the following\"\n" )
          .append( "        @numOptionsToShow 4\n" )
          .append( "        @numOptionsPerRow 2\n" )
          .append( "    }\n" )
          .append( "}" ) ;
        
        return sb.toString() ;
    }
    
    private String getLeftCandidateStr( String candidate ) {
        String str = "\"" + candidate + "\"" ;
        return StringUtils.rightPad( str, maxLeftLen+2 ) ;
    }
}
