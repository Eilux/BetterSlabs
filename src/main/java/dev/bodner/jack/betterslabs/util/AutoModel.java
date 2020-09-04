package dev.bodner.jack.betterslabs.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class AutoModel {

    /**
     *             Identifier identifier3 = new Identifier(id.getNamespace(), "blockstates/" + id.getPath() + ".json");
     *             use filepath to find aplicable json files from list and then edit them
     *             use similar path to add models
     */

    public static List<Identifier> slabList = new ArrayList<>();
    
    public static void editBlockModel(ResourceManager resourceManager){
        for(int i = 0; i<=slabList.size()-1; i++){

        }
    }
}
