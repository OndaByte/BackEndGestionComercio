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
(3, 4); -- Usuario deberia poder cambiar su contra

INSERT INTO Usuario
(estado,usuario,contra)
VALUES
("ACTIVO", "antartida", "$2a$10$zEQlxtngIZHklP3kyLDLLuxU0rwRuqtbHLc01eyDrY0tAx110uY46");

INSERT INTO UsuarioRol
(usuario_id,rol_id)
VALUES
(1,1);
