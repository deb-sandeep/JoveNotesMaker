package com.sandy.jnmaker.ui.panels.image.k12;

import static java.awt.event.KeyEvent.VK_1 ;
import static java.awt.event.KeyEvent.VK_2 ;
import static java.awt.event.KeyEvent.VK_3 ;

import java.awt.event.ActionListener ;
import java.io.File ;

import org.apache.log4j.Logger ;

import com.sandy.common.ui.CloseableTabbedPane.TabCloseListener ;
import com.sandy.common.ui.DrawingCanvas ;
import com.sandy.common.ui.ScalableImagePanel.ScalableImagePanelListener ;
import com.sandy.jnmaker.ui.panels.image.AbstractImagePanel ;
import com.sandy.jnmaker.ui.panels.image.SaveFnKeyHandler ;

@SuppressWarnings( "serial" )
public class K12QuestionsImagePanel extends AbstractImagePanel 
    implements ActionListener, TabCloseListener, ScalableImagePanelListener {

    static final Logger log = Logger.getLogger( K12QuestionsImagePanel.class ) ;
    
    public final static String ID = "K12" ;
    
    private K12ExerciseQuestion lastQuestion = null ;
    
    private SaveFnKeyHandler nextMajorElementSDHandler = 
        new SaveFnKeyHandler( "nextMajorElement" ) {
            public void handleEvent() {
                if( lastQuestion != null ) {
                    K12ExerciseQuestion nextQ = null ;
                    nextQ = lastQuestion.nextMajorElement() ;
                    setSelectedFile( nextQ ) ;
                }
            }
        } ;
    
    private SaveFnKeyHandler toggleHeaderSDHandler = 
        new SaveFnKeyHandler( "toggleHeader" ) {
            public void handleEvent() {
                File selFile = saveFileChooser.getSelectedFile() ;
                if( selFile != null ) {
                    K12ExerciseQuestion nextQ = null ;
                    nextQ = new K12ExerciseQuestion( selFile.getName() ) ;
                    nextQ.setHeader( !nextQ.isHeader() ) ;
                    setSelectedFile( nextQ ) ;
                }
            }
        } ; 
    
    private SaveFnKeyHandler nextMajorElementWithHeaderSDHandler = 
        new SaveFnKeyHandler( "nextMajorElementWithHeader" ) {
            public void handleEvent() {
                if( lastQuestion != null ) {
                    K12ExerciseQuestion nextQ = null ;
                    nextQ = lastQuestion.nextMajorElement() ;
                    nextQ.setHeader( true ) ;
                    setSelectedFile( nextQ ) ;
                }
            }
        } ;
    
    public K12QuestionsImagePanel() {
        super() ;
        bindKeyStrokesForSaveDialog() ;
    }

    private void bindKeyStrokesForSaveDialog() {
        
        super.registerSaveFnHandler( VK_1, nextMajorElementSDHandler ) ;
        super.registerSaveFnHandler( VK_2, toggleHeaderSDHandler ) ;
        super.registerSaveFnHandler( VK_3, nextMajorElementWithHeaderSDHandler ) ;
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
