package com.sandy.jnmaker.lucene.indexer ;

import static com.sandy.jnmaker.lucene.indexer.Fields.CHAPTER_ID ;
import static com.sandy.jnmaker.lucene.indexer.Fields.CHAPTER_NAME ;
import static com.sandy.jnmaker.lucene.indexer.Fields.CHAPTER_NUM ;
import static com.sandy.jnmaker.lucene.indexer.Fields.MEDIA_PATHS ;
import static com.sandy.jnmaker.lucene.indexer.Fields.NOTE_ID ;
import static com.sandy.jnmaker.lucene.indexer.Fields.NOTE_TEXT ;
import static com.sandy.jnmaker.lucene.indexer.Fields.NOTE_TYPE ;
import static com.sandy.jnmaker.lucene.indexer.Fields.SRC_PATH ;
import static com.sandy.jnmaker.lucene.indexer.Fields.SUBJECT ;
import static com.sandy.jnmaker.lucene.indexer.Fields.SUB_CHAPTER_NUM ;
import static com.sandy.jnmaker.lucene.indexer.Fields.SYLLABUS ;
import static com.sandy.jnmaker.util.ObjectRepository.getAppConfig ;

import java.io.File ;
import java.io.FileInputStream ;
import java.io.IOException ;
import java.io.StringReader ;
import java.security.MessageDigest ;
import java.security.NoSuchAlgorithmException ;

import org.apache.commons.codec.binary.Hex ;
import org.apache.commons.io.IOUtils ;
import org.apache.log4j.Logger ;
import org.apache.lucene.document.Document ;
import org.apache.lucene.document.Field ;
import org.apache.lucene.document.Field.Store ;
import org.apache.lucene.document.StringField ;
import org.apache.lucene.document.TextField ;
import org.apache.lucene.store.FSDirectory ;


public class LuceneHelper {
	
	public static final Logger log = Logger.getLogger( LuceneHelper.class ) ;
	
    private static MessageDigest md5Gen = null ;

    static {
        try {
			md5Gen = MessageDigest.getInstance( "MD5" ) ;
		} 
        catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
    }
    
    public static void addFieldToDocument( Document doc, 
                                           String key, String value ) {

        Field field = null ;
        switch( key ) {
            case NOTE_ID :
            case CHAPTER_ID :
                field = new StringField( key, value, Store.NO ) ;
                break ;
                
            case SRC_PATH :
            case MEDIA_PATHS :
            case CHAPTER_NUM :
            case SUB_CHAPTER_NUM :
                field = new StringField( key, value, Store.YES ) ;
                break ;
                
            case NOTE_TYPE :
            case SUBJECT :
            case CHAPTER_NAME :
            case NOTE_TEXT :
            case SYLLABUS :
                field = new TextField( key, value, Store.YES ) ;
                break ;
        }
        
        doc.add( field ) ;
    }
    
    public static String getFileContentMD5( File file ) throws Exception {
    	byte[] digest = md5Gen.digest( IOUtils.toByteArray( new FileInputStream( file )) ) ;
        return Hex.encodeHexString( digest ) ;
    }
    
    public static String getMD5( String input ) throws Exception {
        byte[] digest = md5Gen.digest( input.getBytes() ) ;
        return Hex.encodeHexString( digest ) ;
    }
    
    public static FSDirectory getLuceneFSDir() 
        throws Exception {
        
        FSDirectory luceneFSDir = null ;
        File workspaceDir = getAppConfig().getWorkspaceDir() ;
        File indexDir     = new File( workspaceDir, "index" ) ;
        
        // check for index directory and create it if not present
        log.debug( "Using index directory at " + indexDir.getAbsolutePath() ) ;
        if ( !indexDir.exists() ) {
            log.debug( "Index directory not present, creating new" ) ;
            indexDir.mkdirs() ;
        }
        
        luceneFSDir = FSDirectory.open( indexDir.toPath() ) ;
        return luceneFSDir ;
    }
    
    public static String removeComments( String code ) {
        StringBuilder newCode = new StringBuilder() ;
        try( StringReader sr = new StringReader( code ) ) {
            boolean inBlockComment = false ;
            boolean inLineComment = false ;
            boolean out = true ;

            int prev = sr.read() ;
            int cur ;
            for( cur = sr.read() ; cur != -1 ; cur = sr.read() ) {
                if( inBlockComment ) {
                    if( prev == '*' && cur == '/' ) {
                        inBlockComment = false ;
                        out = false ;
                    }
                }
                else if( inLineComment ) {
                    if( cur == '\r' ) { // start untested block
                        sr.mark( 1 ) ;
                        int next = sr.read() ;
                        if( next != '\n' ) {
                            sr.reset() ;
                        }
                        inLineComment = false ;
                        out = false ; // end untested block
                    }
                    else if( cur == '\n' ) {
                        inLineComment = false ;
                        out = false ;
                    }
                }
                else {
                    if( prev == '/' && cur == '*' ) {
                        sr.mark( 1 ) ; // start untested block
                        int next = sr.read() ;
                        if( next != '*' ) {
                            inBlockComment = true ; // tested line (without rest
                                                    // of block)
                        }
                        sr.reset() ; // end untested block
                    }
                    else if( prev == '/' && cur == '/' ) {
                        inLineComment = true ;
                    }
                    else if( out ) {
                        newCode.append( (char) prev ) ;
                    }
                    else {
                        out = true ;
                    }
                }
                prev = cur ;
            }
            if( prev != -1 && out && !inLineComment ) {
                newCode.append( (char) prev ) ;
            }
        }
        catch( IOException e ) {
            e.printStackTrace() ;
        }

        return newCode.toString() ;
    }
}
