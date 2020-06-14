package harmonised.explosiont.mixin;

import net.minecraft.block.FireBlock;
import net.minecraft.client.gui.screen.MainMenuScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin( FireBlock.class )
public class FireMixin
{
    @Inject( at = @At("HEAD"), method = "tryCatchFire", remap = false )
    private void init( CallbackInfo info )
    {
        TryCatchFireHandler.handle( info );
    }
}