package com.example.chillinapp.data.StressData

import com.example.chillinapp.data.ServiceResult

interface StressDataService {
    suspend fun InsertRawData(stressData: StressRawData) : ServiceResult<Unit,StressErrorType>
}