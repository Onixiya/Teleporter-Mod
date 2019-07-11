package com.biom4st3r.netherportals.gui;

import com.biom4st3r.netherportals.NetherPortalClientMod;
import com.biom4st3r.netherportals.NetherPortalMod;
import com.mojang.blaze3d.platform.GlStateManager;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.PageTurnWidget;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class BirdBathGui extends Screen {

    private int tick;
    TextRenderer textRenderer = MinecraftClient.getInstance().getFontManager()
            .getTextRenderer(MinecraftClient.DEFAULT_TEXT_RENDERER_ID);
    public static final Identifier BG_Texture = new Identifier(NetherPortalMod.MODID,"textures/gui/birdbath.png");
    public static final Identifier BG_Texture_Quartz = new Identifier(NetherPortalMod.MODID,"textures/gui/birdbath2.png");
    public static final Identifier waterTexture = new Identifier(NetherPortalMod.MODID, "textures/block/water_still3.png");
    private int pageIndex = 1;
    private int maxPages = 666;
    private AbstractButtonWidget back;
    private AbstractButtonWidget forward;
    private BlockPos position;
    private LocationButton[] locationButtons = new LocationButton[6];
    private ToggleSwitch[] toggleswitches = new ToggleSwitch[6];
    private ClientPlayNetworkHandler nic;

    private int nameListHashCode;
    private int isPublicListHashCode;

    private TextFieldWidget nameField;

    private String listOwner;

    public BirdBathGui(BlockPos thisPortal) {
        super(new TextComponent("Bird Bath Portal"));
        position = thisPortal;
    }

    protected boolean isPurPur()
    {
        return this.minecraft.player.getEntityWorld().getBlockState(this.position).getBlock() == NetherPortalMod.bbb;
    }
    
    @Override
    public void tick()
    {
        tick++;
    }
    
    @Override
    public boolean keyPressed(int keyCode, int int_2, int int_3) {
        if((keyCode == 257 || keyCode == 335) && nameField.isFocused())
        {
            searchPlayer(nameField.getText());
            return true;
        }
        return super.keyPressed(keyCode, int_2, int_3);
    }
     
    private void buttonRegistry()
    {
        if(isPurPur())
        {
            back = this.addButton(new PageTurnWidget(width()+50, 182, false, (buttonwidet) -> {
                pageIndex--;
                updateLocationButtons();
            }, true));
    
            forward = this.addButton(new PageTurnWidget(width() + 150-24, 182, true, (buttonwidget) -> {
                pageIndex++;
                updateLocationButtons();
            }, true));
        }
        if (!NetherPortalClientMod.poses.contains(position)) 
        { //Save Location
            this.addButton(new ButtonWidget(width() +100-60, 204, 120, 20, "Save This Location", (buttonWidget -> 
            {
                this.minecraft.player.closeScreen();
                MinecraftClient.getInstance().openScreen(new BirdBathRegister(position));
            })));
        }
        else if(NetherPortalClientMod.poses.contains(position) )
        { //Remove Location
            this.addButton(new ButtonWidget(width() +100-65, 204, 130, 20, "Remove This Location", (buttonWidget -> 
            {
                this.nic.sendPacket(NetherPortalClientMod.createRemoveEntry(this.position));
                this.minecraft.player.closeScreen();
            })));
        }
        // this.addButton(new ButtonWidget (this.width() +155,5,20,20,"Go", (ButtonWidget ->
        // {
        //     searchPlayer(this.nameField.getText());
        // })));
    }

    private void searchPlayer(String name)
    {
        //Player497
        if(name.equals(this.minecraft.player.getEntityName()))
        {
            return;
        }

        if(name.length() > 3)
        {
            CustomPayloadC2SPacket searchPlayer = NetherPortalClientMod.createSearchPlayer(name);
            //System.out.println("GUi start searchPlayer");
            if(searchPlayer != null)
            {
                //System.out.println("GUi send searchPlayer");
                this.listOwner = name;
                this.pageIndex = 1;
                this.nic.sendPacket(searchPlayer);
            }
        }
        boolean searchForSelf = name.equals(this.minecraft.player.getEntityName());
        if(!searchForSelf)
        {
            for(int i = 0; i < 6; i++)
            {
                locationButtons[i].visible = false;
                toggleswitches[i].visible = false;
            }
            // locationButtons[0].visible = false;
            // locationButtons[1].visible = false;
            // locationButtons[2].visible = false;
            // locationButtons[3].visible = false;
            // locationButtons[4].visible = false;
            // locationButtons[5].visible = false;
        }
        
    }

    public void calcMaxPages()
    {
        this.maxPages = Math.max(1,(int)Math.ceil((double)NetherPortalClientMod.names.size()/6d));
    }

    @Override
    protected void init() 
    {
        
        //#region Must Be Top
        nic = MinecraftClient.getInstance().getNetworkHandler();
        nameListHashCode = getCurrentHashCode();
        isPublicListHashCode = NetherPortalClientMod.isPublic.hashCode();
        listOwner = this.minecraft.player.getEntityName();
        
        //#endregion
        buttonRegistry();

        //this.setInitialFocus(this);
        //this.maxPages = Math.max(1,(int)Math.ceil((double)NetherPortalClientMod.names.size()/6d));
        calcMaxPages();
        if(back != null && forward != null)
        {
            back.visible = false;
            forward.visible = false;
        }
        generateTeleportButtons();
        this.nameField = new TextFieldWidget(this.font, this.width()+57, 10, 103, 14, "");
        //                                                           50  7
        { // nameField
            this.nameField.setEditableColor(0xbfbf73);
            this.nameField.setHasBorder(false);
            this.nameField.setMaxLength(16); // name can only be 3 - 16 chars
            this.nameField.setIsEditable(true);
            this.nameField.setText(this.minecraft.player.getEntityName());//Search player...");
        }
        this.children.add(this.nameField);
        this.setInitialFocus(nameField);
        //System.out.println(nameField != null);
        super.init();
    }

    private int getCurrentHashCode()
    {
        return NetherPortalClientMod.names.hashCode();
    }
    
    @Override
    public void render(int int1, int int2, float float1) {
        if(nameListHashCode != getCurrentHashCode() || isPublicListHashCode != NetherPortalClientMod.isPublic.hashCode())
        {
            updateLocationButtons();
            nameListHashCode = getCurrentHashCode();
            isPublicListHashCode = NetherPortalClientMod.isPublic.hashCode();
            calcMaxPages();
        }
        if(pageIndex > 1 && back != null)
        {
            back.visible = true;
        }
        else if(pageIndex <= 1 && back != null)
        {
            back.visible = false;
        }
        if(pageIndex < maxPages && forward != null)
        {
            forward.visible = true;
        }
        else if(pageIndex >= maxPages && forward != null)
        {
            forward.visible = false;
        }
        //this.renderBackground();
        GlStateManager.pushMatrix();

        if(isPurPur())
        {
            this.minecraft.getTextureManager().bindTexture(BG_Texture);
        }
        else
        {
            this.minecraft.getTextureManager().bindTexture(BG_Texture_Quartz);
        }
        
        this.blit(width()+2,3,0,0,200,200);

        
        this.minecraft.getTextureManager().bindTexture(waterTexture);
        //guess and check till i figure out whta i'm doing
        blit(width()+27,28,0,0,(tick%32*152),150,150,16*16*19,152);
        //                          16*16*(19/2)

        this.drawCenteredString(textRenderer, getPageDisplay(), width() + 100, 185, 0x694069);//0xaaaa55);
        GlStateManager.popMatrix();
        fill(this.width()+50+5, 7, this.width()+153-5, 3+18, 0x66000000);
        this.nameField.render(int1, int2, float1);
        //fill(0, 5, this.width()+100, 5+20, 0xFFFFFF);
        
        //AnvilScreen
        super.render(int1, int2, float1);
    }


    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void updateLocationButtons()
    {
        for(int x = 0; x < 6 ; x++)
        {
            int index = x + ((6 * pageIndex) - 6);
            if(locationButtons[x] != null && NetherPortalClientMod.names.size() > index)
            {
                locationButtons[x].visible = true;
                if(listOwner.equals(this.minecraft.player.getEntityName()))
                {
                    toggleswitches[x].index = index;
                    toggleswitches[x].visible = true;
                    toggleswitches[x].status = NetherPortalClientMod.isPublic.get(index);
                    
                }
                String title = NetherPortalClientMod.names.get(index);
                BlockPos body = NetherPortalClientMod.poses.get(index);
                String blockPos = String.format("%s, %s, %s", body.getX(), body.getY(), body.getZ());
                locationButtons[x].updateContent(title, blockPos, index);
            }
            else
            {
                if(locationButtons[x]==null)
                {
                    //System.out.println("NULL NULL");
                    return;
                }
                locationButtons[x].visible = false;
                toggleswitches[x].visible = false;
                locationButtons[x].updateContent("ERROR", "ERROR", -1);
            }
        }
    }

    private void generateTeleportButtons() {
        final int width = this.width / 2 - 54;//;68;//this.width / 2 - 60;
        int height = 30;//15;
        for (int x = 0 ; x < 6; x++) 
        {
            int index = x + ((6 * pageIndex) - 6);
            //loaded = true;
            locationButtons[x] = this.addButton(new LocationButton(width, height, "", "",index, (buttonwidget) -> {
                this.nic.sendPacket(NetherPortalClientMod.createRequestTeleport(((LocationButton)buttonwidget).index, listOwner));
            }));

            toggleswitches[x] = this.addButton(new ToggleSwitch(width-13, height+4, false, (buttonWidget) ->
            {
                NetherPortalClientMod.isPublic.set(((ToggleSwitch)buttonWidget).index, ((ToggleSwitch)buttonWidget).status);
                this.nic.sendPacket(NetherPortalClientMod.createChangeLocationStatus(((ToggleSwitch)buttonWidget).index, ((ToggleSwitch)buttonWidget).status));
            }));

            locationButtons[x].visible = false;
            toggleswitches[x].visible = false;
            if (index <= NetherPortalClientMod.names.size() -1 && NetherPortalClientMod.names.size() != 0) 
            {
                locationButtons[x].visible = true;
                toggleswitches[x].visible = true;
                toggleswitches[x].index = index;


                toggleswitches[x].status = NetherPortalClientMod.isPublic.get(index);
                String thistitle = NetherPortalClientMod.names.get(index);
                BlockPos blockPost = NetherPortalClientMod.poses.get(index);
                String blockPos = String.format("%s, %s, %s", blockPost.getX(), blockPost.getY(), blockPost.getZ());
                locationButtons[x].updateContent(thistitle, blockPos, index);
            }
            height += LocationButton.buttonHeight;

        }

    }

    private int width() {
        return (this.width - 194) / 2;
    }

    private String getPageDisplay() {
        return String.format("%s of %s", pageIndex, maxPages);
    }

    public void drawCenteredString(TextRenderer textRenderer_1, String string_1, int xCenter, int y, int color) {
        textRenderer.drawStringBounded(string_1, (xCenter - textRenderer_1.getStringWidth(string_1) / 2), y, 160,
                color);
    }



    
}