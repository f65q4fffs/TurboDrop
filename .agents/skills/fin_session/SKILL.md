---
name: fin-session
description: Déclenché à la fin d'une session de pair-programming pour commiter, pusher sur GitHub, mettre à jour agentia.md et générer un résumé de session copiable.
---

# Fin de session — Commit, Push et Résumé pour TurboDrop

Lorsque l'utilisateur demande une fin de session ou invoque cette directive, effectue les actions suivantes dans l'ordre :

## 1. Commit & Push Git
- Exécute `git status` and `git diff` pour inspecter les modifications.
- Indexe (`git add`) uniquement les fichiers modifiés du code source, de configuration, `agentia.md` et les dossiers de skills. Exclure les logs de build ou les JARs.
- Crée un commit avec un message structuré de la manière suivante :
  - **Titre court en français** décrivant le périmètre global de la session (ex: "implémentation des raccourcis et options").
  - **Sections par fonctionnalité** avec des listes à puces.
  - **Pour chaque fichier modifié :** expliquer brièvement ce qui a changé et pourquoi.
  - **Signature de co-auteur :** ajouter `Co-authored-by: [Nom de l'Agent] <[Email de l'Agent]>` en remplaçant par les informations réelles de l'assistant AI actif (ex: `Antigravity <antigravity@gemini.google>` pour l'agent Antigravity).
- Pousse le commit sur la branche principale distante via `git push origin main`.

## 2. Résumé de Session (Format Copiable)
Rédige un résumé complet de la session sous forme d'**un seul bloc de code brut (``` ```)**. Ce format permet à l'utilisateur de cliquer sur "Copier" directement pour le conserver.

Le bloc de code brut doit suivre cette structure exacte :

```text
Résumé de session — TurboDrop [Minecraft 1.21.1 / NeoForge]

FONCTIONNALITÉS IMPLÉMENTÉES
- [Nom de la fonctionnalité et détails]

FICHIERS MODIFIÉS OU CRÉÉS
- [Chemin du fichier] — [Nature du changement et rôle]

BUGS CORRIGÉS
- [Bug identifié] — [Correction appliquée]

COMMITS DISTANTS PUSHÉS
- [Hash court] — [Message de commit]

CE QUI RESTE À FAIRE
- [Tâches restantes, tests manuels à effectuer ou pistes futures]
```

## 3. Mise à jour de AGENTIA.md
- Ouvre le fichier `agentia.md` à la racine du projet.
- Met à jour la section **📋 Roadmap & État d'Avancement** :
  - Déplace les tâches terminées de la liste **🔄 À faire** vers la liste **✅ En place**.
