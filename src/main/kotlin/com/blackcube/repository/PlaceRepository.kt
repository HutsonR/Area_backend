package com.blackcube.repository

import com.blackcube.models.places.PlaceModel
import com.blackcube.models.tours.HistoryModel
import com.blackcube.models.tours.TourModel

interface PlaceRepository {
    fun getPlaces(limit: Int? = null): List<PlaceModel>
    fun getPlaceById(id: String): PlaceModel?
}

class PlaceRepositoryImpl : PlaceRepository {

    private val places: MutableList<PlaceModel> = mutableListOf(
        PlaceModel(
            id = "1",
            imageUrl = "https://st10.styapokupayu.ru/ckeditor_assets/pictures/000/573/253_content.jpg",
            title = "Мемориал Тачанка-Ростовчанка",
            description = "Если вы въедете в город со стороны Восточного шоссе, вам встретится огромный мемориал Тачанка-Ростовчанка. Монумент высотой 15 метров, открытый в 1977 году, посвящён памяти Октябрьской революции. Здесь можно сделать красивые фото на память.",
            lat = 47.184078,
            lon = 39.738757
        ),
        PlaceModel(
            id = "2",
            imageUrl = "https://st10.styapokupayu.ru/ckeditor_assets/pictures/000/573/254_content.jpg",
            title = "Стела Освободителям Ростова и Театральная площадь",
            description = "На Театральной площади возвышается величественная стела «Освободителям Ростова». Здесь же располагается вход в Парк Революции с фонтаном и большим колесом обозрения, а также Театр драмы и кафе. Это одна из визитных карточек города.",
            lat = 47.225953,
            lon = 39.745845
        ),
        PlaceModel(
            id = "3",
            imageUrl = "https://st10.styapokupayu.ru/ckeditor_assets/pictures/000/573/255_content.jpg",
            title = "Парамоновские склады",
            description = "Руины 19 века, оставшиеся от комплекса для хранения пшеницы купца Парамонова. Склады привлекают своей архитектурой, видами на левый берег Дона и зеленью, являясь одной из известных достопримечательностей Ростова.",
            lat = 47.218198,
            lon = 39.727022
        ),
        PlaceModel(
            id = "4",
            imageUrl = "https://st10.styapokupayu.ru/ckeditor_assets/pictures/000/573/261_content.jpg",
            title = "Набережная Ростова",
            description = "Прогулка по набережной открывает захватывающие виды на реку Дон, множество кафе, скульптуры, уличных музыкантов и возможность расслабиться в атмосфере настоящего курортного вайба.",
            lat = 47.217090,
            lon = 39.726840
        ),
        PlaceModel(
            id = "5",
            imageUrl = "https://st10.styapokupayu.ru/ckeditor_assets/pictures/000/573/262_content.jpg",
            title = "Парк Левобережный",
            description = "Современный парк на левом берегу Дона с аллеями, кафе, велодорожками и площадками для спорта и отдыха. Здесь можно насладиться природой и активным отдыхом в городе.",
            lat = 47.212652,
            lon = 39.731030
        ),
        PlaceModel(
            id = "6",
            imageUrl = "https://st10.styapokupayu.ru/ckeditor_assets/pictures/000/573/263_content.jpg",
            title = "Улица Большая Садовая",
            description = "Главная пешеходная улица Ростова, известная своей архитектурой, входами в парки, историческими зданиями и кафе. Здесь всегда кипит жизнь и можно прочувствовать атмосферу города.",
            lat = 47.222568,
            lon = 39.719415
        ),
        PlaceModel(
            id = "7",
            imageUrl = "https://st10.styapokupayu.ru/ckeditor_assets/pictures/000/573/264_content.jpg",
            title = "Ботанический сад ЮФУ",
            description = "Огромный ботанический сад площадью 260 гектаров, где собраны экзотические растения, оранжерея и зимний сад. Идеальное место для пикников и спокойных прогулок.",
            lat = 47.230609,
            lon = 39.659685
        ),
        PlaceModel(
            id = "8",
            imageUrl = "https://st10.styapokupayu.ru/ckeditor_assets/pictures/000/573/265_content.jpg",
            title = "Улица Пушкинская",
            description = "Живая пешеходная улица с кафе, музеями, памятниками и фонтанчиками. Здесь проходят уличные выставки, концерты и ярмарки, создающие неповторимую атмосферу.",
            lat = 47.225903,
            lon = 39.719046
        ),
        PlaceModel(
            id = "9",
            imageUrl = "https://st10.styapokupayu.ru/ckeditor_assets/pictures/000/573/269_content.jpg",
            title = "Ростовский-на-Дону зоопарк",
            description = "Один из крупнейших зоопарков, где обитает свыше 7000 животных. Зоопарк порадует детей и взрослых своими ухоженными зонами, кафе и возможностью провести целый день на свежем воздухе.",
            lat = 47.247493,
            lon = 39.672729
        ),
        PlaceModel(
            id = "10",
            imageUrl = "https://st10.styapokupayu.ru/ckeditor_assets/pictures/000/573/270_content.jpg",
            title = "Ростовский цирк",
            description = "Знаковое здание с яркими представлениями, где выступают артисты цирка, клоуны, акробаты и фокусники. Цирк радует гостей масштабными шоу и традиционной атмосферой.",
            lat = 47.224385,
            lon = 39.704960
        ),
        PlaceModel(
            id = "11",
            imageUrl = "https://st10.styapokupayu.ru/ckeditor_assets/pictures/000/573/271_content.jpg",
            title = "Дельфинарий",
            description = "Уникальное развлекательное заведение, где можно посмотреть шоу с дельфинами, сфотографироваться с обитателями океана и даже попытаться поплавать вместе с ними по предварительной записи.",
            lat = 47.208629,
            lon = 39.626272
        ),
        PlaceModel(
            id = "12",
            imageUrl = "https://st10.styapokupayu.ru/ckeditor_assets/pictures/000/573/272_content.jpg",
            title = "Музей ИЗО",
            description = "Музей изобразительных искусств, расположенный в старинном особняке, где собрана коллекция из более 6000 произведений искусства: картины, скульптуры и декоративно-прикладное искусство.",
            lat = 47.225706,
            lon = 39.715811
        ),
        PlaceModel(
            id = "13",
            imageUrl = "https://st10.styapokupayu.ru/ckeditor_assets/pictures/000/573/273_content.jpg",
            title = "Театр драмы им. М. Горького",
            description = "Культовый театр, известный своими масштабными постановками и необычным оформлением. Здание театра стало визитной карточкой Ростова, где регулярно проходят спектакли, концерты и фестивали.",
            lat = 47.228394,
            lon = 39.744792
        )
    )

    override fun getPlaces(limit: Int?): List<PlaceModel> {
        return if (limit != null) {
            places.take(limit)
        } else{
            places
        }
    }

    override fun getPlaceById(id: String): PlaceModel? = places.find { it.id == id }
}
