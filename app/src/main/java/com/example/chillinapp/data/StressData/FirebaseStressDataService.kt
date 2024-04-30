package com.example.chillinapp.data.StressData

import com.example.chillinapp.data.ServiceResult

class FirebaseStressDataService():
    StressDataService {
    override suspend fun InsertData(stressData: StressData): ServiceResult<Unit, StressErrorType> {
        TODO("Not yet implemented")
    }

}