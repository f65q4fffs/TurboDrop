# AGENTIA.md — TurboDrop (NeoForge 1.21.1)

Ce fichier est lu automatiquement par l'agent à chaque session.
Respecter toutes les règles définies ici avant toute modification du projet et tenir à jour l'état d'avancement.

---

## 📌 Vision du Projet
TurboDrop permet de vider instantanément d'immenses stockages (Vanilla, Sophisticated Storage, Functional Storage, Tom's Storage, etc.) en éjectant les items au sol de manière ultra-rapide via des raccourcis clavier. Le mod doit être 100% compatible multijoueur, optimisé (zéro freeze) et impossible à utiliser pour dupliquer des items.

---

## 🏗️ Stack Technique & Environnement
- **Version Minecraft :** 1.21.1
- **API :** NeoForge (javafml loader version `[4,)`, dependency version `[1.21.1,1.21.2)`)
- **JDK :** Java 21 (Adoptium portable)
- **Outil de Build :** Gradle 8.10.2
- **Réseau :** `CustomPacketPayload` avec payloads d'intention client sécurisés
- **Options :** Configuration NeoForge `COMMON` s'intégrant au mod **Configured** pour les réglages en jeu.

---

## 💎 Fonctionnalités et Comportements Clés
1. **Raccourcis Configurables avec Alignement Automatique :**
   - **Vider le stockage (Empty Storage) :** Par défaut `ALT + Touche de drop du joueur`.
   - **Drop la case survolée (Drop Hovered Slot) :** Par défaut `CONTROL + Touche de drop du joueur`.
   - Au premier lancement, le mod lit la touche de drop configurée dans Minecraft (`options.txt`) et l'utilise comme raccourci par défaut (AZERTY vs QWERTY géré nativement).
2. **Support des Gros Stacks (> 64) par case :**
   - **Loop Extraction Serveur :** Pour contourner la limite de 64 par appel imposée par l'API `IItemHandler`, le serveur effectue une boucle continue d'extraction jusqu'à ce que la case soit vide.
   - Configuration du comportement de drop de case : `ALL` (vide tout d'un coup), `MAX_64` (limite à 64 d'un coup), ou `CUSTOM_LIMIT` (limite à une valeur TOML personnalisée).
3. **Menu de Confirmation Natif & Confort de Jeu :**
   - Utilisation de l'écran natif `ConfirmScreen` de Minecraft pour éviter le flou de texture.
   - Renvoyé vers l'interface du coffre en cours d'utilisation lors de l'annulation ou de la validation.
4. **Intégration du Mod Configured :**
   - Localisation complète des options du mod avec des clés de traduction (`.translation(...)`) pour afficher une interface propre et lisible en jeu.

---

## 🏗️ Architecture du Projet

```text
fr.turbodrop.mod
├── TurboDrop.java                 // Classe principale (Config COMMON)
├── client
│   ├── KeyBindings.java           // Déclaration dynamique des touches de drop
│   └── KeyInputHandler.java       // Détection des touches, ConfirmScreen net et reflection
├── config
│   └── TurboDropConfig.java       // Paramètres TOML et clés de traduction
├── network
│   ├── ModPackets.java            // Enregistrement des payloads réseau
│   ├── DropRequestPayload.java    // Intent de vidage de conteneur
│   └── DropSlotPayload.java       // Intent de drop de case unique survolée
└── server
    ├── DropManager.java           // Validation des intentions (loops d'extraction complète)
    └── TaskQueue.java             // File d'attente de drop progressif anti-lag
```

---

## ⚙️ Règles de Développement (À respecter à la lettre)

### 1. Gestion des Touches (Raccourcis)
- Ne jamais coder de touche physique en dur (comme `GLFW_KEY_Q` ou `GLFW_KEY_A`) sans fallback dynamique.
- Récupérer dynamiquement la touche de drop de l'utilisateur (`mc.options.keyDrop`) sur le client lors de la première installation pour définir le raccourci par défaut.

### 2. Logique de Drop & Anti-glitch
- **Délai de ramassage :** Toujours utiliser le délai de ramassage de drop standard de Minecraft (`10` ticks) pour rester fidèle au jeu.
- **Sécurité et positionnement :**
  - Ne jamais faire apparaître d'ItemEntity à la même coordonnée exacte sans offset, sous peine de voir les items se crammer, traverser les murs/sols (clipping) et disparaître.
  - Spécialement pour le bloc de stockage ciblé, générer les entités à `pos.getY() + 1.1` (au-dessus du coffre) avec une dispersion aléatoire gaussienne standard.

### 3. Complétude d'extraction (Gros Stacks > 64)
- Puisque `IItemHandler.extractItem` est bridé à un stack size de 64 par appel par NeoForge, toujours utiliser des boucles d'extraction continue (`while`) côté serveur jusqu'à ce que le slot soit vide.

### 4. Configuration en jeu (User-Friendly)
- Toutes les variables de configuration doivent posséder une clé de traduction `.translation(...)` déclarée dans `en_us.json` et `fr_fr.json`.

---

## 📋 Roadmap & État d'Avancement

### ✅ En place
- [x] Initialisation complète du projet NeoForge 1.21.1
- [x] Paquets réseau robustes `DropRequestPayload` et `DropSlotPayload`
- [x] Écran de confirmation natif non flouté `ConfirmScreen` et retour propre au coffre après annulation/validation
- [x] Raccourcis clavier enregistrés et configurables dans le menu Contrôles
- [x] Alignement dynamique sur la touche drop du joueur à la première installation
- [x] Boucles d'extraction serveur complètes pour vider l'entièreté d'un slot moddé > 64
- [x] Dispersion et éjection réalistes au sol (sans perte ni clipping dans le vide)
- [x] Traduction complète de l'onglet de configuration Configured en jeu

### 🔄 À faire / Pistes futures
- [ ] Prise en charge du drop par glisser-déplacer (drag & drop) à l'extérieur des inventaires personnalisés
- [ ] Optimisations de performance réseau pour les très gros serveurs multijoueurs
- [ ] Prise en charge de configurations supplémentaires pour des mods de stockage spécifiques

---

## 🤖 Directives pour l'Agent Antigravity
1. **Lire `agentia.md`** au début de chaque session.
2. **Poser des questions à l'utilisateur** pour chaque étape impliquant un choix technique, ergonomique ou esthétique avant d'exécuter les modifications.
3. **Mettre à jour la Roadmap** (dans ce fichier) après chaque étape complétée ou modification demandée.
4. **Vérifier et compiler** le code à chaque étape avant de le copier vers le dossier de mods CurseForge.
5. **Fin de session :** Dès que l'utilisateur annonce une fin de session, lire et appliquer immédiatement les instructions du Skill `fin-session` (`.agents/skills/fin_session/SKILL.md`).

