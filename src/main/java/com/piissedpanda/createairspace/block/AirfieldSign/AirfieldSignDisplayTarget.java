package com.piissedpanda.createairspace.block.AirfieldSign;

import com.simibubi.create.content.redstone.displayLink.DisplayLinkContext;
import com.simibubi.create.content.redstone.displayLink.target.SingleLineDisplayTarget;

import net.minecraft.network.chat.MutableComponent;

public class AirfieldSignDisplayTarget extends SingleLineDisplayTarget {

	@Override
	protected void acceptLine(MutableComponent text, DisplayLinkContext context) {
		AirfieldSignBlock.setRowText(context.level(), context.getTargetPos(), text.getString());
	}

	@Override
	protected int getWidth(DisplayLinkContext context) {
		return AirfieldSignBlock.getRow(context.level(), context.getTargetPos())
				.size() * 4;
	}

}
