/*
 * Copyright (c) 2023 JetBrains s.r.o.
 * Use of this source code is governed by the MIT license that can be found in the LICENSE file.
 */

package demo.plot.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember


@Suppress("FunctionName")
@Composable
fun DemoDropdownMenu(
    options: List<String>,
    selectedIndex: MutableState<Int>
) {
    val expanded = remember { (mutableStateOf(false)) }

    Box() {
        @OptIn(ExperimentalMaterialApi::class)
        ExposedDropdownMenuBox(
            expanded = expanded.value,
            onExpandedChange = {
                expanded.value = !expanded.value
            },
//            modifier = Modifier.height(40.dp).width(40.dp)
        ) {
            TextField(
                value = options[selectedIndex.value],
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    @OptIn(ExperimentalMaterialApi::class)
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
                }
            )

            @OptIn(ExperimentalMaterialApi::class)
            ExposedDropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }
            ) {
                options.forEachIndexed { index, name ->
                    DropdownMenuItem(
                        onClick = {
                            selectedIndex.value = index
                            expanded.value = false
                        }
                    ) {
                        Text(text = name)
                    }
                }
            }
        }
    }
}