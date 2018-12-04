package com.wk.unichat.Utils

const val BASE_URL = "https://unichatchat.herokuapp.com/v1/" // DB adres
const val SOCKET_URL = "https://unichatchat.herokuapp.com/"
const val URL_REGISTER = "${BASE_URL}account/register"  // Rejestracja
const val URL_LOGIN = "${BASE_URL}account/login" // Login
const val URL_CREATE_USER = "${BASE_URL}user/add" // Dodanie
const val URL_GET_CHANNELS = "${BASE_URL}channel/" // Adres kanałów
const val URL_GET_USER = "${BASE_URL}user/byEmail/" // Wyciąganie użytkownika po mailu
const val BROADCAST_USER_UPDATE = "BROADCAST_USER_UPDATE"
const val URL_GET_MESSAGES = "${BASE_URL}message/byChannel/"