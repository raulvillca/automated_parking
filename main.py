#!/usr/bin/python
import requests
import RPi.GPIO as GPIO
import time
import sys
import threading

#inicializar sensores y actuadores
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
