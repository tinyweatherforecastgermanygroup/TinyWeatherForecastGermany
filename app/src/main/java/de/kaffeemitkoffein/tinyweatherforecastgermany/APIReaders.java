package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.ZipInputStream;

public class APIReaders {

   public static class WeatherWarningsRunnable implements Runnable {

       public Context context;
       public Weather.WeatherLocation weatherLocation;

       public WeatherWarningsRunnable(Context context) {
           this.context = context;
           weatherLocation = WeatherSettings.getSetStationLocation(context);
       }

       public long getTimeStampFromString(String source){
           if (source != null){
               SimpleDateFormat kml_dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
               kml_dateFormat.setLenient(true);
               try {
                   Date date = kml_dateFormat.parse(source);
                   return date.getTime();
               } catch (ParseException e){
                   PrivateLog.log(context,PrivateLog.DATA,PrivateLog.ERR,"Malformed timestamp in warning ("+source+"):"+e.getMessage());
                   return 0;
               }
           }
           return 0;
       }

       private String getValue(Element element, String tag){
           NodeList nl2 = element.getElementsByTagName(tag);
           if (nl2.getLength()>0){
               return nl2.item(0).getTextContent();
           }
           return null;
       }

       private WeatherWarning parseWarning(String zipfile) throws ParserConfigurationException, IOException, SAXException {
           WeatherWarning warning = new WeatherWarning();
           DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
           Document document = documentBuilder.parse(new InputSource(new StringReader(zipfile)));
           NodeList nl = document.getElementsByTagName("identifier");
           warning.polling_time = Calendar.getInstance().getTimeInMillis();
           for (int i=0;i<nl.getLength(); i++){
               // should be only one, but we take the latest
               Element elIdentifier = (Element) nl.item(i);
               warning.identifier =  elIdentifier.getFirstChild().getNodeValue();
           }

           nl = document.getElementsByTagName("sender");
           for (int i=0;i<nl.getLength(); i++){
               // should be only one, but we take the latest
               Element element = (Element) nl.item(i);
               warning.sender =  element.getFirstChild().getNodeValue();
           }

           nl = document.getElementsByTagName("sent");
           for (int i=0;i<nl.getLength(); i++){
               // should be only one, but we take the latest
               Element element = (Element) nl.item(i);
               String time =  element.getFirstChild().getNodeValue();
               warning.sent = getTimeStampFromString(time);
           }

           nl = document.getElementsByTagName("status");
           for (int i=0;i<nl.getLength(); i++){
               // should be only one, but we take the latest
               Element element = (Element) nl.item(i);
               warning.status =  element.getFirstChild().getNodeValue();
           }

           nl = document.getElementsByTagName("msgType");
           for (int i=0;i<nl.getLength(); i++){
               // should be only one, but we take the latest
               Element element = (Element) nl.item(i);
               warning.msgType =  element.getFirstChild().getNodeValue();
           }

           nl = document.getElementsByTagName("source");
           for (int i=0;i<nl.getLength(); i++){
               // should be only one, but we take the latest
               Element element = (Element) nl.item(i);
               warning.source =  element.getFirstChild().getNodeValue();
           }

           nl = document.getElementsByTagName("scope");
           for (int i=0;i<nl.getLength(); i++){
               // should be only one, but we take the latest
               Element element = (Element) nl.item(i);
               warning.scope =  element.getFirstChild().getNodeValue();
           }

           nl = document.getElementsByTagName("code");
           warning.codes = new ArrayList<String>();
           for (int i=0;i<nl.getLength(); i++){
               // should be only one, but we take the latest
               Element element = (Element) nl.item(i);
               warning.codes.add(element.getFirstChild().getNodeValue());
           }

           nl = document.getElementsByTagName("references");
           warning.references = new ArrayList<String>();
           for (int i=0;i<nl.getLength(); i++){
               // usually 1, but seems to be intended as multiple. We handle it in an arraylist to be safe.
               Element element = (Element) nl.item(i);
               String reference = element.getFirstChild().getNodeValue();
               warning.references.add(reference.substring(reference.indexOf(",")+1,reference.lastIndexOf(",")));
           }

           nl = document.getElementsByTagName("info");
           for (int n=0; n< nl.getLength(); n++){
               Node node = nl.item(n);
               if (node.getNodeType() == Node.ELEMENT_NODE){
                   Element element = (Element) node;
                   warning.language = getValue(element,"language");
                   warning.category = getValue(element,"category");
                   warning.event = getValue(element,"event");
                   warning.responseType = getValue(element,"responseType");
                   warning.urgency = getValue(element,"urgency");
                   warning.severity = getValue(element,"severity");
                   warning.certainty = getValue(element,"certainty");
                   warning.effective = getTimeStampFromString(getValue(element,"effective"));
                   warning.onset = getTimeStampFromString(getValue(element,"onset"));
                   warning.expires = getTimeStampFromString(getValue(element,"expires"));
                   warning.senderName = getValue(element,"senderName");
                   warning.headline = getValue(element,"headline");
                   warning.description = getValue(element,"description");
                   warning.instruction = getValue(element,"instruction");
                   warning.web = getValue(element,"web");
                   warning.contact = getValue(element,"contact");
               }
           }

           nl = document.getElementsByTagName("eventCode");
           warning.groups = new ArrayList<String>();
           for (int n=0; n< nl.getLength(); n++){
               Node node = nl.item(n);
               if (node.getNodeType() == Node.ELEMENT_NODE){
                   Element element = (Element) node;
                   String valuename = getValue(element,"valueName");
                   String value = getValue(element,"value");
                   switch (valuename){
                       case "PROFILE_VERSION": warning.profile_version = value; break;
                       case "LICENSE": warning.license = value; break;
                       case "II": warning.ii = value; break;
                       case "AREA_COLOR": warning.area_color=value; break;
                       case "GROUP": warning.groups.add(value); break;
                   }
               }
           }

           nl = document.getElementsByTagName("parameter");
           warning.parameter_names = new ArrayList<String>();
           warning.parameter_values = new ArrayList<String>();
           for (int n=0; n< nl.getLength(); n++){
               Node node = nl.item(n);
               if (node.getNodeType() == Node.ELEMENT_NODE){
                   Element element = (Element) node;
                   warning.parameter_names.add(getValue(element,"valueName"));
                   warning.parameter_values.add(getValue(element,"value"));
               }
           }

           nl = document.getElementsByTagName("area");
           warning.polygons = new ArrayList<String>();
           warning.excluded_polygons = new ArrayList<String>();
           warning.area_names = new ArrayList<String>();
           warning.area_warncellIDs = new ArrayList<String>();
           for (int n=0; n< nl.getLength(); n++){
               Node node = nl.item(n);
               if (node.getNodeType() == Node.ELEMENT_NODE){
                   Element element = (Element) node;
                   String polygon = getValue(element,"polygon");
                   if (polygon!=null){
                       warning.polygons.add(polygon);
                   } else {
                       warning.area_names.add(getValue(element,"areaDesc"));
                       NodeList subnodelist = element.getElementsByTagName("geocode");
                       for (int mm=0; mm<subnodelist.getLength(); mm++){
                           Node subnode = subnodelist.item(mm);
                           if (subnode.getNodeType() == Node.ELEMENT_NODE){
                               Element subelement = (Element) subnode;
                               String valueName = getValue(subelement,"valueName");
                               String value = getValue(subelement,"value");
                               if (valueName.equals("EXCLUDE_POLYGON")){
                                   if (value!=null){
                                       warning.excluded_polygons.add(value);
                                   }
                               }
                               if (valueName.equals("WARNCELLID")){
                                   if (value == null){
                                       value="-";
                                   }
                                   warning.area_warncellIDs.add(value);
                               }
                           }
                       }

                   }
               }
           }

           return warning;
       }

