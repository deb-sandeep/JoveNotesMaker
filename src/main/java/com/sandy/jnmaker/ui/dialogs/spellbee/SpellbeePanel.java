package com.sandy.jnmaker.ui.dialogs.spellbee;

import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;
import java.io.File ;
import java.io.FileInputStream ;
import java.io.IOException ;
import java.util.LinkedHashMap ;
import java.util.Map ;

import org.apache.commons.io.FileUtils ;
import org.apache.commons.lang.WordUtils ;
import org.apache.log4j.Logger ;
import org.json.simple.JSONValue ;

import com.sandy.common.util.StringUtil ;
import com.sandy.jnmaker.ui.helper.UIUtil ;
import com.sandy.jnmaker.util.ObjectRepository ;

import javazoom.jl.player.Player ;

public class SpellbeePanel extends SpellbeePanelUI implements ActionListener {

    private static final long serialVersionUID = 1L ;
    private static final Logger logger = Logger.getLogger( SpellbeePanel.class ) ;
    
    private File soundClip = null ;

    public SpellbeePanel( String selectedText ) {
        
        initComponents( selectedText ) ;
        initListeners() ;
        new Thread( new Runnable() {
            @Override public void run() {
                downloadContent() ;
            }
        } ).start() ;
    }
    
    private void initComponents( String selectedText ) {
        
        wordTF.setText( WordUtils.capitalize( selectedText ) ) ;
        playBtn.setEnabled( false ) ;
        pronunciationTF.setEnabled( false ) ;
        
        UIUtil.associateEditMenu( wordTF ) ;
        UIUtil.associateEditMenu( meaningTF ) ;
    }
    
    private void initListeners() {
        
        bindOkPressEventCapture( meaningTF ) ;
        bindOkPressEventCapture( wordTF ) ;
        downloadBtn.addActionListener( this ) ;
        playBtn.addActionListener( this ) ;
    }
    
    @Override
    public String getFormattedNote() {
        
        String word = wordTF.getText().trim() ;
        
        if( StringUtil.isEmptyOrNull( word ) ) {
            showErrorMsg( "Meaning can't be empty" ) ;
            return null ;
        }
        
        StringBuilder buffer = new StringBuilder() ;
        
        buffer.append( "@spellbee \"" )
              .append( word )
              .append( "\"" ) ;
        
        try {
            if( ObjectRepository.getAppConfig().getJoveNotesMediaDir() != null ) {
                saveDownloadsInMediaDir( word ) ;
            }
        }
        catch( IOException e ) {
            logger.error( "Could not save to media dir.", e ) ;
            showErrorMsg( "Could not save meaning in media dir." ) ;
            return null ;
        }
        return buffer.toString() ;
    }
    
    @Override
    public void actionPerformed( ActionEvent e ) {
        
        if( e.getSource() == downloadBtn ) {
            if( StringUtil.isEmptyOrNull( wordTF.getText() ) ) {
                showErrorMsg( "Word can't be empty." ) ;
            }
            else {
                downloadContent() ;
            }
        }
        else if( e.getSource() == playBtn ) {
            if( soundClip != null && soundClip.exists() ) {
                playSound() ;
            }
        }
    }
    
    private void downloadContent() {
        
        fetchAndPopulateMeaning( wordTF.getText().trim(), 
                                 msgLabel, meaningTF, pronunciationTF ) ;
        downloadSoundClip() ;
    }
    
    private void downloadSoundClip() {
        
        SoundClipDownloader downloader = new SoundClipDownloader() ;
        try {
            msgLabel.setText( "Downloading sound clip..." ) ;
            soundClip = downloader.downloadSoundClip( wordTF.getText().trim() ) ;
            playBtn.setEnabled( true ) ;
            playSound() ;
        }
        catch( Exception e ) {
            logger.error( "Error downloading clip.", e ) ;
            msgLabel.setText( "Error downoading sound clip." ) ;
            try { Thread.sleep( 500 ) ; } catch( Exception e1 ){}
            playBtn.setEnabled( false ) ;
        }
        finally {
            msgLabel.setText( "" ) ;
        }
    }
    
    private void playSound() {
        try {
            Player player = new Player( new FileInputStream( soundClip ) ) ;
            player.play() ;
        }
        catch( Exception e ) {
            logger.error( "Could not play sound clip.", e ) ;
        }
    }
    
    private void saveDownloadsInMediaDir( String word ) 
        throws IOException {
        
        File mediaDir = ObjectRepository.getAppConfig().getJoveNotesMediaDir() ;
        File clipFile = new File( mediaDir, word.toLowerCase() + ".mp3" ) ;
        File descFile = new File( mediaDir, word.toLowerCase() + ".descr" ) ;
        File prnFile  = new File( mediaDir, word.toLowerCase() + ".pronunciation" ) ;
        
        FileUtils.writeStringToFile( prnFile, pronunciationTF.getText().trim() ) ;
        FileUtils.writeStringToFile( descFile, meaningTF.getText().trim() ) ;
        FileUtils.copyFile( soundClip, clipFile ) ;
        
        saveDownloadedContentAsJSON( word ) ;
    }
    
    private void saveDownloadedContentAsJSON( String word ) 
        throws IOException {
        
        File mediaDir = ObjectRepository.getAppConfig().getJoveNotesMediaDir() ;
        File jsonFile = new File( mediaDir, word.toLowerCase() + ".json" ) ;
        File descFile = new File( mediaDir, word.toLowerCase() + ".descr" ) ;
        File pronFile = new File( mediaDir, word.toLowerCase() + ".pronunciation" ) ;

        FileUtils.writeStringToFile( jsonFile, getJSONConverted() ) ;
        FileUtils.writeStringToFile( pronFile, pronunciationTF.getText().trim() );
        FileUtils.writeStringToFile( descFile, meaningTF.getText().trim() ) ;
    }
    
    private String getJSONConverted() {
        
        Map<String, Object> map = new LinkedHashMap<String, Object>() ;
        
        map.put( "pronunciation", pronunciationTF.getText().trim() ) ;
        map.put( "meaning",       meaningTF.getText().trim() ) ;
        
        return JSONValue.toJSONString( map ) ;
    }
}
