package ru.netology.nmedia.repository

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.netology.nmedia.dto.Event
import ru.netology.nmedia.dto.Post
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
interface RepositoryModule {
    @Singleton
    @Binds
    fun bindsPostRepository(impl: PostRepositoryImpl): BaseRepository<Post>
    @Singleton
    @Binds
    fun bindsEventRepository(impl: EventRepositoryImpl): BaseRepository<Event>
    @Singleton
    @Binds
    fun bindsUserRepository(impl: UserRepositoryImpl): UserRepository
    @Singleton
    @Binds
    fun bindsJobRepository(impl: JobRepositoryImpl): JobRepository
}