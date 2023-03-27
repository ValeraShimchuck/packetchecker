package net.lollipopmc.packetchecker.client;

import com.google.gson.JsonObject;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket;
import net.minecraft.registry.Registries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HexFormat;

@Environment(EnvType.CLIENT)
public class PacketCheckerClient implements ClientModInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PacketCheckerClient.class);

    @Override
    public void onInitializeClient() {
        LOGGER.info(Item.getRawId(Items.PLAYER_HEAD) + "");
        JsonObject jsonObject = new JsonObject();
        Registries.ITEM.stream().forEach(item -> {
            int id = Registries.ITEM.getRawId(item);
            String namespaceValue = Registries.ITEM.getId(item).getPath();
            jsonObject.addProperty(id + "", namespaceValue);
        });
        String json = jsonObject.toString();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("761.json"));
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
