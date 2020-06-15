package harmonised.explosiont.mixin;

import harmonised.explosiont.util.TryCatchFireHandler;
import net.minecraft.block.FireBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin( FireBlock.class )
public class FireRemoveBlockMixin
{
    @Inject( at = @At( value = "INVOKE", target = "Lnet/minecraft/world/World;removeBlock(Lnet/minecraft/util/math/BlockPos;Z)Z" ), method = "tryCatchFire", remap = false )
    private void init(World worldIn, BlockPos pos, int chance, Random random, int age, Direction face, CallbackInfo info )
    {
        TryCatchFireHandler.handle( worldIn, pos, info );
    }
}