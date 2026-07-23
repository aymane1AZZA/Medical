# Apheris - explorations visuelles de phase 2

Cette zone compare trois directions artistiques sur le même cockpit infirmier d'aphérèse :

- A - Clinical Precision
- B - Human Clinical Operations
- C - Advanced Hospital Command

Elle est autonome : HTML, CSS et JavaScript statiques, sans dépendance au bundle, aux routes, à l'authentification ou au backend de production. Les données sont fictives et servent uniquement à comparer les interfaces.

## Lancer

Depuis la racine du dépôt :

```powershell
python -m http.server 4174 --bind 127.0.0.1
```

Puis ouvrir `http://127.0.0.1:4174/design-explorations/`.

Les fragments `#a`, `#b` et `#c` ouvrent directement une direction. Les contrôles du cockpit simulent les états interactifs demandés sans appel réseau.

## Contenu

- `index.html` : structure commune et données identiques aux trois directions.
- `styles.css` : tokens et compositions propres à A, B et C.
- `app.js` : comparaison, drawer, modal, états, validation et navigation clavier.
- `directions.md` : définition détaillée et matrice comparative.
- `research.md` : preuve ui-ux-pro-max et références réelles.
- `captures/` : captures desktop et mobile des trois directions.

Ce dossier ne constitue pas un design system définitif et ne contient volontairement aucun `design-system/MASTER.md`.
