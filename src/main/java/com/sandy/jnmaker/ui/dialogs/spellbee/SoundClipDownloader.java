package com.sandy.jnmaker.ui.dialogs.spellbee;

import java.io.File ;
import java.net.URLEncoder ;

import org.apache.log4j.Logger ;

import com.sandy.common.net.NetworkResourceDownloader ;
import com.sandy.common.util.WorkspaceManager ;
import com.sandy.jnmaker.util.ObjectRepository ;

public class SoundClipDownloader {

    private static final Logger logger = Logger.getLogger( SoundClipDownloader.class ) ;
    
    private static String GOOGLE_CLIP_URL_TEMPLATE = 
            "https://ssl.gstatic.com/dictionary/static/sounds/de/0/{{word}}.mp3" ;
    private static String DICTCOM_CLIP_URL_TEMPLATE = 
            "http://dictionary.reference.com/browse/{{word}}?s=t" ;
      
    private File mediaDir = null ;
    
    public File getMediaDir() {
        return mediaDir ;
    }

    public void setMediaDir( File mediaDir ) {
        this.mediaDir = mediaDir ;
    }

    public File downloadSoundClip( String word ) throws Exception {
        
        logger.info( "\tDownloading sound clip for word - " + word ) ;
        
        WorkspaceManager wkspMgr = ObjectRepository.getWkspManager() ;
        File tempDir = wkspMgr.getTempDir() ;
        
        File clipFile = new File( tempDir, word.toLowerCase() + ".mp3" ) ;
        
        try {
            if( !clipFile.exists() ) {
                logger.info( "\t\tDownloading sound clip." ) ;
                if( !downloadClipFromGoogle( word, clipFile ) ) {
                    if( !downloadClipFromDictionaryDotCom( word, clipFile ) ) {
                        throw new Exception( "Could not download sound clip." ) ;
                    }
                }
            }
        } 
        catch( Exception e ){
            logger.error( "Could not process spellbee command.", e ) ;
            throw e ;
        }
        
        return clipFile ;
    }
    
    private boolean downloadClipFromGoogle( String word, File outputFile ) 
            throws Exception {
            
        logger.debug( "\t\t\tDownloading clip from google.com" ) ;
        
        boolean result = false ;
        NetworkResourceDownloader downloader = null ;
        
        String url = GOOGLE_CLIP_URL_TEMPLATE.replace( 
                        "{{word}}", 
                        URLEncoder.encode( word.toLowerCase(), "UTF-8" ) ) ;
        
        downloader = new NetworkResourceDownloader( url ) ;
        if( downloader.execute() == 200 ) {
            downloader.saveResponseToFile( outputFile ) ;
            result = true ;
        }
        else {
            String msg = "Could not download sound clip from Google. msg=" + 
                         downloader.getStatusCode() + downloader.getReasonPhrase() ;
            logger.info( "\t\t\t" + msg ) ;
        }
        return result ;
    }
        
    private boolean downloadClipFromDictionaryDotCom( String word, File outputFile )
        throws Exception {
        
        logger.debug( "\t\t\tDownloading clip from dictionary.com" ) ;
        
        boolean result = false ;
        NetworkResourceDownloader downloader = null ;
            
        String url = DICTCOM_CLIP_URL_TEMPLATE.replace( 
                        "{{word}}", 
                        URLEncoder.encode( word, "UTF-8" ) ) ;
        
        downloader = new NetworkResourceDownloader( url ) ;
        if( downloader.execute() == 200 ) {
            
            String content = downloader.getResponseAsString() ;
            int endIndex = content.indexOf( ".mp3" ) ;
            if( endIndex != -1 ) {
                int startIndex = content.lastIndexOf( '"', endIndex ) ;
                String soundURL = content.substring( startIndex+1, endIndex+4 ) ;
                
                downloader = new NetworkResourceDownloader( soundURL ) ;
                downloader.execute() ;
                downloader.saveResponseToFile( outputFile ) ;
                result = true ;
            }
        }
        else {
            String msg = "Could not download sound clip from Dictionary.com. msg=" + 
                         downloader.getStatusCode() + downloader.getReasonPhrase() ;
            logger.info( "\t\t\t" + msg ) ;
        }
        return result ;
    }
}
