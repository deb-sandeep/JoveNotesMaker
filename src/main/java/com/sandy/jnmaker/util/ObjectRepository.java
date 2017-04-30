package com.sandy.jnmaker.util;

import com.sandy.common.bus.EventBus ;
import com.sandy.common.objfactory.SpringObjectFactory ;
import com.sandy.common.util.StateManager ;
import com.sandy.common.util.WorkspaceManager ;
import com.sandy.jnmaker.JoveNotesMaker ;
import com.sandy.jnmaker.lucene.indexer.IndexingDaemon ;
import com.sandy.jnmaker.ui.MainFrame ;
import com.sandy.jnmaker.ui.actions.Actions ;
import com.sandy.jnmaker.ui.helper.ProjectManager ;
import com.sandy.jnmaker.ui.notedialogs.NotesCreatorDialog ;

public class ObjectRepository {

    private static SpringObjectFactory objFactory     = null ;
    private static WorkspaceManager    wkspMgr        = null ;
    private static EventBus            bus            = null ;
    private static MainFrame           mainFrame      = null ;
    private static JoveNotesMaker      app            = null ;
    private static StateManager        stateMgr       = null ;
    private static WordnicAdapter      wordnicAdapter = null ;
    private static AppConfig           appConfig      = null ;
    private static Actions             uiActions      = null ;
    private static ProjectManager      projectManager = null ;
    private static WordRepository      wordRepository = null ;
    private static NotesCreatorDialog  curNotesDialog = null ;
    private static IndexingDaemon      indexingDaemon = null ;
    
    public static NotesCreatorDialog getCurNotesDialog() {
        return curNotesDialog;
    }

    public static void setCurNotesDialog( NotesCreatorDialog curNotesDialog ) {
        ObjectRepository.curNotesDialog = curNotesDialog;
    }

    public static WordRepository getWordRepository() {
        return wordRepository;
    }

    public static void setWordRepository( WordRepository wordRepository ) {
        ObjectRepository.wordRepository = wordRepository;
    }

    public static ProjectManager getProjectManager() {
        return projectManager;
    }

    public static void setProjectManager( ProjectManager projectManager ) {
        ObjectRepository.projectManager = projectManager;
    }

    public static Actions getUiActions() {
        return uiActions;
    }

    public static void setUiActions( Actions uiActions ) {
        ObjectRepository.uiActions = uiActions;
    }

    public static AppConfig getAppConfig() {
        return appConfig;
    }

    public static void setAppConfig( AppConfig appConfig ) {
        ObjectRepository.appConfig = appConfig;
    }

    public static WordnicAdapter getWordnicAdapter() {
        return wordnicAdapter;
    }

    public static void setWordnicAdapter( WordnicAdapter wordnicAdapter ) {
        ObjectRepository.wordnicAdapter = wordnicAdapter;
    }

    public static StateManager getStateMgr() {
        return stateMgr;
    }

    public static void setStateMgr( StateManager stateMgr ) {
        ObjectRepository.stateMgr = stateMgr;
    }

    public static JoveNotesMaker getApp() {
        return app;
    }

    public static void setApp( JoveNotesMaker app ) {
        ObjectRepository.app = app;
    }

    public static MainFrame getMainFrame() {
        return mainFrame;
    }

    public static void setMainFrame( MainFrame mainFrame ) {
        ObjectRepository.mainFrame = mainFrame;
    }

    public static void setObjectFactory( SpringObjectFactory obj ) {
        objFactory = obj ;
    }
    
    public static SpringObjectFactory getObjectFactory() {
        return objFactory ;
    }
    
    public static void setWkspManager( WorkspaceManager obj ) {
        wkspMgr = obj ;
    }
    
    public static WorkspaceManager getWkspManager() {
        return wkspMgr ;
    }
    
    public static void setBus( EventBus obj ) {
        bus = obj ;
    }
    
    public static EventBus getBus() {
        return bus ;
    }
    
    public static void setIndexingDaemon( IndexingDaemon daemon ) {
        indexingDaemon = daemon ;
    }
    
    public static IndexingDaemon getIndexingDaemon() {
        return indexingDaemon ;
    }
}
