package harmonised.explosiont;

import harmonised.explosiont.commands.ExplosiontCommand;
import harmonised.explosiont.config.*;
import harmonised.explosiont.events.ChunkDataHandler;
import harmonised.explosiont.util.BlackList;
import harmonised.explosiont.util.Reference;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Reference.MOD_ID)
public class ExplosiontMod
{
    public ExplosiontMod()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::modsLoading);
        MinecraftForge.EVENT_BUS.addListener(this::serverAboutToStart);
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
        Config.init();
    }

    private void modsLoading(FMLCommonSetupEvent event)
    {
        MinecraftForge.EVENT_BUS.register(harmonised.explosiont.events.EventHandler.class);
    }

    private void serverAboutToStart(ServerAboutToStartEvent event)
    {
        BlackList.init();
        JsonConfig.init();
        ChunkDataHandler.init();
    }

    private void registerCommands(RegisterCommandsEvent event)
    {
        ExplosiontCommand.register(event.getDispatcher());
    }
}
