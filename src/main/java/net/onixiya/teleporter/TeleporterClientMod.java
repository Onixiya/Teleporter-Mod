package net.onixiya.teleporter;

import java.util.ArrayList;
import java.util.List;

import net.onixiya.teleporter.registries.Packets;

import io.netty.buffer.Unpooled;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.server.network.packet.CustomPayloadC2SPacket;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class TeleporterClientMod implements ClientModInitializer
{

    public static List<BlockPos> poses = new ArrayList<BlockPos>();
    public static List<String> names = new ArrayList<String>();
    public static List<String> dims = new ArrayList<String>();
    public static List<Boolean> isPublic = new ArrayList<Boolean>();
    public static void resetPortalsLists()
    {
        poses = new ArrayList<BlockPos>();
        names = new ArrayList<String>();
    }

    @Override
    public void onInitializeClient() {
        ClientSidePacketRegistry.INSTANCE.register(Packets.SEND_ALL_PORTAL, (packetContext, packetByteBuf) ->
        {                                                                      
            int length = packetByteBuf.readInt();
            
            List<BlockPos> tPoses = new ArrayList<BlockPos>();
            List<String> tNames = new ArrayList<String>();
            List<String> tDims = new ArrayList<String>();
            List<Boolean> tisPublics = new ArrayList<Boolean>();
            if(length != 0)
            {
                for(int i = 0; i < length; i++)
                {
                    tPoses.add(packetByteBuf.readBlockPos());
                }
                for(int i = 0; i < length; i++)
                {
                    tNames.add(packetByteBuf.readString());
                }
                for(int i = 0; i < length; i++)
                {
                    tDims.add(packetByteBuf.readString());
                }
                for(int i = 0; i < length; i++)
                {
                    tisPublics.add(packetByteBuf.readBoolean());
                }


                poses = tPoses;
                names = tNames;
                dims = tDims;
                isPublic = tisPublics;
                
            }
            else 
            {
                poses = new ArrayList<BlockPos>();
                names = new ArrayList<String>();
            }
        });
        ClientSidePacketRegistry.INSTANCE.register(Packets.SEND_PUBLICITY_UPDATE, (context, buffer) ->
        {
            List<Boolean> t = new ArrayList<Boolean>();
            int size = buffer.readInt();
            for(int i = 0; i < size; i++)
            {
                t.add(buffer.readBoolean());
            }
            isPublic = t;
        });
        
    }

    public static CustomPayloadC2SPacket createRegisterPortal(String name, BlockPos blockPos)
    {
        PacketByteBuf pbb = new PacketByteBuf(Unpooled.buffer());
        pbb.writeString(name);
        pbb.writeBlockPos(blockPos);
        return new CustomPayloadC2SPacket(Packets.REGISTER_NEW_PORTAL,pbb);
    }
    public static CustomPayloadC2SPacket createRequestPortalList()
	{
		PacketByteBuf pbb = new PacketByteBuf(Unpooled.buffer());
		return new CustomPayloadC2SPacket(Packets.REQUEST_PORTAL_LIST,pbb);
    }
    public static CustomPayloadC2SPacket createRequestTeleport(int index, String owner)
    {
        PacketByteBuf pbb = new PacketByteBuf(Unpooled.buffer());
        pbb.writeInt(index);
        pbb.writeString(owner);
        return new CustomPayloadC2SPacket(Packets.REQUEST_TELEPORT,pbb);
    }
    public static CustomPayloadC2SPacket createRemoveEntry(BlockPos bp)
    {
        PacketByteBuf pbb = new PacketByteBuf(Unpooled.buffer());
        pbb.writeBlockPos(bp);
        return new CustomPayloadC2SPacket(Packets.REMOVE_ENTRY,pbb);
    }
    public static CustomPayloadC2SPacket createSearchPlayer(String name)
    {
        PacketByteBuf pbb = new PacketByteBuf(Unpooled.buffer());
        if(name.length() < 3)
        {
            return null;
        }
        pbb.writeString(name);
        return new CustomPayloadC2SPacket(Packets.SEARCH_PLAYER,pbb);
    }
    public static CustomPayloadC2SPacket createChangeLocationStatus(int index, boolean isPublic)
    {
        PacketByteBuf pbb = new PacketByteBuf(Unpooled.buffer());
        pbb.writeInt(index);
        pbb.writeBoolean(isPublic);
        return new CustomPayloadC2SPacket(Packets.CHANGE_PUBLIC_STATUS,pbb);
    }
}