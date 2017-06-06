import json
import requests
from firebase import firebase

firebase = firebase.FirebaseApplication('https://automatedparking-43b58.firebaseio.com/')

def send_notification(token_gcm, title, message):
    data = {
        'to': 'd9Y5oO9Ol9A:APA91bHViajMMu2IUavIGuQzg42S4UfsIMcVGkyMsCCVYJCsS3jHot4ijYUPpvArbTzTMpHaeN7jorlQCy7Wa9oFnqQBQiGIuU03oOeLsO42eJS44W0j-HiByrBjQ-uLJ-M-oYepX051',
        'notification': {'title': 'Aviso importante', 'text': 'Tu tiempo se esta agotando'}
    }
    headers = {
        "Content-Type": "application/json",
        "Authorization": "key=AAAAWUF2oGQ:APA91bFNkIO0-qlzhNL3oYFAhN6mpaYBPJD0P1zMMNuB2BblI7Md5xjYlLgfJKlcPLZ9LOiXULQLQz0h8M-Zx-8-U2aayr1-jEIEYl1fnT42rahEkiimUNtsdlcEsElFb3hsPu268nMk"
    }
    url = 'https://fcm.googleapis.com/fcm/send'

    r = requests.post(url, data=json.dumps(data), headers=headers)
    print(r.text)

def getReservations():
    arrayParking = {}
    arrayParking['parking_a'] = firebase.get('/parking_a/unlam/times', None)
    arrayParking['parking_b'] = firebase.get('/parking_a/unlam/times', None)
    return arrayParking;
