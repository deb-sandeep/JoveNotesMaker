package com.sandy.jnmaker.lucene;

import com.sandy.jnmaker.lucene.indexer.LuceneHelper ;

public class ChapterInfo {

    private String srcPath       = null ;
    private String syllabus      = null ;
    private String subject       = null ;
    private int    chapterNum    = -1 ;
    private int    subChapterNum = -1 ;
    private String chapterName   = null ;
    
    public ChapterInfo() {
    }
    
    public String getSrcPath() {
        return this.srcPath ;
    }
    
    public ChapterInfo setSrcPath( String path ) {
        this.srcPath = path ;
        return this ;
    }

    public String getSyllabus() {
        return syllabus ;
    }

    public ChapterInfo setSyllabus( String syllabus ) {
        this.syllabus = syllabus ;
        return this ;
    }

    public String getSubject() {
        return subject ;
    }

    public ChapterInfo setSubject( String subject ) {
        this.subject = subject ;
        return this ;
    }

    public int getChapterNum() {
        return chapterNum ;
    }

    public ChapterInfo setChapterNum( int chapterNum ) {
        this.chapterNum = chapterNum ;
        return this ;
    }

    public int getSubChapterNum() {
        return subChapterNum ;
    }

    public ChapterInfo setSubChapterNum( int subChapterNum ) {
        this.subChapterNum = subChapterNum ;
        return this ;
    }

    public String getChapterName() {
        return chapterName ;
    }

    public ChapterInfo setChapterName( String chapterName ) {
        this.chapterName = chapterName ;
        return this ;
    }
    
    public String getChapterID() throws Exception {
        String idBase = getSyllabus()      + ":" +
                        getSubject()       + ":" +
                        getChapterNum()    + ":" +
                        getSubChapterNum() + ":" +
                        getChapterName() ;
        
        return LuceneHelper.getMD5( idBase ) ;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31 ;
        int result = 1 ;
        result = prime * result
                + ( ( chapterName == null ) ? 0 : chapterName.hashCode() ) ;
        result = prime * result + chapterNum ;
        result = prime * result
                + ( ( srcPath == null ) ? 0 : srcPath.hashCode() ) ;
        result = prime * result + subChapterNum ;
        result = prime * result
                + ( ( subject == null ) ? 0 : subject.hashCode() ) ;
        result = prime * result
                + ( ( syllabus == null ) ? 0 : syllabus.hashCode() ) ;
        return result ;
    }

    @Override
    public boolean equals( Object obj ) {
        
        if( this == obj ) { 
            return true ; 
        }
        
        if( obj == null ) { 
            return false ; 
        }
        
        if( !( obj instanceof ChapterInfo ) ) { 
            return false ; 
        }

        ChapterInfo other = (ChapterInfo) obj ;
        if( srcPath == null ) {
            if( other.srcPath != null ) { 
                return false ; 
            }
        }
        else if( srcPath.equals( other.srcPath ) ) { 
            return true ; 
        }
        
        return false ;
    }
}
