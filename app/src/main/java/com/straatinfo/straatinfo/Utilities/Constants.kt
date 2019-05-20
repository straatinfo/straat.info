package com.straatinfo.straatinfo.Utilities


const val LOCATION_RECORD_CODE = 1


const val BASE_URL = "https://straatinfo-backend-v2.herokuapp.com" // testing server

// const val BASE_URL = "https://straatinfo-backend-v2-prod.herokuapp.com" // production server

const val LOGIN_URL = "$BASE_URL/v3/api/auth/login"
const val SIGNUP_V2 = "$BASE_URL/v1/api/registration/signupV3"
const val SIGNUP_V3 =  "$BASE_URL/v3/api/registration/signupV3"
const val REQUEST_HOST_WITH_CODE = "$BASE_URL/v3/api/registration/validation/host"
const val POST_CODE_API = "$BASE_URL/v3/api/utility/postcode"
const val REPORT_NEAR = "$BASE_URL/v1/api/report/near"
const val GET_MAIN_CAT = "$BASE_URL/v1/api/category/app/mainCategory/withGeneral/hostId/"
const val PUBLIC_UPLOAD = "$BASE_URL/v1/api/upload/public"
const val SEND_REPORT_V2 = "$BASE_URL/v1/api/report/V2"

const val GEOCODE = "https://maps.googleapis.com/maps/api/geocode/json"
const val GOOGLE_API_KEY = "AIzaSyDuQd44hbjRx-70DSsFUWuAtt2uMe_Hotk"

// team related
const val TEAM_API = "$BASE_URL/v1/api/team"
const val TEAM_LIST = "$BASE_URL/v1/api/team/list/"

const val EMAL_REGEX = "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"
const val UK_PHONE_REGEX = "^(?:(?:\\(?(?:0(?:0|11)\\)?[\\s-]?\\(?|\\+)44\\)?[\\s-]?(?:\\(?0\\)?[\\s-]?)?)|(?:\\(?0))(?:(?:\\d{5}\\)?[\\s-]?\\d{4,5})|(?:\\d{4}\\)?[\\s-]?(?:\\d{5}|\\d{3}[\\s-]?\\d{3}))|(?:\\d{3}\\)?[\\s-]?\\d{3}[\\s-]?\\d{3,4})|(?:\\d{2}\\)?[\\s-]?\\d{4}[\\s-]?\\d{4}))(?:[\\s-]?(?:x|ext\\.?|\\#)\\d{3,4})?\$"
const val PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])([a-z0-9_-]+)\$"

const val WINDOW_INFO_REPORT_DATE_FORMAT = "d MMM yyyy"