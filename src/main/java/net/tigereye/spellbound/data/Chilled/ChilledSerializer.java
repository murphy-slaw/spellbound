package net.tigereye.spellbound.data.Chilled;

import com.google.gson.JsonSyntaxException;
import net.minecraft.util.Identifier;

public class ChilledSerializer {
    public ChilledData read(Identifier id, ChilledJsonFormat chilledJson) {

        if (chilledJson.block == null) {
            throw new JsonSyntaxException("Chilled entry" + id + " must provide treasure");
        }
        if (chilledJson.result == null) {
            throw new JsonSyntaxException("Chilled entry" + id + " cannot have 0 frequency");
        }

        ChilledData chilledData = new ChilledData();
        chilledData.block = new Identifier(chilledJson.block);
        chilledData.result = new Identifier(chilledJson.result);
        return chilledData;
    }
}
