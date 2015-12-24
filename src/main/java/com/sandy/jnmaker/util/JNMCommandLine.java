package com.sandy.jnmaker.util;

import com.sandy.common.util.AbstractCLParser ;

public class JNMCommandLine extends AbstractCLParser {
    
    private String wordnicKey = null ;

    public String getWordnicKey() {
        return wordnicKey;
    }

    public void setWordnicKey( String wordnicKey ) {
        this.wordnicKey = wordnicKey;
    }

    @Override
    protected void prepareOptions( OptionCfgCollection options ) {
        options.addOption( "w", "wordnicKey", true, true, "The wordnic API Key" ) ;
    }

    @Override
    protected String getUsageString() {
        return "JoveNotesMaker [options]" ;
    }
}
