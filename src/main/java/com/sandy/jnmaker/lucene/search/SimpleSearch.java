package com.sandy.jnmaker.lucene.search ;

import java.util.HashMap ;
import java.util.List ;
import java.util.Map ;

import org.apache.lucene.analysis.standard.StandardAnalyzer ;
import org.apache.lucene.queryparser.classic.QueryParser ;
import org.apache.lucene.search.Query ;

import com.sandy.jnmaker.lucene.NoteInfo ;

public class SimpleSearch extends AbstractSearch {

    public SimpleSearch() throws Exception {
        super() ;
    }

    public static final String KEYWORD_CRITERIA = "KEYWORD_CRITERIA" ;

    protected Query buildQuery( Map<String, String> criteria ) throws Exception {

        String queryStr = criteria.get( KEYWORD_CRITERIA ) ;

        QueryParser parser = new QueryParser( queryStr, new StandardAnalyzer() ) ;
        Query q = parser.parse( queryStr ) ;
        
        return q ;
    }

    public List<NoteInfo> search( String query, int max )
            throws SearchException {

        Map<String, String> criteria = new HashMap<String, String>() ;
        criteria.put( KEYWORD_CRITERIA, query ) ;

        return super.search( criteria, max ) ;
    }
}
