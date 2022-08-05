package com.github.tatercertified;

import com.github.tatercertified.gui.MainGUI;
import net.fabricmc.installer.util.MetaHandler;
import net.fabricmc.installer.util.Reference;

import java.io.IOException;

public class Main {

    public static MetaHandler LOADER_META = new MetaHandler(Reference.getMetaServerEndpoint("v2/versions/loader"));
    public static MetaHandler GAME_VERSION_META = new MetaHandler(Reference.getMetaServerEndpoint("v2/versions/game"));

    public static void main(String[] args) throws IOException {
        GAME_VERSION_META.load();
        LOADER_META.load();
        MainGUI.LaunchGUI();
    }
}