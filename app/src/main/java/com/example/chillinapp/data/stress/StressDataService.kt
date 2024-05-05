package com.example.chillinapp.data.stress

import com.example.chillinapp.data.ServiceResult


/**
 * Interface for a service that handles operations related to stress data.
 *
 * This interface defines methods for inserting raw stress data, retrieving raw stress data, and getting faster stress data.
 * The methods are suspend functions, meaning they are designed to be used with Kotlin's coroutines and can perform long-running operations such as network requests or database operations.
 * Each method returns a ServiceResult, which is a wrapper class that can hold either a successful result or an error.
 */
interface StressDataService {
    /**
     * Inserts a list of raw stress data.
     *
     * @param stressData The list of stress data to insert.
     * @return A ServiceResult instance containing the result of the operation. If the operation was successful, the success flag is set to true and the data field is null. If an error occurred, the success flag is set to false and the error field contains a StressErrorType indicating the type of error.
     */
    suspend fun insertRawData(stressData: List<StressRawData>) : ServiceResult<Unit,StressErrorType>

    /**
     * Retrieves a specified number of raw stress data.
     *
     * @param n The number of stress data to retrieve.
     * @return A ServiceResult instance containing the result of the operation. If the operation was successful, the success flag is set to true and the data field contains the retrieved stress data. If an error occurred, the success flag is set to false and the error field contains a StressErrorType indicating the type of error.
     */
    suspend fun getRawData(n: Int) : ServiceResult <List <StressRawData>,StressErrorType>

    /**
     * Retrieves faster stress data.
     *
     * This method is intended to retrieve stress data more quickly than the getRawData method, but the exact implementation is up to the classes that implement this interface.
     * @return A ServiceResult instance containing the result of the operation. If the operation was successful, the success flag is set to true and the data field contains the retrieved stress data. If an error occurred, the success flag is set to false and the error field contains a StressErrorType indicating the type of error.
     */
    suspend fun getFaster(): ServiceResult <List <StressRawData>,StressErrorType>
}