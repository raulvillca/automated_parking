#!/usr/bin/python
import RPi.GPIO as GPIO
from threading import Thread
import Requests
import json
import time
import sys

import ultrasonico_buzzer_a
import ultrasonico_buzzer_b
import RPi_I2C_driver
import infra_servo

IS_ACTIVE = True
HAY_MSJ = False
GLOBAL_SERVO = False

ADDRESS_LCD = 0x3f

lcd = RPi_I2C_driver.lcd()

def receive(arg):
    while IS_ACTIVE:
        time.sleep(2)
        f = open("tuberia.txt", "r")
        valor_cerrojos = f.readline()
        f.close()

        mensajes_a_display = Requests.getNotifications()

        if mensajes_a_display != None:
            lcd.lcd_display_string("Bienvenido " + mensajes_a_display[0]["fullname"], 1)
            lcd.lcd_display_string("***SOA*-*IOT***", 2)
            time.sleep(2)
            print "Eliminar saludo"
            Requests.removeMSJDisplay(mensajes_a_display[0])

        print "MENSAJE RECIBIDO ", valor_cerrojos, GLOBAL_SERVO
        if valor_cerrojos == '11' :
            lcd.lcd_display_string("ESTACIONAMIENTO*", 1)
            lcd.lcd_display_string("****COMPLETO****", 2)
        else:
            lcd.lcd_display_string("  *** HAY ****  ", 1)
            lcd.lcd_display_string("*DISPONIBILIDAD*", 2)

def begin():
    cerrojo_ultrasonico_a = False
    cerrojo_ultrasonico_b = False
    enviar_notificacion_a = True
    enviar_notificacion_b = True

    while IS_ACTIVE == True:

        arrayA = Requests.getAReservations()
        print "Primera request A"
        arrayB = Requests.getBReservations()
        print "Primera request B"

        print "Primer recorrido"
        i = 0
        while i < len(arrayA):
            print "entra al while"
            if enviar_notificacion_a:
                Requests.send_notification(arrayA[i], arrayA[i]['user_gcm'], "TP SOA", "Te queda poco tiempo de uso")
                enviar_notificacion_a = False

            resultado = Requests.equalsTime(arrayA[i]['final_time'])
            if resultado:
                Requests.removeItemA(arrayA[i])
            i += 1


        print "Segundo recorrido"
        i = 0
        while i < len(arrayB):
            if enviar_notificacion_b:
                Requests.send_notification(arrayB[i], arrayB[i]['user_gcm'], "TP SOA", "Te queda poco tiempo de uso")
                enviar_notificacion_b = False
            
            resultado = Requests.equalsTime(arrayB[i]['final_time'])
            if resultado:
                Requests.removeItemB(arrayB[i])
            i += 1

        abrir_servo = cerrojo_ultrasonico_a & cerrojo_ultrasonico_b
        GLOBAL_SERVO = abrir_servo
        print "MENSAJE A ENVIAR ", GLOBAL_SERVO

        infra_servo.servo_infrarrojo(abrir_servo)

        result_a = ultrasonico_buzzer_a.ultrasonico_buzzer_a(GPIO.BCM)
        if result_a > 10:
            cerrojo_ultrasonico_a = False
            print "Entro cerrojo a"
        else:
            cerrojo_ultrasonico_a = True
            print "Ultrasonico a"

        result_b = ultrasonico_buzzer_b.ultrasonico_buzzer_b(GPIO.BCM)
        if result_b > 10:
            cerrojo_ultrasonico_b = False
            print "Entro cerrojo b"
        else:
            cerrojo_ultrasonico_b = True
            print "Ultrasonico b"


        f = open ("tuberia.txt", "w")
        if cerrojo_ultrasonico_a & cerrojo_ultrasonico_b:
            print "Escribiendo archivos 11"
            f.write("11")
        else:
            print "Escribiendo archivos 10"
            f.write("10")
        f.close()

try:
    subproceso = Thread(target=receive, args=(5,))
    subproceso.start()
    begin()
    subproceso.join()
except Exception,e:
    print str(e)
    print "Murio"
    IS_ACTIVE = False
