package com.piissedpanda.createairspace.block.Cargo;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import javax.annotation.Nullable;

import com.piissedpanda.createairspace.block.Cargo.CargoContainerBlock.Point;
import com.simibubi.create.api.contraption.BlockMovementChecks;
import com.simibubi.create.api.contraption.BlockMovementChecks.CheckResult;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

// Forming and splitting cargo containers
public class CargoContainers {

	// Sorted bottom to top, so the combined inventory has a stable slot order
	public static final Comparator<BlockPos> ORDER = Comparator.<BlockPos>comparingInt(BlockPos::getY)
			.thenComparingInt(BlockPos::getZ)
			.thenComparingInt(BlockPos::getX);

	// The blocks of a finished container stick together when moved, so Simulated's
	// Physics Assembler and Create contraptions take the whole container without glue,
	// but nothing else around it
	public static void registerMovementChecks() {
		BlockMovementChecks.registerAttachedCheck((state, level, pos, direction) -> {
			if (!(state.getBlock() instanceof CargoContainerBlock))
				return CheckResult.PASS;
			return CheckResult.of(CargoContainerBlock.isConnected(state, direction));
		});
	}

	private static boolean isContainer(Level level, BlockPos pos) {
		return level.getBlockState(pos)
				.getBlock() instanceof CargoContainerBlock;
	}

	private static boolean isFormed(Level level, BlockPos pos) {
		BlockState state = level.getBlockState(pos);
		return state.getBlock() instanceof CargoContainerBlock && state.getValue(CargoContainerBlock.FORMED);
	}

	// All blocks of the finished container at pos, following the connections
	public static List<BlockPos> members(Level level, BlockPos pos) {
		List<BlockPos> members = new ArrayList<>();
		if (!isFormed(level, pos))
			return members;
		Set<BlockPos> visited = new HashSet<>();
		Queue<BlockPos> frontier = new ArrayDeque<>();
		frontier.add(pos);
		visited.add(pos);
		while (!frontier.isEmpty() && members.size() < UldType.MAX_BLOCKS) {
			BlockPos current = frontier.poll();
			members.add(current);
			BlockState state = level.getBlockState(current);
			for (Direction side : Direction.values()) {
				BlockPos next = current.relative(side);
				if (CargoContainerBlock.isConnected(state, side) && visited.add(next)
						&& CargoContainerBlock.isConnected(level.getBlockState(next), side.getOpposite()))
					frontier.add(next);
			}
		}
		members.sort(ORDER);
		return members;
	}

	// Loose blocks join into a container when they form a ULD shape, alone or
	// together with containers next to them (an LD2 can grow into an LD3)
	public static void tryForm(Level level, BlockPos start) {
		if (!isContainer(level, start) || isFormed(level, start))
			return;

		Set<BlockPos> loose = new LinkedHashSet<>();
		Queue<BlockPos> frontier = new ArrayDeque<>();
		frontier.add(start);
		loose.add(start);
		while (!frontier.isEmpty()) {
			BlockPos current = frontier.poll();
			for (Direction side : Direction.values()) {
				BlockPos next = current.relative(side);
				if (!isContainer(level, next) || isFormed(level, next) || !loose.add(next))
					continue;
				if (loose.size() > UldType.MAX_BLOCKS)
					return;
				frontier.add(next);
			}
		}

		List<List<BlockPos>> neighbours = new ArrayList<>();
		Set<BlockPos> seen = new HashSet<>();
		for (BlockPos pos : loose)
			for (Direction side : Direction.values()) {
				BlockPos next = pos.relative(side);
				if (seen.contains(next) || !isFormed(level, next))
					continue;
				List<BlockPos> other = members(level, next);
				seen.addAll(other);
				neighbours.add(other);
			}

		Direction facing = level.getBlockEntity(start) instanceof CargoContainerBlockEntity be ? be.placedFacing
				: Direction.NORTH;

		List<Set<BlockPos>> candidates = new ArrayList<>();
		candidates.add(loose);
		if (!neighbours.isEmpty()) {
			Set<BlockPos> all = new HashSet<>(loose);
			neighbours.forEach(all::addAll);
			candidates.add(all);
		}
		if (neighbours.size() > 1)
			for (List<BlockPos> other : neighbours) {
				Set<BlockPos> merged = new HashSet<>(loose);
				merged.addAll(other);
				candidates.add(merged);
			}

		for (Set<BlockPos> candidate : candidates)
			if (shape(candidate) != null) {
				form(level, candidate, facing);
				return;
			}
	}

	@Nullable
	private static UldType shape(Set<BlockPos> blocks) {
		if (blocks.size() > UldType.MAX_BLOCKS)
			return null;
		BlockPos min = min(blocks), max = max(blocks);
		int sizeX = max.getX() - min.getX() + 1, sizeY = max.getY() - min.getY() + 1,
				sizeZ = max.getZ() - min.getZ() + 1;
		if (sizeX * sizeY * sizeZ != blocks.size())
			return null;
		return UldType.of(sizeX, sizeY, sizeZ);
	}

