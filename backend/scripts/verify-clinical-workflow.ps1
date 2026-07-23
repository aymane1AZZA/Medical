param(
    [string]$BaseUrl = "http://127.0.0.1:8081",
    [string]$AdminUsername = "admin",
    [string]$AdminPassword = $env:ADMIN_PASSWORD
)

$ErrorActionPreference = "Stop"

if ([string]::IsNullOrWhiteSpace($AdminPassword)) {
    throw "ADMIN_PASSWORD doit etre defini."
}

function Invoke-ClinicalApi {
    param(
        [Parameter(Mandatory)] [string]$Method,
        [Parameter(Mandatory)] [string]$Path,
        [object]$Body,
        [string]$Token
    )

    $headers = @{}
    if ($Token) {
        $headers.Authorization = "Bearer $Token"
    }

    $parameters = @{
        Uri = "$BaseUrl$Path"
        Method = $Method
        Headers = $headers
        ContentType = "application/json"
    }
    if ($null -ne $Body) {
        $parameters.Body = $Body | ConvertTo-Json -Depth 12
    }

    try {
        Invoke-RestMethod @parameters
    } catch {
        $responseBody = $_.ErrorDetails.Message
        throw "$Method $Path a echoue: $responseBody"
    }
}

function Login([string]$Identifier, [string]$Password) {
    Invoke-ClinicalApi -Method POST -Path "/api/auth/login" -Body @{
        identifier = $Identifier
        password = $Password
    }
}

$suffix = [DateTimeOffset]::UtcNow.ToUnixTimeSeconds().ToString()
$clinicalPassword = "ClinicalDemo123"
$admin = Login $AdminUsername $AdminPassword
$adminToken = $admin.token

$specialist = Invoke-ClinicalApi -Method POST -Path "/api/admin/users" -Token $adminToken -Body @{
    username = "specialiste.$suffix"
    email = "specialiste.$suffix@apheris.local"
    fullName = "Specialiste Verification"
    role = "ROLE_MEDECIN_SPECIALISTE"
    password = $clinicalPassword
    enabled = $true
}
$biologist = Invoke-ClinicalApi -Method POST -Path "/api/admin/users" -Token $adminToken -Body @{
    username = "biologiste.$suffix"
    email = "biologiste.$suffix@apheris.local"
    fullName = "Biologiste Verification"
    role = "ROLE_MEDECIN_BIOLOGISTE"
    password = $clinicalPassword
    enabled = $true
}

$patient = Invoke-ClinicalApi -Method POST -Path "/api/clinical/patients" -Token $adminToken -Body @{
    medicalRecordNumber = "DM-$suffix"
    nationalIdentifier = "CIN-$suffix"
    familyName = "Parcours"
    givenName = "Test"
    birthDate = "1988-04-12"
    administrativeGender = "FEMALE"
    bloodGroup = "A+"
    phone = "+212600000000"
    email = "patient.$suffix@example.test"
    preferredLanguage = "fr"
}
$equipment = Invoke-ClinicalApi -Method POST -Path "/api/clinical/equipment" -Token $adminToken -Body @{
    assetNumber = "APH-$suffix"
    udi = "UDI-$suffix"
    manufacturer = "Verification"
    model = "Apheresis One"
    serialNumber = "SN-$suffix"
    equipmentType = "APHERESIS_MACHINE"
    commissionedOn = "2026-01-01"
    nextMaintenanceAt = [DateTimeOffset]::UtcNow.AddMonths(3).ToString("o")
    firmwareVersion = "1.0.0"
}

$specialistToken = (Login $specialist.username $clinicalPassword).token
$consent = Invoke-ClinicalApi -Method POST -Path "/api/clinical/consents" -Token $specialistToken -Body @{
    patientId = $patient.id
    consentType = "APHERESIS"
    scopeCode = "THERAPEUTIC_APHERESIS"
    validUntil = [DateTimeOffset]::UtcNow.AddYears(1).ToString("o")
}
$prescription = Invoke-ClinicalApi -Method POST -Path "/api/clinical/prescriptions" -Token $specialistToken -Body @{
    patientId = $patient.id
    indicationCode = "TPE-DEMO"
    indicationDisplay = "Echange plasmatique therapeutique"
    asfaCategory = "I"
    modality = "TPE"
    priority = "ROUTINE"
    sessionsPlanned = 1
    frequencyText = "Une seance"
    targetVolumeMl = 3000
    replacementFluid = "Albumine 5%"
    anticoagulant = "ACD-A"
    anticoagulantRatio = 12
    calciumProphylaxis = "Selon protocole"
    vascularAccessPlan = "Voie peripherique"
    clinicalInstructions = "Surveillance continue"
}
$prescription = Invoke-ClinicalApi -Method POST -Path "/api/clinical/prescriptions/$($prescription.id)/submit" -Token $specialistToken

