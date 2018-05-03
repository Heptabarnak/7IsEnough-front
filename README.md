# 7isEnough

_7isEnough_ est une application de chasse aux trésors développée
dans le cadre du projet **SMART** à l'_INSA Lyon_.


> Le code source du back est disponible [ici](https://github.com/Heptabarnak/7IsEnough-back/)

## Auteurs

- Tom Bourret
- Julien Charles-Nicolas
- Marc-Antoine Fernandes
- Justin Gally
- Vincent Guillon
- Pierre Jaglin
- Loïc Sérazin


> Les différents livrables sont disponibles dans le dossier [docs](./docs)
de ce repo.

## Application Android

L'application android est disponible sur le Google Play :

<a href='https://play.google.com/store/apps/details?id=com.heptabargames.a7isenough'>
    <img alt='Disponible sur Google Play' width="200"
        src='https://play.google.com/intl/en_us/badges/images/generic/fr_badge_web_generic.png'/>
</a>

## Serveur

### Installation

Le serveur est un simple serveur web de fichier.
On conseille [Caddy Server](https://caddyserver.com) car il permet
d'avoir du [HTTPS par défaut](https://doesmysiteneedhttps.com/).

Prenez également le plugin **filemanager** pour simplifier le déploiement
de nouvelles versions des données.

La configuration de _Caddy_ est simple :

```
7isenough.app {
	root ./path/to/7isenough

	filemanager /filemanager ./path/to/7isenough {
		database 7isenough.db
	}

	internal ./path/to/7isenough/internal

	gzip
}
```

Ici, le dossier _internal_ permettra de stocker des informations sensibles.

Pour accéder aux données, allez sur https://7isenough.app/filemanager
et connectez vous avec `admin:admin`. Il ne reste plus qu'à créer les différents fichiers
nécessaires et de changer les mots de passes.

### Utilisation

#### Le manifeste

Le serveur doit avoir au minimum 1 fichier
`manifest.json`, qui contient l'ensemble des cartes disponibles (ainsi que le numéro de version).


Le manifeste est structuré comme ça :

```json
[
  {
    "id": "String",
    "name": "String",
    "description": "String",
    "startDate": "Number",
    "endDate": "Number",
    "version": "Number",
    "scoreboardId": "String"
  }
]
```

- **`id`**: Correspond à l'identifiant de la carte (nom du fichier `.json`).
- **`name`**: Nom de la carte.
- **`description`**: Description de la carte.
- **`startDate`**: Début de l'événement (en millisecondes UNIX).
- **`endDate`**: Fin de l'événement (en millisecondes UNIX).
- **`version`**: Numéro de version. Sans ça, les utilisateurs risquent de ne pas recevoir les mises à jours.
- **`scoreboardId`**: Id du scoreboard Google Play Games (Optionel).


#### Les cartes

Chaque événement déclaré dans le manifeste doit avoir son propre fichier `.json`.

Ces cartes sont générées grâce à deux interfaces:

##### L'interface web

Dans le dossier `mapGrid` de la partie _back_ du projet se trouve le fichier
[`testAPI3`](https://github.com/Heptabarnak/7IsEnough-back/blob/master/mapGrid/testAPI3.html)
qui permet à l'administrateur de créer et modifier les cartes une par une.

##### Le générateur de QR Code

Pour générer les QR Code on utilise Java et non plus JavaScript.
Pour lancer le logiciel, il suffit de lancer `./gradlew run` depuis
le repo **[7isEnough-back](https://github.com/Heptabarnak/7IsEnough-back/)**.

> Les 2 interfaces contiennent des indications sur la fabrication de A à Z d'une carte.


## Compiler Android

Pour compiler l'application Android, il suffit de clone le projet
et l'ouvrir avec Android Studio (3.1+).

Il faut ensuite créer le projet Google Cloud (pas détaillé ici).

Les propriétés suivantes sont à définir dans votre `$HOME/.gradle/gradle.properties` :

- `a7isEnough_GMaps_API_KEY` : L'api key concernant Google maps Android
- `a7isEnough_Google_APP_ID` : L'ID de l'application (voir la doc de Google Play)


## License

[GNU General Public License v3.0](./LICENSE)
