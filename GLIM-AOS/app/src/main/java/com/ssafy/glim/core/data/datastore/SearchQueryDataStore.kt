package com.ssafy.glim.core.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchHistoryDataStore
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) {
        companion object {
            private val SEARCH_HISTORY_KEY = stringPreferencesKey("search_history")
            private const val DELIMITER = "|" // 구분자
            private const val MAX_HISTORY_SIZE = 10 // 최대 저장할 검색어 개수
        }

        /**
         * 검색 기록 관찰
         */
        fun getSearchHistory(): Flow<List<String>> {
            return dataStore.data.map { preferences ->
                val historyString = preferences[SEARCH_HISTORY_KEY] ?: ""
                if (historyString.isEmpty()) {
                    emptyList()
                } else {
                    historyString.split(DELIMITER)
                }
            }
        }

        /**
         * 검색어 추가
         * - 이미 존재하는 검색어는 맨 앞으로 이동
         * - 새로운 검색어는 맨 앞에 추가
         * - 최대 개수 초과 시 오래된 것부터 제거
         */
        suspend fun addSearchQuery(query: String) {
            if (query.isBlank()) return

            // 구분자가 포함된 검색어는 저장하지 않음 (데이터 무결성)
            if (query.contains(DELIMITER)) return

            dataStore.edit { preferences ->
                val currentString = preferences[SEARCH_HISTORY_KEY] ?: ""
                val currentList =
                    if (currentString.isEmpty()) {
                        emptyList()
                    } else {
                        currentString.split(DELIMITER)
                    }

                // 기존 검색어 제거 (중복 방지) 후 맨 앞에 새 검색어 추가
                val newList =
                    (listOf(query) + currentList.filter { it != query })
                        .take(MAX_HISTORY_SIZE)

                preferences[SEARCH_HISTORY_KEY] = newList.joinToString(DELIMITER)
            }
        }

        /**
         * 특정 검색어 삭제
         */
        suspend fun removeSearchQuery(query: String) {
            dataStore.edit { preferences ->
                val currentString = preferences[SEARCH_HISTORY_KEY] ?: ""
                if (currentString.isEmpty()) return@edit

                val currentList = currentString.split(DELIMITER)
                val newList = currentList.filter { it != query }

                preferences[SEARCH_HISTORY_KEY] =
                    if (newList.isEmpty()) {
                        "" // 빈 문자열로 저장
                    } else {
                        newList.joinToString(DELIMITER)
                    }
            }
        }

        /**
         * 모든 검색 기록 삭제
         */
        suspend fun clearSearchHistory() {
            dataStore.edit { preferences ->
                preferences.remove(SEARCH_HISTORY_KEY)
            }
        }
    }
