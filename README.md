Tiny Weather Forecast Germany
=================================

Weather forecast with widgets for up to 10 days, based on open data from the Deutscher Wetterdienst (DWD).

Screenshots
--------

![Screenshot #1](fastlane/metadata/android/en-US/images/phoneScreenshots/1.png)
![Screenshot #2](fastlane/metadata/android/en-US/images/phoneScreenshots/2.png)
![Screenshot #3](fastlane/metadata/android/en-US/images/phoneScreenshots/3.png)

How to get the app
------------------

Tiny Weather Forecast Germany is available from the F-Droid main repository. You can download it here:

[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png" 
    alt="Get it on F-Droid"
    height="80">](https://f-droid.org/packages/de.kaffeemitkoffein.tinyweatherforecastgermany)

**Alternative sources:**

You can get the binary builds here: <https://kaffeemitkoffein.de/nextcloud/index.php/s/qqao88LzSQ4rTeg>

Or from the developer's own fdroid repo: https://kaffeemitkoffein.de/fdroid

You get userdebug builds for testing here: <https://kaffeemitkoffein.de/nextcloud/index.php/s/4SXHaLxLSmFd8Ri>

Please note that the builds linked here are not signed by the fdroid key, so you basically need to uninstall the fdroid versions before installing them and vice versa.

License
-------

 Copyright (c) 2020, 2021 Pawel Dube

 This program is free software: you can redistribute it and/or modify it
 under the terms of the GNU General Public License as published by the
 Free Software Foundation, either version 3 of the License, or (at
 your option) any later version.

 Tiny Weather Forecast Germany is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Tiny Weather Forecast Germany. If not, see <http://www.gnu.org/licenses/>.

Credits
-------

 The Material Design icons are Copyright (c) Google Inc., licensed 
 under the Apache License Version 2.0.
 
 This app uses gradle and the gradle wrapper, Copyright Gradle Inc,
 licensed under the Apache 2.0 license.
 
 Data source: Deutscher Wetterdienst, own elements added, some data 
 averaged over individual values.
 
 This program uses the WeatherSpec class of Gadgetbridge,
 Copyright (C) 2016-2020 Andreas Shimokawa, Carsten Pfeiffer,
 Daniele Gobbetti, licensed under the GNU AFFRERO GENERAL PUBLIC LICENSE,
 Version 3, 19 November 2007. 
 
 This program uses PNPOLY - Point Inclusion in Polygon Test by W. Randolph Franklin (WRF), Copyright (c) 1970-2003, Wm. Randolph Franklin
 
 This program uses the Astronomy class, Copyright (C) 2010-2012, Helmut Lehmeyer, licensed under the GNU General Public License version 3.
 
 PAT maps by Ian Macky <http://ian.macky.net/pat>.
 
 Contributors:
 - Andreas Shimokawa (bugfixes & support for the Gadgetbridge API)
 - Izzy (metadata improvements)
 - Janis Bitta (new app icon)
 - Jonas Lochmann (.gitignore)
 - Marc Nause (code improvements, bitmap caching)
 - Mondstern (Dutch translation)
 
 Privacy
 -------
 
 For the privacy statement, see [here](https://codeberg.org/Starfish/TinyWeatherForecastGermany/wiki/Home).

 Concept
 -------
 
 The idea is to create a simple, floss and light-weight weather forecast app with a focus on home screen widgets that uses open data from the Deutscher Wetterdienst (DWD) and that does not track the users.
 
 Permissions
 -------
 The app uses the following permissions:
 <li>Internet: needed to get forecast data from the DWD.</li> 
 <li>Access network state: query network state before getting data.</li>
 <li>foreground service: a foreground service is used to reliably get the forecast data. The service does not run all the time but only when a weather forecast update from the DWD needs to be done, and it runs until it is finished.</li>
 <li>Receive boot completed: the app needs to know about a reboot to restart periodic checks if an update is due, to update widgets and to send data to Gadgetbridge when enabled.</li>
 <li>Access fine location: used to determine the closest weather sensors to your position. This permission needs not to be granted if this feature is not used or if the search is performed based on manually entered geo-coordinates.</li>

 FAQ
 ---
 *For locations in a different time zone the day/night icons seem incorrect.*
 
 The app always displays the date & time of your device (and locale). Example: you are in Berlin and have selected the weater for Cuba. The app shows you the weather in Cuba at the corresponding Berlin time and uses the day/night-icons corresponding to the Berlin time. Once you have travelled to Cuba and your device switched to the local time in Cuba, the app will display the weather in Cuba at the Cuba time.   
 
 *How to read the widget?*
 
 The widget icon, the weather description and the current temperature refer to the weather forecast that can be expected until the next full hour. The low and high temperatures refer to the values that can be expected to occur from now to midnight.

 When showing more days (bold widget, large widget), the min and max values and the weather symbol refer to the whole day.  
 
 *What do the symbols in the main app mean?*
 
 Symbols used:
 <img src="app/src/main/res/mipmap-mdpi/symbol_precipitation.png" height="16" width="16"/> precipitation
<img src="app/src/main/res/mipmap-mdpi/symbol_cloud.png" height="16" width="16"/> clouds
 <img src="app/src/main/res/mipmap-mdpi/symbol_lightning.png" height="16" width="16"/> thunderstorm
 <img src="app/src/main/res/mipmap-mdpi/symbol_hail.png" height="16" width="16"/> hail
 <img src="app/src/main/res/mipmap-mdpi/symbol_freezing_rain.png" height="16" width="16"/> freezing rain
 <img src="app/src/main/res/mipmap-mdpi/symbol_fog.png" height="16" width="16"/>  fog
 <img src="app/src/main/res/mipmap-mdpi/symbol_drizzle.png" height="16" width="16"/> drizzle
 <img src="app/src/main/res/mipmap-mdpi/arrow.png" height="16" width="16"/> wind direction
 
 *My widgets don't get updated (unless I open the app).*

The widgets get updated every 15-30 minutes. If this does not happen at all or only happens when you open the app, then you likely have a device that prefers battery life over proper functionality. Likely, some so-called *battery saving feature* kills the components of the app and breaks the updates. See [this page](https://https://dontkillmyapp.com/) to ckeck if you own such a device and what you can do. 

 *Does the app display the actual, current weather?*
   
 No. This app is about weather forecasts. You see the weather you can expect to occur until the displayed time. For the widgets, see above.  
        
 *How often does the app update the weather forecast?*
 
 The Deutscher Wetterdienst updates the forecast data that is used every 6 hours. Therefore, it does not make sense to pull weather data more frequently than this from the DWD API. However, a manual data update triggered by the user's selection in the main app always forces an update of forecast data. The forecast data covers the next ten days. So it is pretty feasible to present a weather forecast for some time without polling new data.
 
 *How often does the GadgetBridge app gets updated (when this feature is enabled)?*
 
  When GadgetBridge support is **enabled**, the app will, in the best case, update GadgetBridge every 30 minutes using forecast data that is already in place, meaning that the DWD API will not be called for this. However, on devices with API 23 or higher, such updates might not occur that regularly when the device goes in *doze mode*, but should be launched in the so-called “maintenance window”, and it is difficult to say what this really means in manners of time. This will likely mean very different things depending on the device and/or ROM.
  
  If you encounter problems with GadgetBridge not updating, placing the widget on the home screen may help, since the widget will try to also update GadgetBridge every time the widget itself gets updated by the system.
  
  *Why is percipitation displayed in kg/m² instead of cm?*
  
  Because the DWD provides this data this way. This cannot be changed. Please consider that this unit is more precise regarding the amount of hail and snow.
  
  *Why does the app not display sunrise und sunset?*
  
  Sunrise and sunset cannot be reliably calculated with the formulas used for latitudes < -65° and > 65°. When this is the case, this feature is disabled.

*Why does the app show a particular weather icon (and not an other)?*

Icons refer to *significant weather conditions*. The idea is to show you the most important (significant) weather condition that you can expect to occur within the given time period. For example, thunderstorms have a higher priority than rain and rain/fog have a higher priority than clouds. If such a significant weather condition is likely to occur, it will be preferred over others.

Usually, the weather conditions are calculated by the DWD. If a weather condition is not available in the forecast data, the app tries to calculate it from other items. If you are interested how this app calculates icons in this case, see [here](https://codeberg.org/Starfish/TinyWeatherForecastGermany/src/branch/master/app/src/main/java/de/kaffeemitkoffein/tinyweatherforecastgermany/WeatherCodeContract.java) in the source code.

Thresholds for significant weather conditions are subjective and perhaps debatable, but weather conditions calculated by the DWD have priority and always remain unmodified, if available. If you are interested in the priorities, see [this DWD document](https://www.dwd.de/DE/leistungen/opendata/help/schluessel_datenformate/kml/mosmix_element_weather_xls.xlsx?__blob=publicationFile&v=6).

 Contributing
 ------------

 Please leave comments, bug reports, issues and feature requests at
 the app repository at codeberg.org:
 
 https://codeberg.org/Starfish/TinyWeatherForecastGermany
 
 Alternatively, for suggestions and bug reports, you can contact me
 by email: weather (at) kaffeemitkoffein.de 
