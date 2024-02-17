# TALLER 4: ARQUITECTURAS DE SERVIDORES DE APLICACIONES, META PROTOCOLOS DE OBJETOS, PATRÓN IOC, REFLEXIÓN

Servidor Web (tipo Apache) en Java. El servidor es capaz de entregar páginas html e imágenes tipo PNG. Igualmente, el servidor provee un framework IoC para la construcción de aplicaciones web a partir de POJOS. Usando el servidor se construye una aplicación Web de ejemplo. El servidor atiende múltiples solicitudes no concurrentes.

## Comenzando

Estas instrucciones te ayudarán a obtener una copia del proyecto en funcionamiento en tu máquina local para fines de desarrollo y pruebas.

### Requisitos previos

- Kit de desarrollo de Java (JDK) versión 11 o posterior
- Herramienta de construcción Maven

### Instalando

1. Clona el repositorio:
    ```
    git clone https://github.com/AlexisGR117/AREP-TALLER4.git
    ```
2. Navega a la carpeta del proyecto:
    ```
    cd AREP-TALLER4
    ```
3. Construye el proyecto usando Maven:
    ```
    mvn clean install
    ```
4.  Ejecuta la aplicación:
    ```
    java -cp target/AREP-TALLER4-1.0-SNAPSHOT.jar edu.eci.arep.MovieInfoServer
    ```
5. Abre un navegador web y accede a la aplicación en http://localhost:35000/client.html.

## Ejecutando las pruebas

Ejecuta las pruebas unitarias:
```
mvn test
```

## Documentación

Para generar el Javadoc (se generará en la carpeta target/site):
```
mvn site
```

## Arquitectura

Este proyecto implementa un servidor web simple en Java usando el patrón IoC para construir aplicaciones web a partir de POJOs. El servidor es capaz de entregar páginas HTML, imágenes PNG y atender múltiples solicitudes no concurrentes.

### Componentes principales:

* **Servidor Web**: Implementado en Java, responde a peticiones HTTP y atiende archivos estáticos como HTML e imágenes.
* **Framework IoC:** Desarrollado a medida, permite cargar POJOs anotados con la anotación @Component y utilizar reflection para identificar métodos anotados con @RequestMapping que funcionan como controladores web.
* **Controladores Web:** Clases POJO anotadas con @Component que contienen métodos anotados con @RequestMapping. Estos métodos manejan las solicitudes HTTP y generan respuestas (generalmente páginas HTML o JSON).
* **Proveedor de datos de películas:** La clase OMDbMovieDataProvider implementa la interfaz MovieDataProvider y recupera información de películas de la API OMDb.

## Ejemplo de desarrollo

El servidor proporcionado utiliza un framework IoC para simplificar la creación de aplicaciones web a partir de POJOs. Aquí le mostramos cómo desarrollar una aplicación básica:

1.  Componentes POJO
    
    Los componentes de la aplicación se definen como clases POJO anotadas con ```@Component```. Estas clases contienen métodos que actúan como controladores para manejar diferentes solicitudes web.

    **Ejemplo:**
    
    ```java
    @Component
    public class HelloController {
    
        @RequestMapping("/hola")
        public static String hola(Map<String, String> p) {
            // Accede a parámetros de la URL usando el Map 'p'
            MovieInfoServer.responseType("text/html"); // Cambia el tipo de respuesta
            return "<h1>Hola " + p.get("nombre") + "</h1>";
        }
    }
    ```

2. Anotaciones

    * **@RequestMapping:** Define la ruta HTTP asociada al método controlador. Por ejemplo, /hola en el ejemplo anterior.
    * **@Component:** Indica que la clase es un componente del framework y debe ser gestionada por el servidor.

3. Parámetros de la URL

    Los parámetros de la URL se pasan al método controlador como un Map<String, String>. Puedes acceder a ellos utilizando el nombre del parámetro como clave.

4. Cambiar el tipo de respuesta

    Utiliza el método ```MovieInfoServer.responseType(String contentType)``` para establecer el tipo de contenido de la respuesta. Por ejemplo, text/html, application/json, etc.

5. Servir archivos estáticos

   El servidor puede servir archivos estáticos como imágenes y HTML desde el directorio configurado con ```MovieInfoServer.staticDirectory(String directoryPath)```.

6. Ejecutar el servidor

    Para ejecutar el servidor, compila y ejecuta la clase MovieInfoServer. Escuchará en el puerto 35000 por defecto y responderá a las solicitudes HTTP. Por ejemplo, para acceder desde el navegador a la ruta dada en el código del inciso uno sería con la URL: http://localhost:35000/component/hola?nombre=TuNombre.
    
    Antes de la ruta que se definió con @RequestMapping debe ir /component.

## Evaluación

### Caso de prueba 1:

**Objetivo:** Verificar que el servidor entrega correctamente archivos.

**Entrada:** Solicitud al servidor para un archivo (client.hml).

**Salida:** El cliente debe mostrar los archivos correctamente, sin errores de carga o visualización.

![Caso1.png](img/Caso1.png)

Se realizó la petición a http://localhost:35000/client.html la cual obtuvo como respuesta exitosa tanto en Windows como en Linux el archivo html, que a su vez hace la petición a los demás archivos que se requieren para mostrar la aplicación.

### Caso de prueba 2:

**Objetivo:** Validar que el servicio web GET para "/component/movies" funciona correctamente.

**Entrada:** Solicitud GET a la ruta "/component/movies" con un parámetro de consulta "title" que especifica el título de una película.

**Salida:** El servidor debe responder con un JSON que contiene la información de la película.

![Caso2.png](img/Caso2.png)

Se realizó la petición GET a http://localhost:35000/component/movies?title=Guardians la cual obtuvo como respuesta exitosa el json con la información de la película que se pasó como parámetro.

### Caso de prueba 3:

**Objetivo:** Confirmar que el servicio web GET para "/component/hola" funciona correctamente.

**Entrada:** Solicitud GET a la ruta "/component/hola" con un parámetro de consulta "nombre".

**Salida:** El servidor debe responder con una página HTML que dice "Hola" seguido del nombre especificado en el parámetro.

![Caso3.png](img/Caso3.png)

Se realizó la petición GET a http://localhost:35000/component/hola?nombre=Alexis la cual obtuvo como respuesta exitosa el archivo html donde se muestra el parámetro que se recibió en la url.

## Construido con

- Java 11
- Maven

## Autores

* Jefer Alexis Gonzalez Romero