package io.dev.kmpventas.presentation.components

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
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
        // User Profile Section
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.padding(16.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Menu Items with Scroll
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

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // Logout Button
        ListItem(
            headlineContent = { Text("Cerrar Sesión") },
            leadingContent = {
                Icon(
                    Icons.Default.ExitToApp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .clip(MaterialTheme.shapes.medium)
                .clickable {
                    // Llamar directamente a onLogout
                    onLogout()
                }
        )

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

    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        isExpanded -> MaterialTheme.colorScheme.surfaceVariant
        else -> Color.Transparent
    }

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
        color = backgroundColor
    ) {
        Column {
            ListItem(
                headlineContent = {
                    Text(
                        text = menuItem.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = if (isSelected) {
                            MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        } else {
                            MaterialTheme.typography.bodyLarge
                        }
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = getIconForName(menuItem.icon),
                        contentDescription = null,
                        tint = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                },
                trailingContent = if (!menuItem.children.isNullOrEmpty()) {
                    {
                        IconButton(onClick = onExpandToggle) {
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (isExpanded) "Colapsar" else "Expandir"
                            )
                        }
                    }
                } else null,
                modifier = Modifier.clickable(
                    enabled = menuItem.link.isNotEmpty()
                ) {
                    if (menuItem.link.isNotEmpty()) {
                        onNavigate(menuItem.link)
                    }
                    if (!menuItem.children.isNullOrEmpty()) {
                        onExpandToggle()
                    }
                }
            )

            if (isExpanded && !menuItem.children.isNullOrEmpty()) {
                menuItem.children.forEach { childItem ->
                    MenuItemComponent(
                        menuItem = childItem,
                        currentRoute = currentRoute,
                        isExpanded = false, // Los items hijos no tienen expansión
                        onExpandToggle = { }, // Los items hijos no tienen expansión
                        onNavigate = onNavigate,
                        level = level + 1
                    )
                }
            }
        }
    }
}

private fun getIconForName(iconName: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (iconName) {
        "fa fa-pie-chart" -> Icons.Default.PieChart
        "fa fa-bar-chart" -> Icons.Default.BarChart
        "fa fa-cogs" -> Icons.Default.Settings
        "fa fa-user-secret" -> Icons.Default.Security
        "fa fa-user-circle-o" -> Icons.Default.Person
        "fa fa-users" -> Icons.Default.Group
        "fa fa-calendar-o" -> Icons.Default.DateRange
        "fa fa-file" -> Icons.Default.Description
        "fa fa-building-o" -> Icons.Default.Business
        "fa fa-bars" -> Icons.Default.Menu
        "fa fa-university" -> Icons.Default.AccountBalance
        "fa fa-cart-arrow-down" -> Icons.Default.ShoppingCart
        "fa fa-cart-plus" -> Icons.Default.AddShoppingCart
        "fa fa-area-chart" -> Icons.Default.ShowChart
        "fa fa-credit-card" -> Icons.Default.CreditCard
        "fa fa-list-alt" -> Icons.Default.List
        "fa fa-thermometer-empty" -> Icons.Default.Thermostat
        "fa fa-cube" -> Icons.Default.Category
        "fa fa-cubes" -> Icons.Default.Inventory
        "fa fa-qrcode" -> Icons.Default.QrCode
        "fa fa-truck" -> Icons.Default.LocalShipping
        "fa fa-folder-open" -> Icons.Default.Folder
        // Añade más mappings según necesites
        else -> Icons.Default.Circle
    }
}
