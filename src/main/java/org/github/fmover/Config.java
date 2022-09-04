package org.github.fmover;

import org.github.fmover.service.Logger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author iamsinghankit
 */
@SuppressWarnings("unchecked")
public class Config<K, V> {

    private final Properties prop;

    public Config(Path file,String comments) throws IOException {
        String path = file.toFile().getAbsolutePath();
        prop = load(path);
        var outputStream = new FileOutputStream(path);
        prop.store(outputStream, comments);
    }

    public Config(Path file, String comments,Map<String, String> defaultEntry) throws IOException {
        String path = file.toFile().getAbsolutePath();
        prop = load(path);
        if (prop.isEmpty()) {
            prop.putAll(defaultEntry);
        }
        var outputStream = new FileOutputStream(path);
        prop.store(outputStream, comments);
    }

    private Properties load(String file) throws IOException {
        Logger.log(() -> "Loading config " + file);
        var prop = new Properties();
        prop.load(new FileInputStream(file));
        return prop;
    }

    public List<ConfigMapping<K, V>> getMapping(Function<String, K> keyMapper) {
        return getMapping(keyMapper, stringMapper());
    }

    public List<ConfigMapping<K, V>> getMapping(Function<String, K> keyMapper, Function<String, V> valueMapper) {
        var entries = prop.entrySet();
        var mappings = new ArrayList<ConfigMapping<K, V>>(entries.size());
        for (var entry : entries) {
            var values = Stream.of(entry.getValue().toString().split(",")).map(valueMapper).toList();
            var mapping = new ConfigMapping<K, V>(keyMapper.apply(entry.getKey().toString()), values);
            mappings.add(mapping);
        }
        return mappings;
    }

    public List<ConfigMapping<K, V>> getMapping() {
        return getMapping((Function<String, K>) stringMapper(), stringMapper());
    }

    private Function<String, V> stringMapper() {
        Function<String, String> mapper = String::valueOf;
        return (Function<String, V>) mapper;
    }




    public record ConfigMapping<K, V>(K key, List<V> value) {

        public boolean isValueExists(String valueToCheck) {
            return value.stream().anyMatch(e -> e.equals(valueToCheck));
        }
    }
}
