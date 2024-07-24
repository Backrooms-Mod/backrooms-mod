# Backrooms Exploration
This is a Fabric 1.20.1 mod adding the Backrooms to Minecraft.

\[README incomplete\] 

## Features

This mod, by itself will add Levels 0-TBD of the Backrooms to Minecraft. These will likely primarily be based on the Wikidot (Tech Support) versions of these Levels, though this does not exclude the possibility of elements from elsewhere being included.

### Levels

In the latest [snapshot](https://github.com/Backrooms-Mod/backrooms-mod/releases/tag/WIP):
- Level 0
- Level 1

### Entities

There are Entities lurking around, so better watch out! They are also primarily based on the Wikidot.
In the latest [snapshot](https://github.com/Backrooms-Mod/backrooms-mod/releases/tag/WIP): 
- Hound

### Items

- Almond Water
- Fire Salt

###  Miscellaneous

- Wretched Cycle

\[TODO\]


### Addon system

This mod will have a system for addon mods to integrate with this mod and add new Levels, and possibly even alter or replace existing Levels. Anyone will be free to develop an addon for this mod, as long as they comply with the LGPL-v3.0 license terms of this mod.


## Install & Play

Since the mod is not ready for release yet, there is no Curseforge or Modrinth release. However, if you want to play the mod right now (with bugs and instability), you can download [Actions Artifacts](https://github.com/Backrooms-Mod/backrooms-mod/actions) or the latest [snapshot](https://github.com/Backrooms-Mod/backrooms-mod/releases/tag/WIP).

### Get the .jar

#### Artifacts

Artifacts are built by Github Actions, an automatic tool that tries to build the mod whenever there is a new commit (update in source). Often the builds are very unstable or unplayable and sometimes the build fails. However, it contains the newest features we are working on. 
To download an Artifact, you must be logged in with your Github account. Go to the [Actions tab](https://github.com/Backrooms-Mod/backrooms-mod/actions), click on a run and scroll down to the the "Artifacts" section. Download "Mod.zip" and extract it. There should be the *.jar file.

#### Snapshots

Snapshots are still work-in-progress builds, but they are considered as more stable than Artifacts. You can download the .jar in the [release tab](https://github.com/Backrooms-Mod/backrooms-mod/releases/tag/WIP):


### Install the mod

This is a Fabric 1.20.1 mod, so you need a Fabric 1.20.1 (Fabric 0.15.11) profile with a *mods* folder. Place the .jar file there. 

The Backrooms mod depends on some other mods and libraries. You must install them with the right version (compatible with 1.20.1):
- ClothConfig (11.1.118)
- Mod Menu (7.2.2)
- Satin API (1.14.0)
- Geckolib (4.4.7)

Other versions may work, but it is not guaranteed.

### Settings

You can change some settings. In Mod Menu, click on the Backrooms and open the configuration screen. Some of them have no effect yet.


## Building and Running

If you are a developer, you can build the mod on your own and run a debug build. Make sure you have JDK 17 or higher installed on your machine, since we use 1.20.1 and Gradle 8.8. Clone this Repository and open it in your preferred IDE. Follow the instructions on [the Fabric Wiki](https://fabricmc.net/wiki/tutorial:setup). You can skip "Mod Setup".


## License

This mod is licensed under the LGPL v3.0.
