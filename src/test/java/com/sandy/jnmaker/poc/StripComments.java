package com.sandy.jnmaker.poc;

public class StripComments {

    public static void main( String[] args ) {
        String sourceCode =
                "/*\n"
               + " * Multi-line comment\n"
               + " * Creates a new Object.\n"
               + " */\n"
               + "public Object someFunction() {\n"
               + " // single line comment\n"
               + " Object obj =  new Object();\n"
               + " return obj; /* single-line comment */\n"
               + "}";

       System.out.println(sourceCode.replaceAll(
               "//.*|/\\*((.|\\n)(?!=*/))+\\*/", ""));    }

}