$biologistToken = (Login $biologist.username $clinicalPassword).token
$prescription = Invoke-ClinicalApi -Method POST -Path "/api/clinical/prescriptions/$($prescription.id)/validate" -Token $biologistToken
$prescription = Invoke-ClinicalApi -Method POST -Path "/api/clinical/prescriptions/$($prescription.id)/activate" -Token $biologistToken

$startsAt = [DateTimeOffset]::UtcNow.AddHours(2)
$appointment = Invoke-ClinicalApi -Method POST -Path "/api/clinical/appointments" -Token $specialistToken -Body @{
    patientId = $patient.id
    prescriptionId = $prescription.id
    equipmentId = $equipment.id
    startsAt = $startsAt.ToString("o")
    endsAt = $startsAt.AddHours(3).ToString("o")
    reason = "Seance prescrite"
}
$session = Invoke-ClinicalApi -Method POST -Path "/api/clinical/sessions" -Token $specialistToken -Body @{
    patientId = $patient.id
    prescriptionId = $prescription.id
    appointmentId = $appointment.id
    equipmentId = $equipment.id
    sequenceNumber = 1
    plannedVolumeMl = 3000
    vascularAccess = "PERIPHERAL"
}

$checklist = Invoke-ClinicalApi -Method GET -Path "/api/clinical/sessions/$($session.id)/checklist" -Token $specialistToken
foreach ($item in $checklist) {
    Invoke-ClinicalApi -Method POST -Path "/api/clinical/sessions/$($session.id)/checklist/$($item.id)/complete?comment=verification" -Token $specialistToken | Out-Null
}
$session = Invoke-ClinicalApi -Method POST -Path "/api/clinical/sessions/$($session.id)/transition" -Token $specialistToken -Body @{ targetStatus = "READY" }
$session = Invoke-ClinicalApi -Method POST -Path "/api/clinical/sessions/$($session.id)/transition" -Token $specialistToken -Body @{ targetStatus = "IN_PROGRESS" }
$observation = Invoke-ClinicalApi -Method POST -Path "/api/clinical/sessions/$($session.id)/observations" -Token $specialistToken -Body @{
    observationCode = "8867-4"
    codeSystem = "LOINC"
    valueNumeric = 72
    unitUcum = "/min"
    source = "MANUAL"
    observedAt = [DateTimeOffset]::UtcNow.ToString("o")
}

$labOrder = Invoke-ClinicalApi -Method POST -Path "/api/clinical/laboratory/orders" -Token $specialistToken -Body @{
    patientId = $patient.id
    prescriptionId = $prescription.id
    priority = "ROUTINE"
    clinicalContext = "Controle de seance"
    items = @(@{ loincCode = "718-7"; display = "Hemoglobine"; specimenType = "BLOOD" })
}
$labItems = Invoke-ClinicalApi -Method GET -Path "/api/clinical/laboratory/orders/$($labOrder.id)/items" -Token $biologistToken
$labResult = Invoke-ClinicalApi -Method POST -Path "/api/clinical/laboratory/results" -Token $biologistToken -Body @{
    orderItemId = $labItems[0].id
    loincCode = "718-7"
    valueNumeric = 12.8
    unitUcum = "g/dL"
    referenceLow = 12
    referenceHigh = 16
    interpretation = "N"
    critical = $false
    measuredAt = [DateTimeOffset]::UtcNow.ToString("o")
}
$labResult = Invoke-ClinicalApi -Method POST -Path "/api/clinical/laboratory/results/$($labResult.id)/validate" -Token $biologistToken

$incident = Invoke-ClinicalApi -Method POST -Path "/api/clinical/incidents" -Token $specialistToken -Body @{
    patientId = $patient.id
    sessionId = $session.id
    equipmentId = $equipment.id
    category = "CLINICAL"
    severity = "LOW"
    occurredAt = [DateTimeOffset]::UtcNow.ToString("o")
    description = "Incident mineur simule pour verifier le workflow"
    immediateAction = "Surveillance renforcee"
    causality = "UNLIKELY"
    reportable = $false
}
$session = Invoke-ClinicalApi -Method POST -Path "/api/clinical/sessions/$($session.id)/transition" -Token $specialistToken -Body @{ targetStatus = "COMPLETED" }
$session = Invoke-ClinicalApi -Method POST -Path "/api/clinical/sessions/$($session.id)/transition" -Token $biologistToken -Body @{
    targetStatus = "VALIDATED"
    clinicalSummary = "Seance terminee et validee sans evenement grave."
}

$audit = Invoke-ClinicalApi -Method GET -Path "/api/admin/audit?patientId=$($patient.id)&size=100" -Token $adminToken

[pscustomobject]@{
    patientId = $patient.id
    prescriptionStatus = $prescription.status
    sessionStatus = $session.status
    observationId = $observation.id
    laboratoryResultStatus = $labResult.status
    incidentId = $incident.id
    auditEventCount = $audit.totalElements
    specialistId = $specialist.id
    biologistId = $biologist.id
}
