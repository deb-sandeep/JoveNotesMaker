package com.sandy.jnmaker.ui.panels.image.k12;

import java.awt.event.ActionListener ;
import java.io.File ;
import java.io.FileFilter;
import java.text.DecimalFormat;

import com.sandy.common.ui.CloseableTabbedPane.TabCloseListener ;
import com.sandy.common.ui.ScalableImagePanel.ScalableImagePanelListener ;
import com.sandy.jnmaker.ui.panels.image.AbstractImagePanel ;
import com.sandy.jnmaker.ui.panels.image.SaveFileNameHelperAccessory;
import com.sandy.jnmaker.ui.panels.image.SaveFnKeyHandler ;
import org.apache.log4j.Logger;

import javax.swing.*;

import static java.awt.event.KeyEvent.*;

public class K12QuestionsImagePanel extends AbstractImagePanel<K12ExerciseQuestion>
    implements ActionListener, TabCloseListener, ScalableImagePanelListener {
    
    static final Logger log = Logger.getLogger( K12QuestionsImagePanel.class ) ;

    public final static String ID  = "K12" ;
    
    private final SaveFnKeyHandler nextMajorElementSDHandler =
        new SaveFnKeyHandler( "nextMajorElement" ) {
            public void handleEvent() {
                if( currentlyDisplayedQuestion != null ) {
                    K12ExerciseQuestion nextQ ;
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
                        K12ExerciseQuestion nextQ ;
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
                    K12ExerciseQuestion nextQ ;
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
                    K12ExerciseQuestion nextQ ;
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

    private final SaveFnKeyHandler nextChapterSDHandler =
        new SaveFnKeyHandler( "nextChapter" ) {
            public void handleEvent() {
                if( currentlyDisplayedQuestion != null ) {
                    currentlyDisplayedQuestion.incrementChapterNumber() ;
                    updateSaveDialogFileName( currentlyDisplayedQuestion.getFileName() ) ;
                    
                    if( lastSavedDir != null ) {
                        File subRoot = new File( lastSavedDir, "../../.." ) ;
                        File[] chapters = subRoot.listFiles( File::isDirectory ) ;
                        if( chapters != null ) {
                            for( File chapter : chapters ) {
                                String nextChapterNameStart = String.format( "%02d",
                                        currentlyDisplayedQuestion.getChapterNum() ) + " - " ;
                                if( chapter.getName().startsWith( nextChapterNameStart ) ) {
                                    File exercisesDir = new File( chapter, "img/exercise" ) ;
                                    if( !exercisesDir.exists() ) {
                                        exercisesDir.mkdirs() ;
                                    }
                                    lastSavedDir = exercisesDir ;
                                    saveFileChooser.setCurrentDirectory( lastSavedDir ) ;
                                }
                            }
                        }
                    }
                }
            }
        } ;

    private final SaveFnKeyHandler nextExerciseSDHandler =
        new SaveFnKeyHandler( "nextExercise" ) {
            public void handleEvent() {
                if( currentlyDisplayedQuestion != null ) {
                    currentlyDisplayedQuestion.incrementExerciseNumber() ;
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
        super.registerSaveFnHandler( VK_6, nextChapterSDHandler ) ;
        super.registerSaveFnHandler( VK_7, nextExerciseSDHandler ) ;
    }
    
    public File getRecommendedSaveDir( File imgFile ) {
        return imgFile.getParentFile().getParentFile() ;
    }
    
    @Override
    protected JComponent getSaveFileChooserAccessory() {
        String[] help = {
                "--------- Shortcuts --------",
                "Ctrl+1 - Increment Major QNo",
                "Ctrl+2 - Next element in seq",
                "Ctrl+3 - Toggle header",
                "Ctrl+4 - Next major q + Hdr",
                "Ctrl+5 - Add first part num",
                "Ctrl+6 - Next chapter",
                "Ctrl+7 - Next exercise",
                "",
                "File name format:",
                "* Ch9_A_1Hdr(1)",
                "* Ch9_M_2.1",
            
        } ;
        return new SaveFileNameHelperAccessory( help ) ;
    }
}
