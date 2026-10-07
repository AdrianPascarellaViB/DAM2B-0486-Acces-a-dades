# Problemes de la serialització i dels fitxers binaris

La serialització (`ObjectOutputStream` / `ObjectInputStream`) permet desar objectes Java sencers en
un fitxer i recuperar-los després. És còmoda, però té aquests problemes:

## 1. Dependència de la versió de la classe

El fitxer guarda l'estructura de la classe (atributs, tipus, ordre). Si després canvia la classe
`Videojoc` (per exemple, s'afegeix un atribut o se'n canvia el tipus), el `videojocs.dat` antic pot
deixar de ser llegible i salta `InvalidClassException`.

Si no es defineix `serialVersionUID`, Java en calcula un a partir de la classe i qualsevol canvi el
modifica. Per això `Videojoc` el declara (`private static final long serialVersionUID = 1L;`): així
els canvis compatibles (afegir un atribut, que es llegirà amb el valor per defecte) no trenquen els
fitxers antics. Els canvis incompatibles (canviar el tipus d'un atribut) segueixen fallant.

## 2. Classes no serialitzables

Tots els objectes que es desen han d'implementar `Serializable`, també els que hi ha dins dels
atributs. Si un atribut és d'una classe que no ho implementa, es llença
`NotSerializableException`. Solució: fer que aquella classe també sigui `Serializable` o marcar
l'atribut com a `transient` (no es desarà).

## 3. No és llegible ni fàcil de depurar

El fitxer és binari: no es pot obrir amb un editor de text per veure o corregir les dades. Això
dificulta recuperar part de la informació si alguna cosa falla, comparar dos fitxers o migrar les
dades a un altre sistema. Si cal llegibilitat, és millor JSON, XML o CSV.

## 4. No és portable a altres llenguatges

El format de serialització és propi de Java. Un `.dat` creat així no el pot llegir un programa en
Python o en un altre llenguatge, i fins i tot depèn que el programa Java tingui la mateixa classe.

## 5. Fitxers corruptes

Si el programa es tanca a mig escriure, el fitxer pot quedar incomplet i ja no es pot deserialitzar
(`EOFException`, `StreamCorruptedException`). Cal tancar sempre els fluxos correctament, per exemple
amb `try-with-resources`, com fa el programa. A més, el programa controla aquests errors en carregar
i comença amb el catàleg buit en lloc d'aturar-se.

Un altre problema típic: afegir objectes a un fitxer existent obrint un nou `ObjectOutputStream` en
mode *append* escriu una segona capçalera al mig del fitxer i en llegir-lo salta
`StreamCorruptedException`. Per això el programa desa la llista sencera (`ArrayList<Videojoc>`) de
cop cada vegada, sobreescrivint el fitxer.

## 6. Seguretat

Deserialitzar un fitxer d'origen desconegut és perillós: un fitxer manipulat pot contenir objectes
que, en reconstruir-se, executin codi (*Remote Code Execution*). No s'han de deserialitzar fitxers
en què no es confia. Des de Java 9 es poden filtrar les classes acceptades amb
`ObjectInputFilter`, i convé validar les dades després de llegir-les.

## 7. Mida del fitxer

Serialitzar llistes molt grans genera fitxers grans, perquè també es guarda informació de la
classe. Es pot comprimir (`GZIPOutputStream`) o desar només les dades essencials.

## 8. Cast sense comprovar

`readObject()` retorna un `Object` i cal fer un cast a `ArrayList<Videojoc>`. El compilador no ho
pot comprovar (d'aquí el `@SuppressWarnings("unchecked")`); si el fitxer conté una altra cosa, l'error
surt en temps d'execució (`ClassCastException`).
