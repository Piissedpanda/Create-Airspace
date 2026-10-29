package com.piissedpanda.createairspace.block.CheckIn;

import com.piissedpanda.createairspace.CABlocks;
import com.piissedpanda.createairspace.CALang;
import com.simibubi.create.foundation.gui.AllGuiTextures;
import com.simibubi.create.foundation.gui.AllIcons;
import com.simibubi.create.foundation.gui.widget.IconButton;
import com.simibubi.create.foundation.gui.widget.ScrollInput;

import net.createmod.catnip.gui.AbstractSimiScreen;
import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.catnip.gui.element.BoxElement;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

// Weight range of the luggage scale: below the minimum the comparator gives 0, at the maximum 15
public class LuggageScaleScreen extends AbstractSimiScreen {

	private static final int WIDTH = 190, HEIGHT = 96;

	private final LuggageScaleBlockEntity be;
	private final ItemStack icon = CABlocks.LUGGAGE_SCALE.asStack();
	private ScrollInput min, max;

	public LuggageScaleScreen(LuggageScaleBlockEntity be) {
		super(CALang.translate("luggage_scale.title").component());
		this.be = be;
	}

	public static void open(LuggageScaleBlockEntity be) {
		ScreenOpener.open(new LuggageScaleScreen(be));
	}

	@Override
	protected void init() {
		setWindowSize(WIDTH, HEIGHT);
		super.init();
		int x = guiLeft, y = guiTop;

		min = new ScrollInput(x + 14, y + 26, 115, 18).withRange(0, LuggageScaleBlockEntity.MAX_KG)
				.titled(CALang.translate("luggage_scale.min").component())
				.withStepFunction(c -> c.shift ? 10 : 1)
				.calling(value -> {
					if (max != null && max.getState() <= value)
						max.setState(value + 1);
				})
				.setState(be.minKg);
		max = new ScrollInput(x + 14, y + 50, 115, 18).withRange(1, LuggageScaleBlockEntity.MAX_KG + 1)
				.titled(CALang.translate("luggage_scale.max").component())
				.withStepFunction(c -> c.shift ? 10 : 1)
				.calling(value -> {
					if (min != null && min.getState() >= value)
						min.setState(value - 1);
				})
				.setState(be.maxKg);
		addRenderableWidgets(min, max);

		IconButton confirm = new IconButton(x + WIDTH - 30, y + HEIGHT - 26, AllIcons.I_CONFIRM);
		confirm.withCallback(this::onClose);
		addRenderableWidget(confirm);
	}

	@Override
	protected void renderWindow(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
		int x = guiLeft, y = guiTop;
		new BoxElement().withBackground(0xEE1E1E24)
				.gradientBorder(0xFFB59A6A, 0xFF7A6240)
				.at(x, y, 0)
				.withBounds(WIDTH, HEIGHT)
				.render(graphics);
		graphics.drawString(font, title, x + 10, y + 8, 0xFFE8D7B0, false);

		AllGuiTextures.THRESHOLD_SWITCH_MISC_INPUTS.render(graphics, x + 12, y + 24);
		AllGuiTextures.THRESHOLD_SWITCH_MISC_INPUTS.render(graphics, x + 12, y + 48);
		graphics.drawString(font, Component.literal("≥ ")
				.append(CALang.translate("luggage_scale.kg", min.getState()).component()), x + 20, y + 31, 0xFFFFFFFF,
				true);
		graphics.drawString(font, Component.literal("≤ ")
				.append(CALang.translate("luggage_scale.kg", max.getState()).component()), x + 20, y + 55, 0xFFFFFFFF,
				true);
		graphics.drawString(font, CALang.translate("luggage_scale.signal_min").component(), x + 132, y + 31, 0xFFB0B0B0,
				false);
		graphics.drawString(font, CALang.translate("luggage_scale.signal_max").component(), x + 132, y + 55, 0xFFB0B0B0,
				false);
		graphics.drawString(font, CALang.translate("luggage_scale.hint").component(), x + 10, y + HEIGHT - 20,
				0xFF909090,
				false);

		GuiGameElement.of(icon).<GuiGameElement.GuiRenderBuilder>at(x + WIDTH + 6, y + HEIGHT - 48, -200)
				.scale(3)
				.render(graphics);
	}

	@Override
	public void removed() {
		CatnipServices.NETWORK.sendToServer(new ConfigureLuggageScalePacket(be.getBlockPos(), min.getState(),
				max.getState()));
	}

}
