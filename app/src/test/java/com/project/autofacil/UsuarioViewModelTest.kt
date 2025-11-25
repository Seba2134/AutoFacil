package com.project.autofacil

import com.project.autofacil.ViewModels.UsuarioViewModel
import com.project.autofacil.data.UsuarioDao
import com.project.autofacil.data.UsuarioEntity
import io.mockk.coEvery
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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class UsuarioViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var dao: UsuarioDao
    private lateinit var viewModel: UsuarioViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        dao = mockk(relaxed = true)
        viewModel = UsuarioViewModel(dao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login correcto llena cliente`() = runTest {
        val adminEntity = UsuarioEntity(
            id = 1,
            nombre = "Admin",
            correo = "admin@autofacil.cl",
            contrasena = "admin123",
            direccion = "Oficina",
            rol = "admin"
        )

        coEvery { dao.login("admin@autofacil.cl", "admin123") } returns adminEntity

        viewModel.iniciarSesion("admin@autofacil.cl", "admin123")
        dispatcher.scheduler.advanceUntilIdle()

        assertNotNull(viewModel.cliente.value)
    }

    @Test
    fun `login incorrecto no llena cliente`() = runTest {
        coEvery { dao.login("alguien@correo.cl", "mala") } returns null

        viewModel.iniciarSesion("alguien@correo.cl", "mala")
        dispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.cliente.value)
    }
}