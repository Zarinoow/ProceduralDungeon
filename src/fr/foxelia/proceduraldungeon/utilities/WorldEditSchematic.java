package fr.foxelia.proceduraldungeon.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.bukkit.Location;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.BuiltInClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;

public class WorldEditSchematic {
	
	private Clipboard loadedSchematic;
	private Exception saveException;
	
	public Clipboard getLoadedSchematic() {
		return loadedSchematic;
	}
	
	public Exception getSaveException() {
		return saveException;
	}
	
	public void loadSchematic(File file) {
		if(!(file.exists())) return;
		ClipboardFormat format = ClipboardFormats.findByFile(file);
		try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
		   this.loadedSchematic = reader.read();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void pasteSchematic(Location loc) {
		if(loadedSchematic == null) return;
		try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(loc.getWorld()))) {
		    Operation operation = new ClipboardHolder(loadedSchematic)
		            .createPaste(editSession)
		            .to(BlockVector3.at(loc.getX(), loc.getY(), loc.getZ()))
		            .copyEntities(true)
		            .build();
		    Operations.complete(operation);
		} catch (WorldEditException e) {
			e.printStackTrace();
		}
	}
	
	public void saveSchematic(Location origin, LocalSession ps, File exportFile) {		
		CuboidRegion region;
		try {
			region = new CuboidRegion(ps.getSelection().getMinimumPoint(), ps.getSelection().getMaximumPoint());
		} catch (IncompleteRegionException e) {
			saveException = e;
			return;
		}
		BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
		clipboard.setOrigin(BukkitAdapter.asBlockVector(origin));

		try (EditSession editSession = WorldEdit.getInstance().newEditSession(ps.getSelection().getWorld())) {
		    ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
		        editSession, region, clipboard, region.getMinimumPoint()
		    );
		    forwardExtentCopy.setCopyingEntities(true);
		    Operations.complete(forwardExtentCopy);
		} catch (WorldEditException e) {
			e.printStackTrace();
			saveException = e;
			return;
		}
		
		if(!(exportFile.getParentFile().exists())) {
			exportFile.getParentFile().mkdirs();
		}
		
		try (ClipboardWriter writer = BuiltInClipboardFormat.SPONGE_SCHEMATIC.getWriter(new FileOutputStream(exportFile))) {
		    writer.write(clipboard);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			saveException = e;
			return;
		} catch (IOException e) {
			e.printStackTrace();
			saveException = e;
			return;
		}
		return;
	}
	
}
