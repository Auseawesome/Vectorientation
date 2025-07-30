package dev.perxenic.vectorientation.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.perxenic.vectorientation.Config;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.TntRenderer;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.phys.Vec3;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TntRenderer.class, priority = 1100)
public class TntRendererMixin {
    @Inject(
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/TntMinecartRenderer;renderWhiteSolidBlock(Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;Lnet/minecraft/world/level/block/state/BlockState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IZ)V"
            ),
            method = "render(Lnet/minecraft/world/entity/item/PrimedTnt;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"
    )
    public void addRotation(PrimedTnt entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight, CallbackInfo ci) {
        if (!entity.onGround()) {
            Vec3 deltaMovement = entity.getDeltaMovement();
            Vector3d velocity = new Vector3d(deltaMovement.x, deltaMovement.y, deltaMovement.z);
            velocity.y -= entity.getGravity() * entity.getGravity();
            velocity.y *= .98D;

            boolean blacklisted = Config.blacklist.contains(entity.getBlockState().getBlock());
            float speed = (!blacklisted && Config.squetch) ?
                    (float) (Config.minWarp + Config.warpFactor * velocity.length())
                    : 1.0f;
            float angle = (float) Math.acos(velocity.normalize().y);
            Vector3f axis = new Vector3f((float) (-1 * velocity.z()), 0, (float) velocity.x());
            Quaternionf rot = new Quaternionf();
            if (axis.length() > .01f) {
                axis.normalize();
                rot = new Quaternionf(new AxisAngle4f(-angle, axis));
            }
            poseStack.translate(0.5D, 0.5D, 0.5D);
            poseStack.mulPose(rot);
            poseStack.scale(1 / speed, speed, 1 / speed);
            poseStack.translate(-0.5D, -0.5D, -0.5D);
        }
    }
}