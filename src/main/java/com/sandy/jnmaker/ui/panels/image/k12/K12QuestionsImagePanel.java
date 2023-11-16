package com.sandy.jnmaker.ui.panels.image.k12;

import static java.awt.event.KeyEvent.VK_1 ;
import static java.awt.event.KeyEvent.VK_2 ;
import static java.awt.event.KeyEvent.VK_3 ;
import static java.awt.event.KeyEvent.VK_4 ;
import static java.awt.event.KeyEvent.VK_5 ;

import java.awt.event.ActionListener ;
import java.io.File ;

import org.apache.log4j.Logger ;

import com.sandy.common.ui.CloseableTabbedPane.TabCloseListener ;
import com.sandy.common.ui.ScalableImagePanel.ScalableImagePanelListener ;
import com.sandy.jnmaker.ui.panels.image.AbstractImagePanel ;
import com.sandy.jnmaker.ui.panels.image.SaveFnKeyHandler ;

@SuppressWarnings( "serial" )
public class K12QuestionsImagePanel extends AbstractImagePanel<K12ExerciseQuestion>
    implements ActionListener, TabCloseListener, ScalableImagePanelListener {

    static final Logger log = Logger.getLogger( K12QuestionsImagePanel.class ) ;
    
    public final static String ID = "K12" ;
    
    private final SaveFnKeyHandler nextMajorElementSDHandler =
        new SaveFnKeyHandler( "nextMajorElement" ) {
            public void handleEvent() {
                if( currentlyDisplayedQuestion != null ) {
                    K12ExerciseQuestion nextQ = null ;
                    nextQ = currentlyDisplayedQuestion.nextMajorElement() ;
                    updateSaveDialogFileName( nextQ.getFileName() ) ;
                    currentlyDisplayedQuestion = nextQ ;
                }
            }
        } ;
    
    private final SaveFnKeyHandler nextElementSDHandler =
            new SaveFnKeyHandler( "nextMajorElement" ) {
                public void handleEvent() {
                    if( currentlyDisplayedQuestion != null ) {
                        K12ExerciseQuestion nextQ = null ;
                        nextQ = currentlyDisplayedQuestion.nextQuestion() ;
                        updateSaveDialogFileName( nextQ.getFileName() ) ;
                        currentlyDisplayedQuestion = nextQ ;
                    }
                }
            } ;
        
    private final SaveFnKeyHandler toggleHeaderSDHandler =
        new SaveFnKeyHandler( "toggleHeader" ) {
            public void handleEvent() {
                File selFile = saveFileChooser.getSelectedFile() ;
                if( selFile != null ) {
                    K12ExerciseQuestion nextQ = null ;
                    nextQ = new K12ExerciseQuestion( selFile.getName() ) ;
                    nextQ.setHeader( !nextQ.isHeader() ) ;
                    updateSaveDialogFileName( nextQ.getFileName() ) ;
                }
            }
        } ; 
    
    private final SaveFnKeyHandler nextMajorElementWithHeaderSDHandler =
        new SaveFnKeyHandler( "nextMajorElementWithHeader" ) {
            public void handleEvent() {
                if( currentlyDisplayedQuestion != null ) {
                    K12ExerciseQuestion nextQ = null ;
                    nextQ = currentlyDisplayedQuestion.nextMajorElement() ;
                    nextQ.setHeader( true ) ;
                    updateSaveDialogFileName( nextQ.getFileName() ) ;
                    currentlyDisplayedQuestion = nextQ ;
                }
            }
        } ;
    
    private final SaveFnKeyHandler addFirstPartNumberSDHandler =
        new SaveFnKeyHandler( "addFirstPartNumber" ) {
            public void handleEvent() {
                if( currentlyDisplayedQuestion != null ) {
                    currentlyDisplayedQuestion.setPartNum( 1 );
                    updateSaveDialogFileName( currentlyDisplayedQuestion.getFileName() ) ;
                }
            }
        } ;

    public K12QuestionsImagePanel() {
        super() ;
        bindKeyStrokesForSaveDialog() ;
    }
    
    @Override
    protected K12ExerciseQuestion constructQuestion( File file ) {
        return new K12ExerciseQuestion( file.getName() ) ;
    }

    private void bindKeyStrokesForSaveDialog() {
        
        super.registerSaveFnHandler( VK_1, nextMajorElementSDHandler ) ;
        super.registerSaveFnHandler( VK_2, nextElementSDHandler ) ;
        super.registerSaveFnHandler( VK_3, toggleHeaderSDHandler ) ;
        super.registerSaveFnHandler( VK_4, nextMajorElementWithHeaderSDHandler ) ;
        super.registerSaveFnHandler( VK_5, addFirstPartNumberSDHandler) ;
    }
    
    public File getRecommendedSaveDir( File imgFile ) {
        return imgFile.getParentFile().getParentFile() ;
    }

}
