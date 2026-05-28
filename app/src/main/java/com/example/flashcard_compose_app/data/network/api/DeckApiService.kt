package com.example.flashcard_compose_app.data.network.api

import com.example.flashcard_compose_app.data.network.dto.DeckDTO
import com.example.flashcard_compose_app.data.network.dto.RecentDeckDTO
import com.example.flashcard_compose_app.data.network.dto.UnitDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DeckApiService {

    @GET("decks")
    suspend fun getAllDecks(
        @Query("userId") userId: Int
    ): Response<List<DeckDTO>>

    @GET("decks/recent")
    suspend fun getRecentDecks(
        @Query("userId") userId: Int
    ): Response<List<RecentDeckDTO>>

    @GET("decks/{deckId}/subdecks")
    suspend fun getSubDecks(
        @Path("deckId") deckId: Int,
        @Query("userId") userId: Int
    ): Response<List<DeckDTO>>

    @GET("decks/{deckId}/units")
    suspend fun getUnitsByDeckId(
        @Path("deckId") deckId: Int,
        @Query("userId") userId: Int
    ): Response<List<UnitDTO>>
}