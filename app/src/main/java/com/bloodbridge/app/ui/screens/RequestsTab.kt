package com.bloodbridge.app.ui.screens

import com.bloodbridge.app.i18n.lbl
import com.bloodbridge.app.i18n.t
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
    var tab by rememberSaveable { mutableIntStateOf(if (vm.user?.activeMode == "donor") 1 else 0) }
    LaunchedEffect(Unit) { vm.loadRequests() }
    val titles = listOf(t("My requests"), "${t("Incoming")} (${vm.incoming.size})", t("Nearby"))

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
                        when (tab) { 0 -> t("You haven't sent any requests yet."); 1 -> t("No incoming requests right now."); else -> t("No nearby requests.") },
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
                    (if (req.isEmergency) "🚨 " else "") + "${req.bloodGroup} · ${req.units} ${t("bag(s)")}",
                    fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium,
                )
                Text(lbl(req.status), color = statusColor(req.status), fontWeight = FontWeight.SemiBold)
            }
            Text("${req.hospital}, ${req.upazila}, ${lbl(req.district)}")
            Text(
                "${t("Urgency:")} ${lbl(req.urgency)} • ${t("Needed by")} ${shortDate(req.neededByDate)}",
                color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall,
            )
            if (mode != 0) {
                Text("${t("From:")} ${req.seekerName}" + if (req.seekerPhone.isNotBlank()) " · ${req.seekerPhone}" else "", style = MaterialTheme.typography.bodyMedium)
                if (!req.targetDonorUid.isNullOrBlank()) Text(t("Direct request"), color = Color(0xFF7C3AED), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodySmall)
            }
            if (req.reason.isNotBlank()) Text("${t("Reason")}: ${req.reason}", style = MaterialTheme.typography.bodySmall)
            if (req.message.isNotBlank()) Text("“${req.message}”", style = MaterialTheme.typography.bodyMedium)
            if (mode == 0 && !req.targetDonorName.isNullOrBlank()) Text("${t("To:")} ${req.targetDonorName}")
            if (mode == 0 && req.status == "accepted") {
                Text(
                    "${t("Accepted by")} ${req.acceptedDonorName.orEmpty()} — ${req.acceptedDonorPhone.orEmpty()}" + (req.acceptedDonorEmail?.takeIf { it.isNotBlank() }?.let { ", $it" } ?: ""),
                    color = statusColor("accepted"), fontWeight = FontWeight.SemiBold,
                )
            }
            if (mode == 0 && req.status == "rejected") Text(t("No donor accepted this request."), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            req.distanceKm?.let { if (mode == 2) Text("${it.toInt()} ${t("km away")}", style = MaterialTheme.typography.bodySmall) }

            if (mode == 0 && (req.status == "pending" || req.status == "accepted")) {
                OutlinedButton(onClick = onCancel, enabled = !busy, modifier = Modifier.fillMaxWidth()) { Text(t("Cancel request")) }
            }
            if (mode == 1) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = onAccept, enabled = !busy, modifier = Modifier.weight(1f)) { Text(t("Accept")) }
                    OutlinedButton(onClick = onReject, enabled = !busy, modifier = Modifier.weight(1f)) { Text(t("Decline")) }
                }
            }
        }
    }
}