	private static BlockPos min(Set<BlockPos> blocks) {
		int x = Integer.MAX_VALUE, y = Integer.MAX_VALUE, z = Integer.MAX_VALUE;
		for (BlockPos pos : blocks) {
			x = Math.min(x, pos.getX());
			y = Math.min(y, pos.getY());
			z = Math.min(z, pos.getZ());
		}
		return new BlockPos(x, y, z);
	}

	private static BlockPos max(Set<BlockPos> blocks) {
		int x = Integer.MIN_VALUE, y = Integer.MIN_VALUE, z = Integer.MIN_VALUE;
		for (BlockPos pos : blocks) {
			x = Math.max(x, pos.getX());
			y = Math.max(y, pos.getY());
			z = Math.max(z, pos.getZ());
		}
		return new BlockPos(x, y, z);
	}

	private static void form(Level level, Set<BlockPos> blocks, Direction facing) {
		UldType type = shape(blocks);
		BlockPos min = min(blocks), max = max(blocks);
		// The LD11 docks on one of its long sides
		if (type == UldType.LD11 && (max.getX() - min.getX() == 2) == (facing.getAxis() == Direction.Axis.X))
			facing = facing.getClockWise();

		// Looking at the dock side from outside, columns count from left to right
		Direction right = facing.getOpposite()
				.getClockWise();
		int left = Integer.MAX_VALUE, width = 0;
		for (BlockPos pos : blocks)
			left = Math.min(left, along(pos, right));
		for (BlockPos pos : blocks)
			width = Math.max(width, along(pos, right) - left + 1);

		for (BlockPos pos : blocks) {
			BlockState state = level.getBlockState(pos)
					.setValue(CargoContainerBlock.FORMED, true)
					.setValue(CargoContainerBlock.FACING, facing);
			for (Direction side : Direction.values())
				state = state.setValue(CargoContainerBlock.CONNECTED.get(side), blocks.contains(pos.relative(side)));
			boolean contour = type != null && type.slope && pos.getY() == min.getY()
					&& !blocks.contains(pos.relative(facing.getOpposite()));
			Point point = Point.NONE;
			if (!blocks.contains(pos.relative(facing)))
				point = point(type, width, along(pos, right) - left, pos.getY() - min.getY());
			if (type == UldType.AMA && pos.equals(min.offset(1, 0, 1)))
				point = Point.LOCK_BOTTOM;
			state = state.setValue(CargoContainerBlock.SLOPE, contour)
					.setValue(CargoContainerBlock.POINT, point);
			level.setBlock(pos, state, Block.UPDATE_ALL);
		}
		refresh(level, blocks);
	}

	private static int along(BlockPos pos, Direction direction) {
		return pos.getX() * direction.getStepX() + pos.getZ() * direction.getStepZ();
	}

	// Lock in the middle of the bottom row, ports next to it, or above it on an LD2.
	// The AMA has its bottom lock in the centre instead of under the side lock
	private static Point point(@Nullable UldType type, int width, int column, int row) {
		int lock = width == 3 ? 1 : 0;
		if (row == 0 && column == lock)
			return type == UldType.AMA ? Point.LOCK_SIDE : Point.LOCK;
		if (width == 1)
			return row == 1 ? Point.PORT : Point.NONE;
		return row == 0 ? Point.PORT : Point.NONE;
	}

	// Breaking one block turns the rest of its container back into loose blocks,
	// they keep their items
	public static void dissolveAround(Level level, BlockPos removed, BlockState removedState) {
		Set<BlockPos> rest = new HashSet<>();
		for (Direction side : Direction.values())
			if (removedState.getValue(CargoContainerBlock.CONNECTED.get(side)))
				rest.addAll(members(level, removed.relative(side)));
		for (BlockPos pos : rest)
			level.setBlock(pos, CargoContainerBlock.unformed(level.getBlockState(pos)), Block.UPDATE_ALL);
		refresh(level, rest);
	}

	// Turns the dock side (and the LD3 contour) to the next side
	public static void turn(Level level, BlockPos pos) {
		List<BlockPos> members = members(level, pos);
		if (members.isEmpty())
			return;
		Direction facing = level.getBlockState(pos)
				.getValue(CargoContainerBlock.FACING);
		Set<BlockPos> blocks = new HashSet<>(members);
		// An LD11 has only two long sides
		form(level, blocks, shape(blocks) == UldType.LD11 ? facing.getOpposite() : facing.getClockWise());
	}

	private static void refresh(Level level, Iterable<BlockPos> blocks) {
		for (BlockPos pos : blocks) {
			if (level.getBlockEntity(pos) instanceof CargoContainerBlockEntity be)
				be.invalidateContainer();
			level.invalidateCapabilities(pos);
		}
	}

}
