Tiny Weather Forecast Germany
=================================

Previsioni del tempo con widget per un massimo di 10 giorni, basate su dati aperti del Deutscher Wetterdienst (DWD).

Screenshot
--------

![Screenshot #1](fastlane/metadata/android/en-US/images/phoneScreenshots/1.png)
![Screenshot #2](fastlane/metadata/android/en-US/images/phoneScreenshots/2.png)
![Screenshot #3](fastlane/metadata/android/en-US/images/phoneScreenshots/3.png)

Come ottenere l'app
------------------

Tiny Weather Forecast Germany è disponibile dal repository principale di F-Droid. Puoi scaricarla qui:

[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png"
alt="Get it on F-Droid"
height="80">](https://f-droid.org/packages/de.kaffeemitkoffein.tinyweatherforecastgermany)

**Fonti alternative:**

È possibile ottenere le build binarie qui: <https://kaffeemitkoffein.de/nextcloud/index.php/s/qqao88LzSQ4rTeg>

O dal repo fdroid dello sviluppatore: <https://kaffeemitkoffein.de/fdroid>.

Puoi ottenere le build di userdebug per i test qui: <https://kaffeemitkoffein.de/nextcloud/index.php/s/4SXHaLxLSmFd8Ri>.

Si prega di notare che le build linkate qui non sono firmate dalla chiave fdroid, quindi è necessario disinstallare le versioni fdroid prima di installarle e viceversa.

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


Referenze
-------

Le icone del Material Design sono Copyright (c) Google Inc. sotto la Licenza Apache versione 2.0.

Questa applicazione usa gradle e il wrapper gradle, Copyright Gradle Inc, con licenza Apache 2.0.

Fonte dei dati: Deutscher Wetterdienst, elementi propri aggiunti, alcuni dati mediati su valori individuali. Gli avvisi meteo sono abbreviati. Fonte dei geo-dati: Deutscher Wetterdienst.

Questo programma usa la classe WeatherSpec di Gadgetbridge,
Copyright (C) 2016-2020 Andreas Shimokawa, Carsten Pfeiffer,
Daniele Gobbetti, con licenza GNU AFFRERO GENERAL PUBLIC LICENSE,
Versione 3, 19 novembre 2007.

Questo programma usa PNPOLY - Point Inclusion in Polygon Test di W. Randolph Franklin (WRF), Copyright (c) 1970-2003, Wm. Randolph Franklin

Questo programma usa la classe Astronomy, Copyright (C) 2010-2012, Helmut Lehmeyer, con licenza GNU General Public License versione 3.

Mappe PAT di Ian Macky <http://ian.macky.net/pat>.

Calculation of relative humidity from temperature and dew point is done using a simplified formula example from the DWD (Deutscher Wetterdienst), see <https://www.dwd.de/DE/leistungen/met_verfahren_mosmix/faq/faq_mosmix_node.html>.

Uses the Solarized Precision colors for machines and people theme, Copyright (c) 2011 Ethan Schoonover

Collaboratori:

* Andreas Shimokawa (correzioni di bug e supporto per l'API Gadgetbridge)
* Izzy (miglioramenti ai metadati)
* Janis Bitta (nuova icona dell'app)
* Jonas Lochmann (.gitignore)
* Marc Nause (miglioramenti al codice, caching delle bitmap)
* Mondstern (traduzione olandese)
* Jean-Luc (traduzione francese, correzioni di bug, miglioramenti al javadoc, traduzioni di questo file README.md)
* arwanger (README.md)

Privacy
-------

Per la dichiarazione sulla privacy, vedere [qui](https://codeberg.org/Starfish/TinyWeatherForecastGermany/wiki/Home).

Concetto
-------

L'idea è quella di creare un'app per le previsioni del tempo semplice, fluida e leggera con un focus sui widget dello schermo domestico che utilizzi i dati aperti del Deutscher Wetterdienst (DWD) e che non tracci gli utenti.

Permessi
-----------

L'app utilizza i seguenti permessi:

* Internet: necessario per ottenere i dati di previsione dal DWD.
* Accesso allo stato della rete: per interrogare lo stato della rete prima di ottenere i dati.
* Servizio in primo piano: un servizio in primo piano è utilizzato per ottenere in modo affidabile i dati di previsione. Il servizio non viene eseguito tutto il tempo, ma solo quando è necessario un aggiornamento delle previsioni del tempo dal DWD, e viene eseguito finché non è finito.
* Ricevere l'avvio completato: l'app ha bisogno di sapere di un riavvio per riavviare i controlli periodici se un aggiornamento è dovuto, per aggiornare i widget e per inviare dati a Gadgetbridge quando abilitato.
* Accesso alla posizione fine: utilizzato per determinare i sensori deep L più vicini alla tua posizione. Questo permesso non deve essere concesso se questa funzione non viene utilizzata o se la ricerca viene eseguita sulla base di coordinate geografiche inserite manualmente.


FAQ
---

*Per i luoghi in un diverso fuso orario le icone giorno/notte sembrano errate.*

L'app mostra sempre la data e l'ora del tuo dispositivo (e della tua località). Esempio: sei a Berlino e hai selezionato il meteo per Cuba. L'applicazione ti mostra il meteo di Cuba al corrispondente orario di Berlino e utilizza le icone giorno/notte corrispondenti all'orario di Berlino. Una volta che hai viaggiato a Cuba e il tuo dispositivo è passato all'ora locale di Cuba, l'applicazione mostrerà il tempo a Cuba all'ora di Cuba.

*Come leggere il widget?*

L'icona del widget, la descrizione del tempo e la temperatura attuale si riferiscono alle previsioni del tempo che si possono prevedere fino alla prossima ora completa. Le temperature basse e alte si riferiscono ai valori che ci si può aspettare da ora a mezzanotte.

Quando si mostrano più giorni (widget in grassetto, widget grande), i valori minimi e massimi e il simbolo del tempo si riferiscono all'intera giornata.


*Cosa significano i simboli nell'applicazione principale?*

Icone utilizzate:
<img alt="icona per la precipitazione" title="icona per la precipitazione" src="app/src/main/res/mipmap-mdpi/symbol_precipitation.png" height="16" width="16"/> Precipitazione
<img alt="icona per le nuvole" title="icona per le nuvole" src="app/src/main/res/mipmap-mdpi/symbol_cloud.png" height="16" width="16"/> Nuvole
<img alt="icona del temporale" title="icona del temporale" src="app/src/main/res/mipmap-mdpi/symbol_lightning.png" height="16" width="16"/> Temporale
<img alt=" icona della Grandine" title="icona della Grandine" src="app/src/main/res/mipmap-mdpi/symbol_hail.png" height="16" width="16"/> Grandine
<img alt=" icona per la pioggia gelata" title="icona per la pioggia gelata" src="app/src/main/res/mipmap-mdpi/symbol_freezing_rain.png" height="16" width="16"/> pioggia gelata
<img alt="icona per la nebbia" title="icona per la nebbia" src="app/src/main/res/mipmap-mdpi/symbol_fog.png" height="16" width="16"/> nebbia
<img alt="icona per drizzle" title="icona per drizzle" src="app/src/main/res/mipmap-mdpi/symbol_drizzle.png" height="16" width="16"/> Drizzle
<img alt="icona per la direzione del vento" title="icona per la direzione del vento" src="app/src/main/res/mipmap-mdpi/arrow.png" height="16" width="16"/> Direzione del vento


*I miei widget non vengono aggiornati (a meno che non apra l'app).*

I widget vengono aggiornati ogni 15-30 minuti. Se questo non succede affatto o succede solo quando apri l'app, allora probabilmente hai un dispositivo che preferisce la durata della batteria alla corretta funzionalità. È probabile che qualche cosiddetta *funzione di risparmio della batteria* uccida i componenti dell'app e interrompa gli aggiornamenti. Vedi [questa pagina](https://dontkillmyapp.com/) per verificare se possiedi un tale dispositivo e cosa puoi fare.

*Quanto spesso l'app aggiorna le previsioni del tempo?*

Il Deutscher Wetterdienst aggiorna i dati di previsione utilizzati ogni 6 ore. Pertanto, non ha senso estrarre i dati meteo più frequentemente di così dall'API DWD. Tuttavia, un aggiornamento manuale dei dati innescato dalla selezione dell'utente nell'app principale forza sempre un aggiornamento dei dati di previsione. I dati di previsione coprono i prossimi dieci giorni. Quindi è abbastanza fattibile presentare una previsione del tempo per un po' di tempo senza interrogare nuovi dati.

*Quanto spesso viene aggiornata l'app GadgetBridge (quando questa funzione è abilitata)?*

Quando il supporto GadgetBridge è **abilitato**, l'app, nel migliore dei casi, aggiornerà GadgetBridge ogni 30 minuti utilizzando i dati delle previsioni che sono già presenti, il che significa che l'API DWD non sarà chiamata per questo. Tuttavia, sui dispositivi con API 23 o superiore, tali aggiornamenti potrebbero non verificarsi così regolarmente quando il dispositivo va in *modalità dormiente*, ma dovrebbero essere lanciati nella cosiddetta "finestra di manutenzione", ed è difficile dire cosa questo significhi realmente in termini di tempo. Questo probabilmente significherà cose molto diverse a seconda del dispositivo e/o della ROM.

Se avete problemi con GadgetBridge che non si aggiorna, mettere il widget nella schermata iniziale può aiutare, poiché il widget cercherà di aggiornare anche GadgetBridge ogni volta che il widget stesso viene aggiornato dal sistema.

*Perché le precipitazioni sono visualizzate in kg/m² invece che in cm?*

Perché il DWD fornisce questi dati in questo modo. Questo non può essere cambiato. Si prega di considerare che questa unità è più precisa per quanto riguarda la quantità di grandine e neve.

*Perché l'app non mostra l'alba e il tramonto?*

L'alba e il tramonto non possono essere calcolati in modo affidabile con le formule usate per latitudini < -65° e > 65°. Quando questo è il caso, questa funzione è disabilitata.

*Perché l'app mostra una particolare icona meteo (e non un'altra)?*

Le icone si riferiscono a *condizioni meteorologiche significative*. L'idea è quella di mostrarti la condizione meteorologica più importante (significativa) che puoi aspettarti che si verifichi in un determinato periodo di tempo. Per esempio, i temporali hanno una priorità maggiore della pioggia e la pioggia/nebbia hanno una priorità maggiore delle nuvole. Se è probabile che si verifichi una condizione meteorologica significativa, questa sarà preferita alle altre.

Di solito, le condizioni meteorologiche sono calcolate dal DWD. Se una condizione meteorologica non è disponibile nei dati di previsione, l'applicazione cerca di calcolarla da altri elementi. Se sei interessato a come questa app calcola le icone in questo caso, vedi [qui](https://codeberg.org/Starfish/TinyWeatherForecastGermany/src/branch/master/app/src/main/java/de/kaffeemitkoffein/tinyweatherforecastgermany/WeatherCodeContract.java) nel codice sorgente.

Le soglie per le condizioni meteo significative sono soggettive e forse discutibili, ma le condizioni meteo calcolate dal DWD hanno la priorità e rimangono sempre non modificate, se disponibili. Se sei interessato alle priorità, vedi [questo documento DWD tedesco](https://www.dwd.de/DE/leistungen/opendata/help/schluessel_datenformate/kml/mosmix_element_weather_xls.xlsx?__blob=publicationFile&v=6).

Contribuire
------------

Si prega di lasciare commenti, segnalazioni di bug, problemi e richieste di funzionalità (in inglese se possibile) al repository dell'app su codeberg.org:

<https://codeberg.org/Starfish/TinyWeatherForecastGermany>.

In alternativa, potete contattare **Pawel Dube** (Starfish) per suggerimenti e segnalazioni di bug via email (in inglese se possibile): *weather (at) kaffeemitkoffein.de*