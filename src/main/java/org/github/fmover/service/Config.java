package org.github.fmover.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author iamsinghankit
 */
public class Config {

    private static final String DEFAULT_CONFIG = ".fmover.properties";
    private static final String COMMENTS = """
            This is a config file for fmover, key and value pair represents directory_path=file_extensions 
            """;
    private Properties prop;

    public Config(String file) throws IOException {
        Logger.log(() -> "Loading " + file);
        prop = load(file);
    }


    public Config() throws IOException {
        String path = System.getProperty("user.home") + File.separator + DEFAULT_CONFIG;
        Logger.log(() -> "Loading config " + path);
        try {
            prop = load(path);
        } catch (IOException ex) {
            Logger.log(() -> "Error while loading config :" + ex.getMessage() + ", creating new config");
            prop = new Properties();
            putEntry();
        }
        FileOutputStream outputStrem = new FileOutputStream(path);
        prop.store(outputStrem, COMMENTS);
    }

    private Properties load(String file) throws IOException {
        Properties prop = new Properties();
        prop.load(new FileInputStream(file));
        return prop;
    }

    public Properties get() {
        return prop;
    }


    private void putEntry() {
        prop.put("Images", "png,jpeg,jpg,heic");
        prop.put("Document", "txt,pdf,xlsx,xls,ppt,pptx,doc,docx");
        prop.put("Video", "mkv,mp4");
        prop.put("Compressed", "rar,jar,war,zip,tar");
        prop.put("Application", "dmg,app,exe,deb,rpm");
    }


}
