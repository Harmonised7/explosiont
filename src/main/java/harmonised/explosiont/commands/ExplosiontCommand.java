package harmonised.explosiont.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import harmonised.explosiont.events.WorldTickHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.TranslationTextComponent;

public class ExplosiontCommand
{
    public static void register( CommandDispatcher<CommandSource> dispatcher )
    {
        dispatcher.register( Commands.literal( "forceHealAll" )
                  .executes( ExplosiontCommand::forceHealAll ));
    }

    private static int forceHealAll( CommandContext<CommandSource> context )
    {
        try
        {
            context.getSource().asPlayer().sendStatusMessage( new TranslationTextComponent( "All loaded chunks have been healed" ), false );
        }
        catch( CommandSyntaxException e )
        {
            //not player, it's fine
        }

        WorldTickHandler.forceAllHeal();
        return 1;
    }
}