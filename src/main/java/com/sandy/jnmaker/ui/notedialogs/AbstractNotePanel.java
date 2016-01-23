package com.sandy.jnmaker.ui.notedialogs;

import java.awt.event.KeyAdapter ;
import java.awt.event.KeyEvent ;
import java.io.File ;
import java.io.IOException ;
import java.util.List ;

import javax.swing.JLabel ;
import javax.swing.JOptionPane ;
import javax.swing.JPanel ;
import javax.swing.JTextArea ;
import javax.swing.JTextField ;
import javax.swing.text.JTextComponent ;

import org.apache.commons.io.FileUtils ;
import org.apache.commons.lang.WordUtils ;
import org.json.simple.JSONObject ;
import org.json.simple.JSONValue ;

import com.sandy.common.util.StringUtil ;
import com.sandy.jnmaker.util.AppConfig ;
import com.sandy.jnmaker.util.ObjectRepository ;
import com.sandy.jnmaker.util.WordnicAdapter ;

public abstract class AbstractNotePanel extends JPanel {

    private static final long serialVersionUID = -2589969430412971534L ;
    
    protected NotesCreatorDialog parent = null ;
    
    public abstract String getFormattedNote() ;

    public void setParentDialog( NotesCreatorDialog parent ) {
        this.parent = parent ;
    }
    
    protected String escapeQuotes( String input ) {
        return input.replaceAll( "\\\"", "\\\\\"" ) ;
    }

    protected String formatText( String input, boolean escapeQuote ) {
        
        StringBuilder buffer = new StringBuilder() ;
        String[] lines = input.split( "\n" ) ;
        
        for( int i=0; i<lines.length; i++ ) {
            String line = lines[i] ;
            if( !StringUtil.isEmptyOrNull( line ) ) {
                if( escapeQuote ) {
                    line = escapeQuotes( line ) ;
                }
                line = WordUtils.wrap( line, 80, "\n", false ) ;
                buffer.append( line ) ;
                
                if( i < (lines.length - 1) ) {
                    buffer.append( "  \n" ) ;
                }
            }
            else {
                buffer.append( "\n" ) ;
            }
        }
        return buffer.toString() ;
    }
    
    protected String formatText( String input ) {
        return formatText( input, true ) ;
    }
    
    protected void showErrorMsg( String msg ) {
        JOptionPane.showMessageDialog( this, msg, "Input error", 
                                       JOptionPane.ERROR_MESSAGE ) ; 
    }

    protected void fetchAndPopulateMeaning( String word, JLabel msgLabel,
                                            JTextArea meaningTF,
                                            JTextField pronunciationTF ) {
        
        AppConfig appConfig = ObjectRepository.getAppConfig() ;
        File cachedFile = new File( appConfig.getJoveNotesMediaDir(), 
                                    word.toLowerCase() + ".json" ) ;
        
        if( cachedFile.exists() && cachedFile.length() > 0 ) {
            try {
                populateFromCachedFile( cachedFile, meaningTF, pronunciationTF ) ;
            }
            catch( IOException e ) {
                downloadAndPopulate( word, msgLabel, meaningTF, pronunciationTF ) ;
            }
        }
        else {
            downloadAndPopulate( word, msgLabel, meaningTF, pronunciationTF ) ;
        }
    }
    
    private void populateFromCachedFile( File cachedFile, JTextArea meaningTF,
                                         JTextField pronunciationTF ) 
        throws IOException {

        JSONObject obj = null ;
        obj = ( JSONObject )JSONValue.parse( FileUtils.readFileToString( cachedFile ) ) ;
        
        String pronunciation = obj.get( "pronunciation" ).toString() ;
        String meaning       = obj.get( "meaning" ).toString() ;
        
        pronunciationTF.setText( pronunciation ) ;
        meaningTF.setText( meaning ) ;
    }
    
    private void downloadAndPopulate( String word, JLabel msgLabel,
                                      JTextArea meaningTF,
                                      JTextField pronunciationTF ) {
        String pronunciation = null ;

        StringBuilder buffer = new StringBuilder() ;
        WordnicAdapter wordnic = ObjectRepository.getWordnicAdapter() ;
        
        try {
            msgLabel.setText( "Downloading meaning ..." ) ;
            meaningTF.setEnabled( false ) ;
            pronunciationTF.setEnabled( false ) ;
            
            pronunciation = wordnic.getPronounciation( word ) ;
            List<String> definitions = wordnic.getDefinitions( word.trim() ) ;
            
            for( int i = 0 ; i < definitions.size() ; i++ ) {
                
                buffer.append( i + 1 )
                      .append( " ) " )
                      .append( formatText( definitions.get( i ) ) ) ;

                if( i < ( definitions.size() - 1 ) ) {
                    buffer.append( "\n" ) ;
                }
            }
            
            pronunciationTF.setText( pronunciation ) ;
            meaningTF.setText( buffer.toString() ) ;
        }
        catch( Exception e1 ) {
            showErrorMsg( "Word meaning could not be downloaded.\n" + 
                          e1.getMessage() );
        }
        finally {
            msgLabel.setText( "" ) ;
            meaningTF.setEnabled( true ) ;
            pronunciationTF.setEnabled( true ) ;
        }
    }
    
    protected abstract void captureFocus() ;
    
    protected void bindOkPressEventCapture( JTextComponent textArea ) {
        
        textArea.addKeyListener( new KeyAdapter() {
            @Override public void keyPressed( KeyEvent e ) {
                if( e.getKeyCode()   == KeyEvent.VK_ENTER && 
                    e.getModifiers() == KeyEvent.CTRL_MASK ) {
                    parent.okPressed() ;
                }            
            }
        });
    }
}
