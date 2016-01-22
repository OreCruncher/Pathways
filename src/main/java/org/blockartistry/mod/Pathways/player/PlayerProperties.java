package org.blockartistry.mod.Pathways.player;

import org.blockartistry.mod.Pathways.Pathways;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public final class PlayerProperties implements IExtendedEntityProperties {

	public static final String NAME = Pathways.MOD_ID + "_props";

	private static class NBT {
		public static final String IS_NEW = "in";
	}

	public boolean isNew = true;

	@Override
	public void saveNBTData(final NBTTagCompound compound) {
		final NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean(NBT.IS_NEW, this.isNew);
		compound.setTag(NAME, nbt);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		final NBTTagCompound nbt = compound.getCompoundTag(NAME);
		if (nbt.hasKey(NBT.IS_NEW))
			this.isNew = nbt.getBoolean(NBT.IS_NEW);
	}

	@Override
	public void init(final Entity entity, final World world) {

	}

	public static PlayerProperties get(final EntityPlayer player) {
		final IExtendedEntityProperties props = player.getExtendedProperties(NAME);
		return props == null ? null : (PlayerProperties) props;
	}
	
	public static void register(final EntityPlayer player) {
		player.registerExtendedProperties(NAME, new PlayerProperties());
	}
}
