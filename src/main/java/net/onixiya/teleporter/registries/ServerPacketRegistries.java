package net.onixiya.teleporter.registries;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.onixiya.teleporter.TeleporterMod;
import net.onixiya.teleporter.PortalList;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.ChatFormat;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.packet.CustomPayloadS2CPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

public class ServerPacketRegistries
{

	public static void debug(String message)
	{
		if(false)
			System.out.println(TeleporterMod.MODID_SHORT + ": " + message);
	}

    public static void reg()
    {
        ServerSidePacketRegistry.INSTANCE.register(Packets.REQUEST_PORTAL_LIST, (packetContext, packetByteBuf) -> 
		{
			
			debug("packet(REQUEST_PORTAL_LIST)");
			PlayerEntity pe =  packetContext.getPlayer();
			PortalList pl = PortalList.getPortalList((PlayerEntity)pe);
			int nameListSize = pl.names.size();
			if(pl.isPublic.size() != nameListSize)
			{
				pl.isPublic = new ArrayList<Boolean>();
				while(pl.isPublic.size() != nameListSize)
				{
					pl.isPublic.add(false);
				}
				PortalList.overwritePortalLocations(pe, pl);
			}
			//System.out.println(pList.version + " asdfasdf");
			((ServerPlayerEntity)pe).networkHandler.sendPacket(createSendPortalLocations(pl.positions, pl.names,pl.dimensions,pl.isPublic));
		});

		ServerSidePacketRegistry.INSTANCE.register(Packets.REGISTER_NEW_PORTAL, (context, buffer) ->
		{
			debug("packet(REGISTER_NEW_PORTAL)");
			String name = buffer.readString(23);
			BlockPos bpos = buffer.readBlockPos();
			boolean within_range = Math.sqrt(context.getPlayer().squaredDistanceTo(bpos.getX(), bpos.getY(), bpos.getZ())) < 10;
			boolean is_tele_bath = context.getPlayer().getEntityWorld().getBlockState(bpos).getBlock() == TeleporterMod.bbb;
			//String uuid = packetContext.getPlayer().getUuidAsString();
			if(within_range && is_tele_bath)
			{
				PortalList.addPortalLocation(context.getPlayer(), bpos, name);
			}
			else
			{
				if(!within_range)
				{
					System.out.println(context.getPlayer().getEntityName() + ": Attempted to register a new Teleporter > 10 Blocks away from themselfs. Vanilla Reach is ~5");
				}
				if(!is_tele_bath)
				{
					System.out.println(context.getPlayer().getEntityName() + ": Attempted to register a new Teleporter at location that's not a portal");
				}
			}
		});

		ServerSidePacketRegistry.INSTANCE.register(Packets.REQUEST_TELEPORT, (packetContext, packetByteBuf) ->
		{
			debug("packet(REQUEST_TELEPORT)");
			int indexOfLocEntry = packetByteBuf.readInt();
			//debug("Index " + index);
			String owner = packetByteBuf.readString(16);
			ServerPlayerEntity pe = (ServerPlayerEntity)packetContext.getPlayer();
			PortalList pl;
			BlockPos blockPos = null;
			DimensionType dt = null;

			if(owner.equals(pe.getEntityName()))
			{
				//debug("is owner");
				pl = PortalList.getPortalList(packetContext.getPlayer());
				blockPos = pl.positions.get(indexOfLocEntry);
				dt = DimensionType.byId(new Identifier(pl.dimensions.get(indexOfLocEntry)));
			}
			else
			{
				pl = PortalList.getPortalList(PlayerEntity.getOfflinePlayerUuid(owner));
				//debug("List length " + pl.names.size());
				for(int portalListIndex = 0,publicIndex = -1; portalListIndex < pl.names.size(); portalListIndex ++)
				{
					publicIndex += (pl.isPublic.get(portalListIndex) ? 1 : 0);
					//debug("portalListIndex "+ portalListIndex);
					//debug("publicIndex" + publicIndex);
					if(publicIndex == indexOfLocEntry)
					{
						blockPos = pl.positions.get(portalListIndex);
						dt = DimensionType.byId(new Identifier(pl.dimensions.get(portalListIndex)));
						//System.out.println(index);
						//System.out.println(portalListIndex);
						//System.out.println(publicIndex);
						break;
					}
				}
			}
			if(dt == null)
			{
				System.out.println("IF YOU SEE THIS MESSAGE. PLEASE CONTACT BIOM4ST3R.\n " + packetContext.getPlayer().getEntityName() +" HAS ATTEMPTED TO ACCESS AN INVALID TELEPORT LOCATION.");
				return;
			}

			// BlockPos blockPos = pl.positions.get(index);
			// String dimension = pl.dimensions.get(index);
			// DimensionType dt = DimensionType.byId(new Identifier(dimension));
			boolean illegalHeight = blockPos.getY() == 125 || blockPos.getY() == 126;

			if(illegalHeight && dt == DimensionType.THE_NETHER)
			{
					pe.sendChatMessage(new TextComponent(ChatFormat.DARK_PURPLE + "Seems like it didn't work?"), ChatMessageType.CHAT);
					return;
			}
			if(dt != pe.dimension)
			{
				TeleporterMod.interDimesionalTeleport(pe, dt, blockPos.add(0, 1, 0));
			}
			else
			{
				packetContext.getPlayer().requestTeleport(blockPos.getX()+0.5d, blockPos.getY() + 1, blockPos.getZ()+0.5d);
			}
			//checking if valid
			BlockState Teleporter = pe.world.getBlockState(blockPos);
			pe.getEntityWorld().sendEntityStatus(pe, (byte)46);
			if(Teleporter.getBlock() != TeleporterMod.bbb) // Teleporter.isAir())
			{
				pl.remove(indexOfLocEntry);
				if(pe.getEntityName().equals(owner))
				{
					PortalList.overwritePortalLocations(pe, pl);
				}
				pe.networkHandler.sendPacket(createSendPortalLocations(pl.positions, pl.names,pl.dimensions,pl.isPublic));
			}
			pe.closeContainer();


		});
		ServerSidePacketRegistry.INSTANCE.register(Packets.REMOVE_ENTRY, (packetContext,packetByteBuf) ->
		{
			debug("packet(REMOVE_ENTRY)");
			BlockPos bp = packetByteBuf.readBlockPos();
			ServerPlayerEntity pe = (ServerPlayerEntity)packetContext.getPlayer();
			PortalList pl = PortalList.getPortalList(pe);
			int index = pl.positions.indexOf(bp);

			pl.remove(index);

			PortalList.overwritePortalLocations(pe, pl);
			pe.networkHandler.sendPacket(createSendPortalLocations(pl.positions, pl.names,pl.dimensions, pl.isPublic));
		});
		ServerSidePacketRegistry.INSTANCE.register(Packets.SEARCH_PLAYER, (packetContext,packetByteBuf) ->
		{
			debug("packet(SEARCH_PLAYER)");
			String playername = packetByteBuf.readString(16);
			UUID playeruuid = ServerPlayerEntity.getOfflinePlayerUuid(playername);
			PortalList pl = PortalList.getPortalList(playeruuid);
			boolean searchingForSelf = packetContext.getPlayer().getEntityName().equals(playername);
			if(pl.names.size() > 3 && !searchingForSelf)
			{
				//System.out.println("Sent portal locations");
				debug("packet(SEARCH_PLAYER)#createSendPublicPortalLocations");
				((ServerPlayerEntity)packetContext.getPlayer()).networkHandler.sendPacket(createSendPlayerSearchResults(pl.positions, pl.names,pl.dimensions,pl.isPublic));
			}
			// else if(searchingForSelf)
			// {
			// 	debug("packet(SEARCH_PLAYER)#createSendPortalLocations");
			// 	((ServerPlayerEntity)packetContext.getPlayer()).networkHandler.sendPacket(createSendPortalLocations(pl.positions, pl.names, pl.dimensions,pl.isPublic));
			// }
		});
		ServerSidePacketRegistry.INSTANCE.register(Packets.CHANGE_PUBLIC_STATUS, (context,buffer) ->
		{
			debug("packet(CHANGE_PUBLIC_STATUS)");
			int index = buffer.readInt();
			boolean isPublic = buffer.readBoolean();
			PlayerEntity pe = context.getPlayer();
			PortalList pl = PortalList.getPortalList(pe);
			pl.isPublic.set(index, isPublic);
			PortalList.overwritePortalLocations(pe, pl);
			//((ServerPlayerEntity)pe).networkHandler.sendPacket(createSendPublicityUpdate(pl.isPublic));
		});
	}
	
