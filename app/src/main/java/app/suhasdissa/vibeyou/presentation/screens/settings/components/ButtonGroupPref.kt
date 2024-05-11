package app.suhasdissa.vibeyou.presentation.screens.settings.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun <T> ButtonGroupPref(
    title: String,
    options: List<String>,
    values: List<T>,
    currentValue: T,
    onChange: (T) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(top = 8.dp)
            .padding(horizontal = 12.dp)
    ) {
        Text(title)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val cornerRadius = 20.dp
            var selectedItem by remember {
                mutableStateOf(
                    currentValue
                )
            }

            values.forEachIndexed { index, value ->
                val startRadius = if (index != 0) 0.dp else cornerRadius
                val endRadius = if (index == values.size - 1) cornerRadius else 0.dp

                OutlinedButton(
                    onClick = {
                        selectedItem = value
                        onChange.invoke(values[index])
                    },
                    modifier = Modifier
                        .offset(if (index == 0) 0.dp else (-1 * index).dp, 0.dp)
                        .zIndex(if (selectedItem == value) 1f else 0f),
                    shape = RoundedCornerShape(
                        topStart = startRadius,
                        topEnd = endRadius,
                        bottomStart = startRadius,
                        bottomEnd = endRadius
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (selectedItem == value) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.75f)
                        }
                    ),
                    colors = if (selectedItem == value) {
                        ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    }
                ) {
                    Text(options[index])
                }
            }
        }
    }
}
