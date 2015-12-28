package com.sandy.jnmaker.ui.dialogs.comment ;

import java.awt.event.KeyAdapter ;
import java.awt.event.KeyEvent ;
import java.io.BufferedReader ;
import java.io.IOException ;
import java.io.StringReader ;

import org.apache.log4j.Logger ;

import com.sandy.jnmaker.ui.helper.UIUtil ;

public class CommentPanel extends CommentPanelUI {

    private static final long serialVersionUID = 1L ;
    private static final Logger logger = Logger.getLogger( CommentPanel.class ) ;
    
    public CommentPanel( String selectedText ) {
        
        UIUtil.associateEditMenu( commentTF ) ;
        setUpListeners() ;
        commentTF.setText( selectedText ) ;
    }
    
    private void setUpListeners() {
        
        commentTF.addKeyListener( new KeyAdapter() {
            public void keyPressed( KeyEvent e ) {
                if( e.getKeyCode()   == KeyEvent.VK_ENTER && 
                    e.getModifiers() == KeyEvent.CTRL_MASK ) {
                    parent.okPressed() ;
                }
            }       
        } ) ; 
    }

    @Override
    public String getFormattedNote() {
        
        String        line    = null ;
        String        comment = commentTF.getText().trim() ;
        StringBuilder buffer  = new StringBuilder() ;
        
        comment = formatText( comment ) ;
        
        BufferedReader br = new BufferedReader( new StringReader( comment ) ) ;
        try {
            while( ( line = br.readLine() ) != null ) {
                buffer.append( "// " ).append( line ).append( "\n" ) ;
            }
        }
        catch( IOException e ) {
            showErrorMsg( "Exception while processing" ) ;
            logger.error( "Unanticipated error.", e ) ;
            return null ;
        }
        
        return buffer.toString().trim() ;
    }
}
