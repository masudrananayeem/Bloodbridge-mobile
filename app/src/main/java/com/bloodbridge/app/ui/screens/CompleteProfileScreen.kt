package com.bloodbridge.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.bloodbridge.app.data.Constants
import com.bloodbridge.app.i18n.t
import com.bloodbridge.app.ui.AppTextField
import com.bloodbridge.app.ui.AppViewModel
import com.bloodbridge.app.ui.DropdownField
import com.bloodbridge.app.ui.ErrorBanner
import com.bloodbridge.app.ui.LanguageToggle
import com.bloodbridge.app.ui.PrimaryButton

/** Shown after a first-time Google sign-in: the backend needs blood group, phone, district, etc. */
@Composable
fun CompleteProfileScreen(vm: AppViewModel) {
    var phone by rememberSaveable { mutableStateOf("") }
    var bloodGroup by rememberSaveable { mutableStateOf("") }
    var district by rememberSaveable { mutableStateOf("") }
    var upazila by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }
    var dob by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().statusBarsPadding().imePadding().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        LanguageToggle()
        Text(t("Complete your profile"), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(t("We need a few more details before you can find or become a donor."), color = MaterialTheme.colorScheme.onSurfaceVariant)

        AppTextField(phone, { phone = it }, t("Phone (01XXXXXXXXX)"), keyboardType = KeyboardType.Phone)
        DropdownField(t("Blood group"), bloodGroup, Constants.bloodGroups, { bloodGroup = it })
        DropdownField(t("District"), district, Constants.districts, { district = it })
        AppTextField(upazila, { upazila = it }, t("Upazila"))
        AppTextField(address, { address = it }, t("Present address"))
        AppTextField(dob, { dob = it }, t("Date of birth"), placeholder = "YYYY-MM-DD", keyboardType = KeyboardType.Number)

        ErrorBanner(vm.error)
        PrimaryButton(t("Save and continue"), vm.busy, { vm.completeProfile(phone, bloodGroup, district, upazila, address, dob) })
        TextButton(onClick = { vm.logout() }, modifier = Modifier.fillMaxWidth()) { Text(t("Log out")) }
    }
}
