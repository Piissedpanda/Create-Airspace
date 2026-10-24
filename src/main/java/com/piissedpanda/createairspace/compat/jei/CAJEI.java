package com.piissedpanda.createairspace.compat.jei;

import java.util.ArrayList;
import java.util.List;

import com.piissedpanda.createairspace.CAFluids;
import com.piissedpanda.createairspace.CAItems;
import com.piissedpanda.createairspace.CreateAirspace;
import com.piissedpanda.createairspace.block.RunwayMarking.PaintPailItem;
import com.simibubi.create.content.fluids.transfer.FillingRecipe;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import com.tterrag.registrate.util.entry.FluidEntry;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

// Create's JEI plugin skips items that stay the same item when filled, so the paint pail is added here
@JeiPlugin
public class CAJEI implements IModPlugin {

	private static final RecipeType<RecipeHolder<FillingRecipe>> SPOUT_FILLING = RecipeType
			.createRecipeHolderType(ResourceLocation.fromNamespaceAndPath("create", "spout_filling"));

	@Override
	public ResourceLocation getPluginUid() {
		return CreateAirspace.asResource("jei_plugin");
	}

	// Empty, white and yellow pails are separate entries, the amount and mode do
	// not count
	@Override
	public void registerItemSubtypes(ISubtypeRegistration registration) {
		registration.registerSubtypeInterpreter(CAItems.PAINT_PAIL.get(), new ISubtypeInterpreter<ItemStack>() {
			@Override
			public Object getSubtypeData(ItemStack stack, UidContext context) {
				return getLegacyStringSubtypeInfo(stack, context);
			}

			@Override
			public String getLegacyStringSubtypeInfo(ItemStack stack, UidContext context) {
				FluidStack paint = PaintPailItem.getPaint(stack);
				return paint.isEmpty() ? "empty"
						: BuiltInRegistries.FLUID.getKey(paint.getFluid())
								.toString();
			}
		});
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		List<RecipeHolder<FillingRecipe>> recipes = new ArrayList<>();
		for (FluidEntry<?> paint : List.of(CAFluids.WHITE_PAINT, CAFluids.YELLOW_PAINT)) {
			Fluid fluid = paint.get()
					.getSource();
			ResourceLocation id = CreateAirspace.asResource("fill_paint_pail_with_" + paint.getId()
					.getPath());
			FillingRecipe recipe = new StandardProcessingRecipe.Builder<>(FillingRecipe::new, id)
					.withItemIngredients(Ingredient.of(CAItems.PAINT_PAIL))
					.withFluidIngredients(SizedFluidIngredient.of(fluid, PaintPailItem.CAPACITY))
					.withSingleItemOutput(PaintPailItem.filled(new FluidStack(fluid, PaintPailItem.CAPACITY)))
					.build();
			recipes.add(new RecipeHolder<>(id, recipe));
		}
		registration.addRecipes(SPOUT_FILLING, recipes);
	}

}
