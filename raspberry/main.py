#!/usr/bin/python
import RPi.GPIO as GPIO
import Requests
import json
import time
import sys

import LCDDriver
import ultrasonico_buzzer_a
import RPi_I2C_driver
import infra_servo

IS_ACTIVE = True

ADDRESS_LCD = 0x3f

cerrojo_ultrasonico = False

def medicion_ultrasonico_a():
    while IS_ACTIVE == True:
        result_a = ultrasonico_buzzer_a.ultrasonico_buzzer_a(GPIO.BCM)
        if result_a > 10:
            #cerrojo_ultrasonico = False
            print "Entro cerrojo"
        else:
            #cerrojo_ultrasonico = True
            print "No entra a cerrojo"

def imprimir_mensajes():
    lcd = LCDDriver.Lcd()
    while IS_ACTIVE == True:
        #if cerrojo_ultrasonico == False:
        lcd.lcd_display_string("    Hay", 1)
        lcd.lcd_display_string("disponibilidad", 2)
        #else:
            #lcd.lcd_display_string("Estacionamiento", 1)
            #lcd.lcd_display_string("completo", 2)

def begin():
    cerrojo_ultrasonico = False

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

        infra_servo.servo_infrarrojo(cerrojo_ultrasonico)

        result_a = ultrasonico_buzzer_a.ultrasonico_buzzer_a(GPIO.BCM)
        if result_a > 10:
            cerrojo_ultrasonico = False
            print "Entro cerrojo"
        else:
            cerrojo_ultrasonico = True
            print "No entra a cerrojo"

        if cerrojo_ultrasonico == False:
            lcd.lcd_display_string("  *** HAY ****  ", 1)
            lcd.lcd_display_string("*DISPONIBILIDAD*", 2)

        else:
            #lcd.lcd_clear()
            lcd.lcd_display_string("ESTACIONAMIENTO*", 1)
            lcd.lcd_display_string("****COMPLETO****", 2)


begin()
