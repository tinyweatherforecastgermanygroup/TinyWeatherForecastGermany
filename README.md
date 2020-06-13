Tiny 24h Weather Forecast Germany
=================================

An android app with a widget that presents a 24 hours weather forecast based on open data from the Deutscher Wetterdienst (DWD).

How to get the app
------------------

This app is currently in a heavy alpha state, current builds are not available yet - but this will shortly change.

License
-------

 Copyright (c) 2020 Pawel Dube

 This program is free software: you can redistribute it and/or modify it
 under the terms of the GNU General Public License as published by the
 Free Software Foundation, either version 3 of the License, or (at
 your option) any later version.

 Tiny 24h Weather Forecast Germany is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Imagepipe. If not, see <http://www.gnu.org/licenses/>.

Credits
-------

 The Material Design icons are Copyright (c) by Google Inc., licensed 
 under the Apache License Version 2.0.
 
 This app uses gradle and the gradle wrapper, Copyright by Gradle Inc,
 licensed under the Apache 2.0 license.
 
 Weather data: Deutscher Wetterdienst, own elements added, some data 
 averaged over individual values.
 
 Privacy
 -------
 
 For the privacy statement, see [here](https://codeberg.org/Starfish/TinyWeatherForecastGermany/wiki/Home).

 Concept
 -------
 
 When installing [lineageos](https://lineageos.org/) on your device, you will perhaps miss the out of the box functionality of the cLock stock app to display a weather forecast. To do so, you need to install a third-party weather provider service. The first idea was to code a weather forecast provider that uses the open data from the Deutscher Wetterdienst (DWD).
 
 However, although some basic functionality could be established in a test version, the lineageos classes to integrate a [weather provider service](https://lineageos.github.io/android_lineage-sdk/reference/lineageos/weatherservice/WeatherProviderService.html) turned out to be quite inflexible (e.g. you need to provide a forecast for exactly 7 days to achieve full functionality, some additional data available form the DWD could not be displayed, etc.).
 
 Therefore, this idea was dropped in favour of a widget that could be placed comfortably on the home screen and adjusted to the needs of the user, displaying more detailed weather data that would have been possible using a weather provider service.
 
 Furthermore, this approach makes this app also available to anyone, not requiring lineageos to be installed on the device.
 
 Please also note that the DWD presents huge amounts of open weather data. The scope of this app is to poll a simple, 24 hours weather forecast from the DWD, and not more at the present time. 
 
 Contributing
 ------------

 Please leave comments, bug reports, issues and feature requests at
 the app repository at codeberg.org:
 
 https://codeberg.org/Starfish/TinyWeatherForecastGermany
 
 Alternatively, for suggestions and bug reports, you can contact me
 by email: pawel (at) kaffeemitkoffein.de 
