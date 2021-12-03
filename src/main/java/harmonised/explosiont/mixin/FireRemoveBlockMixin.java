package harmonised.explosiont.mixin;

import harmonised.explosiont.util.TryCatchFireHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

//COUT
//@Mixin(FireBlock.class)
//public class FireRemoveBlockMixin
//{
//    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/level/Level;removeBlock(Lnet/minecraft/util/math/BlockPos;Z)Z"), method = "tryCatchFire", remap = false)
//    private void init(Level worldIn, BlockPos pos, int chance, Random random, int age, Direction face, CallbackInfo info)
//    {
//        TryCatchFireHandler.handle(worldIn, pos, info);
//    }
//}