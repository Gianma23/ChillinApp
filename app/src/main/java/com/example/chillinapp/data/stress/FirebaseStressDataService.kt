package com.example.chillinapp.data.stress

import com.example.chillinapp.data.ServiceResult

class FirebaseStressDataService(private val stressDataDao: FirebaseStressDataDao):
    StressDataService {
    override suspend fun insertRawData(stressData: List<StressRawData>): ServiceResult<Unit, StressErrorType> =
        stressDataDao.insertRawData(stressData)

    override suspend fun getRawData(n: Int): ServiceResult<List<StressRawData>, StressErrorType> =
        stressDataDao.getRawData(n)

    override suspend fun getfaster(): ServiceResult<List<StressRawData>, StressErrorType> =
        stressDataDao.fastget()

    override suspend fun insertDerivedData(stressData: List<StressDerivedData>): ServiceResult<Unit, StressErrorType> =
        stressDataDao.insertDerivedData(stressData)

    override suspend fun getDerivedData(n: Int): ServiceResult<List<StressDerivedData>, StressErrorType> =
    stressDataDao.getDerivedData(n)

    override suspend fun getavgRawData(sincewhen: Long): ServiceResult<StressRawData?, StressErrorType> =
        stressDataDao.avgRawData(sincewhen)







}