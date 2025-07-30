package dev.perxenic.vectorientation.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.perxenic.vectorientation.Config;
import dev.perxenic.vectorientation.Vectorientation;
import net.minecraft.client.renderer.MultiBufferSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.At;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.world.entity.item.FallingBlockEntity;

@Mixin(value = FallingBlockRenderer.class, priority = 1100)
public class FallingBlockRendererMixin {
    @Inject(
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(DDD)V"
            ),
            method = "render(Lnet/minecraft/world/entity/item/FallingBlockEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
    )
    public void addRotation(FallingBlockEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        if (Config.blacklist.contains(entity.getBlockState().getBlock())) return;
        Vectorientation.addRotation(entity, poseStack);
    }
}