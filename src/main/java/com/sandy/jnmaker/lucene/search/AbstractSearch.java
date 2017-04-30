package com.sandy.jnmaker.lucene.search;

import static com.sandy.jnmaker.lucene.indexer.LuceneHelper.getLuceneFSDir ;

import java.util.ArrayList ;
import java.util.HashMap ;
import java.util.List ;
import java.util.Map ;

import org.apache.log4j.Logger ;
import org.apache.lucene.document.Document ;
import org.apache.lucene.index.DirectoryReader ;
import org.apache.lucene.search.IndexSearcher ;
import org.apache.lucene.search.Query ;
import org.apache.lucene.search.ScoreDoc ;
import org.apache.lucene.search.TopScoreDocCollector ;
import org.apache.lucene.store.FSDirectory ;

import com.sandy.jnmaker.lucene.NoteInfo ;

public abstract class AbstractSearch {
	
	private static final Logger log = Logger.getLogger( AbstractSearch.class ) ;
	
	@SuppressWarnings( "serial" )
    public static class SearchException extends Exception {
	    
        public SearchException( String msg ) {
	        super( msg ) ;
	    }
	    
	    public SearchException( String msg, Exception e ) {
	        super( msg, e ) ;
	    }
	}
	
    private IndexSearcher searcher = null ;
    private TopScoreDocCollector collector = null ;

	public AbstractSearch() throws Exception {
	    
        FSDirectory luceneFSDir = getLuceneFSDir() ;
        DirectoryReader dirReader = DirectoryReader.open( luceneFSDir ) ;
        searcher = new IndexSearcher( dirReader ) ;
        collector = TopScoreDocCollector.create( 100 ) ;
	}
	
	public List<NoteInfo> search( Map<String, String> criteria, int max ) 
	        throws SearchException {
		
		List<NoteInfo> results = new ArrayList<NoteInfo>() ;
		Map<String, Boolean> noteInfoHashMap = new HashMap<String, Boolean>() ;
		
		try {
            Query q = buildQuery( criteria ) ;
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
            throw new SearchException( "Search failure", e ) ;
        }
		return results ;
	}
	
    protected abstract Query buildQuery( Map<String, String> criteria ) 
            throws Exception ;
    
	private NoteInfo getNoteInfoFromDoc( Document doc ) {
		return null ;
	}
}
