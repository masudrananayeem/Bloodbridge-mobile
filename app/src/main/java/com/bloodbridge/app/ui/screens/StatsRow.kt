package com.bloodbridge.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bloodbridge.app.data.ageFromDob
import com.bloodbridge.app.data.shortDate
import com.bloodbridge.app.i18n.lbl
import com.bloodbridge.app.i18n.t
import com.bloodbridge.app.ui.AppViewModel

@Composable
private fun Stat(label: String, value: String, modifier: Modifier = Modifier) {
    Card(shape = RoundedCornerShape(14.dp), modifier = modifier) {
        Column(Modifier.padding(12.dp)) {
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/** Same numbers as the web "Overview" pages. mode = "donor" or "seeker". */
@Composable
fun StatsRow(vm: AppViewModel, mode: String) {
    val u = vm.user
    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        if (mode == "donor") {
            val badges = buildList {
                add(if (u?.isAvailable == true) t("Available") else t("Unavailable"))
                if (u?.isVerified == true) add("✓ " + t("Verified"))
                ageFromDob(u?.dateOfBirth)?.let { add("$it ${t("yrs")}") }
            }.joinToString(" • ")
            Text(badges, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Stat(t("Blood group"), u?.bloodGroup?.ifBlank { "—" } ?: "—", Modifier.weight(1f))
                Stat(t("Pending requests"), vm.incoming.size.toString(), Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Stat(t("Total donations"), vm.history.size.toString(), Modifier.weight(1f))
                Stat(t("Last donation"), u?.lastDonationDate?.let { shortDate(it) } ?: "N/A", Modifier.weight(1f))
            }
        } else {
            val active = vm.myRequests.count { it.status == "pending" || it.status == "accepted" }
            val emergency = vm.myRequests.count { it.urgency == "High" }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Stat(t("Active requests"), active.toString(), Modifier.weight(1f))
                Stat(t("Saved donors"), vm.savedDonors.size.toString(), Modifier.weight(1f))
                Stat(t("Emergency requests"), emergency.toString(), Modifier.weight(1f))
            }
        }
    }
}
