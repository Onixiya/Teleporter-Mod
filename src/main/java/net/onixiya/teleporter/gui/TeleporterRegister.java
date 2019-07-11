package net.onixiya.teleporter.gui;

import net.onixiya.teleporter.TeleporterClientMod;
import com.mojang.blaze3d.platform.GlStateManager;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class TeleporterRegister extends Screen {

    private TextFieldWidget nameField;
    private BlockPos location;

    protected TeleporterRegister(BlockPos blockPos) {
        super(new TextComponent("Teleporter"));
        location = blockPos;
    }

    @Override
    public boolean keyPressed(int keyCode, int int_2, int int_3) {
        if((keyCode == 257 || keyCode == 335) && nameField.isFocused())
        {
            doneAction();
            return true;
        }
        return super.keyPressed(keyCode, int_2, int_3);
    }
    @Override
    public boolean changeFocus(boolean boolean_1) {
        return false;
    }

    public void doneAction()
    {
        MinecraftClient.getInstance().getNetworkHandler().getClientConnection()
        .send(TeleporterClientMod.createRegisterPortal(nameField.getText(), location));
        MinecraftClient.getInstance().getNetworkHandler().getClientConnection().send(TeleporterClientMod.createRequestPortalList());
        this.minecraft.player.closeContainer();
    }

    protected void init() {
        this.nameField = new TextFieldWidget(this.font, this.width / 2 - 50, this.height / 2 - 6, 103, 12, "");
        this.nameField.method_1856(true);
        this.nameField.changeFocus(true);
        this.nameField.setEditableColor(-1);
        this.nameField.setUneditableColor(-1);
        this.nameField.setHasBorder(true);
        this.nameField.setMaxLength(23);
        this.nameField.setIsEditable(true);
        this.nameField.setText("Location Name");
        this.children.add(this.nameField);
        this.setInitialFocus(nameField);

        this.addButton(new ButtonWidget((this.width / 2 - 48), this.height / 2 + 10, 100, 20, "Done", (buttonWidget -> {
            doneAction();
        })));
    }

    @Override
    public void render(int int1, int int2, float float1) {
        this.renderBackground();
        // GlStateManager.disableLighting();
        GlStateManager.pushMatrix();
        this.nameField.render(int1, int2, float1);
        // GlStateManager.disableBlend();
        //this.blit(this.width/2,0,2,0,192,192);
        GlStateManager.popMatrix();
        super.render(int1, int2, float1);

    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }



}