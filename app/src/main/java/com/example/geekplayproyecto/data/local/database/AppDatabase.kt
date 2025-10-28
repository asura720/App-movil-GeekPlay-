package com.example.geekplayproyecto.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.geekplayproyecto.R
import com.example.geekplayproyecto.data.local.user.UserDao
import com.example.geekplayproyecto.data.local.user.UserEntity
import com.example.geekplayproyecto.data.local.post.PostDao
import com.example.geekplayproyecto.data.local.post.PostEntity
import com.example.geekplayproyecto.data.local.comment.CommentDao
import com.example.geekplayproyecto.data.local.comment.CommentEntity
import com.example.geekplayproyecto.data.local.like.LikeDao
import com.example.geekplayproyecto.data.local.like.LikeEntity
import com.example.geekplayproyecto.domain.geekplay.Category
import com.example.geekplayproyecto.utils.ImageUtils
import com.example.geekplayproyecto.utils.PasswordUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@Database(
    entities = [
        UserEntity::class,
        PostEntity::class,
        CommentEntity::class,
        LikeEntity::class
    ],
    version = 16, // Versión incrementada
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
    abstract fun commentDao(): CommentDao
    abstract fun likeDao(): LikeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private const val DB_NAME = "geekplay.db"

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.let { database ->
                                    val userDao = database.userDao()
                                    val postDao = database.postDao()

                                    // Copiar imágenes de perfil primero
                                    val adminImage = ImageUtils.copyDrawableToInternalStorage(context, R.drawable.spiderman_comic, "admin_profile.jpg")

                                    // Precargar usuarios
                                    userDao.insert(UserEntity(
                                        id = 1,
                                        name = "Admin",
                                        email = "admin@geek.cl",
                                        phone = "+56912222222",
                                        password = PasswordUtils.hashPassword("Admin123!"),
                                        profileImagePath = adminImage, // Asignar imagen
                                        isAdmin = true
                                    ))
                                    userDao.insert(UserEntity(
                                        id = 2,
                                        name = "Ricardo Diaz",
                                        email = "ricardo@gmail.com",
                                        phone = "+5694545454535",
                                        password = PasswordUtils.hashPassword("Asura1234!"),
                                        isAdmin = false
                                    ))

                                    // Precargar posts si la tabla está vacía
                                    if (postDao.count() == 0) {
                                        val image1 = ImageUtils.copyDrawableToInternalStorage(context, R.drawable.valorant_videojuego, "valorant_videojuego.jpg")
                                        val image2 = ImageUtils.copyDrawableToInternalStorage(context, R.drawable.kny_pelicula, "kny_pelicula.jpg")
                                        val image3 = ImageUtils.copyDrawableToInternalStorage(context, R.drawable.twd_serie, "twd_serie.jpg")
                                        val image4 = ImageUtils.copyDrawableToInternalStorage(context, R.drawable.spiderman_comic, "spiderman_comic.jpg")

                                        val valorantContent = """Hola, gente. ¡Se viene una versión cargadita! El equipo de VALORANT al completo se ha dejado la piel para que nuestro juego esté más alineado con la visión que tenemos para él a largo plazo, preservando en todo momento nuestra identidad como shooter táctico, pero evolucionando para alcanzar un equilibrio más saludable entre armas y habilidades.\n\nEs decir, estamos realizando ajustes en todo el ámbito de la experiencia de juego para que el diálogo en combate sea más sano: la idea es que existan formas evidentes de responder y adaptarse a lo que hace el equipo enemigo ronda tras ronda. Por este motivo, los cambios que traemos buscan conseguir un estado más saludable de equilibrio entre la precisión del manejo de las armas y las habilidades únicas que aportan su toque característico a VALORANT. Estos ajustes abarcan a los agentes, las armas y los mapas, y todos tienen por objetivo recompensar la toma de decisiones estratégica en VALORANT."""
                                        val knyContent = "Kimetsu no Yaiba: Mugen-jō-hen, también conocida como Demon Slayer: Kimetsu no Yaiba – The Movie: Infinity Castle, es una película de anime japonesa de fantasía oscura y acción basada en el arco «Castillo Infinito» del manga Kimetsu no Yaiba de Koyoharu Gotouge"
                                        val twdContent = "The Walking Dead trata sobre un grupo de sobrevivientes en un mundo postapocalíptico invadido por zombis, llamados \"caminantes\". La serie se enfoca en la lucha por la supervivencia, los conflictos interpersonales y las decisiones morales que los humanos deben tomar cuando la civilización se ha derrumbado. El personaje principal es el ayudante del sheriff Rick Grimes, quien despierta de un coma para encontrar este nuevo mundo."
                                        val spidermanContent = "La peor pesadilla de Spiderman se ha hecho realidad. Uno de sus enemigos ha descubierto su identidad secreta y está usando esa información para golpear a la familia de Peter Parker. Ahora, Tía May ha sido secuestrada, y Mary Jane puede ser la siguiente."

                                        val posts = listOf(
                                            PostEntity(id = UUID.randomUUID().toString(), title = "Valorant", summary = "El shooter táctico de Riot Games", content = valorantContent, category = Category.VIDEOJUEGOS.name, authorId = 1, publishedAt = System.currentTimeMillis(), imageUrl = image1),
                                            PostEntity(id = UUID.randomUUID().toString(), title = "Kimetsu no Yaiba: La película", summary = "El Castillo Infinito!", content = knyContent, category = Category.PELICULAS.name, authorId = 1, publishedAt = System.currentTimeMillis(), imageUrl = image2),
                                            PostEntity(id = UUID.randomUUID().toString(), title = "The Walking Dead", summary = "La serie de zombies", content = twdContent, category = Category.SERIES.name, authorId = 1, publishedAt = System.currentTimeMillis(), imageUrl = image3),
                                            PostEntity(id = UUID.randomUUID().toString(), title = "Spider-Man", summary = "El Hombre Araña", content = spidermanContent, category = Category.COMICS.name, authorId = 1, publishedAt = System.currentTimeMillis(), imageUrl = image4)
                                        )
                                        posts.forEach { postDao.insert(it) }
                                    }
                                }
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
