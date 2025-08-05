-- Inserciones para Rol
INSERT INTO Rol (nombre) VALUES
('ADMIN'),
('EMPLEADO'),
('USUARIO');

-- Inserciones para Permiso
INSERT INTO Permiso (nombre) VALUES
('CLIENTE_ALTA'),
('CLIENTE_BAJA'),
('CLIENTE_LISTAR'),
('CLIENTE_MODIFICAR'),
('EMPLEADO_ALTA'),
('EMPLEADO_BAJA'),
('EMPLEADO_LISTAR'),
('EMPLEADO_MODIFICAR'),
('GASTO_ALTA'),
('GASTO_BAJA'),
('GASTO_LISTAR'),
('GASTO_MODIFICAR'),
('INSUMO_ALTA'),
('INSUMO_BAJA'),
('INSUMO_LISTAR'),
('INSUMO_MODIFICAR'),
('ORDEN_ALTA'),
('ORDEN_BAJA'),
('ORDEN_LISTAR'),
('ORDEN_MODIFICAR'),
('PEDIDO_ALTA'),
('PEDIDO_BAJA'),
('PEDIDO_LISTAR'),
('PEDIDO_MODIFICAR'),
('PRESUPUESTO_ALTA'),
('PRESUPUESTO_BAJA'),
('PRESUPUESTO_LISTAR'),
('PRESUPUESTO_MODIFICAR'),
('TURNO_ALTA'),
('TURNO_BAJA'),
('TURNO_LISTAR'),
('TURNO_MODIFICAR'),
('USUARIO_ALTA'),
('USUARIO_BAJA'),
('USUARIO_LISTAR'),
('USUARIO_MODIFICAR');

-- Asignar permisos a roles
INSERT INTO RolPermiso (rol_id, permiso_id) VALUES
(1, 1),
(1, 2),
(1, 3),
(1, 4),
(1, 5),
(1, 6),
(1, 7),
(1, 8),
(1, 9),
(1, 10),
(1, 11),
(1, 12),
(1, 13),
(1, 14),
(1, 15),
(1, 16),
(1, 17),
(1, 18),
(1, 19),
(1, 20),
(1, 21),
(1, 22),
(1, 23),
(1, 24),
(1, 25),
(1, 26),
(1, 27),
(1, 28),
(1, 29),
(1, 30),
(1, 31),
(1, 32),
(1, 33),
(1, 34),
(1, 35),
(1, 36),
(2, 1), -- Empleado puede dar de ALTA un cliente
(2, 3), -- Empleado puede LISTAR los cliente
(2, 4), -- Empleado puede MODIFICAR un cliente
(2, 21), -- Empleado puede dar de ALTA pedido
(2, 23), -- Empleado puede LISTAR los pedidos
(2, 24), -- Empleado puede MODIFICAR un pedido
(2, 15), -- Empleado puede LISTAR los insumos
(2, 16), -- Empleado puede MODIFICAR un insumo
(2, 17), -- Empleado puede dar de ALtA una orden
(2, 19), -- Empleado puede actualizar orden ¿CUAL ES LA DIFERENCIA? (LISTAR)
(2, 20), -- Empleado puede MODIFICAR una orden
(2, 36), -- Empleado deberia poder cambiar su contra
(3, 36); -- Usuario deberia poder cambiar su contra

-- Inserciones para Cliente
INSERT INTO `Cliente`
(`nombre`, `email`, `dni`, `cuit_cuil`, `telefono`, `direccion`, `localidad`, `codigo_postal`, `provincia`, `cond_iva`)
VALUES
    ('Juan Pérez','cliente@hotmail.com', '12345678', '20-12345678-9', '1123456789', 'Av. Siempre Viva 123', 'Ciudad de Buenos Aires', '1400', 'Buenos Aires', 'RESPONSABLE INSCRIPTO'),
    ('María Gómez','cliente2@yahoo.com', '23456789', '27-23456789-0', '1134567890', 'Calle Falsa 456', 'La Plata', '1900', 'Buenos Aires', 'MONOTRIBUTISTA'),
    ('Carlos López','cliente3@gmail.com', '34567890', '20-34567890-1', '1145678901', 'Av. Libertador 789', 'Mendoza', '5500', 'Mendoza', 'EXENTO');

-- Inserciones para Insumo
INSERT INTO Insumo (nombre, precio, stock) VALUES
('Pintura Roja', 50.00, 100),
('Tornillos 3mm', 5.00, 500);

-- Inserciones para Empleado
INSERT INTO Empleado (dni, nombre, telefono, direccion) VALUES
('22334455', 'Carlos Gómez', '333333333', 'Calle Industrial 789'),
('33445566', 'Ana Fernández', '444444444', 'Av. Comercial 321');

-- Inserciones para Recurso
-- INSERT INTO Recurso (orden_id, insumo_id, empleado_id) VALUES
-- (1, 1, 1),
-- (2, 2, 2);

INSERT INTO Usuario
(estado,usuario,contra)
VALUES
("ACTIVO", "fran", "$2a$10$GwuLXIm2pFBq5KOUc27VjOqiNAv.sQ3rj8YgwooVcF7vxGgeviEr2"),
("ACTIVO", "fran2", "$2a$10$3Y0ACtiagET0hasOs2zs3OXFj18gUGZX247OeNQS6DW0M..IcVbKO"),
("ACTIVO", "fran3", "$2a$10$idqTko6.OM4hxae7Omn/3OZqCNSUtsnMWWQ2w7G1GaOcqVVdJVc8u"),
("ACTIVO", "luc", "$2a$10$FBfutjjtgqDw7mDPhe16V.SUI9rZm.sxMLr1UVc6piarjV045NDwa"),
("ACTIVO", "moc", "$2a$10$h6VonR5UlST/KMQcVKoUOOyWMT9iTEAyT.sqiVU3dpyA1uyaAF2D2");

INSERT INTO UsuarioRol
(usuario_id,rol_id)
VALUES
(1,1),
(2,2),
(3,3),
(4,1),
(5,1);

-- Inserciones en GastoFijo
INSERT INTO GastoFijo (nombre, inicio, repeticion) VALUES
('Alquiler Oficina', '2025-01-01', 3),   -- mensual
('Licencia Software', '2025-02-15', 8),  -- anual
('Comida Dia', '2025-02-15', 0),  -- diario
('Limpieza Semanal', '2025-05-01', 1);   -- semanal

-- Periodos para 'Alquiler Oficina' (mensual)
INSERT INTO Periodo (gasto_id, periodo, costo) VALUES
(1, '2025-01-01', 1200),
(2, '2025-01-01', 500),
(3, '2025-01-01', 50);

UPDATE GastoFijo SET estado='INACTIVO' WHERE id = 3;

INSERT INTO Caja (estado_caja, nombre, sesion_actual) VALUES
("CERRADA", 'Caja 1', NULL);


-- INSERT INTO SesionCaja (monto_inicial,caja_id,cajero_id) VALUES (0,1,1);
-- INSERT INTO Movimiento (tipo_mov,descripcion,total,sesion_caja_id) VALUES ("VENTA",NULL,200,1);
-- INSERT INTO Movimiento (tipo_mov,descripcion,total,sesion_caja_id) VALUES ("VENTA",NULL,100,1);
-- INSERT INTO Movimiento (tipo_mov,descripcion,total,sesion_caja_id) VALUES ("VENTA",NULL,100,1);
