package harmonised.explosiont.config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import harmonised.explosiont.ExplosiontMod;
import harmonised.explosiont.util.LogHandler;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JsonConfig
{
    public static final Type mapType = new TypeToken<Map<String, Set<String>>>(){}.getType();
    private static final String hardDataPath = "/assets/explosiont/util/filter.json";
    private static final String dataPath = "explosiont/filter.json";
    public static Map<String, Set<String>> filter;
    public static Gson gson = new Gson();

    public static void init()
    {
        try
        {
            File filterFile = FMLPaths.CONFIGDIR.get().resolve( dataPath ).toFile();
            createData( filterFile );
            filter = readFromFile( filterFile.getPath() );
        }
        catch( Exception e )
        {
            System.out.println( e );
        }
    }

    public static Map<String, Set<String>> readFromFile( String path )
    {
        try (
                InputStream input = new FileInputStream( path );
                Reader reader = new BufferedReader(new InputStreamReader(input));
        )
        {
            return gson.fromJson( reader, mapType );
        }
        catch (IOException e)
        {
//            LogHandler.LOGGER.error("Could not parse json from {}", path, e);

            return new HashMap<>();
        }
    }

    private static void createData( File dataFile )
    {
        try     //create template data file
        {
            dataFile.getParentFile().mkdir();
            dataFile.createNewFile();
        }
        catch( IOException e )
        {
            LogHandler.LOGGER.error( "Could not create template json config!", dataFile.getPath(), e );
        }

        try( InputStream inputStream = ExplosiontMod.class.getResourceAsStream( hardDataPath );
            FileOutputStream outputStream = new FileOutputStream( dataFile ); )
        {
            IOUtils.copy( inputStream, outputStream );
        }
        catch( IOException e )
        {
            LogHandler.LOGGER.error( "Error copying over default json config to " + dataFile.getPath(), dataFile.getPath(), e );
        }
    }
}