       @SuppressWarnings("deprecation")
       private String getUrlString(Context context){
           String country = "EN";
           if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
               country = context.getResources().getConfiguration().getLocales().get(0).getCountry();
           } else {
               country = context.getResources().getConfiguration().locale.getCountry();
           }
           // 3.2.1 Gemeindebasis mit DWD Aktualisierungsstrategie
           final String C_FIRST ="https://opendata.dwd.de/weather/alerts/cap/COMMUNEUNION_DWD_STAT/Z_CAP_C_EDZW_LATEST_PVW_STATUS_PREMIUMDWD_COMMUNEUNION_";
           final String C_LAST  = ".zip";
           switch (country){
               case "FR": return C_FIRST+"FR"+C_LAST;
               case "ES": return C_FIRST+"ES"+C_LAST;
               case "DE": return C_FIRST+"DE"+C_LAST;
           }
           return C_FIRST+"EN"+C_LAST;
           //return "https://kaffeemitkoffein.de/local/Z_CAP_C_EDZW_LATEST_PVW_STATUS_PREMIUMDWD_COMMUNEUNION_DE.zip";
       }

       @SuppressWarnings("deprecation")
       private String getLegacyUrlString(Context context){
           String country = "EN";
           if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
               country = context.getResources().getConfiguration().getLocales().get(0).getCountry();
           } else {
               country = context.getResources().getConfiguration().locale.getCountry();
           }
           final String C_FIRST ="http://opendata.dwd.de/weather/alerts/cap/COMMUNEUNION_DWD_STAT/Z_CAP_C_EDZW_LATEST_PVW_STATUS_PREMIUMDWD_COMMUNEUNION_";
           final String C_LAST  = ".zip";
           switch (country){
               case "FR": return C_FIRST+"FR"+C_LAST;
               case "ES": return C_FIRST+"ES"+C_LAST;
               case "DE": return C_FIRST+"DE"+C_LAST;
           }
           return C_FIRST+"EN"+C_LAST;
       }

       private InputStream getWeatherWarningInputStream() throws IOException {
           URL url;
           URL url_legacy;
           try {
               url = new URL(getUrlString(context));
               url_legacy = new URL(getLegacyUrlString(context));
           } catch (MalformedURLException e){
               throw e;
           }
           try {
               HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
               InputStream inputStream = new BufferedInputStream(httpsURLConnection.getInputStream());
               return inputStream;
           } catch (SSLException e){
               if (WeatherSettings.isTLSdisabled(context)){
                   // try fallback to http
                   try {
                       HttpURLConnection httpURLConnection = (HttpURLConnection) url_legacy.openConnection();
                       InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                       PrivateLog.log(context,PrivateLog.DATA,PrivateLog.WARN,"Warning: weather warnings are polled over http without encryption.");
                       return inputStream;
                   } catch (IOException e2){
                       PrivateLog.log(context,PrivateLog.DATA,PrivateLog.ERR,"Reading weather warnings via http failed: "+e2.getMessage());
                       throw e2;
                   }
               } else {
                   PrivateLog.log(context,PrivateLog.DATA,PrivateLog.ERR,"ssl connection could not be established, but http is not allowed.");
                   throw e;
               }
           }
       }

       public ArrayList<WeatherWarning> doInBackground() {
           ArrayList<WeatherWarning> warnings = new ArrayList<WeatherWarning>();
           try {
               // URL warningsUrl = new URL("https://opendata.dwd.de/weather/alerts/cap/COMMUNEUNION_DWD_STAT/Z_CAP_C_EDZW_LATEST_PVW_STATUS_PREMIUMDWD_COMMUNEUNION_DE.zip");
               // URL warningsUrl = new URL(getUrlString(context));
               // ZipInputStream zipInputStream = new ZipInputStream(warningsUrl.openStream());
               InputStream inputStream = new BufferedInputStream(getWeatherWarningInputStream());
               ZipInputStream zipInputStream = new ZipInputStream(inputStream);
               // iterate through the warnings; each warning is a file
               int warnings_counter = 0;
               while (zipInputStream.getNextEntry() != null){
                   warnings_counter ++;
                   StringBuffer stringBuffer = new StringBuffer();
                   byte[] buffer = new byte[4096];
                   int l=0;
                   while ((l=zipInputStream.read(buffer))>0){
                       stringBuffer.append(new String(buffer,0,l));
                   }
                   try {
                       WeatherWarning warning = parseWarning(stringBuffer.toString());
                       warnings.add(warning);
                   } catch (ParserConfigurationException e){
                       PrivateLog.log(context,PrivateLog.DATA,PrivateLog.ERR,"Could not configure parser, skipping a warning:"+e.getMessage());
                   } catch (IOException e){
                       PrivateLog.log(context,PrivateLog.DATA,PrivateLog.ERR,"I/O Error, skipping a warning:"+e.getMessage());
                   } catch (SAXException e){
                       PrivateLog.log(context,PrivateLog.DATA,PrivateLog.ERR,"Possibly malformed warning, skipping a warning:"+e.getMessage());
                   }
               }
           } catch (MalformedURLException e){
               PrivateLog.log(context,PrivateLog.DATA,PrivateLog.ERR,"Malformed URL for DWD resource:"+e.getMessage());
               return null;
           } catch (IOException e){
               PrivateLog.log(context,PrivateLog.DATA,PrivateLog.ERR,"Unable to open DWD stream:"+e.getMessage());
               return null;
           }
           return warnings;
       }

       //* Override to do something before getting data starts

       public void onStart(){
           // do something
       }

       public void onNegativeResult(){
           // do nothing at the moment.
       }

       public void onPositiveResult(){
           // do nothing at the moment.
       }

       public void onPositiveResult(ArrayList<WeatherWarning> warnings){
           onPositiveResult();
       }

       private void onPostExecute(ArrayList<WeatherWarning> warnings) {
           if (warnings!=null){
               WeatherWarnings.cleanWeatherWarningsDatabase(context);
               WeatherWarnings.writeWarningsToDatabase(context,warnings);
               onPositiveResult(warnings);
           } else {
               PrivateLog.log(context,PrivateLog.DATA,PrivateLog.ERR,"getting warnings failed.");
               onNegativeResult();
           }
       }

       @Override
       public void run() {
           onStart();
           ArrayList<WeatherWarning> weatherWarnings = doInBackground();
           onPostExecute(weatherWarnings);
       }
   }

    public static class WeatherForecastRunnable implements Runnable {

        private Context context;
        private ArrayList<Weather.WeatherLocation> weatherLocations;
        public boolean ssl_exception = false;

        private String[] seperateValues(String s){
            String[] resultarray = new String[Weather.DATA_SIZE];
            int index =0;
            while (String.valueOf(s.charAt(index)).equals(" ")){
                index++;
            }
            s=s.substring(index);
            resultarray = s.split(" +");
            return resultarray;
        }

        private String[] assigntoRaw(final Element element){
            String[] result = new String[Weather.DATA_SIZE];
            NodeList values = element.getElementsByTagName("dwd:value");
            for (int j=0; j<values.getLength(); j++){
                Element value_element = (Element) values.item(j);
                String value_string = value_element.getFirstChild().getNodeValue();
                result = seperateValues(value_string);
            }
            return result;
        }

        public WeatherForecastRunnable(Context context, ArrayList<Weather.WeatherLocation> weatherLocations){
            this.context = context;
            this.weatherLocations = weatherLocations;
        }

        private InputStream getWeatherInputStream(String stationName) throws IOException {
            String weather_url = "https://opendata.dwd.de/weather/local_forecasts/mos/MOSMIX_L/single_stations/"+stationName+"/kml/MOSMIX_L_LATEST_"+stationName+".kmz";
            String weather_url_legacy = "http://opendata.dwd.de/weather/local_forecasts/mos/MOSMIX_L/single_stations/"+stationName+"/kml/MOSMIX_L_LATEST_"+stationName+".kmz";
            // PrivateLog.log(context,Tag.SERVICE2,"URL: "+weather_url);
            URL url;
            URL url_legacy;
            try {
                url = new URL(weather_url);
                url_legacy = new URL(weather_url_legacy);
            } catch (MalformedURLException e){
                throw e;
            }
            try {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                InputStream inputStream = new BufferedInputStream(httpsURLConnection.getInputStream());
                return inputStream;
            } catch (SSLException e){
                ssl_exception = true;
                if (WeatherSettings.isTLSdisabled(context)){
                    // try fallback to http
                    try {
                        HttpURLConnection httpURLConnection = (HttpURLConnection) url_legacy.openConnection();
                        InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                        PrivateLog.log(context,PrivateLog.DATA,PrivateLog.WARN,"weather data is polled over http without encryption.");
                        return inputStream;
                    } catch (IOException e2){
                        throw e2;
                    }
                } else {
                    PrivateLog.log(context,PrivateLog.DATA,PrivateLog.ERR,"Error: ssl connection could not be established, but http is not allowed.");
                    throw e;
                }
            } catch (Exception e){
                throw e;
            }
        }

        private RawWeatherInfo updateWeatherLocationData(Weather.WeatherLocation weatherLocation) {
            try{
                InputStream inputStream = new BufferedInputStream(getWeatherInputStream(weatherLocation.name));
                ZipInputStream zipInputStream = new ZipInputStream(inputStream);
                zipInputStream.getNextEntry();
                // init new RawWeatherInfo instance to fill with data
                RawWeatherInfo rawWeatherInfo = new RawWeatherInfo();
                // populate name from settings, as name is file-name in API but not repeated in the content
                rawWeatherInfo.weatherLocation = weatherLocation;
                try {
                    DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document document = documentBuilder.parse(zipInputStream);
                    rawWeatherInfo.weatherLocation.description = "?";
                    // get sensor description, usually city name. This should be equal to weatherLocation.description,
                    // but we take it from the api to be sure nothing changed and the right city gets displayed!

                    NodeList placemark_nodes = document.getElementsByTagName("kml:description");
                    for (int i=0;i<placemark_nodes.getLength(); i++){
                        // should be only one, but we take the latest
                        Element placemark_element = (Element) placemark_nodes.item(i);
                        String description = placemark_element.getFirstChild().getNodeValue();
                        rawWeatherInfo.weatherLocation.description = description;
                    }

                    NodeList timesteps = document.getElementsByTagName("dwd:TimeStep");
                    for (int i=0; i<timesteps.getLength(); i++){
                        Element element = (Element) timesteps.item(i);
                        rawWeatherInfo.timesteps[i] = element.getFirstChild().getNodeValue();
                        rawWeatherInfo.elements = i;
                    }
                    NodeList forecast = document.getElementsByTagName("dwd:Forecast");
                    for (int i=0; i<forecast.getLength(); i++){
                        Element element = (Element) forecast.item(i);
                        String type     = element.getAttribute("dwd:elementName");
                        switch (type){
                            case "TTT": rawWeatherInfo.TTT = assigntoRaw(element); break;
                            case "E_TTT": rawWeatherInfo.E_TTT = assigntoRaw(element); break;
                            case "T5cm": rawWeatherInfo.T5cm = assigntoRaw(element); break;
                            case "Td": rawWeatherInfo.Td = assigntoRaw(element); break;
                            case "E_Td": rawWeatherInfo.E_Td = assigntoRaw(element); break;
                            case "Tx": rawWeatherInfo.Tx = assigntoRaw(element); break;
                            case "Tn": rawWeatherInfo.Tn = assigntoRaw(element); break;
                            case "TM": rawWeatherInfo.TM = assigntoRaw(element); break;
                            case "TG": rawWeatherInfo.TG = assigntoRaw(element); break;
                            case "DD": rawWeatherInfo.DD = assigntoRaw(element); break;
                            case "E_DD": rawWeatherInfo.E_DD = assigntoRaw(element); break;
                            case "FF": rawWeatherInfo.FF = assigntoRaw(element); break;
                            case "E_FF": rawWeatherInfo.E_FF = assigntoRaw(element); break;
                            case "FX1": rawWeatherInfo.FX1 = assigntoRaw(element); break;
                            case "FX3": rawWeatherInfo.FX3 = assigntoRaw(element); break;
                            case "FXh": rawWeatherInfo.FXh = assigntoRaw(element); break;
                            case "FXh25": rawWeatherInfo.FXh25 = assigntoRaw(element); break;
                            case "FXh40": rawWeatherInfo.FXh40 = assigntoRaw(element); break;
                            case "FXh55": rawWeatherInfo.FXh55 = assigntoRaw(element); break;
                            case "FX625": rawWeatherInfo.FX625 = assigntoRaw(element); break;
                            case "FX640": rawWeatherInfo.FX640 = assigntoRaw(element); break;
                            case "FX655": rawWeatherInfo.FX655 = assigntoRaw(element); break;
                            case "RR1c": rawWeatherInfo.RR1c = assigntoRaw(element); break;
                            case "RRL1c": rawWeatherInfo.RRL1c = assigntoRaw(element); break;
                            case "RR3": rawWeatherInfo.RR3 = assigntoRaw(element); break;
                            case "RR6": rawWeatherInfo.RR6 = assigntoRaw(element); break;
                            case "RR3c": rawWeatherInfo.RR3c = assigntoRaw(element); break;
                            case "RR6c": rawWeatherInfo.RR6c = assigntoRaw(element); break;
                            case "RRhc": rawWeatherInfo.RRhc = assigntoRaw(element); break;
                            case "RRdc": rawWeatherInfo.RRdc = assigntoRaw(element); break;
                            case "RRS1c": rawWeatherInfo.RRS1c = assigntoRaw(element); break;
                            case "RRS3c": rawWeatherInfo.RRS3c = assigntoRaw(element); break;
                            case "R101": rawWeatherInfo.R101 = assigntoRaw(element); break;
                            case "R102": rawWeatherInfo.R102 = assigntoRaw(element); break;
                            case "R103": rawWeatherInfo.R103 = assigntoRaw(element); break;
                            case "R105": rawWeatherInfo.R105 = assigntoRaw(element); break;
                            case "R107": rawWeatherInfo.R107 = assigntoRaw(element); break;
                            case "R110": rawWeatherInfo.R110 = assigntoRaw(element); break;
                            case "R120": rawWeatherInfo.R120 = assigntoRaw(element); break;
                            case "R130": rawWeatherInfo.R130 = assigntoRaw(element); break;
                            case "R150": rawWeatherInfo.R150 = assigntoRaw(element); break;
                            case "RR1o1": rawWeatherInfo.RR1o1 = assigntoRaw(element); break;
                            case "RR1w1": rawWeatherInfo.RR1w1 = assigntoRaw(element); break;
                            case "RR1u1": rawWeatherInfo.RR1u1 = assigntoRaw(element); break;
                            case "R600": rawWeatherInfo.R600 = assigntoRaw(element); break;
                            case "Rh00": rawWeatherInfo.Rh00 = assigntoRaw(element); break;
                            case "R602": rawWeatherInfo.R602 = assigntoRaw(element); break;
                            case "Rh02": rawWeatherInfo.Rh02 = assigntoRaw(element); break;
                            case "Rd02": rawWeatherInfo.Rd02 = assigntoRaw(element); break;
                            case "R610": rawWeatherInfo.R610 = assigntoRaw(element); break;
                            case "Rh10": rawWeatherInfo.Rh10 = assigntoRaw(element); break;
                            case "R650": rawWeatherInfo.R650 = assigntoRaw(element); break;
                            case "Rh50": rawWeatherInfo.Rh50 = assigntoRaw(element); break;
                            case "Rd00": rawWeatherInfo.Rd00 = assigntoRaw(element); break;
                            case "Rd10": rawWeatherInfo.Rd10 = assigntoRaw(element); break;
                            case "Rd50": rawWeatherInfo.Rd50 = assigntoRaw(element); break;
                            case "wwPd": rawWeatherInfo.wwPd = assigntoRaw(element); break;
                            case "DRR1": rawWeatherInfo.DRR1 = assigntoRaw(element); break;
                            case "wwZ": rawWeatherInfo.wwZ = assigntoRaw(element); break;
                            case "wwZ6": rawWeatherInfo.wwZ6 = assigntoRaw(element); break;
                            case "wwZh": rawWeatherInfo.wwZh = assigntoRaw(element); break;
                            case "wwD": rawWeatherInfo.wwD = assigntoRaw(element); break;
                            case "wwD6": rawWeatherInfo.wwD6 = assigntoRaw(element); break;
                            case "wwDh": rawWeatherInfo.wwDh = assigntoRaw(element); break;
                            case "wwC": rawWeatherInfo.wwC = assigntoRaw(element); break;
                            case "wwC6": rawWeatherInfo.wwC6 = assigntoRaw(element); break;
                            case "wwCh": rawWeatherInfo.wwCh = assigntoRaw(element); break;
                            case "wwT": rawWeatherInfo.wwT = assigntoRaw(element); break;
                            case "wwT6": rawWeatherInfo.wwT6 = assigntoRaw(element); break;
                            case "wwTh": rawWeatherInfo.wwTh = assigntoRaw(element); break;
                            case "wwTd": rawWeatherInfo.wwTd = assigntoRaw(element); break;
                            case "wwL": rawWeatherInfo.wwL = assigntoRaw(element); break;
                            case "wwL6": rawWeatherInfo.wwL6 = assigntoRaw(element); break;
                            case "wwLh": rawWeatherInfo.wwLh = assigntoRaw(element); break;
                            case "wwS": rawWeatherInfo.wwS = assigntoRaw(element); break;
                            case "wwS6": rawWeatherInfo.wwS6 = assigntoRaw(element); break;
                            case "wwSh": rawWeatherInfo.wwSh = assigntoRaw(element); break;
                            case "wwF": rawWeatherInfo.wwF = assigntoRaw(element); break;
                            case "wwF6": rawWeatherInfo.wwF6 = assigntoRaw(element); break;
                            case "wwFh": rawWeatherInfo.wwFh = assigntoRaw(element); break;
                            case "wwP": rawWeatherInfo.wwP = assigntoRaw(element); break;
                            case "wwP6": rawWeatherInfo.wwP6 = assigntoRaw(element); break;
                            case "wwPh": rawWeatherInfo.wwPh = assigntoRaw(element); break;
                            case "VV10": rawWeatherInfo.VV10 = assigntoRaw(element); break;
                            case "ww": rawWeatherInfo.ww = assigntoRaw(element); break;
                            case "ww3": rawWeatherInfo.ww3 = assigntoRaw(element); break;
                            case "W1W2": rawWeatherInfo.W1W2 = assigntoRaw(element); break;
                            case "WPc11": rawWeatherInfo.WPc11 = assigntoRaw(element); break;
                            case "WPc31": rawWeatherInfo.WPc31 = assigntoRaw(element); break;
                            case "WPc61": rawWeatherInfo.WPc61 = assigntoRaw(element); break;
                            case "WPch1": rawWeatherInfo.WPch1 = assigntoRaw(element); break;
                            case "WPcd1": rawWeatherInfo.WPcd1 = assigntoRaw(element); break;
                            case "N": rawWeatherInfo.N = assigntoRaw(element); break;
                            case "N05": rawWeatherInfo.N05 = assigntoRaw(element); break;
                            case "Nl": rawWeatherInfo.Nl = assigntoRaw(element); break;
                            case "Nm": rawWeatherInfo.Nm = assigntoRaw(element); break;
                            case "Nh": rawWeatherInfo.Nh = assigntoRaw(element); break;
                            case "Nlm": rawWeatherInfo.Nlm = assigntoRaw(element); break;
                            case "H_BsC": rawWeatherInfo.H_BsC = assigntoRaw(element); break;
                            case "PPPP": rawWeatherInfo.PPPP = assigntoRaw(element); break;
                            case "E_PPP": rawWeatherInfo.E_PPP = assigntoRaw(element); break;
                            case "RadS3": rawWeatherInfo.RadS3 = assigntoRaw(element); break;
                            case "RRad1": rawWeatherInfo.RRad1 = assigntoRaw(element); break;
                            case "Rad1h": rawWeatherInfo.Rad1h = assigntoRaw(element); break;
                            case "RadL3": rawWeatherInfo.RadL3 = assigntoRaw(element); break;
                            case "VV": rawWeatherInfo.VV = assigntoRaw(element); break;
                            case "D1": rawWeatherInfo.D1 = assigntoRaw(element); break;
                            case "SunD": rawWeatherInfo.SunD = assigntoRaw(element); break;
                            case "SunD3": rawWeatherInfo.SunD3 = assigntoRaw(element); break;
                            case "RSunD": rawWeatherInfo.RSunD = assigntoRaw(element); break;
                            case "PSd00": rawWeatherInfo.PSd00 = assigntoRaw(element); break;
                            case "PSd30": rawWeatherInfo.PSd30 = assigntoRaw(element); break;
                            case "PSd60": rawWeatherInfo.PSd60 = assigntoRaw(element); break;
                            case "wwM": rawWeatherInfo.wwM = assigntoRaw(element); break;
                            case "wwM6": rawWeatherInfo.wwM6 = assigntoRaw(element); break;
                            case "wwMh": rawWeatherInfo.wwMh = assigntoRaw(element); break;
                            case "wwMd": rawWeatherInfo.wwMd = assigntoRaw(element); break;
                            case "PEvap": rawWeatherInfo.PEvap = assigntoRaw(element); break;
                        }
                    }
                    PrivateLog.log(context, PrivateLog.DATA,PrivateLog.INFO,"Weather elements read: "+rawWeatherInfo.elements);
                    return rawWeatherInfo;
                } catch (Exception e){
                    // nothing to do
                }
            } catch (IOException e){
                // nothing to do
            }
            return null;
        }

        //* Override to do something before getting data starts

        public void onStart(){
          // do something
        }

        public void onNegativeResult(){
            // do nothing at the moment.
            PrivateLog.log(context,PrivateLog.DATA,PrivateLog.ERR,"Failed getting forecast data!");
        }

        /*
         * Override this routine to define what to do if obtaining data succeeded.
         * Remember: at this point, the new data is already written to the database and can be
         * accessed via the CardHandler class.
         */

        public void onPositiveResult(){
            // do nothing at the moment.
        }

        public void onPositiveResult(ArrayList<RawWeatherInfo> rawWeatherInfos){
            onPositiveResult();
        }

        private void addEntryToDatabase(RawWeatherInfo rawWeatherInfo){
            // get timestamp
            Calendar calendar = Calendar.getInstance();
            rawWeatherInfo.polling_time = calendar.getTimeInMillis();
            //context.getContentResolver().insert(WeatherContentManager.FORECAST_URI_ALL,WeatherContentManager.getContentValuesFromWeatherCard(rawWeatherInfo));
            String selection = ""+WeatherContentProvider.WeatherDatabaseHelper.KEY_FORECASTS_name+"=?";
            String[] selectionArgs = {rawWeatherInfo.weatherLocation.name};
            int result = context.getContentResolver().update(WeatherContentManager.FORECAST_URI_ALL,WeatherContentManager.getContentValuesFromWeatherCard(rawWeatherInfo),selection,selectionArgs);
            PrivateLog.log(context,PrivateLog.UPDATER,PrivateLog.INFO,"Weather entries updated: "+result);
            if (result==0){
                // insert, if the entry is not present in the data base. This happens, when this is a new station.
                Uri uri = context.getContentResolver().insert(WeatherContentManager.FORECAST_URI_ALL,WeatherContentManager.getContentValuesFromWeatherCard(rawWeatherInfo));
                PrivateLog.log(context,PrivateLog.UPDATER,PrivateLog.INFO,"New weather entry, content uri is: "+uri.toString());
            }
        }

        private void onPostExecute(ArrayList<RawWeatherInfo> rawWeatherInfos) {
            if (rawWeatherInfos == null) {
                onNegativeResult();
            } else {
                for (int i=0; i<rawWeatherInfos.size(); i++){
                    addEntryToDatabase(rawWeatherInfos.get(i));
                }
                onPositiveResult(rawWeatherInfos);
            }
        }

        @Override
        public void run() {
            onStart();
            ArrayList<RawWeatherInfo> rawWeatherInfos = new ArrayList<RawWeatherInfo>();
            for (int i=0; i<weatherLocations.size(); i++){
                RawWeatherInfo rawWeatherInfo = updateWeatherLocationData(weatherLocations.get(i));
                if (rawWeatherInfo!=null){
                    rawWeatherInfos.add(rawWeatherInfo);
                }
            }
            onPostExecute(rawWeatherInfos);
        }
    }

    public static class TextForecastRunnable implements Runnable {

        private Context context;

        private ArrayList<String> getTextFromUrl(String url_string){
            try {
                InputStream inputStream;
                try {
                    // https standard
                    URL url = new URL(url_string);
                    HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                    inputStream = new BufferedInputStream(httpsURLConnection.getInputStream());
                } catch (SSLException e){
                    // http fallback
                    String url_string_legacy = url_string.replace("https","http");
                    URL url_legacy = new URL(url_string_legacy);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url_legacy.openConnection();
                    inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                }
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                ArrayList<String> resultList = new ArrayList<String>();
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    resultList.add(line);
                }
                bufferedReader.close();
                return resultList;
            } catch (Exception e) {
                return null;
            }
        }

        private String getStringFromUrl(String url_string){
            StringBuilder stringBuilder = new StringBuilder();
            ArrayList<String> resultList = getTextFromUrl(url_string);
            for (int i=0; i<resultList.size(); i++){
                stringBuilder.append(resultList.get(i));
            }
            return stringBuilder.toString();
        }

        private ArrayList<TextForecast> getTextForecastMetadata(){
            ArrayList<TextForecast> textForecasts = new ArrayList<TextForecast>();
            ArrayList<TextForecasts.TextForecastSource> fileSources = TextForecasts.getTextForecastSources(context);
            for (int sourcePosition=0; sourcePosition<fileSources.size(); sourcePosition++){
                ArrayList<String> index = getTextFromUrl(fileSources.get(sourcePosition).getWebPath(context));
                if (index!=null) {
                    for (int i = 0; i < index.size(); i++) {
                        String s = index.get(i);
                        TextForecasts.TextForecastFile forecastFile = fileSources.get(sourcePosition).getValidFile(s);
                        if (forecastFile != null) {
                            TextForecast textForecast = new TextForecast();
                            textForecast.setIssued(s, 84);
                            textForecast.identifier = s.substring(s.indexOf(forecastFile.filename), s.indexOf(forecastFile.filename)+22);
                            if (textForecast.identifier.contains(">")){
                                textForecast.identifier = textForecast.identifier.substring(0,18);
                            }
                            textForecast.webUrl = fileSources.get(sourcePosition).getWebPath(context);
                            textForecast.type = forecastFile.type;
                            textForecasts.add(textForecast);
                        }
                    }
                }
            }
            return textForecasts;
        }

        public TextForecastRunnable(Context context) {
            this.context = context;
            // HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
            // InputStream inputStream = new BufferedInputStream(httpsURLConnection.getInputStream());
        }

        //* Override to do something before getting data starts

        public void onStart(){
            // do something
        }

        public void onNegativeResult(){
            // do nothing at the moment.
        }

        /*
         * Override this routine to define what to do if obtaining data succeeded.
         * Remember: at this point, the new data is already written to the database and can be
         * accessed via the CardHandler class.
         */

        public void onPositiveResult(){
            // do nothing at the moment.
        }

        @Override
        public void run() {
            onStart();
            ArrayList<TextForecast> textForecasts = getTextForecastMetadata();
            // determine which texts are new
            ArrayList<TextForecast> newTextForecasts = TextForecasts.getNewTextForecasts(context,textForecasts);
            PrivateLog.log(context,PrivateLog.DATA,PrivateLog.INFO,newTextForecasts.size()+ " new texts found in "+textForecasts.size()+ " available online.");
            for (int i=0;i<newTextForecasts.size(); i++){
                // read text data from web and parse
               newTextForecasts.get(i).parse(getTextFromUrl(newTextForecasts.get(i).getUrlString()));
               newTextForecasts.get(i).polled = Calendar.getInstance().getTimeInMillis();
            }
            // save new texts to database
            TextForecasts.writeTextForecastsToDatabaseUnconditionally(context,newTextForecasts);
            // call
            onPositiveResult();
        }
    }

    public static class RadarmapRunnable implements Runnable{
        private static final String WX_RADAR_URL="//opendata.dwd.de/weather/radar/composit/wx/raa01-wx_10000-latest-dwd---bin";
        private Context context;
        public boolean ssl_exception = false;

        public RadarmapRunnable(Context context){
            this.context = context;
        }

        private InputStream getRadarInputStream() throws IOException {
            String radar_url        = "https:"+WX_RADAR_URL;
            String radar_url_legacy = "http:"+WX_RADAR_URL;
            URL url;
            URL url_legacy;
            try {
                url = new URL(radar_url);
                url_legacy = new URL(radar_url_legacy);
            } catch (MalformedURLException e){
                throw e;
            }
            try {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                InputStream inputStream = new BufferedInputStream(httpsURLConnection.getInputStream());
                return inputStream;
            } catch (SSLException e){
                ssl_exception = true;
                if (WeatherSettings.isTLSdisabled(context)){
                    // try fallback to http
                    try {
                        HttpURLConnection httpURLConnection = (HttpURLConnection) url_legacy.openConnection();
                        InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                        PrivateLog.log(context,PrivateLog.DATA,PrivateLog.WARN,"weather data is polled over http without encryption.");
                        return inputStream;
                    } catch (IOException e2){
                        throw e2;
                    }
                } else {
                    PrivateLog.log(context,PrivateLog.DATA,PrivateLog.ERR,"ssl connection could not be established, but http is not allowed.");
                    throw e;
                }
            } catch (Exception e){
                throw e;
            }
        }

        private byte[] putRadarMapToCache(InputStream inputStream){
            byte[] radararray = new byte[1500*1400+1024];
            int radarMapsize = radararray.length;
            File cacheDir = context.getCacheDir();
            File cacheFile = new File(cacheDir,"radardata.bin");
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(cacheFile);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(radarMapsize);
                int i;
                byte[] cache = new byte[1024];
                while ((i=inputStream.read(cache))!=-1){
                    fileOutputStream.write(cache,0,i);
                    byteArrayOutputStream.write(cache,0,i);
                }
                fileOutputStream.close();
                byteArrayOutputStream.close();
                return byteArrayOutputStream.toByteArray();
            } catch (Exception e){
                return null;
            }
        }

        private byte[] getRadarMapFromCache(){
            byte[] radararray = new byte[1500*1400+1024];
            int radarMapsize = radararray.length;
            File cacheDir = context.getCacheDir();
            File cacheFile = new File(cacheDir,"radardata.bin");
            try {
                FileInputStream fileInputStream = new FileInputStream(cacheFile);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(radarMapsize);
                int i;
                byte[] cache = new byte[1024];
                while ((i=fileInputStream.read(cache))!=-1){
                    byteArrayOutputStream.write(cache,0,i);
                }
                fileInputStream.close();
                return byteArrayOutputStream.toByteArray();
            } catch (Exception e){
                return null;
            }
        }

        private boolean radarCacheFileExists(){
            File cacheDir = context.getCacheDir();
            File cacheFile = new File(cacheDir,"radardata.bin");
            return cacheFile.exists();
        }

        private RawRadarmap readRadarData(){
            byte[] radararray = null;
            if (radarCacheFileExists() && !WeatherSettings.isRadarDataOutdated(context)) {
                radararray = getRadarMapFromCache();
            } else
            {
                try {
                    InputStream inputStream = new BufferedInputStream(getRadarInputStream());
                    radararray = putRadarMapToCache(inputStream);
                    WeatherSettings.setPrefRadarLastdatapoll(context,Calendar.getInstance().getTimeInMillis());
                } catch (Exception e){
                    // do nothing
                }
            }
            if ((radararray==null) && (radarCacheFileExists())){
                radararray = getRadarMapFromCache();
            }
            if (radararray!=null){
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(radararray);
                RawRadarmap rawRadarmap = new RawRadarmap(byteArrayInputStream);
                return rawRadarmap;
            }
            return null;
        }

        public void onFinished(Radarmap radarmap){
            // override to do something with the map
        }

        @Override
        public void run() {
            Radarmap radarmap = new Radarmap(readRadarData());
            onFinished(radarmap);
        }
    }

    public static class RadarMNGeoserverRunnable implements Runnable{

        private static final String WN_RADAR_URL="//maps.dwd.de/geoserver/dwd/wms?service=WMS&version=1.1.0&request=GetMap&layers=dwd%3AWN-Produkt&bbox=-543.462%2C-4808.645%2C556.538%2C-3608.645&width=1100&height=1200&srs=EPSG%3A1000001&styles=&format=image%2Fpng";
        public static final String RADAR_CACHE_FILENAME = "radarMN.png";
        private Context context;
        private boolean forceUpdate = false;
        public boolean ssl_exception = false;

        public RadarMNGeoserverRunnable(Context context){
            this.context = context;
        }

        public RadarMNGeoserverRunnable(Context context, boolean forceUpdate){
            this.context = context;
            this.forceUpdate = forceUpdate;
        }

        public void setForceUpdate(boolean forceUpdate){
            this.forceUpdate = forceUpdate;
        }

        private InputStream getRadarInputStream() throws IOException {
            String radar_url        = "https:"+WN_RADAR_URL;
            String radar_url_legacy = "http:"+WN_RADAR_URL;
            URL url;
            URL url_legacy;
            try {
                url = new URL(radar_url);
                url_legacy = new URL(radar_url_legacy);
            } catch (MalformedURLException e){
                throw e;
            }
            try {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
                InputStream inputStream = new BufferedInputStream(httpsURLConnection.getInputStream());
                return inputStream;
            } catch (SSLException e){
                ssl_exception = true;
                if (WeatherSettings.isTLSdisabled(context)){
                    // try fallback to http
                    try {
                        HttpURLConnection httpURLConnection = (HttpURLConnection) url_legacy.openConnection();
                        InputStream inputStream = new BufferedInputStream(httpURLConnection.getInputStream());
                        PrivateLog.log(context,PrivateLog.DATA,PrivateLog.WARN,"MN radar data is polled over http without encryption.");
                        return inputStream;
                    } catch (IOException e2){
                        throw e2;
                    }
                } else {
                    PrivateLog.log(context,PrivateLog.DATA,PrivateLog.ERR,"Error: ssl connection could not be established, but http is not allowed.");
                    throw e;
                }
            } catch (Exception e){
                throw e;
            }
        }

        public static File getRadarMNFile(Context context){
            File cacheDir = context.getCacheDir();
            return new File(cacheDir,RADAR_CACHE_FILENAME);

        }

        private boolean putRadarMapToCache(InputStream inputStream){
            File cacheFile = getRadarMNFile(context);
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(cacheFile);
                int i;
                byte[] cache = new byte[1024];
                while ((i=inputStream.read(cache))!=-1){
                    fileOutputStream.write(cache,0,i);
                }
                fileOutputStream.close();
                inputStream.close();
                WeatherSettings.setPrefRadarLastdatapoll(context,Calendar.getInstance().getTimeInMillis());
            } catch (Exception e){
                return false;
            }
            return true;
        }

        public static boolean radarCacheFileExists(Context context){
            File cacheFile = getRadarMNFile(context);
            return cacheFile.exists();
        }

        public void onFinished(RadarMN radarMN){
            // override to do something with the map
        }

        @Override
        public void run() {
            try {
                // read new data from DWD geo server if present data does not exist or is outdated
                if ((!radarCacheFileExists(context) || (WeatherSettings.isRadarDataOutdated(context)) || (forceUpdate))){
                    putRadarMapToCache(getRadarInputStream());
                }
                // construct radar data from new or old data
                RadarMN radarMN = new RadarMN(context);
                if (radarMN.hasData()){
                    onFinished(radarMN);
                } else {
                    onFinished(null);
                }
            } catch (IOException e) {
                onFinished(null);
            }
        }
    }


}
