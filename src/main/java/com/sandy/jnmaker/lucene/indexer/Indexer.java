package com.sandy.jnmaker.lucene.indexer ;

import static com.sandy.jnmaker.lucene.indexer.LuceneHelper.addFieldToDocument ;
import static com.sandy.jnmaker.lucene.indexer.LuceneHelper.getLuceneFSDir ;

import java.io.File ;

import org.apache.log4j.Logger ;
import org.apache.lucene.analysis.standard.StandardAnalyzer ;
import org.apache.lucene.document.Document ;
import org.apache.lucene.index.IndexWriter ;
import org.apache.lucene.index.IndexWriterConfig ;
import org.apache.lucene.index.Term ;
import org.apache.lucene.store.FSDirectory ;

import com.sandy.jnmaker.lucene.ChapterInfo ;
import com.sandy.jnmaker.lucene.NoteInfo ;

public class Indexer implements Fields {
	
	private static final Logger log = Logger.getLogger( Indexer.class ) ;
	
	private IndexWriter writer = null ;

	public Indexer() {
		super() ;
	}
	
	public void initialize() throws Exception {
	    
		FSDirectory luceneFSDir = getLuceneFSDir() ;
		writer = new IndexWriter( luceneFSDir, 
		                          new IndexWriterConfig( new StandardAnalyzer() ) ) ; 
		log.debug( "Opened index with " + writer.numDocs() + " documents" ) ;
	}
	
	public void close() throws Exception {
	    
		log.debug( "Committing and closing the index..." ) ;
		writer.close() ;
	}
	
	public void clear() throws Exception {
		
		log.debug( "Cleaning the index. Removing all documents." ) ;
		writer.deleteAll() ;
		writer.commit() ;
	}
	
	public void add( NoteInfo note ) throws Exception {
	    
        ChapterInfo chp = note.getChapter() ;
        Document doc = new Document() ;
        
        // Add the id properties
        addFieldToDocument( doc, SRC_PATH, chp.getSrcPath() ) ;
        addFieldToDocument( doc, NOTE_ID,  note.getNoteID() ) ;
        
        // Add the standard properties
        addFieldToDocument( doc, CHAPTER_ID,      chp.getChapterID() ) ;
        addFieldToDocument( doc, SYLLABUS,        chp.getSyllabus() ) ;
        addFieldToDocument( doc, SUBJECT,         chp.getSubject() ) ;
        addFieldToDocument( doc, CHAPTER_NUM,     "" + chp.getChapterNum() ) ;
        addFieldToDocument( doc, SUB_CHAPTER_NUM, "" + chp.getSubChapterNum() ) ;
        addFieldToDocument( doc, CHAPTER_NAME,    chp.getChapterName() ) ;
        addFieldToDocument( doc, NOTE_TYPE,       note.getType() ) ;
        addFieldToDocument( doc, NOTE_TEXT,       note.getContent() ) ;
        addFieldToDocument( doc, MEDIA_PATHS,     getMediaPaths( note ) ) ;
        
        writer.addDocument( doc ) ;
        writer.commit() ;
	}
	
	private String getMediaPaths( NoteInfo note ) {
	    
        StringBuilder buffer = new StringBuilder() ;
        for( File file : note.getMediaFiles() ) {
            buffer.append( file.getAbsolutePath() )
                  .append( File.pathSeparator ) ;
        }
        return buffer.toString() ;
	}
	
	public void deleteDocumentsFromSource( File file )
	    throws Exception {
	    
	    Term term = new Term( SRC_PATH, file.getAbsolutePath() ) ;
	    writer.deleteDocuments( term ) ;
	    writer.commit() ;
	}
}
