package io.dev.kmpventas.presentation.screens.configuration.role

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import io.dev.kmpventas.data.remote.dto.Role
import io.dev.kmpventas.presentation.components.*
import io.dev.kmpventas.presentation.theme.LocalAppDimens
import kotlinx.datetime.*
import org.koin.compose.koinInject

@Composable
fun RoleScreen(
    navController: NavHostController,
    paddingValues: PaddingValues,
    viewModel: RoleViewModel = koinInject()
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
                // Estadísticas
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = LocalAppDimens.current.spacing_16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatisticCard(
                        title = "Total Roles",
                        value = state.totalElements.toString(),
                        icon = Icons.Default.SupervisorAccount,
                        modifier = Modifier.weight(1f)
                    )
                    StatisticCard(
                        title = "Roles Activos",
                        value = state.items.count { it.status }.toString(),
                        icon = Icons.Default.CheckCircle,
                        modifier = Modifier.weight(1f)
                    )
                    StatisticCard(
                        title = "Roles Inactivos",
                        value = state.items.count { !it.status }.toString(),
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
                        onValueChange = {
                            searchQuery = it
                            viewModel.loadRoles(searchQuery = it.ifEmpty { null })
                        },
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
                        text = "Nuevo Rol",
                        onClick = {
                            viewModel.setSelectedRole(Role("", "", "", "", true, null, "", "", null))
                        },
                        icon = Icons.Default.Add,
                        modifier = Modifier.widthIn(min = 180.dp)
                    )
                }

                // Tabla de roles
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    shadowElevation = 1.dp
                ) {
                    Column {
                        // Encabezados
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
                                text = "FECHA CREACIÓN",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.weight(1.5f)
                            )
                            Text(
                                text = "ACCIONES",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.weight(1.2f),
                                textAlign = TextAlign.Center
                            )
                        }

                        if (state.items.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (searchQuery.isEmpty())
                                        "No hay roles disponibles"
                                    else
                                        "No se encontraron resultados para '$searchQuery'",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyColumn {
                                items(state.items) { role ->
                                    RoleRow(
                                        role = role,
                                        onEdit = { viewModel.setSelectedRole(role) },
                                        onDelete = { viewModel.deleteRole(role.id) }
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

        // Dialog para crear/editar
        if (state.isDialogOpen) {
            RoleDialog(
                role = state.selectedItem,
                onDismiss = { viewModel.closeDialog() },
                onSave = { role ->
                    if (role.id.isEmpty()) {
                        viewModel.createRole(role)
                    } else {
                        viewModel.updateRole(role)
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
private fun RoleRow(
    role: Role,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val createdAt = try {
        val instant = Instant.parse(role.createdAt) // Parse a ISO-8601 string
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        "${localDateTime.date.dayOfMonth}/${localDateTime.date.monthNumber}/${localDateTime.date.year} ${localDateTime.hour}:${localDateTime.minute}"
    } catch (e: Exception) {
        role.createdAt // Fallback if parsing fails
    }

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
                text = role.name,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = role.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = role.code,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                color = if (role.status)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = if (role.status) "Activo" else "Inactivo",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (role.status)
                        MaterialTheme.colorScheme.onPrimaryContainer
                    else
                        MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
        Text(
            text = createdAt,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1.5f)
        )
        Row(
            modifier = Modifier.weight(1.2f),
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
private fun RoleDialog(
    role: Role?,
    onDismiss: () -> Unit,
    onSave: (Role) -> Unit
) {
    var name by remember { mutableStateOf(role?.name ?: "") }
    var description by remember { mutableStateOf(role?.description ?: "") }
    var code by remember { mutableStateOf(role?.code ?: "") }
    var status by remember { mutableStateOf(role?.status ?: true) }

    AppDialog(
        title = if (role?.id?.isEmpty() == true) "Nuevo Rol" else "Editar Rol",
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        Role(
                            id = role?.id ?: "",
                            name = name,
                            code = code,
                            description = description,
                            status = status,
                            company = role?.company,
                            createdAt = role?.createdAt ?: "",
                            updatedAt = role?.updatedAt ?: "",
                            deletedAt = role?.deletedAt
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
                label = "Nombre",
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )

            AppTextField(
                value = code,
                onValueChange = { code = it },
                label = "Código",
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )

            AppTextField(
                value = description,
                onValueChange = { description = it },
                label = "Descripción",
                modifier = Modifier.height(100.dp),
                singleLine = false,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                )
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Switch(
                    checked = status,
                    onCheckedChange = { status = it }
                )
                Text(
                    text = "Activo",
                    modifier = Modifier.padding(start = LocalAppDimens.current.spacing_8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}