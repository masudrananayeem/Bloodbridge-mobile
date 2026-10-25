package com.bloodbridge.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.bloodbridge.app.data.Constants
import com.bloodbridge.app.data.CreateRequestBody
import com.bloodbridge.app.ui.AppTextField
import com.bloodbridge.app.ui.DropdownField

/** Form for a direct request to one donor (targetDonor != null) or a blood-group/emergency request. */
@Composable
fun RequestDialog(
    title: String,
    defaultBloodGroup: String,
    defaultDistrict: String,
    defaultUpazila: String,
    targetDonorUid: String?,
    emergency: Boolean,
    busy: Boolean,
    error: String?,
    onSubmit: (CreateRequestBody) -> Unit,
    onDismiss: () -> Unit,
) {
    var bloodGroup by remember { mutableStateOf(defaultBloodGroup) }
    var hospital by remember { mutableStateOf("") }
    var district by remember { mutableStateOf(defaultDistrict) }
    var upazila by remember { mutableStateOf(defaultUpazila) }
    var units by remember { mutableStateOf("1") }
    var urgency by remember { mutableStateOf(if (emergency) "High" else "Medium") }
    var neededBy by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var localError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = { if (!busy) onDismiss() },
        title = { Text(title) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.verticalScroll(rememberScrollState()),
            ) {
                DropdownField("Blood group", bloodGroup, Constants.bloodGroups, { bloodGroup = it })
                AppTextField(hospital, { hospital = it }, "Hospital")
                DropdownField("District", district, Constants.districts, { district = it })
                AppTextField(upazila, { upazila = it }, "Upazila")
                AppTextField(units, { units = it.filter(Char::isDigit) }, "Units (bags)", keyboardType = KeyboardType.Number)
                DropdownField("Urgency", urgency, Constants.urgencies, { urgency = it })
                AppTextField(neededBy, { neededBy = it }, "Needed by", placeholder = "YYYY-MM-DD")
                AppTextField(message, { message = it.take(500) }, "Message (optional)")
                val shown = localError ?: error
                if (shown != null) Text(shown, color = androidx.compose.material3.MaterialTheme.colorScheme.error)
            }
        },
        confirmButton = {
            TextButton(
                enabled = !busy,
                onClick = {
                    val u = units.toIntOrNull() ?: 0
                    localError = when {
                        bloodGroup.isBlank() || hospital.isBlank() || district.isBlank() || upazila.isBlank() ->
                            "Please fill in all required fields."
                        u < 1 -> "Units must be at least 1."
                        !Regex("^\\d{4}-\\d{2}-\\d{2}$").matches(neededBy) -> "Needed-by date must be YYYY-MM-DD."
                        else -> null
                    }
                    if (localError == null) {
                        onSubmit(
                            CreateRequestBody(
                                bloodGroup = bloodGroup, units = u, hospital = hospital.trim(), district = district,
                                upazila = upazila.trim(), urgency = urgency, neededByDate = neededBy,
                                message = message.trim(), targetDonorUid = targetDonorUid, isEmergency = emergency,
                            )
                        )
                    }
                },
            ) { Text(if (busy) "Sending…" else "Send") }
        },
        dismissButton = { TextButton(onClick = onDismiss, enabled = !busy) { Text("Cancel") } },
    )
}
