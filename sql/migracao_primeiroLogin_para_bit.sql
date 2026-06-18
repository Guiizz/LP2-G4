UPDATE Estudante
   SET primeiroLogin = CASE
        WHEN LOWER(LTRIM(RTRIM(primeiroLogin))) IN ('1', 'true', 'tru', 'sim', 's', 'y', 'yes') THEN '1'
        ELSE '0'
   END;

UPDATE Docente
   SET primeiroLogin = CASE
        WHEN LOWER(LTRIM(RTRIM(primeiroLogin))) IN ('1', 'true', 'tru', 'sim', 's', 'y', 'yes') THEN '1'
        ELSE '0'
   END;

UPDATE Gestor
   SET primeiroLogin = CASE
        WHEN LOWER(LTRIM(RTRIM(primeiroLogin))) IN ('1', 'true', 'tru', 'sim', 's', 'y', 'yes') THEN '1'
        ELSE '0'
   END;

DECLARE @sql NVARCHAR(MAX) = N'';
SELECT @sql += 'ALTER TABLE ' + QUOTENAME(t.name) + ' DROP CONSTRAINT ' + QUOTENAME(d.name) + ';'
FROM sys.default_constraints d
JOIN sys.columns c ON c.default_object_id = d.object_id
JOIN sys.tables  t ON t.object_id = c.object_id
WHERE c.name = 'primeiroLogin';
EXEC sp_executesql @sql;

ALTER TABLE Estudante ALTER COLUMN primeiroLogin BIT NOT NULL;
ALTER TABLE Docente   ALTER COLUMN primeiroLogin BIT NOT NULL;
ALTER TABLE Gestor    ALTER COLUMN primeiroLogin BIT NOT NULL;

ALTER TABLE Estudante ADD CONSTRAINT DF_Estudante_primeiroLogin DEFAULT 1 FOR primeiroLogin;
ALTER TABLE Docente   ADD CONSTRAINT DF_Docente_primeiroLogin   DEFAULT 1 FOR primeiroLogin;
ALTER TABLE Gestor    ADD CONSTRAINT DF_Gestor_primeiroLogin    DEFAULT 1 FOR primeiroLogin;
