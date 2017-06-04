import RPi.GPIO as GPIO

class Buzzer:

    def __init__(self, pinOut, mode):

        GPIO.setmode(mode)

        GPIO.setup(pinOut, GPIO.OUT)

        self.io = GPIO

        self.pinBuzzer = pinOut

        return

    def turnOff(self):

        self.io.output(self.pinBuzzer, False)

        return

    def turnOn(self):

        self.io.output(self.pinBuzzer, True)

        return

