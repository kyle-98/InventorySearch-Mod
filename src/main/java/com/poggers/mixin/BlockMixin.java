package com.poggers.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.poggers.config.ModConfig;
import com.poggers.utils.XrayUtils;

@Mixin(value = Block.class)
public class BlockMixin {
    @Inject(at = @At("HEAD"), method = "shouldDrawSide(" + "Lnet/minecraft/block/BlockState;" + 
            "Lnet/minecraft/world/BlockView;" + 
            "Lnet/minecraft/util/math/BlockPos;" +
            "Lnet/minecraft/util/math/Direction;" + 
            "Lnet/minecraft/util/math/BlockPos;" + 
            ")Z",
            cancellable = true)
    private static void shouldDrawSide(BlockState state, BlockView reader, BlockPos pos, Direction face, BlockPos blockPos, CallbackInfoReturnable<Boolean> ci) {
        if(XrayUtils.getXrayState()){
            if (ModConfig.xraySettings.getXrayBlocks().contains(state.getBlock())) {
                ci.setReturnValue(true);
            } else {
                ci.setReturnValue(false);
            }
        }
    }

    private BlockMixin() {
    }
}
