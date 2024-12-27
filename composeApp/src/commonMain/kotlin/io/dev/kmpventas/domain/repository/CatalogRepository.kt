package io.dev.kmpventas.domain.repository

import io.dev.kmpventas.data.remote.dto.UnitMeasurement
import io.dev.kmpventas.data.remote.dto.UnitMeasurementResponse

interface CatalogRepository {
    suspend fun getUnitMeasurements(page: Int = 0, size: Int = 20): Result<UnitMeasurementResponse>
    suspend fun getUnitMeasurementById(id: String): Result<UnitMeasurement>
    suspend fun createUnitMeasurement(unitMeasurement: UnitMeasurement): Result<UnitMeasurement>
    suspend fun updateUnitMeasurement(unitMeasurement: UnitMeasurement): Result<UnitMeasurement>
    suspend fun deleteUnitMeasurement(id: String): Result<Unit>
}