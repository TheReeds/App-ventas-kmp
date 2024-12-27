package io.dev.kmpventas.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.dev.kmpventas.data.remote.dto.MenuItem
import io.dev.kmpventas.domain.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawer(
    user: User,
    menuItems: List<MenuItem>,
    expandedMenuItems: Set<String>,
    onMenuItemExpand: (String) -> Unit,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    currentRoute: String? = null
) {
    val scrollState = rememberScrollState()

    ModalDrawerSheet(
        modifier = Modifier.fillMaxHeight(),
        drawerContainerColor = MaterialTheme.colorScheme.surface,
    ) {
        // Perfil de Usuario Mejorado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Surface(
                    modifier = Modifier
                        .size(72.dp)
                        .padding(4.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    border = BorderStroke(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(16.dp)
                            .size(32.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Menú Items con Scroll
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 8.dp)
        ) {
            menuItems.forEach { menuItem ->
                MenuItemComponent(
                    menuItem = menuItem,
                    currentRoute = currentRoute,
                    isExpanded = expandedMenuItems.contains(menuItem.id),
                    onExpandToggle = { onMenuItemExpand(menuItem.id) },
                    onNavigate = onNavigate
                )
            }
        }

        Divider(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )

        // Botón de Cerrar Sesión
        Surface(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .clickable { onLogout() },
            color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.08f)
        ) {
            ListItem(
                headlineContent = {
                    Text(
                        "Cerrar Sesión",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                },
                leadingContent = {
                    Icon(
                        Icons.Default.ExitToApp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun MenuItemComponent(
    menuItem: MenuItem,
    currentRoute: String?,
    isExpanded: Boolean,
    onExpandToggle: () -> Unit,
    onNavigate: (String) -> Unit,
    level: Int = 0
) {
    val isSelected = currentRoute == menuItem.link

    val backgroundColor by animateColorAsState(
        targetValue = when {
            isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
            isExpanded -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            else -> Color.Transparent
        },
        label = "backgroundColorAnimation"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = (level * 12).dp,
                top = 2.dp,
                bottom = 2.dp,
                end = 4.dp
            )
            .clip(MaterialTheme.shapes.small),
        color = backgroundColor,
        tonalElevation = if (isSelected) 1.dp else 0.dp
    ) {
        Column {
            ListItem(
                headlineContent = {
                    Text(
                        text = menuItem.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        ),
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
                        }
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = getIconForName(menuItem.icon),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                },
                trailingContent = if (!menuItem.children.isNullOrEmpty()) {
                    {
                        IconButton(
                            onClick = onExpandToggle,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isExpanded) {
                                    Icons.Default.ExpandLess
                                } else {
                                    Icons.Default.ExpandMore
                                },
                                contentDescription = if (isExpanded) "Colapsar" else "Expandir",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                } else null,
                modifier = Modifier.clickable(
                    enabled = menuItem.link.isNotEmpty() && menuItem.type == "basic"
                ) {
                    if (menuItem.link.isNotEmpty() && menuItem.type == "basic") {
                        onNavigate(menuItem.link)
                    }
                    if (!menuItem.children.isNullOrEmpty()) {
                        onExpandToggle()
                    }
                }
            )

            if (isExpanded && !menuItem.children.isNullOrEmpty()) {
                AnimatedVisibility(
                    visible = isExpanded,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    Column {
                        menuItem.children.forEach { childItem ->
                            MenuItemComponent(
                                menuItem = childItem,
                                currentRoute = currentRoute,
                                isExpanded = false,
                                onExpandToggle = { },
                                onNavigate = onNavigate,
                                level = level + 1
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getIconForName(iconName: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (iconName) {
        "heroicons_outline:cog-6-tooth" -> Icons.Default.Settings
        "heroicons_outline:user-group" -> Icons.Default.Group
        "heroicons_outline:user-circle" -> Icons.Default.Person
        "heroicons_outline:clipboard-document" -> Icons.Default.Description
        "heroicons_outline:clipboard-document-check" -> Icons.Default.Assignment
        "heroicons_outline:users" -> Icons.Default.People
        "heroicons_outline:chart-pie" -> Icons.Default.PieChart
        "heroicons_outline:chart-bar" -> Icons.Default.BarChart
        "heroicons_outline:building-office" -> Icons.Default.Business
        "heroicons_outline:building-library" -> Icons.Default.AccountBalance
        "heroicons_outline:shopping-cart" -> Icons.Default.ShoppingCart
        "heroicons_outline:credit-card" -> Icons.Default.CreditCard
        "heroicons_outline:clipboard-list" -> Icons.Default.List
        "heroicons_outline:cube" -> Icons.Default.Category
        "heroicons_outline:qr-code" -> Icons.Default.QrCode
        "heroicons_outline:truck" -> Icons.Default.LocalShipping
        "heroicons_outline:folder" -> Icons.Default.Folder
        "heroicons_outline:home" -> Icons.Default.Home                     // Para "Empresa"
        "heroicons_outline:currency-dollar" -> Icons.Default.AttachMoney   // Para "Contabilidad"
        "heroicons_outline:bars-4" -> Icons.Default.ViewList              // Para "Areas"
        "heroicons_outline:exclamation-circle" -> Icons.Default.Error     // Para "Tipo de Afectacion"
        "heroicons_outline:building-storefront" -> Icons.Default.Store    // Para "Almacén"
        "heroicons_outline:circle-stack" -> Icons.Default.ViewModule
        else -> Icons.Default.Circle
    }
}
