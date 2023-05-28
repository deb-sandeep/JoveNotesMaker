package com.sandy.jnmaker.junit.ui.panels.rawtxt;

import com.sandy.jnmaker.ui.panels.rawtxt.RawTextParser;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class RawTextParserTest {

    private static final Logger log = Logger.getLogger( RawTextParserTest.class ) ;

    private static final String JN_CONTENT_PATH =
            "/com/sandy/jnmaker/junit/ui/panels/rawtxt/sample.jn-ocr";

    @Test
    public void test() throws IOException {
        InputStream is = getClass().getResourceAsStream( JN_CONTENT_PATH ) ;
        assertThat( is, is( notNullValue() ) ) ;

        String fileContent = IOUtils.toString( is ) ;
        assertThat( fileContent, is( notNullValue() ) ) ;

        RawTextParser parser = new RawTextParser( fileContent ) ;
        List<String> metaNotes = parser.getParsedMetaNotes() ;
        assertThat( metaNotes.isEmpty(), is( false ) ) ;


        for( String metaNote : metaNotes ) {
            log.error( "\n---------------------------------------------------\n" ) ;
            log.debug( metaNote ) ;
        }
    }
}
