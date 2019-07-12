package net.onixiya.teleporter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.loader.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

public class PortalList
{
    int version;
    public List<BlockPos> positions;
    public List<String> names;
    public List<String> dimensions;
    public List<Boolean> isPublic;
    public PortalList()
    {
        positions = new ArrayList<BlockPos>();
        names = new ArrayList<String>();
        dimensions = new ArrayList<String>();
        isPublic = new ArrayList<Boolean>();
    }

    public void remove(int index)
    {
        if(positions.size() > index)
        {
            this.positions.remove(index);
            this.names.remove(index);
            this.dimensions.remove(index);
            this.isPublic.remove(index);
        }
    }
    public void add(BlockPos blockPos, String name, String dim, boolean isPublic)
    {
        version = 1;
        this.dimensions.add(dim);
        this.isPublic.add(isPublic);
        this.names.add(name);
        this.positions.add(blockPos);
    }

    public static void addPortalLocation(PlayerEntity pe, BlockPos blockPos, String name)
	{
		PortalList pList = getPortalList(pe);

		pList.add(blockPos, name, DimensionType.getId(pe.dimension).toString(), false);

		File file = new File(getUserConfigPath().toString(), pe.getUuidAsString() + ".json");
		try 
		{
			FileWriter fw = new FileWriter(file);
			fw.write(new GsonBuilder().setPrettyPrinting().create().toJson(pList));
			fw.close();
		} catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	public static void overwritePortalLocations(PlayerEntity pe, PortalList pl)
	{
		File file = new File(getUserConfigPath().toString(),pe.getUuidAsString() + ".json");
		try {
			FileWriter fw = new FileWriter(file);
			fw.write(new GsonBuilder().setPrettyPrinting().create().toJson(pl));
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static Path getUserConfigPath()
	{
		Path path = Paths.get(FabricLoader.INSTANCE.getConfigDirectory().getPath(),"NetherTeleports","players");
		if(!Files.exists(path))
		{
			try {
				Files.createDirectories(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return path;
	}

	public static PortalList getPortalList(UUID pe)
	{
		Path path = getUserConfigPath();
		PortalList pList;
		
		File file = new File(path.toString(), pe + ".json");
		try 
		{
			FileReader fr = new FileReader(file);
			pList = new Gson().fromJson(fr, PortalList.class);
			FileWriter fw = new FileWriter(file);
			fw.write(new GsonBuilder().setPrettyPrinting().create().toJson(pList));
			fw.close();
			return pList;
		} catch (FileNotFoundException e) 
		{
			pList = new PortalList();
			try 
			{
				FileWriter fw = new FileWriter(file);
				fw.write(new GsonBuilder().setPrettyPrinting().create().toJson(pList));
				fw.close();
				return pList;
			} catch (IOException e1) 
			{
				e1.printStackTrace();
				return null;
			}
		} catch (IOException e) 
		{
			e.printStackTrace();
			return null;
		}
	}

	public static PortalList getPortalList(PlayerEntity pe)
	{
		UUID playerUUID = pe.getUuid();
		return getPortalList(playerUUID);
	}
}