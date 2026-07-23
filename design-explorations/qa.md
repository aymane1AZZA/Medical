# Vérification des explorations

Tests réalisés le 22 juillet 2026 dans le navigateur intégré, sur les trois directions.

## Responsive

Largeurs vérifiées : 1440, 1024, 768 et 390 px, soit 12 combinaisons.

- Aucun débordement horizontal global détecté.
- Le tableau reste contenu dans sa zone; son défilement horizontal local est annoncé au clavier.
- Aucune chaîne visible sous 12 px.
- Les boutons, liens et champs de recherche visibles atteignent au moins 44 px de hauteur.
- Les cases de sélection disposent d'une cible `label` de 44 x 44 px.
- Les compositions A, B et C activent bien leurs classes, titres et structures respectifs.
- Le workflow devient un rail horizontal local sur mobile afin de conserver des libellés lisibles.

## Interactions et clavier

- Drawer : ouverture, backdrop, focus initial, fermeture et retour du focus validés.
- Modal sensible : focus initial, boucle de focus, fermeture, retour du focus et `Escape` implémentés.
- Confirmation : bouton désactivé tant que la vérification n'est pas cochée; état de chargement bloquant la double soumission; succès annoncé dans une région `aria-live`.
- Tableau : sélection de ligne, état visuel et compteur de sélection validés.
- Workflow : changement d'étape et mise à jour de `aria-current` validés.
- Observation : erreur en ligne, `aria-invalid`, focus sur le champ, loading, remise à zéro et message de succès validés.
- Comparateur : flèches gauche/droite et touches Home/End changent la direction et le focus.
- Console : aucune erreur ni avertissement lors du scénario testé.

## Contrastes clés

Ratios calculés selon WCAG pour les couples principaux :

| Direction | Texte/surface | Secondaire/canvas | Blanc/primaire | Navigation |
|---|---:|---:|---:|---:|
| A | 16,29:1 | 5,16:1 | 6,34:1 | 11,32:1 |
| B | 13,34:1 | 4,77:1 | 5,44:1 | 10,36:1 |
| C | 15,08:1 | 5,47:1 | 4,99:1 | 13,14:1 |

Ces couples dépassent 4,5:1. Les états cliniques associent couleur, libellé et/ou symbole; la couleur n'est jamais l'unique signal.

## Mouvement

Les transitions utiles restent entre 150 et 220 ms. La règle `prefers-reduced-motion: reduce` réduit transitions et animations à 0,01 ms, supprime le défilement animé et limite toute itération à une seule.

## Portée

Ce contrôle valide l'exploration statique et ses comportements. Il ne remplace pas un audit WCAG complet sur le futur produit connecté, ni une validation clinique ou une vérification des autorisations backend.
