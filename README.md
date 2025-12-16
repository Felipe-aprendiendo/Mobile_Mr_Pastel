# Proyecto Mr. Pastel
​
 ## Integrantes
 *   Felipe Hernandez
 *   Maximiliano Madrid
 *   Omar Felipe
​
 ## Funcionalidades
 *   **Autenticación:** Inicio de sesión, registro de usuarios, recuperación de contraseña.
 *   **Gestión de Perfil:** Modificación de datos de usuario y foto de perfil.
 *   **Visualización de Productos:** Catálogo de pasteles.
 *   **Gestión de Pedidos:** Carrito de compras y visualización de historial de pedidos.
 *   **Roles de Usuario:** 
​
 ## Endpoints Usados
​
 ### API Externa (Oracle APEX)
 *   `POST /api/auth/login`: Autenticación de usuarios.
 *   `POST /api/auth/register`: Registro de nuevos usuarios.
 *   `PUT /api/usuarios/{id}`: Actualización de perfil de usuario.
 *   `GET /api/productos`: Obtener listado de productos.
 *   `POST /api/pedidos`: Crear un nuevo pedido.
 *   `GET /api/pedidos/usuario/{id}`: Obtener pedidos de un usuario.

​
 ## Instrucciones para Ejecutar el Proyecto
 1.  Clonar el repositorio.
 2.  Importar el proyecto en Android Studio.
 3.  Asegurarse de tener un emulador o dispositivo físico con Android API 26 o superior.
 4.  APEX no necesita ejecución: https://g382daee58087c5-mrpastelreact.adb.sa-santiago-1.oraclecloudapps.com/ords/mr_pastel/api/.
 5.  Ejecutar la aplicación desde Android Studio.
​
 ## APK Firmado
 El APK firmado (`app-release.apk`) se encuentra en la carpeta `/apk` del repositorio.
 El archivo de firmas (`keystore.jks`) se encuentra en la raíz del proyecto.
​
 ## Código Fuente
 *   **App Móvil:** El código fuente se encuentra en la carpeta `/app`.
 *   **Backend** Se utilizo el entorno APEX `[/app](https://g382daee58087c5-mrpastelreact.adb.sa-santiago-1.oraclecloudapps.com/ords/r/apex/workspace/home?session=313304962599232)`.
