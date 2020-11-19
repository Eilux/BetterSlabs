package dev.bodner.jack.betterslabs.json;

import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;

class Variants {
    @SerializedName("type=top")
    Object top;
    @SerializedName("type=bottom")
    Object bottom;
    @SerializedName("type=double")
    Object doubleSlab;
}
public class BlockstateMap {
    @SerializedName("variants")
    Variants variants;

    private boolean isTopList(){
        return variants.top instanceof List;
    }
    private boolean isBottomList(){
        return variants.bottom instanceof List;
    }
    private boolean isDoubleList(){
        return variants.doubleSlab instanceof List;
    }

    public List<String> getTop(){
        List<String> str = new ArrayList<>();
        if(isTopList()){
            for(int i = 0; i<=((List<LinkedTreeMap>)variants.top).size()-1; i++){
                str.add(((List<LinkedTreeMap>)variants.top).get(i).get("model").toString());
            }
        }
        else {
            str.add(((LinkedTreeMap)variants.top).get("model").toString());
        }
        return str;
    }

    public List<String> getBottom(){
        List<String> str = new ArrayList<>();
        if(isBottomList()){
            for(int i = 0; i<=((List<LinkedTreeMap>)variants.bottom).size()-1; i++){
                str.add(((List<LinkedTreeMap>)variants.bottom).get(i).get("model").toString());
            }
        }
        else {
            str.add(((LinkedTreeMap)variants.bottom).get("model").toString());
        }
        return str;
    }

    public List<String> getDouble(){
        List<String> str = new ArrayList<>();
        if(isDoubleList()){
            for(int i = 0; i<=((List<LinkedTreeMap>)variants.doubleSlab).size()-1; i++){
                str.add(((List<LinkedTreeMap>)variants.doubleSlab).get(i).get("model").toString());
            }
        }
        else {
            str.add(((LinkedTreeMap)variants.doubleSlab).get("model").toString());
        }
        return str;
    }
}

