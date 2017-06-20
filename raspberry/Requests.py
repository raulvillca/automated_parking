import json
from time import gmtime, strftime
import requests
from firebase import firebase

token_gcm = 'd9Y5oO9Ol9A:APA91bHViajMMu2IUavIGuQzg42S4UfsIMcVGkyMsCCVYJCsS3jHot4ijYUPpvArbTzTMpHaeN7jorlQCy7Wa9oFnqQBQiGIuU03oOeLsO42eJS44W0j-HiByrBjQ-uLJ-M-oYepX051'

headers = {
    "Content-Type": "application/json",
    "Authorization": "key=AAAAWUF2oGQ:APA91bFNkIO0-qlzhNL3oYFAhN6mpaYBPJD0P1zMMNuB2BblI7Md5xjYlLgfJKlcPLZ9LOiXULQLQz0h8M-Zx-8-U2aayr1-jEIEYl1fnT42rahEkiimUNtsdlcEsElFb3hsPu268nMk"
}
url = 'https://fcm.googleapis.com/fcm/send'
firebase = firebase.FirebaseApplication('https://automatedparking-43b58.firebaseio.com/')

def send_notification(time_firebase, token_gcm, title, message):

    data = {
        'to': token_gcm,
        'notification': {'title': title, 'text': message}
    }

    time_now = strftime("%H:%M", gmtime())

    hh_fb, mm_fb = time_firebase.split(':')
    hh_now, mm_now = time_now.split(':')

    hh_fb = hh_fb*100
    hh_fb = hh_fb + mm_fb

    hh_now = hh_now*100
    hh_now = hh_now + mm_now - 10

    if hh_fb <= hh_now:
        return False;
    else:
        r = requests.post(url, data=json.dumps(data), headers=headers)
        print(r.text)
        return True;

def getAReservations():
    arrayParking = {}
    arrayParking['parking_a'] = firebase.get('/parking_a/unlam/times', None)

    i = 0
    arrayItems = {}
    for path in arrayParking['parking_a']:
        arrayItems[i] = firebase.get('/parking_a/unlam/times/'+path, None)
        i += 1

    return arrayItems;

def getBReservations():
    arrayParking = {}
    arrayParking['parking_b'] = firebase.get('/parking_a/unlam/times', None)

    i = 0
    arrayItems = {}
    for path in arrayParking['parking_b']:
        arrayItems[i] = firebase.get('/parking_a/unlam/times/'+path, None)
        i += 1

    return arrayItems;

def greaterThanTimeNow(time_firebase):
    time_now = strftime("%H:%M", gmtime())

    hh_fb, mm_fb = time_firebase.split(':')
    hh_now, mm_now = time_now.split(':')

    hh_fb = hh_fb*100
    hh_fb = hh_fb + mm_fb

    hh_now = hh_now*100
    hh_now = hh_now + mm_now

    if hh_fb <= hh_now:
        return False;
    else:
        return True;

def removeItem(item_firebase):
    firebase.delete('/parking_a/unlam/times/'+item_firebase['time_id'], None)
""
arrayA = getAReservations()
arrayB = getBReservations()

print ("A")
i = 0
while i < len(arrayA):
    print(arrayA[i]['start_time'])
    print(arrayA[i]['final_time'])
    print(arrayA[i]['user_gcm'])
    i += 1

print ("B")
i = 0
while i < len(arrayB):
    print(arrayB[i]['start_time'])
    print(arrayB[i]['final_time'])
    print(arrayB[i]['user_gcm'])
    i += 1

""
