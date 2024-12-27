package io.dev.kmpventas.presentation.screens.catalog.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import io.dev.kmpventas.data.remote.dto.Category
import io.dev.kmpventas.presentation.components.*
import io.dev.kmpventas.presentation.layouts.ListScreenLayout
import io.dev.kmpventas.presentation.theme.LocalAppDimens
import org.koin.compose.koinInject

@Composable
fun CategoryScreen(
    navController: NavHostController,
    paddingValues: PaddingValues,
    viewModel: CategoryViewModel = koinInject()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val notificationState = rememberNotificationState()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(state.notification) {
        if (state.notification.isVisible) {
            notificationState.showNotification(
                message = state.notification.message,
                type = state.notification.type,
                duration = state.notification.duration
            )
        }
    }

    NotificationHost(state = notificationState) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(LocalAppDimens.current.spacing_16.dp)
            ) {
                // Cabecera con estadísticas
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = LocalAppDimens.current.spacing_16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatisticCard(
                        title = "Total Categorías",
                        value = state.totalElements.toString(),
                        icon = Icons.Default.Category,
                        modifier = Modifier.weight(1f)
                    )
                    StatisticCard(
                        title = "Activas en la pagina",
                        value = state.items.count { it.state }.toString(),
                        icon = Icons.Default.CheckCircle,
                        modifier = Modifier.weight(1f)
                    )
                    StatisticCard(
                        title = "Inactivas en la pagina",
                        value = state.items.count { !it.state }.toString(),
                        icon = Icons.Default.Cancel,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Barra de herramientas
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = LocalAppDimens.current.spacing_16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Buscar...") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 56.dp),
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    AppButton(
                        text = "Nueva Categoría",
                        onClick = {
                            viewModel.setSelectedCategory(Category("", "", "", "", true, "", "", "", null))
                        },
                        icon = Icons.Default.Add,
                        modifier = Modifier.widthIn(min = 180.dp)
                    )
                }

                // Tabla de categorías
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    shadowElevation = 1.dp
                ) {
                    Column {
                        // Encabezados de la tabla
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "NOMBRE",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.weight(2f)
                            )
                            Text(
                                text = "CÓDIGO",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "ESTADO",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "ACCIONES",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                        }

                        // Lista de categorías
                        val filteredCategories = state.items.filter {
                            it.name.contains(searchQuery, ignoreCase = true) ||
                                    it.code.contains(searchQuery, ignoreCase = true) ||
                                    it.description.contains(searchQuery, ignoreCase = true)
                        }

                        if (filteredCategories.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (searchQuery.isEmpty())
                                        "No hay categorías disponibles"
                                    else
                                        "No se encontraron resultados para '$searchQuery'",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyColumn {
                                items(filteredCategories) { category ->
                                    CategoryRow(
                                        category = category,
                                        onEdit = { viewModel.setSelectedCategory(category) },
                                        onDelete = { viewModel.deleteCategory(category.id) }
                                    )
                                }
                            }
                        }
                    }
                }

                // Paginación
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = LocalAppDimens.current.spacing_16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = { viewModel.previousPage() },
                        enabled = state.currentPage > 0,
                        modifier = Modifier.widthIn(min = 100.dp)
                    ) {
                        Icon(
                            Icons.Default.NavigateBefore,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Anterior")
                    }

                    Text(
                        text = "${state.currentPage + 1} de ${state.totalPages}",
                        modifier = Modifier.padding(horizontal = 24.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Button(
                        onClick = { viewModel.nextPage() },
                        enabled = state.currentPage + 1 < state.totalPages,
                        modifier = Modifier.widthIn(min = 100.dp)
                    ) {
                        Text("Siguiente")
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Default.NavigateNext,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Diálogo para crear/editar
        if (state.isDialogOpen) {
            CategoryDialog(
                category = state.selectedItem,
                onDismiss = { viewModel.closeDialog() },
                onSave = { category ->
                    if (category.id.isEmpty()) {
                        viewModel.createCategory(category)
                    } else {
                        viewModel.updateCategory(category)
                    }
                }
            )
        }
    }
}

@Composable
private fun StatisticCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    AppCard(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}

@Composable
private fun CategoryRow(
    category: Category,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(2f)
        ) {
            Text(
                text = category.name,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = category.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = category.code,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                color = if (category.state)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = if (category.state) "Activo" else "Inactivo",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (category.state)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Editar",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
    Divider()
}

@Composable
private fun CategoryDialog(
    category: Category?,
    onDismiss: () -> Unit,
    onSave: (Category) -> Unit
) {
    var name by remember { mutableStateOf(category?.name ?: "") }
    var description by remember { mutableStateOf(category?.description ?: "") }
    var code by remember { mutableStateOf(category?.code ?: "") }
    var state by remember { mutableStateOf(category?.state ?: true) }

    AppDialog(
        title = if (category?.id?.isEmpty() == true) "Nueva Categoría" else "Editar Categoría",
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        Category(
                            id = category?.id ?: "",
                            name = name,
                            description = description,
                            code = code,
                            state = state,
                            companyId = category?.companyId ?: "",
                            createdAt = category?.createdAt ?: "",
                            updatedAt = category?.updatedAt ?: "",
                            deletedAt = category?.deletedAt
                        )
                    )
                },
                enabled = name.isNotEmpty() && code.isNotEmpty()
            ) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(LocalAppDimens.current.spacing_16.dp),
            verticalArrangement = Arrangement.spacedBy(LocalAppDimens.current.spacing_16.dp)
        ) {
            AppTextField(
                value = name,
                onValueChange = { name = it },
                label = "Nombre"
            )

            AppTextField(
                value = description,
                onValueChange = { description = it },
                label = "Descripción"
            )

            AppTextField(
                value = code,
                onValueChange = { code = it },
                label = "Código"
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = state,
                    onCheckedChange = { state = it }
                )
                Text(
                    text = "Activo",
                    modifier = Modifier.padding(start = LocalAppDimens.current.spacing_8.dp)
                )
            }
        }
    }
}