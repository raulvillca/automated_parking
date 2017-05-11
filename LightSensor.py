import RPi.GPIO as GPIO

class LightSensor:

    def __init__(self, pinIn, mode):

        GPIO.setmode(mode)

        GPIO.setup(pinIn, GPIO.IN)

        self.io = GPIO

        self.pinLight = pinIn

        return

    def getSignal(self):

        return self.io.input(self.pinLight)
