package com.sandy.jnmaker.tools ;

import com.sandy.jnmaker.tools.ToolConfigDialog.UserChoice ;

public abstract class AbstractTool {
    
    private String  displayName     = "Abstract Tool" ;
    private boolean freezeMainFrame = false ;
    
    private ToolConfigDialog configDialog            = null ;
    private boolean          configDialogInitialized = false ;
    
    public AbstractTool( String name ) {
        this( name, true ) ;
    }
    
    public AbstractTool( String name, boolean freezeMainFrame ) {
        this.displayName     = name ;
        this.freezeMainFrame = freezeMainFrame ;
    }
    
    public String getDisplayName() {
        return this.displayName ;
    }
    
    public boolean shouldFreezeMainFrame() {
        return this.freezeMainFrame ;
    }
    
    private ToolConfigDialog getConfigDialog() {
        if( !configDialogInitialized ) {
            AbstractToolConfigPanel centerPanel = null ;
            centerPanel = getConfigPanel() ;
            if( centerPanel != null ) {
                configDialog = new ToolConfigDialog( centerPanel ) ;
            }
            configDialogInitialized = true ;
        }
        return configDialog ;
    }
    
    public final void execute() throws Exception {
        ToolConfigDialog configDialog = getConfigDialog() ;
        UserChoice userChoice = UserChoice.OK ;
        if( configDialog != null ) {
            userChoice = configDialog.showDialog() ;
            if( userChoice == UserChoice.CANCEL ) {
                return ;
            }
        }
        executeTool() ;
    }
    
    protected abstract void executeTool() throws Exception ;
    
    protected AbstractToolConfigPanel getConfigPanel() {
        return null ;
    }
}
