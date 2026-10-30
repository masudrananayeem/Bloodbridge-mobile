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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bloodbridge.app.i18n.Lang
import com.bloodbridge.app.i18n.t

private data class LegalSection(val heading: String, val body: String)

private val privacyEn = listOf(
    LegalSection("1. About this policy", "This policy explains how the BloodBridge Android app handles information when you create an account and use donor search, blood requests, donation records, saved donors, organizations, notifications and profile features. BloodBridge is designed to help connect voluntary blood donors with people seeking blood."),
    LegalSection("2. Information you provide", "Depending on the features you use, we may process your full name, email address, phone number, blood group, date of birth, gender, district, upazila, present address and optional profile photo. You may also provide information in a blood request, such as the requested blood group, number of units, hospital, location, urgency, reason, required date and message."),
    LegalSection("3. Activity and app information", "To operate the service, BloodBridge processes your donor availability, donation history, requests you create or respond to, saved donor list, organization enquiries and in-app notification/read status. Firebase Authentication handles sign-in credentials. The app does not store your plain-text password on BloodBridge servers."),
    LegalSection("4. Location information", "The current Android app uses the district, upazila and address that you enter in your profile or request to help match donors and seekers. The current app version does not request device GPS/location permission or continuously track your precise location. If a future version introduces GPS-based features, this policy and the in-app permission explanation should be updated before that feature is enabled."),
    LegalSection("5. How information is used", "We use information to create and maintain your account; display relevant donor profiles; match blood groups and areas; create, route and manage blood requests; show donation history; save donors; provide organization details; deliver in-app notifications; respond to support or feedback messages where available; protect the service and investigate misuse."),
    LegalSection("6. What other users can see", "Information needed for donor discovery may be shown to signed-in users, such as your name, blood group, district/upazila, availability, verification indicator and profile photo if provided. Your phone number and email should remain masked in donor search. Contact details may be made available to the relevant parties for a specific request after a donor accepts it, according to the service's request workflow. Do not put sensitive personal information in public request messages."),
    LegalSection("7. Service providers and sharing", "We do not sell personal information. The app uses Firebase Authentication for account sign-in and Cloudinary for profile-photo hosting when configured. The backend/API hosting provider processes information to operate the service. These providers may process data under their own terms and privacy policies. We may disclose information if required by applicable law or to address security, fraud or safety concerns. We do not authorize unrelated advertising use of your blood or health-related profile information."),
    LegalSection("8. Photos and device permissions", "Profile photos are optional and are selected by you through the Android photo picker. The app needs internet access to communicate with Firebase and the BloodBridge API. The current manifest does not request contacts, microphone, camera or location permissions. A photo is uploaded only when you choose one and the configured upload service is available."),
    LegalSection("9. Notifications", "The current app includes an in-app notifications screen and read/unread status. This statement does not promise push notifications when the app is closed; push notification delivery may be added in a future version and will be described separately if enabled."),
    LegalSection("10. Retention and account deletion", "You can edit your profile, turn donor availability off, sign out or use Profile > Delete my account. Account deletion is intended to remove your account and associated app records through the backend. Some limited technical logs, security records or backup copies may persist for a reasonable period where needed for security, dispute handling or legal obligations. If deletion fails or you need help, contact the BloodBridge team through the support/contact channel published on the official BloodBridge website."),
    LegalSection("11. Security", "We use Firebase Authentication and encrypted HTTPS connections for production API communication. No internet service can guarantee absolute security. Keep your device secure, do not share passwords or one-time codes, and report suspected account misuse promptly."),
    LegalSection("12. Children and donor eligibility", "Blood donation is subject to medical and legal eligibility requirements. The app's terms require donors to be at least 18 years old. Do not create a donor profile for a child or submit another person's personal information without proper authority. A qualified medical professional or blood bank must determine donation eligibility."),
    LegalSection("13. Your choices and rights", "You can review and update profile information in the app, control donor availability, manage saved donors and request account deletion. You may contact the BloodBridge team to ask about access, correction or deletion of your information, subject to applicable law and operational/security requirements."),
    LegalSection("14. Changes and contact", "We may update this policy when app features, service providers or legal requirements change. The latest in-app version will show its update date. For privacy questions or requests, use the support/contact channel published on the official BloodBridge website. This policy should be read together with the BloodBridge Terms of Service."),
)

