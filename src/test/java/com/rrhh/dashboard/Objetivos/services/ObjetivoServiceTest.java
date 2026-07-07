package com.rrhh.dashboard.Objetivos.services;

import com.rrhh.dashboard.Objetivos.Entity.Objetivo;
import com.rrhh.dashboard.Objetivos.Entity.TipoObjetivo;
import com.rrhh.dashboard.Objetivos.Repository.ObjetivoRepository;
import com.rrhh.dashboard.Objetivos.dtos.ObjetivoProgresoResponse;
import com.rrhh.dashboard.Objetivos.exceptions.DuplicateObjetivoException;
import com.rrhh.dashboard.Objetivos.exceptions.InvalidObjetivoException;
import com.rrhh.dashboard.Objetivos.exceptions.ObjetivoNotFoundException;
import com.rrhh.dashboard.Productividad.Service.RegistroHorarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObjetivoServiceTest {

    @Mock
    private ObjetivoRepository repository;

    @Mock
    private RegistroHorarioService registroHorarioService;

    private ObjetivoService service;

    private static final LocalDate LUNES = LocalDate.of(2026, 7, 6);

    @BeforeEach
    void setUp() {
        service = new ObjetivoService(repository, registroHorarioService);
    }

    @Test
    void crearGuardaElObjetivoConSemanaIndicada() {
        Long empleadoId = 1L;
        when(repository.existsByEmpleadoIdAndTipoAndSemanaInicio(empleadoId, TipoObjetivo.PEDIDOS, LUNES))
                .thenReturn(false);
        when(repository.save(any(Objetivo.class))).thenAnswer(inv -> inv.getArgument(0));

        Objetivo objetivo = service.crear(empleadoId, TipoObjetivo.PEDIDOS, 120.0, LUNES);

        assertThat(objetivo.getEmpleadoId()).isEqualTo(empleadoId);
        assertThat(objetivo.getValorSemanal()).isEqualTo(120.0);
        assertThat(objetivo.getValorDiario()).isEqualTo(20.0);
        assertThat(objetivo.getSemanaInicio()).isEqualTo(LUNES);
    }

    @Test
    void crearFallaSiYaExisteUnObjetivoDelMismoTipoYSemana() {
        Long empleadoId = 1L;
        when(repository.existsByEmpleadoIdAndTipoAndSemanaInicio(empleadoId, TipoObjetivo.PEDIDOS, LUNES))
                .thenReturn(true);

        assertThatThrownBy(() -> service.crear(empleadoId, TipoObjetivo.PEDIDOS, 120.0, LUNES))
                .isInstanceOf(DuplicateObjetivoException.class);
    }

    @Test
    void crearFallaConValorSemanalInvalido() {
        Long empleadoId = 1L;
        when(repository.existsByEmpleadoIdAndTipoAndSemanaInicio(empleadoId, TipoObjetivo.PEDIDOS, LUNES))
                .thenReturn(false);

        assertThatThrownBy(() -> service.crear(empleadoId, TipoObjetivo.PEDIDOS, 0.0, LUNES))
                .isInstanceOf(InvalidObjetivoException.class);
    }

    @Test
    void actualizarReemplazaElValorSemanal() {
        Objetivo existente = new Objetivo(1L, TipoObjetivo.PEDIDOS, 60.0, LUNES);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));

        Objetivo actualizado = service.actualizar(1L, 90.0);

        assertThat(actualizado.getValorSemanal()).isEqualTo(90.0);
    }

    @Test
    void eliminarFallaSiNoExiste() {
        when(repository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> service.eliminar(1L)).isInstanceOf(ObjetivoNotFoundException.class);
    }

    @Test
    void calcularProgresoParaPedidosCruzaConProductividadCargada() {
        Objetivo objetivo = new Objetivo(1L, TipoObjetivo.PEDIDOS, 120.0, LUNES);
        when(repository.findById(1L)).thenReturn(Optional.of(objetivo));
        when(registroHorarioService.totalPedidosDelDia(eq(1L), any(LocalDate.class))).thenReturn(5);

        ObjetivoProgresoResponse progreso = service.calcularProgreso(1L);

        assertThat(progreso.cargadoHoy()).isEqualTo(5.0);
        assertThat(progreso.pendienteHoy()).isEqualTo(15.0);
        assertThat(progreso.cargadoSemana()).isGreaterThan(0);
    }

    @Test
    void calcularProgresoParaDineroNoTieneCruceAutomatico() {
        Objetivo objetivo = new Objetivo(1L, TipoObjetivo.DINERO, 50000.0, LUNES);
        when(repository.findById(1L)).thenReturn(Optional.of(objetivo));

        ObjetivoProgresoResponse progreso = service.calcularProgreso(1L);

        assertThat(progreso.cargadoHoy()).isNull();
        assertThat(progreso.pendienteHoy()).isNull();
        assertThat(progreso.cargadoSemana()).isNull();
        assertThat(progreso.pendienteSemana()).isNull();
    }
}
