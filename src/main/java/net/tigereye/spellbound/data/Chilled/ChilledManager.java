package net.tigereye.spellbound.data.Chilled;

import com.google.gson.Gson;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.tigereye.spellbound.Spellbound;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class ChilledManager implements SimpleSynchronousResourceReloadListener {

    private static final String RESOURCE_LOCATION = "chilled";
    private final ChilledSerializer SERIALIZER = new ChilledSerializer();
    private static final Map<Identifier, Identifier> recipeMap = new HashMap<>();

    @Override
    public Identifier getFabricId() {
        return new Identifier(Spellbound.MODID, RESOURCE_LOCATION);
    }

    @Override
    public void reload(ResourceManager manager) {
        recipeMap.clear();
        Spellbound.LOGGER.info("Loading Spellbound Chilled Recipes.");
        manager.findResources(RESOURCE_LOCATION, path -> path.getPath().endsWith(".json")).forEach((id,resource) -> {
            try(InputStream stream = resource.getInputStream()) {
                Reader reader = new InputStreamReader(stream);
                ChilledData chilledData = SERIALIZER.read(id,new Gson().fromJson(reader, ChilledJsonFormat.class));
                if(recipeMap.containsKey(chilledData.block)){
                    Spellbound.LOGGER.warn("Chilled recipe "+chilledData.block.toString()+" -> "+recipeMap.get(chilledData.block).toString()+" overwritten with -> "+chilledData.result.toString()+".");
                }
                recipeMap.put(chilledData.block,chilledData.result);
            } catch(Exception e) {
                Spellbound.LOGGER.error("Error occurred while loading resource json " + id.toString(), e);
            }
        });
        Spellbound.LOGGER.info("Loaded "+ recipeMap.size()+" Chilled Recipes.");
    }

    public static Map<Identifier, Identifier> getRecipeMap(){
        return recipeMap;
    }

    @Nullable
    public static Identifier getResult(BlockState state){
        return recipeMap.get(Registries.BLOCK.getId(state.getBlock()));
    }
}
