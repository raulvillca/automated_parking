#!/usr/bin/env python
import RPi.GPIO as GPIO
import time

BUZZER_A = 26
TRIG = 21
ECHO = 20
V    = 34300

def ultrasonico_buzzer_b(mode):

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

    pulse_start = time.time()
    while GPIO.input(ECHO)==1:
        pulse_end = time.time()

    t = pulse_end - pulse_start

    distancia = t * (V/2)
    distancia = round(distancia, 2)

    if distancia > 2 and distancia < 400:

        if 2 < distancia < 3:
            GPIO.output(BUZZER_A, True)
            time.sleep(0.5)
        elif distancia > 3:
            GPIO.output(BUZZER_A, False)

        print "Distancia b: ",distancia,"cm"

    else:
      print "Fuera de Rango"

    return distancia
    #GPIO.cleanup()

#ultrasonico_buzzer_b(GPIO.BCM)
#GPIO.cleanup()
