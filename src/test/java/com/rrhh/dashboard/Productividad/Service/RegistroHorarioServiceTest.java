package com.rrhh.dashboard.Productividad.Service;

import com.rrhh.dashboard.Productividad.Entity.RegistroHorario;
import com.rrhh.dashboard.Productividad.exceptions.YaRegistradoEnEstaHoraException;
import com.rrhh.dashboard.Productividad.repository.RegistroHorarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistroHorarioServiceTest {

    @Mock
    private RegistroHorarioRepository repository;

    private RegistroHorarioService service;

    @BeforeEach
    void setUp() {
        service = new RegistroHorarioService(repository);
    }

    @Test
    void registrarCreaElRegistroCuandoNoHayCargaEnEstaHora() {
        Long empleadoId = 1L;
        when(repository.existsByEmpleadoIdAndRegistradoEnBetween(eq(empleadoId), any(), any())).thenReturn(false);
        when(repository.save(any(RegistroHorario.class))).thenAnswer(inv -> inv.getArgument(0));

        RegistroHorario resultado = service.registrar(empleadoId, 10, 5);

        assertThat(resultado.getEmpleadoId()).isEqualTo(empleadoId);
        assertThat(resultado.getPedidosPreparados()).isEqualTo(10);
        assertThat(resultado.getBultosPreparados()).isEqualTo(5);
    }

    @Test
    void registrarFallaSiYaCargoEnEstaHora() {
        Long empleadoId = 1L;
        when(repository.existsByEmpleadoIdAndRegistradoEnBetween(eq(empleadoId), any(), any())).thenReturn(true);

        assertThatThrownBy(() -> service.registrar(empleadoId, 10, 5))
                .isInstanceOf(YaRegistradoEnEstaHoraException.class);
    }

    @Test
    void totalPedidosDelDiaSumaLosRegistrosDeEseDia() {
        Long empleadoId = 1L;
        LocalDate hoy = LocalDate.now();
        Instant unaHora = hoy.atStartOfDay(ZoneId.systemDefault()).toInstant().plusSeconds(3600);
        RegistroHorario registro1 = new RegistroHorario(empleadoId, 7, 2, unaHora);
        RegistroHorario registro2 = new RegistroHorario(empleadoId, 3, 1, unaHora.plusSeconds(3600));
        when(repository.findByEmpleadoIdAndRegistradoEnBetween(eq(empleadoId), any(), any()))
                .thenReturn(List.of(registro1, registro2));

        int total = service.totalPedidosDelDia(empleadoId, hoy);

        assertThat(total).isEqualTo(10);
    }
}
