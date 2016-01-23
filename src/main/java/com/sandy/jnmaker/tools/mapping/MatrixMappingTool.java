package com.sandy.jnmaker.tools.mapping;

import com.sandy.jnmaker.tools.AbstractTool ;
import com.sandy.jnmaker.tools.AbstractToolConfigPanel ;

public class MatrixMappingTool extends AbstractTool {
    
    private MappingToolPanel configPanel = null ;
    
    public MatrixMappingTool() {
        super( "Matrix Mapping Tool" ) ;
    }
    
    @Override
    protected AbstractToolConfigPanel getConfigPanel() {
        if( configPanel == null ) {
            configPanel = new MappingToolPanel() ;
        }
        return configPanel ;
    }

    @Override
    protected void executeTool() throws Exception {
    }
}