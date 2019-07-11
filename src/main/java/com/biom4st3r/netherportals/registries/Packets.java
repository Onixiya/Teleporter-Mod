package com.biom4st3r.netherportals.registries;

import com.biom4st3r.netherportals.NetherPortalMod;

import net.minecraft.util.Identifier;

public class Packets
{
    private static final String MODID = NetherPortalMod.MODID;
    public static final Identifier SEND_ALL_PORTAL = new Identifier(MODID,"sendportallocations");
    public static final Identifier SEND_NAME_UPDATE = new Identifier(MODID,"sendportalnamelist");
    public static final Identifier SEND_LOCATION_UPDATE = new Identifier(MODID,"sendportallocationlist");
    public static final Identifier SEND_DIMINSION_UPDATE = new Identifier(MODID,"sendportaldimiensionlist");
    public static final Identifier SEND_PUBLICITY_UPDATE = new Identifier(MODID,"sendportalpubliclist");
//#region Client Packets
    public static final Identifier REQUEST_PORTAL_LIST = new Identifier(MODID,"requestportllist");
    public static final Identifier REGISTER_NEW_PORTAL = new Identifier(MODID,"registernewportal");
    public static final Identifier    REQUEST_TELEPORT = new Identifier(MODID,"requestteleport");
    public static final Identifier        REMOVE_ENTRY = new Identifier(MODID,"removeentry");
    public static final Identifier       SEARCH_PLAYER = new Identifier(MODID,"searchplayer");
    public static final Identifier CHANGE_PUBLIC_STATUS = new Identifier(MODID,"changelocstat");
        
    public static class Server
    {

    }
    public static class Client
    {
        
    }
//#endregion
    
}