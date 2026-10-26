package com.bloodbridge.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bloodbridge.app.i18n.Lang
import com.bloodbridge.app.i18n.t

private typealias Section = Pair<String, String>

private val privacyEn: List<Section> = listOf(
    "Information we collect" to "When you register we collect your name, email, phone number, date of birth, gender, blood group, district/upazila/address and an optional profile photo. We also store your donation history, blood requests you create or respond to, saved donors and notifications.",
    "How we use it" to "We use this information to run BloodBridge: matching blood seekers with available donors, sending request notifications and keeping the platform safe. A donor's phone and email are shown to a seeker only after the donor accepts that seeker's request.",
    "Sharing" to "We do not sell your data. Your profile is visible to other users only as needed for donor search (name, blood group, location, availability). Service providers we use: Firebase (authentication) and Cloudinary (profile photo storage).",
    "Your choices" to "You can edit your profile, switch your availability off, or permanently delete your account and data from Profile > Delete account.",
    "Security" to "Passwords are handled by Firebase Authentication and are never stored by us. Data is sent over HTTPS.",
    "Contact" to "Questions about this policy? Contact the BloodBridge team through the contact details on our website.",
)

private val privacyBn: List<Section> = listOf(
    "আমরা যে তথ্য সংগ্রহ করি" to "রেজিস্ট্রেশনের সময় আমরা আপনার নাম, ইমেইল, ফোন নম্বর, জন্ম তারিখ, লিঙ্গ, রক্তের গ্রুপ, জেলা/উপজেলা/ঠিকানা এবং ঐচ্ছিক প্রোফাইল ছবি সংগ্রহ করি। এছাড়া আপনার রক্তদানের ইতিহাস, আপনার করা বা সাড়া দেওয়া রক্তের অনুরোধ, সেভ করা ডোনার ও নোটিফিকেশন সংরক্ষণ করা হয়।",
    "কীভাবে ব্যবহার করি" to "এই তথ্য দিয়ে আমরা BloodBridge চালাই: রক্তগ্রহীতার সাথে উপলব্ধ ডোনারের মিল করা, অনুরোধের নোটিফিকেশন পাঠানো এবং প্ল্যাটফর্ম নিরাপদ রাখা। ডোনার অনুরোধ গ্রহণ করার পরেই কেবল গ্রহীতা ডোনারের ফোন ও ইমেইল দেখতে পায়।",
    "শেয়ারিং" to "আমরা আপনার তথ্য বিক্রি করি না। ডোনার সার্চের জন্য যতটুকু দরকার (নাম, রক্তের গ্রুপ, অবস্থান, উপলব্ধতা) ততটুকুই অন্য ব্যবহারকারীরা দেখতে পায়। আমরা Firebase (অথেনটিকেশন) ও Cloudinary (প্রোফাইল ছবি) ব্যবহার করি।",
    "আপনার নিয়ন্ত্রণ" to "আপনি প্রোফাইল এডিট করতে, উপলব্ধতা বন্ধ করতে, বা প্রোফাইল > অ্যাকাউন্ট ডিলিট থেকে আপনার অ্যাকাউন্ট ও ডেটা স্থায়ীভাবে মুছে ফেলতে পারেন।",
    "নিরাপত্তা" to "পাসওয়ার্ড Firebase Authentication পরিচালনা করে, আমরা তা সংরক্ষণ করি না। ডেটা HTTPS এর মাধ্যমে পাঠানো হয়।",
    "যোগাযোগ" to "এই নীতি নিয়ে প্রশ্ন থাকলে আমাদের ওয়েবসাইটে দেওয়া যোগাযোগের তথ্যের মাধ্যমে জানান।",
)

private val termsEn: List<Section> = listOf(
    "Using BloodBridge" to "BloodBridge connects people who need blood with voluntary donors. You must provide accurate information and be at least 18 years old to register as a donor.",
    "Not medical advice" to "BloodBridge is not a medical service or a blood bank. Donors and seekers are responsible for confirming eligibility and safety with a qualified medical professional or hospital.",
    "Donor eligibility" to "A donor becomes available again 120 days after a recorded donation. Do not mark yourself available if you are unwell or medically ineligible.",
    "Acceptable use" to "Do not post false requests, harass other users or misuse contact details. Accounts that do so may be suspended or removed by an admin.",
    "Blood is never sold" to "Donation through BloodBridge is voluntary. Do not ask for or offer payment for blood.",
    "Changes" to "We may update these terms. Continued use of the app means you accept the updated terms.",
)

private val termsBn: List<Section> = listOf(
    "BloodBridge ব্যবহার" to "BloodBridge রক্তপ্রয়োজনী মানুষকে স্বেচ্ছাসেবী ডোনারের সাথে যুক্ত করে। সঠিক তথ্য দিতে হবে এবং ডোনার হিসেবে রেজিস্ট্রেশনের জন্য বয়স কমপক্ষে ১৮ হতে হবে।",
    "চিকিৎসা পরামর্শ নয়" to "BloodBridge কোনো চিকিৎসা সেবা বা ব্লাড ব্যাংক নয়। যোগ্যতা ও নিরাপত্তা যাচাই করার দায়িত্ব ডোনার ও গ্রহীতার; এজন্য যোগ্য চিকিৎসক বা হাসপাতালের সাথে কথা বলুন।",
    "ডোনারের যোগ্যতা" to "রক্তদান রেকর্ড করার ১২০ দিন পর ডোনার আবার উপলব্ধ হন। অসুস্থ বা চিকিৎসাগতভাবে অযোগ্য হলে নিজেকে উপলব্ধ দেখাবেন না।",
    "গ্রহণযোগ্য ব্যবহার" to "মিথ্যা অনুরোধ দেবেন না, অন্যদের হয়রানি করবেন না, বা যোগাযোগের তথ্যের অপব্যবহার করবেন না। এমন করলে অ্যাডমিন অ্যাকাউন্ট স্থগিত বা মুছে দিতে পারেন।",
    "রক্ত বিক্রি নয়" to "BloodBridge এর মাধ্যমে রক্তদান সম্পূর্ণ স্বেচ্ছায়। রক্তের বিনিময়ে টাকা চাইবেন না বা দেবেন না।",
    "পরিবর্তন" to "আমরা এই শর্ত হালনাগাদ করতে পারি। অ্যাপ ব্যবহার চালিয়ে গেলে নতুন শর্ত মেনে নেওয়া হবে।",
)

/** Full-screen in-app Privacy Policy / Terms (kind = "privacy" or "terms"). Works offline. */
@Composable
fun LegalDialog(kind: String, onClose: () -> Unit) {
    val bn = Lang.current == "bn"
    val title = if (kind == "privacy") t("Privacy Policy") else t("Terms of Service")
    val sections = when {
        kind == "privacy" && bn -> privacyBn
        kind == "privacy" -> privacyEn
        bn -> termsBn
        else -> termsEn
    }
    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(Modifier.fillMaxSize()) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onClose) { Text("← " + t("Back")) }
                    Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
                Column(
                    modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(t("Last updated") + ": October 2026", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    sections.forEach { (h, p) ->
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(h, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text(p, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
