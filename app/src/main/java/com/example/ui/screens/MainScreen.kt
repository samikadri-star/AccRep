package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.Person
import com.example.data.SpreadsheetRow
import com.example.ui.ExpenseViewModel
import java.util.Calendar
import java.util.Locale

@Composable
fun MainScreen(viewModel: ExpenseViewModel) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(1) } // Default to Data Entry / Spreadsheet tab

    // Toast/Alert Handling
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is ExpenseViewModel.UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Force Arabic RTL direction globally
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(top = 24.dp, bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "💰",
                                fontSize = 20.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "دفتر المصاريف الذكي",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "إدارة البيانات المالية",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Navigation Tabs using custom styled rows for maximum stability and Elegant Dark aesthetics
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            Triple(0, "البيانات الأساسية", Icons.Default.Person),
                            Triple(1, "جدول الإدخال", Icons.Default.List),
                            Triple(2, "التقارير", Icons.Default.Share)
                        ).forEach { (index, title, icon) ->
                            val isSelected = selectedTab == index
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                        else Color.Transparent
                                    )
                                    .clickable { selectedTab = index }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                                               else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                                               else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            bottomBar = {
                // Developer Footer
                Column(modifier = Modifier.fillMaxWidth()) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "تطوير : سامي القادري 777484160",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                when (selectedTab) {
                    0 -> PersonsScreen(viewModel = viewModel)
                    1 -> DataEntryScreen(viewModel = viewModel)
                    2 -> ReportsScreen(viewModel = viewModel)
                }
            }
        }
    }
}

