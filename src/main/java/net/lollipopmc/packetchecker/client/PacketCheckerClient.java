package net.lollipopmc.packetchecker.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.s2c.play.MapUpdateS2CPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HexFormat;

@Environment(EnvType.CLIENT)
public class PacketCheckerClient implements ClientModInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PacketCheckerClient.class);

    @Override
    public void onInitializeClient() {
        LOGGER.info(NetworkState.PLAY.getPacketId(NetworkSide.CLIENTBOUND, new MapUpdateS2CPacket(0, (byte) 0, false, null, null)) + "");

    }

}
