package harmonised.explosiont.mixin;

import harmonised.explosiont.util.TryCatchFireHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin( FireBlock.class )
public class FireBlockMixin
{

    @Inject( at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/Level;setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z" ), method = "tryCatchFire", remap = false )
    private void fireSetBlock$Explosiont(Level levelIn, BlockPos pos, int p_53434_, RandomSource p_53435_, int p_53436_, Direction face, CallbackInfo ci )
    {
        TryCatchFireHandler.handle( levelIn, pos, ci );
    }

    @Inject( at = @At( value = "INVOKE", target = "Lnet/minecraft/world/level/Level;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z" ), method = "tryCatchFire", remap = false )
    private void fireRemoveBlock$Explosiont(Level levelIn, BlockPos pos, int p_53434_, RandomSource p_53435_, int p_53436_, Direction face, CallbackInfo ci )
    {
        TryCatchFireHandler.handle( levelIn, pos, ci );
    }
}