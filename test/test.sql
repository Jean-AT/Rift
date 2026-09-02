USE [BaseDatosProduccion];
GO

SET XACT_ABORT ON;

BEGIN TRANSACTION;

IF OBJECT_ID('dbo.transacciones', 'U') IS NOT NULL
BEGIN
ALTER TABLE dbo.transacciones DROP CONSTRAINT IF EXISTS FK_transacciones_usuarios;
DROP TABLE dbo.transacciones;
END

IF OBJECT_ID('dbo.usuarios', 'U') IS NOT NULL
DROP TABLE dbo.usuarios;

IF OBJECT_ID('dbo.logs_sistema', 'U') IS NOT NULL
DROP TABLE dbo.logs_sistema;

CREATE TABLE dbo.usuarios (
                              id_usuario INT IDENTITY(1,1) PRIMARY KEY, -- IDENTITY reemplaza a SERIAL
                              nombre NVARCHAR(100) NOT NULL,            -- NVARCHAR para soporte Unicode
                              email NVARCHAR(150) NOT NULL,
                              fecha_registro DATETIME2 DEFAULT GETDATE(),

                              CONSTRAINT UQ_usuarios_email UNIQUE (email)
);


CREATE TABLE dbo.transacciones (
                                   id_transaccion INT IDENTITY(1,1) PRIMARY KEY,
                                   id_usuario INT NOT NULL,
                                   monto DECIMAL(12, 2) NOT NULL,
                                   estado VARCHAR(20) DEFAULT 'pendiente',
                                   fecha_pago DATETIME2 DEFAULT GETDATE(),

                                   CONSTRAINT FK_transacciones_usuarios FOREIGN KEY (id_usuario)
                                       REFERENCES dbo.usuarios(id_usuario) ON DELETE CASCADE
);


INSERT INTO dbo.usuarios (nombre, email) VALUES
                                             ('Carlos Mendoza', 'carlos@mail.com'),
                                             ('Ana Gomez', 'ana@mail.com'),
                                             ('Sofia Ruiz', 'sofia@mail.com');

INSERT INTO dbo.transacciones (id_usuario, monto, estado, fecha_pago) VALUES
                                                                          (1, 1500.50, 'completado', '2026-01-15 10:00:00'),
                                                                          (2, 45.00, 'fallido', '2026-02-20 14:30:00'),
                                                                          (3, 3200.00, 'completado', '2025-11-05 09:15:00');


DELETE FROM dbo.transacciones
WHERE fecha_pago < CAST('2026-01-01 00:00:00' AS DATETIME2);

ALTER TABLE dbo.usuarios DROP CONSTRAINT IF EXISTS UQ_usuarios_email;

COMMIT TRANSACTION;
GO
