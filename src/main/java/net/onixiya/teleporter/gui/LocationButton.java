package net.onixiya.teleporter.gui;

import net.onixiya.teleporter.TeleporterMod;
import com.mojang.blaze3d.platform.GlStateManager;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class LocationButton extends ButtonWidget
{
    private static final Identifier MY_BOOK = new Identifier(TeleporterMod.MODID,"textures/gui/tele.png");
    private String body;
    private String title;
    public int index;
    public static int buttonWidth = 115;
    public static int buttonHeight = 24;
    TextRenderer textRenderer = MinecraftClient.getInstance().getFontManager().getTextRenderer(MinecraftClient.DEFAULT_TEXT_RENDERER_ID);
        
    public void updateContent(String title,String body,int index)
    {
        this.body = body;
        this.title = title;
        this.index = index;
    }

    public LocationButton(int xPos, int yPos,String title, String body,int index,
            PressAction buttonWidget$PressAction_1) {
        super(xPos, yPos, 115, 24, "", buttonWidget$PressAction_1);
        this.index = index;
        this.body = body;
        this.title = title;
    }

    public void renderButton(int int_1, int int_2, float float_1)
    {
        GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
        MinecraftClient.getInstance().getTextureManager().bindTexture(MY_BOOK);
        int x = 0;
        int y = 256-buttonHeight;
        if(this.isHovered || this.isFocused())
        {
            y-=buttonHeight;
            this.blit(this.x,this.y,x,y,buttonWidth,buttonHeight);
        }
        this.drawCenteredString(textRenderer, title, this.x+58, this.y+3,  0x99eeee);
        this.drawCenteredString(textRenderer, body, this.x+58, this.y+9+4, 0x00abab);
    }
    public void drawCenteredString(TextRenderer textRenderer_1, String string_1, int xCenter, int yPos, int color) {
        textRenderer.drawStringBounded(string_1, (xCenter - textRenderer_1.getStringWidth(string_1) / 2), yPos, 160,
                color);
    }   
}