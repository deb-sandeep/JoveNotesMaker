package com.sandy.jnmaker.junit.ui.helper.seqgen;

import static org.junit.Assert.assertEquals ;

import org.junit.Test ;

import com.sandy.jnmaker.ui.helper.seqgen.SequenceGenerator ;
import com.sandy.jnmaker.ui.helper.seqgen.Sequencer ;

public class SequencerTestCase {

    private SequenceGenerator seqGen = null ;
    
    @Test
    public void numericSeqGeneration() {
        seqGen = Sequencer.identifySequence( "fig_1.2.3" ) ;
        assertEquals( "fig_1.2.4",  seqGen.getNextSequence() );
    }
    
    @Test
    public void smallLCharSeqGeneration() {
        seqGen = Sequencer.identifySequence( "fig_1.2.3a" ) ;
        assertEquals( "fig_1.2.3b",  seqGen.getNextSequence() );
    }
    
    @Test
    public void bigLCharSeqGeneration() {
        seqGen = Sequencer.identifySequence( "fig_1.2.3F" ) ;
        assertEquals( "fig_1.2.3G",  seqGen.getNextSequence() );
    }
    
    @Test
    public void QASeqGeneration() {
        seqGen = Sequencer.identifySequence( "ex_1Ans" ) ;
        assertEquals( "ex_2",  seqGen.getNextSequence() ) ;
        assertEquals( "ex_2Ans", seqGen.getNextSequence() ) ;
        assertEquals( "ex_3",  seqGen.getNextSequence() ) ;
    }
}
