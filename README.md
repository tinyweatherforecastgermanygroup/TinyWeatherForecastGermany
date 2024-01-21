# Tiny Weather Forecast Germany

Weather forecast with widgets for up to 10 days, based on open data from the Deutscher Wetterdienst (DWD).

## Screenshots

![Screenshot #1](https://codeberg.org/Starfish/TinyWeatherForecastGermany/media/branch/master/fastlane/metadata/android/en-US/images/phoneScreenshots/1.png)
![Screenshot #2](https://codeberg.org/Starfish/TinyWeatherForecastGermany/media/branch/master/fastlane/metadata/android/en-US/images/phoneScreenshots/2.png)
![Screenshot #3](https://codeberg.org/Starfish/TinyWeatherForecastGermany/media/branch/master/fastlane/metadata/android/en-US/images/phoneScreenshots/3.png)

## How to get the app

Tiny Weather Forecast Germany is available from the F-Droid main repository. You can download it here:

[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png" alt="Get it on F-Droid" height="80px">](https://f-droid.org/packages/de.kaffeemitkoffein.tinyweatherforecastgermany)

**Alternative sources:**

You can get the binary builds here: <https://kaffeemitkoffein.de/nextcloud/index.php/s/qqao88LzSQ4rTeg>

Or from the developer's own F-Droid repository: <https://kaffeemitkoffein.de/fdroid>

You get userdebug builds for testing here: <https://kaffeemitkoffein.de/nextcloud/index.php/s/4SXHaLxLSmFd8Ri>

Please note that the builds linked here are not signed by the F-Droid key, so you basically need to uninstall the F-Droid versions before installing them and vice versa.

For a detailed list of recent changes, see [the changelog](https://codeberg.org/Starfish/TinyWeatherForecastGermany/src/branch/master/changelog.txt). 

## License

Copyright (c) 2020, 2021, 2022, 2023 Pawel Dube

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

## Credits

The Material Design icons are Copyright (c) Google Inc., licensed
under the Apache License Version 2.0.

This app uses gradle and the gradle wrapper, Copyright Gradle Inc,
licensed under the Apache 2.0 license.

Data source: Deutscher Wetterdienst, own elements added, some data
averaged over individual values. Weather warnings are abbreviated. Source for geo-data: Deutscher Wetterdienst.

This program uses the WeatherSpec class of Gadgetbridge,
Copyright (C) 2016-2020 Andreas Shimokawa, Carsten Pfeiffer,
Daniele Gobbetti, licensed under the GNU AFFRERO GENERAL PUBLIC LICENSE,
Version 3, 19 November 2007.

This program uses PNPOLY - Point Inclusion in Polygon Test by W. Randolph Franklin (WRF), Copyright (c) 1970-2003, Wm. Randolph Franklin

This program uses the Astronomy class, Copyright (C) 2010-2012, Helmut Lehmeyer, licensed under the GNU General Public License version 3.

PAT maps by Ian Macky <http://ian.macky.net/pat>. Images of the "Lange Anna" (Helgoland) and the Pellworm lighthouse by arwanger.

Calculation of relative humidity from temperature and dew point is done using a simplified formula example from the DWD (Deutscher Wetterdienst), see <https://www.dwd.de/DE/leistungen/met_verfahren_mosmix/faq/faq_mosmix_node.html>.

Uses the Solarized Precision colors for machines and people theme, Copyright (c) 2011 Ethan Schoonover

Contributors:

* Andreas Shimokawa (bugfixes & support for the Gadgetbridge API)
* Izzy (metadata improvements)
* Janis Bitta (new app icon)
* Jonas Lochmann (.gitignore)
* Marc Nause (code improvements, bitmap caching)
* Jean-Luc Tibaux (French translation, bugfixes, javadoc improvements, translations of this README.md file)
* arwanger (README.md)
* maximilianovermeyer (fix of geo-intent-handling)

Translations:

* Czech: eUgEntOptIc44, mondstern
* Danish: eUgEntOptIc44, mondstern
* Dutch: Allan Nordhøy, alternative_be, eUgEntOptIc44, jwildeboer, mondstern, Vistaus
* English: Allan Nordhøy
* French: Allan Nordhøy, eUgEntOptIc44, J. Lavoie, Jean-Luc Tibaux, lejun, mondstern
* Finnish: eUgEntOptIc44, mondstern
* German: Aircan, Allan Nordhøy, buhtz, eUgEntOptIc44, Hexagon, Hiajen, J. Lavoie, mondstern, silmaril, w4ts0n
* Hungarian: eUgEntOptIc44, Gőz Barnabás
* Indonesian: Linerly
* Italian: mondstern, eUgEntOptIc44
* Irish: mondstern
* Norwegian Bokmål: Allan Nordhøy, eUgEntOptIc44, mondstern
* Polish: eUgEntOptIc44, Eryk Michalak, ewm, mondstern
* Russian: Wirdi51
* Spanish: eUgEntOptIc44, hegondev
* Swedish: tygyh
* Ukrainian: eUgEntOptIc44, Andrij Mizyk, SomeTr

Get involved in *Tiny Weather Forecast Germany* and [help to translate it into more languages or complete existing translations](https://translate.codeberg.org/engage/tiny-weather-forecast-germany/).

[<img src="https://translate.codeberg.org/widgets/tiny-weather-forecast-germany/-/multi-blue.svg">](https://translate.codeberg.org/engage/tiny-weather-forecast-germany/)

## Privacy

For the privacy statement, see [here](https://codeberg.org/Starfish/TinyWeatherForecastGermany/wiki/Home).

## Concept

The idea is to create a simple, floss and light-weight weather forecast app with a focus on home screen widgets that uses open data from the Deutscher Wetterdienst (DWD) and that does not track the users.

## Permissions

The app uses the following permissions:

- INTERNET: needed to get weather data from the DWD.
- ACCESS_NETWORK_STATE: query network state before getting data.
- RECEIVE_BOOT_COMPLETED: the app needs to know about a reboot to restart periodic syncs of weather data.
- ACCESS_COARSE_LOCATION & ACCESS_FINE_LOCATION: used to optionally determine the closest weather sensors to your position. This permission needs not to be granted if this feature is not used or if the search is performed based on manually entered geo-coordinates.
- ACCESS_BACKGROUND_LOCATION: optionally used to passively check the location while the app is not running and switch to a closer weather station if appropriate. This permission needs not to be granted if this feature is not used.
- REQUEST_IGNORE_BATTERY_OPTIMIZATIONS: needed to present a user-friendly dialog to optionally disable battery optimization. This is only necessary when enabling location checks in the background.
- POST_NOTIFICATIONS: needed to post notifications about weather warnings.
- AUTHENTICATE_ACCOUNTS, WRITE_SYNC_SETTINGS & READ_SYNC_SETTINGS: used to automatically create a sync account to perform background syncs of weather data. The information synced can be configured in detail in the app settings.

Since version 0.62.0, the FOREGROUND_SERVICE permission is no longer necessary and was removed.

## FAQ

### For locations in a different time zone the day/night icons seem incorrect.

The app always displays the date & time of your device (and locale). Example: you are in Berlin and have selected the weather for Cuba. The app shows you the weather in Cuba at the corresponding Berlin time and uses the day/night-icons corresponding to the Berlin time. Once you have travelled to Cuba and your device switched to the local time in Cuba, the app will display the weather in Cuba at the Cuba time.

### How to read the widget?

The widget icon, the weather description and the current temperature refer to the weather forecast that can be expected until the next full hour. The low and high temperatures refer to the values that can be expected to occur from now to midnight.

When showing more days (bold widget, large widget), the min and max values and the weather symbol refer to the whole day.

Since version 0.58.0 onward, this app can display weather warnings in widgets. To display weather warnings in widgets, you need to enable this feature in the settings. When a warning is issued for your chosen location, the classic widget and the large widget display a warning symbol. The clock widget and the bold widget also display a small text indicating the warning category of the *most severe* weather warning issued for your location. When there are multiple warnings issued, this is indicated by three dots ("...") and a plus ("+"). You have to go to the app to see them all.  

### What do the symbols in the main app mean?

Symbols used:
<img src="https://codeberg.org/Starfish/TinyWeatherForecastGermany/media/branch/master/app/src/main/res/mipmap-mdpi/symbol_precipitation.png" height="16" width="16"/> precipitation
<img src="https://codeberg.org/Starfish/TinyWeatherForecastGermany/media/branch/master/app/src/main/res/mipmap-mdpi/symbol_cloud_black.png" height="16" width="16"/> clouds
<img src="https://codeberg.org/Starfish/TinyWeatherForecastGermany/media/branch/master/app/src/main/res/mipmap-mdpi/symbol_lightning.png" height="16" width="16"/> thunderstorm
<img src="https://codeberg.org/Starfish/TinyWeatherForecastGermany/media/branch/master/app/src/main/res/mipmap-mdpi/symbol_hail_black.png" height="16" width="16"/> hail
<img src="https://codeberg.org/Starfish/TinyWeatherForecastGermany/media/branch/master/app/src/main/res/mipmap-mdpi/symbol_freezing_rain_black.png" height="16" width="16"/> freezing rain
<img src="https://codeberg.org/Starfish/TinyWeatherForecastGermany/media/branch/master/app/src/main/res/mipmap-mdpi/symbol_fog_black.png" height="16" width="16"/>  fog
<img src="https://codeberg.org/Starfish/TinyWeatherForecastGermany/media/branch/master/app/src/main/res/mipmap-mdpi/symbol_drizzle.png" height="16" width="16"/> drizzle
<img src="https://codeberg.org/Starfish/TinyWeatherForecastGermany/media/branch/master/app/src/main/res/mipmap-mdpi/arrow_black.png" height="16" width="16"/> wind direction
<img src="https://codeberg.org/Starfish/TinyWeatherForecastGermany/media/branch/master/app/src/main/res/mipmap-mdpi/symbol_temperature5cm_black.png" height="16" width="16"/> temperature 5 cm above ground level
<img src="https://codeberg.org/Starfish/TinyWeatherForecastGermany/media/branch/master/app/src/main/res/mipmap-mdpi/symbol_rh_black.png" height="16" width="16"/> relative humidity
<img src="https://codeberg.org/Starfish/TinyWeatherForecastGermany/media/branch/master/app/src/main/res/mipmap-mdpi/biocular_black.png" height="16" width="16"/> visibility

### What do the icons shown do?

You may get a hint about their functionality if you just *long-press* them.

### Why are some locations labelled "DMO" and show a forecast for about three days only?

Since version 0.59.4, Tiny Weather Forecast Germany also includes [some of the DMO forecasts available from the Deutscher Wetterdienst (DWD) since 17.01.2023](https://www.dwd.de/DE/fachnutzer/forschung_lehre/meteorologische_fachverfahren/mosmix_aenderungen/downloads/2023_0117_mosmix_aenderung.pdf) in the open data.

*DMO* (Direct Model Output) locations provide forecasts based on the numeric ICON13 and ICON6-NEST weather forecast models. *DMO* locations usually provide a more limited data set with a shorter forecast period than MOSMIX stations. Since the *DMO* data is not post-processed using *MOS (Model Output Statistics)*, MOSMIX stations are preferred over *DMO* locations.

Therefore, Tiny Weather Forecast Germany currently only includes a small subset of available *DMO* locations and limits it to locations not already well-represented by the Mosmix data. In particular, *DMO* locations with the same name already present in the Mosmix data and/or within +/- 0.02 longitude and latitude are ignored.

Currently (version 0.59.4), Tiny Weather Forecast Germany uses only 195 of the 3541 *DMO* locations in total, since all other locations meet the criteria above having a much better Mosmix forecast point available instead.

Should you have *"Geographic coordinates"* enabled (geographic coordinates of the weather station are displayed in the app), *DMO* stations are indicated by "*(DMO)*" in the app.

Since *DMO* forecasts get updated every 12 hours only, periodic updates automatically get postponed should you have set a 6-hourly update cycle and did choose a *DMO* location.

### My widgets don't get updated (unless I open the app).

Home screen widgets usually get updated every 30 minutes by the system. However, to take into account some manufacturer and/or rom limitations, the sync adapter also updates the widgets.

Make sure that you did not *disable the syncs manually* in the account settings. Furthermore, some device options like the battery saver may turn off syncs temporarily (e.g. until the device gets charged again). 

 If widget updates do not happen at all or only happen when you open the app, then you likely have a device that prefers battery life over proper functionality. Likely, some so-called *battery saving feature* kills the components of the app and breaks the updates. See [this page](https://dontkillmyapp.com/) to check if you own such a device and what you can do.

You may try to disable battery optimization for Tiny Weatherforecast Germany.

Again, make sure that in Settings -> Passwords & accounts -> Weather -> Account sync is *not disabled*.

### My widgets don't update the station automatically when I am on travel.

1. You need to grand the _background location permission_. Please note on some devices you must grant the general location permission before you can grant the background permission. The app asks all of this is the appropriate order. Should you have declined any of the location permissions, you can grant them in the app settings.
2. You also need to enable background location checks in the app settings.
3. To save your battery, the app never starts an active location fix in the background. It only re-uses locations obtained by other apps, e.g. map applications. Simply start the location search in any app on your device or search actively for the closest station within the app. All of this will make the widgets change to the closest station.
4. Please also note that the background-checks are performed every 15-30 minutes only. 
5. Open the app to immediately check for known, passive locations.

Should all of this not help: this functionality may be broken due to energy saving apps or roms breaking expected functionality. See above "my widgets don't get updated (unless I open the app)". 

### How often does the app update weather data?

Preface: the Deutscher Wetterdienst updates the forecast data that is used every 6 hours for *Mosmix* locations (most of the locations available), and every 12 hours for *DMO* locations. Therefore, it does not make sense to pull weather data more frequently than every 6 or 12 hours, depending on your chosen location.

Before getting any data from the internet, the app always tries to reuse the data in place and performs syncs only when necessary.

You can set up the sync options in detail in the app settings. For the most use-cases, you should enable sync for weather and warnings, and keep it disabled for pollen count, texts and maps since the latter information is not displayed on the main screen and in the home screen widgets.

For a scenario with only intermittent and/or limited access to the internet, e.g. when using unmetered networks only, you may also consider enabling automatic sync for pollen count, texts and maps. 

The sync intervals for weather forcasts may be set to 6, 12, 18 or 24 hours.

The sync intervals for weather warnings may be set to 15 minutes, 30 minutes, 1 hour, 2 hours, 3 hours or 6 hours.

A sane compromise between battery use, network use and up-to-date information is to sync weather forecasts every 24h and warnings every 30 minutes.

The background sync interval for texts, pollen count and maps is every 24 hours, if enabled. You can trigger a forced update of this information from inside the app in the respective view. 

You see the last update time of the weather forecasts in the main app. *Long pressing* this text makes the app display the time the weather forecast was *issued* by the DWD.

The app uses a sync adapter that runs in the background. This sync adapter is triggered periodically by the system based on your settings (e.g. every 30 minutes to update warnings). The timing is *inexact*, so that the system can combine various sync requests to safe your battery. The system won't call the sync adapter when no suitable network is available and/or other system settings like the battery saver prevent background syncs. Syncs may also not take place when the device is idle, e.g. in "doze mode". 

### What can I do if the app does not sync data at all?

Please make sure that:
- syncs are *enabled* in the app settings,
- sync is *enabled* in Settings -> Passwords & accounts -> Weather,
- you have *not turned on* the battery saver.

Contrary to previous app versions, putting a widget on the home screen has no influence on sync intervals.

When the above fails, you may try the following:
- grant the app the "allow all the time" location permission,
- allow the app to run in the background (exclude it from battery optimization).

### How often does the GadgetBridge app gets updated (when this feature is enabled)?

When GadgetBridge support is **enabled**, the app will update GadgetBridge simultaneously when performing syncs.

With enabled GadgetBridge support, the sync adapter will be called at least once per hour to check if updates are necessary and will send data to GadgetBridge. 

Please note that GadgetBridge will not get updated according to the user settings when:
- no suitable network is available to perform syncs, 
- the device is in "doze mode" (idle), 
- sync is disabled in Settings -> Passwords & accounts -> Weather.

**Since the update logic changed with version 0.62.0 to better account for battery use, users are encouraged to file bug reports should GadgetBridge updates not work like expected.**

### Why is precipitation displayed in kg/m² instead of mm?
  
This unit is more precise regarding solid precipitation (e.g. snow). Assuming rain, after some converting of units you will notice that the value is the same like "mm".
  
### Why does the app sometimes not display sunrise und sunset?
  
Sunrise and sunset cannot be reliably calculated with the formulas used for latitudes < -65° and > 65°. When this is the case, this feature is disabled.

### Why does the app show a particular weather icon (and not an other)?

Icons refer to *significant weather conditions*. The idea is to show you the most important (significant) weather condition that you can expect to occur within the given time period. For example, thunderstorms have a higher priority than rain and rain/fog have a higher priority than clouds. If such a significant weather condition is likely to occur, it will be preferred over others.

Usually, the weather conditions are *calculated by the DWD*. If a weather condition is not available in the forecast data, the app tries to calculate it from other items. If you are interested how this app calculates icons in this case, see [here](https://codeberg.org/Starfish/TinyWeatherForecastGermany/src/branch/master/app/src/main/java/de/kaffeemitkoffein/tinyweatherforecastgermany/WeatherCodeContract.java) in the source code.

Thresholds for significant weather conditions are subjective and perhaps debatable, but weather conditions calculated by the DWD have priority and always remain unmodified (for one single exception, see below), if available. If you are interested in the priorities, see [this DWD document](https://www.dwd.de/DE/leistungen/opendata/help/schluessel_datenformate/kml/mosmix_element_weather_xls.xlsx?__blob=publicationFile&v=6).

However, the official label for thunderstorms (having the highest priority available) is *slight or moderate thunderstorm with rain or snow*; to prevent confusion, Tiny Weather Forecast Germany shows lightning with rain if temperature is above 0° C *or* snowflakes if it is equal or below zero.

### Why does the app give a different value for some parameter (e.g. temperature) than the official station reading?

Tiny Weather Forecast Germany gives *forecasts*, not the current reading at some weather station. If the displayed value in Tiny Weather Forecast Germany differs from an official reading at some spot, the forecast was just off.

### Where do the names come from that are offered in searches?

Tiny Weather Forecast Germany uses *WarncellIDs* for the regions offered. These are closely related to the [Amtlicher Gemeindeschlüssel](https://www.destatis.de/DE/Themen/Laender-Regionen/Regionales/Gemeindeverzeichnis/_inhalt.html) (AGS) provided by [destatis](https://www.destatis.de/DE/Home/_inhalt.html). Basically, the WarncellIDs are an extension of the AGS. WarncellIDs also feature human-readable names (e.g. "Landkreis Göttingen"). These names can be used while looking for a weather station. They *do not* refer to the names of weather stations. Once you select a name, depending on your setting, Tiny Weather Forecast Germany will automatically determine the closest weather station or will give you a list of close by stations including their distance to choose from. This distance is derived from the centroid (the center of the area polygon(s)) associated with a given region. It may happen, that a WarncellID has more than one search key associated. E.g. searching for `Insel Helgoland` or `Gemeinde Helgoland` will show a list starting with `Helgoland` at an approximate distance of 0.4 km. Hence the weather station is actually just called `Helgoland`.

*Note*: more than one station will be shown if your search matches a search key. These stations are sorted by increasing distance from the initially searched entity. E.g. searching for `Insel Helgoland` in the above example will also show `Elbmuendung` (a sea area next to Cuxhaven) which is 23.4 km away, `UFS Deutsche Bucht` (an automatized weather station aboard a light vessel in the middle of the German Bight) already 29.3 km away, or `Wangerooge` (one of the East Frisian islands) at a distance of 29.4 km. (And of course many others even further away.) If `Helgoland` is searched however, it matches the station right away, and the app jumps to it immediately without a further selection.

As a rule of thumb, when a name is written in capital letters (e.g. "BERLIN-ALEX."), it is the weather station. All other names help you find the proper weather station.

### Is there a map available to choose a spot if I don't know the proper names in the vicinity?

You can use any application on your device that allows to share coordinates via `geo:`-intents to Tiny Weather Forecast Germany. Most mapping and location applications will allow this.

This also works from a web browser, provided the page in question offers a `geo:` link. [geohack](https://geohack.toolforge.org) is a common service that provides such links as `Geo URI` (e.g. for [Helgoland](https://geohack.toolforge.org/geohack.php?pagename=Helgoland&language=de&params=54.1825_N_7.8852777777778_E_region:DE-SH_type:city(1307))). You may want to note that also Wikipedia links to this service once you click on the geographic coordinates in their web pages. (However, they do not provide a direct `geo:` link, yet.)

### Why does the delete icon next to the location not remove the selected location?

The delete icon works the other way around: it does not remove the currently displayed region but *all other* regions that were selected at some point in the past. The idea of Tiny Weather Forecast Germany is centred around the idea of your usual location and not so much on a list of bookmarks.

It is also implemented this way to quickly delete a presumptive travel history. Think of it as a privacy feature.

### Why aren't all locations in my bookmarks?

When on travel and with the passive location checks in the background enabled, the weather station may switch multiple times before you open the app. To prevent the app from recording a detailed travel history, only locations that applied when you actively opened the app are added to the bookmarks. PLease also note that the number of bookmarks is limited to 10. 

### How do I delete a location?

Select your new region and hit the clean button next to the regions title. Mind that this will clean *all other* regions, except the one currently displayed.

### Why does the app not update a location? It is in my bookmarks.

Tiny Weather Forecast Germany tries to be very conservative with your download volume. For this reason it only updates the currently displayed region. All other regions in the regions drop down are merely places once visited and do not get any updates.

*Note*: the currently selected region is also the region displayed in any widget added to the home screen.

If you want all regions from the dropdown menu to get updated, select *"Update everything"*.

### How do I set the region to be displayed at the home screen widget?

Just select it as current region in the app. The region displayed in the app and in the widget stay in sync all the time. Any region change in the app is reflected in the widget(s) automatically.

### How can I fetch weather for a different location if I go on travel?

If you want to update a different location e.g. before travelling, select it first and update the data. (If `Always update` is not set you may do this manually.) You can then revert to your standard location. Tiny Weather Forecast Germany will remove data only once it got invalid over time, but keep all fetched data in it's internal cache.

If you want all regions from the drop down menu to get updated, select *"Update everything"*.

### Why do some coloured polygons show up next to the coasts sometimes?

Those stem from coastal or sea area warnings issued by DWD in their shipping forecasts. The warning areas correspond to the coastal and sea areas in those reports. The definitions of the sea areas can be found in the document "Sturmwarnungen und Seewetterberichte für die Sport- und Küstenschifffahrt" published regularly by DWD or "Wetter- und Warnfunk" updated yearly by the Bundesamt für Seeschifffahrt und Hydrographie (BSH).

*Note* there is a coastal area that stretches along the Elbe from Cuxhaven to Hamburg.

### Which weather model is the app using?

The app uses the *MOSMIX* model provided by Deutscher Wetterdienst (DWD). "The MOSMIX System of Deutscher Wetterdienst optimizes and interprets the computations of the numerical models ICON and IFS (ECMWF) and combines them to statistically optimal weather forecasts." (DWD). This model provides data for almost all common meteorological parameters and uses statistical methods to derive parameters not available in the numerical models. More details can be found at [the MOSMIX page at Deutscher Wetterdienst (DWD)](https://www.dwd.de/EN/research/weatherforecasting/met_applications/nwp_applications/mosmix_application_node.html).

Additionally, Tiny Weather Forecast Germany also includes some of the *DMO* forecast locations available. *DMO* (Direct Model Output) locations provide forecasts based on the numeric ICON13 and ICON6-NEST weather forecast models.

### Can I access textual/specialized weather reports?

Tiny Weather Forecast Germany offers an extensive list of textual weather reports as provided by DWD. Those include short and medium term synoptic reports as well as specialized reports for the coastal regions of Germany, the sea weather bulletin for the North- and Baltic sea and the Mediterranean. Strong wind, gale and storm warnings are available for the German Bight, western and southern Baltic. *Note* Keep in mind that Tiny Weather Forecast Germany uses the data from DWD, so some textual reports are available in German only and the textual reports cover mainly Germany.

*Note* the textual sea and coastal weather reports coincide with those published by DWD via [RTTY radio broadcasts](https://www.dwd.de/EN/specialusers/shipping/broadcast_en/_node.html) but are drawn from the OpenData-server.

### What does wind direction "Beaufort" do? The wind speed still displays in another unit.

Setting the wind direction to Beaufort will display the wind as in a weather map with proper feathering according to the Beaufort scale, but it only affects the symbol used to display the wind direction. To set the unit used for wind speed to Beaufort use the appropriate setting in "Wind speed".

*Hint* setting the wind speed e.g. to knots and the display to `Beaufort` might be a good way to learn the Beaufort scale.

### What does the small arc next to the wind display refer to?

It shows the direction of the wind change during the next `Wind forecast period`. E.g. if the wind comes from the south and there is a quarter of an arc to the right next to it and the `forecast period` is set to 6h the wind will veer from S to E in the next 6 hours.

### When do I get a notification about a weather warning?

First of all, you need to enable this feature in the settings. Second, you need to specify how often the app will update the weather warnings (see below). Then, you will get notifications about weather warnings issued for the selected location. **The app will not check for warnings when the device is in doze mode**. You will also get no notifications when your device is offline. You may also miss some notifications when you restrict the app to use *un*metered networks only. So do not expect to get woken up in the middle of the night when a weather warning gets issued.

The notifications include weather warnings with a future onset once they get issued and weather warnings that are already ongoing.

You may get weather warnings of a similiar type, e.g. when gusts change into a storm.

Once you have been notified about a warning, you will also get notified about an update regarding this warning. Should the initial warning still be visible in the notification area, it will be canceled in favour of the update. A notification will be also canceled once the warning expires. Already expired weather warnings won't pop up at all.

To remind you about a persistent weather warning, the notification about this weather warning will be repeated after 12 hours.

To be kept aware about current warnings, simply don't swipe them away. They wil automatically get updated and disappear from your notifications drawer once they expire.

### How quickly do I get a weather warning?

In the settings, you can set up how often warnings are checked. The default is every 30 minutes. Please note that the interval specified may have quite an implact on battery drain and data volume use. As a rule of thumb, the warning data that gets downloaded may be about 300 Kb or even more in size when weather conditions are heavy.

To get an immediate and current weather warning status, go to the app and hit "update".

### How do I provide a crash log?

Please keep in mind: device logs may include and/or compromise sensitive information, consider reviewing the logs before sending.

If you pose yourself this question one can assume that you don't shy away from some more technical stuff. It is not enormously complex, though, so just read on, even though it requires you to install some developer tools. The idea is to provide a really detailed log of what Tiny Weather Forecast Germany actually did till the point where it died or did something unpredictable. All this is logged by your device, however, usually you can not see those logs.

The key to access those extensive logs is to hook up your device to your PC via USB and use a tool called Android Debug Bridge (`adb`) to access the system log.

The following steps need to be done *only once*:

1. Install `adb`. Most Linux distributions package it. If you use another operating system or it is not packaged in your distribution you can download it from [https://developer.android.com/studio/releases/**platform-tools**](https://developer.android.com/studio/releases/platform-tools) for various platforms as ZIP-files to unpack and use the usual means to install software there. You will also find a very extensive discussion of `adb` itself [here](https://developer.android.com/studio/command-line/adb). For the purposes at hand it is enough to just get it installed, though.
2. Enable the developer options on your device. Extensive instructions can also be found here: [https://developer.android.com/studio/**debug/dev-options**](https://developer.android.com/studio/debug/dev-options). The short form:
    1. Find the build number of your phone.

        - Open `Settings` (Searching for `Build number` might guide you directly to it)
        - Go to `System`
        - Go to `About phone`

    2. Tap the `Build number` several times.
    3. A dialogue informs you how many taps you are a way from being a developer
    4. Keep tapping on `Build number` until you see *You Are Now a Developer*
3. Enable USB debugging in the developer options of your device:
    1. Open `Settings`
    2. Search for `USB debugging`
    3. Make sure it is switched on

After this initial setup, you are ready to access the system log.

1. Connect your device to the PC via USB
2. Open a terminal/command line/shell and start `adb logcat`. This will display the devices **log** on the shell. Beware, this is a *lot of* scroll. You may want to redirect it to a file or use some shell extension that allows searches in the text output (like `screen` or `tmux`). To redirect `adb logcat > android.log` should do the trick. It will create a file `android.log` which is a plain text file that can be viewed in any text editor.
3. Watch out for stuff related to the app. A typical log start similar to this:

    ```logcat
    08-20 13:16:33.114  1529  3193 I ActivityManager: START u0 {act=android.intent.action.VIEW flg=0x1000c000 cmp=de.kaffeemitkoffein.tinyweatherforecastgermany/.TextForecastListActivity bnds=[290,534][430,692]} from uid 10477
    ```

    `de.kaffeemitkoffein.tinyweatherforecastgermany` is the apps key and signifies that the interesting parts will follow. You'll get the crash information further down. It starts like this:

    ```logcat
    08-20 13:16:33.244 11798 11798 D AndroidRuntime: Shutting down VM
    08-20 13:16:33.245 11798 11798 E AndroidRuntime: FATAL EXCEPTION: main
    08-20 13:16:33.245 11798 11798 E AndroidRuntime: Process: de.kaffeemitkoffein.tinyweatherforecastgermany, PID: 11798
    ```

    Read: fatal exception in `de.kaffeemitkoffein.tinyweatherforecastgermany` (PID will show another number.) So, Tiny Weather Forecast Germany died unexpectedly.

4. Copy all the blurb from the start mentioned in step 3. till you reach a line that holds

    ```logcat
    ActivityManager:   Force finishing activity de.kaffeemitkoffein.tinyweatherforecastgermany/.TextForecastListActivity
    ```

5. Log in to [`codeberg.org`](https://codeberg.org)
6. Navigate to the issue tracker [`https://codeberg.org/Starfish/TinyWeatherForecastGermany/issues`](https://codeberg.org/Starfish/TinyWeatherForecastGermany/issues)
7. Create an **issue** describing what you did and add the log just created (copy & paste will do).

## Contributing

Please leave comments, bug reports, issues and feature requests at the app repository at [codeberg.org](https://codeberg.org/Starfish/TinyWeatherForecastGermany):

<https://codeberg.org/Starfish/TinyWeatherForecastGermany>

Alternatively, for suggestions and bug reports, you can contact the maintainer by email: weather (at) kaffeemitkoffein.de

Get involved in *Tiny Weather Forecast Germany* and [help to translate it into more languages or complete existing translations](https://translate.codeberg.org/engage/tiny-weather-forecast-germany/).
