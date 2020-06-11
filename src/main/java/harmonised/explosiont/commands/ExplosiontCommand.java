package harmonised.explosiont.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import harmonised.explosiont.events.ChunkDataHandler;
import harmonised.explosiont.events.WorldTickHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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