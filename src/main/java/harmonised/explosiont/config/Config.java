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
        public ConfigHelper.ConfigValueListener<Integer> healDelay;
        public ConfigHelper.ConfigValueListener<Integer> eachHealDelay;

        public ConfigImplementation(ForgeConfigSpec.Builder builder, ConfigHelper.Subscriber subscriber)
        {
            builder.push( "Explosiont" );
            {
                this.healDelay = subscriber.subscribe(builder
                        .comment( "How long should explosions wait before starting to heal? (SECONDS)" )
                        .translation( "pmmo.healDelay" )
                        .defineInRange( "healDelay", 30, 0, 86400 ) );

                this.eachHealDelay = subscriber.subscribe(builder
                        .comment( "How many ticks should it take between each heal? (TICKS)" )
                        .translation( "pmmo.eachHealDelay" )
                        .defineInRange( "eachHealDelay", 50, 0, 100000 ) );

                builder.pop();
            }
        }
    }
}
