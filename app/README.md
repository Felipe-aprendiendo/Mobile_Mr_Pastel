 

DESARROLLO DE APLICACIONES MÓVILES

MISTER PASTEL APP



Integrantes:
Omar Felipe
Felipe Hernández
Maximiliano Madrid


Sección:
DSY1105-005V


Profesor:
Miguel Avecedo C.






1. Descripción General del Proyecto

Mister Pastel es una aplicación móvil desarrollada en Kotlin + Jetpack Compose, cuyo propósito es digitalizar la experiencia de compra de una pastelería ficticia —permitiendo explorar productos, revisar detalles, filtrar por categorías y navegar mediante un catálogo visual moderno.
Este proyecto corresponde a la Evaluación Parcial 4 y es un avance directo hacia el Examen Final Transversal (EFT), cumpliendo los requisitos de:
-	Integración con API externa real vía Retrofit
-	Persistencia local con Room Database
-	Arquitectura MVVM + Repository
-	Navegación completa con Compose
-	Generación de APK firmado
-	Trabajo colaborativo gestionado con GitHub

2. Funcionalidades del Proyecto

Catálogo de productos
-	Obtención remota de productos mediante API APEX.
-	Sincronización automática al abrir la app.
-	Filtros por categoría.
-	Buscador por texto.
Vista de detalle de productos
-	Imagen principal
-	Nombre, precio y descripción
-	Acceso desde el catálogo
Navegación completa
-	Home (modo invitado)
-	Pantalla de catálogo con sesión iniciada
-	Menú lateral (Drawer)
-	Detalle de producto
-	Carrito (solo accesible con sesión)
Persistencia con Room
-	Sincronización catálogo → base de datos local
-	Repositorio centralizado para acceso y cache local
Integración con API externa real
-	API APEX Oracle ORDS (Mr Pastel API)
-	Retrofit configurado correctamente
-	Operación: GET productos
Interfaz moderna
-	Jetpack Compose
-	Material 3
-	Uso de Coil para carga de imágenes



3. Arquitectura del Sistema

El proyecto utiliza el patrón MVVM, estructurado en 4 capas:

UI (Compose)
Pantallas, componentes y navegación.

ViewModels
Administran el estado y la lógica de presentación.
Ejemplo: CatalogoViewModel gestiona el catálogo, búsqueda, filtros y sincronización remota.

Repository
Capa intermedia encargada de:
-	Obtener datos de la API externa (Retrofit)
-	Guardar y leer desde Room Database
-	Mantener lista centralizada de productos

Room (Persistencia Local)
Incluye:
-	ProductoEntity
-	ProductoDao
-	AppDatabase


4. API Externa Utilizada

La app consume datos desde una API real expuesta con Oracle APEX / ORDS:
https://g382daee58087c5-mrpastelreact.adb.sa-santiago-1.oraclecloudapps.com/ords/mr_pastel/api/

Endpoint utilizado:
GET /productos/

Archivos encargados del consumo:
-	RetrofitInstance.kt → configuración Base URL
-	ApiService.kt → definición del endpoint
-	ProductoRepository.kt → sincronización API → Room


5. Dependencias principales

El proyecto utiliza:
-	Kotlin
-	Jetpack Compose
-	Navigation Compose
-	Room Database
-	Retrofit + Gson
-	Coil (imágenes)
-	StateFlow
Todas están declaradas dentro del archivo build.gradle.kts.



6. Estructura del Proyecto

app/
 └─ src/main/java/com/grupo3/misterpastel/
      ├─ model/                      ← modelos de datos
      ├─ viewmodel/            ← lógica de presentación
      ├─ repository/              ← repositorios + acceso API
       │   ├─ remote/              ← Retrofit
       │    └─ local/                    ← Room
      ├─ ui/
       │   ├─ screens/             ← pantallas Compose
       │   └─ components/      ← componentes reutilizables
       └─ MrPastelApp.kt        ← configuración global Coil


7. Cómo Ejecutar el Proyecto
Requisitos
-	Android Studio Koala o superior
-	SDK 33+
-	Emulador o dispositivo físico con Android 10+
-	Internet activo (primer uso sincroniza catálogo)

Pasos
1.	Clonar el repositorio:
2.	git clone https://github.com/Felipe-aprendiendo/Mobile_Mr_Pastel.git
3.	Abrir en Android Studio
4.	Esperar sincronización de Gradle
5.	Ejecutar el proyecto
6.	La app iniciará en modo invitado y luego permitirá navegar al catálogo

8. APK Firmado
El APK entregado está firmado con la llave:
-	Archivo: mi-keystore.jks
-	Alias: almacenado internamente (no se publica por seguridad)
-	Firma aplicada mediante: Build → Generate Signed Bundle / APK
El archivo APK se encuentra incluido en el repositorio en la carpeta correspondiente a la entrega.


9. Estado Actual del Proyecto
-	Projecto móvil funcional
-	API externa integrada (Retrofit + APEX)
-	Sincronización local mediante Room
-	Navegación fluida con Compose
-	Catálogo completo y filtrable
-	Uso de GitHub como herramienta colaborativa
-	APK firmado












