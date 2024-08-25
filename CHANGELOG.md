# Changelog 

## Version 0.62.5
- fix app crash caused by launcher returning an inaccurately large widget size
- - disable features requiring location services after the user declined the location permission again in the permission rationale dialog
- removed all non-static calls to the sharedPreferences
- added DataPackage and DataStorage classes to replace shared preferences for values actively written by the WeatherSyncAdapter
- moved all values written by the WeatherSyncAdapter from the shared preferences to the new data storage to avoid random resets of the settings
- unified shared preference init in WeatherSettings
- fixed wrong context reference in WeatherSyncAdapter

## Version 0.62.4
- removed stray pixels in modular clouds icon
- added an option to display relative humidity in the overview chart
- added warnings filter to overview chart
- fixed main activity terminating twice under certain conditions
- added hint in settings when background location checks are enabled but the app is lacking the background location permission
- added background location check call to onResume
- a new background location is now also loaded in onResume
- fixed manually selected location from spinner did not override older last known location

## Version 0.62.3:
- add monochrome app icon to support app icon theming
- improved layout of the compact weather widget, fixing multiple bugs
- fixed classic widget having a maximum height limit

## Version 0.62.2:

- updated build tools

## Version 0.62.1:

- simplify code calling the WelcomeActivity and returning the launch mode to the main activity
- fix wind speed in overview chart displayed too high when temperature was below zero
- fixed possible crash related to the overview chart when data about the precipitation amount was missing
- added optional display of the precipitation amount to the overview chart
- switch the rain radar layers from WGS84 (EPSG 4326) to Mercator projection (EPSG 3857) to fit the target map, thus displaying the rain radar with a better local accuracy
- Gadgetbridge support: fix wrong moon-phase in daily forecast
- turning the Gadgetbridge support off/on now always triggers a Gadgetbridge update
- fix some preferences missing when saving the whole preference instance
- fix wind unit in overview chart km/h instead of kn
- moved the wind options for the overview chart from "wind" to "overview chart" in the settings
- bold widget: align the current day to the left to prevent line breaks in temperature display when the values have two digits on some devices
- switched weather warnings activity map to use a static OpenStreetMap in Mercator Projection
- rewritten plot functions to draw warning polygons and administrative borders on Mercator projection (EPSG 3857)
- map legend moved below the map to not overlap with the rain radar
- depending on the device width in pixels, a higher map resolution is used
- fix a crash related to unknown pollen region when pollen data was not used before
- new setting to force use of the higher resolution map despite lower device specs
- fix wrong dates shown in the weather maps activity when bitmaps were recycled from present data
- fix rain data loaded twice when rain radar data outdated
- fix a lag related to loading maps
- rewritten code to display the rain radar slides, now running smoother and more reliable
- fix crash when reusing warnings and rain radar data 
- add customizable location pin size on map
- stop rain radar when updating/processing warnings in onResume to avoid performance issues on older devices
- fix spinner not disappearing after update of warnings finishes

## Version 0.62.0:

- added a PendingIntent.FLAG_IMMUTABLE to all intents on devices with sdk>=23
- exact alarms changed to inexact when battery saving present to keep compliant with the sdk changes
- added the missing permission ACCESS_COARSE_LOCATION needed to properly check the location
- added the handling for the new permission POST_NOTIFICATION
- unified the permission requests to one call in onCreate
- fixed app icon being very small and items not centered in 1st welcome screen
- fixed some missing touch responses in the intro
- improved Gadgetbridge logging
- fixed WeatherSpec bug regarding air pressure
- improved app launch time by preventing multiple initializations of the stations list
- improved stability by introducing a lock to ensure that the area database is only built once
- added widget margins to avoid cropping of content in the rounded widget corners
- modified widget backgrounds to comply with new sdk recommendations
- the classic widget is now dynamic and shows the information of the former large widget if there is enough space
- the large widget got retired (merged with classic widget)
- added dynamic widget preview layouts and new widget descriptions
- modified the permitted widget sizes on api >=31 to improve the appearance
- widgets now better display on launchers allowing display rotation
- renamed the widgets to better reflect their information
- fixed date & time not updated correctly at various places inside the app when the locale changed while the app was running
- removed the data update logic based on AlarmManager and JobWorker using a foreground service for updates
- implemented new update logic based on sync adapter
- new necessary permissions to manage the sync adapter: AUTHENTICATE_ACCOUNTS, WRITE_SYNC_SETTINGS, READ_SYNC_SETTINGS
- syncs in the background won't waste battery when no suitable network is available or when the device is idle
- syncs won't occur when the user disables background syncs
- to better adapt to other apps and their data sync, the sync intervals are now inexact
- the user can now decide in detail which data should be synced in the background
- the settings for syncs are now unified in a new category called "sync settings"
- the user-triggered updates now only reload the data necessary for the view visible
- added dialog to ask if the favorites should be really deleted
- added button in welcome activity to optionally opt out from battery saving
- added setting to optionally opt out from battery saving
- the travel update now also does a force update for weather forecasts, weather warnings, texts, pollen count and maps
- add buttons to logging activity to scroll to the top & bottom of the text
- rewritten update logic for Gadgetbridge based on AlarmManager/JobWorker and widgets
- regular Gadgetbridge updates occur together with widget updates
- additionally, regular Gadgetbridge updates are now performed independently of data syncs, at least every 60 minutes, but not more frequent than every 30 minutes
- updates of widgets and Gadgetbridge from the main app are delayed, so that they do not slow down the user interface upon app launch
- added button in welcome activity to opt out from battery optimization
- added warnings in settings if sync is disabled, and/or battery optimization enabled and/or the data saver is enabled
- links and e-mail addresses are now clickable in the popups
- fixed sunrise, sunset, moonrise, moonset, moon position on sky sent to Gadgetbridge shifted by 24h 
- fixed wrong numbering of weather warning intervals in WelcomeActivity
- immediately request sync when location change detected in background, so that weather warnings are delivered immediately
- reduced padding in the compact weather widget to prevent line-breaks in current min. & max. temperature 