# Recherche et preuves

## Vérification de ui-ux-pro-max

Skill lue intégralement :

`C:\Users\HP\Documents\Medical\.codex\skills\ui-ux-pro-max\SKILL.md`

Éléments vérifiés sur disque :

- `scripts/` est un dossier réel et lisible.
- `data/` est un dossier réel et lisible.
- `scripts/search.py` est présent et `python scripts/search.py --help` s'exécute correctement.

Commandes réellement exécutées depuis le dossier de la skill :

```powershell
python scripts/search.py "enterprise healthcare operations dashboard clinical precision structured calm data dense" --design-system
python scripts/search.py "human clinical operations longitudinal patient journey calm supportive hospital" --design-system
python scripts/search.py "advanced hospital command monitoring alerts incidents equipment real time cockpit" --design-system

python scripts/search.py "enterprise healthcare operations dashboard" --domain product
python scripts/search.py "clinical workflow patient safety" --domain ux
python scripts/search.py "hospital information system data dense" --domain style
python scripts/search.py "patient longitudinal medical record timeline" --domain ux
python scripts/search.py "laboratory results abnormal values" --domain color
python scripts/search.py "treatment session monitoring dashboard" --domain chart
python scripts/search.py "medical scheduling workflow" --domain ux
python scripts/search.py "equipment monitoring maintenance incidents" --domain product
python scripts/search.py "enterprise data tables filters bulk actions" --domain web
python scripts/search.py "accessible clinical forms" --domain ux
python scripts/search.py "healthcare role based navigation" --domain web
python scripts/search.py "React enterprise dashboard" --stack react
python scripts/search.py "motion critical enterprise applications" --domain ux
python scripts/search.py "responsive hospital software" --domain web

python scripts/search.py "data dense accessible real time minimal" --domain style
python scripts/search.py "healthcare medical trust clinical" --domain color
python scripts/search.py "enterprise dashboard professional readable" --domain typography
python scripts/search.py "data table sorting filtering pagination bulk" --domain web
python scripts/search.py "forms validation labels error units" --domain ux
python scripts/search.py "real time trend threshold comparison" --domain chart
python scripts/search.py "navigation keyboard breadcrumbs sidebar" --domain ux
python scripts/search.py "loading feedback confirmation disabled" --domain ux
python scripts/search.py "accessibility focus contrast errors screen reader" --domain ux
python scripts/search.py "enterprise dashboard forms tables state performance accessibility" --stack react
python scripts/search.py "clinical dashboard table form dialog sidebar alert" --stack shadcn
python scripts/search.py "Inter Source Sans IBM Plex Lexend readable healthcare enterprise" --domain typography
```

## Résultats utiles et filtrage

Les familles utiles remontées sont Data-Dense, Real-Time Monitoring, Drill-Down, Swiss Modernism et Accessible & Ethical. Les recommandations d'interaction utiles couvrent le focus visible, les liens d'évitement, les libellés persistants, les erreurs en ligne, les régions live, la réduction du mouvement, les cibles tactiles et les confirmations renforcées.

Les suggestions Portfolio Grid, palette sombre dominante, Fira Code, narration au défilement, Cinzel/Josefin et esthétique wellness ont été rejetées : elles diminuent la lisibilité prolongée, la neutralité institutionnelle ou la vitesse d'exécution. La skill est utilisée comme base de recherche, puis filtrée par le contexte hospitalier.

## Références réelles

### NHS Service Manual et NICE Design System

