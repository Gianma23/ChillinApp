package com.example.chillinapp.data.stress

import com.example.chillinapp.data.ServiceResult

interface StressDataService {
    suspend fun insertRawData(stressData: List<StressRawData>) : ServiceResult<Unit,StressErrorType>
    suspend fun getRawData(n: Int) : ServiceResult <List <StressRawData>,StressErrorType>
    suspend fun getfaster(): ServiceResult <List <StressRawData>,StressErrorType>
    suspend fun insertDerivedData(stressData:List<StressDerivedData>):ServiceResult<Unit,StressErrorType>
    suspend fun getDerivedData(n:Int):ServiceResult <List<StressDerivedData>,StressErrorType>
}