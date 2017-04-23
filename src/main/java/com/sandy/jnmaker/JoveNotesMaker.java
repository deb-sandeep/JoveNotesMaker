package com.sandy.jnmaker ;

import static com.sandy.jnmaker.util.ObjectRepository.getProjectManager ;
import static com.sandy.jnmaker.util.ObjectRepository.getWkspManager ;
import static com.sandy.jnmaker.util.ObjectRepository.setApp ;
import static com.sandy.jnmaker.util.ObjectRepository.setAppConfig ;
import static com.sandy.jnmaker.util.ObjectRepository.setBus ;
import static com.sandy.jnmaker.util.ObjectRepository.setMainFrame ;
import static com.sandy.jnmaker.util.ObjectRepository.setObjectFactory ;
import static com.sandy.jnmaker.util.ObjectRepository.setProjectManager ;
import static com.sandy.jnmaker.util.ObjectRepository.setStateMgr ;
import static com.sandy.jnmaker.util.ObjectRepository.setUiActions ;
import static com.sandy.jnmaker.util.ObjectRepository.setWkspManager ;
import static com.sandy.jnmaker.util.ObjectRepository.setWordRepository ;
import static com.sandy.jnmaker.util.ObjectRepository.setWordnicAdapter ;

import java.awt.SplashScreen ;
import java.io.File ;

import javax.swing.SwingUtilities ;

import org.apache.log4j.Logger ;
import org.eclipse.xtext.nodemodel.ICompositeNode ;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils ;

import com.sandy.common.bus.EventBus ;
import com.sandy.common.objfactory.SpringObjectFactory ;
import com.sandy.common.ui.SwingUtils ;
import com.sandy.common.util.Configurator ;
import com.sandy.common.util.StateManager ;
import com.sandy.common.util.WorkspaceManager ;
import com.sandy.jnmaker.ui.MainFrame ;
import com.sandy.jnmaker.ui.actions.Actions ;
import com.sandy.jnmaker.ui.helper.ProjectManager ;
import com.sandy.jnmaker.util.AppConfig ;
import com.sandy.jnmaker.util.ConfiguratorBuilder ;
import com.sandy.jnmaker.util.JNMCommandLine ;
import com.sandy.jnmaker.util.ObjectRepository ;
import com.sandy.jnmaker.util.WordRepository ;
import com.sandy.jnmaker.util.WordnicAdapter ;
import com.sandy.jnmaker.util.XTextModelParser ;
import com.sandy.xtext.joveNotes.JoveNotes ;
import com.sandy.xtext.joveNotes.NotesElement ;

public class JoveNotesMaker {
    
    private static final Logger logger = Logger.getLogger( JoveNotesMaker.class ) ;
    
    public static final String APP_ID = "jnmaker" ;
    private XTextModelParser modelParser = null ;
    
    public void launch( String[] args ) throws Exception {
        
        preInitialize( args ) ;
        SwingUtils.setNimbusLookAndFeel() ;
        SwingUtilities.invokeLater( new Runnable() {
            @Override
            public void run() {
                try {
                    setUpAndShowMainFrame() ;
                    postInitialize() ;
                }
                catch( Exception e ) {
                    e.printStackTrace();
                }
            }
        } );
        
//        testParsing() ;
    }
    
    private void testParsing() throws Exception {
        
        modelParser = new XTextModelParser( "com.sandy.xtext.JoveNotesStandaloneSetup" ) ;
        for( File dir : ObjectRepository.getAppConfig().getJNSrcDirs() ) {
            recurseAndProcessDir( dir ) ;
        }
    }
    
    private void recurseAndProcessDir( File dir ) throws Exception {
        
        if( dir.isFile() ) {
            if( dir.getName().endsWith( ".jn" ) ) {
                parseJNFile( dir ) ;
            }
        }
        else {
            File[] files = dir.listFiles() ;
            for( File file : files ) {
                recurseAndProcessDir( file ) ;
            }
        }
    }
    
    private void parseJNFile( File file ) throws Exception {
        
        JoveNotes ast = ( JoveNotes )modelParser.parseFile( file ) ;
        
        for( NotesElement element : ast.getNotesElements() ) {
            ICompositeNode cmpNode = NodeModelUtils.getNode( element ) ;
            if( cmpNode != null ) {
                String sourceText = cmpNode.getText() ;
                logger.info( sourceText ) ;
            }
        }
    }
    
    private void preInitialize( String[] args ) throws Exception {
        
        // Process command line
        JNMCommandLine cmdLine = new JNMCommandLine() ;
        cmdLine.parse( args ) ;
        
        // Initialize the object factory
        SpringObjectFactory objFactory = new SpringObjectFactory() ;
        objFactory.addResourcePath( "classpath:com/sandy/jnmaker/objfactory.xml" ) ;
        objFactory.initialize() ;
        
        setObjectFactory( objFactory ) ;
        setBus( new EventBus() ) ;
        setWkspManager( new WorkspaceManager( APP_ID ) ) ;
        setWordnicAdapter( new WordnicAdapter() ) ;
        setAppConfig( new AppConfig() ) ;
        setUiActions( new Actions() ) ;
        setProjectManager( new ProjectManager() ) ;
        setWordRepository( new WordRepository() ) ;
        
        // Configure the system components
        ConfiguratorBuilder builder = new ConfiguratorBuilder( APP_ID, cmdLine ) ;
        Configurator configurator = builder.createConfigurator() ;
        configurator.initialize() ;
    }
    
    private void postInitialize() throws Exception {
        initializeStateManager() ;
    }
    
    private void initializeStateManager() throws Exception {
        
        StateManager stateManager = new StateManager( this, getWkspManager() ) ;
        setStateMgr( stateManager ) ;
        
        stateManager.registerObject( "ProjectManager", getProjectManager() ) ;
        stateManager.initialize() ;
        stateManager.loadState() ;
        
    }
    
    private void setUpAndShowMainFrame() throws Exception {
        
        MainFrame mainFrame = new MainFrame() ;
        mainFrame.setUp() ;
        setMainFrame( mainFrame ) ;
        mainFrame.setVisible( true ) ;
    }
    
    public static void main( String[] args ) {
        
        logger.info( "Starting JoveNotesMaker application" ) ;
        JoveNotesMaker app = new JoveNotesMaker() ;
        try {
            showSplashScreen() ;
            setApp( app ) ;
            app.launch( args ) ;
        }
        catch( Exception e ) {
            logger.error( "JoveNotesMaker exitted with an exception", e ) ;
        }
    }
    
    private static void showSplashScreen() throws Exception {
        final SplashScreen splash = SplashScreen.getSplashScreen() ;
        if( splash != null ) {
            Thread.sleep( 2000 ) ;
            splash.close() ;
        }
    }
}
