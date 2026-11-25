# Botiga Online

Aplicació de consola en Java amb JDBC i MySQL: CRUD de productes, clients i descomptes, comandes amb transaccions i savepoints, i consultes amb JOIN.

## Importar la base de dades

Amb MySQL engegat (per exemple, des de XAMPP):

```
mysql -u root --default-character-set=utf8mb4 < sql/schema.sql
```

Si `botiga` ja existeix, l'script esborra les seves taules i les torna a crear.

## Configurar l'usuari

Per defecte es connecta a `localhost:3306/botiga` amb `root` i sense contrasenya, com XAMPP. Per canviar-ho, afegeix `-Dbotiga.url=...`, `-Dbotiga.usuari=...` o `-Dbotiga.contrasenya=...` en executar.

## Executar

Des de la carpeta `botiga-online` (a Linux o Mac, canvia `;` per `:`):

```
javac -encoding UTF-8 -d out -sourcepath src src/App.java
java -cp "out;lib/mysql-connector-j-8.4.0.jar" App
```

## Com funcionen els descomptes

- `%`: s'aplica sobre el preu de la línia.
- `€`: es resta per cada unitat comprada.
- S'apliquen després del savepoint. Si un descompte és incorrecte, es fa `rollback` fins al savepoint i la comanda es guarda sense descompte.
