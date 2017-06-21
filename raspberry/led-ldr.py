import RPi.GPIO as GPIO
import time

piezo = 29
fotocelda = 31

GPIO.setmode(GPIO.BOARD)

GPIO.setup(piezo,GPIO.OUT)
GPIO.setup(fotocelda,GPIO.IN)

current_state = 0

try:      
	while True:
		print GPIO.input(fotocelda)
		if GPIO.input(fotocelda) == 1: 
			time.sleep(0.1)
			print("paso")
        
			GPIO.output(piezo,True)
       			time.sleep(1)
        		print("paso 1")
        		GPIO.output(piezo, False)
	
        		time.sleep(1)
        		print("paso 2")
        		time.sleep(5)
	
except KeyboardInterrupt:
    pass
finally:
    GPIO.cleanup()
