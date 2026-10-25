package com.bloodbridge.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bloodbridge.app.data.daysUntilEligible
import com.bloodbridge.app.data.nextEligibleLabel
import com.bloodbridge.app.data.shortDate
import com.bloodbridge.app.ui.AppViewModel
import com.bloodbridge.app.ui.BloodLevelBody
import com.bloodbridge.app.ui.ErrorBanner

@Composable
fun DonorTab(vm: AppViewModel) {
    LaunchedEffect(Unit) { vm.loadDonorDashboard() }
    val user = vm.user
    var confirming by rememberSaveable { mutableStateOf(false) }
    val daysLeft = daysUntilEligible(user?.lastDonationDate)
    val cooldown = daysLeft > 0
    val isAvailable = user?.isAvailable ?: true

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item { Spacer(Modifier.height(8.dp)) }
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Available to donate", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                        Text(
                            "When off, you are hidden from donor search and new requests.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    Switch(checked = isAvailable, enabled = !vm.busy, onCheckedChange = { vm.setAvailability(it) })
                }
            }
        }
        item { ErrorBanner(vm.error) }
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (cooldown) {
                            Text("You donated recently", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                            Text(
                                "For your safety you can donate again in $daysLeft days (${nextEligibleLabel(user?.lastDonationDate).orEmpty()}).",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        } else {
                            Text("Did you donate blood today?", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Recording it adds this to your Donation History and automatically makes you Unavailable for 120 days.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall,
                            )
                            if (!confirming) {
                                Button(onClick = { confirming = true }) { Text("I donated today") }
                            } else {
                                Text("Confirm you donated blood today?", style = MaterialTheme.typography.bodyMedium)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(enabled = !vm.busy, onClick = { vm.recordDonation(); confirming = false }) { Text("Yes") }
                                    OutlinedButton(onClick = { confirming = false }) { Text("Cancel") }
                                }
                            }
                        }
                    }
                    BloodLevelBody(lastDonationDate = user?.lastDonationDate)
                }
            }
        }
        item { Text("Donation history", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium) }
        if (vm.history.isEmpty()) {
            item { Text("No donations recorded yet.", color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
        items(vm.history, key = { it.id }) { d ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("${d.bloodGroup} · ${d.units} bag(s)", fontWeight = FontWeight.SemiBold)
                    Text("${d.hospital}, ${d.district}", style = MaterialTheme.typography.bodyMedium)
                    Text(shortDate(d.donationDate), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}
