package net.lollipopmc.packetchecker.mapping;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.BlockEventS2CPacket;

import java.io.*;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MinecraftClassMappings {

    private static final Map<Class<?>, String> MAPPINGS;


    public static String deObfuscateClassName(Class<?> clazz) {
        String name = MAPPINGS.get(clazz);
        if (name == null) throw new NullPointerException("class " + clazz.getName() + " does not exist in mappings");
        return name;
    }

    public static String deObfuscateClassSimple(Class<?> clazz) {
        String fullName = deObfuscateClassName(clazz);
        return getSimpleName(fullName);
    }

    public static Map<Class<?>, String> getMapByDeObfuscatedPackage(String packageName) {
        return MAPPINGS.entrySet().stream()
                .filter(entry -> entry.getValue().startsWith(packageName))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static Map<Class<?>, String> getSimpleNameMapByDeObfuscatedPackage(String packageName) {
        return getMapByDeObfuscatedPackage(packageName).entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), getSimpleName(entry.getValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static String getSimpleName(String fullName) {
        String[] separatedNamePieces = fullName.split("\\.");
        return separatedNamePieces[separatedNamePieces.length-1];
    }

    static {
        JsonObject json = new Gson()
                .fromJson(
                        new InputStreamReader(MinecraftClassMappings.class
                                .getClassLoader().getResourceAsStream("mappings.json")),
                        JsonObject.class
                        );
        Map<Class<?>, String> mappings = new HashMap<>();
        AtomicInteger notLoadedCount = new AtomicInteger();
        json.entrySet().forEach(entry -> {
            String obfuscated  = entry.getKey().replace('/', '.');
            String deObfuscated = entry.getValue().getAsString().replace('/', '.');
            //if (deObfuscated.contains("net.minecraft.client.gl") ) {
            //    String regex = "net\\.minecraft\\.client\\.gl.*";
            //    System.out.println("check class " + obfuscated + " / "  + deObfuscated);
            //    System.out.println("match: " + deObfuscated.matches(regex));
            //}
            if (!toLoad(obfuscated) || !toLoadDeobfuscated(deObfuscated)) {
                System.out.println("skip " + obfuscated + "/" + deObfuscated + " due to filter");
                return;
            }
            try {
                mappings.put(Class.forName(obfuscated), deObfuscated);
            } catch (ExceptionInInitializerError e) {
                notLoadedCount.incrementAndGet();
            } catch (Throwable e) {
                //System.out.println("can't find " + obfuscated);
                notLoadedCount.incrementAndGet();
            }
        });
        System.out.println("can't load: " + notLoadedCount.get());
        MAPPINGS = Map.copyOf(mappings);
    }

    private static boolean toLoad(String obfuscated) {
        return Stream.of(
                "net.minecraft.class_310$class_5859",
                "net.minecraft.class_310",
                "net.minecraft.class_310$1",
                "net.minecraft.class_310$class_5859$1",
                "net.minecraft.class_310$class_5859$2",
                "net.minecraft.class_310$class_5859$3",
                "net.minecraft.class_310$class_5859$4",
                "net.minecraft.class_7168",
                "net.minecraft.class_7168$class_7170"
        ).noneMatch(str -> str.equals(obfuscated));
    }

    private static boolean toLoadDeobfuscated(String deobfuscated) {
        return Stream.of(
                //"net\\.minecraft\\.client\\.render.*",
                "net\\.minecraft\\.client\\.gl.*"
        ).noneMatch(deobfuscated::matches);
    }

}
