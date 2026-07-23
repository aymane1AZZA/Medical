# Trois directions artistiques

Les trois directions utilisent le même contenu clinique, les mêmes actions et les mêmes aperçus multi-rôle. Elles diffèrent par composition, densité, navigation, hiérarchie et langage visuel.

## A - Clinical Precision

1. **Principe central** : rendre chaque donnée vérifiable et chaque action traçable.
2. **Personnalité** : institutionnelle, rigoureuse, calme, sans froideur technologique.
3. **Densité** : élevée mais régulière, avec grilles et alignements constants.
4. **Palette** : fond `#F4F7F6`, surface `#FFFFFF`, texte `#172321`, secondaire `#5A6966`, bordure `#CBD6D3`, primaire `#006B62`; information `#176B87`; succès `#1E7455`; attention `#9A6500`; danger `#B42318`; anomalie clinique `#A53A22`; incident `#8E2D44`; équipement indisponible `#5D6470`; séance active `#007C70`; attente `#7A5C00`. Chaque statut comporte aussi un libellé ou un symbole.
5. **Typographies comparées** : IBM Plex Sans, nette pour les données; Inter, très neutre mais commune; Atkinson Hyperlegible, excellente accessibilité mais moins compacte.
6. **Choix recommandé** : IBM Plex Sans, avec chiffres tabulaires pour les constantes.
7. **Espacement** : base 4 px; rythme 4/8/12/16/24/32.
8. **Rayons et bordures** : 4 à 5 px; bordure 1 px; séparation structurelle fréquente.
9. **Surfaces** : blanches ou gris clinique, peu d'ombres, sections délimitées.
10. **Sidebar et topbar** : sidebar métier 236 px; topbar compacte avec fil d'Ariane et recherche.
11. **Navigation** : domaines stables, état courant explicite, raccourcis sans masquer le libellé.
12. **Contexte patient** : bandeau horizontal persistant avec IPP, âge, groupe et indication.
13. **Tableaux** : lignes 44-48 px, en-têtes fixes, sélection et actions groupées.
14. **Formulaires** : labels au-dessus, unités adjacentes, aide et erreur en ligne.
15. **Cards** : uniquement pour unités fonctionnelles; pas de cartes statistiques décoratives.
16. **Badges** : compacts, texte + symbole; couleur secondaire.
17. **Alertes** : niveau, fait, seuil, échéance et action sur une même zone.
18. **Graphiques** : lignes simples, seuils nommés et décision associée.
19. **Timeline** : journal horodaté dense, auteur et statut visibles.
20. **Calendrier** : grille de planning avec ressources, statuts textuels et conflits.
21. **États interactifs** : contour net au hover, focus 3 px, pressé sombre, selected avec fond et marque latérale; disabled expliqué; loading conserve la largeur; success/warning/error textuels.
22. **Motion** : 160-200 ms pour couleur et déplacement court; aucune animation continue; reduced motion coupe les transitions.
23. **Avantages** : meilleure lisibilité clinique, cohérence multi-rôle, maintenance simple.
24. **Risques** : peut paraître austère et demander une excellente rédaction pour rester humaine.
25. **Rôles adaptés** : biologiste, laboratoire, spécialiste, administrateur et infirmier.
26. **Risque IA** : accumulation de panneaux blancs et teal générique. L'éviter par une densité métier réelle, des statuts précis et des composants non décoratifs.

## B - Human Clinical Operations

1. **Principe central** : relier l'action actuelle au parcours du patient et au travail de l'équipe.
2. **Personnalité** : attentive, posée, légèrement chaleureuse, toujours clinique.
3. **Densité** : moyenne à élevée, avec respiration autour des décisions et du patient.
4. **Palette** : fond `#F6F7F4`, surface `#FFFFFF`, surface douce `#EFF5F1`, texte `#1E2925`, secondaire `#61706A`, bordure `#D4DCD7`, primaire `#247064`; information `#316D8A`; succès `#34745B`; attention `#946400`; danger `#AC352A`; anomalie clinique `#A1432C`; incident `#933A4D`; équipement indisponible `#626973`; séance active `#287B6F`; attente `#846728`.
5. **Typographies comparées** : Source Sans 3, claire et chaleureuse; Lexend, très lisible mais large; Figtree, contemporaine mais moins institutionnelle.
6. **Choix recommandé** : Source Sans 3 pour le corps, Lexend uniquement pour les titres courts.
7. **Espacement** : base 4 px; rythme 4/8/12/16/24/36 avec plus d'air autour des étapes.
8. **Rayons et bordures** : 7-8 px; bordures douces 1 px; aucun effet capsule hors statut.
9. **Surfaces** : alternance de blanc et vert gris très pâle; ombre réservée au drawer/modal.
10. **Sidebar et topbar** : navigation métier horizontale à grande largeur; barre de contexte concise.
11. **Navigation** : orientation par parcours et étape, retour patient toujours visible.
12. **Contexte patient** : identité plus présente, indication en langage clair et continuité récente.
13. **Tableaux** : densité modérée, première colonne narrative, détails au drawer.
14. **Formulaires** : groupés par intention, microcopie factuelle, validation progressive.
15. **Cards** : épisodes, tâches ou personnes uniquement, avec hiérarchie interne forte.
16. **Badges** : libellés naturels et courts, jamais ambigus.
17. **Alertes** : formulation factuelle puis recommandation, sans ton alarmiste injustifié.
18. **Graphiques** : évolution annotée avec plages attendues et événements cliniques.
19. **Timeline** : axe longitudinal patient, épisodes regroupés par journée.
20. **Calendrier** : agenda par journée avec patient, préparation et dépendances.
21. **États interactifs** : hover de surface, focus 3 px, active tangible, selected par fond + coche; disabled avec raison; loading textuel; succès/warning/error avec phrase d'action.
22. **Motion** : 180-220 ms, drawer fluide mais court, aucune transition sur les valeurs vitales; reduced motion immédiat.
23. **Avantages** : baisse du stress, meilleure compréhension du parcours et du relais d'équipe.
24. **Risques** : respiration excessive sur les écrans de forte volumétrie; langage trop conversationnel.
25. **Rôles adaptés** : patient, généraliste, spécialiste, infirmier et planification.
26. **Risque IA** : tons sauge, grandes cartes et textes empathiques génériques. L'éviter par des verbes métier, des dossiers réels et une composition orientée tâche.

