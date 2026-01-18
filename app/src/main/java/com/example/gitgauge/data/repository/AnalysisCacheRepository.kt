package com.example.gitgauge.data.repository

import com.example.gitgauge.data.db.AnalysisCacheDao
import com.example.gitgauge.data.db.AnalysisCacheEntity
import com.example.gitgauge.data.model.AnalysisResponse
import com.example.gitgauge.data.model.RepositoryAnalysisInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalysisCacheRepository @Inject constructor(
    private val analysisCacheDao: AnalysisCacheDao
) {

    suspend fun saveAnalysis(
        owner: String,
        repo: String,
        ref: String,
        analysisResponse: AnalysisResponse
    ) {
        val entity = AnalysisCacheEntity(
            id = "$owner/$repo/$ref",
            owner = owner,
            repo = repo,
            summary = analysisResponse.summary,
            analysis = analysisResponse.analysis,
            filesAnalyzed = analysisResponse.repository.filesAnalyzed,
            ref = ref,
            timestamp = System.currentTimeMillis()
        )
        analysisCacheDao.insertAnalysis(entity)
    }

    suspend fun getAnalysis(owner: String, repo: String, ref: String): AnalysisResponse? {
        val entity = analysisCacheDao.getAnalysis(owner, repo, ref) ?: return null
        return AnalysisResponse(
            summary = entity.summary,
            analysis = entity.analysis,
            repository = RepositoryAnalysisInfo(
                owner = entity.owner,
                repo = entity.repo,
                ref = entity.ref,
                filesAnalyzed = entity.filesAnalyzed
            )
        )
    }

    suspend fun deleteAnalysis(owner: String, repo: String, ref: String) {
        val entity = analysisCacheDao.getAnalysis(owner, repo, ref) ?: return
        analysisCacheDao.deleteAnalysis(entity)
    }

    suspend fun clearCache() {
        analysisCacheDao.clearCache()
    }

    suspend fun clearOldAnalysis(ageInMillis: Long) {
        val expirationTime = System.currentTimeMillis() - ageInMillis
        analysisCacheDao.clearOldAnalysis(expirationTime)
    }
}
