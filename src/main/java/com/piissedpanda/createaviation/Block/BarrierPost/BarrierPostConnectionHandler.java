package com.piissedpanda.createaviation.Block.BarrierPost;

import com.piissedpanda.createaviation.CABlocks;
import com.piissedpanda.createaviation.CALang;
import com.piissedpanda.createaviation.foundation.config.CAConfigs;
import com.simibubi.create.AllSoundEvents;

import net.createmod.catnip.platform.CatnipServices;
import com.simibubi.create.foundation.utility.CreateLang;

import net.createmod.catnip.outliner.Outliner;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.HitResult.Type;
import net.minecraft.world.phys.Vec3;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(Dist.CLIENT)
public class BarrierPostConnectionHandler {

	private static BlockPos firstPos;
	private static ResourceKey<Level> firstDim;

	public static boolean onRightClick() {
		Minecraft mc = Minecraft.getInstance();
		if (!isWrench(mc.player.getMainHandItem()))
			return false;
		if (mc.player.isShiftKeyDown())
			return false;
		if (firstPos == null)
			return false;
		boolean missed = false;
		if (mc.hitResult instanceof BlockHitResult bhr && bhr.getType() != Type.MISS)
			if (!(mc.level.getBlockEntity(bhr.getBlockPos()) instanceof BarrierPostBlockEntity))
				missed = true;
		if (!mc.player.isShiftKeyDown() && !missed)
			return false;
		firstPos = null;
		CALang.translate("barrier_post.selection_cleared")
				.sendStatus(mc.player);
		return true;
	}

	@SubscribeEvent
	public static void onItemUsedOnBlock(PlayerInteractEvent.RightClickBlock event) {
		ItemStack itemStack = event.getItemStack();
		BlockPos pos = event.getPos();
		Level level = event.getLevel();
		Player player = event.getEntity();
		BlockState blockState = level.getBlockState(pos);

		if (!CABlocks.BARRIER_POST.has(blockState))
			return;
		if (!isWrench(itemStack))
			return;
		if (player.isShiftKeyDown())
			return;
		if (!player.mayBuild() || player instanceof FakePlayer)
			return;

		event.setCanceled(true);
		event.setCancellationResult(InteractionResult.CONSUME);

		if (!level.isClientSide())
			return;
		if (level.getBlockEntity(pos) instanceof BarrierPostBlockEntity ccbe
				&& ccbe.connections.size() >= CAConfigs.server().maxBarrierPostConnections.get()) {
			CALang.translate("barrier_post.cannot_add_more_connections")
					.style(ChatFormatting.RED)
					.sendStatus(player);
			return;
		}

		if (firstPos == null || firstDim != level.dimension()) {
			firstPos = pos;
			firstDim = level.dimension();
			player.swing(event.getHand());
			return;
		}

		boolean success = validateAndConnect(level, pos, player, itemStack, false);
		firstPos = null;

		if (!success) {
			AllSoundEvents.DENY.play(level, player, pos);
			return;
		}

		level.playSound(
				player,
				pos,
				SoundEvents.LEASH_KNOT_PLACE,
				SoundSource.BLOCKS,
				1.0F,
				1.0F);
	}

	private static boolean isWrench(ItemStack itemStack) {
		return itemStack.is(i -> i.is(Tags.Items.TOOLS_WRENCH));
	}

