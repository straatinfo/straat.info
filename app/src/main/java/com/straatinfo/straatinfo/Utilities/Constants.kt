package com.straatinfo.straatinfo.Utilities


const val LOCATION_RECORD_CODE = 1


const val BASE_URL = "https://straatinfo-backend-v2.herokuapp.com" // testing server

// const val BASE_URL = "https://straatinfo-backend-v2-prod.herokuapp.com" // production server

const val LOGIN_URL = "$BASE_URL/v3/api/auth/login"
const val SIGNUP_V3 =  "$BASE_URL/v3/api/registration/signupV3"
const val REQUEST_HOST_WITH_CODE = "$BASE_URL/v3/api/registration/validation/host"
const val POST_CODE_API = "$BASE_URL/v3/api/utility/postcode"
const val REPORT_NEAR = "$BASE_URL/v1/api/report/near"
const val GET_MAIN_CAT = "$BASE_URL/v1/api/category/app/mainCategory/withGeneral/hostId/"

const val GEOCODE = "https://maps.googleapis.com/maps/api/geocode/json"
const val GOOGLE_API_KEY = "AIzaSyDuQd44hbjRx-70DSsFUWuAtt2uMe_Hotk"