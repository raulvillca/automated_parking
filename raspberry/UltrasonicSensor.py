#!/usr/bin/python
import RPi.GPIO as GPIO
import time

class UltrasonicSensor:

    def __init__(self, pinIn, pinOut, mode):

        GPIO.setmode(mode)

        GPIO.setup(pinOut, GPIO.OUT)

        GPIO.setup(pinIn, GPIO.IN)

        print "Medicion de la distancia en curso"

        self.io = GPIO

        self.trig = pinOut

        self.echo = pinIn

        self.V = 34300

        return

    def calculateDistanceFromAnObject(self):

        GPIO.output(self.trig, False)
        print "Espere que el sensor se estabilice"
        time.sleep(1)

        GPIO.output(self.trig, True)
        time.sleep(0.00001)
        GPIO.output(self.trig, False)            

        while GPIO.input(self.echo)==0:
          pulse_start = time.time()

        while GPIO.input(self.echo)==1:
          pulse_end = time.time()

        t = pulse_end - pulse_start

        medida = t * (self.V/2)
        medida = round(medida, 2)

        if medida > 2 and medida < 400:

          print "Distancia: ",medida,"cm"

        else:
          print "Fuera de Rango"

        GPIO.cleanup()

        return medida


us = UltrasonicSensor(23,24,GPIO.BCM)
result = us.calculateDistanceFromAnObject()
print result
