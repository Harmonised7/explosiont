package harmonised.explosiont.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.HashMap;
import java.util.Map;

public class Config
{
    public static ConfigImplementation config;

    public static void init()
    {
        config = ConfigHelper.register( ModConfig.Type.COMMON, ConfigImplementation::new );
    }

    public static class ConfigImplementation
    {
        public ConfigHelper.ConfigValueListener<Integer> healDelayExplosion;
//        public ConfigHelper.ConfigValueListener<Integer> healDelayFire;
        public ConfigHelper.ConfigValueListener<Double> ticksPerHeal;

        public ConfigImplementation(ForgeConfigSpec.Builder builder, ConfigHelper.Subscriber subscriber)
        {
            builder.push( "Explosiont" );
            {
                this.healDelayExplosion = subscriber.subscribe(builder
                        .comment( "How many ticks should pass until the healing starts after the explosion? (TICKS)" )
                        .translation( "pmmo.healDelayExplosion" )
                        .defineInRange( "healDelayExplosion", 600, 0, 100000 ) );

//                this.healDelayFire = subscriber.subscribe(builder
//                        .comment( "How many ticks should pass until the healing starts after the fire? (TICKS)" )
//                        .translation( "pmmo.healDelayFire" )
//                        .defineInRange( "healDelayFire", 60, 0, 100000 ) );

                this.ticksPerHeal = subscriber.subscribe(builder
                        .comment( "How many ticks should it take between each heal? (TICKS)" )
                        .translation( "pmmo.ticksPerHeal" )
                        .defineInRange( "ticksPerHeal", 10D, 0, 100 ) );

                builder.pop();
            }
        }
    }
}
