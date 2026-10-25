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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bloodbridge.app.data.BloodRequest
import com.bloodbridge.app.data.shortDate
import com.bloodbridge.app.ui.AppViewModel
import com.bloodbridge.app.ui.ErrorBanner

private fun statusColor(status: String): Color = when (status) {
    "accepted", "completed" -> Color(0xFF16A34A)
    "cancelled", "rejected" -> Color(0xFF6B7280)
    else -> Color(0xFFD97706)
}

@Composable
fun RequestsTab(vm: AppViewModel) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    LaunchedEffect(Unit) { vm.loadRequests() }
    val titles = listOf("My requests", "Incoming (${vm.incoming.size})", "Nearby")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = tab) {
            titles.forEachIndexed { i, title ->
                Tab(selected = tab == i, onClick = { tab = i }, text = { Text(title, maxLines = 1) })
            }
        }
        ErrorBanner(vm.error)
        val list = when (tab) { 0 -> vm.myRequests; 1 -> vm.incoming; else -> vm.nearby }
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item { Spacer(Modifier.height(4.dp)) }
            if (list.isEmpty() && !vm.busy) {
                item {
                    Text(
                        when (tab) { 0 -> "You haven't sent any requests yet."; 1 -> "No incoming requests right now."; else -> "No nearby requests." },
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 24.dp),
                    )
                }
            }
            items(list, key = { it.id }) { req ->
                RequestCard(
                    req = req,
                    mode = tab,
                    busy = vm.busy,
                    onCancel = { vm.cancelRequest(req.id) },
                    onAccept = { vm.respond(req.id, "accept") },
                    onReject = { vm.respond(req.id, "reject") },
                )
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun RequestCard(
    req: BloodRequest, mode: Int, busy: Boolean,
    onCancel: () -> Unit, onAccept: () -> Unit, onReject: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    (if (req.isEmergency) "🚨 " else "") + "${req.bloodGroup} · ${req.units} bag(s)",
                    fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium,
                )
                Text(req.status.replaceFirstChar { it.uppercase() }, color = statusColor(req.status), fontWeight = FontWeight.SemiBold)
            }
            Text("${req.hospital}, ${req.upazila}, ${req.district}")
            Text(
                "Urgency: ${req.urgency} • Needed by ${shortDate(req.neededByDate)}",
                color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall,
            )
            if (mode != 0) {
                Text("From: ${req.seekerName}", style = MaterialTheme.typography.bodyMedium)
            }
            if (req.message.isNotBlank()) Text("“${req.message}”", style = MaterialTheme.typography.bodyMedium)
            if (mode == 0 && !req.targetDonorName.isNullOrBlank()) Text("To: ${req.targetDonorName}")
            if (mode == 0 && req.status == "accepted") {
                Text(
                    "Accepted by ${req.acceptedDonorName.orEmpty()} — ${req.acceptedDonorPhone.orEmpty()}",
                    color = statusColor("accepted"), fontWeight = FontWeight.SemiBold,
                )
            }
            req.distanceKm?.let { if (mode == 2) Text("${it.toInt()} km away", style = MaterialTheme.typography.bodySmall) }

            if (mode == 0 && (req.status == "pending" || req.status == "accepted")) {
                OutlinedButton(onClick = onCancel, enabled = !busy, modifier = Modifier.fillMaxWidth()) { Text("Cancel request") }
            }
            if (mode == 1) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = onAccept, enabled = !busy, modifier = Modifier.weight(1f)) { Text("Accept") }
                    OutlinedButton(onClick = onReject, enabled = !busy, modifier = Modifier.weight(1f)) { Text("Decline") }
                }
            }
        }
    }
}
