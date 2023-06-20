package demo.plot

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

@Suppress("FunctionName")
@Composable
fun AspectRatioRadioGroup(
    preserveAspectRatio: MutableState<Boolean>
) {
    val radioOptions: List<String> = listOf("Yes", "No")
//    val selectedOption = remember {
//        mutableStateOf(
//            radioOptions[if (preserveAspectRatio.value) 0 else 1]
//        )
//    }

    // Yes <=> 0
    // No <=> 1
    var selectedIndex = if (preserveAspectRatio.value) 0 else 1

//    preserve.value = when (selectedOption.value) {
//        radioOptions[0] -> true
//        else -> false
//    }

    Row(
        modifier = Modifier.selectableGroup(),
//        horizontalArrangement = Arrangement.Center,
//        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
        ) {
            Text("Preserve aspect ratio", modifier = Modifier.padding(end = 16.dp))
        }
        radioOptions.forEachIndexed { index, label ->
            Column(
                modifier = Modifier.selectable(
                    selected = (radioOptions[selectedIndex] == label),
                    onClick = {
                        selectedIndex = index
                        preserveAspectRatio.value =  when (index) {
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
                    modifier = Modifier.padding(end = 16.dp)
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