#!/usr/bin/python
import RPi.GPIO as GPIO
import requests
import json
import time
import sys
import threading

import UltrasonicSensor
import Buzzer
import LightLed
import LightSensor
import InfraredSensor
import Servomotor

PIN_LED_LDR = 1
PIN_LDR = 2

PIN_SERVOMOTOR_IN = 3
PIN_INFRARED_IN = 5

PIN_SERVOMOTOR_OUT = 4
PIN_INFRARED_OUT = 5

POUT_ULTRASONIC_A = 6
PIN_ULTRASONIC_A = 7
PIN_BUZZER_A = 8
PIN_LED_A = 9

POUT_ULTRASONIC_B = 10
PIN_ULTRASONIC_B = 11
PIN_BUZZER_B = 12
PIN_LED_B = 13

POUT_ULTRASONIC_C = 14
PIN_ULTRASONIC_C = 15
PIN_BUZZER_C = 16
PIN_LED_C = 17

PIN_SDA_LCD = 18
PIN_SCL_LCD = 19
ADDRESS_LCD = 0x3f

def setup(mode):
    s_light = LightSensor(PIN_LDR, mode)
    a_led_ldr = LightLed(PIN_LED_LDR, mode)

    s_ultra_a = UltrasonicSensor(PIN_ULTRASONIC_A, POUT_ULTRASONIC_A, mode)
    s_ultra_b = UltrasonicSensor(PIN_ULTRASONIC_B, POUT_ULTRASONIC_B, mode)
    s_ultra_c = UltrasonicSensor(PIN_ULTRASONIC_C, POUT_ULTRASONIC_C, mode)
    a_buzzer_a = Buzzer(PIN_BUZZER_A, mode)
    a_buzzer_b = Buzzer(PIN_BUZZER_B, mode)
    a_buzzer_c = Buzzer(PIN_BUZZER_C, mode)
    a_led_a = LightLed(PIN_LED_A, mode)
    a_led_b = LightLed(PIN_LED_B, mode)
    a_led_c = LightLed(PIN_LED_C, mode)

    s_infra_in = InfraredSensor(PIN_INFRARED_IN, mode)
    a_servo_in = Servomotor(PIN_SERVOMOTOR_IN, mode)
    s_infra_out = InfraredSensor(PIN_INFRARED_OUT, mode)
    a_servo_out = Servomotor(PIN_SERVOMOTOR_OUT, mode)

def init():

    GPIO.setmode(GPIO.BOARD)
    GPIO.setup(11,GPIO.OUT)
    pwm=GPIO.PWM(11,50)
    pwm.start(3.5)
    time.sleep(1)
    pwm.ChangeDutyCycle(7.5)
    time.sleep(1)
    pwm.ChangeDutyCycle(3.5)
    time.sleep(1)
    return;

init()

exit()
