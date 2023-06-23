package demo.plot.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Suppress("FunctionName")
@Composable
fun DemoRadioGroup(
    preserveAspectRatio: MutableState<Boolean>
) {
    val radioOptions: List<String> = listOf("Yes", "No")

    // Yes <=> 0
    // No <=> 1
    var selectedIndex = if (preserveAspectRatio.value) 0 else 1

    Row(
        modifier = Modifier.selectableGroup().padding(8.dp)//.fillMaxWidth(),
//        horizontalArrangement = Arrangement.Center,
//        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
        ) {
            Text("Preserve aspect ratio", modifier = Modifier.padding(end = 8.dp))
        }
        radioOptions.forEachIndexed { index, label ->
            Column(
                modifier = Modifier.selectable(
                    selected = (radioOptions[selectedIndex] == label),
                    onClick = {
                        selectedIndex = index
                        preserveAspectRatio.value = when (index) {
                            0 -> true
                            else -> false
                        }
                    },
                    role = Role.RadioButton
                ),
                verticalArrangement = Arrangement.Center,
//                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Row(
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    RadioButton(
                        selected = (index == selectedIndex),
                        onClick = null
                    )
                    Text(text = radioOptions[index])
                }
            }
        }
    }
}