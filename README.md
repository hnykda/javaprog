ejjednodušeji lze spustit program takto:

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


