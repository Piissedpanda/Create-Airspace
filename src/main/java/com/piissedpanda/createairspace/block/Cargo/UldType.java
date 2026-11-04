package com.piissedpanda.createairspace.block.Cargo;

import javax.annotation.Nullable;

// Real unit load devices, a finished container must have exactly one of these
// shapes (width and depth in either order)
public enum UldType {

	LD2(1, 1, 2),
	LD3(2, 2, 2),
	LD11(2, 3, 2),
	AMA(3, 3, 3);

	public static final int MAX_BLOCKS = 27;

	private final int shortSide;
	private final int longSide;
	private final int height;

	UldType(int shortSide, int longSide, int height) {
		this.shortSide = shortSide;
		this.longSide = longSide;
		this.height = height;
	}

	@Nullable
	public static UldType of(int sizeX, int sizeY, int sizeZ) {
		int shortSide = Math.min(sizeX, sizeZ);
		int longSide = Math.max(sizeX, sizeZ);
		for (UldType type : values())
			if (type.shortSide == shortSide && type.longSide == longSide && type.height == sizeY)
				return type;
		return null;
	}

}
