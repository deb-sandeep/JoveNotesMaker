package com.sandy.jnmaker.indexer;

import java.io.File ;
import java.util.ArrayList ;
import java.util.List ;

public class IndexingDaemon extends Thread {

    private SourceProcessingJournal journal = null ;
    private List<File> sourceDirectories = new ArrayList<File>() ;
//  private XTextModelParser modelParser = null ;
    
    public IndexingDaemon() {
        super( "JoveNotesMaker indexing daemon" ) ;
        super.setDaemon( true ) ;
    }

    public void setSourceDirectories( String paths ) {
        
        String[] dirs = paths.split( ":" ) ;
        for( String dir : dirs ) {
            File file = new File( dir ) ;
            if( !file.exists() ) {
                throw new IllegalArgumentException(
                        "The directory " + dir + " does not exist." ) ;
            }
            else if( !file.isDirectory() ) {
                throw new IllegalArgumentException(
                        "The directory " + dir + " is not a directory." ) ;
            }
            else {
                sourceDirectories.add( file ) ;
            }
        }
    }
    
    public void run() {
        
    }

//  private void testParsing() throws Exception {
//  
//  modelParser = new XTextModelParser( "com.sandy.xtext.JoveNotesStandaloneSetup" ) ;
//  for( File dir : ObjectRepository.getAppConfig().getJNSrcDirs() ) {
//      recurseAndProcessDir( dir ) ;
//  }
//}
//
//private void recurseAndProcessDir( File dir ) throws Exception {
//  
//  if( dir.isFile() ) {
//      if( dir.getName().endsWith( ".jn" ) ) {
//          parseJNFile( dir ) ;
//      }
//  }
//  else {
//      File[] files = dir.listFiles() ;
//      for( File file : files ) {
//          recurseAndProcessDir( file ) ;
//      }
//  }
//}
//
//private void parseJNFile( File file ) throws Exception {
//  
//  JoveNotes ast = ( JoveNotes )modelParser.parseFile( file ) ;
//  
//  for( NotesElement element : ast.getNotesElements() ) {
//      ICompositeNode cmpNode = NodeModelUtils.getNode( element ) ;
//      if( cmpNode != null ) {
//          String sourceText = cmpNode.getText() ;
//          logger.info( sourceText ) ;
//      }
//  }
//}

}
