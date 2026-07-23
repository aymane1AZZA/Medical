# Apheris Clinical OS

Prototype React isolé de calibration visuelle. Il n'est importé par aucune route de l'application de production.

## Lancer

Depuis `frontend/clinical-os` :

```powershell
..\node_modules\.bin\vite.cmd --host 127.0.0.1 --port 4175
```

Ouvrir `http://127.0.0.1:4175/`.

## Connexion de démonstration

- Adresse : `salma.bennani@chu-ibnsina.ma`
- Code : `demo-clinique`

Le bouton d'ouverture accepte ces valeurs localement et n'appelle aucun backend.

## Écrans inclus

- authentification professionnelle ;
- cockpit infirmier de séance active ;
- dossier patient longitudinal ;
- résultats biologiques et prescription dans les onglets du dossier ;
- recherche clinique, drawer d'observation et confirmation sensible.

Fondations utilisées : shadcn/ui Radix Nova, Lucide, Geist Variable et Framer Motion. Aucun composant décoratif de registre tiers n'a été ajouté.
