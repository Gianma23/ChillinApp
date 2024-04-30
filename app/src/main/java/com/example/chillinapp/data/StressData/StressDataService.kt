package com.example.chillinapp.data.StressData

import com.example.chillinapp.data.ServiceResult

interface StressDataService {
    suspend fun InsertData(stressData: StressData) : ServiceResult<Unit,StressErrorType>
}