private val privacyBn = listOf(
    LegalSection("১. এই নীতিমালা সম্পর্কে", "এই নীতিমালায় BloodBridge Android অ্যাপে অ্যাকাউন্ট তৈরি, ডোনার খোঁজা, রক্তের অনুরোধ, রক্তদানের ইতিহাস, সেভড ডোনার, অর্গানাইজেশন, নোটিফিকেশন ও প্রোফাইল ব্যবহারের সময় তথ্য কীভাবে পরিচালিত হয় তা ব্যাখ্যা করা হয়েছে। BloodBridge স্বেচ্ছায় রক্তদান করতে ইচ্ছুক ব্যক্তি ও রক্তপ্রার্থীদের সংযুক্ত করতে সহায়তা করে।"),
    LegalSection("২. আপনার দেওয়া তথ্য", "আপনি কোন ফিচার ব্যবহার করছেন তার ওপর নির্ভর করে আপনার নাম, ইমেইল, ফোন নম্বর, রক্তের গ্রুপ, জন্মতারিখ, লিঙ্গ, জেলা, উপজেলা, বর্তমান ঠিকানা এবং ঐচ্ছিক প্রোফাইল ছবি প্রক্রিয়া করা হতে পারে। রক্তের অনুরোধে রক্তের গ্রুপ, ব্যাগ/ইউনিটের সংখ্যা, হাসপাতাল, এলাকা, জরুরিতা, কারণ, প্রয়োজনের তারিখ ও বার্তাও থাকতে পারে।"),
    LegalSection("৩. অ্যাপ ব্যবহারের তথ্য", "সেবা চালাতে আপনার ডোনার availability, রক্তদানের ইতিহাস, তৈরি করা বা গ্রহণ করা রিকোয়েস্ট, সেভড ডোনার তালিকা, অর্গানাইজেশন সংক্রান্ত অনুসন্ধান এবং ইন-অ্যাপ নোটিফিকেশনের read/unread অবস্থা প্রক্রিয়া করা হয়। Firebase Authentication লগইন পরিচালনা করে। BloodBridge সার্ভারে আপনার পাসওয়ার্ড plain text হিসেবে সংরক্ষণ করা হয় না।"),
    LegalSection("৪. অবস্থান-সংক্রান্ত তথ্য", "বর্তমান Android অ্যাপে ডোনার ও রক্তপ্রার্থী খুঁজতে আপনার প্রোফাইল বা রিকোয়েস্টে নিজে দেওয়া জেলা, উপজেলা ও ঠিকানা ব্যবহার করা হয়। বর্তমান সংস্করণ ফোনের GPS/location permission চায় না এবং আপনার সুনির্দিষ্ট অবস্থান নিয়মিত ট্র্যাক করে না। ভবিষ্যতে GPS-ভিত্তিক ফিচার যোগ হলে সেটি চালুর আগে এই নীতিমালা ও permission-এর ব্যাখ্যা আপডেট করতে হবে।"),
    LegalSection("৫. তথ্য ব্যবহারের উদ্দেশ্য", "অ্যাকাউন্ট পরিচালনা, উপযুক্ত ডোনার প্রোফাইল দেখানো, রক্তের গ্রুপ ও এলাকার ভিত্তিতে ম্যাচ করা, রক্তের অনুরোধ তৈরি ও পরিচালনা, ডোনেশন হিস্ট্রি দেখানো, ডোনার সেভ করা, অর্গানাইজেশনের তথ্য দেখানো, ইন-অ্যাপ নোটিফিকেশন, যেখানে সুবিধা আছে সেখানে সাপোর্ট/ফিডব্যাকের উত্তর এবং নিরাপত্তা ও অপব্যবহার প্রতিরোধে তথ্য ব্যবহার করা হয়।"),
    LegalSection("৬. অন্য ব্যবহারকারীরা কী দেখতে পারেন", "ডোনার খোঁজার জন্য প্রয়োজনীয় তথ্য—যেমন নাম, রক্তের গ্রুপ, জেলা/উপজেলা, availability, verification status এবং দেওয়া থাকলে প্রোফাইল ছবি—সাইন-ইন করা ব্যবহারকারীদের দেখানো হতে পারে। ডোনার সার্চে ফোন নম্বর ও ইমেইল মাস্ক করা থাকার কথা। নির্দিষ্ট রিকোয়েস্টে ডোনার গ্রহণ করলে সংশ্লিষ্ট পক্ষের কাছে যোগাযোগের তথ্য প্রকাশিত হতে পারে। পাবলিক রিকোয়েস্টের বার্তায় অপ্রয়োজনীয় সংবেদনশীল তথ্য দেবেন না।"),
    LegalSection("৭. তৃতীয় পক্ষের সেবা ও তথ্য শেয়ার", "আমরা ব্যক্তিগত তথ্য বিক্রি করি না। অ্যাকাউন্ট লগইনের জন্য Firebase Authentication এবং কনফিগার করা থাকলে প্রোফাইল ছবি সংরক্ষণের জন্য Cloudinary ব্যবহৃত হয়। Backend/API hosting provider-ও সেবা পরিচালনার জন্য তথ্য প্রক্রিয়া করে। এসব প্রতিষ্ঠানের নিজস্ব শর্ত ও প্রাইভেসি নীতি প্রযোজ্য হতে পারে। আইনগত বাধ্যবাধকতা, নিরাপত্তা, জালিয়াতি বা ঝুঁকি মোকাবিলায় প্রয়োজন হলে তথ্য প্রকাশ করা হতে পারে। আপনার রক্ত বা স্বাস্থ্য-সম্পর্কিত প্রোফাইল তথ্যকে অপ্রাসঙ্গিক বিজ্ঞাপনের কাজে ব্যবহারের অনুমতি দেওয়া হয় না।"),
    LegalSection("৮. ছবি ও ডিভাইস পারমিশন", "প্রোফাইল ছবি ঐচ্ছিক; Android photo picker থেকে আপনি নিজে ছবি নির্বাচন করেন। Firebase ও BloodBridge API-তে যোগাযোগের জন্য ইন্টারনেট দরকার। বর্তমান Android manifest contacts, microphone, camera বা location permission চায় না। আপনি ছবি নির্বাচন করলে এবং upload service কনফিগার করা থাকলেই ছবি আপলোড হয়।"),
    LegalSection("৯. নোটিফিকেশন", "বর্তমান অ্যাপে ইন-অ্যাপ নোটিফিকেশন স্ক্রিন এবং read/unread অবস্থা রয়েছে। অ্যাপ বন্ধ থাকলেও push notification আসবে—এমন নিশ্চয়তা এই নীতিমালা দেয় না। ভবিষ্যতে push notification চালু হলে তার ব্যবহার আলাদাভাবে জানানো হবে।"),
    LegalSection("১০. তথ্য সংরক্ষণ ও অ্যাকাউন্ট মুছে ফেলা", "আপনি অ্যাপ থেকে প্রোফাইল পরিবর্তন, donor availability বন্ধ, logout অথবা Profile > Delete my account ব্যবহার করতে পারেন। অ্যাকাউন্ট ডিলিটের মাধ্যমে backend-এ থাকা অ্যাকাউন্ট ও সংশ্লিষ্ট অ্যাপ রেকর্ড মুছে ফেলার উদ্দেশ্য রয়েছে। নিরাপত্তা, বিরোধ নিষ্পত্তি বা আইনগত কারণে সীমিত technical log, security record বা backup যুক্তিসঙ্গত সময় পর্যন্ত থাকতে পারে। ডিলিট ব্যর্থ হলে বা সহায়তা প্রয়োজন হলে BloodBridge-এর অফিসিয়াল ওয়েবসাইটে প্রকাশিত support/contact মাধ্যম ব্যবহার করুন।"),
    LegalSection("১১. তথ্যের নিরাপত্তা", "Production API যোগাযোগে HTTPS এবং অ্যাকাউন্টের জন্য Firebase Authentication ব্যবহার করা হয়। কোনো অনলাইন সেবাই শতভাগ নিরাপত্তার নিশ্চয়তা দিতে পারে না। নিজের ডিভাইস সুরক্ষিত রাখুন, পাসওয়ার্ড বা OTP শেয়ার করবেন না এবং সন্দেহজনক অ্যাকাউন্ট ব্যবহারের ঘটনা দ্রুত জানান।"),
    LegalSection("১২. শিশু ও ডোনার হওয়ার যোগ্যতা", "রক্তদানের ক্ষেত্রে চিকিৎসাগত ও আইনগত যোগ্যতার শর্ত প্রযোজ্য। অ্যাপের শর্ত অনুযায়ী ডোনার হিসেবে নিবন্ধনের ন্যূনতম বয়স ১৮ বছর। শিশুর নামে ডোনার প্রোফাইল তৈরি করবেন না এবং যথাযথ অনুমতি ছাড়া অন্যের ব্যক্তিগত তথ্য জমা দেবেন না। রক্তদানের যোগ্যতা চিকিৎসক বা ব্লাড ব্যাংক নির্ধারণ করবেন।"),
    LegalSection("১৩. আপনার অধিকার ও নিয়ন্ত্রণ", "অ্যাপে প্রোফাইলের তথ্য দেখা ও সংশোধন, ডোনার availability নিয়ন্ত্রণ, সেভড ডোনার পরিচালনা এবং অ্যাকাউন্ট ডিলিটের অনুরোধ করা যায়। প্রযোজ্য আইন এবং নিরাপত্তা/পরিচালনাগত প্রয়োজনীয়তার অধীনে তথ্য দেখা, সংশোধন বা মুছে ফেলার বিষয়ে BloodBridge টিমের সঙ্গে যোগাযোগ করতে পারেন।"),
    LegalSection("১৪. নীতিমালার পরিবর্তন ও যোগাযোগ", "অ্যাপের ফিচার, সেবা প্রদানকারী বা আইনগত প্রয়োজন পরিবর্তিত হলে এই নীতিমালা হালনাগাদ হতে পারে। সর্বশেষ সংস্করণে আপডেটের তারিখ দেখানো হবে। প্রাইভেসি সংক্রান্ত প্রশ্ন বা অনুরোধের জন্য BloodBridge-এর অফিসিয়াল ওয়েবসাইটে প্রকাশিত support/contact মাধ্যম ব্যবহার করুন। এই নীতিমালার পাশাপাশি BloodBridge Terms of Service-ও পড়ুন।"),
)

