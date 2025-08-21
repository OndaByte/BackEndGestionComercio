
-- Inserciones para Rol
INSERT INTO Rol (nombre) VALUES
('ADMIN'),
('EMPLEADO'),
('USUARIO');

-- Inserciones para Permiso
INSERT INTO Permiso (nombre) VALUES
('CAJA_ABRIR'),
('CAJA_CERRAR'),
('CAJA_VENTA'),
('CAJA_VER_INFO'),
('CATEGORIA_ALTA'),
('CATEGORIA_BAJA'),
('CATEGORIA_LISTAR'),
('CATEGORIA_MODIFICAR'),
('DESCUENTO_PRECIO_UNITARIO'),
('DESCUENTO_SOBRE_TOTAL'),
('MOVIMIENTO_ALTA'),
('MOVIMIENTO_BAJA'),
('MOVIMIENTO_LISTAR'),
('MOVIMIENTO_MODIFICAR'),
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
('PRODUCTO_ALTA'),
('PRODUCTO_BAJA'),
('PRODUCTO_LISTAR'),
('PRODUCTO_MODIFICAR'),
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
(2, 1), -- Vendedor abre caja
(2, 3), -- Vendedor hace venta
(2, 7), -- Vendedor puede ver y buscar categorias
(2, 29), -- Vendedor puede ver y buscar productos
(2, 34), -- Vendedor puede cambiar su contra
(3, 34); -- Usuario puede cambiar su contra

INSERT INTO Usuario
(estado,usuario,contra)
VALUES
("ACTIVO", "misterqueso", "$2a$10$xJQ6mbLodSr9bwo6G0h27Onp5dSBbqFfbLuif/0kTu1ZGNoDqUx8."),
("ACTIVO", "vendedor","$2a$10$prvpKU.xdZf1X1MwroLsreA61VjuLp/a6OBHgpBgfGDlXVh5GLFem");

INSERT INTO UsuarioRol
(usuario_id,rol_id)
VALUES
(1,1),
(2,2);

INSERT INTO Caja (estado_caja, nombre, sesion_actual) VALUES
("CERRADA", 'Caja 1', NULL);
