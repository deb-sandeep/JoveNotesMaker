package com.sandy.jnmaker.lucene.search;

import static com.sandy.jnmaker.lucene.indexer.LuceneHelper.getLuceneFSDir ;

import java.io.File ;
import java.util.ArrayList ;
import java.util.HashMap ;
import java.util.List ;
import java.util.Map ;

import org.apache.log4j.Logger ;
import org.apache.lucene.analysis.standard.StandardAnalyzer ;
import org.apache.lucene.document.Document ;
import org.apache.lucene.index.DirectoryReader ;
import org.apache.lucene.queryparser.classic.QueryParser ;
import org.apache.lucene.search.IndexSearcher ;
import org.apache.lucene.search.Query ;
import org.apache.lucene.search.ScoreDoc ;
import org.apache.lucene.search.TopScoreDocCollector ;
import org.apache.lucene.store.FSDirectory ;

import com.sandy.common.util.StringUtil ;
import com.sandy.jnmaker.lucene.ChapterInfo ;
import com.sandy.jnmaker.lucene.NoteInfo ;
import com.sandy.jnmaker.lucene.indexer.Fields ;

public class Searcher {
	
	private static final Logger log = Logger.getLogger( Searcher.class ) ;
	
	@SuppressWarnings( "serial" )
    public static class SearchException extends Exception {
	    
        public SearchException( String msg ) {
	        super( msg ) ;
	    }
	    
	    public SearchException( String msg, Exception e ) {
	        super( msg, e ) ;
	    }
	}
	
    private IndexSearcher        searcher  = null ;
    private QueryParser          parser    = null ;

	public Searcher() throws Exception {
	    
        FSDirectory luceneFSDir = getLuceneFSDir() ;
        DirectoryReader dirReader = DirectoryReader.open( luceneFSDir ) ;
        searcher = new IndexSearcher( dirReader ) ;
        parser = new QueryParser( Fields.NOTE_TEXT, new StandardAnalyzer() ) ;
	}
	
	public List<NoteInfo> search( String queryStr, int max ) 
	        throws SearchException {
		
		List<NoteInfo> results = new ArrayList<NoteInfo>() ;
		Map<String, Boolean> noteInfoHashMap = new HashMap<String, Boolean>() ;
		TopScoreDocCollector collector = null ;
		
		try {
            Query q = parser.parse( queryStr ) ;
            collector = TopScoreDocCollector.create( 100 ) ;

            searcher.search( q, collector ) ;
            ScoreDoc[] hits = collector.topDocs().scoreDocs ;
            
            log.debug( "Search returned " + hits.length + " hits" ) ;
            
            int numSimilar = 0 ;
            for( int i = 0 ; i < hits.length && i < max ; i++ ) {
            	int id = hits[i].doc ;
            	Document doc = searcher.doc( id ) ;
            	
            	NoteInfo noteInfo = getNoteInfoFromDoc( doc ) ;
            	String   noteHash = noteInfo.getHash() ;
            	
            	if( !noteInfoHashMap.containsKey( noteHash ) ) {
            	    noteInfoHashMap.put( noteHash, true ) ;
            	    results.add( noteInfo ) ;
            	}
            }
            log.debug( "Ignoring " + numSimilar + " similar results" ) ;
        }
        catch( Exception e ) {
            log.error( "Error in searching", e ) ;
            throw new SearchException( "Search failure", e ) ;
        }
		return results ;
	}
	
	private NoteInfo getNoteInfoFromDoc( Document doc ) {
	    
	    ChapterInfo c = new ChapterInfo() ;
	    c.setSyllabus( doc.get( Fields.SYLLABUS ) ) ;
	    c.setSubject( doc.get( Fields.SUBJECT ) ) ;
	    c.setSrcPath( doc.get( Fields.SRC_PATH ) ) ;
	    c.setChapterNum( Integer.parseInt( doc.get( Fields.CHAPTER_NUM ) ) ) ;
	    c.setSubChapterNum( Integer.parseInt( doc.get( Fields.SUB_CHAPTER_NUM ) ) ) ;
	    c.setChapterName( doc.get( Fields.CHAPTER_NAME ) ) ;
	    
        NoteInfo n = new NoteInfo( c ) ;
        n.setType( doc.get( Fields.NOTE_TYPE ) ) ;
        n.setContent( doc.get( Fields.NOTE_TEXT ) ) ;
        n.setMediaFiles( getMediaFiles( doc ) ) ;
	    
		return n ;
	}
	
	private List<File> getMediaFiles( Document doc ) {
	    
        List<File> mediaFiles = new ArrayList<File>() ;
        String mediaFileStr = doc.get( Fields.MEDIA_PATHS ) ;
        String[] mediaFilePaths = mediaFileStr.split( ":" ) ;
        for( String path : mediaFilePaths ) {
            if( StringUtil.isNotEmptyOrNull( path ) ) {
                mediaFiles.add( new File( path ) ) ;
            }
        }
        return mediaFiles ;
	}
}
