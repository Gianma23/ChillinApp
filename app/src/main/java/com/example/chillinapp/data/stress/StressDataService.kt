package com.example.chillinapp.data.stress

import com.example.chillinapp.data.ServiceResult

interface StressDataService {
    suspend fun insertRawData(stressData: List<StressRawData>) : ServiceResult<Unit,StressErrorType>
}