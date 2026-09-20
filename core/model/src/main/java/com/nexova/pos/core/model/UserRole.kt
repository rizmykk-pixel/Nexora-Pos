package com.nexova.pos.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class UserRole { 
    OWNER, 
    HQ_ADMIN, 
    REGIONAL_MANAGER, 
    ENTITY_MANAGER, 
    OUTLET_MANAGER, 
    SUPERVISOR, 
    CASHIER, 
    STAFF, 
    ACCOUNTANT, 
    WAREHOUSE, 
    PROCUREMENT, 
    AUDITOR, 
    SUPPORT,
    CUSTOM
}
