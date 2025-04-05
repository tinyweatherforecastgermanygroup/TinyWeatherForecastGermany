package de.kaffeemitkoffein.tinyweatherforecastgermany;
import java.util.Arrays;

public class
DataPackage {

    public int id;
    public long timestamp;
    public int type;
    public byte[] valueBlob;
    public int valueInt;
    public long valueLong;
    public float valueFloat;
    public String valueString;

    public final static int FIELD_TYPE_BOOLEAN = 5;
    public final static int FIELD_TYPE_LONG    = 6;

    public DataPackage(int id, long timestamp, int type, byte[] valueBlob, int valueInt,  long valueLong, float valueFloat, String valueString){
        this.id = id;
        this.timestamp = timestamp;
        this.type = type;
        this.valueBlob = valueBlob;
        this.valueInt = valueInt;
        this.valueLong = valueLong;
        this.valueFloat = valueFloat;
        this.valueString = valueString;
    }

    public int getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public static String toString(DataPackage dataPackage){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("id: "+dataPackage.id+"|");
        stringBuilder.append("time: "+dataPackage.timestamp+"|");
        stringBuilder.append("type: "+dataPackage.type+"|");
        stringBuilder.append("V-B: "+ Arrays.toString(dataPackage.valueBlob) +"|");
        stringBuilder.append("V-I: "+dataPackage.valueInt+"|");
        stringBuilder.append("V-L: "+dataPackage.valueLong+"|");
        stringBuilder.append("V-F: "+dataPackage.valueFloat+"|");
        stringBuilder.append("V-S: "+dataPackage.valueString);
        return stringBuilder.toString();
    }

    public String toString(){
        return toString(this);
    }
}
