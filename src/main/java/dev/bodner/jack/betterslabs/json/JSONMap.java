package dev.bodner.jack.betterslabs.json;

import java.util.Map;

public class JSONMap {
    Map<String,NewType> variants;
    Map<String,String> textures;


    public String getModel(String id){
        return variants.get(id).model;
    }

    public String getTexture(String id){
        return textures.get(id);
    }
}

class NewType{
    String model;
}
