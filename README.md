# 🍰 Mil Sabores Mobile (Mr. Pastel)

**Mil Sabores Mobile** es una aplicación Android desarrollada con **Kotlin + Jetpack Compose**, que digitaliza la experiencia de compra de la pastelería chilena *Mil Sabores*, ofreciendo un sistema moderno de e-commerce para tortas, repostería y pedidos personalizados.

Este proyecto forma parte de la **Evaluación Parcial 2 (Grupo 3)** del curso **DSY1104**, integrando base de datos, lógica de negocio y diseño móvil.

---

## 🚀 Características principales

- 🧁 **Catálogo interactivo** de productos con buscador y filtros por categoría.
- 🛒 **Carrito de compras dinámico** con descuentos automáticos:
  - 100% descuento si correo termina en `@duocuc.cl`
  - 50% descuento si edad ≥ 50 años
  - 10% descuento con cupón `FELICES50`
- 💳 **Procesamiento de pago simulado** con generación de comprobante digital.
- 📦 **Gestión de pedidos** (historial con estado).
- 👤 **Registro/Login seguro** con contraseñas **hasheadas (BCrypt)**.
- 📸 **Perfil de usuario con cámara o selección de imagen desde archivos.**
- 🌙 **Tema visual personalizado (MrPastelTheme).**
- 💾 **Persistencia local con Room Database (Productos, Usuarios, Pedidos).**
- 🧭 **Navegación con Jetpack Navigation Compose.**

---

## 🧱 Arquitectura

El proyecto sigue el patrón **MVVM (Model - ViewModel - View)** + **Repository Pattern**.

UI (Compose Screens)
↓
ViewModel (maneja estado y lógica de presentación)
↓
Repository (reglas de negocio y validaciones)
↓
Room (persistencia local: @Entity, @Dao, @Database)


---

## 📂 Estructura del proyecto

app/
 └─ src/main/java/com/grupo3/misterpastel/
    ├─ MainActivity.kt
    ├─ navigation/
    │   └─ AppNavigation.kt
    ├─ ui/
    │   ├─ components/
    │   │   └─ ProductoCard.kt
    │   ├─ screens/
    │   │   ├─ HomeScreen.kt
    │   │   ├─ HomeSesionIniciada.kt
    │   │   ├─ LoginScreen.kt
    │   │   ├─ RegistroScreen.kt
    │   │   ├─ DetalleProductoScreen.kt
    │   │   ├─ CarritoScreen.kt
    │   │   ├─ PagoProcesandoScreen.kt
    │   │   ├─ ComprobantePagoScreen.kt
    │   │   ├─ PedidoScreen.kt
    │   │   ├─ PerfilUsuarioScreen.kt   ← cámara/archivos
    │   │   └─ splash/SplashScreen.kt
    │   └─ theme/
    │       ├─ Color.kt
    │       ├─ Theme.kt
    │       └─ Type.kt
    ├─ viewmodel/
    │   ├─ LoginViewModel.kt
    │   ├─ RegistroViewModel.kt
    │   ├─ SessionViewModel.kt
    │   ├─ CatalogoViewModel.kt
    │   ├─ CarritoViewModel.kt
    │   ├─ PagoViewModel.kt
    │   └─ PedidoViewModel.kt
    ├─ model/
    │   ├─ Usuario.kt
    │   ├─ Producto.kt
    │   ├─ CarritoItem.kt (+ extensión subtotal)
    │   ├─ Carrito.kt
    │   ├─ Pedido.kt
    │   ├─ ComprobantePago.kt
    │   ├─ Categoria.kt
    │   └─ EstadoPedido.kt
    └─ repository/
        ├─ ProductoRepository.kt
        ├─ UsuarioRepository.kt   ← registro/login con BCrypt
        ├─ CarritoRepository.kt   ← descuentos/cupones/comprobante
        ├─ PedidoRepository.kt
        └─ local/                 ← ROOM
            ├─ AppDatabase.kt
            ├─ ProductoEntity.kt
            ├─ ProductoDao.kt
            ├─ UsuarioEntity.kt
            ├─ UsuarioDao.kt
            ├─ PedidoEntity.kt
            └─ PedidoDao.kt

res/
 ├─ drawable/ (imágenes del catálogo y logos)

 
---

## 🔒 Seguridad

El sistema implementa **hashing de contraseñas** con **BCrypt** para garantizar la seguridad de los usuarios:

Registro
val hash = BCrypt.hashpw(password, BCrypt.gensalt())
usuarioDao.insertarUsuario(UsuarioEntity(..., passwordHash = hash))

Login
val valido = BCrypt.checkpw(passwordIngresada, entity.passwordHash)

 ├─ mipmap/   (iconos de la app)
 └─ values/   (strings.xml, etc.)

Las contraseñas nunca se almacenan en texto plano.

##🗄️ Persistencia (Room Database)

Entidades principales:

ProductoEntity

UsuarioEntity

PedidoEntity

Ejemplo de @Dao:

@Dao
interface ProductoDao {
  @Insert(onConflict = REPLACE) suspend fun insertarProductos(list: List<ProductoEntity>)
  @Query("SELECT * FROM producto") fun obtenerTodos(): Flow<List<ProductoEntity>>
}

##🧭 Navegación

Se implementa con Navigation Compose (NavHost + NavController).

Rutas: splash → home → login/registro → homeSesion → detalle → carrito → pago → comprobante → pedidos → perfil.

##📱 Funcionalidades nativas (Android)

Cámara: captura foto con ActivityResultContracts.TakePicturePreview().

Archivos: selecciona imagen con ActivityResultContracts.GetContent().

Almacenamiento: se usa MediaStore para guardar imágenes en la galería (Pictures/MrPastel).

Visualización: imágenes renderizadas con Coil (AsyncImage(uri)).

##⚙️ Tecnologías
Componente	Tecnología / Librería
Lenguaje  	  Kotlin
UI	          Jetpack Compose
Arquitectura	MVVM + Repository
Persistencia	Room Database
Navegación	  Navigation Compose
Hashing	      BCrypt
Imágenes	    Coil
Android       SDK	33+
IDE	Android   Studio Koala 🐨

##🧩 Flujo principal

El usuario abre la app (SplashScreen).

Si no ha iniciado sesión → LoginScreen / RegistroScreen.

Tras el login → HomeSesionIniciada (catálogo).

Puede ver detalles, agregar productos al carrito.

Desde el carrito → aplicar cupón o pagar.

Genera un comprobante (ComprobantePago).

El pedido se guarda en la base local (Room).

El usuario puede ver sus pedidos en PedidoScreen.

En PerfilUsuarioScreen, puede cambiar su foto o datos.

##📦 Instalación y ejecución

Clona el repositorio:

git clone https://github.com/grupo3/milsabores-mobile.git


Ábrelo con Android Studio.

Ejecuta en un emulador o dispositivo físico (API 33+).

(Opcional) Limpia y reconstruye:

Build → Clean Project
Build → Rebuild Project

👥 Autores

Grupo 3 — Proyecto DSY1104: Mil Sabores

Integrante 1: Felipe Hernández 

Integrante 2: Maximiliano Madrid

Integrante 3: Omar Felipe



🎂 Créditos

Desarrollado con ❤️ por estudiantes DUOC UC.
Proyecto académico — no comercial.
Inspirado en la pastelería Mil Sabores (Chile).

🪶 Licencia

Este proyecto es de uso educativo.
Puedes modificarlo y reutilizarlo con fines académicos, dando crédito al equipo original.
