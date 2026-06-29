# 🚀 TurboDrop

**TurboDrop** is a client-server optimized utility mod for **Minecraft 1.21.1 (NeoForge)** that lets you instantly empty containers or eject individual item slots directly onto the ground. Built with performance, security, and multiplayer compatibility in mind, it handles massive inventories and modded oversized stacks (>64) with ease.

Available in both **English** and **French** (fr_FR).

---

## ✨ Features

*   **🗑️ Eject Entire Container Contents:** Empty any chest, shulker box, or storage container instantly onto the ground with a simple keybind or mouse click.
*   **📦 Drop Hovered Slot (Oversized Stack Support):** Hover over any slot inside a GUI and drop its contents. Perfect for storage mods that allow stacks larger than 64 (it loops extraction to drop everything safely).
*   **⌨️ Dynamic Keybind Auto-Detection:** Automatically detects your player's Minecraft drop key mapping (AZERTY vs QWERTY handled native-out-of-the-box). It defaults to `ALT + [Your Drop Key]` for empty storage, and `CONTROL + [Your Drop Key]` for dropping slots.
*   **🛡️ Secure & Server-Checked:** Ejection logic is verified and handled on the server-side to prevent duplication exploits, while letting each client manage their own custom drop limits.
*   **⏳ Progressive Ejection (Anti-Lag):** Empties large containers progressively over tick updates (configurable maximum stacks per tick) to prevent server lag or packet overflow.
*   **💥 Realistic Ejection & Physics:**
    *   **In-World Ejection:** Items pop out from the top of the container with a gaussian random velocity spread (preventing items from clipping through blocks or falling through the floor).
    *   **GUI Ejection:** Items throw forward from the player's eye height in the look direction with a standard Minecraft pickup delay (10 ticks / 0.5s).
*   **🎨 Sharp Native UI:** Uses Minecraft's native `ConfirmScreen` to bypass graphics shader blurs, and gracefully returns you to the active chest container screen upon completion or cancellation.
*   **⚙️ In-Game Configuration Support:** Compatible with **Configured** for a localized, visual settings screen with simple ON/OFF switches.

---

## ⌨️ Default Controls

| Action | Control | Context |
|---|---|---|
| **Empty Storage** | `ALT + [Drop Key]` *(e.g. ALT+A / ALT+Q)* | In-Game (pointing at chest) or GUI |
| **Drop Hovered Slot** | `CTRL + [Drop Key]` | Inside GUI (hovering slot) |
| **Mouse Empty Storage** | `ALT + Left Click` *(configurable modifier)* | Inside GUI |

---

## ⚙️ Configuration Options

Modify these options directly in-game via the mods menu or in `config/turbodrop-common.toml`:
*   `maxStacksPerTick` (Default: 10): Limit how many stacks are spawned per tick to protect server performance.
*   `enableConfirmation` (Default: true): Require confirmation before emptying a storage container.
*   `enableMouseShortcut` (Default: true): Enable the click shortcut inside storage screens.
*   `mouseShortcutModifier` (Default: "ALT"): Modifier key required for mouse click (ALT, CONTROL, SHIFT, NONE).
*   `dropEntireSlot` (Default: true): Drop the entire slot contents (mode ALL). If set to `false`, uses the limits below.
*   `useVanillaMaxStack` (Default: false): Limit drops to the item's vanilla max stack size (e.g. 16 for ender pearls, 64 for cobble). Only active if `dropEntireSlot` is `false`.
*   `customDropLimit` (Default: 64): Maximum items dropped per slot action if both `dropEntireSlot` and `useVanillaMaxStack` are set to `false`.

---

## 🏗️ Technical Stack & License
*   **Loader:** NeoForge 21.1.65+
*   **Version:** Minecraft 1.21.1
*   **JDK:** Java 21
*   **Network:** `CustomPacketPayload` (Vanilla 1.21 Network Pipeline)
*   **License:** LGPLv3 (GNU Lesser General Public License version 3.0)

---

# 🇫🇷 Version Française

**TurboDrop** est un mod utilitaire client-serveur pour **Minecraft 1.21.1 (NeoForge)** permettant de vider instantanément des conteneurs ou d'éjecter des cases d'inventaire spécifiques directement au sol. Conçu pour le multijoueur, il gère les grands inventaires et les stacks moddés géants (>64) sans latence ni risque de duplication.

### ✨ Fonctionnalités :
*   **🗑️ Vider l'intégralité du stockage :** Visez un coffre ou ouvrez son interface, puis videz-le d'une seule touche.
*   **📦 Drop de case survolée :** Jetez le contenu d'une seule case d'un coup (compatible avec les stacks géants >64).
*   **⌨️ Raccourcis dynamiques :** Détecte automatiquement la touche de drop de votre jeu (Q sur QWERTY, A sur AZERTY) pour configurer les raccourcis.
*   **🛡️ Options individuelles en Multijoueur :** Chaque joueur gère ses propres réglages de drop depuis son menu de jeu local. Le serveur valide et éjecte de façon sécurisée.
*   **⏳ Éjection progressive (Anti-Lag) :** Distribue l'apparition des items sur plusieurs ticks (configurable) pour éviter les freezes serveurs.
*   **💥 Dispersion anti-glitch :**
    *   Les items apparaissent au-dessus du coffre ciblé avec une dispersion aléatoire pour éviter qu'ils ne traversent le sol (clipping).
    *   Depuis l'interface, les items sont projetés dans la direction du regard du joueur avec le délai de ramassage standard du jeu (10 ticks).
*   **🎨 Confirmation nette :** Utilise l'écran natif `ConfirmScreen` de Minecraft (pas de flou de shaders) et vous renvoie directement dans le coffre ouvert ensuite.
*   **⚙️ Menu d'options en jeu :** Entièrement traduit en français et configurable visuellement (ON/OFF) grâce au mod **Configured**.
