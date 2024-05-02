package com.example.chillinapp.data.stress

import com.example.chillinapp.data.ServiceResult

class FirebaseStressDataService(private val stressDataDao: FirebaseStressDataDao):
    StressDataService {
    override suspend fun insertRawData(stressData: List<StressRawData>): ServiceResult<Unit, StressErrorType> =
        stressDataDao.insertRawData(stressData)

    override suspend fun getRawData(n: Int): ServiceResult<List<StressRawData>, StressErrorType> =
        stressDataDao.getRawData(n)

}