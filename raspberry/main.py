#!/usr/bin/python
import RPi.GPIO as GPIO
import Requests
import json
import time
import sys

import ultrasonico_buzzer_a
import ultrasonico_buzzer_b
import RPi_I2C_driver
import infra_servo

IS_ACTIVE = True

ADDRESS_LCD = 0x3f

def begin():
    cerrojo_ultrasonico_a = False
    cerrojo_ultrasonico_b = False

    lcd = RPi_I2C_driver.lcd()

    while IS_ACTIVE == True:

        arrayA = Requests.getAReservations()
        #arrayB = Requests.getBReservations()

        print ("A")
        i = 0
        while i < len(arrayA):
            print(arrayA[i]['start_time'], arrayA[i]['final_time'], arrayA[i]['user_gcm'])
            i += 1



        #print ("B")
        #i = 0
        #while i < len(arrayB):
        #    print(arrayB[i]['start_time'])
        #    print(arrayB[i]['final_time'])
        #    print(arrayB[i]['user_gcm'])
        #    i += 1

        infra_servo.servo_infrarrojo(cerrojo_ultrasonico_a)

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


        if cerrojo_ultrasonico_a == True | cerrojo_ultrasonico_b == True:
            lcd.lcd_display_string("ESTACIONAMIENTO*", 1)
            lcd.lcd_display_string("****COMPLETO****", 2)
        else:
            lcd.lcd_display_string("  *** HAY ****  ", 1)
            lcd.lcd_display_string("*DISPONIBILIDAD*", 2)
            #lcd.lcd_clear()



begin()
