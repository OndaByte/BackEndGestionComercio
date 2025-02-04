-- Inserciones en Usuario
INSERT INTO Usuario (id, usuario, contra) VALUES (1, 'usuario1', 'contra1');
INSERT INTO Usuario (id, usuario, contra) VALUES (2, 'usuario2', 'contra2');
INSERT INTO Usuario (id, usuario, contra) VALUES (3, 'usuario3', 'contra3');
INSERT INTO Usuario (id, usuario, contra) VALUES (4, 'usuario4', 'contra4');
INSERT INTO Usuario (id, usuario, contra) VALUES (5, 'usuario5', 'contra5');

-- Inserciones en la tabla Rol
INSERT INTO Rol (nombre) VALUES ('Admin');
INSERT INTO Rol (nombre) VALUES ('User');
INSERT INTO Rol (nombre) VALUES ('Guest');

-- Inserciones en la tabla Permiso
INSERT INTO Permiso (nombre) VALUES ('Leer');
INSERT INTO Permiso (nombre) VALUES ('Escribir');
INSERT INTO Permiso (nombre) VALUES ('Eliminar');

-- Inserciones en la tabla Usuario_Rol
INSERT INTO UsuarioRol (usuario_id, rol_id) VALUES (1, 1); -- usuario1 es Admin
INSERT INTO UsuarioRol (usuario_id, rol_id) VALUES (5, 1); -- usuario5 es Admin
INSERT INTO UsuarioRol (usuario_id, rol_id) VALUES (2, 2); -- usuario2 es User
INSERT INTO UsuarioRol (usuario_id, rol_id) VALUES (3, 3); -- usuario3 es Guest

-- Inserciones en la tabla Rol_Permiso
INSERT INTO RolPermiso (rol_id, permiso_id) VALUES (1, 1); -- Admin tiene permiso Leer
INSERT INTO RolPermiso (rol_id, permiso_id) VALUES (1, 2); -- Admin tiene permiso Escribir
INSERT INTO RolPermiso (rol_id, permiso_id) VALUES (1, 3); -- Admin tiene permiso Eliminar
INSERT INTO RolPermiso (rol_id, permiso_id) VALUES (2, 1); -- User tiene permiso Leer
INSERT INTO RolPermiso (rol_id, permiso_id) VALUES (3, 1); -- Guest tiene permiso Leer


INSERT INTO Producto (nombre, precio_costo, precio_venta, stock_actual, stock_frizado, ingredientes_receta)
VALUES 
('Pizza Margherita', 3.50, 8.00, 50, 10, 'Harina, Agua, Tomate, Mozzarella, Albahaca'),
('Empanadas de Carne', 1.20, 3.00, 100, 20, 'Carne, Cebolla, Huevo, Aceituna, Masa'),
('Hamburguesa Clasica', 2.00, 5.50, 70, 15, 'Carne de res, Lechuga, Tomate, Queso, Pan'),
('Sushi Roll', 4.00, 12.00, 40, 5, 'Arroz, Alga nori, Salmin, Aguacate, Pepino'),
('Lasania de Carne', 3.80, 9.50, 30, 8, 'Pasta, Carne, Tomate, Mozzarella, Bechamel');


-- Inserciones en la tabla Caja
INSERT INTO Caja (dinero_inicial, dinero_total, fecha_cierre, estado) VALUES
(100.0, 200.0, NULL, 'ACTIVO'),
(200.0, 400.0, NULL, 'ACTIVO');

-- Inserciones en la tabla Movimiento
INSERT INTO Movimiento (caja_id, descripcion, total, estado) VALUES
(1, 'Apertura de caja', 100.0, 'ACTIVO'),
(2, 'Ingreso por ventas', 200.0, 'ACTIVO');

-- Inserciones en la tabla Venta
INSERT INTO Venta (caja_id, metodo_pago, total, fecha_pago, estado) VALUES
(1, 'Efectivo', 50.0, CURRENT_TIMESTAMP, 'ACTIVO'),
(2, 'Tarjeta', 100.0, CURRENT_TIMESTAMP, 'ACTIVO');

-- Inserciones en la tabla Items_venta
INSERT INTO ItemVenta (producto_id, venta_id, cantidad, subtotal, estado) VALUES
(1, 1, 3, 4.5, 'ACTIVO'),
(2, 2, 2, 2.4, 'ACTIVO');
