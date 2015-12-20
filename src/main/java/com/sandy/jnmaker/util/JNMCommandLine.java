package com.sandy.jnmaker.util;

import com.sandy.common.util.AbstractCLParser ;

public class JNMCommandLine extends AbstractCLParser {

    @Override
    protected void prepareOptions( OptionCfgCollection options ) {
    }

    @Override
    protected String getUsageString() {
        return "JoveNotesMaker [options]" ;
    }
}
