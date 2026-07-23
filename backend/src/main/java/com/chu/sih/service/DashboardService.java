package com.chu.sih.service;

import com.chu.sih.dto.DashboardResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    public DashboardResponse admin() {
        return response("Tableau de bord Administrateur", "ROLE_ADMIN", List.of(
                "Gérer tous les utilisateurs",
                "Attribuer les rôles",
                "Consulter les dossiers",
                "Gérer les services hospitaliers",
                "Consulter les statistiques",
                "Gérer les équipements",
                "Accéder aux journaux d'activité"
        ));
    }

    public DashboardResponse medecin() {
        return response("Tableau de bord Médecin", "ROLE_MEDECIN", List.of(
                "Consulter ses patients",
                "Accéder aux dossiers médicaux",
                "Créer des consultations",
                "Rédiger des prescriptions",
                "Demander des analyses",
                "Consulter les résultats de laboratoire",
                "Gérer son planning"
        ));
    }

    public DashboardResponse infirmier() {
        return response("Tableau de bord Infirmier", "ROLE_INFERMIER", List.of(
                "Consulter les patients du service",
                "Enregistrer les constantes vitales",
                "Assurer le suivi des soins",
                "Consulter les prescriptions",
                "Mettre à jour les observations"
        ));
    }

    public DashboardResponse biomedical() {
        return response("Tableau de bord Biomédical", "ROLE_BIOMEDICAL", List.of(
                "Gérer les équipements médicaux",
                "Suivre les maintenances",
                "Déclarer les pannes",
                "Gérer l'inventaire",
                "Consulter l'historique des interventions"
        ));
    }

    public DashboardResponse patient() {
        return response("Espace Patient", "ROLE_PATIENT", List.of(
                "Consulter son dossier médical",
                "Consulter ses prescriptions",
                "Consulter ses rendez-vous",
                "Consulter ses résultats d'analyses",
                "Télécharger ses documents médicaux"
        ));
    }

    public DashboardResponse labo() {
        return response("Tableau de bord Laboratoire", "ROLE_LABO", List.of(
                "Consulter les demandes d'analyses",
                "Enregistrer les prélèvements",
                "Saisir les résultats",
                "Valider les analyses",
                "Transmettre les résultats aux médecins"
        ));
    }

    private DashboardResponse response(String title, String role, List<String> permissions) {
        return DashboardResponse.builder()
                .title(title)
                .role(role)
                .permissions(permissions)
                .build();
    }
}
