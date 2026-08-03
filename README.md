# Hitcolour

A **client-side Fabric mod** that replaces Minecraft's vanilla entity hit-flash — the red wash that flashes over a mob or player when they take damage — with a color and opacity of your choosing.

Customize it live in-game: press **H** (or use the **ModMenu** config button) to open a YACL settings screen.

## Features

- 🎨 **Custom color** picker for the hit overlay
- 🔍 **Opacity slider** (0% invisible → 100% fully opaque)
- ⚡ **Apply instantly** — no game restart, changes re-upload to the shader every tick
- ⌨ **H key** hotkey + ModMenu integration
- 📦 Ships for **5 Minecraft versions**

## Supported versions

| Minecraft | Mappings | Java |
|-----------|----------|------|
| 1.21.11   | yarn     | 21   |
| 26.1      | mojmap   | 25   |
| 26.1.1    | mojmap   | 25   |
| 26.1.2    | mojmap   | 25   |
| 26.2      | mojmap   | 25   |

Each version's source lives under `versions/` and builds independently with its own Gradle wrapper.

## Requirements

- Fabric Loader >= 0.16.0
- Java >= 21
- [YACL](https://modrinth.com/mod/yet-another-config-lib) (config UI)
- [ModMenu](https://modrinth.com/mod/modmenu) *(optional, for the config button)*

## Building

From a version folder (e.g. `versions/26.1.1`):

```bash
./gradlew build
```

The jar is produced in `build/libs/`. Drop it into your instance's `mods/` folder.

## How it works

The hurt flash lives in Minecraft's `OverlayTexture` atlas. `MixinOverlayTexture` rewrites the first 8 rows of that texture to your chosen color. Because the entity shader mixes the overlay in by alpha, the opacity slider is inverted internally: higher slider = lower alpha byte = stronger flash.

`MixinMinecraft` re-uploads the recolored texture every tick, so config changes apply live, and it opens the config screen when H is pressed.

## License

[MIT](LICENSE)

**Author:** mayank · **Company:** afkz studio