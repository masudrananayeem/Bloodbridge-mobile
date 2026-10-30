package com.bloodbridge.app.ui.screens

import com.bloodbridge.app.i18n.t
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import com.bloodbridge.app.ui.LanguageToggle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bloodbridge.app.data.Constants
import com.bloodbridge.app.data.ageFromDob
import com.bloodbridge.app.data.UpdateProfileBody
import com.bloodbridge.app.ui.AppTextField
import com.bloodbridge.app.ui.AppViewModel
import com.bloodbridge.app.ui.DropdownField
import com.bloodbridge.app.ui.ErrorBanner
import com.bloodbridge.app.ui.PrimaryButton

@Composable
fun ProfileTab(vm: AppViewModel) {
    val u = vm.user
    var fullName by remember(u?.id) { mutableStateOf(u?.fullName.orEmpty()) }
    var phone by remember(u?.id) { mutableStateOf(u?.phone.orEmpty()) }
    var bloodGroup by remember(u?.id) { mutableStateOf(u?.bloodGroup.orEmpty()) }
    var district by remember(u?.id) { mutableStateOf(u?.district.orEmpty()) }
    var upazila by remember(u?.id) { mutableStateOf(u?.upazila.orEmpty()) }
    var address by remember(u?.id) { mutableStateOf(u?.address.orEmpty()) }
    var dob by remember(u?.id) { mutableStateOf(u?.dateOfBirth?.take(10).orEmpty()) }
    var localError by remember { mutableStateOf<String?>(null) }
    var confirmDelete by remember { mutableStateOf(false) }
    var deleteText by remember { mutableStateOf("") }
    var feedbackCategory by remember { mutableStateOf("General") }
    var feedbackMessage by remember { mutableStateOf("") }
    LaunchedEffect(u?.id) { if (u != null) vm.loadMyFeedback() }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) vm.uploadPhoto(uri)
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text(t("Delete account?")) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(t("This permanently deletes your profile, requests and history. This cannot be undone."))
                    Text(t("Type DELETE to confirm") + ": DELETE")
                    AppTextField(deleteText, { deleteText = it }, "DELETE")
                }
            },
            confirmButton = {
                TextButton(enabled = !vm.busy && deleteText == "DELETE", onClick = { confirmDelete = false; deleteText = ""; vm.deleteAccount() }) {
                    Text(t("Delete"), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = false; deleteText = "" }) { Text(t("Cancel")) } },
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier.size(104.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            if (!u?.photoURL.isNullOrBlank()) {
                AsyncImage(
                    model = u?.photoURL, contentDescription = "Profile photo",
                    contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize(),
                )
            } else {
                Text(u?.fullName?.take(1)?.uppercase().orEmpty(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            }
        }
        OutlinedButton(onClick = { picker.launch("image/*") }, enabled = !vm.busy) { Text(t("Change photo")) }
        Text(u?.email.orEmpty(), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("${t("Verified")}: ${if (u?.isVerified == true) "✓" else "—"}", style = MaterialTheme.typography.bodySmall)

        AppTextField(fullName, { fullName = it }, t("Full name"))
        AppTextField(phone, { phone = it }, t("Phone (01XXXXXXXXX)"), keyboardType = KeyboardType.Phone)
        DropdownField(t("Blood group"), bloodGroup, Constants.bloodGroups, { bloodGroup = it })
        DropdownField(t("District"), district, Constants.districts, { district = it })
        AppTextField(dob, { dob = it.take(10) }, t("Date of birth"), placeholder = "YYYY-MM-DD")
        ageFromDob(dob)?.let { Text("${t("Age")}: $it ${t("yrs")}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        AppTextField(upazila, { upazila = it }, t("Upazila"))
        AppTextField(address, { address = it }, t("Present address"))
        localError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        ErrorBanner(vm.error)
        PrimaryButton(t("Save changes"), vm.busy, {
            localError = when {
                address.isBlank() -> t("Present address is required.")
                dob.isNotBlank() && ageFromDob(dob) == null -> t("Date of birth must be YYYY-MM-DD.")
                else -> null
            }
            if (localError == null) vm.updateProfile(
                UpdateProfileBody(
                    fullName = fullName.trim(), phone = phone.trim(), bloodGroup = bloodGroup,
                    district = district, upazila = upazila.trim(), address = address.trim(),
                    dateOfBirth = dob.ifBlank { null },
                )
            )
        })

        OutlinedButton(onClick = { vm.resetPassword(u?.email.orEmpty()) {} }, enabled = !vm.busy, modifier = Modifier.fillMaxWidth()) { Text(t("Send password reset link")) }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(t("Language"), modifier = Modifier.padding(end = 8.dp))
            LanguageToggle()
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(t("Theme"), modifier = Modifier.padding(end = 8.dp))
            com.bloodbridge.app.ui.ThemeToggle()
        }
        FeedbackPanel(
            category = feedbackCategory, onCategoryChange = { feedbackCategory = it },
            message = feedbackMessage, onMessageChange = { feedbackMessage = it },
            items = vm.myFeedback, busy = vm.busy, error = vm.error,
            onSend = { vm.sendFeedback(feedbackCategory, feedbackMessage) },
        )
        com.bloodbridge.app.ui.LegalLinks()
        Box(Modifier.height(8.dp))
        OutlinedButton(onClick = { vm.logout() }, modifier = Modifier.fillMaxWidth()) { Text(t("Log out")) }
        TextButton(onClick = { confirmDelete = true }, modifier = Modifier.fillMaxWidth()) {
            Text(t("Delete my account"), color = MaterialTheme.colorScheme.error)
        }
    }
}


@Composable
private fun FeedbackPanel(
    category: String, onCategoryChange: (String) -> Unit,
    message: String, onMessageChange: (String) -> Unit,
    items: List<com.bloodbridge.app.data.FeedbackItem>, busy: Boolean, error: String?, onSend: () -> Unit,
) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(t("Send Feedback"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(t("Share a suggestion, report a problem, or tell us about your experience."), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        DropdownField(t("Category"), category, listOf("General", "Bug Report", "Feature Request", "Complaint", "Praise"), onCategoryChange)
        OutlinedTextField(
            value = message, onValueChange = onMessageChange, modifier = Modifier.fillMaxWidth(),
            label = { Text(t("Your Message")) }, placeholder = { Text(t("Tell us what's on your mind...")) },
            minLines = 3, maxLines = 5,
        )
        error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
        PrimaryButton(t("Send Feedback"), busy, onSend)
        if (items.isNotEmpty()) {
            Text(t("My Feedback History"), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            items.forEach { item ->
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                    Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(item.category, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                            Text(if (item.status == "reviewed") t("Reviewed") else t("New"), style = MaterialTheme.typography.labelSmall)
                        }
                        Text(item.message, style = MaterialTheme.typography.bodyMedium)
                        item.createdAt?.let { Text(it.take(10), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    }
                }
            }
        }
    }
}
