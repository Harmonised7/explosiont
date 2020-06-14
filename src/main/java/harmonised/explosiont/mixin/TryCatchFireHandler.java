package harmonised.explosiont.mixin;

import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class TryCatchFireHandler
{
    public static void handle( CallbackInfo info )
    {
        System.out.println( info );
    }
}
