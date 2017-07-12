package com.sandy.jnmaker.tools.cmap;

import java.awt.event.KeyAdapter ;
import java.awt.event.KeyEvent ;
import java.io.File;
import java.net.URL;

import javax.swing.JFrame ;

import com.sandy.common.util.StringUtil ;
import com.sandy.core.ConfigManager ;
import com.sandy.jcmap.JCMap ;
import com.sandy.jnmaker.JoveNotesMaker;
import com.sandy.jnmaker.ui.notedialogs.NotesCreatorDialog ;
import com.sandy.jnmaker.ui.notedialogs.qa.QAPanel ;
import com.sandy.jnmaker.util.ObjectRepository ;

public class CMapper {
    
    private JCMap cMapper = null ;
    
    public CMapper() throws Exception {
    	
    	initializeJCMapConfig() ;

        cMapper = new JCMap( false ) ;
        cMapper.setBounds( 0, 0, 600, 700 ) ;
        cMapper.setDefaultCloseOperation( JFrame.HIDE_ON_CLOSE ) ;
        
        cMapper.getEditor().addKeyListener( new KeyAdapter() {
            @Override
            public void keyPressed( KeyEvent e ) {
                if( e.getKeyCode()   == KeyEvent.VK_ENTER && 
                    e.getModifiers() == KeyEvent.CTRL_MASK ) {
                    String text = cMapper.getEditor().getText() ;
                    if( StringUtil.isNotEmptyOrNull( text ) ) {
                        processConceptMapText( text ) ;
                    }
                }            
            }
        } );
    }
    
    private void initializeJCMapConfig() throws Exception {
    	
        ConfigManager cfgMgr = ConfigManager.getInstance() ;
        
        boolean initialized = false ;
        String userHomePath = System.getProperty( "user.home" ) ;
        File   userHomeAppDir = new File( userHomePath, JoveNotesMaker.APP_ID ) ;
        
        if( userHomeAppDir.exists() ) {
        	File localProp = new File( userHomeAppDir, "jcmap-config.properties" ) ;
        	if( localProp.exists() ) {
        		URL url = localProp.toURI().toURL() ; ;
        		cfgMgr.initialize( url ) ;
        		initialized = true ;
        	}
        }
        else {
        	userHomeAppDir.mkdirs() ;
        }
    	
        if( !initialized ) {
        	URL url = CMapper.class.getResource( "/jcmap-config.properties" ) ;
        	cfgMgr.initialize( url ) ;
        }
    }
    
    public JCMap getCMapper() {
        return this.cMapper ;
    }
    
    private void processConceptMapText( String text ) {
        
        String cmapText = convertEditorText( text ) ;
        
        NotesCreatorDialog curDialog = ObjectRepository.getCurNotesDialog() ;
        if( curDialog != null && 
            ( curDialog.getCenterPanel() instanceof QAPanel ) ) {
            
            QAPanel qaPanel = ( QAPanel )curDialog.getCenterPanel() ;
            qaPanel.appendNoteToAnswer( cmapText ) ;
        }
        else {
            ObjectRepository.getMainFrame().getJNPanel().addNote( cmapText ) ;
        }
        
        cMapper.getEditor().setText( "" ) ;
        cMapper.setVisible( false ) ;
    }
    
    private String convertEditorText( String text ) {
        return "\n{{@cmap\n" + text + "\n}}\n" ;
    }
}
