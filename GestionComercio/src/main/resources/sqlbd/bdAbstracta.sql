SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS Producto;
DROP TABLE IF EXISTS Categoria;
DROP TABLE IF EXISTS Cliente;
DROP TABLE IF EXISTS Empresa;
DROP TABLE IF EXISTS RolPermiso;

DROP TABLE IF EXISTS UsuarioRol;
DROP TABLE IF EXISTS Permiso;
DROP TABLE IF EXISTS Rol;
DROP TABLE IF EXISTS Usuario;

DROP TABLE IF EXISTS Periodo;
DROP TABLE IF EXISTS GastoFijo;
DROP TABLE IF EXISTS Venta;
DROP TABLE IF EXISTS ItemVenta;
DROP TABLE IF EXISTS Movimiento;
DROP TABLE IF EXISTS Caja;
DROP TABLE IF EXISTS SesionCaja;

SET FOREIGN_KEY_CHECKS = 1;


-- USUARIO TABLAS --

CREATE TABLE Usuario(
    id INT AUTO_INCREMENT PRIMARY KEY,
    creado TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultMod TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE current_timestamp(),
    estado ENUM("ACTIVO","INACTIVO") DEFAULT "ACTIVO",

    usuario VARCHAR(50) UNIQUE NOT NULL,
    contra VARCHAR(500) NOT NULL
);

