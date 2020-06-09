package harmonised.explosiont.events;

import harmonised.explosiont.util.BlockInfo;
import harmonised.explosiont.util.ExplosionInfo;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.TickEvent;

import java.util.*;

public class WorldTickHandler
{
    public static List<ExplosionInfo> explosions = new ArrayList<>();
    public static long lastHeal = System.currentTimeMillis();

    public static void handleWorldTick( TickEvent.WorldTickEvent event )
    {
        if( System.currentTimeMillis() - lastHeal > 100 )
        {
            List<ExplosionInfo> newExplosions = new ArrayList<>();
            lastHeal = System.currentTimeMillis();

            for( ExplosionInfo explosion : explosions )
            {
                if( explosion.blocks.size() > 0 )
                {
                    if( explosion.age > 3 )
                    {
                        BlockInfo blockInfo = explosion.blocks.get( 0 );
                        Block block = event.world.getBlockState( blockInfo.pos ).getBlock();
                        if( block.equals( Blocks.AIR ) || block.equals( Blocks.WATER ) )
                            event.world.setBlockState(blockInfo.pos, blockInfo.state);
                        else
                            Block.spawnAsEntity( event.world, blockInfo.pos, new ItemStack( blockInfo.state.getBlock().asItem() ) );

                        explosion.blocks.remove(0);
                }
                    else
                        explosion.age++;
                }

                if( explosion.blocks.size() > 0 )
                    newExplosions.add( explosion );
            }

            explosions = newExplosions;
        }
    }
}
