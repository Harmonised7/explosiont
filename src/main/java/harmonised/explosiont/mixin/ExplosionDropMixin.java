package harmonised.explosiont.mixin;

import net.minecraft.data.loot.BlockLootTables;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin( BlockLootTables.class )
public class ExplosionDropMixin
{
//    @Inject(at = @At("HEAD"), method = "withSurvivesExplosion")
    @Inject(at = @At("HEAD"), method = "chookity")
    private void init(CallbackInfo info)
    {
        System.out.println("This line is printed by an example mod mixin!");
    }
}