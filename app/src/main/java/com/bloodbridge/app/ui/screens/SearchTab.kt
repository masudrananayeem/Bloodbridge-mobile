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
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.layout.ContentScale
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
    LaunchedEffect(Unit) { vm.loadSearchScreen() }
    var upazila by rememberSaveable { mutableStateOf("") }
    var nearMe by rememberSaveable { mutableStateOf(true) }
    var target by remember { mutableStateOf<Donor?>(null) }
    var showEmergency by remember { mutableStateOf(false) }
    var savedOnly by rememberSaveable { mutableStateOf(false) }

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
                Text(t("Find compatible blood donors near you"), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                DropdownField(t("Blood group"), bloodGroup, Constants.bloodGroups, { bloodGroup = it }, anyLabel = t("Any"))
                DropdownField(t("District"), district, Constants.districts, { district = it }, anyLabel = t("Any"))
                com.bloodbridge.app.ui.AppTextField(upazila, { upazila = it }, t("Upazila"))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text(t("Nearest first"), modifier = Modifier.weight(1f))
                    Switch(checked = nearMe, onCheckedChange = { nearMe = it })
                }
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text(t("Show saved donors only"), modifier = Modifier.weight(1f))
                    Switch(checked = savedOnly, onCheckedChange = { savedOnly = it; if (it) vm.loadSavedDonors() })
                }
                if (target == null && !showEmergency) ErrorBanner(vm.error)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    PrimaryButton(t("Search"), vm.busy, { savedOnly = false; vm.search(bloodGroup, district, upazila.trim(), nearMe && district.isBlank(), 1) }, Modifier.weight(1f))
                    OutlinedButton(onClick = { bloodGroup = ""; district = ""; upazila = ""; nearMe = true; savedOnly = false; vm.search("", "", "", true, 1) }, enabled = !vm.busy, modifier = Modifier.height(50.dp)) { Text(t("Reset")) }
                }
                Button(onClick = { vm.clearError(); showEmergency = true }, modifier = Modifier.fillMaxWidth().height(46.dp)) {
                    Text(t("🚨 Emergency request (nearest 5 donors)"))
                }
            }
        }

        val shown = if (savedOnly) vm.savedDonors else vm.donors
        if (vm.searched && !savedOnly) {
            item { Text("${vm.donorTotal} ${t("donors found")}", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall) }
        }
        if ((savedOnly || vm.searched) && shown.isEmpty() && !vm.busy) {
            item {
                Text(
                    if (savedOnly) t("You haven't saved any donors yet.") else t("No available donors match these filters."),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 24.dp),
                )
            }
        }

        items(shown, key = { it.id }) { donor ->
            DonorCard(
                donor = donor,
                saved = vm.user?.savedDonors?.contains(donor.id) == true,
                onToggleSave = { vm.toggleSaved(donor) },
                onRequest = { vm.clearError(); target = donor },
            )
        }
        if (!savedOnly && vm.searched && vm.donorPages > 1) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = { vm.search(bloodGroup, district, upazila.trim(), nearMe && district.isBlank(), vm.donorPage - 1) }, enabled = !vm.busy && vm.donorPage > 1, modifier = Modifier.weight(1f)) { Text(t("Previous")) }
                    Text("${vm.donorPage} / ${vm.donorPages}", modifier = Modifier.padding(top = 14.dp), style = MaterialTheme.typography.bodyMedium)
                    OutlinedButton(onClick = { vm.search(bloodGroup, district, upazila.trim(), nearMe && district.isBlank(), vm.donorPage + 1) }, enabled = !vm.busy && vm.donorPage < vm.donorPages, modifier = Modifier.weight(1f)) { Text(t("Next")) }
                }
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
                if (donor.photoURL.isNotBlank()) {
                    coil.compose.AsyncImage(
                        model = donor.photoURL,
                        contentDescription = "${donor.fullName} profile photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(52.dp).clip(CircleShape),
                    )
                } else {
                    Box(
                        modifier = Modifier.size(52.dp).clip(CircleShape).background(Brand),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(donor.fullName.firstOrNull()?.uppercase() ?: "?", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(Modifier.width(10.dp))
                Box(
                    modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(Brand).padding(horizontal = 9.dp, vertical = 7.dp),
                    contentAlignment = Alignment.Center,
                ) { Text(donor.bloodGroup, color = Color.White, fontWeight = FontWeight.Bold) }
                Spacer(Modifier.width(10.dp))
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
                    if (donor.daysUntilEligible > 0) {
                        Text(
                            "${t("Eligible to donate in")} ${donor.daysUntilEligible} ${t("days")}",
                            color = MaterialTheme.colorScheme.tertiary,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onRequest, enabled = donor.isAvailable, modifier = Modifier.weight(1f)) { Text(if (donor.isAvailable) t("Request blood") else t("Unavailable")) }
                OutlinedButton(onClick = onToggleSave) { Text(if (saved) "♥ " + t("Saved") else "♡ " + t("Save")) }
            }
        }
    }
}
