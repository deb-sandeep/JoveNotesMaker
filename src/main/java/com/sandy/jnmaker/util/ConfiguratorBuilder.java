package com.sandy.jnmaker.util;

import static com.sandy.common.util.ReflectionUtil.getResource ;
import static com.sandy.jnmaker.util.ObjectRepository.getApp;
import static com.sandy.jnmaker.util.ObjectRepository.getAppConfig;
import static com.sandy.jnmaker.util.ObjectRepository.getIndexingDaemon;
import static com.sandy.jnmaker.util.ObjectRepository.getWordnicAdapter;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

import com.sandy.common.util.Configurator ;

public class ConfiguratorBuilder {
	
	static Logger log = Logger.getLogger( ConfiguratorBuilder.class ) ;

    private String              appId      = null ;
    private JNMCommandLine      cmdLine    = null ;
    
    public ConfiguratorBuilder( String appId,
                                JNMCommandLine cmdLine ) {
        this.appId      = appId ;
        this.cmdLine    = cmdLine ;
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

        // Secondly load the bundled properties if any. Bundled properties are
        // first loaded from the classpath and then from the user home 
        // directory/appId folder. The user home/appId folder properties
        // overwrite any classpath properties.
        String defPropPath = "/" + appId + "-config.properties";
        URL defPropURL = getResource( defPropPath );
        if( defPropURL != null ) {
            configurator.registerConfigResourceURL( defPropURL );
        }
        
        // Lastly we check in the workspace directory
        loadLocalPropertiesIfAny( configurator ) ;
    }
    
    private void loadLocalPropertiesIfAny( Configurator configurator ) {
    	
        String userHomePath = System.getProperty( "user.home" ) ;
        File   userHomeAppDir = new File( userHomePath, appId ) ;
        
        if( userHomeAppDir.exists() ) {
        	
        	File localProp = new File( userHomeAppDir, appId + "-config.properties" ) ;
        	if( localProp.exists() ) {
        		URL url = null ;
        		try {
					url = localProp.toURI().toURL() ;
					configurator.registerConfigResourceURL( url );
				} 
        		catch ( MalformedURLException e ) {
        			log.error( "Could not load local properties.", e ) ;
				}
        	}
        }
        else {
        	userHomeAppDir.mkdirs() ;
        }
    }
}
