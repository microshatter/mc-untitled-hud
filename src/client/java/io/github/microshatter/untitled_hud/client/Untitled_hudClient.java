package io.github.microshatter.untitled_hud.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;

public class Untitled_hudClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HudElementRegistry.addLast(RenderOverlay.UNTITLED_GUI, new RenderOverlay());
    }
}
