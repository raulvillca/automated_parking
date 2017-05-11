#!/usr/bin/python
import RPi.GPIO as GPIO

class LightLed:
    def __init__(self, pin, mode):

        GPIO.setmode(mode)

        self.io = GPIO

        self.io.setup(pin, GPIO.OUT)

        return

    def turnOff(self, pin):

        self.io.output(pin, False)

        return

    def turnOn(self, pin):

        self.io.output(pin, True)

        return
