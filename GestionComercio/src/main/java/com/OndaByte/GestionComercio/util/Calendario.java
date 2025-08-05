package com.OndaByte.GestionComercio.util;

import java.time.LocalDate;

public class Calendario {

    public static LocalDate sigPeriodo(LocalDate ultimo, int repeticion) {
        switch (repeticion) {
            case 0:
                return ultimo.plusDays(1);    // Diario
            case 1:
                return ultimo.plusWeeks(1);   // Semanal
            case 2:
                return ultimo.plusWeeks(2);   // 2 semanas
            case 3:
                return ultimo.plusMonths(1);  // Mensual
            case 4:
                return ultimo.plusMonths(2);  // 2 meses
            case 5:
                return ultimo.plusMonths(3);
            case 6:
                return ultimo.plusMonths(4);
            case 7:
                return ultimo.plusMonths(6);
            case 8:
                return ultimo.plusYears(1);   // Anual
            default:
                throw new IllegalArgumentException("Repetición inválida");
        }
    }
}
