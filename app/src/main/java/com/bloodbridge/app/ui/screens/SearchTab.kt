package com.bloodbridge.app.ui.screens

import com.bloodbridge.app.i18n.lbl
import com.bloodbridge.app.i18n.t
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bloodbridge.app.data.Constants
import com.bloodbridge.app.data.Donor
import com.bloodbridge.app.ui.AppViewModel
import com.bloodbridge.app.ui.Brand
import com.bloodbridge.app.ui.DropdownField
import com.bloodbridge.app.ui.ErrorBanner
import com.bloodbridge.app.ui.PrimaryButton
import kotlin.math.roundToInt

@Composable
fun SearchTab(vm: AppViewModel) {
    var bloodGroup by rememberSaveable { mutableStateOf("") }
    var district by rememberSaveable { mutableStateOf("") }
    LaunchedEffect(Unit) { vm.loadRequests(); vm.loadSavedDonors() }
    var upazila by rememberSaveable { mutableStateOf("") }
    var nearMe by rememberSaveable { mutableStateOf(true) }
    var target by remember { mutableStateOf<Donor?>(null) }
    var showEmergency by remember { mutableStateOf(false) }
    var savedOnly by rememberSaveable { mutableStateOf(false) }
    var visible by rememberSaveable { mutableStateOf(24) }

    target?.let { donor ->
        RequestDialog(
            title = "${t("Request")}: ${donor.fullName}",
            defaultBloodGroup = donor.bloodGroup,
            defaultDistrict = vm.user?.district.orEmpty(),
            defaultUpazila = vm.user?.upazila.orEmpty(),
            targetDonorUid = donor.id,
            emergency = false,
            busy = vm.busy,
            error = vm.error,
            onSubmit = { body -> vm.createRequest(body) { target = null } },
            onDismiss = { target = null; vm.clearError() },
        )
    }
    if (showEmergency) {
        RequestDialog(
            title = t("Emergency request"),
            defaultBloodGroup = bloodGroup,
            defaultDistrict = vm.user?.district.orEmpty(),
            defaultUpazila = vm.user?.upazila.orEmpty(),
            targetDonorUid = null,
            emergency = true,
            busy = vm.busy,
            error = vm.error,
            onSubmit = { body -> vm.createRequest(body) { showEmergency = false } },
            onDismiss = { showEmergency = false; vm.clearError() },
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 12.dp)) {
                StatsRow(vm, "seeker")
                Text("${t("Hello,")} ${vm.user?.fullName?.substringBefore(' ').orEmpty()}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                DropdownField(t("Blood group"), bloodGroup, Constants.bloodGroups, { bloodGroup = it }, anyLabel = t("Any"))
                DropdownField(t("District"), district, Constants.districts, { district = it }, anyLabel = t("Any"))
                com.bloodbridge.app.ui.AppTextField(upazila, { upazila = it }, t("Upazila"))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text(t("Near me first"), modifier = Modifier.weight(1f))
                    Switch(checked = nearMe, onCheckedChange = { nearMe = it })
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text(t("Show saved donors only"), modifier = Modifier.weight(1f))
                    Switch(checked = savedOnly, onCheckedChange = { savedOnly = it; if (it) vm.loadSavedDonors() })
                }
                if (target == null && !showEmergency) ErrorBanner(vm.error)
                PrimaryButton(t("Search"), vm.busy, { savedOnly = false; visible = 24; vm.search(bloodGroup, district, upazila.trim(), nearMe) })
                Button(onClick = { vm.clearError(); showEmergency = true }, modifier = Modifier.fillMaxWidth().height(46.dp)) {
                    Text(t("🚨 Emergency request (nearest 5 donors)"))
                }
            }
        }

        val shown = if (savedOnly) vm.savedDonors else vm.donors
        if ((savedOnly || vm.searched) && shown.isEmpty() && !vm.busy) {
            item {
                Text(
                    if (savedOnly) t("You haven't saved any donors yet.") else t("No available donors match these filters."),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 24.dp),
                )
            }
        }

        items(shown.take(visible), key = { it.id }) { donor ->
            DonorCard(
                donor = donor,
                saved = vm.user?.savedDonors?.contains(donor.id) == true,
                onToggleSave = { vm.toggleSaved(donor) },
                onRequest = { vm.clearError(); target = donor },
            )
        }
        if (shown.size > visible) {
            item {
                OutlinedButton(onClick = { visible += 24 }, modifier = Modifier.fillMaxWidth()) { Text(t("Show more")) }
            }
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun DonorCard(donor: Donor, saved: Boolean, onToggleSave: () -> Unit, onRequest: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.clip(CircleShape).background(Brand).padding(12.dp),
                    contentAlignment = Alignment.Center,
                ) { Text(donor.bloodGroup, color = Color.White, fontWeight = FontWeight.Bold) }
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        donor.fullName + if (donor.isVerified) "  ✓" else "",
                        fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium,
                    )
                    val place = listOf(donor.upazila, lbl(donor.district)).filter { it.isNotBlank() }.joinToString(", ")
                    Text(place, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                    val extra = buildList {
                        donor.age?.let { add("$it ${t("yrs")}") }
                        donor.distanceKm?.let { add("${it.roundToInt()} ${t("km away")}") }
                    }.joinToString(" • ")
                    if (extra.isNotBlank()) Text(extra, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onRequest, enabled = donor.isAvailable, modifier = Modifier.weight(1f)) { Text(if (donor.isAvailable) t("Request blood") else t("Unavailable")) }
                OutlinedButton(onClick = onToggleSave) { Text(if (saved) "♥ " + t("Saved") else "♡ " + t("Save")) }
            }
        }
    }
}
