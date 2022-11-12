Kleine Wettervorschau Deutschland
=================================

Wettervorhersage mit Widgets für bis zu 10 Tage, basierend auf offenen Daten des Deutschen Wetterdiensts (DWD).

Bildschirmfotos
---------------

![Screenshot #1](fastlane/metadata/android/en-US/images/phoneScreenshots/1.png)
![Screenshot #2](fastlane/metadata/android/en-US/images/phoneScreenshots/2.png)
![Screenshot #3](fastlane/metadata/android/en-US/images/phoneScreenshots/3.png)

Download
--------

Die App ist im F-Droid-Hauptrepository verfügbar:

[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png" alt="Vom F-Droid app store herunterladen" height="80px">](https://f-droid.org/packages/de.kaffeemitkoffein.tinyweatherforecastgermany)

**Alternative Quellen:**

Sie können die Binär-Builds hier erhalten: <https://kaffeemitkoffein.de/nextcloud/index.php/s/qqao88LzSQ4rTeg>

Oder aus dem eigenen F-Droid-Repository des Entwicklers: <https://kaffeemitkoffein.de/fdroid>

Userdebug-Builds zum Testen erhalten Sie hier: <https://kaffeemitkoffein.de/nextcloud/index.php/s/4SXHaLxLSmFd8Ri>

Bitte beachten Sie, dass die hier verlinkten Builds nicht mit dem F-Droid-Schlüssel signiert sind, so dass Sie grundsätzlich die F-Droid-Versionen deinstallieren müssen, bevor Sie sie installieren und umgekehrt.

Lizenz
-------

Copyright (c) 2020, 2021 Pawel Dube

Dieses Programm ist freie Software: Sie können es weiterverteilen und/oder modifizieren
unter den Bedingungen der GNU General Public License, wie sie von der
Free Software Foundation veröffentlicht wird, entweder in Version 3 der Lizenz oder (nach
Ihrer Wahl) jede spätere Version.

Diese App wird in der Hoffnung verteilt, dass sie nützlich sein wird, aber
OHNE JEGLICHE GEWÄHRLEISTUNG; auch ohne die stillschweigende Gewährleistung der
MARKTGÄNGIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK. Siehe die GNU
General Public License für weitere Details.

Sie sollten ein Exemplar der GNU General Public License
zusammen mit dieser App erhalten haben. Falls nicht, finden sie sie hier: <http://www.gnu.org/licenses/>.

Quellenangaben
--------------

Die Material-Design-Symbole stehen unter dem Copyright (c) von Google Inc. und unter der Apache-Lizenz Version 2.0 lizenziert.

Diese App verwendet gradle und den gradle-Wrapper. Copyright: Gradle Inc, lizenziert unter der Apache 2.0 Lizenz.

Datenquelle: Deutscher Wetterdienst, eigene Elemente hinzugefügt, einige Daten über Einzelwerte gemittelt.
Wetterwarnungen werden abgekürzt. Quelle für Geodaten: Deutscher Wetterdienst.

Dieses Programm verwendet die WeatherSpec-Klasse von Gadgetbridge. Copyright (C): 2016-2020 Andreas Shimokawa, Carsten Pfeiffer, Daniele Gobbetti, lizenziert unter der GNU AFFRERO GENERAL PUBLIC LICENSE, Version 3, 19. November 2007.

Dieses Programm verwendet PNPOLY - Point Inclusion in Polygon Test von W. Randolph Franklin (WRF). Copyright: (c) 1970-2003, Wm. Randolph Franklin

Dieses Programm verwendet die Astronomie-Klasse. Copyright (C): 2010-2012, Helmut Lehmeyer, lizenziert unter der GNU General Public License Version 3.

PAT-Karten von Ian Macky <http://ian.macky.net/pat>.

Calculation of relative humidity from temperature and dew point is done using a simplified formula example from the DWD (Deutscher Wetterdienst), see <https://www.dwd.de/DE/leistungen/met_verfahren_mosmix/faq/faq_mosmix_node.html>.

Uses the Solarized Precision colors for machines and people theme, Copyright (c) 2011 Ethan Schoonover

Mitwirkende:

* Andreas Shimokawa (Bugfixes & Support für die Gadgetbridge API)
* Izzy (Metadaten-Verbesserungen)
* Janis Bitta (neues App-Icon)
* Jonas Lochmann (.gitignore-Datei)
* Marc Nause (Codeverbesserungen, Bitmap-Caching)
* Jean-Luc Tibaux (Französische Übersetzung, Bugfixes, Javadoc-Verbesserungen, Übersetzungen dieser README.md-Datei)
* arwanger (README.md)
* maximilianovermeyer (Fehlerbehebung beim Geo-Intent-Handling)

Übersetzungen:

* Tschechisch: eUgEntOptIc44, mondstern
* Dänisch: eUgEntOptIc44, mondstern
* Niederländisch: mondstern
* Englisch: Allan Nordhøy
* Französisch: eUgEntOptIc44, Mondstern, J. Lavoie
* Finnisch: Mondstern
* Deutsch: eUgEntOptIc44, Hiajen, J. Lavoie, mondstern, w4ts0n
* Ungarisch: eUgEntOptIc44, Gőz Barnabás
* Italienisch: mondstern, eUgEntOptIc44
* Norwegischer Bokmål: Allan Nordhøy, eUgEntOptIc44, mondstern
* Polnisch: mondstern
* Spanisch: eUgEntOptIc44
* Ukrainisch: eUgEntOptIc44, Andrij Mizyk

Beteiligen Sie sich gerne an *Tiny Weather Forecast Germany* und [helfen Sie mit, die App in weitere Sprachen zu übersetzen oder bestehende Übersetzungen zu vervollständigen](https://translate.codeberg.org/engage/tiny-weather-forecast-germany/).

[<img src="https://translate.codeberg.org/widgets/tiny-weather-forecast-germany/-/multi-blue.svg">](https://translate.codeberg.org/engage/tiny-weather-forecast-deutschland/)

Datenschutz
-----------

Für die Datenschutzerklärung siehe [hier](https://codeberg.org/Starfish/TinyWeatherForecastGermany/wiki/Home).

Konzept
-------

Die Idee ist es, eine einfache, schnelle und leichtgewichtige Wettervorhersage-App mit Fokus auf Home-Screen-Widgets zu erstellen, die offene Daten des Deutschen Wetterdienstes (DWD) nutzt und die Nutzer nicht trackt.

Berechtigungen
--------------

Die App verwendet die folgenden Berechtigungen:

* Internet: wird benötigt, um Vorhersagedaten vom DWD zu erhalten.
* Zugriff auf den Netzwerkstatus: fragt den Netzwerkstatus ab, bevor Daten abgerufen werden.
* Vordergrunddienst: ein Vordergrunddienst wird verwendet, um die Vorhersagedaten zuverlässig zu erhalten. Der Dienst läuft nicht die ganze Zeit, sondern nur dann, wenn eine Aktualisierung der Wettervorhersage vom DWD erforderlich ist, und er läuft so lange, bis diese beendet ist.
* Receive boot completed: die App muss von einem Neustart wissen, um periodisch zu prüfen, ob ein Update fällig ist, um Widgets zu aktualisieren und um Daten an Gadgetbridge zu senden, wenn dies aktiviert ist.
* Zugriff auf Feinortung: wird verwendet, um die nächstgelegenen Wetter-Sensoren zu Ihrer Position zu ermitteln. Diese Berechtigung muss nicht erteilt werden, wenn diese Funktion nicht verwendet wird oder wenn die Suche basierend auf manuell eingegebenen Geokoordinaten durchgeführt wird.

FAQ
---

*Für Standorte in einer anderen Zeitzone scheinen die Tag/Nacht-Symbole falsch zu sein.*

Die App zeigt immer das Datum und die Uhrzeit Ihres Geräts (und Ihrer Ortszone) an. Beispiel: Sie sind in Berlin und haben das Wetter für Kuba ausgewählt. Die App zeigt Ihnen das Wetter in Kuba zur entsprechenden Berliner Zeit an und verwendet die Tag/Nacht-Symbole entsprechend der Berliner Zeit. Sobald Sie nach Kuba gereist sind und Ihr Gerät auf die lokale Zeit in Kuba umgeschaltet hat, zeigt die App das Wetter in Kuba zur kubanischen Zeit an.

*Wie ist das Widget zu lesen?*

Das Widgetsymbol, die Wetterbeschreibung und die aktuelle Temperatur beziehen sich auf die Wettervorhersage, die bis zur nächsten vollen Stunde erwartet werden kann. Die niedrigen und hohen Temperaturen beziehen sich auf die Werte, die von jetzt bis Mitternacht zu erwarten sind.

Bei der Anzeige mehrerer Tage (fettes Widget, großes Widget) beziehen sich die Min- und Max-Werte und das Wettersymbol auf den ganzen Tag.

*Was bedeuten die Symbole in der Haupt-App?*

Verwendete Symbole:
<img alt="icon für Niederschlag" title="icon für Niederschlag" src="app/src/main/res/mipmap-mdpi/symbol_precipitation.png" height="16" width="16"/> Niederschlag
<img alt="icon für Wolken" title="icon für Wolken" src="app/src/main/res/mipmap-mdpi/symbol_cloud.png" height="16" width="16"/> Wolken
<img alt="icon für Gewitter" title="icon für Gewitter" src="app/src/main/res/mipmap-mdpi/symbol_lightning.png" height="16" width="16"/> Gewitter
<img alt="icon für Hagel" title="icon für Hagel" src="app/src/main/res/mipmap-mdpi/symbol_hail.png" height="16" width="16"/> Hagel
<img alt="icon für gefrierenden Regen" title="icon für gefrierenden Regen" src="app/src/main/res/mipmap-mdpi/symbol_freezing_rain.png" height="16" width="16"/> gefrierender Regen
<img alt="icon für Nebel" title="icon für Nebel" src="app/src/main/res/mipmap-mdpi/symbol_fog.png" height="16" width="16"/> Nebel
<img alt="icon für Nieselregen" title="icon für Nieselregen" src="app/src/main/res/mipmap-mdpi/symbol_drizzle.png" height="16" width="16"/> Nieselregen
<img alt="icon für Windrichtung" title="icon für Windrichtung" src="app/src/main/res/mipmap-mdpi/arrow.png" height="16" width="16"/> Windrichtung

*Meine Widgets werden nicht aktualisiert (außer ich öffne die App).*

Die Widgets werden alle 15-30 Minuten aktualisiert. Wenn dies überhaupt nicht oder nur beim Öffnen der App passiert, dann haben Sie wahrscheinlich ein Gerät, das die Batterielebensdauer über die richtige Funktionalität stellt. Wahrscheinlich schaltet eine sogenannte *Batteriesparfunktion* die Komponenten der App ab und unterbricht die Aktualisierungen. Sehen Sie auf [dieser Seite](https://dontkillmyapp.com/) nach, um zu prüfen, ob Sie ein solches Gerät besitzen und was Sie tun können.

*Wie oft aktualisiert die App die Wettervorhersage?*

Der Deutsche Wetterdienst aktualisiert die Vorhersagedaten, die verwendet werden, alle 6 Stunden. Daher ist es nicht sinnvoll, Wetterdaten häufiger als in diesem Rhythmus über die DWD-API zu laden. Eine manuelle Datenaktualisierung, ausgelöst durch die Auswahl des Benutzers in der Haupt-App, erzwingt jedoch immer eine Aktualisierung der Vorhersagedaten. Die Vorhersagedaten umfassen die nächsten zehn Tage. Es ist also durchaus machbar, eine Wettervorhersage für einige Zeit zu präsentieren, ohne neue Daten abzufragen.

*Wie oft wird die GadgetBridge App aktualisiert (wenn diese Funktion aktiviert ist)?

Wenn die GadgetBridge-Unterstützung **aktiviert** ist, wird die App im besten Fall alle 30 Minuten die GadgetBridge aktualisieren, indem sie bereits vorhandene Vorhersagedaten verwendet, was bedeutet, dass die DWD-API dafür nicht aufgerufen wird. Auf Geräten mit API 23 oder höher werden solche Aktualisierungen jedoch möglicherweise nicht so regelmäßig durchgeführt, wenn das Gerät in den *Doze-Modus* geht, sondern sollten im sogenannten "Wartungsfenster" gestartet werden, und es ist schwierig zu sagen, was dies wirklich in Bezug auf die Zeit bedeutet. Dies wird wahrscheinlich je nach Gerät und/oder ROM sehr unterschiedliche Dinge bedeuten.

Wenn Sie Probleme damit haben, dass sich GadgetBridge nicht aktualisiert, kann es helfen, das Widget auf dem Startbildschirm zu platzieren, da das Widget jedes Mal, wenn das Widget selbst vom System aktualisiert wird, versucht, auch GadgetBridge zu aktualisieren.

*Warum wird die Niederschlagsmenge in kg/m² statt in cm angezeigt?*

Weil der DWD diese Daten auf diese Weise bereitstellt. Dies kann nicht geändert werden. Bitte beachten Sie, dass diese Einheit genauer ist, was die Menge an Hagel und Schnee angeht.

*Warum zeigt die App keinen Sonnenaufgang und Sonnenuntergang an?*

Sonnenaufgang und Sonnenuntergang können mit den verwendeten Formeln für Breitengrade < -65° und > 65° nicht zuverlässig berechnet werden. Wenn dies der Fall ist, ist diese Funktion deaktiviert.

*Warum zeigt die App ein bestimmtes Wettersymbol an (und nicht ein anderes)?*

Die Icons verweisen auf *wichtige Wetterbedingungen*. Die Idee ist, Ihnen die wichtigste (signifikante) Wetterbedingung zu zeigen, die Sie innerhalb des gegebenen Zeitraums erwarten können. Zum Beispiel haben Gewitter eine höhere Priorität als Regen und Regen/Nebel eine höhere Priorität als Wolken. Wenn das Auftreten einer solchen signifikanten Wetterbedingung wahrscheinlich ist, wird sie gegenüber anderen bevorzugt.

In der Regel werden die Wetterbedingungen vom DWD berechnet. Wenn eine Wetterbedingung nicht in den Vorhersagedaten vorhanden ist, versucht die App, sie aus anderen Elementen zu berechnen. Wenn es Sie interessiert, wie die App in diesem Fall die Symbole berechnet, sehen Sie [hier](https://codeberg.org/Starfish/TinyWeatherForecastGermany/src/branch/master/app/src/main/java/de/kaffeemitkoffein/tinyweatherforecastgermany/WeatherCodeContract.java) im Quellcode nach.

Schwellenwerte für signifikante Wetterbedingungen sind subjektiv und vielleicht diskutabel, aber die vom DWD berechneten Wetterbedingungen haben Priorität und bleiben immer unverändert, wenn sie verfügbar sind. Wenn Sie sich für die Prioritäten interessieren, lesen Sie [dieses DWD-Dokument](https://www.dwd.de/DE/leistungen/opendata/help/schluessel_datenformate/kml/mosmix_element_weather_xls.xlsx?__blob=publicationFile&v=6).

Bei der Weiterentwicklung mithelfen
-----------------------------------

Bitte hinterlassen Sie Kommentare, Fehlerberichte, Probleme und Funktionswünsche im App-Repository auf codeberg.org:

<https://codeberg.org/Starfish/TinyWeatherForecastGermany>.

Alternativ können Sie **Pawel Dube** (Starfish) für Vorschläge und Fehlerberichte per E-Mail kontaktieren: *wetter (at) kaffeemitkoffein.de*
