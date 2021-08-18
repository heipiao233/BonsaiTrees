package com.davenonymous.bonsaitrees2.compat.jei;

import com.davenonymous.bonsaitrees2.BonsaiTrees2;
import com.davenonymous.bonsaitrees2.config.Config;
import com.davenonymous.bonsaitrees2.registry.SoilCompatibility;
import com.davenonymous.bonsaitrees2.registry.sapling.SaplingDrop;
import com.davenonymous.bonsaitrees2.registry.sapling.SaplingInfo;
import com.davenonymous.bonsaitrees2.registry.soil.SoilInfo;
import com.davenonymous.bonsaitrees2.render.TreeModels;
import com.davenonymous.libnonymous.render.MultiBlockModelWorldReader;
import com.davenonymous.libnonymous.render.MultiblockBlockModel;
import com.davenonymous.libnonymous.render.MultiblockBlockModelRenderer;
import com.davenonymous.libnonymous.render.RenderTickCounter;
import com.davenonymous.libnonymous.utils.TickTimeHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.ingredient.ITooltipCallback;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.AmbientOcclusionStatus;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import org.lwjgl.opengl.GL11;

import java.util.*;

public class BonsaiRecipeWrapper implements IRecipeCategoryExtension, ITooltipCallback<ItemStack> {
    SaplingInfo sapling;
    public float[] slotChances;
    public Map<ResourceLocation, Float> tickModifiers;

    public BonsaiRecipeWrapper(SaplingInfo sapling) {
        this.sapling = sapling;
    }

    @Override
    public void drawInfo(int recipeWidth, int recipeHeight, MatrixStack stack, double mouseX, double mouseY) {
        MultiblockBlockModel model = TreeModels.get(sapling.getId());
        if(model == null) {
            return;
        }

        float angle = RenderTickCounter.renderTicks * 45.0f / 128.0f;

        stack.push();

        // Init RenderSystem
        RenderSystem.enableAlphaTest();
        RenderSystem.alphaFunc(GL11.GL_GREATER, 0.1f);
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);


        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);

        RenderSystem.disableFog();
        RenderSystem.disableLighting();
        RenderHelper.disableStandardItemLighting();

        RenderSystem.enableBlend();
        RenderSystem.enableCull();
        RenderSystem.enableAlphaTest();

        if (Minecraft.isAmbientOcclusionEnabled()) {
            RenderSystem.shadeModel(GL11.GL_SMOOTH);
        } else {
            RenderSystem.shadeModel(GL11.GL_FLAT);
        }

        RenderSystem.disableRescaleNormal();

        stack.translate(0F, 0F, 216.5F);


        stack.translate(50.0f, 20.0f, 0.0f);

        // Shift it a bit down so one can properly see 3d
        stack.rotate(new Quaternion(-25.0f, 1.0f, 0.0f, 0.0f));

        // Rotate per our calculated time
        stack.rotate(new Quaternion(angle, 0.0f, 1.0f, 0.0f));

        float scale = (float) model.getScaleRatio(true);
        stack.scale(scale, scale, scale);

        float progress = 40.0f;
        stack.scale(progress, progress, progress);



        stack.rotate(new Quaternion(180.0f, 1.0f, 0.0f, 0.0f));

        stack.translate(
                (model.width + 1) / -2.0f,
                (model.height + 1) / -2.0f,
                (model.depth + 1) / -2.0f
        );

        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).setBlurMipmapDirect(false, false);

        GL11.glFrontFace(GL11.GL_CW);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        IRenderTypeBuffer buffer = IRenderTypeBuffer.getImpl(builder);

        AmbientOcclusionStatus before = Minecraft.getInstance().gameSettings.ambientOcclusionStatus;
        Minecraft.getInstance().gameSettings.ambientOcclusionStatus = AmbientOcclusionStatus.OFF;
        MultiblockBlockModelRenderer.renderModel(model, new MatrixStack(), buffer, 0xff0000,  OverlayTexture.NO_OVERLAY, BonsaiTrees2.proxy.getClientWorld(), BonsaiTrees2.proxy.getClientPlayer().getPosition());
        Minecraft.getInstance().gameSettings.ambientOcclusionStatus = before;

        textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        textureManager.getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();

        ((IRenderTypeBuffer.Impl) buffer).finish();

        GL11.glFrontFace(GL11.GL_CCW);

        RenderSystem.disableBlend();

        stack.pop();
    }

    @Override
    public void onTooltip(int slot, boolean isInput, ItemStack stack, List<ITextComponent> tooltip) {
        if(stack.isEmpty()) {
            return;
        }

        if(isInput) {
            if(slot == 0) {
                // Sapling slot
                String timeToGrow = TickTimeHelper.getDuration(sapling.baseTicks);
                tooltip.add(tooltip.size()-1, new TranslationTextComponent("bonsaitrees.jei.category.sapling.growtime", timeToGrow).mergeStyle(TextFormatting.YELLOW));
            }

            if(slot == 1) {
                float tickModifier = tickModifiers.getOrDefault(stack.getItem().getRegistryName(), 1.0f);
                String timeToGrow = TickTimeHelper.getDuration((int) (sapling.baseTicks * tickModifier));
                tooltip.add(tooltip.size()-1, new TranslationTextComponent("bonsaitrees.jei.category.sapling.soiltime", timeToGrow).mergeStyle(TextFormatting.YELLOW));
            }
        } else {
            // Some output slot
            if(Config.SHOW_CHANCE_IN_JEI.get()) {
                tooltip.add(tooltip.size() - 1, new TranslationTextComponent("bonsaitrees.jei.category.growing.chance", (int) (slotChances[slot - 2] * 100)).mergeStyle(TextFormatting.YELLOW));
            }
        }
    }

    @Override
    public void setIngredients(IIngredients iIngredients) {
        //sapling.ingredient
        List<List<ItemStack>> inputs = new ArrayList<>();
        inputs.add(Collections.singletonList(sapling.ingredient.getMatchingStacks()[0]));

        tickModifiers = new HashMap<>();
        List<ItemStack> soilStacks = new ArrayList<>();
        for(SoilInfo soil : SoilCompatibility.INSTANCE.getValidSoilsForSapling(sapling)) {
            ItemStack representation = soil.ingredient.getMatchingStacks()[0];
            tickModifiers.put(representation.getItem().getRegistryName(), soil.getTickModifier());
            soilStacks.add(representation);
        }
        inputs.add(soilStacks);

        iIngredients.setInputLists(VanillaTypes.ITEM, inputs);

        List<ItemStack> drops = new ArrayList<>();

        slotChances = new float[sapling.drops.size()];
        int slot = 0;
        for(SaplingDrop drop : sapling.drops) {
            ItemStack dropStack = drop.resultStack.copy();
            dropStack.setCount(drop.rolls);
            drops.add(dropStack);
            slotChances[slot] = drop.chance;
            slot++;
        }

        iIngredients.setOutputs(VanillaTypes.ITEM, drops);
    }
}
