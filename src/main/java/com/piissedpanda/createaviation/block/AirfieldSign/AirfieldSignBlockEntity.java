package com.piissedpanda.createaviation.block.AirfieldSign;

import java.util.List;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class AirfieldSignBlockEntity extends SmartBlockEntity {

	public static final int MAX_LENGTH = 48;

	private String text = "";

	public AirfieldSignBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		if (text.length() > MAX_LENGTH)
			text = text.substring(0, MAX_LENGTH);
		if (this.text.equals(text))
			return;
		this.text = text;
		notifyUpdate();
	}

	@Override
	protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(tag, registries, clientPacket);
		tag.putString("Text", text);
	}

	@Override
	protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(tag, registries, clientPacket);
		text = tag.getString("Text");
	}

	// The first sign of a row draws the text for all of them
	@Override
	protected AABB createRenderBoundingBox() {
		return new AABB(worldPosition).inflate(AirfieldSignBlock.MAX_ROW_LENGTH);
	}

}
