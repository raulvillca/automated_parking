#!/usr/bin/env python
import RPi.GPIO as GPIO
import time

BUZZER_A = 8
TRIG = 23
ECHO = 24
V    = 34300


def ultrasonico_buzzer_a(mode):

    #GPIO.setmode(GPIO.BCM)
    GPIO.setmode(mode)

    GPIO.setup(TRIG,GPIO.OUT)
    GPIO.setup(ECHO,GPIO.IN)
    GPIO.setup(BUZZER_A, GPIO.OUT)

    GPIO.output(TRIG, False)
    print "Espere que el sensor se estabilice"
    time.sleep(0.5)

    GPIO.output(TRIG, True)
    time.sleep(0.00001)
    GPIO.output(TRIG, False)

    pulse_start = time.time()
    pulse_end = time.time()

    while GPIO.input(ECHO)==0:
        pulse_start = time.time()

    while GPIO.input(ECHO)==1:
        pulse_end = time.time()

    t = pulse_end - pulse_start

    distancia = t * (V/2)
    distancia = round(distancia, 2)

    if distancia > 2 and distancia < 400:
        if 4 < distancia < 7:
            GPIO.output(BUZZER_A, False)
        elif 7 < distancia < 10:
            GPIO.output(BUZZER_A, True)

        print "Distancia: ",distancia,"cm"

    else:
      print "Fuera de Rango"

    return distancia
    #GPIO.cleanup()
