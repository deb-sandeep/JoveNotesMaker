package com.sandy.jnmaker.lucene;

import java.io.File ;
import java.util.ArrayList ;
import java.util.List ;

import com.sandy.jnmaker.lucene.indexer.LuceneHelper ;

public class NoteInfo {

    private ChapterInfo chapter    = null ;
    private String      type       = null ;
    private String      content    = null ;
    private List<File>  mediaFiles = new ArrayList<File>() ;
    
    public NoteInfo( ChapterInfo chapter ) {
        this.chapter = chapter ;
    }
    
    public NoteInfo setType( String type ) {
        this.type = type ;
        return this ;
    }
    
    public NoteInfo setContent( String content ) {
        this.content = content ;
        return this ;
    }
    
    public NoteInfo setMediaFiles( List<File> files ) {
        this.mediaFiles.addAll( files ) ;
        return this ;
    }
    
    public ChapterInfo getChapter() {
        return this.chapter ;
    }
    
    public String getType() {
        return this.type ;
    }
    
    public String getContent() {
        return this.content ;
    }
    
    public List<File> getMediaFiles() {
        return this.mediaFiles ;
    }
    
    public String getNoteID() throws Exception {
        
        String idBase = chapter.getChapterID() + ":" +
                        getType() + ":" + 
                        getContent() ;
        return LuceneHelper.getMD5( idBase ) ;
    }
    
    public String getHash() throws Exception {
        return LuceneHelper.getMD5( getType() + getContent() ) ;
    }
}
