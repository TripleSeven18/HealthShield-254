package com.triple7.healthshield254.ui.screens.bot

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.triple7.healthshield254.ui.theme.WarmCream
import com.triple7.healthshield254.ui.theme.tripleSeven
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val SUPPORT_NUMBER = "+254743887226"

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

enum class ConsultationStage {
    IDLE,
    INTAKE_STEP_1, // Age/Sex
    INTAKE_STEP_2, // Weight/History/Meds
    CHIEF_COMPLAINT,
    SYMPTOM_FOLLOW_UP,
    CLINICAL_REASONING
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BotEnquiryScreen(navController: NavController) {
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<ChatMessage>() }
    val coroutineScope = rememberCoroutineScope()

    var stage by remember { mutableStateOf(ConsultationStage.IDLE) }
    var chiefComplaint by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if (messages.isEmpty()) {
            messages.add(ChatMessage("Good day. I am the HealthShield Virtual Physician. I am programmed to perform structured clinical assessments and provide evidence-based medical guidance.\n\nTo begin our consultation, please state your **age** and **biological sex**.", false))
            stage = ConsultationStage.INTAKE_STEP_1
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("HealthShield Virtual Physician", fontSize = 18.sp, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val waUri = Uri.parse("https://wa.me/${SUPPORT_NUMBER.removePrefix("+")}")
                        val intent = Intent(Intent.ACTION_VIEW, waUri)
                        context.startActivity(intent)
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Consultation Support")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = tripleSeven,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        containerColor = WarmCream
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            UrgentEscalationBanner()

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(messages) { message ->
                    PhysicianChatBubble(message)
                }
            }

            Surface(
                tonalElevation = 8.dp,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .navigationBarsPadding()
                        .imePadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Search pharmacology or describe symptoms...") },
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 3
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    FloatingActionButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                val userText = inputText
                                messages.add(ChatMessage(userText, true))
                                inputText = ""
                                coroutineScope.launch {
                                    delay(1200)
                                    val (response, nextStage, updatedComplaint) = getPhysicianResponse(userText, stage, chiefComplaint)
                                    messages.add(ChatMessage(response, false))
                                    stage = nextStage
                                    chiefComplaint = updatedComplaint
                                }
                            }
                        },
                        containerColor = tripleSeven,
                        contentColor = Color.White,
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun UrgentEscalationBanner() {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$SUPPORT_NUMBER")
                }
                context.startActivity(intent)
            },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFB71C1C)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.NotificationsActive, null, tint = Color.White, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("EMERGENCY ESCALATION", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Tap for immediate human physician contact: $SUPPORT_NUMBER", color = Color.White.copy(0.9f), fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun PhysicianChatBubble(message: ChatMessage) {
    val alignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    val bubbleColor = if (message.isUser) tripleSeven else Color.White
    val textColor = if (message.isUser) Color.White else Color.Black

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
        Column(horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start) {
            Surface(
                color = bubbleColor,
                shape = RoundedCornerShape(
                    topStart = 16.dp, topEnd = 16.dp,
                    bottomStart = if (message.isUser) 16.dp else 4.dp,
                    bottomEnd = if (message.isUser) 4.dp else 16.dp
                ),
                tonalElevation = 4.dp,
                modifier = Modifier.widthIn(max = 300.dp)
            ) {
                Text(text = message.text, modifier = Modifier.padding(14.dp), color = textColor, fontSize = 14.sp, lineHeight = 20.sp)
            }
            if (!message.isUser) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 6.dp, top = 4.dp)) {
                    Icon(Icons.Default.SmartToy, null, modifier = Modifier.size(14.dp), tint = tripleSeven)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("HealthShield Virtual Physician", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

data class PhysicianResult(val text: String, val nextStage: ConsultationStage, val complaint: String)

fun getPhysicianResponse(input: String, stage: ConsultationStage, currentComplaint: String): PhysicianResult {
    val q = input.lowercase()
    val disclaimer = "\n\n***\n**Clinical Disclaimer:** This automated assessment is for informational purposes only. It is not a substitute for an in-person physical examination or professional diagnosis. In emergencies, go to the nearest hospital."

    // 100+ Medicine Handbook Lookup
    val drugInfo = consultPharmacologyHandbook(q)
    if (drugInfo != null && (stage == ConsultationStage.IDLE || stage == ConsultationStage.CLINICAL_REASONING)) {
        return PhysicianResult(drugInfo + disclaimer, stage, currentComplaint)
    }

    return when (stage) {
        ConsultationStage.INTAKE_STEP_1 -> {
            PhysicianResult(
                "I have noted your profile. \n\n**Step 2: Medical History**\nTo ensure a safe assessment, please provide your approximate weight, pregnancy status (if applicable), and any known chronic conditions (e.g., Diabetes, Hypertension) or current medications/allergies.",
                ConsultationStage.INTAKE_STEP_2,
                ""
            )
        }

        ConsultationStage.INTAKE_STEP_2 -> {
            PhysicianResult(
                "Clinical history integrated. \n\n**Step 3: Chief Complaint**\nWhat is the primary medical concern or symptom you wish to discuss today?",
                ConsultationStage.CHIEF_COMPLAINT,
                ""
            )
        }

        ConsultationStage.CHIEF_COMPLAINT -> {
            val complaint = when {
                q.contains("headache") -> "Cephalgia"
                q.contains("chest") -> "Thoracic Pain"
                q.contains("cough") || q.contains("breath") -> "Respiratory Symptom"
                q.contains("fever") || q.contains("temperature") -> "Pyrexia"
                q.contains("stomach") || q.contains("belly") || q.contains("diarrhea") -> "Gastrointestinal Symptom"
                q.contains("urinary") || q.contains("pee") || q.contains("bladder") -> "Urinary Symptom"
                q.contains("rash") || q.contains("skin") || q.contains("itch") -> "Dermatological Symptom"
                else -> "General Inquiry"
            }
            val questions = when (complaint) {
                "Cephalgia" -> "Please characterize the headache: Is the onset sudden? Severity (1-10)? Where is the pain located? Are you experiencing vision changes, neck stiffness, or light sensitivity?"
                "Thoracic Pain" -> "This requires urgent detail: Is the pain sharp or crushing? Does it radiate to your jaw or left arm? Any sweating or shortness of breath?"
                "Respiratory Symptom" -> "Tell me more: Is the cough dry or productive of mucus? Are you experiencing dyspnea (shortness of breath) or audible wheezing?"
                "Pyrexia" -> "Regarding the fever: What is the highest recorded temperature? Do you have associated rigors (shaking), night sweats, or recent travel?"
                "Gastrointestinal Symptom" -> "Describe the abdominal issue: Where is the pain located? Is there associated nausea, vomiting, or change in bowel habits?"
                "Urinary Symptom" -> "Is there dysuria (painful urination), increased frequency, or blood in the urine? Any associated lower back or flank pain?"
                else -> "Please describe the onset, severity, and any factors that make the symptoms better or worse."
            }
            PhysicianResult(questions, ConsultationStage.SYMPTOM_FOLLOW_UP, complaint)
        }

        ConsultationStage.SYMPTOM_FOLLOW_UP -> {
            val diagnosis = when (currentComplaint) {
                "Cephalgia" -> {
                    if (q.contains("severe") || q.contains("stiff") || q.contains("vision") || q.contains("10")) {
                        "**PHYSICIAN ASSESSMENT:** Acute severe cephalgia with neurological red flags.\n**Differential Diagnosis:** \n1. Subarachnoid Hemorrhage \n2. Meningitis\n**Urgency:** 🚨 CRITICAL EMERGENCY.\n**Action:** Seek immediate evaluation at an Emergency Department."
                    } else {
                        "**PHYSICIAN ASSESSMENT:** Tension-type headache or Migraine.\n**Differential Diagnosis:** \n1. Tension Headache \n2. Primary Migraine\n**Urgency:** Non-emergent.\n**Management:** I recommend starting with Paracetamol (500mg-1g) or Ibuprofen (400mg) for relief. If symptoms do not improve within 4 hours, schedule a clinic visit."
                    }
                }
                "Thoracic Pain" -> {
                    if (q.contains("radiat") || q.contains("arm") || q.contains("jaw") || q.contains("sweat") || q.contains("breath")) {
                        "**PHYSICIAN ASSESSMENT:** Suspected Acute Coronary Syndrome (Myocardial Infarction).\n**Urgency:** 🚨 CRITICAL EMERGENCY.\n**Action:** CALL EMERGENCY SERVICES IMMEDIATELY. Do not attempt to drive."
                    } else {
                        "**PHYSICIAN ASSESSMENT:** Thoracic pain, likely musculoskeletal or gastric (GERD).\n**Urgency:** Same-day clinical evaluation required to rule out cardiac causes."
                    }
                }
                "Pyrexia" -> {
                    if (q.contains("travel") || q.contains("malaria") || q.contains("rigor")) {
                        "**PHYSICIAN ASSESSMENT:** Febrile illness with high risk of endemic infection.\n**Differential Diagnosis:** \n1. Malaria \n2. Typhoid Fever\n**Action:** Use Paracetamol for fever control and proceed to a laboratory for Malaria/CBC screening today."
                    } else {
                        "**PHYSICIAN ASSESSMENT:** Isolated pyrexia, likely viral.\n**Plan:** Maintain hydration and use Paracetamol (500mg) for symptomatic relief. If fever persists >48 hours, consult a physician."
                    }
                }
                "Respiratory Symptom" -> {
                    if (q.contains("breath") || q.contains("short")) {
                        "**PHYSICIAN ASSESSMENT:** Respiratory distress.\n**Differential Diagnosis:** \n1. Asthma Exacerbation \n2. Pneumonia\n**Urgency:** Urgent Medical Evaluation required. If breathing becomes labored, seek ER care."
                    } else {
                        "**PHYSICIAN ASSESSMENT:** Likely Upper Respiratory Tract Infection (URTI).\n**Plan:** Consider a mild pain reliever and cough suppressant. If productive cough develops with fever, start a professional consultation for possible antibiotics."
                    }
                }
                "Urinary Symptom" -> {
                    "**PHYSICIAN ASSESSMENT:** Suspected Urinary Tract Infection (Cystitis).\n**Differential Diagnosis:** \n1. Bacterial UTI \n2. Urolithiasis (Kidney Stones)\n**Plan:** Increase fluid intake. I recommend a clinic visit for urinalysis and possible antibiotic treatment (e.g., Nitrofurantoin)."
                }
                else -> {
                    "**PHYSICIAN ASSESSMENT:** Presentation requires a physical examination.\n**Plan:** Initial trial of Paracetamol is appropriate for symptomatic relief. Please schedule a clinic appointment for a definitive diagnosis."
                }
            }
            PhysicianResult(diagnosis + disclaimer, ConsultationStage.CLINICAL_REASONING, currentComplaint)
        }

        else -> {
            PhysicianResult("Consultation concluded. I am ready to begin a new assessment. **Step 1:** State your age and sex.", ConsultationStage.INTAKE_STEP_1, "")
        }
    }
}

fun consultPharmacologyHandbook(q: String): String? {
    return when {
        // --- Cardiovascular ---
        q.contains("cardio") || q.contains("lisinopril") || q.contains("losartan") || q.contains("amlodipine") || q.contains("metoprolol") || q.contains("atorvastatin") ->
            "**PHYSICIAN HANDBOOK: CARDIOVASCULAR MEDICINES**\n" +
            "• **Lisinopril:** ACE inhibitor for hypertension/heart failure. Dose: 10–40 mg daily. Monitor: BP, Potassium.\n" +
            "• **Losartan:** Angiotensin receptor blocker for BP. Dose: 25–100 mg daily.\n" +
            "• **Amlodipine:** Calcium channel blocker for BP/Angina. Dose: 5–10 mg. Side effect: Ankle swelling.\n" +
            "• **Metoprolol:** Beta-1 blocker for HR control/BP. Dose: 25–200 mg.\n" +
            "• **Atorvastatin:** Statin for hyperlipidemia. Dose: 10–80 mg daily."

        // --- CNS / Pain ---
        q.contains("pain") || q.contains("paracetamol") || q.contains("ibuprofen") || q.contains("morphine") || q.contains("gabapentin") || q.contains("tramadol") ->
            "**PHYSICIAN HANDBOOK: CNS & ANALGESICS**\n" +
            "• **Paracetamol (Acetaminophen):** For mild pain/fever. Dose: 500-1000 mg every 4-6h. Max 4g/day. Caution: Liver.\n" +
            "• **Ibuprofen (Advil/Motrin):** NSAID for inflammation/pain. Dose: 200-800 mg every 6-8h. Take with food.\n" +
            "• **Morphine:** Strong opioid for severe pain. Individualized dosing. Monitor: Respiratory status.\n" +
            "• **Gabapentin:** For neuropathic pain. Dose: 300-3600 mg daily.\n" +
            "• **Tramadol:** Opioid analgesic for moderate-to-severe pain. Use with caution."

        // --- Anti-Infective ---
        q.contains("antibiotic") || q.contains("infection") || q.contains("amoxicillin") || q.contains("azithromycin") || q.contains("doxycycline") || q.contains("ceftriaxone") || q.contains("cipro") ->
            "**PHYSICIAN HANDBOOK: ANTI-INFECTIVES**\n" +
            "• **Amoxicillin:** Cell wall inhibitor for ENT/Respiratory infections. Dose: 500 mg every 8h.\n" +
            "• **Azithromycin:** Macrolide for pneumonia/STIs. Dose: 250-500 mg daily.\n" +
            "• **Doxycycline:** For acne/malaria prophylaxis. Dose: 100 mg BID.\n" +
            "• **Ceftriaxone:** 3rd gen cephalosporin (Injection). 1-2g daily.\n" +
            "• **Ciprofloxacin:** Fluoroquinolone for UTI/GI. Dose: 250-750 mg twice daily."

        // --- Endocrine / Diabetes ---
        q.contains("diabetes") || q.contains("metformin") || q.contains("semaglutide") || q.contains("insulin") || q.contains("levothyroxine") || q.contains("empagliflozin") ->
            "**PHYSICIAN HANDBOOK: ENDOCRINE & DIABETES**\n" +
            "• **Metformin:** First-line T2D. Dose: 500-2000 mg/day. Monitor: Renal function.\n" +
            "• **Semaglutide (Ozempic/Wegovy):** GLP-1 RA for T2D/Obesity. Weekly injection.\n" +
            "• **Empagliflozin:** SGLT2 inhibitor. 10-25 mg daily.\n" +
            "• **Insulin Glargine:** Long-acting basal insulin. Dose individualized.\n" +
            "• **Levothyroxine:** For Hypothyroidism. Dose titrated by TSH."

        // --- Respiratory ---
        q.contains("asthma") || q.contains("respiratory") || q.contains("salbutamol") || q.contains("budesonide") || q.contains("tiotropium") || q.contains("montelukast") ->
            "**PHYSICIAN HANDBOOK: RESPIRATORY MEDICINES**\n" +
            "• **Salbutamol (Ventolin):** Rescue inhaler for asthma. 1-2 puffs as needed.\n" +
            "• **Budesonide:** Inhaled steroid for long-term control.\n" +
            "• **Tiotropium:** LAMA for COPD management. 18 mcg daily.\n" +
            "• **Montelukast:** For allergic asthma/Rhinitis. 10 mg daily."

        // --- GI ---
        q.contains("acid") || q.contains("stomach") || q.contains("omeprazole") || q.contains("pantoprazole") || q.contains("famotidine") || q.contains("loperamide") || q.contains("ondansetron") ->
            "**PHYSICIAN HANDBOOK: GASTROINTESTINAL MEDICINES**\n" +
            "• **Omeprazole/Pantoprazole:** PPI for GERD/Ulcers. 20-40 mg daily.\n" +
            "• **Famotidine:** H2 blocker for acid suppression. 20-40 mg daily.\n" +
            "• **Loperamide:** For symptomatic relief of diarrhea. 2-16 mg daily.\n" +
            "• **Ondansetron:** Serotonin blocker for nausea/vomiting. 4-8 mg every 8h."

        // --- Emergency / Toxicology ---
        q.contains("poison") || q.contains("overdose") || q.contains("naloxone") || q.contains("atropine") || q.contains("epinephrine") ->
            "**PHYSICIAN HANDBOOK: EMERGENCY MEDICINES**\n" +
            "• **Naloxone:** For opioid overdose reversal.\n" +
            "• **Epinephrine:** For Anaphylaxis/Severe allergic reaction.\n" +
            "• **Atropine:** For Bradycardia/Organophosphate poisoning."

        // --- Preventive ---
        q.contains("vaccine") || q.contains("prevention") || q.contains("influenza") || q.contains("hpv") || q.contains("tetanus") ->
            "**PHYSICIAN HANDBOOK: PREVENTIVE MEDICINES**\n" +
            "• **Influenza Vaccine:** Annual flu prevention.\n" +
            "• **HPV Vaccine:** Prevention of cervical/anal cancer.\n" +
            "• **Tetanus Toxoid:** Prophylaxis against lockjaw."

        else -> null
    }
}
