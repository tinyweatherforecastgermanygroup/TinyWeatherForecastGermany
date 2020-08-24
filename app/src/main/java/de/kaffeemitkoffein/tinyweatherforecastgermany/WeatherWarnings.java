/*
 * This file is part of TinyWeatherForecastGermany.
 *
 * Copyright (c) 2020 Pawel Dube
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.kaffeemitkoffein.tinyweatherforecastgermany;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.ZipInputStream;

public class WeatherWarnings {

    public final static int COMMUNEUNION_DWD_DIFF = 0;
    public final static int COMMUNEUNION_DWD_STAT = 1;


    private URL getWarningsUrl(int mode) throws MalformedURLException {
        switch (mode){
            case 0: return new URL("https://opendata.dwd.de/weather/alerts/cap/COMMUNEUNION_DWD_DIFF/Z_CAP_C_EDZW_LATEST_PVW_DIFFERENCE_PREMIUMDWD_COMMUNEUNION_DE.zip");
        }
        return new URL("https://opendata.dwd.de/weather/alerts/cap/COMMUNEUNION_DWD_STAT/Z_CAP_C_EDZW_LATEST_PVW_STATUS_PREMIUMDWD_COMMUNEUNION_DE.zip");
    }

    public class WeatherWarningReader extends AsyncTask<Void, Void, ArrayList<WeatherWarning>> {

        public Context context;
        public Weather.WeatherLocation weatherLocation;

        public WeatherWarningReader(Context context) {
            this.context = context;
            WeatherSettings weatherSettings = new WeatherSettings(context);
            weatherLocation = weatherSettings.getSetStationLocation();
        }

        public long getTimeStampFromString(String source){
            if (source != null){
                SimpleDateFormat kml_dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                kml_dateFormat.setLenient(true);
                try {
                    Date date = kml_dateFormat.parse(source);
                    return date.getTime();
                } catch (ParseException e){
                    PrivateLog.log(context,Tag.WARNINGS,"Malformed timestamp ("+source+"):"+e.getMessage());
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

            Log.v(Tag.WARNINGS,"reaching code....");

            nl = document.getElementsByTagName("code");
            warning.codes = new ArrayList<String>();
            for (int i=0;i<nl.getLength(); i++){
                // should be only one, but we take the latest
                Element element = (Element) nl.item(i);
                warning.codes.add(element.getFirstChild().getNodeValue());
            }

            Log.v(Tag.WARNINGS,"reaching references....");

            nl = document.getElementsByTagName("references");
            warning.references = new ArrayList<String>();
            for (int i=0;i<nl.getLength(); i++){
                // usually 1, but seems to be intended as multiple. We handle it in an arraylist to be safe.
                Element element = (Element) nl.item(i);
                String reference = element.getFirstChild().getNodeValue();
                warning.references.add(reference.substring(reference.indexOf(",")+1,reference.lastIndexOf(",")));
            }

            Log.v(Tag.WARNINGS,"reaching info....");

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
                                String warnid = getValue(subelement,"value");
                                if (warnid == null){
                                    warnid="-";
                                }
                                warning.area_warncellIDs.add(warnid);
                            }
                        }

                    }
                }
            }

            return warning;
        }

        @Override
        protected ArrayList<WeatherWarning> doInBackground(Void... voids) {
            Log.v(Tag.WARNINGS,"Entered doInBackground.");
            ArrayList<WeatherWarning> warnings = new ArrayList<WeatherWarning>();
            try {
                URL warningsUrl = new URL("https://opendata.dwd.de/weather/alerts/cap/COMMUNEUNION_DWD_STAT/Z_CAP_C_EDZW_LATEST_PVW_STATUS_PREMIUMDWD_COMMUNEUNION_DE.zip");
                ZipInputStream zipInputStream = new ZipInputStream(warningsUrl.openStream());
                // iterate through the warnings; each warning is a file
                int warnings_counter = 0;
                while (zipInputStream.getNextEntry() != null){
                    warnings_counter ++;
                    Log.v(Tag.WARNINGS,"Warning: "+warnings_counter);
                    StringBuffer stringBuffer = new StringBuffer();
                    byte[] buffer = new byte[4096];
                    int l=0;
                    while ((l=zipInputStream.read(buffer))>0){
                        stringBuffer.append(new String(buffer,0,l));
                    }
                    try {
                        WeatherWarning warning = parseWarning(stringBuffer.toString());
                        warnings.add(warning);
                        Log.v(Tag.WARNINGS,"Warning added: "+warnings_counter);
                    } catch (ParserConfigurationException e){
                        PrivateLog.log(context,Tag.WARNINGS,"Could not configure parser, skipping a warning:"+e.getMessage());
                    } catch (IOException e){
                        PrivateLog.log(context,Tag.WARNINGS,"I/O Error, skipping a warning:"+e.getMessage());
                    } catch (SAXException e){
                        PrivateLog.log(context,Tag.WARNINGS,"Possibly malformed warning, skipping a warning:"+e.getMessage());
                    }
                }
            } catch (MalformedURLException e){
                PrivateLog.log(context,Tag.WARNINGS,"Malformed URL for DWD resource:"+e.getMessage());
                return null;
            } catch (IOException e){
                PrivateLog.log(context,Tag.WARNINGS,"Unable to open DWD stream:"+e.getMessage());
                // return null;
            }
            Log.v(Tag.WARNINGS,"DoInBeckground did ok!");
            return warnings;
        }

        public void onNegativeResult(){
            // do nothing at the moment.
        }

        public void onPositiveResult(){
            // do nothing at the moment.
        }

        public void onPositiveResult(ArrayList<WeatherWarning> warnings){
            Log.v(Tag.WARNINGS,"onposres");
            for (int i=0; i<warnings.size(); i++){
                warnings.get(i).outputToLog();
            }
            onPositiveResult();
        }

        protected void onPostExecute(ArrayList<WeatherWarning> warnings) {
            Log.v(Tag.WARNINGS,"entered postexecute");
            Log.v(Tag.WARNINGS,"entered , size "+warnings.size());
            if (warnings!=null){
                Log.v(Tag.WARNINGS,"postexec has results");
                onPositiveResult(warnings);
            } else {
                PrivateLog.log(context,Tag.WARNINGS,"Fatal: warnings failed.");
            }
        }
    }

    public void WarningsToLog(Context context){
        Log.v(Tag.WARNINGS,"Warnings started!");
        WeatherWarningReader weatherWarningReader = new WeatherWarningReader(context);
        Log.v(Tag.WARNINGS,"Warnings initalized");
        weatherWarningReader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        Log.v(Tag.WARNINGS,"Warnings executed");
    }
}
