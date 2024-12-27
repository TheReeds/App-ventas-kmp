package io.dev.kmpventas.presentation.screens.catalog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.dev.kmpventas.data.remote.dto.UnitMeasurement
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitMeasurementScreen(
    navController: NavHostController,
    paddingValues: PaddingValues,
    viewModel: UnitMeasurementViewModel = koinInject()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var showActiveOnly by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        TopAppBar(
            title = { Text("Unidades de Medida") },
            actions = {
                // Botón de filtro
                IconButton(onClick = { showActiveOnly = !showActiveOnly }) {
                    Icon(
                        imageVector = if (showActiveOnly) Icons.Default.FilterAlt else Icons.Default.FilterAltOff,
                        contentDescription = "Filtrar activos",
                        tint = if (showActiveOnly) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
                // Botón de añadir
                IconButton(onClick = { viewModel.selectItem(null) }) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar")
                }
            }
        )

        // Barra de búsqueda
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Buscar unidad de medida...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                    }
                }
            },
            singleLine = true
        )

        // Stats Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "Total",
                    value = state.totalElements.toString(),
                    icon = Icons.Default.List
                )
                StatItem(
                    label = "Activos",
                    value = state.items.count { it.state }.toString(),
                    icon = Icons.Default.CheckCircle
                )
                StatItem(
                    label = "Inactivos",
                    value = state.items.count { !it.state }.toString(),
                    icon = Icons.Default.Cancel
                )
            }
        }

        // Contenido principal
        Box(modifier = Modifier.weight(1f)) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filteredItems = state.items.filter { item ->
                        val matchesSearch = item.name.contains(searchQuery, ignoreCase = true) ||
                                item.sunatCode.contains(searchQuery, ignoreCase = true)
                        val matchesFilter = if (showActiveOnly) item.state else true
                        matchesSearch && matchesFilter
                    }

                    if (filteredItems.isEmpty()) {
                        item {
                            EmptyState(
                                message = if (searchQuery.isEmpty())
                                    "No hay unidades de medida disponibles"
                                else
                                    "No se encontraron resultados para '$searchQuery'"
                            )
                        }
                    }

                    items(
                        items = filteredItems,
                        key = { it.id }
                    ) { item ->
                        UnitMeasurementItem(
                            item = item,
                            onEdit = { viewModel.selectItem(item) },
                            onToggleState = { viewModel.toggleState(item) }
                        )
                    }
                }

                // Error snackbar
                state.error?.let { error ->
                    Snackbar(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.BottomCenter),
                        action = {
                            TextButton(
                                onClick = { viewModel.loadUnitMeasurements() }
                            ) {
                                Text("Reintentar", color = MaterialTheme.colorScheme.inversePrimary)
                            }
                        }
                    ) {
                        Text(error)
                    }
                }
            }
        }

        // Paginación
        if (!state.isLoading && state.totalPages > 1) {
            PaginationControls(
                currentPage = state.currentPage,
                totalPages = state.totalPages,
                onPageChange = { viewModel.loadUnitMeasurements(it) }
            )
        }
    }

    // Dialog para editar/crear
    if (state.isDialogOpen) {
        UnitMeasurementDialog(
            unitMeasurement = state.selectedItem,
            onDismiss = { viewModel.closeDialog() },
            onConfirm = { /*TODO */}
        )
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun EmptyState(message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PaginationControls(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { onPageChange(currentPage - 1) },
            enabled = currentPage > 0
        ) {
            Icon(Icons.Default.NavigateBefore, "Anterior")
        }

        Text(
            text = "Página ${currentPage + 1} de $totalPages",
            style = MaterialTheme.typography.bodyMedium
        )

        IconButton(
            onClick = { onPageChange(currentPage + 1) },
            enabled = currentPage < totalPages - 1
        ) {
            Icon(Icons.Default.NavigateNext, "Siguiente")
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitMeasurementItem(
    item: UnitMeasurement,
    onEdit: () -> Unit,
    onToggleState: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Código SUNAT: ${item.sunatCode}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Símbolo: ${item.symbolPrint}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    if (item.description.isNotEmpty()) {
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = onEdit) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    Switch(
                        checked = item.state,
                        onCheckedChange = { onToggleState() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                            uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun UnitMeasurementDialog(
    unitMeasurement: UnitMeasurement?,
    onDismiss: () -> Unit,
    onConfirm: (UnitMeasurement) -> Unit
) {
    var name by remember { mutableStateOf(unitMeasurement?.name ?: "") }
    var sunatCode by remember { mutableStateOf(unitMeasurement?.sunatCode ?: "") }
    var symbolPrint by remember { mutableStateOf(unitMeasurement?.symbolPrint ?: "") }
    var description by remember { mutableStateOf(unitMeasurement?.description ?: "") }
    var hasError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (unitMeasurement == null) "Nueva Unidad de Medida"
                else "Editar Unidad de Medida"
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        hasError = it.isEmpty()
                    },
                    label = { Text("Nombre") },
                    isError = hasError && name.isEmpty(),
                    supportingText = {
                        if (hasError && name.isEmpty()) {
                            Text("El nombre es requerido")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = sunatCode,
                    onValueChange = {
                        sunatCode = it
                        hasError = it.isEmpty()
                    },
                    label = { Text("Código SUNAT") },
                    isError = hasError && sunatCode.isEmpty(),
                    supportingText = {
                        if (hasError && sunatCode.isEmpty()) {
                            Text("El código SUNAT es requerido")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = symbolPrint,
                    onValueChange = {
                        symbolPrint = it
                        hasError = it.isEmpty()
                    },
                    label = { Text("Símbolo") },
                    isError = hasError && symbolPrint.isEmpty(),
                    supportingText = {
                        if (hasError && symbolPrint.isEmpty()) {
                            Text("El símbolo es requerido")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isEmpty() || sunatCode.isEmpty() || symbolPrint.isEmpty()) {
                        hasError = true
                        return@TextButton
                    }

                    val newUnit = unitMeasurement?.copy(
                        name = name,
                        sunatCode = sunatCode,
                        symbolPrint = symbolPrint,
                        description = description
                    ) ?: UnitMeasurement(
                        id = "",  // Se generará en el backend
                        name = name,
                        sunatCode = sunatCode,
                        symbolPrint = symbolPrint,
                        description = description,
                        state = true,
                        companyId = "",  // Se asignará en el backend
                        createdAt = "",  // Se asignará en el backend
                        updatedAt = "",  // Se asignará en el backend
                        deletedAt = null
                    )
                    onConfirm(newUnit)
                    onDismiss()
                }
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}