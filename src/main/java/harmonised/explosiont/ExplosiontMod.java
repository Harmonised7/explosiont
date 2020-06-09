package harmonised.explosiont;

import harmonised.explosiont.util.Reference;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod( Reference.MOD_ID )
public class ExplosiontMod
{
    public ExplosiontMod()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener( this::modsLoading );
    }

    private void modsLoading( FMLCommonSetupEvent event )
    {
        MinecraftForge.EVENT_BUS.register( harmonised.explosiont.events.EventHandler.class );
    }
}
