Nejjednodušší způsob je zkompilovat zdrojové soubory do jar a pak lze spustit program takto:

`java -jar scja.jar -f http://rss.cnn.com/rss/edition.rss`

Jinak je možné vyvolat nápovědu pomocí `-h`:

`java -jar scja.jar -h`

Pokud běží databáze na localhost:27017, např. tímto příkazem:
`mongod --bind_ip localhost --port 27017  --smallfiles --dbpath . --nojournal`

lze data uložit do této databáze pomocí switche `-d`:

`java -jar scja.jar -f http://rss.cnn.com/rss/edition.rss -d`

ty lze pak vyvolat sekvencí: `use mydb`, `db.testCollection.findOne()`.

data lze jinak uložit do souboru pomocí switche `-o FILENAME`, případně specifikovat typ browseru switchem `-b`:

`java -jar scja.jar -f http://rss.cnn.com/rss/edition.rss -b firefox -o out.txt -d`


Info
-----

* Modul CLI se stará o parsování command line arguments (Apache Commons cli) a inicializuje vychozí webdriver použitý pro všechny následující úkoly
* Ten pak volá `Collector`, což je modul, který se kompletuje sběr dat a ukládá je případně do databáze, nebo souboru.
* `Collector` volá 3 potomky třídy `Scrapper` - to je abstraktní třída, která implementuje scrapper pro sběr informací o dané adrese URL z dostupného engine. Mezi ně patří:

    * načtení chtěných dat ze souboru ve formátu `JSON` ve tvaru `klíč : xpath`
    * posbírání těchto dat
    * abstraktní funkce: `check_availability` - zda jsou informace dostupné
    * uzavření driveru

zmiňované 3 potomci této třídy jsou `Alexa, Urlm, `Websout`. Prakticky stačí naimplementovat pouze jednu funkci `check_availability`.
* poslední třídou, která je volána z `Collector`, je `Feeder`. Ta získává informace o feedu samotném, jako je počet autorů, název, délka popisu a podobně. Kromě toho získává informace o jednotlivých článcích - hlavně jejich linky. Na ty je pak přistupováno a pomocí `boilerpipe` je vyextrahován text. O něm jsou pak zjišťovány nějaké metriky, jako například počet velkých písmen, délka, poměry speciiálních znaků atp. Tyto hodnoty jsou pak pro každý zdroj zprůměrovány a je spočítána také standardní odchylka.

Data jsou ukládána hlavně v různých HashMap wrapperech. Ty jsou pak ukládána do databáze `MongoDB`, nebo ve formátu `JSON` do souboru. 
