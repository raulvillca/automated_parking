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

        GPIO.output(self.trig, False)                   #TRIG en estado bajo
        print "Espere que el sensor se estabilice"
        time.sleep(1)                              #Esperar 2 segundos

        GPIO.output(self.trig, True)                    #TRIG en estado alto
        time.sleep(0.00001)                        #Delay de 0.00001 segundos
        GPIO.output(self.trig, False)                   #TRIG en estado bajo

        while GPIO.input(self.echo)==0:                 #Comprueba si ECHO está en estado bajo
          pulse_start = time.time()                #Guarda el tiempo transcurrido, mientras esta en estado bajo

        while GPIO.input(self.echo)==1:                 #Comprueba si ECHO está en estado alto
          pulse_end = time.time()                  #Guarda el tiempo transcurrido, mientras esta en estado alto

        t = pulse_end - pulse_start                #Se obtienen la duración del pulso, calculando la diferencia entre pulse_start  y pulse_end

        medida = t * (self.V/2)                      #Se multiplica la duración del pulso, por 17150, para obetener la distancia
        medida = round(medida, 2)            #Se redondea a dos decimales

        if medida > 2 and medida < 400:      #Comprueba si la distancia está dentro del rango

          print "Distancia: ",medida,"cm"       #Imprime la distancia

        else:
          print "Fuera de Rango"                   #Imprime fuera de rango

        GPIO.cleanup()							   #Limpia los pines

        return medida


us = UltrasonicSensor(23,24,GPIO.BCM)
result = us.calculateDistanceFromAnObject()
print result