Sources : [NHS Service Manual](https://service-manual.nhs.uk/) et [NICE Design System](https://design-system.nice.org.uk/).

Fait observé : ces systèmes documentent des composants réutilisables, des contenus explicites et des exigences d'accessibilité pour des services de santé. Intérêt : labels stables, langage direct, états d'erreur proches du champ et navigation prévisible. À ne pas copier : l'apparence d'un service public transactionnel simple, insuffisamment dense pour un cockpit actif. Déduction Apheris : conserver leur discipline d'accessibilité, avec une hiérarchie métier plus compacte.

### OpenMRS O3

Sources : [Patient chart](https://o3-docs.openmrs.org/en-US/docs/configure-o3/configure-the-patient-chart/), [architecture O3](https://openmrs.atlassian.net/wiki/spaces/docs/pages/172359738/What%2Bis%2BO3), [produit OpenMRS](https://openmrs.org/product/).

Fait observé : le dossier patient est un centre de contexte configurable, avec navigation par dashboards regroupés. Intérêt : identité persistante, contexte longitudinal et modules cliniques regroupés. À ne pas copier : l'hétérogénéité visuelle possible d'un écosystème extensible. Déduction Apheris : un bandeau patient immuable doit précéder les outils de séance.

### IBM Carbon Data Table

Source : [Carbon Data Table](https://carbondesignsystem.com/components/data-table/usage/?source=post_page---------------------------).

Fait observé : le composant prévoit tri, sélection, expansion, actions groupées et comportements clavier documentés. Intérêt : densité contrôlée et actions liées à une sélection explicite. À ne pas copier : le langage visuel Carbon tel quel. Déduction Apheris : reprendre le modèle comportemental et adapter tokens, statut clinique et traçabilité.

### Grafana Alerting

Sources : [Grafana Alerting](https://grafana.com/docs/grafana/latest/alerting/) et [alertes liées aux panneaux](https://grafana.com/docs/grafana/latest/alerting/alerting-rules/link-alert-rules-to-panels/?src=grafana-readme).

Fait observé : les alertes peuvent être reliées aux panneaux de données et rassemblées pour le triage. Intérêt : faire suivre chaque alerte d'un contexte et d'une action. À ne pas copier : le thème sombre, la densité d'observabilité technique et la multiplication de séries. Déduction Apheris : une alerte clinique doit nommer le seuil, l'échéance et l'action documentaire.

### Oracle Health EHR

Source : [Oracle Health EHR](https://www.oracle.com/health/clinical-suite/electronic-health-record/).

Fait observé : l'offre met en avant un organisateur en un coup d'œil, une chronologie patient et des workflows personnalisés. Intérêt : continuité entre plan de soins et action courante. À ne pas copier : les formulations commerciales ou une personnalisation qui masque les repères communs. Déduction Apheris : la direction B doit rendre le parcours visible sans transformer le cockpit en récit décoratif.

### Siemens Healthineers

Sources : [CentraBytes Sample Status](https://www.siemens-healthineers.com/en-us/laboratory-automation/centrabytes-workflow) et [Atellica Data Manager](https://www.siemens-healthineers.com/en-ae/diagnostics-it/atellica-diagnostics-it/atellica-data-manager).

Fait observé : les produits présentent des indices visuels de statut, des rafraîchissements et des filtres d'alerte, ainsi que des règles de signalement des résultats. Intérêt : distinguer résultat, anomalie, validation et blocage opérationnel. À ne pas copier : les écrans spécifiques à l'automate ou les codes couleur sans libellé. Déduction Apheris : les anomalies biologiques exigent valeur, unité, plage, symbole et action, jamais la couleur seule.

### Philips Central Patient Monitoring

Sources : [central monitoring](https://www.usa.philips.com/healthcare/patient-monitoring/enterprise-monitoring/central-patient-monitoring-systems), [early warning](https://www.usa.philips.com/healthcare/procedure/early-warning-scoring), [PIC iX](https://www.usa.philips.com/healthcare/article/a-new-era-in-centralized-patient-monitoring).

Fait observé : PIC iX centralise des données proches du temps réel, le triage d'alarmes et la communication d'équipe. Intérêt : persistance de l'état, priorisation et coordination. À ne pas copier : la densité de courbes d'un poste de réanimation lorsque la décision d'aphérèse n'en a pas besoin. Déduction Apheris : C réserve l'espace dominant aux alertes et étapes actives, avec peu de graphiques mais tous actionnables.