private val termsEn = listOf(
    LegalSection("1. The service", "BloodBridge is a technology platform that helps connect voluntary blood donors and people seeking blood. It is not a hospital, blood bank, medical provider or emergency response service."),
    LegalSection("2. Accurate information", "You must provide truthful, current information and update it when it changes. Do not impersonate another person or create a request without a genuine need and appropriate authority."),
    LegalSection("3. Medical eligibility and safety", "Donors must be at least 18 and must independently confirm eligibility with a qualified medical professional or blood bank. The app's eligibility countdown is informational only and is not medical clearance."),
    LegalSection("4. Requests and contact", "Use requests only for genuine blood needs. Treat other users respectfully, protect their contact information and independently verify identity, hospital details and medical arrangements before proceeding."),
    LegalSection("5. Prohibited conduct", "Fraud, harassment, discrimination, spam, false emergency requests, misuse of personal information and requesting or offering payment for blood are prohibited."),
    LegalSection("6. No medical or emergency guarantee", "BloodBridge cannot guarantee that a donor will respond, that blood will be available, or that a donation is medically appropriate. In an emergency, contact a hospital or local emergency service directly."),
    LegalSection("7. Account actions and changes", "You are responsible for activity under your account. You may edit your profile or request account deletion in the app. We may restrict access where necessary to protect users, the service or comply with law. These terms may be updated; continued use after an update means you accept the revised terms."),
)