	public static CustomPayloadS2CPacket createSendNameUpdate(List<String> names)
	{
		PacketByteBuf pbb = new PacketByteBuf(Unpooled.buffer());
		pbb.writeInt(names.size());
		for(String s : names)
		{
			pbb.writeString(s);
		}
		return new CustomPayloadS2CPacket(Packets.SEND_NAME_UPDATE,pbb);
	}

	public static CustomPayloadS2CPacket createSendLocationUpdate(List<BlockPos> poses)
	{
		PacketByteBuf pbb = new PacketByteBuf(Unpooled.buffer());
		pbb.writeInt(poses.size());
		for(BlockPos b : poses)
		{
			pbb.writeBlockPos(b);
		}
		return new CustomPayloadS2CPacket(Packets.SEND_LOCATION_UPDATE,pbb);
	}

	public static CustomPayloadS2CPacket createSendDimUpdate(List<String> dims)
	{
		PacketByteBuf pbb = new PacketByteBuf(Unpooled.buffer());
		pbb.writeInt(dims.size());
		for(String s : dims)
		{
			pbb.writeString(s);
		}
		return new CustomPayloadS2CPacket(Packets.SEND_DIMINSION_UPDATE,pbb);
	}

    public static CustomPayloadS2CPacket createSendPortalLocations(List<BlockPos> positions, List<String> names, List<String> dimensions,List<Boolean> isPublics)
	{
		//System.out.println("sendPortalLocationsPacket");
		PacketByteBuf pbb = new PacketByteBuf(Unpooled.buffer());
		pbb.writeInt(positions.size());
		for(BlockPos p : positions)
		{
			pbb.writeBlockPos(p);
		}
		for(String s : names)
		{
			pbb.writeString(s);
		}
		for(String s : dimensions)
		{
			pbb.writeString(s);
		}
		for(Boolean b: isPublics)
		{
			pbb.writeBoolean(b);
		}
		return new CustomPayloadS2CPacket(Packets.SEND_ALL_PORTAL,pbb);
	}
	public static CustomPayloadS2CPacket createSendPlayerSearchResults(List<BlockPos> positions, List<String> names, List<String> dimensions, List<Boolean> isPublic)
	{
		PacketByteBuf pbb = new PacketByteBuf(Unpooled.buffer());
		List<Integer> publicLocations = new ArrayList<Integer>(); 
		for(int i = 0; i < positions.size(); i++)
		{
			if(isPublic.get(i))
			{
				publicLocations.add(i);
			}
		}
		pbb.writeInt(publicLocations.size());
		for(int x : publicLocations)
		{
			pbb.writeBlockPos(positions.get(x));
		}
		for(int x : publicLocations)
		{
			pbb.writeString(names.get(x));
		}
		for(int x : publicLocations)
		{
			pbb.writeString(dimensions.get(x));
		}
		for(int x : publicLocations)
		{
			pbb.writeBoolean(true);
		}
		return new CustomPayloadS2CPacket(Packets.SEND_ALL_PORTAL,pbb);
		
	}
	public static CustomPayloadS2CPacket createSendPublicityUpdate(List<Boolean> isPublics)
	{
		PacketByteBuf pbb = new PacketByteBuf(Unpooled.buffer());
		pbb.writeInt(isPublics.size());
		for(Boolean b : isPublics)
		{
			pbb.writeBoolean(b);
		}
		return new CustomPayloadS2CPacket(Packets.SEND_PUBLICITY_UPDATE, pbb);
	}
}