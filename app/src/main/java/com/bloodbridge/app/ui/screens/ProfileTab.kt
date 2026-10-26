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
import androidx.compose.runtime.Composable
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
        com.bloodbridge.app.ui.LegalLinks()

        androidx.compose.material3.HorizontalDivider()
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Text(t("Feedback & support"), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(t("Share a suggestion, report a bug, or send us a complaint."), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            DropdownField("Category", feedbackCategory, listOf("General", "Bug Report", "Feature Request", "Complaint", "Praise"), { feedbackCategory = it })
            OutlinedTextField(
                value = feedbackMessage,
                onValueChange = { feedbackMessage = it },
                label = { Text(t("Your feedback")) },
                placeholder = { Text(t("Tell us what we can improve…")) },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth(),
            )
            PrimaryButton(t("Send feedback"), vm.busy, {
                vm.sendFeedback(feedbackCategory, feedbackMessage) { feedbackMessage = "" }
            })
            if (vm.feedback.isNotEmpty()) {
                Text(t("My feedback"), fontWeight = FontWeight.SemiBold)
                vm.feedback.take(5).forEach { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    ) {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(item.category, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                                Text(t(item.status), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                            }
                            Text(item.message, style = MaterialTheme.typography.bodyMedium)
                            item.createdAt?.let { Text(it.take(10), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                        }
                    }
                }
            }
            ErrorBanner(vm.error)
        }
        Box(Modifier.height(8.dp))
        OutlinedButton(onClick = { vm.logout() }, modifier = Modifier.fillMaxWidth()) { Text(t("Log out")) }
        TextButton(onClick = { confirmDelete = true }, modifier = Modifier.fillMaxWidth()) {
            Text(t("Delete my account"), color = MaterialTheme.colorScheme.error)
        }
    }
}
