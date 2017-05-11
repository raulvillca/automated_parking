#!/usr/bin/python
import RPi.GPIO as IO
class Servo:

    def __init__(self, pin, rotate):
        self.rotate = rotate
        IO.setwarnings(False)
        IO.setmode(IO.BCM)
        IO.start(rotate)
        IO.setup(pin,IO.OUT)
        self.io=IO
        return

    def getRotate(self):
        return self.rotate

    def setRotate(self, rotate):
        self.rotate = rotate
        return


