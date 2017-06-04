#!/usr/bin/python
import RPi.GPIO as GPIO
import time

class UltrasonicSensor:

    def __init__(self, pinIn, pinOut, mode):

        GPIO.setmode(mode)

        GPIO.setup(pinOut, GPIO.OUT)

        GPIO.setup(pinIn, GPIO.IN)

        self.io = GPIO

        self.trig = pinOut

        self.echo = pinIn

        return

    def calculateDistanceFromAnObject(self):
        self.io.output(self.trig, False)
        time.sleep(2*10**-6)
        self.io.output(self.trig, True)
        time.sleep(10*10**-6)
        self.io.output(self.trig, False)

        while self.io.input(self.echo) == 0:
            start = time.time()

        while self.io.input(self.echo) == 1:
            end = time.time()

        #La duracion del pulso del pin Echo sera la diferencia entre el tiempo de inicio y el final
        duracion = end-start

        #Este tiempo viene dado en segundos. Si lo pasamos
        #a microsegundos, podemos aplicar directamente las formulas de la documentacion
        duracion = duracion*10**6
        medida = duracion/58
        #hay que dividir por la constante que pone en la documentacion, nos dara la distancia en cm

        return medida
