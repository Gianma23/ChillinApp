package com.example.chillinapp.data.StressData

import com.example.chillinapp.data.ServiceResult

class FirebaseStressDataService(private val stressDataDao: FirebaseStressDataDao):
    StressDataService {
    override suspend fun InsertRawData(stressData: StressRawData): ServiceResult<Unit, StressErrorType> =
        stressDataDao.InsertRawData(stressData)

}