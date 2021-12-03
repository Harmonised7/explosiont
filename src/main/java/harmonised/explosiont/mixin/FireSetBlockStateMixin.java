package harmonised.explosiont.mixin;

import harmonised.explosiont.util.TryCatchFireHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FireBlock;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

//COUT
//@Mixin(FireBlock.class)
//class FiresetBlockAndUpdateMixin
//{
//    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/level/Level;setBlockAndUpdate(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"), method = "tryCatchFire", remap = false)
//    private void init(Level worldIn, BlockPos pos, int chance, Random random, int age, Direction face, CallbackInfo info)
//    {
//        TryCatchFireHandler.handle(worldIn, pos, info);
//    }
//}