private val termsBn = listOf(
    LegalSection("১. সেবা পরিচিতি", "BloodBridge একটি প্রযুক্তিভিত্তিক প্ল্যাটফর্ম, যা স্বেচ্ছায় রক্তদাতা ও রক্তপ্রার্থীদের সংযুক্ত করতে সহায়তা করে। এটি হাসপাতাল, ব্লাড ব্যাংক, চিকিৎসা প্রদানকারী বা জরুরি সেবা নয়।"),
    LegalSection("২. সঠিক তথ্য", "আপনাকে সত্য ও হালনাগাদ তথ্য দিতে হবে এবং পরিবর্তন হলে আপডেট করতে হবে। অন্যের পরিচয় ব্যবহার করবেন না এবং প্রকৃত প্রয়োজন ও যথাযথ কর্তৃত্ব ছাড়া রক্তের অনুরোধ তৈরি করবেন না।"),
    LegalSection("৩. চিকিৎসাগত যোগ্যতা ও নিরাপত্তা", "ডোনারের বয়স কমপক্ষে ১৮ বছর হতে হবে এবং যোগ্য চিকিৎসক বা ব্লাড ব্যাংকের মাধ্যমে নিজ দায়িত্বে যোগ্যতা নিশ্চিত করতে হবে। অ্যাপের eligibility countdown শুধু তথ্যের জন্য; এটি চিকিৎসাগত অনুমোদন নয়।"),
    LegalSection("৪. রিকোয়েস্ট ও যোগাযোগ", "শুধু প্রকৃত রক্তের প্রয়োজনে রিকোয়েস্ট ব্যবহার করুন। অন্য ব্যবহারকারীর সঙ্গে সম্মানজনক আচরণ করুন, তাদের যোগাযোগের তথ্য সুরক্ষিত রাখুন এবং এগোনোর আগে পরিচয়, হাসপাতাল ও চিকিৎসা-সংক্রান্ত ব্যবস্থা নিজে যাচাই করুন।"),
    LegalSection("৫. নিষিদ্ধ আচরণ", "প্রতারণা, হয়রানি, বৈষম্য, স্প্যাম, মিথ্যা জরুরি রিকোয়েস্ট, ব্যক্তিগত তথ্যের অপব্যবহার এবং রক্তের বিনিময়ে টাকা চাওয়া বা দেওয়া নিষিদ্ধ।"),
    LegalSection("৬. চিকিৎসা বা জরুরি সেবার নিশ্চয়তা নয়", "কোনো ডোনার সাড়া দেবেন, রক্ত পাওয়া যাবে বা রক্তদান চিকিৎসাগতভাবে উপযুক্ত হবে—BloodBridge তার নিশ্চয়তা দেয় না। জরুরি অবস্থায় সরাসরি হাসপাতাল বা স্থানীয় জরুরি সেবার সঙ্গে যোগাযোগ করুন।"),
    LegalSection("৭. অ্যাকাউন্ট ও পরিবর্তন", "আপনার অ্যাকাউন্টের কার্যকলাপের দায়িত্ব আপনার। অ্যাপে প্রোফাইল পরিবর্তন বা অ্যাকাউন্ট ডিলিটের অনুরোধ করতে পারেন। ব্যবহারকারীর নিরাপত্তা, সেবা সুরক্ষা বা আইন মেনে চলার জন্য প্রয়োজন হলে প্রবেশাধিকার সীমিত করা হতে পারে। শর্তাবলী আপডেট হতে পারে; পরিবর্তনের পর অ্যাপ ব্যবহার চালিয়ে গেলে সংশোধিত শর্ত মেনে নেওয়া হবে।"),
)

@Composable
fun LegalDialog(kind: String, onClose: () -> Unit) {
    val bn = Lang.current == "bn"
    val isPrivacy = kind == "privacy"
    val title = if (isPrivacy) t("Privacy Policy") else t("Terms of Service")
    val sections = when {
        isPrivacy && bn -> privacyBn
        isPrivacy -> privacyEn
        bn -> termsBn
        else -> termsEn
    }
    Dialog(onDismissRequest = onClose, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(Modifier.fillMaxSize()) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TextButton(onClick = onClose) { Text("← " + t("Back")) }
                    Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
                Column(
                    modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        if (bn) "সর্বশেষ আপডেট: ৩০ অক্টোবর ২০২৬" else "Last updated: 30 October 2026",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        if (isPrivacy) {
                            if (bn) "BloodBridge ব্যবহার করার আগে অনুগ্রহ করে এই নীতিমালা পড়ুন।" else "Please read this policy before using BloodBridge."
                        } else {
                            if (bn) "BloodBridge ব্যবহার করলে নিচের শর্তাবলী প্রযোজ্য হবে।" else "These terms apply when you use BloodBridge."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    sections.forEach { section ->
                        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            Text(section.heading, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text(section.body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
        }
    }
}
