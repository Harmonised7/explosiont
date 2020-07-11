package harmonised.explosiont.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

public class Config
{
    public static ConfigImplementation config;

    public static void init()
    {
        config = ConfigHelper.register( ModConfig.Type.COMMON, ConfigImplementation::new );
    }

    public static class ConfigImplementation
    {
        //Global
        public ConfigHelper.ConfigValueListener<Boolean> onlyHealPastMorning;

        //Explosiont
        public ConfigHelper.ConfigValueListener<Boolean> ExplosionHealingEnabled;
        public ConfigHelper.ConfigValueListener<Boolean> OnlyHealCreepers;
        public ConfigHelper.ConfigValueListener<Integer> healDelayExplosion;
        public ConfigHelper.ConfigValueListener<Double> ticksPerHealExplosion;
        public ConfigHelper.ConfigValueListener<Integer> speedUpTresholdExplosion;

        //Firent
        public ConfigHelper.ConfigValueListener<Boolean> FireHealingEnabled;
        public ConfigHelper.ConfigValueListener<Integer> healDelayFire;
        public ConfigHelper.ConfigValueListener<Double> ticksPerHealFire;
        public ConfigHelper.ConfigValueListener<Integer> speedUpTresholdFire;

        public ConfigImplementation(ForgeConfigSpec.Builder builder, ConfigHelper.Subscriber subscriber)
        {
            builder.push( "Global" );
            {
                this.onlyHealPastMorning = subscriber.subscribe(builder
                        .comment( "Should explosions only start to heal when the night passes? (This would ignore all delays)" )
                        .translation( "pmmo.onlyHealPastMorning" )
                        .define( "onlyHealPastMorning", false ) );

                builder.pop();
            }

            builder.push( "Explosiont" );
            {
                this.ExplosionHealingEnabled = subscriber.subscribe(builder
                        .comment( "Should explosions be healed?" )
                        .translation( "pmmo.ExplosionHealingEnabled" )
                        .define( "ExplosionHealingEnabled", true ) );

                this.OnlyHealCreepers = subscriber.subscribe(builder
                        .comment( "Should only creeper explosions be healed? (Excluding any other type of explosive)" )
                        .translation( "pmmo.OnlyHealCreepers" )
                        .define( "OnlyHealCreepers", false ) );

                this.healDelayExplosion = subscriber.subscribe(builder
                        .comment( "How many ticks should pass until the healing starts after the explosion? (TICKS, 20 = 1 second on a non-laggy server)" )
                        .translation( "pmmo.healDelayExplosion" )
                        .defineInRange( "healDelayExplosion", 600, 0, 100000 ) );

                this.ticksPerHealExplosion = subscriber.subscribe(builder
                        .comment( "How many ticks should it take between each heal for exploded blocks? (TICKS)" )
                        .translation( "pmmo.ticksPerHealExplosion" )
                        .defineInRange( "ticksPerHealExplosion", 10D, 0, 100 ) );

                this.speedUpTresholdExplosion = subscriber.subscribe(builder
                        .comment( "Past what number of blocks should rebuild speed start scaling for exploded blocks? (If this is set to 1000, past 1000, the speed will double every 1000 blocks, so at 2000 = 200% speed, 5000 = 500% speed, 0 = no scaling)" )
                        .translation( "pmmo.speedUpTresholdExplosion" )
                        .defineInRange( "speedUpTresholdExplosion", 1000, 0, 100000 ) );

                builder.pop();
            }

            builder.push( "Firent" );
            {
                this.FireHealingEnabled = subscriber.subscribe(builder
                        .comment( "Should fires be healed?" )
                        .translation( "pmmo.FireHealingEnabled" )
                        .define( "FireHealingEnabled", true ) );

                this.healDelayFire = subscriber.subscribe(builder
                        .comment( "How many ticks should pass until the healing starts after the fire? (TICKS, 20 = 1 second on a non-laggy server)" )
                        .translation( "pmmo.healDelayFire" )
                        .defineInRange( "healDelayFire", 18000, 0, 100000 ) );

                this.ticksPerHealFire = subscriber.subscribe(builder
                        .comment( "How many ticks should it take between each heal for burnt blocks? (TICKS)" )
                        .translation( "pmmo.ticksPerHealFire" )
                        .defineInRange( "ticksPerHealFire", 1D, 0, 100 ) );

                this.speedUpTresholdFire = subscriber.subscribe(builder
                        .comment( "Past what number of blocks should rebuild speed start scaling for burnt blocks? (If this is set to 1000, past 1000, the speed will double every 1000 blocks, so at 2000 = 200% speed, 5000 = 500% speed, 0 = no scaling)" )
                        .translation( "pmmo.speedUpTresholdFire" )
                        .defineInRange( "speedUpTresholdFire", 2500, 0, 100000 ) );

                builder.pop();
            }
        }
    }
}
