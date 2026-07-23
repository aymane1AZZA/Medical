# SIH CHU - Authentification et RBAC

Base Full Stack séparée pour un système d'information hospitalier : backend Spring Boot 3 et frontend React/Vite.

## Fonctionnalités

- Connexion JWT sans inscription publique.
- Sélection obligatoire du rôle avant authentification.
- Vérification du rôle choisi contre le rôle stocké en base.
- BCrypt pour les mots de passe.
- RBAC Spring Security pour `ROLE_ADMIN`, `ROLE_MEDECIN`, `ROLE_INFERMIER`, `ROLE_BIOMEDICAL`, `ROLE_PATIENT`, `ROLE_LABO`.
- Création, modification et suppression des comptes via endpoints admin protégés.
- Frontend React avec routes privées, Axios interceptors, React Hook Form, Yup, Tailwind, Framer Motion et Chart.js.

## Démarrage backend

1. Lancer PostgreSQL :

```bash
docker compose up -d
```

2. Depuis `backend/`, lancer l'application :

```bash
mvn spring-boot:run
```

Le compte administrateur initial est créé au démarrage :

- Identifiant : `admin` ou `admin@chu.local`
- Mot de passe : `Admin@12345`
- Rôle à sélectionner : `Administrateur`

En production, remplacer `JWT_SECRET` et `ADMIN_PASSWORD`.

## Démarrage frontend

Depuis `frontend/` :

```bash
npm install
npm run dev
```

L'application est exposée sur `http://localhost:5173`.

## API principale

- `POST /api/auth/login`
- `GET /api/auth/health`
- `GET /api/admin/users`
- `POST /api/admin/users`
- `PATCH /api/admin/users/{id}`
- `DELETE /api/admin/users/{id}`
- `GET /api/{role}/dashboard`

Exemple de création de compte admin :

```json
{
  "username": "medecin1",
  "email": "medecin1@chu.local",
  "fullName": "Dr. Amine Saidi",
  "role": "ROLE_MEDECIN",
  "password": "Medecin123",
  "enabled": true
}
```
