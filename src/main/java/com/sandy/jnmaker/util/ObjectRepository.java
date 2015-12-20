package com.sandy.jnmaker.util;

import com.sandy.common.objfactory.SpringObjectFactory ;
import com.sandy.common.util.WorkspaceManager ;

public class ObjectRepository {

    private static SpringObjectFactory objFactory = null ;
    private static WorkspaceManager    wkspMgr    = null ;
    
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
}
