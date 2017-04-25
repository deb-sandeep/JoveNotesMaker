package com.sandy.jnmaker.util;

import static com.sandy.common.util.ReflectionUtil.getResource ;
import static com.sandy.jnmaker.util.ObjectRepository.* ;

import java.net.URL;

import com.sandy.common.util.Configurator ;
import com.sandy.common.util.WorkspaceManager ;

public class ConfiguratorBuilder {

    private String              appId      = null ;
    private JNMCommandLine      cmdLine    = null ;
    private WorkspaceManager    wkspUtil   = null ;
    
    public ConfiguratorBuilder( String appId,
                                JNMCommandLine cmdLine ) {
        this.appId      = appId ;
        this.cmdLine    = cmdLine ;
        this.wkspUtil   = getWkspManager() ;
    }
    
    public Configurator createConfigurator()
        throws Exception {

        Configurator configurator = new Configurator();
        configurator.setCommandLine( cmdLine );

        registerConfigurableObjects( configurator ) ;
        registerConfigProperties( configurator ) ;

        return configurator;
    }
    
    private void registerConfigurableObjects( Configurator configurator ) 
        throws Exception {
        
        configurator.registerConfigurableObject( "JoveNotesMaker", getApp() ) ;
        configurator.registerConfigurableObject( "WordnicAdapter", getWordnicAdapter() ) ;
        configurator.registerConfigurableObject( "AppConfig",      getAppConfig() ) ;
        configurator.registerConfigurableObject( "IndexingDaemon", getIndexingDaemon() ) ;
    }
    
    private void registerConfigProperties( Configurator configurator ) {
        
        // First of all load the command line configurator properties
        String clpPropPath = "/com/sandy/jnmaker/clp-configurator.properties";
        URL clpConfigURL = getResource( clpPropPath ) ;
        
        configurator.registerConfigResourceURL( clpConfigURL );

        // Secondly load the bundled properties if any. Bundled property is
        // stored in a folder accessible to the classpath
        String defPropPath = "/" + appId + "-config.properties";
        URL defPropURL = getResource( defPropPath );
        if( defPropURL != null ) {
            configurator.registerConfigResourceURL( defPropURL );
        }

        // Lastly we check in the workspace directory
        URL userPropURL = wkspUtil.getFileURL( appId + "-config.properties" );
        if( userPropURL != null ) {
            configurator.registerConfigResourceURL( userPropURL );
        }
    }
}
