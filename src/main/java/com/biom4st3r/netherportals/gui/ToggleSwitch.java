package com.biom4st3r.netherportals.gui;

import com.biom4st3r.netherportals.NetherPortalMod;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.Identifier;

public class ToggleSwitch extends ButtonWidget
{

    public static final Identifier BIRDBATH = new Identifier(NetherPortalMod.MODID,"textures/gui/birdbath.png");
    public boolean status = false;
    public int index = 0;
    //private int xTexPos = 256-12;
    //private int yTexPos = 256-24;

    public ToggleSwitch(int xPos, int yPos, boolean status,
            PressAction buttonWidget$PressAction_1) 
    {
        super(xPos, yPos, 12, 12, "", buttonWidget$PressAction_1);
        this.status = status;
    }

    public void renderButton(int int_1, int int_2, float float_1)
    {
        // MinecraftClient.getInstance().getTextureManager().bindTexture(BIRDBATH);
        // this.blit(this.x, this.y, 256-12, 256-24, 12, 24);
        // if(!status)
        // {
        //     this.blit(this.x,this.y,xTexPos,yTexPos-12,12,12);
        // }
        // else
        // {
        //     //blit(this.x,this.y+12,xTexPos,yTexPos-12,256-12,256-24,256-12,256-36);
        //     this.blit(this.x,this.y+11,xTexPos,yTexPos-24,12,12);
        // }
        MinecraftClient.getInstance().getTextureManager().bindTexture(BIRDBATH);
        if(status)
        {
            //this.blit(this.x,this.y,256-24,0,24,24);
            blit(this.x,this.y,0, // xPos yPos zLayer
                256-12,0, // u v
                12,12, // width height
                128,128); //pwm?
          //blit(int x, int y, int width, int height, float u, float v, int uWidth, int vHeight, int texWidth, int texHeight) {
        }
        else
        {
            //this.blit(this.x,this.y,256-24-24,0,24,24);
            blit(this.x,this.y,0, // xPos yPos zLayer
            256-24,0, // u v
            12,12, // width height
            128,128); //pwm?
        }

    }
    @Override
    public void onPress() {
        status = !status; // must be first
        super.onPress();
    }
    
}