CREATE TABLE Rol(
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE Permiso(
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE UsuarioRol (
    usuario_id INT NOT NULL,
    rol_id INT NOT NULL,
    PRIMARY KEY (usuario_id, rol_id),
    FOREIGN KEY (usuario_id) REFERENCES Usuario(id) ON DELETE CASCADE,
    FOREIGN KEY (rol_id) REFERENCES Rol(id) ON DELETE CASCADE
);

CREATE TABLE RolPermiso (
    rol_id INT NOT NULL,
    permiso_id INT NOT NULL,
    PRIMARY KEY (rol_id, permiso_id),
    FOREIGN KEY (rol_id) REFERENCES Rol(id) ON DELETE CASCADE,
    FOREIGN KEY (permiso_id) REFERENCES Permiso(id) ON DELETE CASCADE
);

-- EMPRESA

CREATE TABLE Empresa(
    id INT AUTO_INCREMENT PRIMARY KEY,
    creado TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultMod TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE current_timestamp(),
    estado ENUM("ACTIVO","INACTIVO") DEFAULT "ACTIVO",
    
    nombre VARCHAR(50) NOT NULL,
    telefono VARCHAR(50) NULL,
    email VARCHAR(50) NULL,
    direccion VARCHAR(100) NULL
);

-- CLIENTE TABLAS

CREATE TABLE Cliente(
    id INT AUTO_INCREMENT PRIMARY KEY,
    creado TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultMod TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE current_timestamp(),
    estado ENUM("ACTIVO","INACTIVO") DEFAULT "ACTIVO",

    nombre VARCHAR(99) NOT NULL,
    email VARCHAR(50) DEFAULT NULL,
    dni VARCHAR(50) DEFAULT NULL,
    cuit_cuil VARCHAR(50) DEFAULT NULL,
    telefono VARCHAR(50) DEFAULT NULL,
    direccion VARCHAR(50) DEFAULT NULL,
    localidad VARCHAR(50) DEFAULT NULL,
    codigo_postal VARCHAR(50) DEFAULT NULL,
    provincia VARCHAR(50) DEFAULT NULL,
    cond_iva ENUM("RESPONSABLE INSCRIPTO","MONOTRIBUTISTA","EXENTO") DEFAULT "MONOTRIBUTISTA"
);


---- Gastos Fijos

CREATE TABLE GastoFijo(
    id INT AUTO_INCREMENT PRIMARY KEY,
    creado TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultMod TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE current_timestamp(),
    estado ENUM("ACTIVO","INACTIVO") DEFAULT "ACTIVO",

    ult_pausa DATE NULL DEFAULT NULL,
    nombre VARCHAR(50) UNIQUE NOT NULL,
    inicio DATE NOT NULL,
    repeticion INT DEFAULT 3
);

CREATE TABLE Periodo(
    id INT AUTO_INCREMENT UNIQUE,
    estado ENUM("ACTIVO","INACTIVO") DEFAULT "ACTIVO",

    movimiento_id INT NULL,
    fecha_pago TIMESTAMP DEFAULT NULL,

    costo FLOAT DEFAULT 0,
    gasto_id INT NOT NULL,
    periodo DATE NOT NULL,
    FOREIGN KEY (gasto_id) REFERENCES GastoFijo(id) ON DELETE CASCADE,
    PRIMARY KEY (gasto_id,periodo)
);

CREATE TABLE Caja(
    id INT AUTO_INCREMENT PRIMARY KEY,
    creado TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultMod TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE current_timestamp(),
    estado ENUM("ACTIVO","INACTIVO") DEFAULT "ACTIVO",
    
    estado_caja ENUM("ABIERTA","CERRADA") DEFAULT "CERRADA",
    nombre VARCHAR(50) NOT NULL UNIQUE,
    sesion_actual INT DEFAULT NULL
);

CREATE TABLE SesionCaja(
    id INT AUTO_INCREMENT PRIMARY KEY,
    monto_inicial DECIMAL(12,2) NOT NULL DEFAULT 0,
    monto_final DECIMAL(12,2) NULL,
    apertura TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cierre TIMESTAMP DEFAULT NULL,
    cajero_id INT NOT NULL,
    caja_id INT NOT NULL,
    FOREIGN KEY (cajero_id) REFERENCES Usuario(id),
    FOREIGN KEY (caja_id) REFERENCES Caja(id)
);

CREATE TABLE Movimiento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    creado TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultMod TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    estado ENUM("ACTIVO", "INACTIVO") DEFAULT "ACTIVO",
    
    cliente_id INT DEFAULT NULL,
    sesion_caja_id INT NOT NULL,

    tipo_mov ENUM("VENTA", "INGRESO", "EGRESO") DEFAULT "VENTA",
    descripcion VARCHAR(255) DEFAULT NULL,
    total DECIMAL(12,2) NOT NULL,
    FOREIGN KEY (cliente_id) REFERENCES Cliente(id),
    FOREIGN KEY (sesion_caja_id) REFERENCES SesionCaja(id) ON DELETE CASCADE
);


CREATE TABLE Venta (
    id INT AUTO_INCREMENT PRIMARY KEY,
    creado TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultMod TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE current_timestamp(),
    estado ENUM("ACTIVO","INACTIVO") DEFAULT "ACTIVO",
    
    movimiento_id INT NOT NULL UNIQUE,
    cliente_id INT DEFAULT NULL,

    subtotal DECIMAL(12,2) NOT NULL,
    porcentaje_descuento INT NOT NULL DEFAULT 0,
    total DECIMAL(12,2) NOT NULL,
    
    forma_pago ENUM('EFECTIVO','TRANSFERENCIA') NOT NULL DEFAULT 'EFECTIVO',
    punto_venta VARCHAR(10) DEFAULT NULL,
    nro_comprobante VARCHAR(30) DEFAULT NULL,
    -- efectivo_entregado DECIMAL(12,2) DEFAULT NULL,
    -- vuelto DECIMAL(12,2) DEFAULT NULL,
    observaciones VARCHAR(255) DEFAULT NULL,
    FOREIGN KEY (movimiento_id) REFERENCES Movimiento(id) ON DELETE CASCADE
);

/*
CREATE TABLE DescuentoCategoria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    categoria_id INT NOT NULL,
    porcentaje FLOAT NOT NULL,
    fecha_inicio DATE,
    fecha_fin DATE,
    FOREIGN KEY (categoria_id) REFERENCES Categoria(id)
);
*/

CREATE TABLE Categoria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    creado TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultMod TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE current_timestamp(),
    estado ENUM("ACTIVO","INACTIVO") DEFAULT "ACTIVO",
    
    nombre VARCHAR(100) NOT NULL,
    porcentaje_descuento INT NOT NULL DEFAULT 0,
    tipo ENUM('PRODUCTO', 'CLIENTE') NOT NULL, -- pára tipo de producto y tipo de cliente por si escala 
    padre_id INT DEFAULT NULL,
    FOREIGN KEY (padre_id) REFERENCES Categoria(id)
);

CREATE TABLE Producto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    creado TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultMod TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE current_timestamp(),
    estado ENUM("ACTIVO","INACTIVO") DEFAULT "ACTIVO",
    
    categoria_id INT NOT NULL,

    nombre VARCHAR(100) NOT NULL,
    codigo_barra VARCHAR(200) DEFAULT NULL UNIQUE,
    descripcion TEXT DEFAULT NULL,
    precio_costo DECIMAL(10,2) NOT NULL,
    porcentaje_ganancia INT NOT NULL,
    porcentaje_descuento INT NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    FOREIGN KEY (categoria_id) REFERENCES Categoria(id)
);
 

