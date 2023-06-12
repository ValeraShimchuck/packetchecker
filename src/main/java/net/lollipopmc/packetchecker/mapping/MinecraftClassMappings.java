package net.lollipopmc.packetchecker.mapping;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.BlockEventS2CPacket;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public class MinecraftClassMappings {

    private static final Map<String, String> MAPPINGS;
    private static final Map<String, String> SORTED_MAP;
    private static final Map<String, String> SIMPLE_NAME_SORTED_MAP;


    public static String deObfuscateClassName(Class<?> clazz) {
        String name = MAPPINGS.get(clazz.getName());
        if (name == null) throw new NullPointerException("class " + clazz.getName() + " does not exist in mappings");
        return name;
    }

    public static String deObfuscateClassSimple(Class<?> clazz) {
        return getSimpleName(deObfuscateClassName(clazz));
    }

    public static Map<Class<?>, String> getMapByDeObfuscatedPackage(String packageName) {
        return MAPPINGS.entrySet().stream()
                .filter(entry -> entry.getValue().startsWith(packageName))
                .map(entry -> {
                    try {
                        return Map.entry(Class.forName(entry.getKey()), entry.getValue());
                    } catch (ClassNotFoundException e) {
                        System.err.println("could not find " + entry.getKey() + "deobf: " + entry.getValue());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static Map<Class<?>, String> getSimpleNameMapByDeObfuscatedPackage(String packageName) {
        return getMapByDeObfuscatedPackage(packageName).entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), getSimpleName(entry.getValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static String getSimpleName(String fullName) {
        String[] separatedNamePieces = fullName.split("\\.");
        return separatedNamePieces[separatedNamePieces.length - 1];
    }

    public static Map<String, String> getMappings() {
        synchronized (SORTED_MAP) {
            return new LinkedHashMap<>(SORTED_MAP);
        }
    }

    public static Map<String, String> getSimpleNameMappings() {
        synchronized (SIMPLE_NAME_SORTED_MAP) {
            return new LinkedHashMap<>(SIMPLE_NAME_SORTED_MAP);
        }
    }


    static {
        JsonObject json = new Gson()
                .fromJson(
                        new InputStreamReader(MinecraftClassMappings.class
                                .getClassLoader().getResourceAsStream("mappings.json")),
                        JsonObject.class
                );
        Map<String, String> mappings = new HashMap<>();
        AtomicInteger notLoadedCount = new AtomicInteger();
        json.entrySet().forEach(entry -> {
            String obfuscated = entry.getKey().replace('/', '.');
            String deObfuscated = entry.getValue().getAsString().replace('/', '.');
            try {
                mappings.put(obfuscated, deObfuscated);
            } catch (Throwable e) {
                //System.out.println("can't find " + obfuscated);
                notLoadedCount.incrementAndGet();
            }
        });
        System.out.println("can't load: " + notLoadedCount.get());
        MAPPINGS = Map.copyOf(mappings);
        SORTED_MAP = MAPPINGS.entrySet().stream()
                .sorted(Comparator.comparing(entry -> -entry.getKey().length()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (v1, v2) -> {throw new RuntimeException("duplicate values");},
                        LinkedHashMap::new
                ));
        SIMPLE_NAME_SORTED_MAP = SORTED_MAP.entrySet().stream()
                .map(entry -> Map.entry(getSimpleName(entry.getKey()), getSimpleName(entry.getValue())))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (v1, v2) -> v1,
                        LinkedHashMap::new
                ));
    }

}
