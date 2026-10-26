package com.bloodbridge.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.background
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
import com.bloodbridge.app.data.Organization
import com.bloodbridge.app.data.OrgBody
import com.bloodbridge.app.ui.ThemeToggle
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

private val adminTabs = listOf("Overview" to "📊", "Users" to "👥", "Requests" to "🩸", "Orgs" to "🏥", "Profile" to "👤")

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
                3 -> AdminOrgs(vm)
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
    LaunchedEffect(Unit) { vm.loadAdminStats(); vm.loadAnalytics() }
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
        BarSection(t("Blood group distribution"), vm.analytics.bloodGroupDistribution.map { it.bloodGroup to it.count })
        BarSection(t("Request status"), vm.analytics.requestStatusBreakdown.map { lbl(it.status) to it.count })
    }
}

@Composable
private fun BarSection(title: String, rows: List<Pair<String, Int>>) {
    if (rows.isEmpty()) return
    val max = (rows.maxOf { it.second }).coerceAtLeast(1)
    Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(title, fontWeight = FontWeight.SemiBold)
            rows.forEach { (label, n) ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(label, modifier = Modifier.width(84.dp), style = MaterialTheme.typography.bodySmall)
                    Box(Modifier.weight(1f).height(10.dp)) {
                        Box(Modifier.fillMaxWidth(n.toFloat() / max).height(10.dp).background(MaterialTheme.colorScheme.primary, RoundedCornerShape(5.dp)))
                    }
                    Text(n.toString(), modifier = Modifier.padding(start = 8.dp), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Composable
private fun AdminOrgs(vm: AppViewModel) {
    LaunchedEffect(Unit) { vm.loadAdminOrgs() }
    var editing by remember { mutableStateOf<Organization?>(null) }
    var creating by remember { mutableStateOf(false) }
    var toDelete by remember { mutableStateOf<Organization?>(null) }
    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(t("Organizations"), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Button(onClick = { creating = true }) { Text("+ " + t("Add")) }
            }
            ErrorBanner(vm.error)
        }
        items(vm.organizations, key = { it.id }) { o ->
            Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(o.name + if (o.isVerified) "  ✓" else "", fontWeight = FontWeight.SemiBold)
                    Text(lbl(o.type) + " · " + lbl(o.district), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(o.phone, style = MaterialTheme.typography.bodySmall)
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (!o.isVerified) TextButton(onClick = { vm.verifyOrg(o.id) }, enabled = !vm.busy) { Text(t("Verify")) }
                        TextButton(onClick = { editing = o }) { Text(t("Edit")) }
                        TextButton(onClick = { toDelete = o }) { Text(t("Delete")) }
                    }
                }
            }
        }
    }
    if (creating || editing != null) {
        OrgDialog(vm, editing, onDismiss = { creating = false; editing = null })
    }
    toDelete?.let { o ->
        AlertDialog(
            onDismissRequest = { toDelete = null },
            title = { Text(t("Delete")) },
            text = { Text(o.name) },
            confirmButton = { TextButton(onClick = { vm.deleteOrg(o.id); toDelete = null }) { Text(t("Delete")) } },
            dismissButton = { TextButton(onClick = { toDelete = null }) { Text(t("Cancel")) } },
        )
    }
}

@Composable
private fun OrgDialog(vm: AppViewModel, org: Organization?, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf(org?.name.orEmpty()) }
    var type by remember { mutableStateOf(org?.type?.ifBlank { null } ?: Constants.orgTypes.first()) }
    var district by remember { mutableStateOf(org?.district.orEmpty()) }
    var upazila by remember { mutableStateOf(org?.upazila.orEmpty()) }
    var address by remember { mutableStateOf(org?.address.orEmpty()) }
    var phone by remember { mutableStateOf(org?.phone.orEmpty()) }
    var email by remember { mutableStateOf(org?.email.orEmpty()) }
    var website by remember { mutableStateOf(org?.website.orEmpty()) }
    var description by remember { mutableStateOf(org?.description.orEmpty()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (org == null) t("Add organization") else t("Edit organization")) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { ErrorBanner(vm.error) }
                item { AppTextField(name, { name = it }, t("Name")) }
                item { DropdownField(t("Type"), type, Constants.orgTypes, { type = it }) }
                item { DropdownField(t("District"), district, Constants.districts, { district = it }) }
                item { AppTextField(upazila, { upazila = it }, t("Upazila")) }
                item { AppTextField(address, { address = it }, t("Address")) }
                item { AppTextField(phone, { phone = it }, t("Phone")) }
                item { AppTextField(email, { email = it }, t("Email")) }
                item { AppTextField(website, { website = it }, t("Website")) }
                item { AppTextField(description, { description = it }, t("Description")) }
            }
        },
        confirmButton = {
            TextButton(
                enabled = !vm.busy,
                onClick = {
                    vm.saveOrg(org?.id, OrgBody(name.trim(), type, description.trim(), district, upazila.trim(), address.trim(), phone.trim(), email.trim(), website.trim()), onDismiss)
                },
            ) { Text(t("Save")) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(t("Cancel")) } },
    )
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(t("Theme"), modifier = Modifier.padding(end = 8.dp))
            ThemeToggle()
        }
        OutlinedButton(onClick = { vm.logout() }, modifier = Modifier.fillMaxWidth()) { Text(t("Log out")) }
    }
}
