package harmonised.explosiont.events;

import harmonised.explosiont.util.BlockInfo;
import harmonised.explosiont.util.ExplosionInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.event.world.ExplosionEvent;

import java.util.*;

public class ExplosionHandler
{
    public static void handleExplosion( ExplosionEvent.Detonate event )
    {
        List<BlockInfo> blocks = new ArrayList<>();
        World world = event.getWorld();

        event.getExplosion().getAffectedBlockPositions().forEach( blockPos ->
        {
            BlockState blockState = world.getBlockState( blockPos );
            Block block = world.getBlockState( blockPos ).getBlock();

            if( !block.equals( Blocks.AIR ) && world.getBlockState( blockPos ).canDropFromExplosion( world, blockPos, event.getExplosion() ) )
            {
                BlockInfo blockInfo = new BlockInfo( blockState, blockPos );
                blocks.add( blockInfo );
                world.destroyBlock( blockInfo.pos, false );
            }
        });

        blocks.sort( Comparator.comparingInt( info -> info.pos.getY() ) );

        WorldTickHandler.explosions.add( WorldTickHandler.explosions.size(), new ExplosionInfo( blocks, 0 ) );
//        System.out.println( event.getExplosion() );

        System.out.println( "boom!" );
    }
}
