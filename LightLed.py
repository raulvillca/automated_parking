#!/usr/bin/python
import RPi.GPIO as IO
class LightLed:
    def __init__(self, pins):
        gpio.setmode(gpio.BOARD)
        self.io=IO
        for pin in pins:
            self.io.setup(pin, IO.OUT)
        return

    def turnOff(self, pin):
        self.io.output(pin, True)
        return

    def turnOn(self, pin):
        self.io.output(pin, True)
        return
