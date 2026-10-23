package com.piissedpanda.createaviation.block.BarrierPost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import org.joml.Vector3f;

import com.piissedpanda.createaviation.block.BarrierPost.BarrierPostShape.BarrierPostBB;
import com.piissedpanda.createaviation.block.BarrierPost.BarrierPostShape.BarrierPostOBB;
import com.simibubi.create.api.contraption.transformable.TransformableBlockEntity;
import com.simibubi.create.content.contraptions.StructureTransform;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;

import net.createmod.catnip.codecs.CatnipCodecUtils;
import net.createmod.catnip.codecs.CatnipCodecs;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.Clearable;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class BarrierPostBlockEntity extends SmartBlockEntity implements TransformableBlockEntity, Clearable {

	public record ConnectionStats(float tangentAngle, float beltLength, Vec3 start, Vec3 end) {
	}

	public Set<BlockPos> connections = new HashSet<>();
	public Map<BlockPos, ConnectionStats> connectionStats;
	public Map<BlockPos, DyeColor> beltColors = new HashMap<>();
	public int visualUpdateVersion;

	public boolean checkInvalid;

	BlockPos beltDestroyedEffectToSend;

	public BarrierPostBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
		super(typeIn, pos, state);
		checkInvalid = true;
	}

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
	}

	@Override
	protected AABB createRenderBoundingBox() {
		return new AABB(worldPosition).inflate(connections.isEmpty() ? 1 : 64);
	}

	@Override
	public void lazyTick() {
		super.lazyTick();
		updateBeltShapes();
	}

	@Override
	public void tick() {
		super.tick();

		if (checkInvalid && !level.isClientSide()) {
			checkInvalid = false;
			removeInvalidConnections();
		}
	}

	public void removeInvalidConnections() {
		boolean changed = false;
		for (Iterator<BlockPos> iterator = connections.iterator(); iterator.hasNext();) {
			BlockPos next = iterator.next();
			BlockPos target = worldPosition.offset(next);
			if (!level.isLoaded(target))
				continue;
			if (level.getBlockEntity(target) instanceof BarrierPostBlockEntity ccbe
					&& ccbe.connections.contains(next.multiply(-1)))
				continue;
			iterator.remove();
			changed = true;
		}
		if (changed)
			notifyUpdate();
	}

	public void notifyConnectedToValidate() {
		for (BlockPos blockPos : connections) {
			BlockPos target = worldPosition.offset(blockPos);
			if (!level.isLoaded(target))
				continue;
			if (level.getBlockEntity(target) instanceof BarrierPostBlockEntity ccbe)
				ccbe.checkInvalid = true;
		}
	}

	@Override
	public void notifyUpdate() {
		level.blockEntityChanged(worldPosition);
		sendData();
	}

	public void prepareStats() {
		if (connectionStats == null) {
			connectionStats = new HashMap<>();
			connections.forEach(this::calculateConnectionStats);
		}
	}

	private void calculateConnectionStats(BlockPos connection) {
		float direction = Mth.RAD_TO_DEG * (float) Mth.atan2(connection.getX(), connection.getZ());

		Vec3 start = Vec3.atBottomCenterOf(worldPosition)
				.add(0, 18 / 16f, 0);

		Vec3 end = Vec3.atBottomCenterOf(worldPosition.offset(connection))
				.add(0, 18 / 16f, 0);

		float length = (float) start.distanceTo(end);
		connectionStats.put(connection, new ConnectionStats(direction, length, start, end));
	}

	public void markVisualDirty() {
		visualUpdateVersion++;
	}

	public boolean addConnectionTo(BlockPos target) {
		BlockPos localTarget = target.subtract(worldPosition);
		boolean added = connections.add(localTarget);
		if (added) {
			markVisualDirty();
			notifyUpdate();
			calculateConnectionStats(localTarget);
			updateBeltShapes();
		}

		return added;
	}

	public void beltDestroyed(BlockPos target, boolean sendEffect) {
		if (sendEffect) {
			beltDestroyedEffectToSend = target;
			sendData();
		}
	}

	public boolean removeConnectionTo(BlockPos target) {
		BlockPos localTarget = target.subtract(worldPosition);
		if (!connections.contains(localTarget))
			return false;

		connections.remove(localTarget);
		connectionStats.remove(localTarget);
		beltColors.remove(localTarget);
		markVisualDirty();
		notifyUpdate();
		updateBeltShapes();

		return true;
	}

	private void updateBeltShapes() {
		prepareStats();

		List<BarrierPostShape> shapes = new ArrayList<>();
		shapes.add(new BarrierPostBB(Vec3.atBottomCenterOf(BlockPos.ZERO)));
		for (BlockPos target : connections) {
			ConnectionStats stats = connectionStats.get(target);
			if (stats == null)
				continue;
			Vec3 localStart = stats.start.subtract(Vec3.atLowerCornerOf(worldPosition));
			Vec3 localEnd = stats.end.subtract(Vec3.atLowerCornerOf(worldPosition));
			shapes.add(new BarrierPostOBB(target, localStart, localEnd));
		}

		if (level != null && level.isClientSide())
			BarrierPostInteractionHandler.loadedBelts.get(level)
					.put(worldPosition, shapes);
	}

	@Override
	public void remove() {
		super.remove();
		if (level == null || !level.isClientSide())
			return;
		for (BlockPos blockPos : connections)
			spawnDestroyParticles(blockPos);
	}

	private void spawnDestroyParticles(BlockPos blockPos) {
		int color = getBeltColor(blockPos).getTextureDiffuseColor();
		Vector3f colorVec = new Vector3f(
				((color >> 16) & 0xFF) / 255f,
				((color >> 8) & 0xFF) / 255f,
				(color & 0xFF) / 255f);

		forPointsAlongBelt(blockPos, (int) Math.round(Vec3.atLowerCornerOf(blockPos)
				.length() * 8),
				vec -> level.addParticle(
						new DustParticleOptions(colorVec, 1f),
						vec.x, vec.y, vec.z, 0, 0, 0));
	}

	@Override
	public void clearContent() {
		connections.clear();
		beltColors.clear();

		if (connectionStats != null)
			connectionStats.clear();
	}

	@Override
	public void destroy() {
		super.destroy();

		for (BlockPos blockPos : connections) {
			beltDestroyed(blockPos, false);
			if (level.getBlockEntity(worldPosition.offset(blockPos)) instanceof BarrierPostBlockEntity clbe)
				clbe.removeConnectionTo(worldPosition);
		}
	}

	public boolean forPointsAlongBelt(BlockPos connection, int positions, Consumer<Vec3> callback) {
		prepareStats();
		ConnectionStats stats = connectionStats.get(connection);
		if (stats == null)
			return false;

		Vec3 start = stats.start;
		Vec3 direction = stats.end.subtract(start);

		for (int i = 0; i < positions; i++) {
			double t = (0.5 + i) / positions;
			callback.accept(start.add(direction.scale(t)));
		}

		return true;
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if (level != null && level.isClientSide())
			BarrierPostInteractionHandler.loadedBelts.get(level)
					.invalidate(worldPosition);
	}

	private void writeBeltColors(CompoundTag tag) {
		CompoundTag colors = new CompoundTag();

		for (Map.Entry<BlockPos, DyeColor> entry : beltColors.entrySet()) {
			CompoundTag color = new CompoundTag();

			color.put("Pos", NbtUtils.writeBlockPos(entry.getKey()));
			color.putString("Color", entry.getValue().getName());

			colors.put(entry.getKey().toShortString(), color);
		}

		tag.put("BeltColors", colors);
	}

	private void readBeltColors(CompoundTag tag) {
		beltColors.clear();

		if (!tag.contains("BeltColors", CompoundTag.TAG_COMPOUND))
			return;

		CompoundTag colors = tag.getCompound("BeltColors");

		for (String key : colors.getAllKeys()) {
			CompoundTag colorTag = colors.getCompound(key);

			if (!colorTag.contains("Pos") || !colorTag.contains("Color"))
				continue;

			BlockPos pos = NbtUtils.readBlockPos(colorTag, "Pos").orElse(null);
			if (pos == null)
				continue;

			DyeColor color = DyeColor.byName(
					colorTag.getString("Color"),
					DyeColor.BLACK);

			beltColors.put(pos, color);
		}
	}

	@Override
	public void writeSafe(CompoundTag tag, HolderLookup.Provider registries) {
		super.writeSafe(tag, registries);
		tag.put("Connections",
				CatnipCodecUtils.encode(CatnipCodecs.set(BlockPos.CODEC), registries, connections).orElseThrow());

		writeBeltColors(tag);
	}

	@Override
	protected void write(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		super.write(compound, registries, clientPacket);
		if (clientPacket && beltDestroyedEffectToSend != null) {
			compound.put("DestroyEffect", NbtUtils.writeBlockPos(beltDestroyedEffectToSend));
			beltDestroyedEffectToSend = null;
		}

		compound.put("Connections",
				CatnipCodecUtils.encode(CatnipCodecs.set(BlockPos.CODEC), registries, connections).orElseThrow());

		writeBeltColors(compound);
	}

	@Override
	protected void read(CompoundTag compound, HolderLookup.Provider registries, boolean clientPacket) {
		super.read(compound, registries, clientPacket);
		if (clientPacket && compound.contains("DestroyEffect") && level != null)
			spawnDestroyParticles(NBTHelper.readBlockPos(compound, "DestroyEffect"));

		int sizeBefore = connections.size();
		connections.clear();
		CatnipCodecUtils.decode(CatnipCodecs.set(BlockPos.CODEC), registries, compound.get("Connections"))
				.ifPresent(connections::addAll);

		readBeltColors(compound);

		beltColors.keySet().retainAll(connections);

		connectionStats = null;
		prepareStats();
		updateBeltShapes();
		if (connections.size() != sizeBefore)
			markVisualDirty();

		if (connections.size() != sizeBefore && level != null && level.isClientSide)
			invalidateRenderBoundingBox();
	}

	@Override
	public void transform(BlockEntity be, StructureTransform transform) {
		if (connections == null || connections.isEmpty())
			return;

		connections = new HashSet<>(connections.stream()
				.map(transform::applyWithoutOffset)
				.toList());

		Map<BlockPos, DyeColor> transformedColors = new HashMap<>();

		for (Map.Entry<BlockPos, DyeColor> entry : beltColors.entrySet()) {
			BlockPos transformedPos = transform.applyWithoutOffset(entry.getKey());
			transformedColors.put(transformedPos, entry.getValue());
		}

		beltColors = transformedColors;

		connectionStats = null;
		markVisualDirty();
		notifyUpdate();
	}

	public DyeColor getBeltColor(BlockPos connection) {
		return beltColors.getOrDefault(connection, DyeColor.BLACK);
	}

	public void setBeltColor(BlockPos connection, DyeColor color) {
		if (!connections.contains(connection))
			return;

		if (beltColors.get(connection) == color)
			return;

		beltColors.put(connection, color);
		markVisualDirty();
		notifyUpdate();
	}

}
