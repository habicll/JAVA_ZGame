# zombien-game

A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).
Ce projet est un petit jeu développé en Java avec libGDX. Il inclut des launchers simples et une extension `ApplicationAdapter` qui affiche le logo libGDX.

## Installation

Clone le projet puis utilise le wrapper Gradle :

```bash
git clone <repo-url>
cd T-JAV-501-NAN_2
make
```

## Structure des modules / Platforms

- `core` : Main module with the application logic shared by all platforms.
- `lwjgl3` : Primary desktop platform using LWJGL3; was called 'desktop' in older docs.

## Gradle

Ce projet utilise [Gradle](https://gradle.org/) pour gérer les dépendances.
Le wrapper Gradle est inclus, donc tu peux lancer les tâches avec `gradlew.bat` ou `./gradlew`.

Tâches et options utiles :

- `--continue` : les erreurs n’arrêtent pas les tâches
- `--daemon` : utilise le daemon Gradle
- `--offline` : utilise les dépendances en cache
- `--refresh-dependencies` : force la validation des dépendances
- `build` : compile et assemble tous les modules
- `cleanEclipse` : supprime les données Eclipse
- `cleanIdea` : supprime les données IntelliJ
- `clean` : supprime les dossiers `build`
- `eclipse` : génère les données Eclipse
- `idea` : génère les données IntelliJ
- `lwjgl3:jar` : génère le jar exécutable dans `lwjgl3/build/libs`
- `lwjgl3:run` : lance le jeu sur desktop
- `test` : lance les tests unitaires
- `javadoc` : génère la documentation JavaDoc

La plupart des tâches peuvent être lancées avec le préfixe du module, par exemple :
`core:clean` supprime le dossier `build` du module `core` uniquement.

## Exécution du jeu

Pour jouer sur PC :

```bash
./gradlew lwjgl3:run
```
Le jar exécutable se trouve dans `lwjgl3/build/libs`.

## Dépendances principales

- [libGDX](https://libgdx.com/) : moteur de jeu Java
- [LWJGL3](https://www.lwjgl.org/) : backend desktop

## Liens utiles

- [Documentation libGDX](https://libgdx.com/wiki/)
- [Gradle](https://gradle.org/)
