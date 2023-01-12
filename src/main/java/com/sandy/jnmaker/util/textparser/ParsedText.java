package com.sandy.jnmaker.util.textparser;

import java.util.ArrayList ;
import java.util.List ;

import lombok.Data ;

@Data
public class ParsedText {

    private List<TextComponent> components = new ArrayList<>() ;
    private TextComponent currentComponent = null ;
    
    void addComponent( TextComponent component ) {
        
        assert component != null ;
        
        TextComponent tail = null ;
        if( !components.isEmpty() ) {
            tail = components.get( components.size()-1 ) ;
        }
        
        if( tail != null ) {
            tail.setNext( component ) ;
        }
        component.setPrev( tail ) ;
        component.setParsedText( this ) ;
        
        components.add( component ) ;
    }
}
