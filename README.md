# Estacionamiento Automatizado
# Objetivo

  Implementar los conocimientos obtenidos sobre Sistemas Embebidos e IOT a lo largo de la cursada desarrollando un sistema de estacionamiento automatizado que servirá para gestionar los espacios disponibles en un estacionamiento informando a los clientes sobre la disponibilidad del lugar a través de un display y una aplicación Android que permitirá además la reserva de espacio de estacionamiento.

-------------------
# Sistema Embebido
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
La aplicación sirve de intermediario para comunicarse con el estacionamiento automatizado a través del celular. 
Para ello creamos en primer lugar las interfaces de usuario. 
La primera activity que se muestra al iniciarse la aplicación verifica si el usuario ya está ingresado en el sistema o no.
En caso de no estarlo se mostrará una pantalla que permite el ingreso de clave y contraseña o el logueo a 
partir del uso de huella digital. Si el usuario no está registrado en la aplicación puede seleccionar la opción de 
registro desde la misma pantalla.

**Version**
```
android {
    compileSdkVersion 25
    buildToolsVersion "25.0.2"
    defaultConfig {
        applicationId "tpsoa.soa.com.parkings"
        minSdkVersion 19
        targetSdkVersion 25
    }
}

dependencies {
    compile 'com.android.support:appcompat-v7:25.1.1'
    compile 'com.android.support:cardview-v7:25.1.1'
    compile 'com.android.support:design:25.1.1'
    
    //biblioteca para compilar apk, luego de pasar 64k de metodos referenciados
    compile 'com.android.support:multidex:1.0.0'

    //biblioteca para usar el servicio de localización de google mapas
    compile 'com.google.android.gms:play-services-places:9.6.1'
    
    //bibliotecas de firebase para utilizar la base de datos y notificaciones
    compile 'com.google.firebase:firebase-core:9.6.1'
    compile 'com.google.firebase:firebase-database:9.6.1'
    compile 'com.google.firebase:firebase-messaging:9.6.1'
    
    compile 'com.google.code.gson:gson:2.6.2'
    
    //sdk de samsung para reconocimiento de huellas
    compile files('libs/sdk-v1.0.0.jar')
    compile files('libs/pass-v1.2.2.jar')
}

//carga la configuración del archivo google-services.json
apply plugin: 'com.google.gms.google-services'
```

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
por lo cual el paquete del proyecto android debe encontrarse en la configuración del archivo google-services.json
