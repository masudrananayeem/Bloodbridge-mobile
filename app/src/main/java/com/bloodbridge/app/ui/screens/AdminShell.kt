package com.bloodbridge.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bloodbridge.app.data.BloodRequest
import com.bloodbridge.app.data.Constants
import com.bloodbridge.app.data.User
import com.bloodbridge.app.data.shortDate
import com.bloodbridge.app.i18n.lbl
import com.bloodbridge.app.i18n.t
import com.bloodbridge.app.ui.AppTextField
import com.bloodbridge.app.ui.AppViewModel
import com.bloodbridge.app.ui.DropdownField
import com.bloodbridge.app.ui.ErrorBanner
import com.bloodbridge.app.ui.LanguageToggle

private val adminTabs = listOf("Overview" to "📊", "Users" to "👥", "Requests" to "🩸", "Profile" to "👤")

@Composable
fun AdminShell(vm: AppViewModel) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    val snackbar = remember { SnackbarHostState() }
    LaunchedEffect(vm.info) {
        vm.info?.let { snackbar.showSnackbar(it); vm.clearInfo() }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = {
            NavigationBar {
                adminTabs.forEachIndexed { i, (label, icon) ->
                    NavigationBarItem(
                        selected = tab == i,
                        onClick = { vm.clearError(); tab = i },
                        icon = { Text(icon) },
                        label = { Text(t(label)) },
                    )
                }
            }
        },
    ) { padding ->
        Box(Modifier.padding(padding)) {
            when (tab) {
                0 -> AdminOverview(vm)
                1 -> AdminUsers(vm)
                2 -> AdminRequests(vm)
                else -> AdminAccount(vm)
            }
        }
    }
}

@Composable
private fun StatCard(title: String, value: Int, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(value.toString(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(title, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun AdminOverview(vm: AppViewModel) {
    LaunchedEffect(Unit) { vm.loadAdminStats() }
    val s = vm.adminStats
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(t("Admin"), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        ErrorBanner(vm.error)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            StatCard(t("Total users"), s.totalUsers, Modifier.weight(1f))
            StatCard(t("Donors (active)"), s.totalDonors, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            StatCard(t("Seekers"), s.totalSeekers, Modifier.weight(1f))
            StatCard(t("Pending requests"), s.pendingRequests, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            StatCard(t("Completed donations"), s.completedDonations, Modifier.weight(1f))
            StatCard(t("Organizations count"), s.totalOrganizations, Modifier.weight(1f))
        }
        StatCard(t("Incomplete profiles"), s.incompleteProfiles, Modifier.fillMaxWidth())
    }
}

@Composable
private fun AdminUsers(vm: AppViewModel) {
    var search by rememberSaveable { mutableStateOf("") }
    var toDelete by remember { mutableStateOf<User?>(null) }
    LaunchedEffect(search) { vm.loadAdminUsers(search) }

    toDelete?.let { u ->
        AlertDialog(
            onDismissRequest = { toDelete = null },
            title = { Text(t("Delete user?")) },
            text = { Text("${u.fullName}\n${t("This permanently deletes the user and all their data.")}") },
            confirmButton = {
                TextButton(onClick = { vm.adminDeleteUser(u.id, search); toDelete = null }) {
                    Text(t("Delete"), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { toDelete = null }) { Text(t("Cancel")) } },
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            Column(modifier = Modifier.padding(top = 12.dp)) {
                AppTextField(search, { search = it }, t("Search name, email or phone"))
                ErrorBanner(vm.error)
            }
        }
        if (vm.adminUsers.isEmpty() && !vm.busy) item { Text(t("No users found."), color = MaterialTheme.colorScheme.onSurfaceVariant) }
        items(vm.adminUsers, key = { it.id }) { u ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(u.fullName + if (u.isVerified) "  ✓" else "", fontWeight = FontWeight.SemiBold)
                    Text("${u.email} • ${u.phone}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${u.bloodGroup} • ${lbl(u.district)}", style = MaterialTheme.typography.bodySmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { vm.adminVerify(u.id, search) }, enabled = !vm.busy) {
                            Text(if (u.isVerified) t("Unverify") else t("Verify"))
                        }
                        TextButton(onClick = { toDelete = u }) { Text(t("Delete"), color = MaterialTheme.colorScheme.error) }
                    }
                }
            }
        }
        if (vm.adminUsersPage < vm.adminUsersPages) {
            item {
                Button(onClick = { vm.loadAdminUsers(search, loadMore = true) }, enabled = !vm.busy, modifier = Modifier.fillMaxWidth()) {
                    Text(t("Load more"))
                }
            }
        }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun AdminRequests(vm: AppViewModel) {
    var filter by rememberSaveable { mutableStateOf("") }
    LaunchedEffect(filter) { vm.loadAdminRequests(filter) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            Column(modifier = Modifier.padding(top = 12.dp)) {
                DropdownField(t("Status"), filter, Constants.requestStatuses, { filter = it }, anyLabel = t("All"))
                ErrorBanner(vm.error)
            }
        }
        if (vm.adminRequests.isEmpty() && !vm.busy) item { Text(t("No requests found."), color = MaterialTheme.colorScheme.onSurfaceVariant) }
        items(vm.adminRequests, key = { it.id }) { r -> AdminRequestCard(r, vm.busy) { status -> vm.adminSetStatus(r.id, status, filter) } }
        item { Spacer(Modifier.height(24.dp)) }
    }
}

@Composable
private fun AdminRequestCard(r: BloodRequest, busy: Boolean, onStatus: (String) -> Unit) {
    var menu by remember { mutableStateOf(false) }
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("${if (r.isEmergency) "🚨 " else ""}${r.bloodGroup} · ${r.units} ${t("bag(s)")} — ${lbl(r.status)}", fontWeight = FontWeight.SemiBold)
            Text("${r.hospital}, ${lbl(r.district)}", style = MaterialTheme.typography.bodyMedium)
            Text("${t("From:")} ${r.seekerName} • ${shortDate(r.createdAt)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Box {
                OutlinedButton(onClick = { menu = true }, enabled = !busy) { Text(t("Status")) }
                androidx.compose.material3.DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                    Constants.requestStatuses.forEach { st ->
                        androidx.compose.material3.DropdownMenuItem(text = { Text(lbl(st)) }, onClick = { menu = false; onStatus(st) })
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminAccount(vm: AppViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalAlignment = Alignment.Start) {
        Text(vm.user?.fullName.orEmpty(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(vm.user?.email.orEmpty(), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(t("Language"), modifier = Modifier.padding(end = 8.dp))
            LanguageToggle()
        }
        OutlinedButton(onClick = { vm.logout() }, modifier = Modifier.fillMaxWidth()) { Text(t("Log out")) }
    }
}
