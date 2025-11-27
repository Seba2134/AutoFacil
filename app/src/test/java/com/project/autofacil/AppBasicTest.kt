package com.project.autofacil

import com.project.autofacil.ViewModels.UsuarioViewModel
import com.project.autofacil.ViewModels.AutoViewModel
import com.project.autofacil.data.UsuarioDao
import com.project.autofacil.data.UsuarioEntity
import com.project.autofacil.data.AutoDao
import com.project.autofacil.data.AutoEntity
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class AppBasicTests {

    private val dispatcher = StandardTestDispatcher()

    // Mocks
    private lateinit var usuarioDao: UsuarioDao
    private lateinit var autoDao: AutoDao

    // ViewModels
    private lateinit var usuarioViewModel: UsuarioViewModel
    private lateinit var autoViewModel: AutoViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        // relaxed = true para que no explote por otros métodos que no nos importan en estos tests
        usuarioDao = mockk(relaxed = true)
        autoDao = mockk(relaxed = true)

        // UsuarioDao.obtenerTodos() -> Flow<List<UsuarioEntity>>
        coEvery { usuarioDao.obtenerTodos() } returns flowOf(emptyList())

        // AutoDao.obtenerTodos() -> List<AutoEntity>
        coEvery { autoDao.obtenerTodos() } returns emptyList()

        usuarioViewModel = UsuarioViewModel(usuarioDao)
        autoViewModel = AutoViewModel(autoDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ───────────────────────────────────────────────
    //  1) Login correcto llena cliente
    // ───────────────────────────────────────────────
    @Test
    fun loginCorrectoLlenaCliente() = runTest {
        val entity = UsuarioEntity(
            id = 1,
            nombre = "Admin",
            correo = "admin@mail.cl",
            contrasena = "1234",
            direccion = "Casa",
            rol = "admin"
        )

        coEvery { usuarioDao.login("admin@mail.cl", "1234") } returns entity

        usuarioViewModel.iniciarSesion("admin@mail.cl", "1234")
        dispatcher.scheduler.advanceUntilIdle()

        val cliente = usuarioViewModel.cliente.value
        assertNotNull(cliente)
        assertEquals("Admin", cliente?.nombre)
        assertEquals("admin@mail.cl", cliente?.email)
    }

    // ───────────────────────────────────────────────
    //  2) Login incorrecto deja cliente en null
    // ───────────────────────────────────────────────
    @Test
    fun loginIncorrectoNoLlenaCliente() = runTest {
        coEvery { usuarioDao.login("x@x.cl", "mala") } returns null

        usuarioViewModel.iniciarSesion("x@x.cl", "mala")
        dispatcher.scheduler.advanceUntilIdle()

        val cliente = usuarioViewModel.cliente.value
        assertNull(cliente)
    }

    // ───────────────────────────────────────────────
    //  3) Cerrar sesión limpia cliente
    // ───────────────────────────────────────────────
    @Test
    fun cerrarSesionFunciona() = runTest {
        val entity = UsuarioEntity(
            id = 10,
            nombre = "Usuario",
            correo = "user@mail.cl",
            contrasena = "1234",
            direccion = "Dir",
            rol = "cliente"
        )

        coEvery { usuarioDao.login("user@mail.cl", "1234") } returns entity

        usuarioViewModel.iniciarSesion("user@mail.cl", "1234")
        dispatcher.scheduler.advanceUntilIdle()

        assertNotNull(usuarioViewModel.cliente.value)

        usuarioViewModel.cerrarSesion()

        assertNull(usuarioViewModel.cliente.value)
    }

    // ───────────────────────────────────────────────
    //  4) cargarAutos con 2 autos actualiza la lista
    // ───────────────────────────────────────────────
    @Test
    fun cargarAutosActualizaLista() = runTest {
        val lista = listOf(
            AutoEntity(
                id = 1,
                marca = "Mazda",
                modelo = "3",
                anio = 2020,
                kilometraje = 5_000,
                precio = 12_000_000,
                disponible = true,
                fotoId = 0
            ),
            AutoEntity(
                id = 2,
                marca = "Toyota",
                modelo = "Yaris",
                anio = 2019,
                kilometraje = 20_000,
                precio = 9_000_000,
                disponible = true,
                fotoId = 0
            )
        )

        coEvery { autoDao.obtenerTodos() } returns lista

        autoViewModel.cargarAutos()
        dispatcher.scheduler.advanceUntilIdle()

        val autos = autoViewModel.autos.value
        assertEquals(2, autos.size)
        assertEquals("Mazda", autos[0].marca)
        assertEquals("Toyota", autos[1].marca)
    }


    @Test
    fun cargarAutosCopiaBienDatos() = runTest {
        val lista = listOf(
            AutoEntity(
                id = 5,
                marca = "Hyundai",
                modelo = "Accent",
                anio = 2021,
                kilometraje = 10_000,
                precio = 11_000_000,
                disponible = true,
                fotoId = 0
            )
        )

        coEvery { autoDao.obtenerTodos() } returns lista

        autoViewModel.cargarAutos()
        dispatcher.scheduler.advanceUntilIdle()

        val autos = autoViewModel.autos.value
        assertEquals(1, autos.size)
        assertEquals(5, autos[0].id)
        assertEquals("Hyundai", autos[0].marca)
        assertEquals("Accent", autos[0].modelo)
        assertEquals(11_000_000, autos[0].precio)
    }


    @Test
    fun cargarAutosVacioDejaListaVacia() = runTest {
        coEvery { autoDao.obtenerTodos() } returns emptyList()

        autoViewModel.cargarAutos()
        dispatcher.scheduler.advanceUntilIdle()

        val autos = autoViewModel.autos.value
        assertEquals(0, autos.size)
    }
}