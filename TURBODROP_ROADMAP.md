# TurboDrop - Fiche Directives & Architecture (NeoForge 1.21.1)

## 📌 Vision du Projet
TurboDrop permet de vider instantanément d'immenses stockages (Vanilla, Sophisticated Storage, Functional Storage, Tom's Storage, etc.) en éjectant les items au sol de manière ultra-rapide via des raccourcis clavier. Le mod doit être 100% compatible multijoueur, optimisé (zéro freeze) et impossible à utiliser pour dupliquer des items.

---

## 🛠️ Stack Technique & Contraintes
- **Version Minecraft :** 1.21.1
- **API :** NeoForge (Dernière structure 2026, pas d'ancien code Forge/LexForge).
- **Réseau :** Utilisation stricte de `CustomPacketPayload` (Nouveau système réseau de la 1.21.1).
- **Sécurité :** Logique d'extraction exécutée à 100% côté Serveur. Le client n'envoie qu'une intention d'action.
- **Performance :** Pas de boucle bloquante. Traitement progressif des grands conteneurs via une file d'attente serveur (`ServerTickEvent.Post`) avec limitation de stacks par tick.

---

## 💎 Fonctionnalités et Comportements Clés
1. **Raccourcis Configurables avec Alignement Automatique :**
   - **Vider le stockage (Empty Storage) :** Par défaut `ALT + Touche de drop du joueur`.
   - **Drop la case survolée (Drop Hovered Slot) :** Par défaut `CONTROL + Touche de drop du joueur`.
   - Au premier lancement, le mod va lire directement la touche de drop configurée dans Minecraft (`options.txt`) et l'utilise comme raccourci par défaut (AZERTY vs QWERTY géré nativement).
2. **Support des Gros Stacks (> 64) par case :**
   - Les cases contenant de très grandes quantités d'items (via d'autres mods) peuvent être vidées d'un coup.
   - **Loop Extraction Serveur :** Pour contourner la limite de 64 par appel imposée par l'API `IItemHandler`, le serveur effectue une boucle continue d'extraction jusqu'à ce que la case soit vide.
   - Configuration du comportement de drop de case : `ALL` (vide tout d'un coup), `MAX_64` (limite à 64 d'un coup), ou `CUSTOM_LIMIT` (limite à une valeur TOML personnalisée).
3. **Menu de Confirmation Natif & Confort de Jeu :**
   - Utilisation de l'écran natif `ConfirmScreen` de Minecraft pour éviter le flou de texture.
   - Renvoyé vers l'interface du coffre en cours d'utilisation lors de l'annulation ou de la validation.
4. **Intégration du Mod Configured :**
   - Localisation complète des options du mod avec des clés de traduction (`.translation(...)`) pour afficher une interface propre et lisible en jeu.

---

## 🏗️ Architecture des Dossiers

```text
fr.turbodrop.mod
├── TurboDrop.java                 // Classe principale (Config COMMON)
├── client
│   ├── KeyBindings.java           // Enregistrement des KeyMapping configurables basés sur options.txt
│   └── KeyInputHandler.java       // Interception, ConfirmScreen et reflection hoveredSlot
├── config
│   └── TurboDropConfig.java       // Configuration TOML avec clés de traduction
├── network
│   ├── ModPackets.java            // Enregistrement des payloads DropRequest et DropSlot
│   ├── DropRequestPayload.java    // Intent de vidage de conteneur
│   └── DropSlotPayload.java       // Intent de drop de case unique
└── server
    ├── DropManager.java           // Validation des règles métier, boucle d'extraction complète
    └── TaskQueue.java             // File d'attente de drop progressif anti-lag
```