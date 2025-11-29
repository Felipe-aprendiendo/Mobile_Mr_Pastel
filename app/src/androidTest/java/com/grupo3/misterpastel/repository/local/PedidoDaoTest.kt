package com.grupo3.misterpastel.repository.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PedidoDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: PedidoDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        db = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        )
            .allowMainThreadQueries() // necesario solo en tests
            .build()

        dao = db.pedidoDao()
    }

    @After
    fun teardown() {
        db.close()
    }


    @Test
    fun insertarPedido_insertaCorrectamente() = runBlocking {
        val pedido = PedidoEntity(
            idPedido = "PED001",
            userId = "USER123",
            fecha = 9999999L,
            total = 15000.0,
            estado = "PENDIENTE",
            itemsJson = """[{"id":1,"nombre":"Pastel","cantidad":2}]"""
        )

        dao.insertarPedido(pedido)

        val lista = dao.obtenerTodosLosPedidos().first()

        Assert.assertEquals(1, lista.size)
        Assert.assertEquals("PED001", lista[0].idPedido)
        Assert.assertEquals("USER123", lista[0].userId)
    }
}