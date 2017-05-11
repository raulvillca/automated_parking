#!/usr/bin/python
import RPi.GPIO as GPIO

class Servomotor:

    def __init__(self, pin, rotate, mode):

        GPIO.setwarnings(False)

        GPIO.setmode(mode)

        GPIO.start(rotate)

        GPIO.setup(pin, GPIO.OUT)

        self.io = GPIO

        self.rotate = rotate

        return

    def getRotate(self):
        return self.rotate

    def setRotate(self, rotate):
        self.rotate = rotate
        return


