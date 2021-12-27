package com.sandy.jnmaker.ui.panels.image.k12;

import java.awt.event.ActionEvent ;
import java.awt.event.ActionListener ;
import java.awt.event.KeyEvent ;
import java.io.File ;

import javax.swing.AbstractAction ;
import javax.swing.ActionMap ;
import javax.swing.InputMap ;
import javax.swing.JFileChooser ;
import javax.swing.KeyStroke ;

import org.apache.log4j.Logger ;

import com.sandy.common.ui.DrawingCanvas ;
import com.sandy.common.ui.CloseableTabbedPane.TabCloseListener ;
import com.sandy.common.ui.ScalableImagePanel.ScalableImagePanelListener ;
import com.sandy.jnmaker.ui.panels.image.AbstractImagePanel ;

@SuppressWarnings( "serial" )
public class K12QuestionsImagePanel extends AbstractImagePanel 
    implements ActionListener, TabCloseListener, ScalableImagePanelListener {

    static final Logger log = Logger.getLogger( K12QuestionsImagePanel.class ) ;
    
    public final static String ID = "K12" ;
    
    private K12ExerciseQuestion lastQuestion = null ;
    
    public K12QuestionsImagePanel() {
        super() ;
    }

    @Override
    protected void bindKeyStrokesForSaveDialog() {
        KeyStroke f1 = KeyStroke.getKeyStroke( KeyEvent.VK_F1, 0 ) ;
        KeyStroke f2 = KeyStroke.getKeyStroke( KeyEvent.VK_F2, 0 ) ;
        KeyStroke f3 = KeyStroke.getKeyStroke( KeyEvent.VK_F3, 0 ) ;
        KeyStroke f4 = KeyStroke.getKeyStroke( KeyEvent.VK_F4, 0 ) ;
        
        InputMap map = saveFileChooser.getInputMap( JFileChooser.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT ) ;
        map.put( f1, "nextMajorElement"    ) ;
        map.put( f2, "toggleHeader"   ) ;
        map.put( f3, "nextMajorElementWithHeader" ) ;
        map.put( f4, "incrementLCTPassage" ) ;
        
        ActionMap actionMap = saveFileChooser.getActionMap() ;
        
        actionMap.put( "nextMajorElement", new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                if( lastQuestion != null ) {
                    K12ExerciseQuestion nextQ = null ;
                    nextQ = lastQuestion.nextMajorElement() ;
                    setSelectedFile( nextQ ) ;
                }
            }
        } ) ;
        
        actionMap.put( "toggleHeader", new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                File selFile = saveFileChooser.getSelectedFile() ;
                if( selFile != null ) {
                    K12ExerciseQuestion nextQ = null ;
                    nextQ = new K12ExerciseQuestion( selFile.getName() ) ;
                    nextQ.setHeader( !nextQ.isHeader() ) ;
                    setSelectedFile( nextQ ) ;
                }
            }
        } ) ;

        actionMap.put( "nextMajorElementWithHeader", new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                if( lastQuestion != null ) {
                    K12ExerciseQuestion nextQ = null ;
                    nextQ = lastQuestion.nextMajorElement() ;
                    nextQ.setHeader( true ) ;
                    setSelectedFile( nextQ ) ;
                }
            }
        } ) ;
    }

    private void setSelectedFile( K12ExerciseQuestion question ) {
        File outputFile = new File( lastSavedDir, question.getFileName() ) ;
        saveFileChooser.setSelectedFile( outputFile ) ; 
    }
    
    @Override
    protected File getUserApprovedOutputFile( int selMod ) {
        
        K12ExerciseQuestion nextQ = null ;
        File outputFile = null ;
        
        boolean interventionRequired = false ;
        
        if( lastQuestion == null || 
            lastQuestion.nextItemNeedsIntervention() || 
            selMod == DrawingCanvas.MARK_END_MODIFIER_RIGHT_BTN ) {
            
            interventionRequired = true ;
        }
        
        if( lastQuestion != null ) {
            nextQ = lastQuestion.nextElement() ;
            outputFile = new File( lastSavedDir, nextQ.getFileName() ) ;
        }
        
        if( interventionRequired ) {
            if( outputFile != null ) {
                saveFileChooser.setSelectedFile( outputFile ) ;
            }
            outputFile = getFileViaSaveDialog() ;
        }
        
        return outputFile ;
    }

    @Override
    protected void handlePostImageSave() {
        lastQuestion = new K12ExerciseQuestion( lastSavedFile.getName() ) ;
    }
}
