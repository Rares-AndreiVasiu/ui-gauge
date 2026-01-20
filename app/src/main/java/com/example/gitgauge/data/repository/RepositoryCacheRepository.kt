package com.example.gitgauge.data.repository

import com.example.gitgauge.data.db.RepositoryDao
import com.example.gitgauge.data.db.RepositoryEntity
import com.example.gitgauge.data.model.RepositoryItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepositoryCacheRepository @Inject constructor(
    private val repositoryDao: RepositoryDao
) {

    suspend fun saveRepositories(repositories: List<RepositoryItem>) {
        val entities = repositories.map { repo ->
            RepositoryEntity(
                id = repo.id,
                name = repo.name,
                description = repo.description,
                htmlUrl = repo.htmlUrl,
                starsCount = repo.starsCount
            )
        }
        repositoryDao.insertRepositories(entities)
    }

    suspend fun getCachedRepositories(): List<RepositoryItem> {
        val entities = repositoryDao.getAllRepositories()
        return entities.map { entity ->
            RepositoryItem(
                id = entity.id,
                name = entity.name,
                description = entity.description,
                htmlUrl = entity.htmlUrl,
                starsCount = entity.starsCount
            )
        }
    }

    suspend fun clearCache() {
        repositoryDao.clearRepositories()
    }

    suspend fun getRepositoryCount(): Int {
        return repositoryDao.getRepositoryCount()
    }
}

