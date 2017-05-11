import RPi.GPIO as GPIO

class InfraredSensor:

    def __init__(self, pinIn, mode):

        GPIO.setmode(mode)

        GPIO.setup(pinIn, GPIO.IN)

        self.io = GPIO

        self.pinInfrared = pinIn

        return

    def getSignal(self):

        return self.io.input(self.pinInfrared)
