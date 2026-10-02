package io.github.microshatter.untitled_hud.client;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.client.Minecraft;
import org.jspecify.annotations.NonNull;

public class RenderOverlay implements HudElement {
    private Minecraft mc;
    public static final Identifier UNTITLED_GUI = Identifier.fromNamespaceAndPath("untitled_hud", "untitled_hud");

    private int main_color = 0xffa6e3a1;
    private int danger_color = 0xfff38ba8;
    private int damaged_color = 0xffeba0ac;

    private float maxHealth;
    private float health;
    private float healthPercentage;
    private float healthWidth;
    private float absorptionAmount;
    private float absorptionPercentage;
    private float absorptionWidth;
    String playerName;

    // Layout
    private int margin = 10;
    private int healthBarWidth = 100;
    private int healthBarHeight = 5;
    private int layoutPosY;

    public RenderOverlay() {
        this.mc = Minecraft.getInstance();
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, @NonNull DeltaTracker deltaTracker) {
        if (mc.player == null) return;

        layoutPosY = graphics.guiHeight() - margin - healthBarHeight;

        playerName = mc.player.getName().getString();
        health = mc.player.getHealth();
        maxHealth = mc.player.getMaxHealth();
        absorptionAmount = mc.player.getAbsorptionAmount();
        absorptionPercentage = absorptionAmount / maxHealth;
        absorptionWidth = healthBarWidth * absorptionPercentage;
        healthPercentage = health / maxHealth;
        healthWidth = healthBarWidth * healthPercentage;

        int useColor = healthPercentage <= 0.35 ? danger_color : main_color;

//        graphics.fill(0, 0, 100, 100, 0xffffffff);
        graphics.text(mc.font, playerName, margin, layoutPosY - 10, useColor, true);
        graphics.fill(margin, layoutPosY, margin + healthBarWidth, layoutPosY + healthBarHeight, 0xAA000000);
        graphics.fill(margin, layoutPosY, margin + Math.min((int) healthWidth, healthBarWidth), layoutPosY + healthBarHeight, useColor);
        if (absorptionAmount > 0) {
            graphics.fill(margin, layoutPosY, margin + healthBarWidth, layoutPosY + (healthBarHeight / 2), 0xAA000000);
            graphics.fill(margin, layoutPosY, margin + Math.min((int) absorptionWidth, healthBarWidth), layoutPosY + (healthBarHeight / 2), 0xffffff00);
        }
    }
}
