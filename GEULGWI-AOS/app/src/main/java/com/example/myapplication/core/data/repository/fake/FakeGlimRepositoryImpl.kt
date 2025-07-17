package com.example.myapplication.core.data.repository.fake

import com.example.myapplication.core.domain.model.Glim
import com.example.myapplication.core.domain.model.GlimInput
import com.example.myapplication.core.domain.repository.FakeGlimRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FakeGlimRepositoryImpl
    @Inject
    constructor() : FakeGlimRepository {
        private val glimDummyData = mutableListOf<Glim>()

        init {
            glimDummyData.add(
                Glim(
                    id = 1,
                    imgUrl = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800&h=600&fit=crop",
                    isLike = false,
                    likes = 142,
                    bookTitle = "알프스 소녀",
                    bookAuthor = "요한나 스피리",
                    bookImgUrl = "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400&h=600&fit=crop",
                ),
            )

            glimDummyData.add(
                Glim(
                    id = 2,
                    imgUrl = "https://images.unsplash.com/photo-1439066615861-d1af74d74000?w=800&h=600&fit=crop",
                    isLike = false,
                    likes = 89,
                    bookTitle = "호밀밭의 파수꾼",
                    bookAuthor = "J.D. 샐린저",
                    bookImgUrl = "https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400&h=600&fit=crop",
                ),
            )

            glimDummyData.add(
                Glim(
                    id = 3,
                    imgUrl = "https://images.unsplash.com/photo-1501594907352-04cda38ebc29?w=800&h=600&fit=crop",
                    isLike = false,
                    likes = 256,
                    bookTitle = "노인과 바다",
                    bookAuthor = "어니스트 헤밍웨이",
                    bookImgUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=600&fit=crop",
                ),
            )

            glimDummyData.add(
                Glim(
                    id = 4,
                    imgUrl = "https://images.unsplash.com/photo-1441974231531-c6227db76b6e?w=800&h=600&fit=crop",
                    isLike = false,
                    likes = 73,
                    bookTitle = "월든",
                    bookAuthor = "헨리 데이비드 소로",
                    bookImgUrl = "https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400&h=600&fit=crop",
                ),
            )

            glimDummyData.add(
                Glim(
                    id = 5,
                    imgUrl = "https://images.unsplash.com/photo-1464822759844-d150ad6c3b37?w=800&h=600&fit=crop",
                    isLike = false,
                    likes = 198,
                    bookTitle = "데미안",
                    bookAuthor = "헤르만 헤세",
                    bookImgUrl = "https://images.unsplash.com/photo-1495446815901-a7297e633e8d?w=400&h=600&fit=crop",
                ),
            )

            glimDummyData.add(
                Glim(
                    id = 6,
                    imgUrl = "https://images.unsplash.com/photo-1500375592092-40eb2168fd21?w=800&h=600&fit=crop",
                    isLike = false,
                    likes = 324,
                    bookTitle = "어린 왕자",
                    bookAuthor = "앙투안 드 생텍쥐페리",
                    bookImgUrl = "https://images.unsplash.com/photo-1543002588-bfa74002ed7e?w=400&h=600&fit=crop",
                ),
            )

            glimDummyData.add(
                Glim(
                    id = 7,
                    imgUrl = "https://images.unsplash.com/photo-1449824913935-59a10b8d2000?w=800&h=600&fit=crop",
                    isLike = false,
                    likes = 156,
                    bookTitle = "연금술사",
                    bookAuthor = "파울로 코엘료",
                    bookImgUrl = "https://images.unsplash.com/photo-1481627834876-b7833e8f5570?w=400&h=600&fit=crop",
                ),
            )

            glimDummyData.add(
                Glim(
                    id = 8,
                    imgUrl = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800&h=600&fit=crop",
                    isLike = false,
                    likes = 211,
                    bookTitle = "시데타르",
                    bookAuthor = "헤르만 헤세",
                    bookImgUrl = "https://images.unsplash.com/photo-1495446815901-a7297e633e8d?w=400&h=600&fit=crop",
                ),
            )

            glimDummyData.add(
                Glim(
                    id = 9,
                    imgUrl = "https://images.unsplash.com/photo-1418489098061-ce87b5dc3aee?w=800&h=600&fit=crop",
                    isLike = false,
                    likes = 94,
                    bookTitle = "파도",
                    bookAuthor = "버지니아 울프",
                    bookImgUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=600&fit=crop",
                ),
            )

            glimDummyData.add(
                Glim(
                    id = 10,
                    imgUrl = "https://images.unsplash.com/photo-1472214103451-9374bd1c798e?w=800&h=600&fit=crop",
                    isLike = false,
                    likes = 167,
                    bookTitle = "봄날은 간다",
                    bookAuthor = "김유정",
                    bookImgUrl = "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=400&h=600&fit=crop",
                ),
            )
        }

        override fun getGlimData(): Flow<List<Glim>> =
            flow {
                emit(glimDummyData)
            }

        override fun saveGlimData(data: GlimInput) =
            flow {
                if (data.bookId.isEmpty()) {
                    emit(false)
                    return@flow
                }
                val newGlim =
                    Glim(
                        id = (glimDummyData.size + 1).toLong(),
                        imgUrl = "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=800&h=600&fit=crop",
                        isLike = false,
                        likes = 0,
                        bookTitle = "봄날은 간다",
                        bookAuthor = "김유정",
                        bookImgUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=600&fit=crop",
                    )
                glimDummyData.add(newGlim)
                emit(true)
            }
    }
