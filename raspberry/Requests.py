import json
from time import gmtime, strftime
import requests
from firebase import firebase

token_gcm = 'd9Y5oO9Ol9A:APA91bHViajMMu2IUavIGuQzg42S4UfsIMcVGkyMsCCVYJCsS3jHot4ijYUPpvArbTzTMpHaeN7jorlQCy7Wa9oFnqQBQiGIuU03oOeLsO42eJS44W0j-HiByrBjQ-uLJ-M-oYepX051'

headers = {
    "Content-Type": "application/json",
    "Authorization": "key=AAAAQ68mVz0:APA91bGsT3lt6PSPZzk7f-Pfr01DedkKrCMEigmqdLoDSINcbxobaZlaYGLmScUWwb_IMj9-q4pWDlwX-bw6BckR2Ddt1isAr4P3zrjM7xt47bNHH3frVV861YpzSDK5zn-XOPZwTpVB"
}
url = 'https://fcm.googleapis.com/fcm/send'
firebase = firebase.FirebaseApplication('https://parkings-50852.firebaseio.com/')

def send_notification(time_firebase, token_gcm, title, message):

    data = {
        'to': token_gcm,
        'notification': {'title': title, 'text': message}
    }

    time_now = strftime("%H:%M", gmtime())

    hh_fb, mm_fb = time_firebase.split(':')
    hh_now, mm_now = time_now.split(':')

    hh_fb_int = int(hh_fb)*3600
    hh_fb_int = hh_fb_int + int(mm_fb)

    hh_now_int = (int(hh_now) - 3)*3600
    hh_now_int = hh_now_int + int(mm_now) - 10


    print time_firebase, time_now
    print str(hh_fb_int), str(hh_now_int)

    if hh_fb_int > hh_now_int:
        r = requests.post(url, data=json.dumps(data), headers=headers)
        print(r.text)
        return False;
    else:
        return False;

def getNotifications():
    notifications = {}
    notifications['notifications'] = firebase.get('/notifications', None)

    i = 0
    arrayItems = {}
    for path in notifications['notifications']:
        arrayItems[i] = firebase.get('/notifications/'+path, None)
        print "Request ", arrayItems[i]
        i += 1

    return arrayItems;

def getAReservations():
    arrayParking = {}
    arrayParking['parking_a'] = firebase.get('/parking_a/parkings/times', None)

    i = 0
    arrayItems = {}
    for path in arrayParking['parking_a']:
        arrayItems[i] = firebase.get('/parking_a/parkings/times/'+path, None)
        i += 1

    return arrayItems;

def getBReservations():
    arrayParking = {}
    arrayParking['parking_b'] = firebase.get('/parking_b/parkings/times', None)

    i = 0
    arrayItems = {}
    for path in arrayParking['parking_b']:
        arrayItems[i] = firebase.get('/parking_b/parkings/times/'+path, None)
        i += 1

    return arrayItems;

def greaterThanTimeNow(time_firebase):
    time_now = strftime("%H:%M", gmtime())

    #datoF = time_firebase.split(':')
    #hh_fb = datoF[0]
    #mm_fb = datoF[1]
    #datoN = time_now.split(':')
    #hh_now = datoN[0]
    #mm_now = datoN[1]

    hh_fb, mm_fb = time_firebase.split(':')

    hh_fb_int = int(hh_fb)*100

    hh_now, mm_now = time_now.split(':')

    hh_fb_int = hh_fb_int + int(mm_fb)

    hh_now_int = int(hh_now)*100

    hh_now_int = hh_now_int + int(mm_now)

    if hh_fb_int <= hh_now_int:

        return False;
    else:

        return True;

def removeItemA(item_firebase):
    firebase.delete('/parking_a/parkings/times/'+item_firebase['time_id'], None)

def removeItemB(item_firebase):
    firebase.delete('/parking_b/parkings/times/'+item_firebase['time_id'], None)

def removeMSJDisplay(notification):
    firebase.delete('/notifications/'+notification['id'], None)


#arrayA = getAReservations()
#arrayB = getBReservations()

#print ("A")
#i = 0
#while i < len(arrayA):
#    print(arrayA[i]['start_time'])
#    print(arrayA[i]['final_time'])
#    print(arrayA[i]['user_gcm'])
#    i += 1

#print ("B")
#i = 0
#while i < len(arrayB):
#    print(arrayB[i]['start_time'])
#    print(arrayB[i]['final_time'])
#    print(arrayB[i]['user_gcm'])
#    i += 1