// ==========================================
// SCREEN 1: BASIC DATA (NAMES / PERSONS)
// ==========================================
@Composable
fun PersonsScreen(viewModel: ExpenseViewModel) {
    val persons by viewModel.allPersons.collectAsStateWithLifecycle()
    val nameInput by viewModel.personInputName.collectAsStateWithLifecycle()
    val nameError by viewModel.personInputError.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "إضافة اسم جديد",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { viewModel.setPersonInputName(it) },
                    label = { Text("الاسم الكامل") },
                    isError = nameError != null,
                    supportingText = {
                        if (nameError != null) {
                            Text(text = nameError!!, color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("name_input_field"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { viewModel.addPerson() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("add_person_button"),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("حفظ الاسم في قاعدة البيانات", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "الأسماء المسجلة حالياً (${persons.size})",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            textAlign = TextAlign.Start
        )

        if (persons.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "لا توجد أسماء مسجلة بعد. يرجى إضافة اسم للبدء.",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(persons) { person ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(
                                            MaterialTheme.colorScheme.primaryContainer,
                                            RoundedCornerShape(18.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = person.name.firstOrNull()?.toString() ?: "",
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = person.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            IconButton(
                                onClick = { viewModel.deletePerson(person) },
                                modifier = Modifier.testTag("delete_person_${person.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "حذف الاسم",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Border Stroke Helper for Composable
@Composable
fun BorderStroke(width: androidx.compose.ui.unit.Dp, color: Color): androidx.compose.foundation.BorderStroke {
    return androidx.compose.foundation.BorderStroke(width, color)
}


// ==========================================
// SCREEN 2: DATA ENTRY (SPREADSHEET SENSITIVE)
// ==========================================
@Composable
fun DataEntryScreen(viewModel: ExpenseViewModel) {
    val context = LocalContext.current
    val persons by viewModel.allPersons.collectAsStateWithLifecycle()
    val rows by viewModel.spreadsheetRows.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Warning if no names added yet
        AnimatedVisibility(
            visible = persons.isEmpty(),
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = "تنبيه: يجب إضافة الأسماء أولاً في علامة تبويب 'البيانات الأساسية' قبل التمكّن من إدخال المصاريف.",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(16.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "جدول إدخال البيانات المالي",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Button(
                onClick = { viewModel.addSpreadsheetRow() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("add_row_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("إضافة سطر", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Spreadsheet Body (Header & Rows)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Table Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "الاسم المستفيد",
                        modifier = Modifier.weight(1.3f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "التاريخ",
                        modifier = Modifier.weight(1.1f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "البيان / الوصف",
                        modifier = Modifier.weight(1.4f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "المبلغ",
                        modifier = Modifier.weight(1f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.Center
                    )
                    Box(modifier = Modifier.size(32.dp)) // Blank spacer for action column
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                // Scrollable Table Rows
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(rows, key = { it.id }) { rowState ->
                        SpreadsheetInputRow(
                            row = rowState,
                            personsList = persons,
                            onPersonSelected = { person -> viewModel.updateRowPerson(rowState.id, person) },
                            onDateClicked = {
                                showDatePicker(context, rowState.dateMillis) { selectedMillis ->
                                    viewModel.updateRowDate(rowState.id, selectedMillis)
                                }
                            },
                            onDescChanged = { valStr -> viewModel.updateRowDescription(rowState.id, valStr) },
                            onAmountChanged = { amtStr -> viewModel.updateRowAmount(rowState.id, amtStr) },
                            onDeleteClicked = { viewModel.removeSpreadsheetRow(rowState.id) },
                            viewModel = viewModel
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Master Command Button: POST TO DATABASE
        Button(
            onClick = { viewModel.postSpreadsheetRows() },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("post_spreadsheet_button"),
            shape = RoundedCornerShape(26.dp),
            enabled = persons.isNotEmpty(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.outlineVariant
            )
        ) {
            Icon(Icons.Default.Send, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "ترحيل وإدراج البيانات إلى قاعدة البيانات",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// SINGLE SPREADSHEET ROW IMPLEMENTATION
@Composable
fun SpreadsheetInputRow(
    row: SpreadsheetRow,
    personsList: List<Person>,
    onPersonSelected: (Person) -> Unit,
    onDateClicked: () -> Unit,
    onDescChanged: (String) -> Unit,
    onAmountChanged: (String) -> Unit,
    onDeleteClicked: () -> Unit,
    viewModel: ExpenseViewModel
) {
    var dropdownExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // COLUMN 1: PERSON SELECTOR (DROPDOWN)
        Box(
            modifier = Modifier
                .weight(1.3f)
                .padding(horizontal = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .border(
                        1.dp,
                        if (row.personError) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                        RoundedCornerShape(6.dp)
                    )
                    .background(
                        if (row.personError) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                        else Color.Transparent
                    )
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { dropdownExpanded = true }
                    .padding(horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = row.person?.name ?: "اختر الاسم...",
                    fontSize = 12.sp,
                    maxLines = 1,
                    color = if (row.person != null) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            DropdownMenu(
                expanded = dropdownExpanded,
                onDismissRequest = { dropdownExpanded = false },
                modifier = Modifier.widthIn(min = 150.dp)
            ) {
                if (personsList.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("لا توجد أسماء مضافة!", fontSize = 12.sp, color = Color.Red) },
                        onClick = { dropdownExpanded = false }
                    )
                } else {
                    personsList.forEach { person ->
                        DropdownMenuItem(
                            text = { Text(person.name, fontSize = 13.sp) },
                            onClick = {
                                onPersonSelected(person)
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // COLUMN 2: DATE SELECTOR
        Box(
            modifier = Modifier
                .weight(1.1f)
                .padding(horizontal = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                        RoundedCornerShape(6.dp)
                    )
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onDateClicked() }
                    .padding(horizontal = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = viewModel.formatDate(row.dateMillis),
                    fontSize = 11.sp,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = "تقويم",
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        // COLUMN 3: DESCRIPTION (بيان)
        Box(
            modifier = Modifier
                .weight(1.4f)
                .padding(horizontal = 2.dp)
        ) {
            OutlinedTextField(
                value = row.description,
                onValueChange = onDescChanged,
                placeholder = { Text("مثال: إيجار، سلفة..", fontSize = 11.sp) },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(6.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                )
            )
        }

        // COLUMN 4: AMOUNT (مبلغ)
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 2.dp)
        ) {
            OutlinedTextField(
                value = row.amount,
                onValueChange = onAmountChanged,
                placeholder = { Text("المبلغ", fontSize = 11.sp) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = row.amountError,
                textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(6.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = if (row.amountError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                    focusedContainerColor = if (row.amountError) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f) else Color.Transparent,
                    unfocusedContainerColor = if (row.amountError) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f) else Color.Transparent
                )
            )
        }

        // COLUMN 5: ACTIONS (DELETE ROW BUTTON)
        IconButton(
            onClick = onDeleteClicked,
            modifier = Modifier
                .size(32.dp)
                .padding(start = 2.dp)
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "حذف السطر",
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}


// ==========================================
// SCREEN 3: REPORTS (DETAILED & SUMMARY)
// ==========================================
@Composable
fun ReportsScreen(viewModel: ExpenseViewModel) {
    val context = LocalContext.current
    var activeReportTab by remember { mutableIntStateOf(0) } // 0: Detailed, 1: Period Summary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Toggle Buttons for Reports
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(24.dp))
                .padding(4.dp)
        ) {
            Button(
                onClick = { activeReportTab = 0 },
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeReportTab == 0) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                    contentColor = if (activeReportTab == 0) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text("١. تقرير تفصيلي لكل اسم", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { activeReportTab = 1 },
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeReportTab == 1) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                    contentColor = if (activeReportTab == 1) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text("٢. تقرير إجمالي الفترة", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (activeReportTab) {
            0 -> DetailedReportSection(viewModel = viewModel, context = context)
            1 -> PeriodSummaryReportSection(viewModel = viewModel, context = context)
        }
    }
}

// 3.1: DETAILED REPORT VIEW
@Composable
fun DetailedReportSection(viewModel: ExpenseViewModel, context: Context) {
    val persons by viewModel.allPersons.collectAsStateWithLifecycle()
    val selectedPerson by viewModel.selectedDetailedPerson.collectAsStateWithLifecycle()
    val expenses by viewModel.detailedExpenses.collectAsStateWithLifecycle()

    var dropdownExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Name selector row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("اختر الاسم للتقرير:", fontSize = 14.sp, fontWeight = FontWeight.Bold)

            Box(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { dropdownExpanded = true }
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = selectedPerson?.name ?: "انقر لاختيار اسم...",
                        fontSize = 14.sp,
                        color = if (selectedPerson != null) MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null)
                }

                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    persons.forEach { person ->
                        DropdownMenuItem(
                            text = { Text(person.name, fontSize = 14.sp) },
                            onClick = {
                                viewModel.selectDetailedPerson(person)
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // PDF Print Button
            IconButton(
                onClick = { viewModel.printDetailedReport(context) },
                enabled = selectedPerson != null && expenses.isNotEmpty(),
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        if (selectedPerson != null && expenses.isNotEmpty()) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(8.dp)
                    )
                    .testTag("print_detailed_report")
            ) {
                Icon(
                    Icons.Default.Share,
                    contentDescription = "طباعة تقرير تفصيلي PDF",
                    tint = if (selectedPerson != null && expenses.isNotEmpty()) MaterialTheme.colorScheme.onPrimaryContainer
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (selectedPerson == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "الرجاء اختيار اسم لعرض التقرير التفصيلي الخاص به.",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else if (expenses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "لا توجد أي قيود مصاريف مسجلة لـ '${selectedPerson!!.name}' حتى الآن.",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // Summary Card
            val total = expenses.sumOf { it.amount }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "إجمالي مصاريف: ${selectedPerson!!.name}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        "${String.format(Locale.US, "%,.2f", total)} ر.س",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Table of records
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Text("التاريخ", modifier = Modifier.weight(1.2f), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("اليوم", modifier = Modifier.weight(1f), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("البيان / الوصف", modifier = Modifier.weight(1.5f), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("المبلغ", modifier = Modifier.weight(1.1f), fontSize = 13.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    // Data list
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(expenses) { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(viewModel.formatDate(item.dateMillis), modifier = Modifier.weight(1.2f), fontSize = 12.sp)
                                Text(viewModel.getDayName(item.dateMillis), modifier = Modifier.weight(1f), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Text(item.description.ifEmpty { "—" }, modifier = Modifier.weight(1.5f), fontSize = 12.sp)
                                Text(
                                    "${String.format(Locale.US, "%,.2f", item.amount)}",
                                    modifier = Modifier.weight(1.1f),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.End
                                )
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }
    }
}

// 3.2: PERIOD SUMMARY VIEW
@Composable
fun PeriodSummaryReportSection(viewModel: ExpenseViewModel, context: Context) {
    val expenses by viewModel.periodExpenses.collectAsStateWithLifecycle()
    val startDate by viewModel.periodStartDate.collectAsStateWithLifecycle()
    val endDate by viewModel.periodEndDate.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxWidth()) {
        // Date Pickers Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // From Date Selector
            Column(modifier = Modifier.weight(1f)) {
                Text("من تاريخ:", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            showDatePicker(context, startDate) { selected ->
                                viewModel.setPeriodStartDate(selected)
                            }
                        }
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(viewModel.formatDate(startDate), fontSize = 12.sp)
                    Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(14.dp))
                }
            }

            // To Date Selector
            Column(modifier = Modifier.weight(1f)) {
                Text("إلى تاريخ:", fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            showDatePicker(context, endDate) { selected ->
                                viewModel.setPeriodEndDate(selected)
                            }
                        }
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(viewModel.formatDate(endDate), fontSize = 12.sp)
                    Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(14.dp))
                }
            }

            // Print Button
            IconButton(
                onClick = { viewModel.printPeriodReport(context) },
                enabled = expenses.isNotEmpty(),
                modifier = Modifier
                    .padding(top = 20.dp)
                    .size(44.dp)
                    .background(
                        if (expenses.isNotEmpty()) MaterialTheme.colorScheme.secondaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(8.dp)
                    )
                    .testTag("print_period_report")
            ) {
                Icon(
                    Icons.Default.Share,
                    contentDescription = "طباعة تقرير الفترة PDF",
                    tint = if (expenses.isNotEmpty()) MaterialTheme.colorScheme.onSecondaryContainer
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (expenses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "لا توجد أي عمليات مسجلة في هذه الفترة المحددة.",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            // Grand Total Card
            val total = expenses.sumOf { it.amount }
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "إجمالي مصاريف الفترة المحددة:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        "${String.format(Locale.US, "%,.2f", total)} ر.س",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            // Table of records
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Text("الاسم", modifier = Modifier.weight(1.1f), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("التاريخ", modifier = Modifier.weight(1f), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("اليوم", modifier = Modifier.weight(0.8f), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("البيان / الوصف", modifier = Modifier.weight(1.2f), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text("المبلغ", modifier = Modifier.weight(1f), fontSize = 13.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    // Data list
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(expenses) { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(item.personName, modifier = Modifier.weight(1.1f), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Text(viewModel.formatDate(item.dateMillis), modifier = Modifier.weight(1f), fontSize = 11.sp)
                                Text(viewModel.getDayName(item.dateMillis), modifier = Modifier.weight(0.8f), fontSize = 11.sp)
                                Text(item.description.ifEmpty { "—" }, modifier = Modifier.weight(1.2f), fontSize = 11.sp)
                                Text(
                                    "${String.format(Locale.US, "%,.2f", item.amount)}",
                                    modifier = Modifier.weight(1f),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary,
                                    textAlign = TextAlign.End
                                )
                            }
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }
    }
}

// DATE PICKER DIALOG HELPER
fun showDatePicker(
    context: Context,
    initialDateMillis: Long,
    onDateSelected: (Long) -> Unit
) {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = initialDateMillis
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    android.app.DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            val resultCal = Calendar.getInstance()
            resultCal.set(Calendar.YEAR, selectedYear)
            resultCal.set(Calendar.MONTH, selectedMonth)
            resultCal.set(Calendar.DAY_OF_MONTH, selectedDay)
            onDateSelected(resultCal.timeInMillis)
        },
        year,
        month,
        day
    ).show()
}
