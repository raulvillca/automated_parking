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

IS_ACTIVE = True

""
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

class AutomatizedParking:

    IS_ACTIVE = True


    def setup(self, pins, address_lcd, mode):
        self.sensors = {
            "light" : LightSensor(pins["PIN_LDR"], mode),
            "ultra_a" : UltrasonicSensor(pins["PIN_ULTRASONIC_A"], pins["POUT_ULTRASONIC_A"], mode),
            "ultra_b" : UltrasonicSensor(pins["PIN_ULTRASONIC_B"], pins["POUT_ULTRASONIC_B"], mode),
            "infra_in" : InfraredSensor(pins["PIN_INFRARED_IN"], mode),
            "infra_out" : InfraredSensor(pins["PIN_INFRARED_OUT"], mode)
        }

        self.actuators = {
            "led_ldr" : LightLed(pins["PIN_LED_LDR"], mode),
            "buzzer_a" : Buzzer(pins["PIN_BUZZER_A"], mode),
            "buzzer_b" : Buzzer(pins["PIN_BUZZER_B"], mode),
            "led_a" : LightLed(pins["PIN_LED_A"], mode),
            "led_b" : LightLed(pins["PIN_LED_B"], mode),
            "servo_in" : Servomotor(pins["PIN_SERVOMOTOR_IN"], 2.5, mode),
            "servo_out" : Servomotor(pins["PIN_SERVOMOTOR_OUT"], 2.5, mode)
        }

        self.availables = {
            "ultra_a" : True,
            "ultra_b" : True,
            "ultra_c" : True
        }

    def servo(self, sensorName, actuatorName):
        while self.IS_ACTIVE == True:
            if self.sensors[sensorName] == 1:
                self.actuators[actuatorName].rotate(7.5)
                time.sleep(3.5)
                self.actuators[actuatorName].rotate(2.5)

    def servoIn(self, sensorName, actuatorName):
        while self.IS_ACTIVE == True:
            if self.sensors[sensorName] == 1 & \
                    ( self.availables["ultra_a"] | self.availables["ultra_b"] ):
                self.actuators[actuatorName].rotate(7.5)
                time.sleep(3.5)
                self.actuators[actuatorName].rotate(2.5)

    def lightLDR(self):
        while self.IS_ACTIVE == True:
            if self.sensors["light"].getSignal() < 700:
                self.actuators["led_ldr"].turnOn()
            else:
                self.actuators["led_ldr"].turnOff()

    def ultrasonic(self, sensorName, actuatorName):
        while self.IS_ACTIVE == True:
            if 4 < self.sensors[sensorName].calculateDistanceFromAnObject() < 6:
                self.actuators[actuatorName].turnOn()
                time.sleep(1.5)
                self.actuators[actuatorName].turnOff()
                self.availables[sensorName] = False
            elif self.sensors[sensorName] < 4:
                self.actuators[actuatorName].turnOn()
                time.sleep(0.7)
                self.actuators[actuatorName].turnOff()
            else:
                self.availables[sensorName] = True
            time.sleep(0.1)

    def monitorUltrasonic(self):
        while self.IS_ACTIVE == True:
            if self.availables["ultra_a"] & self.availables["ultra_b"] & self.availables["ultra_c"]:


    def begin(self):
        try:
            thread.start_new_thread( self.servoIn, ("infra_in", "servo_in", ) )
            thread.start_new_thread( self.servo, ("infra_out", "servo_out", ) )
            thread.start_new_thread( self.ultrasonic, ("ultra_a", "buzzer_a") )
            thread.start_new_thread( self.ultrasonic, ("ultra_b", "buzzer_b") )
        except:
            print "Error: unable to start thread"
            self.IS_ACTIVE = False

parkingSystem = AutomatizedParking()
parkingSystem.setup(pins, ADDRESS_LCD, GPIO.BOARD)
parkingSystem.begin()""

cerrojo_ultrasonico = False

def medicion_ultrasonico_a():
    while IS_ACTIVE == True:
        result_a = ultrasonico_buzzer_a(GPIO.BCM)
        if result_a > 10:
            cerrojo_ultrasonico = False
        else:
            cerrojo_ultrasonico = True

def imprimir_mensajes():
    lcd = LCDDriver.Lcd()
    while IS_ACTIVE == True:
        if cerrojo_ultrasonico == False:
            lcd.lcd_display_string("    Hay", 1)
            lcd.lcd_display_string("disponibilidad", 2)
        else
            lcd.lcd_display_string("Estacionamiento", 1)
            lcd.lcd_display_string("completo", 2)

def begin():
    thread.start_new_thread( medicion_ultrasonico_a )
    thread.start_new_thread( imprimir_mensajes )
