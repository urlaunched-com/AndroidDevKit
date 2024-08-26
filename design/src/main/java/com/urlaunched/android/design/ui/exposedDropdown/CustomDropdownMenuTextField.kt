package com.urlaunched.android.design.ui.exposedDropdown

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.PopupProperties
import com.urlaunched.android.design.resources.dimens.Dimens
import com.urlaunched.android.design.ui.exposedDropdown.constants.DropdownMenuDimens
import com.urlaunched.android.design.ui.textfield.TextField

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun CustomDropdownMenuTextField(
    modifier: Modifier = Modifier,
    menuModifier: Modifier = Modifier,
    text: String,
    onValueChange: (text: String) -> Unit,
    expanded: Boolean,
    placeHolder: String? = null,
    readOnly: Boolean = false,
    menuShape: Shape = RoundedCornerShape(Dimens.cornerRadiusNormalSpecial),
    maxMenuHeight: Dp = Dp.Unspecified,
    menuBackground: Color = Color.White,
    menuBorder: BorderStroke? = BorderStroke(
        width = DropdownMenuDimens.borderThickness,
        color = Color.Gray
    ),
    trailingIcon: @Composable (() -> Unit)? = null,
    onExpandedChange: (onExpandedChange: Boolean) -> Unit,
    onDismiss: () -> Unit,
    menuItems: @Composable () -> Unit
) {
    ExposedDropdownMenuBox(
        modifier = modifier,
        expanded = expanded,
        onExpandedChange = onExpandedChange
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            value = text,
            onValueChange = onValueChange,
            readOnly = readOnly,
            placeHolder = placeHolder,
            trailingIconAlwaysShown = true,
            trailingIcon = trailingIcon,
            singleLine = true,
            maxLines = 1,
            minLines = 1
        )

        BaseExposedDropdownMenu(
            modifier = menuModifier.exposedDropdownSize(),
            expanded = expanded,
            onDismissRequest = onDismiss,
            properties = PopupProperties(focusable = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = maxMenuHeight),
                shape = menuShape,
                color = menuBackground,
                border = menuBorder
            ) {
                menuItems()
            }
        }
    }
}

@Preview
@Composable
private fun PagingDropdownMenuPreview() {
    var expanded by remember { mutableStateOf(false) }

    CustomDropdownMenuTextField(
        text = "Expanded menu",
        expanded = expanded,
        onExpandedChange = { expanded = it },
        onValueChange = {},
        onDismiss = { expanded = false }
    ) {
        Column {
            repeat(2) {
                Text(
                    text = "Menu item",
                    modifier = Modifier.padding(vertical = Dimens.spacingSmallSpecial)
                )
            }
        }
    }
}