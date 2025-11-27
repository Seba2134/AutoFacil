package com.project.autofacil

import com.project.autofacil.ViewModels.AutoViewModel
import com.project.autofacil.data.AutoDao
import com.project.autofacil.data.AutoEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class AutoViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var autoDao: AutoDao
    private lateinit var viewModel: AutoViewModel

    @Before
    fun setUp() {
        // Usamos un dispatcher de test para controlar las corutinas
        Dispatchers.setMain(dispatcher)

        // MUY IMPORTANTE: relaxed = true para que no explote por otras llamadas del ViewModel
        autoDao = mockk(relaxed = true)

        // Si en el init del ViewModel se llama a obtenerTodos(), devolvemos lista vacía por defecto
        coEvery { autoDao.obtenerTodos() } returns emptyList()

        viewModel = AutoViewModel(autoDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cargarAutos actualiza la lista de autos`() = runTest {
        // Dado: el DAO devuelve 2 autos
        val autosFicticios = listOf(
            AutoEntity(
                id = 1,
                marca = "Mazda",
                modelo = "3 Sport",
                anio = 2022,
                kilometraje = 5000,
                precio = 15000000,
                disponible = true,
                fotoId = 0
            ),
            AutoEntity(
                id = 2,
                marca = "Toyota",
                modelo = "Yaris",
                anio = 2020,
                kilometraje = 20000,
                precio = 9000000,
                disponible = true,
                fotoId = 0
            )
        )

        coEvery { autoDao.obtenerTodos() } returns autosFicticios

        // Cuando
        viewModel.cargarAutos()
        dispatcher.scheduler.advanceUntilIdle()

        // Entonces
        val lista = viewModel.autos.value
        assertEquals(2, lista.size)
        assertEquals("Mazda", lista[0].marca)
        assertEquals("Toyota", lista[1].marca)
    }

    @Test
    fun `agregarAuto llama a insertar en el dao`() = runTest {
        val nuevoAuto = AutoEntity(
            id = 0,
            marca = "Hyundai",
            modelo = "Accent",
            anio = 2021,
            kilometraje = 10000,
            precio = 11000000,
            disponible = true,
            fotoId = 0
        )

        // No nos importa lo que haga obtenerTodos aquí, que devuelva lista vacía
        coEvery { autoDao.obtenerTodos() } returns emptyList()

        // Cuando
        viewModel.agregarAuto(nuevoAuto)
        dispatcher.scheduler.advanceUntilIdle()

        // Entonces: verificamos que el ViewModel haya llamado al DAO
        coVerify(exactly = 1) { autoDao.insertar(nuevoAuto) }
    }
}