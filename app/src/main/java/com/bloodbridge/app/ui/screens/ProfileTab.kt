package com.bloodbridge.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
    var confirmDelete by remember { mutableStateOf(false) }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) vm.uploadPhoto(uri)
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Delete account?") },
            text = { Text("This permanently deletes your profile, requests and history. This cannot be undone.") },
            confirmButton = {
                TextButton(enabled = !vm.busy, onClick = { confirmDelete = false; vm.deleteAccount() }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Cancel") } },
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
        OutlinedButton(onClick = { picker.launch("image/*") }, enabled = !vm.busy) { Text("Change photo") }
        Text(u?.email.orEmpty(), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("Verified: ${if (u?.isVerified == true) "Yes ✓" else "No"}", style = MaterialTheme.typography.bodySmall)

        AppTextField(fullName, { fullName = it }, "Full name")
        AppTextField(phone, { phone = it }, "Phone (01XXXXXXXXX)", keyboardType = KeyboardType.Phone)
        DropdownField("Blood group", bloodGroup, Constants.bloodGroups, { bloodGroup = it })
        DropdownField("District", district, Constants.districts, { district = it })
        AppTextField(upazila, { upazila = it }, "Upazila")
        AppTextField(address, { address = it }, "Present address")
        ErrorBanner(vm.error)
        PrimaryButton("Save changes", vm.busy, {
            vm.updateProfile(
                UpdateProfileBody(
                    fullName = fullName.trim(), phone = phone.trim(), bloodGroup = bloodGroup,
                    district = district, upazila = upazila.trim(), address = address.trim(),
                )
            )
        })

        Box(Modifier.height(8.dp))
        OutlinedButton(onClick = { vm.logout() }, modifier = Modifier.fillMaxWidth()) { Text("Log out") }
        TextButton(onClick = { confirmDelete = true }, modifier = Modifier.fillMaxWidth()) {
            Text("Delete my account", color = MaterialTheme.colorScheme.error)
        }
    }
}