	public static void clientTick() {
		if (firstPos == null)
			return;

		LocalPlayer player = Minecraft.getInstance().player;
		BlockEntity sourcePost = player.level()
				.getBlockEntity(firstPos);

		if (firstDim != player.level()
				.dimension() || !(sourcePost instanceof BarrierPostBlockEntity)) {
			firstPos = null;
			CALang.translate("barrier_post.selection_cleared")
					.sendStatus(player);
			return;
		}

		ItemStack stack = player.getMainHandItem();
		HitResult hitResult = Minecraft.getInstance().hitResult;

		if (!isWrench(stack)) {
			stack = player.getOffhandItem();
			if (!isWrench(stack))
				return;
		}

		if (hitResult == null || hitResult.getType() != Type.BLOCK) {
			highlightPost(firstPos, 0xFFFFFF, "belt_connect");
			return;
		}

		Level level = player.level();
		BlockHitResult bhr = (BlockHitResult) hitResult;
		BlockPos pos = bhr.getBlockPos();
		BlockState hitState = level.getBlockState(pos);

		if (pos.equals(firstPos)) {
			highlightPost(firstPos, 0xFFFFFF, "belt_connect");
			CALang.translate("barrier_post.select_second")
					.sendStatus(player);
			return;
		}

		if (!(hitState.getBlock() instanceof BarrierPostBlock)) {
			highlightPost(firstPos, 0xFFFFFF, "belt_connect");
			return;
		}

		boolean success = validateAndConnect(level, pos, player, stack, true);

		if (success)
			CALang.translate("barrier_post.valid_connection")
					.style(ChatFormatting.GREEN)
					.sendStatus(player);

		int color = success ? 0x95CD41 : 0xEA5C2B;

		highlightPost(firstPos, color, "belt_connect");
		highlightPost(pos, color, "belt_connect_to");

		Vec3 from = new Vec3(pos.getX() + 0.5, pos.getY() + 17.0 / 16.0, pos.getZ() + 0.5);
		Vec3 to = new Vec3(firstPos.getX() + 0.5, firstPos.getY() + 17.0 / 16.0, firstPos.getZ() + 0.5);
		Vec3 diff = from.subtract(to);

		if (diff.length() < 1)
			return;

		from = from.subtract(diff.normalize()
				.scale(0));
		to = to.add(diff.normalize()
				.scale(0));

		Vec3 heightOffset = new Vec3(0, 2 / 16f, 0);

		Outliner.getInstance().showLine("belt_connect_line", from, to)
				.lineWidth(1 / 16f)
				.colored(color);
		Outliner.getInstance().showLine("belt_connect_line_1", from.add(heightOffset), to.add(heightOffset))
				.lineWidth(1 / 16f)
				.colored(color);

	}

	private static void highlightPost(BlockPos pos, int color, String key) {
		AABB box = new AABB(
				pos.getX() + 6 / 16f,
				pos.getY() + 0 / 16f,
				pos.getZ() + 6 / 16f,
				pos.getX() + 10 / 16f,
				pos.getY() + 20 / 16f,
				pos.getZ() + 10 / 16f);

		Outliner.getInstance()
				.showAABB(key, box)
				.lineWidth(1 / 16f)
				.colored(color);
	}

	public static boolean validateAndConnect(LevelAccessor level, BlockPos pos, Player player, ItemStack wrench,
			boolean simulate) {
		if (!simulate && player.isShiftKeyDown()) {
			CreateLang.translate("barrier_post.selection_cleared")
					.sendStatus(player);
			return false;
		}

		if (pos.equals(firstPos))
			return false;
		if (!pos.closerThan(firstPos, CAConfigs.server().maxBarrierPostLength.get()))
			return fail("barrier_post.too_far");
		if (pos.closerThan(firstPos, 1))
			return fail("barrier_post.too_close");

		Vec3 diff = Vec3.atLowerCornerOf(pos.subtract(firstPos));
		double horizontalDistance = diff.multiply(1, 0, 1)
				.length();

		if (horizontalDistance < 1 && Math.abs(diff.y) > 0.001)
			return fail("barrier_post.cannot_connect_vertically");
		if (horizontalDistance > 0.0001 && Math.abs(diff.y) / horizontalDistance >= 0.5)
			return fail("barrier_post.too_steep");

		BarrierPostBlock barrierPostBlock = CABlocks.BARRIER_POST.get();
		BarrierPostBlockEntity sourcePost = barrierPostBlock.getBlockEntity(level, firstPos);
		BarrierPostBlockEntity targetPost = barrierPostBlock.getBlockEntity(level, pos);

		if (targetPost.connections.size() >= CAConfigs.server().maxBarrierPostConnections.get())
			return fail("barrier_post.cannot_add_more_connections");
		if (targetPost.connections.contains(firstPos.subtract(pos)))
			return fail("barrier_post.already_connected");
		if (sourcePost == null || targetPost == null)
			return fail("barrier_post.blocks_invalid");

		if (simulate)
			return true;

		CatnipServices.NETWORK.sendToServer(new BarrierPostConnectionPacket(firstPos, pos, wrench, true));

		CALang.text("") // Clear status message
				.sendStatus(player);
		firstPos = null;
		firstDim = null;
		return true;
	}

	private static boolean fail(String message) {
		CALang.translate(message)
				.style(ChatFormatting.RED)
				.sendStatus(Minecraft.getInstance().player);
		return false;
	}

}
