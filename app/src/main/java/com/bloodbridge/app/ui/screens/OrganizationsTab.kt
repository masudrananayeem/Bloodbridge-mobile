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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bloodbridge.app.data.Constants
import com.bloodbridge.app.data.Organization
import com.bloodbridge.app.ui.AppViewModel
import com.bloodbridge.app.ui.DropdownField
import com.bloodbridge.app.ui.AppTextField
import com.bloodbridge.app.ui.ErrorBanner

@Composable
fun OrganizationsTab(vm: AppViewModel) {
    var district by rememberSaveable { mutableStateOf("") }
    var type by rememberSaveable { mutableStateOf("") }
    var search by rememberSaveable { mutableStateOf("") }
    LaunchedEffect(district, type, search) {
        delay(300)
        vm.loadOrganizations(district, type, search.trim())
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 12.dp)) {
                DropdownField(t("District"), district, Constants.districts, { district = it }, anyLabel = t("Any"))
                DropdownField(t("Type"), type, Constants.orgTypes, { type = it }, anyLabel = t("Any"))
                AppTextField(search, { search = it }, t("Search organizations"))
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
            if (org.phone.isNotBlank()) {
                OutlinedButton(
                    onClick = { context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${org.phone}"))) },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("${t("Call")} ${org.phone}") }
            }
            if (org.email.isNotBlank()) {
                OutlinedButton(
                    onClick = { context.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${org.email}"))) },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text("${t("Email")} ${org.email}") }
            }
            if (org.website.isNotBlank()) {
                OutlinedButton(
                    onClick = {
                        val url = if (org.website.startsWith("http://") || org.website.startsWith("https://")) org.website else "https://${org.website}"
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) { Text(t("Visit website")) }
            }
        }
    }
}
