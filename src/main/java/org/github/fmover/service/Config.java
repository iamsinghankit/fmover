package org.github.fmover.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * @author iamsinghankit
 */
public class Config {

    private static final String COMMENTS = """
            This is a config file for fmover, key and value pair represents directory_path=file_extensions 
            """;
    private final Path baseDir;
    private final Properties prop;


    public Config(Path file, Path baseDir) throws IOException {
        this.baseDir = baseDir;
        String path = file.toFile().getAbsolutePath();
        Logger.log(() -> "Loading config " + path);
        prop = load(path);
        if (prop.isEmpty()) {
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

    public List<DirFileMapping> get() {
        Set<Map.Entry<Object, Object>> entries = prop.entrySet();
        List<DirFileMapping> mappings = new ArrayList<>(entries.size());
        for (Map.Entry<Object, Object> entry : entries) {
            var mapping = new DirFileMapping(getBaseDir(entry.getKey().toString()), List.of(entry.getValue().toString().split(",")));
            mappings.add(mapping);
        }
        return mappings;
    }


    private Path getBaseDir(String ext) {
        if (baseDir == null) {
            return Path.of(ext + File.separator);
        }
        String base = baseDir.toFile().getAbsolutePath() + File.separator;
        return Path.of(base + ext + File.separator);
    }


    private void putEntry() {
        prop.put("Images", "png,jpeg,jpg,heic");
        prop.put("Document", "txt,pdf,xlsx,xls,ppt,pptx,doc,docx");
        prop.put("Video", "mkv,mp4");
        prop.put("Compressed", "rar,jar,war,zip,tar");
        prop.put("Application", "dmg,app,exe,deb,rpm");
    }

    public record DirFileMapping(Path dir, List<String> ext) {

        public boolean checkExt(String actualExt) {
            return ext.stream().anyMatch(e -> e.equalsIgnoreCase(actualExt));
        }
    }
}
