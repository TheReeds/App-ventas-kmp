package io.dev.kmpventas.presentation.navigation

object Routes {
    // Rutas base
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val HOME = "home"
    const val ONBOARDING = "onboarding"

    object HomeScreen {
        private const val HOME_PREFIX = "/homeScreen"

        // Configuración
        object Setup {
            private const val SETUP = "$HOME_PREFIX/setup"
            const val USER = "$SETUP/user"
            const val USER_COMPANY = "$SETUP/user-company"
            const val COMPANY = "$SETUP/company"
            const val MODULE = "$SETUP/module"
            const val PARENT_MODULE = "$SETUP/parent-module"
            const val ROLE = "$SETUP/role"
        }

        // Catálogo
        object Catalog {
            private const val CATALOG = "$HOME_PREFIX/catalog"
            const val UNIT_MEASUREMENT = "$CATALOG/unit-measurement"
            const val CATEGORY = "$CATALOG/category"
        }

        // Contabilidad
        object Accounting {
            private const val ACCOUNTING = "$HOME_PREFIX/accounting"
            const val AREAS = "$ACCOUNTING/areas"
            const val TYPE_AFFECTATION = "$ACCOUNTING/typeAfeectation"
            const val TYPE_DOCUMENT = "$ACCOUNTING/typeDocument"
            const val ACCOUNTING_PLAN = "$ACCOUNTING/accoutingPlan"
            const val ACCOUNTING_DYNAMICS = "$ACCOUNTING/accountingDynamics"
            const val ACCOUNTING_ACCOUNT_CLASS = "$ACCOUNTING/accountingAccountClass"
            const val STORES = "$ACCOUNTING/stores"
        }
    }
}