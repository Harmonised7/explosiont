package harmonised.explosiont.events;

import harmonised.explosiont.util.BlockInfo;
import harmonised.explosiont.util.ExplosionInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.event.world.ExplosionEvent;

import java.util.*;

public class ExplosionHandler
{
    public static void handleExplosion( ExplosionEvent.Detonate event )
    {
        List<BlockInfo> blocks = new ArrayList<>();
        World world = event.getWorld();
        ResourceLocation dimResLoc = world.dimension.getType().getRegistryName();

        if( !ChunkDataHandler.toHealDimMap.containsKey( dimResLoc ) )
            ChunkDataHandler.toHealDimMap.put( dimResLoc, new ArrayList<>() );

        List<BlockInfo> blocksToHeal = ChunkDataHandler.toHealDimMap.get( dimResLoc );
        Map<String, Integer> test = new HashMap<>();

        event.getExplosion().getAffectedBlockPositions().forEach( blockPos ->
        {
            BlockState blockState = world.getBlockState( blockPos );
            Block block = world.getBlockState( blockPos ).getBlock();

            if( !block.equals( Blocks.AIR ) && !block.equals( Blocks.CAVE_AIR ) && !block.equals( Blocks.FIRE ) && ( world.getBlockState( blockPos ).canDropFromExplosion( world, blockPos, event.getExplosion() ) ) )
            {
                TileEntity tileEntity = world.getTileEntity( blockPos );
                CompoundNBT tileEntityNBT = null;
                if( tileEntity != null )
                    tileEntityNBT = tileEntity.serializeNBT();

                BlockInfo blockInfo = new BlockInfo( dimResLoc, blockState, blockPos, System.currentTimeMillis(), tileEntityNBT );
                blocks.add( blockInfo );
                world.removeTileEntity( blockPos );
                world.removeBlock( blockPos, false );

                String testString = block.getRegistryName().toString();
                if( !test.containsKey( testString ) )
                    test.put( testString, 1 );
                else
                    test.replace( testString, test.get( testString ) + 1 );
            }
        });

        blocksToHeal.removeAll( blocks );
        blocksToHeal.addAll( blocks );
        blocksToHeal.sort( Comparator.comparingInt( info -> info.pos.getY() ) );

//        WorldTickHandler.explosions.add( WorldTickHandler.explosions.size(), new ExplosionInfo( blocks, 0 ) );
//        System.out.println( event.getExplosion() );

//        event.getExplosion().getAffectedBlockPositions().clear();
    }
}
