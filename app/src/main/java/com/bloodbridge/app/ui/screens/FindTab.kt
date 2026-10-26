package com.bloodbridge.app.ui.screens

import com.bloodbridge.app.i18n.t
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.bloodbridge.app.ui.AppViewModel

@Composable
fun FindTab(vm: AppViewModel) {
    var sub by rememberSaveable { mutableIntStateOf(0) }
    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = sub) {
            Tab(selected = sub == 0, onClick = { vm.clearError(); sub = 0 }, text = { Text(t("Donors")) })
            Tab(selected = sub == 1, onClick = { vm.clearError(); sub = 1 }, text = { Text(t("Organizations")) })
        }
        if (sub == 0) SearchTab(vm) else OrganizationsTab(vm)
    }
}
