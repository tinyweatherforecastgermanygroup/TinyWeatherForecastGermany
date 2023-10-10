package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;

public class PollenArea {


  // This is the known size of polygons from the GeoServer. This is used to check if the database integrity is given.
  public final static int POLYGON_DATABASE_COUNT = 120;
  int region_id;
  int partregion_id;
  String description;
  Polygon geoPolygon;
  public String polygonString;

  public PollenArea(){
  }

  public PollenArea(int region_id, int partregion_id, String description){
    this.region_id = region_id;
    this.partregion_id = partregion_id;
    this.description = description;
  }

  public Polygon initPolygon(){
    try {
      JSONArray jsonArray= new JSONArray(polygonString);
      geoPolygon = new Polygon(jsonArray,null);
      return geoPolygon;
    } catch (JSONException e) {
      return null;
    }
  }

  public static void WritePollenAreasToDatabase(Context context, ArrayList<PollenArea> pollenAreas){
    ContentResolver contentResolver = context.getContentResolver();
    try {
      int i = contentResolver.delete(WeatherContentManager.POLLENAREAS_URI_ALL,null,null);
    } catch (Exception e){
      PrivateLog.log(context,PrivateLog.DATA,PrivateLog.ERR,"Deleting pollen areas failed: "+e.getMessage());
    }
    for (int i=0; i<pollenAreas.size(); i++){
      ContentValues contentValues = WeatherContentManager.getContentValuesFromPollenArea(pollenAreas.get(i));
      contentResolver.insert(WeatherContentManager.POLLENAREAS_URI_ALL,contentValues);
    }
  }

  public static int GetPollenAreasDatabaseSize(Context context){
    ContentResolver contentResolver = context.getContentResolver();
    Cursor cursor = contentResolver.query(WeatherContentManager.POLLENAREAS_URI_ALL, new String[]{WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLENAREA_partregion_id},null,null,null);
    return cursor.getCount();
  }

  public static boolean IsPollenAreaDatabaseComplete(Context context){
    int count = GetPollenAreasDatabaseSize(context);
    if (count!=POLYGON_DATABASE_COUNT){
      return false;
    }
    return true;
  }

  public static ArrayList<PollenArea> GetPollenAreas(Context context, Integer partregion_id){
    ArrayList<PollenArea> pollenAreas = new ArrayList<PollenArea>();
    ContentResolver contentResolver = context.getContentResolver();
    String selection = null;
    String[] selectionArgs = null;
    if (partregion_id!=null){
      selection = WeatherContentProvider.WeatherDatabaseHelper.KEY_POLLENAREA_partregion_id + " = ?";
      selectionArgs = new String[]{String.valueOf(partregion_id)};
    };
    Cursor cursor = contentResolver.query(WeatherContentManager.POLLENAREAS_URI_ALL, null,selection,selectionArgs,null);
    if (cursor.moveToFirst()){
      int count = 0;
      do {
        PollenArea pollenArea = WeatherContentManager.getPollenAreaFromCursor(cursor);
        count++;
        if (pollenArea.polygonString!=null){
          pollenArea.initPolygon();
        } else {
          PrivateLog.log(context,PrivateLog.DATA,PrivateLog.ERR,"Pollen area "+context+" polygon has no data.");
        }
        pollenAreas.add(pollenArea);
      } while (cursor.moveToNext());
      cursor.close();
    }
    return pollenAreas;
  }

  public static PollenArea FindPollenArea(Context context, Weather.WeatherLocation weatherLocation){
    ArrayList<PollenArea> pollenAreas = GetPollenAreas(context,null);
    for (int i=0; i<pollenAreas.size(); i++){
      PollenArea pollenArea = pollenAreas.get(i);
      if (pollenArea.geoPolygon.isInPolygon(weatherLocation)){
        PollenArea resultArea = new PollenArea(pollenArea.region_id,pollenArea.partregion_id, pollenArea.description);
        return resultArea;
      }
    }
    return null;
  }

}
