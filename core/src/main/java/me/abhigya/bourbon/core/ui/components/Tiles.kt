package me.abhigya.bourbon.core.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.abhigya.bourbon.core.utils.bouncyClick
import me.saket.cascade.CascadeDropdownMenu

@Composable
fun TileCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = modifier
            .padding(4.dp)
    ) {
        Card(
            modifier = modifier
                .width(336.dp),
            colors = CardDefaults.elevatedCardColors()
                .copy(
                    containerColor = MaterialTheme.colorScheme.secondary,
                ),
            elevation = CardDefaults.elevatedCardElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp,
                focusedElevation = 8.dp,
                hoveredElevation = 8.dp,
                disabledElevation = 0.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                content()
            }
        }
    }
}

@Composable
fun TileSeparator(modifier: Modifier = Modifier, thickness: Dp = 3.dp) {
    val color = MaterialTheme.colorScheme.tertiary
    Canvas(modifier = modifier
        .height(thickness + 8.dp)
        .padding(vertical = 4.dp)
        .fillMaxWidth()) {
        drawLine(
            color = color,
            strokeWidth = thickness.toPx(),
            start = Offset(0f, thickness.toPx() / 2),
            end = Offset(size.width, thickness.toPx() / 2),
            cap = StrokeCap.Round,
            pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(5.dp.toPx(), 5.dp.toPx()),
                phase = 0.dp.toPx()
            )
        )
    }
}

@Composable
fun TileLabel(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 8.dp, top = 2.dp),
        text = text,
        color = MaterialTheme.colorScheme.tertiary,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Left
    )
}

@Composable
fun TiledRow(modifier: Modifier = Modifier, height: Dp = 48.dp, itemsPerRow: Int = 2, elements: List<(@Composable BoxScope.() -> Unit)>) {
    val e = if (elements.size % itemsPerRow != 0) {
        elements + List(itemsPerRow - elements.size % itemsPerRow) { { } }
    } else {
        elements
    }
    val rows = e.chunked(itemsPerRow)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        for ((i, row) in rows.withIndex()) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .height(height),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (element in row) {
                    Box(
                        modifier = modifier
                            .fillMaxWidth(0.96f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp))
                            .weight(1f, false),
                        contentAlignment = Alignment.Center
                    ) {
                        element()
                    }
                }
            }

            if (i != rows.size - 1) {
                HorizontalDivider(thickness = 16.dp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TileTextBox(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: (@Composable () -> Unit)? = null,
    placeholder: (@Composable () -> Unit)? = null,
    textStyle: TextStyle = LocalTextStyle.current,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    shape: Shape = RoundedCornerShape(4.dp),
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(
        unfocusedContainerColor = Color.Transparent,
        focusedContainerColor = Color.Transparent,
        unfocusedBorderColor = Color.Transparent,
        focusedBorderColor = Color.Transparent,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
    )
) {
    val textColor = textStyle.color.takeOrElse {
        val focused by interactionSource.collectIsFocusedAsState()
        val targetValue = if (focused) {
            colors.focusedTextColor
        } else {
            colors.unfocusedTextColor
        }
        rememberUpdatedState(newValue = targetValue).value
    }
    val mergedTextStyle = textStyle.merge(TextStyle(color = textColor))
    CompositionLocalProvider(LocalTextSelectionColors provides colors.textSelectionColors) {
        BasicTextField(
            value = value,
            modifier = if (label != null) {
                modifier
                    // Merge semantics at the beginning of the modifier chain to ensure padding is
                    // considered part of the text field.
                    .semantics(mergeDescendants = true) {}
                    .padding(top = 4.dp)
            } else {
                modifier
            },
            onValueChange = onValueChange,
            enabled = true,
            readOnly = false,
            textStyle = mergedTextStyle,
            cursorBrush = SolidColor(colors.cursorColor),
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            interactionSource = interactionSource,
            singleLine = true,
            maxLines = 1,
            minLines = 1,
            decorationBox = @Composable { innerTextField ->
                OutlinedTextFieldDefaults.DecorationBox(
                    value = value,
                    visualTransformation = visualTransformation,
                    innerTextField = innerTextField,
                    placeholder = placeholder,
                    label = label,
                    singleLine = true,
                    enabled = true,
                    isError = false,
                    interactionSource = interactionSource,
                    colors = colors,
                    contentPadding = PaddingValues(start = 16.dp),
                    container = {
                        OutlinedTextFieldDefaults.ContainerBox(
                            enabled = true,
                            isError = false,
                            interactionSource,
                            colors,
                            shape
                        )
                    }
                )
            }
        )
    }
}

@Composable
fun TileDropDown(
    modifier: Modifier = Modifier,
    selected: Int,
    entries: List<String>,
    arrowTint: Color = MaterialTheme.colorScheme.background,
    textColor: Color = MaterialTheme.colorScheme.background,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontWeight: FontWeight? = null,
    onEntryClick: (Int) -> Unit
) {
    var menuVisible by remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(4.dp))
            .clickable {
                menuVisible = !menuVisible
            },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.animateContentSize(),
            text = entries[selected],
            color = textColor,
            fontWeight = fontWeight,
            fontSize = fontSize
        )
        VerticalDivider(thickness = 4.dp)
        Icon(
            imageVector = if (menuVisible) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
            contentDescription = "Dropdown",
            tint = arrowTint,
            modifier = Modifier.size(16.dp)
        )
    }
    CascadeDropdownMenu(
        expanded = menuVisible,
        onDismissRequest = { menuVisible = false },
        fixedWidth = 145.dp
    ) {
        for ((idx, f) in entries.withIndex()) {
            androidx.compose.material3.DropdownMenuItem(
                text = { Text(text = f) },
                onClick = {
                    menuVisible = false
                    onEntryClick(idx)
                }
            )
        }
    }
}

@Composable
fun TileOption(
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    onClick: () -> Unit,
    outlined: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .run {
                if (isSelected) {
                    background(
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    this
                }
            }
            .run {
                if (outlined) {
                    border(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(4.dp),
                        width = 1.dp
                    )
                } else {
                    this
                }
            }
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}