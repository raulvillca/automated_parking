#!/usr/bin/python
import RPi.GPIO as GPIO
from threading import Thread
import Requests
import json
import time
import sys

import ultrasonico_buzzer_a
import ultrasonico_buzzer_b
import RPi_I2C_driver
import infra_servo

IS_ACTIVE = True
HAY_MSJ = False

ADDRESS_LCD = 0x3f

lcd = RPi_I2C_driver.lcd()

def receive(arg):
    while IS_ACTIVE:
        time.sleep(2)
        f = open("tuberia.txt", "r")
        valor_cerrojos = f.readline()
        f.close()

        arrayA = Requests.getAReservations()
        mensajes_a_display = Requests.getNotifications()
        #arrayB = Requests.getBReservations()

        lcd.lcd_display_string("Bienvenido " + mensajes_a_display[0]["fullname"], 1)
        lcd.lcd_display_string("***SOA*-*IOT***", 2)
        time.sleep(4)

        if valor_cerrojos is '11':
            lcd.lcd_display_string("ESTACIONAMIENTO*", 1)
            lcd.lcd_display_string("****COMPLETO****", 2)
        else:
            lcd.lcd_display_string("  *** HAY ****  ", 1)
            lcd.lcd_display_string("*DISPONIBILIDAD*", 2)

        i = 0
        while i < len(arrayA):
            print(arrayA[i]['start_time'], arrayA[i]['final_time'], arrayA[i]['user_gcm'])
            i += 1

        print "Mensaje recibido"
        #print ("B")
        #i = 0
        #while i < len(arrayB):
        #    print(arrayB[i]['start_time'])
        #    print(arrayB[i]['final_time'])
        #    print(arrayB[i]['user_gcm'])
        #    i += 1



def begin():
    cerrojo_ultrasonico_a = False
    cerrojo_ultrasonico_b = False

    while IS_ACTIVE == True:

        print "evaluando sensores"

        infra_servo.servo_infrarrojo(cerrojo_ultrasonico_a & cerrojo_ultrasonico_b)

        result_a = ultrasonico_buzzer_a.ultrasonico_buzzer_a(GPIO.BCM)
        if result_a > 10:
            cerrojo_ultrasonico_a = False
            print "Entro cerrojo a"
        else:
            cerrojo_ultrasonico_a = True
            print "Ultrasonico a"


        result_b = ultrasonico_buzzer_b.ultrasonico_buzzer_b(GPIO.BCM)
        if result_b > 10:
            cerrojo_ultrasonico_b = False
            print "Entro cerrojo b"
        else:
            cerrojo_ultrasonico_b = True
            print "Ultrasonico b"


        f = open ("tuberia.txt", "w")
        if cerrojo_ultrasonico_a & cerrojo_ultrasonico_b:
            print "Escribiendo archivos 11"
            f.write("11")
        else:
            print "Escribiendo archivos 10"
            f.write("10")
        f.close()

try:
    subproceso = Thread(target=receive, args=(5,))
    subproceso.start()
    begin()
    subproceso.join()
except:
    IS_ACTIVE = False
