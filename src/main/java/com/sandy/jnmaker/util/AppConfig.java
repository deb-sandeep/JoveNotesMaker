package com.sandy.jnmaker.util;

import java.io.File ;

import lombok.Data ;

@Data
public class AppConfig {

    private File joveNotesMediaDir = null ;
    private File workspaceDir = null ;
    private File jeeImageNameSaveFile = null ;
    private String imagePanelType = null ;
}
