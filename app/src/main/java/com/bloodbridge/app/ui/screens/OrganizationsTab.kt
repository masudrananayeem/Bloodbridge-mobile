package com.bloodbridge.app.ui.screens

import com.bloodbridge.app.i18n.lbl
import com.bloodbridge.app.i18n.t
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bloodbridge.app.data.Constants
import com.bloodbridge.app.data.Organization
import com.bloodbridge.app.ui.AppViewModel
import com.bloodbridge.app.ui.DropdownField
import com.bloodbridge.app.ui.ErrorBanner
import com.bloodbridge.app.ui.AppTextField

@Composable
fun OrganizationsTab(vm: AppViewModel) {
    var district by rememberSaveable { mutableStateOf("") }
    var type by rememberSaveable { mutableStateOf("") }
    var query by rememberSaveable { mutableStateOf("") }
    LaunchedEffect(district, type) { vm.loadOrganizations(district, type, query) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 14.dp)) {
                Text(t("Organizations"), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                Text(t("Find blood donor clubs, voluntary groups and blood banks."), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                AppTextField(query, { query = it }, t("Search organizations"))
                DropdownField(t("District"), district, Constants.districts, { district = it }, anyLabel = t("Any"))
                DropdownField(t("Type"), type, Constants.orgTypes, { type = it }, anyLabel = t("Any"))
                Button(onClick = { vm.loadOrganizations(district, type, query.trim()) }, enabled = !vm.busy, modifier = Modifier.fillMaxWidth()) { Text(t("Search")) }
                ErrorBanner(vm.error)
            }
        }
        if (vm.organizations.isEmpty() && !vm.busy) {
            item { Text(t("No organizations found."), color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
        items(vm.organizations, key = { it.id }) { org -> OrgCard(org) }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun OrgCard(org: Organization) {
    val context = LocalContext.current
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(org.name + if (org.isVerified) "  ✓" else "", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(lbl(org.type), color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
            if (org.description.isNotBlank()) Text(org.description, style = MaterialTheme.typography.bodyMedium)
            val place = listOf(org.address, org.upazila, lbl(org.district)).filter { it.isNotBlank() }.joinToString(", ")
            if (place.isNotBlank()) Text(place, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
            if (org.email.isNotBlank()) {
                Text("✉  ${org.email}", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
            }
            if (org.phone.isNotBlank()) {
                OutlinedButton(
                    onClick = { context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${org.phone}"))) },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("${t("Call")} ${org.phone}") }
            }
            val website = org.website.trim().let { raw ->
                when {
                    raw.isBlank() -> ""
                    raw.startsWith("https://", true) || raw.startsWith("http://", true) -> raw
                    else -> "https://$raw"
                }
            }
            if (website.isNotBlank()) {
                TextButton(
                    onClick = { runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(website))) } },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("🌐  ${t("Visit website")}", fontWeight = FontWeight.SemiBold) }
            }
        }
    }
}
