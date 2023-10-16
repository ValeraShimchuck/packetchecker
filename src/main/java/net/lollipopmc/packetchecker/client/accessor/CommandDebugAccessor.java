package net.lollipopmc.packetchecker.client.accessor;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CommandDebugAccessor {

    private static final Set<String> ADDED_COMMAND = ConcurrentHashMap.newKeySet();

    public static void addCommand(String command) {
        ADDED_COMMAND.add(command);
    }

    public static void printCommand() {
        if (ADDED_COMMAND.isEmpty()) return;
        System.out.println(ADDED_COMMAND);
        ADDED_COMMAND.clear();
    }


}
