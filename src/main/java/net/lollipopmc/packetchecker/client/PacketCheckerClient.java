package net.lollipopmc.packetchecker.client;

import com.google.gson.JsonObject;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.lollipopmc.packetchecker.mapping.MinecraftClassMappings;
import net.minecraft.SharedConstants;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket;
import net.minecraft.util.registry.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class PacketCheckerClient implements ClientModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger(PacketCheckerClient.class);
    public static final Map<Class<?>, String> CLIENT_PACKET_MAPPINGS;
    public static final Map<Class<?>, String> SERVER_PACKET_MAPPINGS;
    private static final Pattern PACKET_NAME_PATTERN = Pattern.compile("(.*)(S2CPacket|C2SPacket)(\\$(.*))?");
    private static final Pattern CAMEL_CASE_SEPARATOR = Pattern.compile("([A-Z][a-z]+)");

    @Override
    public void onInitializeClient() {
        LOGGER.info(Item.getRawId(Items.PLAYER_HEAD) + "");
        JsonObject jsonObject = new JsonObject();
        Registry.ITEM.stream().forEach(item -> {
            int id = Registry.ITEM.getRawId(item);
            String namespaceValue = Registry.ITEM.getId(item).getPath();
            jsonObject.addProperty(id + "", namespaceValue);
        });
        String json = jsonObject.toString();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(SharedConstants.getProtocolVersion() + ".json"));
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Map<NetworkState, Map<NetworkSide, Map<String, Integer>>> playersPacketMap = new HashMap<>();
        Arrays.stream(NetworkState.values()).forEach(networkState -> {
            Map<NetworkSide, Map<String, Integer>> networkSideMap = new HashMap<>();
            playersPacketMap.put(networkState, networkSideMap);
            try {
                Field mapField = Arrays.stream(networkState.getClass().getDeclaredFields())
                        .filter(field ->
                                !Modifier.isStatic(field.getModifiers()) &&
                                        field.getType().isAssignableFrom(Map.class))
                        .findFirst().orElseThrow();
                mapField.setAccessible(true);
                Map<NetworkSide, Object> packetSideHandlers = (Map<NetworkSide, Object>) mapField.get(networkState);
                packetSideHandlers.forEach((side, handler) -> {
                    Class<?> packetHandlerClass = handler.getClass();
                    try {

                        Field packetsMapField = Arrays.stream(packetHandlerClass.getDeclaredFields())
                                .filter(field -> field.getType().isAssignableFrom(Object2IntMap.class))
                                .findFirst()
                                .orElseThrow();
                        packetsMapField.setAccessible(true);
                        Object2IntMap<Class<?>> packetsMap = (Object2IntMap<Class<?>>) packetsMapField.get(handler);
                        networkSideMap.put(side, packetsMap.object2IntEntrySet().stream()
                                .map(entry -> Map.entry(
                                        extractCleanPacketName(MinecraftClassMappings.deObfuscateClassSimple(entry.getKey())),
                                        entry.getIntValue()
                                ))
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        savePacketsToFile(toJson(playersPacketMap));
    }

    private String extractCleanPacketName(String packetName) {
        Matcher matcher = PACKET_NAME_PATTERN.matcher(packetName);
        if (!matcher.find()) throw new IllegalArgumentException("packet doesn't contain pattern");
        String name = matcher.group(1);
        String subClass = matcher.group(4);
        if (subClass != null) name = name + subClass;
        return formatToUppercase(name);
    }

    private String formatToUppercase(String text) {
        Matcher matcher = CAMEL_CASE_SEPARATOR.matcher(text);
        List<String> strings = new ArrayList<>();
        while (matcher.find()) strings.add(matcher.group());
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strings.size(); i++) {
            String element = strings.get(i);
            boolean isLast = i + 1 == strings.size();
            sb.append(element.toUpperCase());
            if (!isLast) sb.append("_");
        }
        return sb.toString();
    }

    private JsonObject toJson(Map<NetworkState, Map<NetworkSide, Map<String, Integer>>> playersPacketMap) {
        JsonObject jsonObject = new JsonObject();
        playersPacketMap.forEach((state, packetSide) -> {
            JsonObject sideJson = new JsonObject();
            packetSide.forEach((side, packets) -> {
                JsonObject packetsJson = new JsonObject();
                packets.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue())
                        .forEachOrdered(entry -> {
                            packetsJson.add(entry.getKey(), new JsonPrimitive(entry.getValue()));
                        });
                sideJson.add(side.toString().toLowerCase(), packetsJson);
            });
            jsonObject.add(state.name().toLowerCase(), sideJson);
        });
        return jsonObject;
    }

    private void savePacketsToFile(JsonObject jsonObject) {
        File file = new File(SharedConstants.getProtocolVersion() + "-packets.json");
        try (FileWriter outputStream = new FileWriter(file)) {
            new Gson().toJson(jsonObject, outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<Class<?>, String> getDeobfuscatedNames(String packageName) {
        return MinecraftClassMappings.getSimpleNameMapByDeObfuscatedPackage(packageName);
    }

    static {
        CLIENT_PACKET_MAPPINGS = getDeobfuscatedNames("net.minecraft.network.packet.c2s");
        SERVER_PACKET_MAPPINGS = getDeobfuscatedNames("net.minecraft.network.packet.s2c");
    }
    private static <K, V> Map.Entry<K, V> entry(K k, V v) {
        return Map.entry(k, v);
    }

}
