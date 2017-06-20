#!/usr/bin/python
import RPi.GPIO as GPIO
import requests
import json
import time
import sys
import thread

import UltrasonicSensor
import Buzzer
import LightLed
import LightSensor
import InfraredSensor
import Servomotor

import LCDDriver
import ultrasonico_buzzer_a
import RPi_I2C_driver
from time import *



IS_ACTIVE = True


pins = {
    "PIN_LED_LDR" : 1,
    "PIN_LDR" : 2,
    "PIN_SERVOMOTOR_IN" : 3,
    "PIN_INFRARED_IN" : 5,
    "PIN_SERVOMOTOR_OUT" : 4,
    "PIN_INFRARED_OUT" : 5,
    "POUT_ULTRASONIC_A" : 6,
    "PIN_ULTRASONIC_A" : 7,
    "PIN_BUZZER_A" : 8,
    "PIN_LED_A" : 9,
    "POUT_ULTRASONIC_B" : 10,
    "PIN_ULTRASONIC_B" : 11,
    "PIN_BUZZER_B" : 12,
    "PIN_LED_B" : 13,
    "PIN_SDA_LCD" : 18,
    "PIN_SCL_LCD" : 19
}

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

        result_a = ultrasonico_buzzer_a.ultrasonico_buzzer_a(GPIO.BCM)
        if result_a > 10:
            cerrojo_ultrasonico = False
            print "Entro cerrojo"
        else:
            cerrojo_ultrasonico = True
            print "No entra a cerrojo"

        if cerrojo_ultrasonico == False:
            lcd.lcd_display_string("    HAY", 1)
            lcd.lcd_display_string("DISPONIBILIDAD", 2)

        else:
            #lcd.lcd_clear()
            lcd.lcd_display_string("Estacionamiento", 1)
            lcd.lcd_display_string("completo", 2)


begin()
