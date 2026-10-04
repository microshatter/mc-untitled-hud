package io.github.microshatter.untitled_hud.client;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import org.jspecify.annotations.NonNull;

public class RenderOverlay implements HudElement {
    private Minecraft mc;
    public static final Identifier UNTITLED_GUI = Identifier.fromNamespaceAndPath("untitled_hud", "untitled_hud");

    // Colors
    private int bg_color = 0xAA000000;
    private int main_color = 0xffa6e3a1;
    private int danger_color = 0xfff38ba8;
    private int absorptionColor = 0xfff9e2af;
    private int damaged_color = 0xffeba0ac;
    private int regen_color = 0xff89dceb;

    // Data
    private float maxHealth;
    private float health;
    private float healthPercentage;
    private float healthWidth;
    private float absorptionAmount;
    private float absorptionPercentage;
    private float absorptionWidth;
    String playerName;

    private float lastHealth;
    private float ghostHealth;
    private boolean lastChangeWasDamage;

    // Layout
    private int margin = 10;
    private int healthBarWidth = 100;
    private int healthBarHeight = 5;
    private int layoutPosY;

    // Time related
    private double lastTimeSeconds;
    private double ghostRegenerateDelaySeconds;

    public RenderOverlay() {
        this.mc = Minecraft.getInstance();
        this.lastTimeSeconds = Util.getMillis() / 1000.0;
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, @NonNull DeltaTracker deltaTracker) {
        if (mc.player == null) return;

        // Calculate Screen
        healthBarWidth = Math.min((int) (graphics.guiWidth() * 0.2), 100);

        // Timing
        double currentTimeSeconds = Util.getMillis() / 1000.0;
        double delta = currentTimeSeconds - lastTimeSeconds;
        lastTimeSeconds = currentTimeSeconds;

        // Calculate bottom left position
        layoutPosY = graphics.guiHeight() - margin - healthBarHeight;

        // Fetch and calculate data
        playerName = mc.player.getName().getString();
        health = mc.player.getHealth();
        maxHealth = mc.player.getMaxHealth();
        absorptionAmount = mc.player.getAbsorptionAmount();
        absorptionPercentage = absorptionAmount / maxHealth;
        absorptionWidth = healthBarWidth * absorptionPercentage;
        healthPercentage = health / maxHealth;
        healthWidth = healthBarWidth * healthPercentage;

        // Ghost Health related calculation
        if (health != lastHealth) {
            if (health < lastHealth) {
                lastChangeWasDamage = true;
            } else if (health > lastHealth) {
                lastChangeWasDamage = false;
                if (health > ghostHealth) {
                    ghostRegenerateDelaySeconds = 1;
                }
            }

            lastHealth = health;
        } else {
            if (ghostRegenerateDelaySeconds > 0) {
                ghostRegenerateDelaySeconds -= delta;
                if (health == maxHealth || ghostRegenerateDelaySeconds < 0) {
                    ghostRegenerateDelaySeconds = 0;
                }
            }
            if (ghostHealth > health) {
                ghostHealth -= (float) ((50 / 100.0 * maxHealth) * (float) delta);
                if (ghostHealth < health) {
                    ghostHealth = health;
                }
            } else if (ghostHealth < health) {
                if (ghostRegenerateDelaySeconds <= 0) {
                    ghostHealth += (float) (((health >= maxHealth ? 200 : 75) / 100.0 * maxHealth) * (float) delta);
                }
                if (ghostHealth > health) {
                    ghostHealth = health;
                }
            }
        }

        float ghostPercentage = ghostHealth / maxHealth;
        float ghostWidth = healthBarWidth * ghostPercentage;

        // Dynamic color
        int useColor = healthPercentage <= 0.35 ? danger_color : main_color;

        // Render health bar and player name
        graphics.text(mc.font, playerName, margin, layoutPosY - 10, useColor, true);
        graphics.fill(margin, layoutPosY, margin + healthBarWidth, layoutPosY + healthBarHeight, bg_color);
        if (health < ghostHealth) {
            graphics.fill(margin, layoutPosY, margin + Math.min((int) ghostWidth, healthBarWidth), layoutPosY + healthBarHeight, damaged_color);
            graphics.fill(margin, layoutPosY, margin + Math.min((int) healthWidth, healthBarWidth), layoutPosY + healthBarHeight, useColor);
        } else {
            graphics.fill(margin, layoutPosY, margin + Math.min((int) healthWidth, healthBarWidth), layoutPosY + healthBarHeight, regen_color);
            graphics.fill(margin, layoutPosY, margin + Math.min((int) ghostWidth, healthBarWidth), layoutPosY + healthBarHeight, useColor);
        }
        if (absorptionAmount > 0) {
            graphics.fill(margin, layoutPosY, margin + healthBarWidth, layoutPosY + (healthBarHeight / 2), bg_color);
            graphics.fill(margin, layoutPosY, margin + Math.min((int) absorptionWidth, healthBarWidth), layoutPosY + (healthBarHeight / 2), absorptionColor);
        }
    }
}
