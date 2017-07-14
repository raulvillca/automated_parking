# Estacionamiento Automatizado
# Objetivo

  Implementar los conocimientos obtenidos sobre Sistemas Embebidos e IOT a lo largo de la cursada desarrollando un sistema de estacionamiento automatizado que servirá para gestionar los espacios disponibles en un estacionamiento informando a los clientes sobre la disponibilidad del lugar a través de un display y una aplicación Android que permitirá además la reserva de espacio de estacionamiento.

-------------------
# Sistema Embebido
## Descripción
La funcionalidad principal del sistema es gestionar los espacios disponibles en un estacionamiento informando a los clientes sobre la disponibilidad del lugar a través de un display en la entrada del estacionamiento. La gestión se realizara utilizando los sensores ultrasónicos que informaran si el espacio está ocupado o no. Además, estos sensores controlaran una posible colisión del automóvil al estacionar y en caso de ser necesario se alertara al conductor mediante la activación de una bocina (buzzer).

**Componentes**
* Microcontrolador Raspberry pi 3 b
* 1 Protoboard
* Cables para conexión
* Resistencias para ultrasonido
* Representación del estacionamiento en maqueta
* Sensores
* Actuadores

**Sensores**
* 2 Sensores de ultrasonido 
* 2 Sensores infrarrojos
* 1 Foto celda

**Bibliotecas**
* RPi.GPIO
* Firebase (instaladores incluidos en el proyecto)
* Request (instaladores incluidos en el proyecto)

**Actuadores**
* 2 Servomotor
* 2 Buzzers
* 3 Luces (Leds)
* Display LCD 16x2

--------------

# Aplicación android
## Descripción
La aplicación sirve de intermediario para comunicarse con el estacionamiento automatizado a través del celular. 
Para ello creamos en primer lugar las interfaces de usuario. 
La primera activity que se muestra al iniciarse la aplicación verifica si el usuario ya está ingresado en el sistema o no.
En caso de no estarlo se mostrará una pantalla que permite el ingreso de clave y contraseña o el logueo a 
partir del uso de huella digital. Si el usuario no está registrado en la aplicación puede seleccionar la opción de 
registro desde la misma pantalla.

**Componentes**
* Notificaciones push
* Base de datos de tiempo real
* Sensores
* Sdk samsung "fingerprint" (pass-v1.2.2 y sdk-v1.0.0)
* Google maps
* Cardview
* Gson

**Pantallas**
* Splash
* Login
* Registro
* Mapa principal

**Servicios**
* FirebaseIDService
* NotificationService
----------------------
**FirebaseIDService**:
Este servicio es utilizado para registrar dispositivo a la nube y comenzar a recibir notificaciones
**NotificationService**:
Este servicio se encargará de recibir los mensajes que el servidor de firebase nos este enviando

**nota**: Tanto el servicio de notificaciones como el de base de datos estaran vinculados a un proyecto en la consola de firebase, 
por lo cual el paquete del proyecto android debe encontrarse en la configuración del archivo google-service.json
