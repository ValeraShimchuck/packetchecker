package net.lollipopmc.packetchecker.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.ClientConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

@Environment(EnvType.CLIENT)
public class PacketCheckerClient implements ClientModInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PacketCheckerClient.class);

    @Override
    public void onInitializeClient() {
        LOGGER.info(Item.getRawId(Items.PLAYER_HEAD) + "");
    }

}
