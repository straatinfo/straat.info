package com.straatinfo.straatinfo.Utilities


const val LOCATION_RECORD_CODE = 1


const val BASE_URL = "https://straatinfo-backend-v2.herokuapp.com" // testing server
// const val BASE_URL = "https://straatinfo-backend-v2-prod.herokuapp.com" // production server

// authentication
const val LOGIN_URL = "$BASE_URL/v3/api/auth/login"
const val SIGNUP_V2 = "$BASE_URL/v1/api/registration/signupV3"
const val SIGNUP_V3 =  "$BASE_URL/v3/api/registration/signupV3"
const val REFRESH_AUTH = "$BASE_URL/v3/api/auth/refresh"
const val REGISTRATIN_VALIDATION = "$BASE_URL/v1/api/registration/validation"
const val FIREBASE_TOKEN_UPDATE = "$BASE_URL/v3/api/auth/firebase"


const val POST_CODE_API = "$BASE_URL/api/v1/utility/postcode"

// report
// @TODO need to change this to code instead of ids
const val REPORT_TYPE_C_ID = "5a7888bb04866e4742f74957"
const val REPORT_TYPE_B_ID = "5a7888bb04866e4742f74956"
const val REPORT_TYPE_A_ID = "5a7888bb04866e4742f74955"
const val REPORT_API = "$BASE_URL/v1/api/report"
const val REPORT_NEAR = "$BASE_URL/v1/api/report/near"
const val GET_MAIN_CAT = "$BASE_URL/v1/api/category/app/mainCategory/withGeneral/hostId/"
const val PUBLIC_UPLOAD = "$BASE_URL/v1/api/upload/public"
const val SEND_REPORT_V2 = "$BASE_URL/v1/api/report/V2"
const val GET_PUBLIC_REPORT_LIST = "$BASE_URL/v1/api/report/public" // params: _reporter, _reportType, language
const val GET_GENERAL_MAIN_CATEGORIES = "$BASE_URL/v1/api/category/app/mainCategory/general"

const val GEOCODE = "https://maps.googleapis.com/maps/api/geocode/json"
const val GOOGLE_API_KEY = "AIzaSyDuQd44hbjRx-70DSsFUWuAtt2uMe_Hotk"

// team related
const val TEAM_API = "$BASE_URL/v1/api/team"
const val TEAM_LIST = "$BASE_URL/v1/api/team/list/"
const val TEAM_LIST_V3 = "$BASE_URL/v3/api/teams/list"
const val TEAM_REQUEST = "$BASE_URL/v1/api/teamInvite/teamRequests/" // param: teamId
const val TEAM_INFO = "$BASE_URL/v1/api/team/info/" // param: teamId
const val TEAM_ACCEPT_MEMBER = "$BASE_URL/v1/api/teamInvite/acceptRequest/" // params: userId / teamId
const val TEAM_DECLINE_MEMBER = "$BASE_URL/v1/api/teamInvite/declineRequest/" // params: userId / teamId
const val TEAM_CREATE_NEW = "$BASE_URL/v1/api/team/new/" // param: userId
const val TEAM_REQUEST_COUNT = "$BASE_URL/v3/api/teams/requestCount" // params userId
const val TEAM_INDIVIDUAL_CHAT = "$BASE_URL/v3/api/teams/chat" // PARAMS teamId, userId

const val EMAL_REGEX = "(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"
const val UK_PHONE_REGEX = "^(?:(?:\\(?(?:0(?:0|11)\\)?[\\s-]?\\(?|\\+)44\\)?[\\s-]?(?:\\(?0\\)?[\\s-]?)?)|(?:\\(?0))(?:(?:\\d{5}\\)?[\\s-]?\\d{4,5})|(?:\\d{4}\\)?[\\s-]?(?:\\d{5}|\\d{3}[\\s-]?\\d{3}))|(?:\\d{3}\\)?[\\s-]?\\d{3}[\\s-]?\\d{3,4})|(?:\\d{2}\\)?[\\s-]?\\d{4}[\\s-]?\\d{4}))(?:[\\s-]?(?:x|ext\\.?|\\#)\\d{3,4})?\$"
const val PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@\$!%*#?&]{8,}\$"

const val WINDOW_INFO_REPORT_DATE_FORMAT = "d MMM yyyy"
const val TZ_ZULU = "yyyy-MM-dd'T'HH:mm:ss"

// G-Map
const val MAP_ZOOM = 16.500f

// Host
const val REQUEST_HOST_WITH_CODE = "$BASE_URL/v3/api/registration/validation/host"
const val REQUEST_HOST_BY_NAME = "$BASE_URL/v3/api/hosts/searchByName/" // params: hostName

// Messages
const val MESSAGES = "$BASE_URL/v2/api/message"
const val SEND_MESSAGE = "$BASE_URL/v3/api/message/send"
const val CONVERSATION_V2 = "$BASE_URL/v2/api/conversation"
const val UNREAD_MESSAGE = "$BASE_URL/v3/api/message/unread" // params: conversationId, userId
const val GET_ALL_UNREAD_MESSAGES_COUNT = "$BASE_URL/v3/api/message/unread/all/count" // params: userId

// Socket
const val SOCKET = "$BASE_URL/socket.io/"


// BROADCASTS

const val BROADCAST_REPORT_DATA_CHANGE = "BROADCAST_REPORT_DATA_CHANGE"
const val BROADCAST_NEW_MESSAGE_RECEIVED = "BROADCAST_NEW_MESSAGE_RECEIVED"
