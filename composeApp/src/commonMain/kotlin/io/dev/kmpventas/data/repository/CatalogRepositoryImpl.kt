package io.dev.kmpventas.data.repository

import io.dev.kmpventas.data.remote.api.ApiService
import io.dev.kmpventas.data.remote.dto.UnitMeasurement
import io.dev.kmpventas.data.remote.dto.UnitMeasurementResponse
import io.dev.kmpventas.domain.repository.CatalogRepository

class CatalogRepositoryImpl(
    private val apiService: ApiService
) : CatalogRepository {

    override suspend fun getUnitMeasurements(page: Int, size: Int): Result<UnitMeasurementResponse> {
        return try {
            Result.success(apiService.getUnitMeasurements(page, size))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUnitMeasurementById(id: String): Result<UnitMeasurement> {
        return try {
            Result.success(apiService.getUnitMeasurementById(id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createUnitMeasurement(unitMeasurement: UnitMeasurement): Result<UnitMeasurement> {
        return try {
            Result.success(apiService.createUnitMeasurement(unitMeasurement))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUnitMeasurement(unitMeasurement: UnitMeasurement): Result<UnitMeasurement> {
        return try {
            Result.success(apiService.updateUnitMeasurement(unitMeasurement.id, unitMeasurement))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteUnitMeasurement(id: String): Result<Unit> {
        return try {
            Result.success(apiService.deleteUnitMeasurement(id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}