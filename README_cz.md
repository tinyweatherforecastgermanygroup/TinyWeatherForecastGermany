# Drobná předpověď počasí Německo

Předpověď počasí s widgety až na 10 dní na základě otevřených dat z Deutscher Wetterdienst (DWD).

## Snímky obrazovky

![Screenshot #1](fastlane/metadata/android/en-US/images/phoneScreenshots/1.png)
![Screenshot #2](fastlane/metadata/android/en-US/images/phoneScreenshots/2.png)
![Screenshot #3](fastlane/metadata/android/en-US/images/phoneScreenshots/3.png)

## Jak získat aplikaci

Aplikace Tiny Weather Forecast Germany je k dispozici v hlavním úložišti F-Droid. Stáhnout si ji můžete zde:

[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png" alt="Získat na F-Droid" height="80px">](https://f-droid.org/packages/de.kaffeemitkoffein.tinyweatherforecastgermany)

**Alternativní zdroje:**

Binární sestavení můžete získat zde: <https://kaffeemitkoffein.de/nextcloud/index.php/s/qqao88LzSQ4rTeg>

Nebo z vlastního repozitáře fdroid od vývojáře: <https://kaffeemitkoffein.de/fdroid>.

Uživatelská sestavení pro testování získáte zde: <https://kaffeemitkoffein.de/nextcloud/index.php/s/4SXHaLxLSmFd8Ri>.

Upozorňujeme, že zde odkazovaná sestavení nejsou podepsána klíčem fdroid, takže před jejich instalací musíte v podstatě odinstalovat verze fdroid a naopak.

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

## Kredity

Ikony Material Designu jsou chráněny autorskými právy (c) Google Inc. a jsou licencovány pod licencí Apache License Version 2.0.

Tato aplikace používá gradle a gradle wrapper, Copyright Gradle Inc, licencováno pod licencí Apache 2.0.

Zdroj dat: Zdroj dat: Deutscher Wetterdienst, přidány vlastní prvky, některá data.
zprůměrována podle jednotlivých hodnot. Výstrahy před počasím jsou zkrácené. Zdroj geografických dat: Zdroj geoinformací: Deutscher Wetterdienst.

Tento program používá třídu WeatherSpec z Gadgetbridge,
Copyright (C) 2016-2020 Andreas Shimokawa, Carsten Pfeiffer,
Daniele Gobbetti, pod licencí GNU AFFRERO GENERAL PUBLIC LICENSE,
verze 3, 19. listopadu 2007.

Tento program používá PNPOLY - Point Inclusion in Polygon Test by W. Randolph Franklin (WRF), Copyright (c) 1970-2003, Wm. Randolph Franklin.

Tento program používá třídu Astronomy, Copyright (C) 2010-2012, Helmut Lehmeyer, licencováno pod GNU General Public License verze 3.

Mapy PAT by Ian Macky <http://ian.macky.net/pat>.

Calculation of relative humidity from temperature and dew point is done using a simplified formula example from the DWD (Deutscher Wetterdienst), see <https://www.dwd.de/DE/leistungen/met_verfahren_mosmix/faq/faq_mosmix_node.html>.

Uses the Solarized Precision colors for machines and people theme, Copyright (c) 2011 Ethan Schoonover

Přispěvatelé:

* Andreas Shimokawa (opravy chyb a podpora pro Gadgetbridge API)
* Izzy (vylepšení metadat)
* Janis Bitta (nová ikona aplikace)
* Jonas Lochmann (.gitignore)
* Marc Nause (vylepšení kódu, ukládání bitmap do mezipaměti)
* eUgEntOptIc44 (francouzský překlad, opravy chyb, vylepšení javadocu, překlady tohoto souboru README.md)
* arwanger (README.md)
* maximilianovermeyer (oprava manipulace s geo-záměry)

Překlady:

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
* Ukrainian: eUgEntOptIc44, Andrij Mizyk

Zapojte se do *Tiny Weather Forecast Germany* a [pomozte ji přeložit do více jazyků nebo dokončit stávající překlady](https://translate.codeberg.org/engage/tiny-weather-forecast-germany/).

[<img src="https://translate.codeberg.org/widgets/tiny-weather-forecast-germany/-/multi-blue.svg">](https://translate.codeberg.org/engage/tiny-weather-forecast-germany/)

## Ochrana osobních údajů

Prohlášení o ochraně osobních údajů naleznete na adrese [zde](https://codeberg.org/Starfish/TinyWeatherForecastGermany/wiki/Home).

## Koncepce

Myšlenkou je vytvořit jednoduchou, nenáročnou a lehkou aplikaci pro předpověď počasí se zaměřením na widgety na domovské obrazovce, která využívá otevřená data z Deutscher Wetterdienst (DWD) a která nesleduje uživatele.

## Oprávnění

Aplikace používá následující oprávnění:

* Internet: potřebné k získání dat předpovědi z DWD.
* Přístup ke stavu sítě: dotaz na stav sítě před získáním dat.
* Služba na popředí: služba na popředí se používá ke spolehlivému získání dat předpovědi. Služba neběží neustále, ale pouze tehdy, když je třeba provést aktualizaci předpovědi počasí z DWD, a běží, dokud není dokončena.
* Přijímání dokončeného spuštění: aplikace potřebuje vědět o restartu, aby mohla znovu spustit pravidelné kontroly, zda je třeba provést aktualizaci, aktualizovat widgety a odesílat data do Gadgetbridge, pokud je to povoleno.
* Přístup k jemné poloze: slouží k určení nejbližších senzorů deep L k vaší poloze. Toto oprávnění nemusí být uděleno, pokud se tato funkce nepoužívá nebo pokud se vyhledávání provádí na základě ručně zadaných zeměpisných souřadnic.

## ČASTO KLADENÉ DOTAZY

*Pro místa v jiném časovém pásmu se ikony dne a noci zdají být nesprávné.*

Aplikace vždy zobrazuje datum a čas vašeho zařízení (a lokality). Příklad: Nacházíte se v Berlíně a vybrali jste čas pro Kubu. Aplikace zobrazí počasí na Kubě v odpovídajícím berlínském čase a použije ikony dne/noci odpovídající berlínskému času. Po cestě na Kubu a přepnutí zařízení na místní čas na Kubě bude aplikace zobrazovat počasí na Kubě podle kubánského času.

*Jak číst widget?*

Ikona widgetu, popis počasí a aktuální teplota odkazují na předpověď počasí, kterou lze očekávat do příští celé hodiny. Nejnižší a nejvyšší teploty odkazují na hodnoty, které lze očekávat od této chvíle do půlnoci.

Při zobrazení více dnů (tučný widget, velký widget) se minimální a maximální hodnoty a symbol počasí vztahují k celému dni.

*Co znamenají symboly v hlavní aplikaci?

Použité symboly:
<img alt="ikona pro srážky" title="ikona pro srážky" src="app/src/main/res/mipmap-mdpi/symbol_srážek.png" height="16" width="16"/> Srážky
<img alt="ikona pro mraky" title="ikona pro mraky" src="app/src/main/res/mipmap-mdpi/symbol_cloud.png" height="16" width="16"/> Mraky
<img alt="ikona pro bouřku" title="ikona pro bouřku" src="app/src/main/res/mipmap-mdpi/symbol_lightning.png" height="16" width="16"/> Bouřka
<img alt="ikona pro krupobití" title="ikona pro krupobití" src="app/src/main/res/mipmap-mdpi/symbol_ krupobití.png" height="16" width="16"/> Krupobití
<img alt="ikona pro mrznoucí déšť" title="ikona pro mrznoucí déšť" src="app/src/main/res/mipmap-mdpi/symbol_freezing_rain.png" height="16" width="16"/> mrznoucí déšť
<img alt="ikona pro mlhu" title="ikona pro mlhu" src="app/src/main/res/mipmap-mdpi/symbol_mlhy.png" height="16" width="16"/> mlha
<img alt="ikona pro drizzle" title="ikona pro drizzle" src="app/src/main/res/mipmap-mdpi/symbol_drizzle.png" height="16" width="16"/> Drizzle
<img alt="ikona pro směr větru" title="ikona pro směr větru" src="app/src/main/res/mipmap-mdpi/arrow.png" height="16" width="16"/> Směr větru

*Moje widgety se neaktualizují (pokud neotevřu aplikaci).*

Widgety se aktualizují každých 15-30 minut. Pokud se tak neděje vůbec nebo jen při otevření aplikace, pak máte pravděpodobně zařízení, které upřednostňuje výdrž baterie před správnou funkčností. Pravděpodobně nějaká takzvaná *funkce pro úsporu baterie* zabíjí součásti aplikace a přerušuje aktualizace. Podívejte se na [tuto stránku](https://dontkillmyapp.com/) a zjistěte, zda takové zařízení vlastníte a co s tím můžete dělat.

*Jak často aplikace aktualizuje předpověď počasí?

Deutscher Wetterdienst aktualizuje data předpovědi, která se používají, každých 6 hodin. Proto nemá smysl stahovat data o počasí z rozhraní DWD API častěji. Ruční aktualizace dat vyvolaná výběrem uživatele v hlavní aplikaci však vždy vynutí aktualizaci dat předpovědi. Data předpovědi pokrývají následujících deset dní. Je tedy docela dobře možné prezentovat předpověď počasí po určitou dobu bez dotazování na nová data.

*Jak často se aplikace GadgetBridge aktualizuje (když je tato funkce povolena)?*

Když je podpora GadgetBridge **povolena**, aplikace bude v nejlepším případě aktualizovat GadgetBridge každých 30 minut pomocí již existujících dat předpovědi, což znamená, že rozhraní DWD API nebude kvůli tomu voláno. Na zařízeních s API 23 nebo vyšším však k takovým aktualizacím nemusí docházet tak pravidelně, když zařízení přejde do režimu *doze*, ale měly by se spouštět v takzvaném "okně údržby", a těžko říci, co to skutečně znamená v časových mantinelech. V závislosti na zařízení a/nebo ROM to bude pravděpodobně znamenat velmi různé věci.

Pokud se setkáte s problémy s neaktualizací widgetu GadgetBridge, může pomoci umístění widgetu na domovskou obrazovku, protože widget se bude snažit aktualizovat také GadgetBridge pokaždé, když systém aktualizuje samotný widget.

*Proč se srážky zobrazují v kg/m² místo v cm?*

Protože DWD poskytuje tyto údaje tímto způsobem. To nelze změnit. Vezměte prosím v úvahu, že tato jednotka je přesnější, pokud jde o množství krup a sněhu.

*Proč aplikace nezobrazuje východ a západ slunce?*

Východ a západ slunce nelze spolehlivě vypočítat pomocí vzorců používaných pro zeměpisné šířky < -65° a > 65°. V takovém případě je tato funkce vypnutá.

*Proč aplikace zobrazuje určitou ikonu počasí (a ne jinou)?*

Ikony označují *významné povětrnostní podmínky*. Smyslem je zobrazit nejdůležitější (významné) povětrnostní podmínky, které můžete v daném časovém období očekávat. Například bouřky mají vyšší prioritu než déšť a déšť/mlha mají vyšší prioritu než oblačnost. Pokud je pravděpodobné, že takový významný stav počasí nastane, bude upřednostněn před ostatními.

Povětrnostní podmínky obvykle vypočítává DWD. Pokud není podmínka počasí v předpovědních datech k dispozici, aplikace se ji pokusí vypočítat z jiných položek. Pokud vás zajímá, jak tato aplikace v tomto případě počítá ikony, podívejte se do zdrojového kódu [zde](https://codeberg.org/Starfish/TinyWeatherForecastGermany/src/branch/master/app/src/main/java/de/kaffeemitkoffein/tinyweatherforecastgermany/WeatherCodeContract.java).

Prahové hodnoty pro významné povětrnostní podmínky jsou subjektivní a možná diskutabilní, ale povětrnostní podmínky vypočítané pomocí DWD mají přednost a vždy zůstávají nezměněny, pokud jsou k dispozici. Pokud vás zajímají priority, podívejte se na [tento dokument DWD](https://www.dwd.de/DE/leistungen/opendata/help/schluessel_datenformate/kml/mosmix_element_weather_xls.xlsx?__blob=publicationFile&v=6).

## Příspěvek

Zanechávejte prosím komentáře, hlášení chyb, problémy a požadavky na funkce v úložišti aplikací na [codeberg.org](https://codeberg.org/Starfish/TinyWeatherForecastGermany):

<https://codeberg.org/Starfish/TinyWeatherForecastGermany>

Případně se můžete obrátit na **Pawela Dubeho** (Starfish) s návrhy a hlášeními chyb e-mailem (pokud možno v angličtině): *weather (at) kaffeemitkoffein.de*
