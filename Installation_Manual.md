# Manual de Instalación

# Windows 10/8.1/7

**Git:** Accediendo al siguiente link: [https://git-scm.com/download/win](https://git-scm.com/download/win) Escoger la versión de git (x32 o x64) según la arquitectura del procesador.
![](https://lh3.googleusercontent.com/p5rFK-OHwYpfOeq9bgd4ujY2NrsjWX-y7LW2NZlREcTkQMTRiYGd7qSiTFJxKwNEPy_tMAVcZnNU2ma0AXmK3z3o_jGmpw48Cp_9-nSA6B4YpZGiobKoAS7KsUB3jKL4Ol_PMbTn)

**JDK 8:** Atraves del siguiente link se selecciona la versión de java según la arquitectura del procesador: http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
![](https://lh3.googleusercontent.com/96eYcNJOVrq7bmGvWvgzk-b-BVIFD18ToaSi5Xmb7y5EY1OKb04jkOJFhmo3s4iwdVrwM7xaN72DKydmBcCuWVt5B6GVQ15tViGJDj73nS4dyc85v43U4w9Ybrj62Uze0rByUjQa)

Una vez descargado e instalado se procede a configurar las variables de entorno.
Teniendo en cuenta la siguiente dirección donde Java hizo la instalación se realizará la configuración de las variables de entorno.

![](https://lh5.googleusercontent.com/k0O_v9Nz1L8UBiIK0MpPMRsEr5leGWz_fMiZTVoefzSBKSqY9OphG7ccMkxzsY_9q07mlJvb0AdKlAGaMsfssHVdbRygGLDZlUE-IoWHk9JnPmqds0VLvcPTzzkwzigLmp065FbT)

Accedemos a Sistema y damos click sobre la opción **"Configuración Avanzada del Sistema"** luego **"Variables de entorno"**.

![](https://lh5.googleusercontent.com/Jmmr9vnYlQwRJ0wiAk_4LyHvKZhRpSkcbk7N-NEECbkI2tntqWp1AqpcY877HzVGrhSWrAG4DaPU8g_JkcscOPxQEk_BHi-otXKThd78ufrKzKLbmqRR4v8dxmm0dOJ_-crKG7o_)

Se crea una nueva variable llamada “JAVA_HOME” con la ruta donde está localizado el JDK.

![](https://lh4.googleusercontent.com/2Tw9mCHOkw8VCkXAaraUV1eW7Qws-TNFBRQ7ofPeuduwyOOjszruSksAtXydMzess7QSWVVaMJzlVxO1FUPIIWY0JND_6YUZcwAsGhCMQW6vyPCWvuxa08jK1tMMGhXfCw1411ua)

Posteriormente se accede a la variable “Path” y se agrega la variable de entorno ”JAVA_HOME” dando el valor como “%JAVA_HOME%\bin”.

![](https://lh3.googleusercontent.com/9a3KgZ2na9CqUWA-Yf1eb9IRbJMoPp_N6_MKoyOLCuuOZNWuZpcHIGW9kZTMeWE8ZH28H_q76agItfFE9eP8Qa0-mQGEe5cJLPz8_bkEIGI8r0MCUBCNr6tEVVeKaJEpoTn7yOkq)

Se comprueba que java está instalado en el pc, accediendo al “Símbolo de sistema” y se escriben las siguientes líneas:
```sh
> javac -version
> java -version
```

![](https://lh6.googleusercontent.com/i3kKH0inQydPPRp1Yq3WKP6VB14KKeICO8IUwt2Q6QSJYkRfD7B2e331HIq2Y64fpUO6pqQY7wDjtrfO-xQRcZ_cM0GZKxOfl54zCrN0SESkhJlr9Gw28OYfptp_uQif-d7vDhh-)

**Maven:** Accediendo a la siguiente dirección (https://maven.apache.org/download.cgi) se busca el bin de maven y se descarga, después se deberá descomprimir en la dirección que desee, para la guia se dejará en el mismo lugar donde se instaló Java.

![](https://lh5.googleusercontent.com/oN33AxhlUVHThwh_Zojj4tWsU4enLMnjwlzi7qkw39GudHjfePLu3eMSf1j1j3oAQRvSR4ZcFJdGTS2YWMASnXV5gvh7bRFKU-nHRx-BjEkqW7YOY3La7ocFgTwvjLldFRvWsXtH)

![](https://lh4.googleusercontent.com/fjjBahCf8jDJdjuEaJStEwC4v2LOLkkarpk97F7HXUwOkxL-TohSHOwdqHVK0rAU1IKLVsvqRTCPI7dzr_s6lsHeTr0_rXnBViDQfyJgX5p1rH_Q9meiZ_EaKbYz6qiQNLci2WUe)

Se dirige a la configuración de variables de entorno “paso similar de instalación de Java” y se agrega el recurso al **Path**.

![](https://lh4.googleusercontent.com/7SUxfI8fYJMgexr5K1jUgOomoFymbfHLFOHQo3v61ODydp2aC7dk6kDLEb95acMH2ye3nXcCdPmiq6tBiloh7LRdyT9L3Et82V4Val5nC_YuvJmCBYYPyRof6oqdma1cM1U3vkub)

**Cassandra:** Descargar la versión de cassandra según la arquitectura del procesador:
- MSI Installer (32 Bits): http://downloads.datastax.com/community/datastax-community-32bit_3.0.9.msi
- MSI Installer (64 Bits): http://downloads.datastax.com/community/datastax-community-64bit_3.0.9.msi

Una vez descargado, se corre el paquete de instalación, se aceptan los términos de licenciamiento.

![](https://lh4.googleusercontent.com/V7hM6QM_WTeIrTMPR1clAeY6j_w0Xg3OO-D7RB7XUSP5ik1txwo1T2rgDoAJSIBKxwBVJhCNOLyRd9GdnRlb6koDtBC5hIA-VMrVyi-aon7xS600s_L1n2xn-jtwDfiwET6Xowcj)

Se especifica en qué lugar se instalará Cassandra y se especifica si se desea que Cassandra se ejecuta una vez encienda el equipo:

![](https://lh6.googleusercontent.com/DMq0M5kcKFNavvmeglkaiXuMxfdQkHNF88T007M9A0ecltq4-17zaSmANBiT1S9i9VojErVYKWemV_GOR8qZiOdR9quUGa00JYJAyUgnYkJn5FXPOBZGxHqMS1aG8amkxLlgVdLc)

Se da inicio a la instalación:

![](https://lh4.googleusercontent.com/NVQe1uYYHNT43N29HjHKvbougpXIsPpehdR3nEajCfCgWPUIOE_puwQzClqRbQzK4ePhoAUpFEDrBYaqT_hGXl9AA6rupyOmSihZ1GKWJV6W41QtU-YLwkja5jip4NJw6hiHtXqc)

Una vez termina aparecerá el aviso de que Cassandra se instaló correctamente:

![](https://lh4.googleusercontent.com/dVDtUrfyAlEtPxyKXM6yaIxPY-DxMoyMJN74CFsBS-GKBxDPdZoPDjUgLio2aWPZNPwJzWnxv33BDdgYxR7tBvYOm0QG3ACBppMKX4gyQAjdOVy24fHNeW1b_3g__tqdPA_sZipn)

Para comprobar si Cassandra está activo, se accede al **Símbolo de Sistema** y se ejecutan los comandos cqlsh.
```sh
> cqlsh
```
![](https://lh6.googleusercontent.com/wZ4EQYYEm5OIr4p4_N7E36Ux8SBnhUPmFUG2GzI4QePI-27-8ydUPGTUozsAiMEa5WiJGUKYjUr1hNzu_rvw3jiUNuv1tv8h3PyAU9dFvjwiK5L-voBRp0mhB_L5VbL4JjJ3dD9z)

# Linux: Ubuntu 16.04

**Java:** Se accede al siguiente link, y se ejecutan las siguientes líneas en la terminal de Linux.
https://www.digitalocean.com/community/tutorials/how-to-install-java-with-apt-get-on-ubuntu-16-04#installing-the-oracle-jdk

**Instalar Oracle JDK**
```sh
$ sudo add-apt-repository ppa:webupd8team/java
$ sudo apt-get update
```
**Oracle JDK 8**
```sh
$ sudo apt-get install oracle-java8-installer
```

![](https://lh5.googleusercontent.com/RPntwwiwzoT7qJqDH345kd4f1mWB3EPnTq1T3KjJGnaoh5dy9SyLDkuHTSq57byRaQ6b2jRhe67VqKBJ8dXQWxg4zR85c171sz6TuBzpanMjyTXh-yFSDOnVpxtIjnmm34zFYDp6)

![](https://lh6.googleusercontent.com/jI2pDRKaMFMpFsb-PQwUnVNJ-9U6z3FAntFIqeIgDwUkNSt9yPr8ljxl0bhmAfe9Abfe9wJ4rhjpd3ulrcwkVlO7v7IXxKIBRxo13yTEUMBtZg7soexg1zNSV-yqAYdk9YC1lJ9m)

**Maven:** Para instalar maven usando la Terminal de linux, se escribe el siguiente comando.
```sh
$ sudo apt-get install maven
```
Una vez instalado, se verifica consultado la versión usando la siguiente línea.
```sh
$ mvn -version
```
![](https://lh5.googleusercontent.com/5A2Eh-kf6QQSHPNuCTU0SwWMbZ7iy-bx83Hi98XwBhhWA0iApgvu40Dr9yrHNNClqaMf6vwfj4BWr9OEJ9Hfs7tEhpBZlXB75xtzy8IQf2RASWUFrf5wE2UBRtY_9nFS-g7g9LNf)

**Cassandra:** 
Lo primero que debemos hacer es verificar que en Ubuntu 16.04 esté instalado **“wget”** el cual es es una herramienta libre que permite la descarga de contenidos desde servidores web de una forma simple. El propósito de instalar **“wget”** será obtener el script de instalación de Cassandra que ofrece Thingsboard el cual agrega el repositorio de Cassandra al sistema para posteriormente actualizarlo con sus herramientas de trabajo.
Pasos:
1. Escribir el siguiente comando en la terminal para instalar curl en caso de no tenerlo sobre Ubuntu.
```sh
$ sudo apt-get install wget
```	
2. Descargar el script de instalación de cassandra usando curl.
```sh
$ sudo wget https://raw.githubusercontent.com/thingsboard/thingsboard.github.io/master/docs/user-guide/install/resources/cassandra-ubuntu-installation.sh 
```	
Verificar que el archivo esté en la carpeta donde está actualmente abierta la terminal.
![](https://lh3.googleusercontent.com/wyw2ZTFq-DhUinncYg8NUy7dw6WBCSElWPwRtUiAMAwVoMVOIRQ3jMD4Gs3oodKPXl7ut-ZMwdQP6l0QlyFAq6Rge1WCWKTYAgFODofgh6Jq_d-U8STIiMkc8wntOTcFeqV3NO0K)

Dar permisos de ejecución del script usando el comando
```sh
$ sudo chmod 777 cassandra-ubuntu-installation.sh
```	
Ejecutar script de instalación de Cassandra.
```sh
$ sudo ./cassandra-ubuntu-installation.sh
```	
Verificar Cassandra en Linux usando el comando cqlsh.
```sh
$ cqlsh
```	
![](https://lh4.googleusercontent.com/epsdIyi_KLomNX9I1QpqTiMPm8wY6QyVHWcZ461GiNeuqXot8XgatFfQthujlCaZqh-htbfoCLJZZfY9mltNFyV7gBdTy5zs9yV3Hyyq2a_N4CFIfWbJlY9hBKp1x7M-poNHqZ0q)
**MongoDB:** Para realizar la instalación de MongoDB usaremos como guía base el siguiente link que realiza la instalación de MongoDB en Ubuntu 16.04.
https://www.digitalocean.com/community/tutorials/como-instalar-mongodb-en-ubuntu-16-04-es
Como primera instancia, esta información es útil conocerla antes de instalar MongoDB.

**MongoDB está actualmente incluido en el repositorio de paquetes de Ubuntu, pero el repositorio oficial de MongoDB proporciona la versión más actualizada y es el camino recomendado para instalar este software.**

**Pasos:**
Ubuntu se asegura de autenticar los paquetes de software verificando que han sido firmados con llaves GPG, así que primero importamos la llave para el repositorio oficial de MongoDB.
```sh
$ sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv EA312927
```	
A continuación, debemos agregar los detalles del repositorio de Mongo de tal manera que apt pueda saber de donde descargar los paquetes.
Corriendo el siguiente comando crearemos la lista para MongoDB.
```sh
$ echo "deb http://repo.mongodb.org/apt/ubuntu xenial/mongodb-org/3.2 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-3.2.list
```	
Después de agregar los detalles del repositorio, debemos actualizar la lista de paquetes.
```sh
$ sudo apt-get update
```	
Ahora podemos instalar el propio paquete de MongoDB.
```sh
$ sudo apt-get install -y mongodb-org
```	
Vamos a crear un archivo de unidad para administrar el servicio de MongoDB. Crearemos un archivo de configuración llamado mongodb.service en el directorio /etc/systemd/system utilizando nano o su editor de texto favorito.
```sh
$ sudo nano /etc/systemd/system/mongodb.service
```	
Pegamos el siguiente contenido en el archivo.
**/etc/systemd/system/mongodb.service**
```sh
[Unit]
Description=High-performance, schema-free document-oriented database
After=network.target

[Service]
User=mongodb
ExecStart=/usr/bin/mongod --quiet --config /etc/mongod.conf

[Install]
WantedBy=multi-user.target
```
![](https://lh5.googleusercontent.com/ASu9GKzpSsZWjgxZjXIl32nXwMAL7ov3eEeSjJm3-jLrAI7Im7j613iOHpRPJ34EA2jXj3QNc3XvXiDnWkvdnau_1q5FeRee1ANJXkuVG-5wjfRjiPnK2ELLlZzbfDDO0aq5f5be)
![](https://lh5.googleusercontent.com/n0pj4gOoRDaecBeqI9Cw-S4oNk_2aYWub2HPfS5btPtZhEYQt9pgRb-iY-y0o2ua3xQK5FVUGgQj9IC1fhmP6NxbgKyUhIa60bFX0N5290O98xnnSmOeMFyxomiMdoSjd3wfFHMw)

Lo siguiente, será iniciar el servicio recién creado con systemctl.
```sh
$ sudo systemctl start mongodb
```	
Aun cuando este comando no responde con un mensaje, puede utilizar systemctl para revisar que el servicio ha arrancado apropiadamente.
```sh
$ sudo systemctl status mongodb
```	
El último paso es habilitar automáticamente el arranque de MongoDB cuando el sistema inicie.
```sh
$ sudo systemctl enable mongodb
```	
**Permitir acceso a puertos de otros servidores**
En la mayoría de los casos, MongoDB solo debería ser accesible desde ubicaciones seguras, como por ejemplo el otro servidor que aloja la aplicación. Para cumplir con esta tarea, puede permitir a una IP de un servidor específico acceder y conectarse al puerto por defecto de MongoDB.
```sh
$ sudo ufw allow from la_IP_del_otro_servidor/32 to any port 27017
```
Puede verificar el cambio en la configuración del firewall con ufw.
```sh
$ sudo ufw status
```
Para comprobar que mongo está corriendo en la máquina, se verifica usando el siguiente comando.
```sh
$ mongo
```
![](https://lh3.googleusercontent.com/QcOqB-3eMsy3AMoy7ZeujRHeGPzeYEg7crw6P6GBgOLiZwiLXpC7_oXbEChXBp2KWVnB60HXhzwcpleMweANgPPjQolK2mwAUr0GSfz2MJyZX2xrMFq8CaRGs3jF1NyIwcO5KxR7)
**Problemas comunes:**
Si aparece la siguiente notificación se debe escribir el comando **$ export LC_ALL=C**.
![](https://lh6.googleusercontent.com/jog2httXLusNVKpl59eiShaPEYULz67EQ80HsWZhVmeYaU_qI8M80VMkL05jrvJ1D6W9M9fSVKIqeoaDI6o1jYurwi3s_WDv9PugwXnm17yNoPpnYdA2JEob0R5OYKu82_E2PmCi)
---

# Agregar archivo para despliegue de nodos Apache Spark

En cada equipo que conformará el cluster de Spark se debe tener el siguiente archivo:
Utilizando la siguiente dirección http://apache.uniminuto.edu/spark/spark-2.3.1/spark-2.3.1-bin-hadoop2.7.tgz se desacarga el archivo que se obtiene usando el comando wget.

```sh
$ wget http://apache.uniminuto.edu/spark/spark-2.3.1/spark-2.3.1-bin-hadoop2.7.tgz
```
![](https://lh6.googleusercontent.com/pk6eDgdbXujN7uciRm1lvZLPC3DjAA4YBxn6W6dn_1RMr_IiCVliCaqrK1jXoMiRT5uEuFEdq5Gr1D-QK_XcuLNAQAYM5JgYyhuuVtmG5UMfL_L--o9MGsioHcAft5aqFH-p3Wc5)

Posteriormente se debe descomprimir. 
La implementación de la solución se desarrolló en 3 equipos:

**![](https://lh4.googleusercontent.com/rGRJZWBVS1QMQzaHbUzp9LcYFr7KvFZqZGDSsoy4N9U0wfrrsGZ8btDNnKazDSO-JTHkxRaEjEfT3kdNp_xF1Bk4VteamfmPVPZy_P_knKPZb7Ug24aiydOa4H6TMrE7iRVFhv3-)**

Es válido aclarar que en el archivo de configuración, todos los equipos deben conocer los hostname de los demás (para efectos del clúster de spark)

para configurar esto puede hacerlo a través del comando:
```sh
sudo nano /etc/hosts
```
De forma que quede de esta manera:

![](https://lh3.googleusercontent.com/7eG07T9MWLfRoF2LWLN4DPXV6LjkuMp3U1F3ISY8uUOMtyLBvPF4tQ9BpY2d0woLTCHLtDoD7A6YbtPVD8X_w1_YGt34JpXA5fa8oU5ejGyGZqJJPJDk_YJdvH36o1PVkUk5BIai)

127.0.0.1 localhost

10.8.0.17 agricultura2

10.8.0.18 agricultura1

10.8.0.23 agricultura3

 
Pasos para la ejecución de la prueba de concepto:

Agricultura2:

-   Correr redis  
```sh
cd redis-stable/  
sudo redis-server redis.conf
```
    
-   Correr Spark Master 
```sh
/home/pgr/spark-2.3.1-bin-hadoop2.7/bin/spark-class org.apache.spark.deploy.master.Master
```    
-   Correr Spark Worker 
```sh
/home/pgr/spark-2.3.1-bin-hadoop2.7/bin/spark-class org.apache.spark.deploy.worker.Worker spark://10.8.0.17:7077
```    

Agricultura1:

-   Correr Thingsboard
    
-   Correr Kafka 
```sh
sudo /opt/kafka/bin/kafka-server-start.sh /opt/kafka/config/server.properties
```    
-   Correr Spark Worker 
```sh
/home/pgr/pgr/spark-2.3.1-bin-hadoop2.7/bin/spark-class org.apache.spark.deploy.worker.Worker spark://10.8.0.17:7077
```    
Agricultura3:
-   Correr Spark Worker 
```sh
/home/pgr/spark-2.3.1-bin-hadoop2.7/bin/spark-class org.apache.spark.deploy.worker.Worker spark://10.8.0.17:7077
```    

Envío de las aplicaciones de spark al clúster

Agricultura2:

-   Compilar las aplicaciones de spark (más información en 6.4.3. Guía para la creación de nuevas aplicaciones de Spark Streaming paso 12 en adelante)
    
-   Enviar al cluster la aplicación de humedad 
```sh
sudo /home/pgr/spark-2.3.1-bin-hadoop2.7/bin/spark-submit --class org.thingsboard.samples.spark.humidity.SparkKafkaStreamingHumidityMain --master spark://10.8.0.17:7077 --conf spark.cores.max=1 /home/pgr/thingsboard-spark-backend/target/Spark-Humidity-KafkaStreaming.jar
```
    
-   Enviar al cluster la aplicación de intensidad de la luz  
```sh
sudo /home/pgr/spark-2.3.1-bin-hadoop2.7/bin/spark-submit --class org.thingsboard.samples.spark.light.SparkKafkaStreamingLightMain --master spark://10.8.0.17:7077 --conf spark.cores.max=1 /home/pgr/thingsboard-spark-backend/target/Spark-Light-KafkaStreaming.jar
```    
-   Enviar al cluster la aplicación de temperatura
```sh
sudo /home/pgr/spark-2.3.1-bin-hadoop2.7/bin/spark-submit --class org.thingsboard.samples.spark.temperature.SparkKafkaStreamingTemperatureMain --master spark://10.8.0.17:7077 --conf spark.cores.max=1 /home/pgr/thingsboard-spark-backend/target/Spark-Temperature-KafkaStreaming.jar
```

Puede cambiar la cantidad de cores que utilizan las aplicaciones cambiando el parámetro en los comandos “ spark.cores.max=1 “ por otro número

Para acceder a la página principal del nodo master de spark debe ir a 10.8.0.17:8080 y aparecerá una pantalla como la siguiente:

![](https://lh4.googleusercontent.com/yvkedIZk-iyZVDh616F7csaCmJOuQIqHYLIflWBHD0x6HCP9efp7kpmslaiXakhOXcoUaHxcgMIJRmT1CAWD2JvItVtbA_Y_N8zghypOaIx7o4saaGWcCTRidh-Yeg79m_4duDJc)


---
# Instalación de Kafka:

1.  Instalar Zookeeper: 
```sh
sudo apt-get install zookeeperd
```  
2.  Probar que está corriendo: 
```sh
telnet localhost 2181
```  
3.  Descargar la última versión de Kafka: [https://kafka.apache.org/downloads](https://kafka.apache.org/downloads), en binary donwloads

4.  Extraer el tar y moverlo a esta carpeta:
```sh
sudo mkdir /opt/kafka  
sudo tar -xvzf kafka_2.12-0.11.0.0.tgz --directory /opt/kafka --strip-components 1
```   
5.  Crear estas dos carpetas:
```sh
sudo mkdir /var/lib/kafka
sudo mkdir /var/lib/kafka/data
```   
6.  Se abre el archivo de configuración del servidor:
```sh
sudo nano /opt/kafka/config/server.properties
```   
7.  Se busca donde diga log.dirs y se cambia la dirección por esta:
```sh
log.dirs=/var/lib/kafka/data
``` 
8.  Ejecutar el comando que inicie el servidor: 
```sh
sudo /opt/kafka/bin/kafka-server-start.sh 
sudo /opt/kafka/config/server.properties
```
9.  En otro terminal crear un tópico: 
```sh
/opt/kafka/bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic temperature
```
10.  Para comprobar que se creó el tópico correctamente se ejecuta el comando: 
```sh
/opt/kafka/bin/kafka-topics.sh --list --zookeeper localhost:2181
```
11.  Para enviar mensajes se ejecuta el comando (Se deben crear los tópicos que se necesitan conforme a los datos de telemetría que se van  usar): 
```sh
/opt/kafka/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic temperature
```
12.  Para ver los mensajes recibidos, se abre otro terminal y se ejecuta este comando: 
```sh
/opt/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic weather-stations-data --from-beginning
```
---
# Instalación de Redis:

Para instalar la herramienta caché Redis toca hacer los siguientes pasos:
```sh
sudo apt-get update
sudo apt-get install build-essential tcl
cd /tmp
curl -O [http://download.redis.io/redis-stable.tar.gz](http://download.redis.io/redis-stable.tar.gz)
tar xzvf redis-stable.tar.gz
cd redis-stable
make
make test
sudo make install
```

Una vez instalado procedemos a ejecutarlo:

1.  Ejecutamos el comando “cd redis-stable/”
    
2.  Y por último ejecutamos “sudo redis-server redis.conf”
    

En caso de que haya quedado abierto el servicio detener con el comando:
```sh
sudo service redis_6379 stop
```
---
# Instalación y ejecución de Thingboard

Tanto para linux como para windows se utiliza el mismo método de compilación y ejecución usando mvn y java.
**Pasos:**
Clonar el proyecto de la extensión de Thingsboard que se encuentra en el Repositorio oficial LIS-Laboratorio de Ingeniería de Software.
```sh
$ git clone https://github.com/LIS-ECI/thingsboard
```
![](https://lh5.googleusercontent.com/RNcGrR9GbOPgG4fKAOrYLyMaXzNLjLnwj5ROm6I0R7GNOP4ZxzBtTrryQbYyXOEpLy_Q2I-qlgMBZ9Ahr-xkPWVnhvR4_1uevfNTdZb8tEzMzj6OnvZOi2vqE8TvAeQ15FGdxp7G)
Lo primero será compilar el proyecto, por lo que debemos localizar la raíz del proyecto y ejecutarlo completamente con la siguiente línea:
```sh
$ mvn clean install -DskipTests -Dlicense.skip=true
```
Omitiremos la compilación de las pruebas y la revisión de licencias de maven

![](https://lh3.googleusercontent.com/HrkhUSM4-3k5F79u2wu3SF-BVRHj5w9POtHmiJm4RH0xneIl3A0ZSIrePN3Gpd6RDgwjhBnOPymFPzFUWJ6or-W6iABL-8PWo5UTXX_PK2wisTYR550Sza9CNuFVXgNPDmVcpBQH)

Para saber si la compilación terminó correctamente, deberá aparecer cada uno de los paquetes mostrando una compilada satisfactoria.

![](https://lh3.googleusercontent.com/o3K8-cokFXZldL2m1BprKriVsrNGVM3w4Hs7MwLLwI4xGANb4Eel41TpPT53PT9nV7LKfVCEjFfX3u_c4pmFgwovldzbvbEor2q-IRjgSDer4MUIGJCHOx4wiHBvJZ5eSk8MIdQt)

Posteriormente debemos instalar el esquema de base de datos de Cassandra por lo que cambiará si está en sistema operativos Windows o Ubuntu.

**Windows:**
Sobre la raíz del proyecto debemos ingresar con el “símbolo del sistema”, nos dirigimos a la siguiente ruta “application\target\windows\” con el siguiente comando:
```cmd
> cd application/target/windows
```
Luego, se ejecuta el script 
```cmd
> install_dev_db.bat
```

![](https://lh3.googleusercontent.com/Fg-GwUpY0dBHREkrqg_0uIIw4RVvbQWi31BLnvc1kaPdg-ljPFVQGNpLQPiAg1Lfgk3Txtyzt1Hi3JnQGdCXx2EskxzRv9M8mYZ66z1LFeA_9d0u3C4QwBM_jtpawWummnDB5kdN)

**Linux:**
Dentro del proyecto Thingsboard, nos dirigimos a la carpeta **“application/target/bin/install”**, allí, encontraremos el archivo **“install_dev_db.sh”**, necesitamos darle los permisos correspondientes por lo que usaremos la siguiente línea:

![](https://lh5.googleusercontent.com/DTgJgJxRXRU78m7MQJxs5C5EH0qWH1bLD-G8IssqwbvjN9wf00jwLj8Yb6ZxR9sbLXXuYkuB42TT0vb_wZP4IYhiuGMSzREvU0Yd3q-K6oMRuFOiX39p0ucx_yTpC9zPwwCeiKZG)

Posteriormente, se debe editar el archivo cambiando el **“run_user”** con el nombre del equipo, para este ejemplo será carlos.
![](https://lh5.googleusercontent.com/wevZ5SkbKzDfyjeswgLHLHjRX-o-yN3JM1fBxf7NYvFmd2I2FlnZlUYp3KZK90jSMmASJtOkUaNWZBrfD7o384DP_Z-k0Tk6725wrJxMA13hnL9g3nXw5D60-6fh6WsKhAIgnjfc)

Se guardarán los cambios y se procede a ejecutar el script usando la siguiente línea:

![](https://lh5.googleusercontent.com/9rVbY7rL0i13pDhBFm_y1XA7DwwT4sPXHFkyRfr6NTzt2HBoc1Ej-Kx2O7LvZcwhe81rUYTBsSY2Msv2rkVsWGkFF9rc7-LO81Ae-7mtT1WaulLRVG67y0rc_sLFg32Y40cv9fQu)

**Ejecución:**
Sobre el proyecto ThingsBoard, acceder a la carpeta **“application/target”**, listamos los archivos y se debe encontrar un ejecutable llamado **“thingsboard-1.4.1-SNAPSHOT-boot.jar”**:

![](https://lh5.googleusercontent.com/XaWHkso0t1TC8wF_CnWR8hzydj20Z3vJS3uY-yQ1fg1dGiXZu2IGWKoFoYxElQfZJVE4U-paX0LSIWk7-p89t6aD1fAaLZHNilawjd3foEoP2wgEdn5Ed8pO0_MPe8llSXSDdoNK)

Para ejecutar el archivo jar se debe escribir la siguiente línea:
**Linux**
```sh
$ java -jar thingsboard-1.4.1-SNAPSHOT-boot.jar
```
**Windows**
```sh
thingsboard\application\target>  java -jar thingsboard-1.4.1-SNAPSHOT-boot.jar
```
La terminal deberá quedar en el siguiente estado para acceder desde cualquier navegador **(RECOMENDACIÓN GOOGLE CHROME)**:

![](https://lh3.googleusercontent.com/sHQJ3shUR8PIh1Pt_d793evS4X5Uc77gHR88y2OLn9Mxj9qQdHZE1gVHtW5NnwP-OTAGcX2Wo6P7XcSvuRHhki9pL3LJyN1o5iS7T0hBikDpnoRZCPISXKqWhpcEQO8kEr2-pL-w)

Una vez ingresamos al navegador accediendo desde la url “localhost:8080” se deberá ingresar el siguiente usuario:
Username: tenant@thingsboard.org
Password: tenant

![](https://lh6.googleusercontent.com/wXeMTxACO6oZWc9aZupXXQvbc1WljCh8nd4P4Hz02Dp7sfZ1pOz6OljKRkEPzvWU1f_GrvDwUl11o_zS-G_dcbIg7Tr88ekj_MByuOChCJgWij5PFrg8CgpaDqVhD10XatPiDPK2)

![](https://lh4.googleusercontent.com/XO2_SeOduXpV8sFjRZx4uFvBWLKLOtrnKSUTAwPDaqmEpkYkQFuJeMUdfNd6eaOaNR56fjVPW0ZTXi4SZStuqsmt61DjToP_5wV12yPNHKFC2b-HFe5YDw7IETO1wHWaea93srs3)

