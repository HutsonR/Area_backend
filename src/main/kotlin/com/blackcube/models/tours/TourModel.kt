package com.blackcube.models.tours

import kotlinx.serialization.Serializable

/**
 * Модель данных, представляющая тур
 *
 * @property id Уникальный идентификатор тура
 * @property imageUrl URL изображения тура
 * @property title Название тура
 * @property description Описание тура
 * @property duration Продолжительность тура (например, 1.5 часа)
 * @property distance Дистанция тура (например, 12 км.)
 * @property isCompleted Флаг, указывающий, завершен ли тур
 * @property isStarted Флаг, указывающий, начат ли тур
 * @property isAR Флаг, указывающий, поддерживает ли тур дополненную реальность (AR)
 * @property histories Список историй, связанных с туром
 */
@Serializable
data class TourModel(
    val id: String,
    val imageUrl: String,
    val title: String,
    val description: String,
    val duration: String,
    val distance: String,
    val isCompleted: Boolean,
    val isStarted: Boolean,
    val isAR: Boolean,
    val histories: List<HistoryModel>
)
