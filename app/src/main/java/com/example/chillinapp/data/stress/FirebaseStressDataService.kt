package com.example.chillinapp.data.stress

import com.example.chillinapp.data.ServiceResult

class FirebaseStressDataService(private val stressDataDao: FirebaseStressDataDao):
    StressDataService {
    override suspend fun insertRawData(stressData: List<StressRawData>): ServiceResult<Unit, StressErrorType> =
        stressDataDao.insertRawData(stressData)

    override suspend fun getRawData(Starttime: Long, Endtime: Long): ServiceResult<List<StressRawData>, StressErrorType> =
        stressDataDao.getRawData(Starttime, Endtime)

    override suspend fun getFaster(): ServiceResult<List<StressRawData>, StressErrorType> =
        stressDataDao.fastGet()

    override suspend fun insertDerivedData(stressData: List<StressDerivedData>): ServiceResult<Unit, StressErrorType> =
        stressDataDao.insertDerivedData(stressData)

    override suspend fun getDerivedData(Starttime:Long,Endtime:Long): ServiceResult<List<StressDerivedData>, StressErrorType> =
    stressDataDao.getDerivedData(Starttime,Endtime)

    override suspend fun getavgRawData(sincewhen: Long): ServiceResult<StressRawData?, StressErrorType> =
        stressDataDao.avgRawData(sincewhen)


}