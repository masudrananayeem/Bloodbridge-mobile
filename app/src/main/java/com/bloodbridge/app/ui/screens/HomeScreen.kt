package com.bloodbridge.app.ui.screens

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(vm: AppViewModel) {
    var bloodGroup by rememberSaveable { mutableStateOf("") }
    var district by rememberSaveable { mutableStateOf("") }
    var upazila by rememberSaveable { mutableStateOf("") }
    var nearMe by rememberSaveable { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Find donors", fontWeight = FontWeight.Bold) },
                actions = { TextButton(onClick = { vm.logout() }) { Text("Log out") } },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 8.dp)) {
                    Text(
                        "Hello, ${vm.user?.fullName?.substringBefore(' ').orEmpty()}",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    DropdownField("Blood group", bloodGroup, Constants.bloodGroups, { bloodGroup = it }, anyLabel = "Any")
                    DropdownField("District", district, Constants.districts, { district = it }, anyLabel = "Any")
                    com.bloodbridge.app.ui.AppTextField(upazila, { upazila = it }, "Upazila")
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Text("Near me first", modifier = Modifier.weight(1f))
                        Switch(checked = nearMe, onCheckedChange = { nearMe = it })
                    }
                    ErrorBanner(vm.error)
                    PrimaryButton("Search", vm.busy, { vm.search(bloodGroup, district, upazila.trim(), nearMe) })
                }
            }

            if (vm.searched && vm.donors.isEmpty() && !vm.busy) {
                item {
                    Text(
                        "No available donors match these filters.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 24.dp),
                    )
                }
            }

            items(vm.donors, key = { it.id }) { donor -> DonorCard(donor) }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun DonorCard(donor: Donor) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.clip(CircleShape).background(Brand).padding(horizontal = 12.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(donor.bloodGroup, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    donor.fullName + if (donor.isVerified) "  ✓" else "",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleMedium,
                )
                val place = listOf(donor.upazila, donor.district).filter { it.isNotBlank() }.joinToString(", ")
                Text(place, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                val extra = buildList {
                    donor.age?.let { add("$it yrs") }
                    donor.distanceKm?.let { add("${it.roundToInt()} km away") }
                }.joinToString(" • ")
                if (extra.isNotBlank()) {
                    Text(extra, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
