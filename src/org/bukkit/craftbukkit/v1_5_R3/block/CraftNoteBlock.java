package org.bukkit.craftbukkit.v1_5_R3.block;

import net.minecraft.tileentity.TileEntityNote;

import org.bukkit.Instrument;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.Block;
import org.bukkit.block.NoteBlock;
import org.bukkit.craftbukkit.v1_5_R3.CraftWorld;

public class CraftNoteBlock extends CraftBlockState implements NoteBlock {
    private final CraftWorld world;
    private final TileEntityNote note;

    public CraftNoteBlock(final Block block) {
        super(block);

        world = (CraftWorld) block.getWorld();
        note = (TileEntityNote) world.getTileEntityAt(getX(), getY(), getZ());
    }

    public Note getNote() {
        return new Note(note.note);
    }

    public byte getRawNote() {
        return note.note;
    }

    public void setNote(Note n) {
        note.note = n.getId();
    }

    public void setRawNote(byte n) {
        note.note = n;
    }

    public boolean play() {
        Block block = getBlock();

        synchronized (block) {
            if (block.getType() == Material.NOTE_BLOCK) {
                note.triggerNote(world.getHandle(), getX(), getY(), getZ());
                return true;
            } else {
                return false;
            }
        }
    }
    /**
     * Drawbacks:
     * - Is always a natural note in the low octave.
     */
    // FIXME
    public boolean play(byte instrument, byte note) {
        Block block = getBlock();

        synchronized (block) {
            if (block.getType() == Material.NOTE_BLOCK) {
            	this.play(Instrument.getByType(instrument), Note.natural(0, Note.Tone.getById(note)));
                return true;
            } else {
                return false;
            }
        }
    }
    /**
     * Drawbacks:
     * - The instrument doesn't work - forge sets it itself on function run.
     * 
     * @throws IllegalArgumentException - if the instrument is invalid
     */
    public boolean play(Instrument instrument, Note note) throws IllegalArgumentException {
        Block block = getBlock();

        synchronized (block) {
            if (block.getType() == Material.NOTE_BLOCK) {
            	//BlockNote nb = (BlockNote) block;
            	TileEntityNote t = (TileEntityNote) ((CraftWorld) block.getWorld()).getTileEntityAt(getX(), getY(), getZ());
            	byte oldNote = t.note;
            	t.note = note.getId();
            	net.minecraft.block.Block targ;
            	switch(instrument.getType()) {
            	case 1:
                    targ = net.minecraft.block.Block.stone;
                    break;
            	case 2:
            		targ = net.minecraft.block.Block.sand;
            		break;
            	case 4:
            		targ = net.minecraft.block.Block.wood;
            		break;
            	case 3:
            		targ = net.minecraft.block.Block.glass;
            		break;
            	default:
            		throw new IllegalArgumentException("Invalid instrument: " + instrument.getType());

            	}
            	((CraftWorld)block.getWorld()).getHandle().setBlockMetadataWithNotify(getX(), getY() - 1, getZ(), targ.blockID, 3);
            	t.triggerNote(((CraftWorld) block.getWorld()).getHandle(), getX(), getY(), getZ());
            	t.note = oldNote;
            	//world.getHandle().playNote(getX(), getY(), getZ(), block.getTypeId(), instrument.getType(), note.getId());
                return true;
            } else {
                return false;
            }
        }
    }
}