CREATE TABLE ItemVenta (
    id INT AUTO_INCREMENT PRIMARY KEY,
    creado TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultMod TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE current_timestamp(),
    estado ENUM("ACTIVO","INACTIVO") DEFAULT "ACTIVO",

    venta_id INT NOT NULL, 
    producto_id INT NOT NULL,

    nombre VARCHAR(100) NOT NULL,    
    cantidad INT NULL,
    porcentaje_descuento INT DEFAULT 0,
    subtotal FLOAT DEFAULT 0 NOT NULL,
    FOREIGN KEY (venta_id) REFERENCES Venta(id) ON DELETE CASCADE,
    FOREIGN KEY (producto_id) REFERENCES Producto(id)
);

DROP TRIGGER IF EXISTS nueva_sesion_caja;
DROP TRIGGER IF EXISTS cerrar_sesion_caja;
DROP TRIGGER IF EXISTS alta_sesion_valida;
DROP TRIGGER IF EXISTS actualizar_total_cerrar_sesion;
DROP TRIGGER IF EXISTS alta_movimiento_valido;
DROP TRIGGER IF EXISTS alta_movimiento_gasto;

DELIMITER $$
CREATE TRIGGER nueva_sesion_caja
AFTER INSERT ON SesionCaja
FOR EACH ROW
BEGIN
    UPDATE Caja 
    SET estado_caja = 'ABIERTA', sesion_actual = NEW.id
    WHERE id = NEW.caja_id;
END;

CREATE TRIGGER cerrar_sesion_caja
AFTER UPDATE ON SesionCaja
FOR EACH ROW
BEGIN
    IF NEW.cierre IS NOT NULL AND OLD.cierre IS NULL THEN
        UPDATE Caja 
        SET estado_caja = 'CERRADA', sesion_actual = NULL
        WHERE id = NEW.caja_id;
    END IF;
END;

CREATE TRIGGER actualizar_total_cerrar_sesion
BEFORE UPDATE ON SesionCaja
FOR EACH ROW
BEGIN
    IF NEW.cierre IS NOT NULL AND OLD.cierre IS NULL THEN
        SET NEW.monto_final = COALESCE(
            (SELECT SUM(total) FROM Movimiento
            WHERE sesion_caja_id=NEW.id)+NEW.monto_inicial,0.0);
     END IF;
END;

CREATE TRIGGER alta_sesion_valida
BEFORE INSERT ON SesionCaja
FOR EACH ROW
BEGIN
    DECLARE caja_ocupada BOOLEAN;
    DECLARE empleado_ocupado BOOLEAN;
    
    SELECT sesion_actual IS NOT NULL 
    INTO caja_ocupada
    FROM Caja 
    WHERE id = NEW.caja_id;

    SELECT EXISTS (
        SELECT 1 
        FROM SesionCaja 
        WHERE cajero_id = NEW.cajero_id AND cierre IS NULL
    ) INTO empleado_ocupado;
    
    IF caja_ocupada THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No se puede crear sesion: caja en uso';
    ELSEIF empleado_ocupado THEN
        SIGNAL SQLSTATE '45001'
        SET MESSAGE_TEXT = 'No se puede crear sesion: cajero ya tiene sesion activa';
    END IF;
END;

CREATE TRIGGER alta_movimiento_valido
BEFORE INSERT ON Movimiento
FOR EACH ROW
BEGIN
    DECLARE sesion_cerrada BOOLEAN;
    
    SELECT cierre IS NOT NULL
    INTO sesion_cerrada
    FROM SesionCaja
    WHERE id = NEW.sesion_caja_id;

    IF sesion_cerrada THEN
       SIGNAL SQLSTATE '45002'
       SET MESSAGE_TEXT = 'No se puede crear movimiento, sesion cerrada';
    END IF;
END;

CREATE TRIGGER alta_movimiento_gasto
BEFORE UPDATE ON Periodo
FOR EACH ROW
BEGIN
    DECLARE sesion_abierta_id INT;
    IF NEW.fecha_pago IS NOT NULL AND OLD.fecha_pago IS NULL THEN   
      
        SELECT id INTO sesion_abierta_id
        FROM SesionCaja
        WHERE cierre IS NULL
        ORDER BY apertura DESC
        LIMIT 1;

        IF sesion_abierta_id IS NOT NULL THEN
            INSERT INTO Movimiento (
                tipo_mov, descripcion, total, sesion_caja_id
            ) VALUES (
                'EGRESO',
                CONCAT('Pago de GastoFijo ', NEW.gasto_id),
                NEW.costo,
                sesion_abierta_id
            );

            SET NEW.movimiento_id = LAST_INSERT_ID();
        ELSE
            -- Si no hay sesión abierta, lanzar error personalizado
            SIGNAL SQLSTATE '45002'
            SET MESSAGE_TEXT = 'No se puede registrar el movimiento: no hay sesión de caja abierta.';
        END IF;
    END IF;
END;



$$

DELIMITER ;

