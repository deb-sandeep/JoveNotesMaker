package com.sandy.jnmaker.util;

import com.sandy.common.bus.EventBus ;
import com.sandy.common.objfactory.SpringObjectFactory ;
import com.sandy.common.util.StateManager ;
import com.sandy.common.util.WorkspaceManager ;
import com.sandy.jnmaker.JoveNotesMaker ;
import com.sandy.jnmaker.ui.MainFrame ;

public class ObjectRepository {

    private static SpringObjectFactory objFactory = null ;
    private static WorkspaceManager    wkspMgr    = null ;
    private static EventBus            bus        = null ;
    private static MainFrame           mainFrame  = null ;
    private static JoveNotesMaker      app        = null ;
    private static StateManager        stateMgr   = null ;
    
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
}
