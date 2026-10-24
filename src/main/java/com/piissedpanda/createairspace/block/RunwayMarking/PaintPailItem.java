package com.piissedpanda.createairspace.block.RunwayMarking;

import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import com.piissedpanda.createairspace.CABlocks;
import com.piissedpanda.createairspace.CADataComponents;
import com.piissedpanda.createairspace.CAFluids;
import com.piissedpanda.createairspace.CAItems;
import com.piissedpanda.createairspace.CALang;
import com.piissedpanda.createairspace.block.RunwayMarking.RunwayMarkingBlock.Pattern;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;

// Paints runway markings with white or yellow paint, sneak-right-click switches what it paints.
// Filled by a spout or by right-clicking a tank, painted blocks give nothing back.
public class PaintPailItem extends Item {

	public static final int CAPACITY = 1000;
	public static final int PER_BLOCK = 10;

	public PaintPailItem(Properties properties) {
		super(properties);
	}

	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.registerItem(Capabilities.FluidHandler.ITEM, (stack, context) -> new FluidHandlerItemStack(
				() -> CADataComponents.PAINT, stack, CAPACITY) {
			@Override
			public boolean canFillFluidType(FluidStack fluid) {
				return isPaint(fluid);
			}
		}, CAItems.PAINT_PAIL.get());
	}

	public static boolean isPaint(FluidStack fluid) {
		return fluid.is(CAFluids.WHITE_PAINT.get().getSource()) || fluid.is(CAFluids.YELLOW_PAINT.get().getSource());
	}

	public static FluidStack getPaint(ItemStack stack) {
		return stack.getOrDefault(CADataComponents.PAINT, SimpleFluidContent.EMPTY)
				.copy();
	}

	public static ItemStack filled(FluidStack paint) {
		ItemStack stack = CAItems.PAINT_PAIL.asStack();
		stack.set(CADataComponents.PAINT, SimpleFluidContent.copyOf(paint));
		return stack;
	}

	public static boolean isYellow(FluidStack paint) {
		return paint.is(CAFluids.YELLOW_PAINT.get().getSource());
	}

	public static Mode getMode(ItemStack stack) {
		Mode[] modes = Mode.values();
		return modes[Math.floorMod(stack.getOrDefault(CADataComponents.PAINT_MODE, 0), modes.length)];
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
		ItemStack stack = pPlayer.getItemInHand(pHand);
		if (!pPlayer.isShiftKeyDown())
			return InteractionResultHolder.pass(stack);
		nextMode(stack, pPlayer, pLevel);
		return InteractionResultHolder.sidedSuccess(stack, pLevel.isClientSide);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		Player player = context.getPlayer();
		ItemStack stack = context.getItemInHand();
		BlockPos clicked = context.getClickedPos();
		Direction face = context.getClickedFace();

		if (player != null && player.isShiftKeyDown()) {
			nextMode(stack, player, level);
			return InteractionResult.sidedSuccess(level.isClientSide);
		}
		if (level.getCapability(Capabilities.FluidHandler.BLOCK, clicked, face) != null) {
			if (player != null && FluidUtil.interactWithFluidHandler(player, context.getHand(), level, clicked, face))
				return InteractionResult.sidedSuccess(level.isClientSide);
			return InteractionResult.PASS;
		}

		// Paint over an existing marking, otherwise on top of the clicked floor
		boolean repaint = level.getBlockState(clicked)
				.getBlock() instanceof RunwayMarkingBlock;
		if (!repaint && face != Direction.UP)
			return InteractionResult.PASS;
		if (level.isClientSide)
			return InteractionResult.SUCCESS;

		BlockPos pos = repaint ? clicked : clicked.above();
		FluidStack paint = getPaint(stack);
		Mode mode = getMode(stack);
		int cost = mode == Mode.NUMBER ? RunwayNumberBlock.COLUMNS * RunwayNumberBlock.ROWS * PER_BLOCK : PER_BLOCK;
		boolean creative = player != null && player.isCreative();
		if (paint.isEmpty() || !creative && paint.getAmount() < cost) {
			if (player != null)
				CALang.translate("paint_pail.not_enough_paint")
						.style(ChatFormatting.RED)
						.sendStatus(player);
			return InteractionResult.FAIL;
		}

		Direction facing = context.getHorizontalDirection();
		boolean yellow = isYellow(paint);
		boolean painted;
		if (mode == Mode.NUMBER)
			painted = RunwayNumberBlock.paint(level, pos, facing, (yellow ? CABlocks.YELLOW_RUNWAY_NUMBER
					: CABlocks.WHITE_RUNWAY_NUMBER).get());
		else
			painted = paintMarking(level, pos, (yellow ? CABlocks.YELLOW_RUNWAY_MARKING
					: CABlocks.WHITE_RUNWAY_MARKING).getDefaultState()
					.setValue(RunwayMarkingBlock.FACING, facing)
					.setValue(RunwayMarkingBlock.PATTERN, mode.pattern));
		if (!painted)
			return InteractionResult.FAIL;

		if (!creative)
			stack.set(CADataComponents.PAINT, SimpleFluidContent.copyOf(paint.copyWithAmount(paint.getAmount()
					- cost)));
		level.playSound(null, pos, SoundEvents.HONEY_BLOCK_PLACE, SoundSource.BLOCKS, 0.6f, 1.2f);
		return InteractionResult.SUCCESS;
	}

	private static boolean paintMarking(Level level, BlockPos pos, BlockState marking) {
		BlockState existing = level.getBlockState(pos);
		if (!existing.canBeReplaced() && !(existing.getBlock() instanceof RunwayMarkingBlock))
			return false;
		if (!marking.canSurvive(level, pos) || existing == marking)
			return false;
		return level.setBlock(pos, marking, Block.UPDATE_ALL);
	}

	private static void nextMode(ItemStack stack, Player player, Level level) {
		if (level.isClientSide)
			return;
		Mode mode = Mode.values()[(getMode(stack).ordinal() + 1) % Mode.values().length];
		stack.set(CADataComponents.PAINT_MODE, mode.ordinal());
		CALang.translate("paint_pail.mode", CALang.translate(mode.langKey())
				.component())
				.sendStatus(player);
	}

	@Override
	public boolean isBarVisible(ItemStack pStack) {
		return !getPaint(pStack).isEmpty();
	}

	@Override
	public int getBarWidth(ItemStack pStack) {
		return Math.round(13f * getPaint(pStack).getAmount() / CAPACITY);
	}

	@Override
	public int getBarColor(ItemStack pStack) {
		return isYellow(getPaint(pStack)) ? 0xE8B618 : 0xEEEEEE;
	}

	@Override
	public void appendHoverText(ItemStack pStack, TooltipContext pContext, List<Component> pTooltipComponents,
			TooltipFlag pTooltipFlag) {
		FluidStack paint = getPaint(pStack);
		if (paint.isEmpty())
			pTooltipComponents.add(CALang.translate("paint_pail.empty")
					.style(ChatFormatting.GRAY)
					.component());
		else
			pTooltipComponents.add(CALang.builder()
					.add(paint.getHoverName()
							.copy())
					.text(": " + paint.getAmount() + " / " + CAPACITY + " mB")
					.style(ChatFormatting.GRAY)
					.component());
		pTooltipComponents.add(CALang.translate("paint_pail.mode", CALang.translate(getMode(pStack).langKey())
				.component())
				.style(ChatFormatting.GOLD)
				.component());
	}

	public enum Mode {
		CENTER_LINE(Pattern.CENTER_LINE), EDGE_LINE(Pattern.EDGE_LINE), DOUBLE_LINE(Pattern.DOUBLE_LINE),
		BAR(Pattern.BAR), FULL(Pattern.FULL), HOLDING_POSITION(Pattern.HOLDING_POSITION), CURVE(Pattern.CURVE),
		DIAGONAL(Pattern.DIAGONAL), NUMBER(null);

		@Nullable
		public final Pattern pattern;

		Mode(@Nullable Pattern pattern) {
			this.pattern = pattern;
		}

		public String langKey() {
			return pattern == null ? "paint_pail.mode.number"
					: "runway_marking.pattern." + name().toLowerCase(Locale.ROOT);
		}
	}

}
