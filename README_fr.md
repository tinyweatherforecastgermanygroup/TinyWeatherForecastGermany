# Tiny Weather Forecast Germany

Prévisions météorologiques avec widgets pour un maximum de 10 jours, basées sur les données ouvertes du Deutscher Wetterdienst (DWD).

## Captures d'écran

![Capture d'écran 1](fastlane/metadata/android/en-US/images/phoneScreenshots/1.png)
![Capture d'écran 2](fastlane/metadata/android/en-US/images/phoneScreenshots/2.png)
![Capture d'écran 3](fastlane/metadata/android/en-US/images/phoneScreenshots/3.png)

## Comment obtenir l'application

Tiny Weather Forecast Germany est disponible depuis le dépôt principal de F-Droid. Vous pouvez la télécharger ici:

[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png" alt="Obtenez-le sur store d'applis F-Droid" height="80px">](https://f-droid.org/packages/de.kaffeemitkoffein.tinyweatherforecastgermany)

**Sources alternatives:**

Vous pouvez obtenir les builds binaires ici: <https://kaffeemitkoffein.de/nextcloud/index.php/s/qqao88LzSQ4rTeg>

Ou à partir du propre repo fdroid du développeur: <https://kaffeemitkoffein.de/fdroid>

Vous obtenez des builds userdebug pour les tests ici: <https://kaffeemitkoffein.de/nextcloud/index.php/s/4SXHaLxLSmFd8Ri>

Veuillez noter que les builds liés ici ne sont pas signés par la clé F-Droid, donc vous devez fondamentalement désinstaller les versions fdroid avant de les installer et vice versa.

## Licence

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

## Crédits

Les icônes Material Design sont protégées par le droit d'auteur (c) de Google Inc, sous licence Apache License Version 2.0.

Cette application utilise gradle et le wrapper gradle, Copyright Gradle Inc, sous licence Apache 2.0.

Source des données :Deutscher Wetterdienst, éléments propres ajoutés, certaines données sont des moyennes de valeurs individuelles. Les alertes météo sont abrégées. Source des géo-données : Deutscher Wetterdienst.

Ce programme utilise la classe WeatherSpec de Gadgetbridge,
Copyright (C) 2016-2020 Andreas Shimokawa, Carsten Pfeiffer,
Daniele Gobbetti, sous licence GNU AFFRERO GENERAL PUBLIC LICENSE, Version 3, 19 novembre 2007.

Ce programme utilise PNPOLY - Point Inclusion in Polygon Test de W. Randolph Franklin (WRF), Copyright (c) 1970-2003, Wm. Randolph Franklin.

Ce programme utilise la classe d'astronomie, Copyright (C) 2010-2012, Helmut Lehmeyer, sous licence GNU General Public License version 3.

Cartes PAT par Ian Macky <http://ian.macky.net/pat>.

Calculation of relative humidity from temperature and dew point is done using a simplified formula example from the DWD (Deutscher Wetterdienst), see <https://www.dwd.de/DE/leistungen/met_verfahren_mosmix/faq/faq_mosmix_node.html>.

Uses the Solarized Precision colors for machines and people theme, Copyright (c) 2011 Ethan Schoonover

Contributeurs :

* Andreas Shimokawa (corrections de bogues et prise en charge de l'API Gadgetbridge)
* Izzy (améliorations des métadonnées)
* Janis Bitta (nouvelle icône d'application)
* Jonas Lochmann (.gitignore)
* Marc Nause (améliorations du code, mise en cache des bitmaps)
* eUgEntOptIc44 (traduction française, corrections de bugs, améliorations javadoc, traductions de ce fichier README.md)
* arwanger (README.md)
* maximilianovermeyer (correction de la gestion de l'intention géographique)

Traductions :

* Tchèque : eUgEntOptIc44, mondstern
* Danois : eUgEntOptIc44, mondstern
* Néerlandais : Allan Nordhøy, alternative_be, eUgEntOptIc44, jwildeboer, mondstern
* Anglais : Allan Nordhøy
* Français : Allan Nordhøy, eUgEntOptIc44, J. Lavoie, Jean-Luc Tibaux, lejun, mondstern
* Finnois : eUgEntOptIc44, mondstern
* Allemand : Aircan, Allan Nordhøy, buhtz, eUgEntOptIc44, Hexagon, Hiajen, J. Lavoie, mondstern, silmaril, w4ts0n
* Hongrois : eUgEntOptIc44, Gőz Barnabás
* Indonésien : Linerly
* Italien : mondstern, eUgEntOptIc44
* Irlandais : mondstern
* Bokmål norvégien : Allan Nordhøy, eUgEntOptIc44, mondstern
* Polonais : eUgEntOptIc44, Eryk Michalak, ewm, mondstern
* Russe : Wirdi51
* Espagnol : eUgEntOptIc44, hegondev
* Ukrainien : eUgEntOptIc44, Andrij Mizyk

Participez à *Tiny Weather Forecast Germany* et [aidez à le traduire dans d'autres langues ou complétez les traductions existantes](https://translate.codeberg.org/engage/tiny-weather-forecast-germany/).

[<img src="https://translate.codeberg.org/widgets/tiny-weather-forecast-germany/-/multi-blue.svg">](https://translate.codeberg.org/engage/tiny-weather-forecast-germany/)

## Vie privée

Pour la déclaration de confidentialité, voir [ici](https://codeberg.org/Starfish/TinyWeatherForecastGermany/wiki/Home).

## Concept

L'idée est de créer une application de prévisions météorologiques simple, fluide et légère, axée sur les widgets de l'écran d'accueil, qui utilise les données ouvertes du Deutscher Wetterdienst (DWD) et ne suit pas les utilisateurs.

## Permissions

L'application utilise les permissions suivantes :

* Internet: nécessaire pour obtenir des données de prévision du DWD.
* Accès à l'état du réseau: interrogation de l'état du réseau avant l'obtention des données.
* Service d'avant-plan: un service d'avant-plan est utilisé pour obtenir les données de prévision de manière fiable. Le service ne fonctionne pas en permanence, mais seulement lorsqu'une mise à jour des prévisions météorologiques du DWD doit être effectuée, et il fonctionne jusqu'à ce qu'elle soit terminée.
* Recevoir le démarrage terminé: l'application a besoin de savoir qu'il y a eu un redémarrage pour relancer les vérifications périodiques si une mise à jour est due, pour mettre à jour les widgets et pour envoyer des données à Gadgetbridge lorsqu'il est activé.
* Accès à la localisation fine: utilisé pour déterminer les capteurs L profonds les plus proches de votre position. Cette permission ne doit pas être accordée si cette fonction n'est pas utilisée ou si la recherche est effectuée sur la base de coordonnées géographiques saisies manuellement.

## Questions et résponses

*Pour les lieux situés dans un fuseau horaire différent, les icônes jour/nuit semblent incorrectes.*

L'application affiche toujours la date et l'heure de votre appareil (et de votre région). Exemple : vous êtes à Berlin et vous avez sélectionné la météo pour Cuba. L'application vous montre la météo à Cuba à l'heure correspondante de Berlin et utilise les icônes jour/nuit correspondant à l'heure de Berlin. Une fois que vous aurez voyagé à Cuba et que votre appareil sera passé à l'heure locale de Cuba, l'application affichera le temps à Cuba à l'heure de Cuba.

*Comment lire le widget ?*

L'icône du widget, la description du temps et la température actuelle font référence aux prévisions météorologiques qui peuvent être attendues jusqu'à la prochaine heure pleine. Les températures basses et hautes font référence aux valeurs attendues entre maintenant et minuit.

Lorsque vous affichez plusieurs jours (widget en gras, grand widget), les valeurs min. et max. et le symbole météo se rapportent à la journée entière.

*Que signifient les symboles dans l'application principale?*

Icônes utilisées:
<img alt="icône pour les précipitations" title="icône pour les précipitations" src="app/src/main/res/mipmap-mdpi/symbol_precipitation.png" height="16" width="16"/> Précipitations
<img alt="icône pour les nuages" title="icône pour les nuages" src="app/src/main/res/mipmap-mdpi/symbol_cloud.png" height="16" width="16"/> Nuages
<img alt="icône pour l'orage" title="icône pour l'orage" src="app/src/main/res/mipmap-mdpi/symbol_lightning.png" height="16" width="16"/> Orage
<img alt="icône pour Grêle" title="icône pour Grêle" src="app/src/main/res/mipmap-mdpi/symbol_hail.png" height="16" width="16"/> Grêle
<img alt="icône pour la pluie verglaçante" title="icône pour la pluie verglaçante" src="app/src/main/res/mipmap-mdpi/symbol_freezing_rain.png" height="16" width="16"/> Pluie verglaçante
<img alt="icône pour le brouillard" title="icône pour le brouillard" src="app/src/main/res/mipmap-mdpi/symbol_fog.png" height="16" width="16"/> Brouillard
<img alt="icône pour bruine" title="icône pour bruine" src="app/src/main/res/mipmap-mdpi/symbol_drizzle.png" height="16" width="16"/> Bruine
<img alt="icône pour la direction du vent" title="icône pour la direction du vent" src="app/src/main/res/mipmap-mdpi/arrow.png" height="16" width="16"/> Direction du vent

*Mes widgets ne sont pas mis à jour (sauf si j'ouvre l'application).*

Les widgets sont mis à jour toutes les 15 à 30 minutes. Si cela ne se produit pas du tout ou seulement lorsque vous ouvrez l'application, il est probable que votre appareil privilégie l'autonomie de la batterie plutôt que les fonctionnalités. Il est probable qu'une soi-disant *fonction d'économie de batterie* tue les composants de l'application et interrompt les mises à jour. Consultez [cette page](https://dontkillmyapp.com/) pour savoir si vous possédez un tel appareil et ce que vous pouvez faire.

*À quelle fréquence l'application met-elle à jour les prévisions météorologiques ?*

Le Deutscher Wetterdienst met à jour les données de prévision utilisées toutes les 6 heures. Par conséquent, il n'est pas utile de tirer des données météorologiques plus fréquemment que cela de l'API du DWD. Cependant, une mise à jour manuelle des données déclenchée par la sélection de l'utilisateur dans l'application principale force toujours une mise à jour des données prévisionnelles. Les données prévisionnelles couvrent les dix prochains jours. Il est donc tout à fait possible de présenter une prévision météorologique pendant un certain temps sans demander de nouvelles données.

*À quelle fréquence l'application GadgetBridge est-elle mise à jour (lorsque cette fonction est activée) ?*

Lorsque la prise en charge de GadgetBridge est **activée**, l'application, dans le meilleur des cas, met à jour GadgetBridge toutes les 30 minutes en utilisant les données de prévision déjà en place, ce qui signifie que l'API DWD ne sera pas appelée pour cela. Toutefois, sur les appareils dotés de l'API 23 ou d'une version plus récente, ces mises à jour ne se produiront peut-être pas aussi régulièrement lorsque l'appareil passe en *mode sommeil*, mais devraient être lancées dans la "fenêtre de maintenance", et il est difficile de dire ce que cela signifie réellement en termes de temps. Cela signifie probablement des choses très différentes selon l'appareil et/ou la ROM.

Si vous rencontrez des problèmes avec GadgetBridge qui ne se met pas à jour, placer le widget sur l'écran d'accueil peut aider, puisque le widget essaiera également de mettre à jour GadgetBridge chaque fois que le widget lui-même sera mis à jour par le système.

*Pourquoi les précipitations sont-elles affichées en kg/m² au lieu de cm ?*

Parce que le DWD fournit ces données de cette façon. Cela ne peut pas être modifié. Veuillez considérer que cette unité est plus précise en ce qui concerne la quantité de grêle et de neige.

*Pourquoi l'application n'affiche-t-elle pas le lever et le coucher du soleil ?*

Le lever et le coucher du soleil ne peuvent être calculés de manière fiable avec les formules utilisées pour les latitudes < -65° et > 65°. Lorsque c'est le cas, cette fonctionnalité est désactivée.

*Pourquoi l'application affiche-t-elle une icône météo particulière (et pas une autre) ?*

Les icônes font référence aux *conditions météorologiques significatives*. L'idée est de vous montrer la condition météorologique la plus importante (significative) à laquelle vous pouvez vous attendre au cours de la période donnée. Par exemple, les orages ont une priorité plus élevée que la pluie et la pluie/brouillard ont une priorité plus élevée que les nuages. Si une telle condition météorologique significative est susceptible de se produire, elle sera préférée aux autres.

En général, les conditions météorologiques sont calculées par le DWD. Si une condition météorologique n'est pas disponible dans les données de prévision, l'application tente de la calculer à partir d'autres éléments. Si vous souhaitez savoir comment l'application calcule les icônes dans ce cas, consultez le code source [ici](https://codeberg.org/Starfish/TinyWeatherForecastGermany/src/branch/master/app/src/main/java/de/kaffeemitkoffein/tinyweatherforecastgermany/WeatherCodeContract.java).

Les seuils pour les conditions météorologiques importantes sont subjectifs et peut-être discutables, mais les conditions météorologiques calculées par le DWD sont prioritaires et restent toujours non modifiées, si elles sont disponibles. Si vous êtes intéressé par les priorités, consultez [ce document du DWD](https://www.dwd.de/DE/leistungen/opendata/help/schluessel_datenformate/kml/mosmix_element_weather_xls.xlsx?__blob=publicationFile&v=6).

## Contribuer

Veuillez laisser des commentaires, des rapports de bogues, des problèmes et des demandes de fonctionnalités à l'adresse suivante du dépôt d'applications à [codeberg.org](https://codeberg.org/Starfish/TinyWeatherForecastGermany):

<https://codeberg.org/Starfish/TinyWeatherForecastGermany>

Sinon, pour les suggestions et les rapports de bogue, vous pouvez contacter **Pawel Dube** (Starfish) par courriel: *weather (at) kaffeemitkoffein.de*