## C - Advanced Hospital Command

1. **Principe central** : concentrer monitoring, alertes, équipements et coordination en temps réel.
2. **Personnalité** : opérationnelle, technique, maîtrisée, rapide.
3. **Densité** : très élevée, organisée par priorité et fréquence d'action.
4. **Palette** : fond `#EEF2F2`, surface `#FFFFFF`, surface active `#E8F3F2`, texte `#111F21`, secondaire `#536569`, bordure `#BAC9CB`, primaire `#006D72`; information `#176D91`; succès `#16705A`; attention `#996000`; danger `#B02A24`; anomalie clinique `#B13A24`; incident `#942747`; équipement indisponible `#4F5964`; séance active `#007F7A`; attente `#725F12`.
5. **Typographies comparées** : Inter, compacte et robuste; IBM Plex Sans, plus expressive; Source Sans 3, plus humaine mais moins command-center.
6. **Choix recommandé** : Inter avec chiffres tabulaires et graisses 500/650.
7. **Espacement** : base 4 px; rythme 4/8/12/16/20/24.
8. **Rayons et bordures** : 2-3 px; bordures contrastées; angles francs sur zones critiques.
9. **Surfaces** : bandes et panneaux attachés à la grille, élévation uniquement pour overlays.
10. **Sidebar et topbar** : rail compact 82 px et barre de commande persistante.
11. **Navigation** : accès direct séance, incidents, équipement et files d'attente.
12. **Contexte patient** : ligne d'identité compacte mais verrouillée au-dessus du cockpit.
13. **Tableaux** : haute densité, colonnes épinglées, sélection et raccourcis d'action.
14. **Formulaires** : saisie rapide en panneau latéral, valeurs par défaut explicites, confirmation sensible séparée.
15. **Cards** : panneaux fonctionnels reliés, jamais flottants.
16. **Badges** : codes courts + libellés complets au focus/tooltip; compteurs réservés aux files.
17. **Alertes** : pile priorisée, acquittement, propriétaire, durée et escalade.
18. **Graphiques** : tendances courtes, seuils visibles, synchronisées avec le journal.
19. **Timeline** : flux d'événements temps réel avec filtres par source.
20. **Calendrier** : tableau d'occupation par box, dispositif et équipe.
21. **États interactifs** : hover très contrasté, focus 3 px, active immédiat, selected par barre + fond; disabled motivé; loading local; success/warning/error persistants jusqu'à lecture.
22. **Motion** : 150-180 ms; aucune animation de données; alerte nouvelle annoncée sans clignotement; reduced motion à zéro.
23. **Avantages** : vitesse, surveillance simultanée, excellente visibilité des incidents.
24. **Risques** : surcharge cognitive et courbe d'apprentissage pour les rôles occasionnels.
25. **Rôles adaptés** : infirmier, ingénieur biomédical, laboratoire et coordinateur/administrateur.
26. **Risque IA** : faux centre de commande sombre, néons et graphiques décoratifs. L'éviter par un thème clair, des données décisionnelles et zéro effet spectaculaire.

## Matrice de comparaison

Les notes évaluent l'adéquation au produit complet, pas la beauté isolée d'une capture. Pour le critère « risque d'apparence IA », 10 signifie risque faible.

| Critère /10 | A | B | C |
|---|---:|---:|---:|
| Lisibilité clinique | 9.5 | 9.0 | 8.5 |
| Vitesse d'exécution | 9.0 | 8.0 | 9.5 |
| Densité maîtrisée | 9.5 | 8.0 | 9.0 |
| Accessibilité | 9.0 | 9.5 | 8.0 |
| Sécurité visuelle | 9.5 | 9.0 | 9.0 |
| Originalité | 7.5 | 8.5 | 9.0 |
| Cohérence multi-rôle | 9.5 | 9.0 | 8.0 |
| Responsive | 9.0 | 9.5 | 8.0 |
| Maintenabilité | 9.5 | 9.0 | 8.0 |
| Faible risque d'apparence IA | 9.0 | 8.0 | 8.5 |
| **Moyenne** | **9.2** | **8.8** | **8.6** |

## Recommandation sans décision définitive

La recommandation de base est **A - Clinical Precision**, car elle obtient le meilleur équilibre entre sécurité visuelle, densité, accessibilité et cohérence pour huit rôles. Deux apports peuvent ensuite être testés sans fusionner arbitrairement les styles : la chronologie et le langage clair de B dans le dossier patient, puis la pile d'alertes et le rail d'actions de C dans le cockpit actif.

Le choix final reste ouvert : B est préférable si la continuité patient-soignant prime; C est préférable si le déploiement initial vise surtout la coordination de plusieurs séances et équipements actifs.
