package com.codebytes2.recommender.dto.request;

import java.time.Instant;

import com.codebytes2.recommender.backend.TournamentStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TournamentCreateRequest {

    @NotBlank(message = "El nombre del torneo es obligatorio.")
    private String name;

    @NotBlank(message = "El juego es obligatorio.")
    private String game;

    @NotNull(message = "La fecha de inicio es obligatoria.")
    @Future(message = "La fecha de inicio debe ser en el futuro.")
    private Instant startDate;

    @NotNull(message = "La fecha de finalización es obligatoria.")
    @Future(message = "La fecha de finalización debe ser posterior a la actual.")
    private Instant endDate;

    // NOTA: Para validar que endDate > startDate, se requeriría una anotación
    // a nivel de clase o una validación manual en el Service o Controller.

    @NotNull(message = "La fecha de apertura de registro es obligatoria.")
    @Future(message = "La apertura de registro debe ser en el futuro.")
    private Instant registrationOpenAt;

    @NotNull(message = "La fecha de cierre de registro es obligatoria.")
    @Future(message = "El cierre de registro debe ser en el futuro.")
    private Instant registrationCloseAt;

    @NotBlank(message = "Las reglas del torneo son obligatorias.")
    private String rules;

    @NotNull(message = "El número máximo de participantes es obligatorio.")
    @Min(value = 2, message = "El torneo debe tener al menos {value} participantes.")
    private Integer maxParticipants;

    // El estado inicial (status) podría ser opcional si el servicio lo fija por defecto
    // o se podría añadir @NotNull si se espera que el cliente lo envíe.
    private TournamentStatus status;
}