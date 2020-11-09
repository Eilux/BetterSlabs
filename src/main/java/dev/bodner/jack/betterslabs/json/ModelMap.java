package dev.bodner.jack.betterslabs.json;

import java.util.Map;

public class ModelMap {
    Map<String,String> textures;
    public String getTexture(String id){
        return textures.get(id);
    }
}
