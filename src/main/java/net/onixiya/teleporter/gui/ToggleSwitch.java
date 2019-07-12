package net.onixiya.teleporter.gui;

import net.onixiya.teleporter.TeleporterMod;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.Identifier;

public class ToggleSwitch extends ButtonWidget
{

    public static final Identifier Teleporter = new Identifier(TeleporterMod.MODID,"textures/gui/tele.png");
    public boolean status = false;
    public int index = 0;
    private int xTexPos = 256-12;
    private int yTexPos = 256-24;

    public ToggleSwitch(int xPos, int yPos, boolean status,
            PressAction buttonWidget$PressAction_1) 
    {
        super(xPos, yPos, 12, 12, "", buttonWidget$PressAction_1);
        this.status = status;
    }

    public void renderButton(int int_1, int int_2, float float_1)
    {
        MinecraftClient.getInstance().getTextureManager().bindTexture(Teleporter);
        if(status)
        {
            blit(this.x,this.y,0,
                256-12,0,
                12,12,
                128,128);
        }
        else
        {
            blit(this.x,this.y,0,
            256-24,0,
            12,12,
            128,128);
        }

    }
    @Override
    public void onPress() {
        status = !status;
        super.onPress();
    }
    
}