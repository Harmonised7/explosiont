package harmonised.explosiont.events;

import harmonised.explosiont.util.BlockInfo;
import harmonised.explosiont.util.ExplosionInfo;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.audio.Sound;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

public class WorldTickHandler
{
    private static final double healDelay = 5000;
    private static final double eachHealDelay = 50;
    public static List<BlockInfo> blocksToHeal = new ArrayList<>();
    public static long lastHeal = System.currentTimeMillis();

    public static void handleWorldTick( TickEvent.WorldTickEvent event )
    {
        if( System.currentTimeMillis() - lastHeal > eachHealDelay )
        {
            if( blocksToHeal.size() > 0 )
            {
                World world = event.world;
                BlockInfo blockInfo = blocksToHeal.get( 0 );

                if( System.currentTimeMillis() - healDelay > blockInfo.time )
                {
                    Block block = world.getBlockState( blockInfo.pos ).getBlock();
                    if( block.equals( Blocks.AIR ) || block.equals( Blocks.WATER ) )
                    {
                        world.setBlockState(blockInfo.pos, blockInfo.state);
                        if( blockInfo.tileEntityNBT != null )
                            world.setTileEntity( blockInfo.pos, TileEntity.create( blockInfo.tileEntityNBT ) );

//                        Random rand = new Random();
//                        world.playSound( blockInfo.pos.getX(), blockInfo.pos.getY(), blockInfo.pos.getZ(), SoundEvents.ENTITY_DRAGON_FIREBALL_EXPLODE, SoundCategory.BLOCKS, 0.2F + rand.nextFloat() * 0.2F, 0.9F + rand.nextFloat() * 0.15F, true );
                    }
                    else
                    {
                        Block.spawnAsEntity( world, blockInfo.pos, new ItemStack( blockInfo.state.getBlock().asItem() ) );
                        if( blockInfo.tileEntityNBT != null && blockInfo.tileEntityNBT.contains( "Items" ) )
                        {
                            ListNBT items = (ListNBT) blockInfo.tileEntityNBT.get( "Items" );
                            for( int i = 0; i < items.size(); i++ )
                            {
                                Block.spawnAsEntity( world, blockInfo.pos, ItemStack.read( items.getCompound( i ) ) );
                            }
                        }
                    }

                    blocksToHeal.remove( 0 );
                }
            }

            lastHeal = System.currentTimeMillis();
        }
    }
}
