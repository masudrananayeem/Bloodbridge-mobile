package com.bloodbridge.app.ui.screens

import com.bloodbridge.app.i18n.t
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bloodbridge.app.data.shortDate
import com.bloodbridge.app.ui.AppViewModel
import com.bloodbridge.app.ui.ErrorBanner

@Composable
fun AlertsTab(vm: AppViewModel) {
    LaunchedEffect(Unit) { vm.loadNotifications() }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            Column {
                Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(t("Notifications"), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    if (vm.unreadCount > 0) TextButton(onClick = { vm.markAllRead() }) { Text(t("Mark all read")) }
                }
                ErrorBanner(vm.error)
            }
        }
        if (vm.notifications.isEmpty() && !vm.busy) {
            item { Text(t("No notifications yet."), color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }
        items(vm.notifications, key = { it.id }) { n ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (n.isRead) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primaryContainer,
                ),
                modifier = Modifier.fillMaxWidth().clickable(enabled = !n.isRead) { vm.markRead(n.id) },
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(n.message, style = MaterialTheme.typography.bodyMedium, fontWeight = if (n.isRead) FontWeight.Normal else FontWeight.SemiBold)
                    Text(shortDate(n.createdAt), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
