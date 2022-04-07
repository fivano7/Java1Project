CREATE DATABASE ProjektniZadatak
GO
USE ProjektniZadatak
GO

----------- TABLICE----------------
CREATE TABLE AppUser (

IDAppUser INT PRIMARY KEY IDENTITY,
Email NVARCHAR(50) NOT NULL,
PasswordHash NVARCHAR(50) NOT NULL,
IsAdministrator INT NOT NULL
)
GO

CREATE TABLE Genre (
	
	IDGenre INT PRIMARY KEY IDENTITY,
	GenreName NVARCHAR(50) NOT NULL
)
GO

CREATE TABLE Movie (
	IDMovie INT PRIMARY KEY IDENTITY,
	Title NVARCHAR(200),
	PublishedDate NVARCHAR(100),
	MovieDescription NVARCHAR(max),
	MovieLength INT,
	PicturePath NVARCHAR(100),
	Link NVARCHAR(200)
)
GO

CREATE TABLE Person (
	IDPerson INT PRIMARY KEY IDENTITY,
	FirstName NVARCHAR(100),
	LastName NVARCHAR(100)
)
GO

CREATE TABLE MovieGenre (
	IDMovieGenre INT PRIMARY KEY IDENTITY,
	MovieID INT FOREIGN KEY REFERENCES Movie(IDMovie),
	GenreID INT FOREIGN KEY REFERENCES Genre(IDGenre),
	UNIQUE(MovieID, GenreID)
)
GO

CREATE TABLE MovieActor (
	IDMovieActor INT PRIMARY KEY IDENTITY,
	MovieID INT,
	PersonID int,
	FOREIGN KEY (MovieID) REFERENCES Movie(IDMovie), 
    FOREIGN KEY (PersonID) REFERENCES Person(IDPerson),
	UNIQUE(MovieID, PersonID)
)
GO

CREATE TABLE MovieDirector (
	IDMovieDirector INT PRIMARY KEY IDENTITY,
	MovieID INT,
	PersonID int,
	FOREIGN KEY (MovieID) REFERENCES Movie(IDMovie), 
    FOREIGN KEY (PersonID) REFERENCES Person(IDPerson),
	UNIQUE(MovieID, PersonID)
)
GO

----------- PROCEDURE ----------------

-- GENRE
CREATE PROC createOrGetGenreID
	@GenreName NVARCHAR(50),
	@IDGenre INT OUTPUT
AS
BEGIN
	IF NOT EXISTS(SELECT * FROM Genre WHERE GenreName = @GenreName)
	BEGIN
		INSERT INTO Genre(GenreName) VALUES (@GenreName)
		SET @IDGenre = SCOPE_IDENTITY()
	END
	ELSE
	BEGIN
		SELECT @IDGenre = IDGenre FROM Genre WHERE GenreName = @GenreName
	END
END
GO

CREATE PROC createGenreForMovie
	@MovieID int,
	@GenreID int
AS
BEGIN
	INSERT INTO MovieGenre (MovieID, GenreID) VALUES (@MovieID, @GenreID)
END
GO

CREATE PROC selectAllGenres
AS
BEGIN
SELECT * FROM Genre
END
GO

CREATE PROC selectGenresForMovie
	@IDMovie int
AS
BEGIN
	SELECT * FROM MovieGenre as mg 
	INNER JOIN Genre as g
	ON g.IDGenre = mg.GenreID
	WHERE MovieID = @IDMovie
END
GO

CREATE PROC selectGenre
	@IDGenre int
AS
BEGIN
	SELECT * FROM Genre WHERE IDGenre = @IDGenre
END
GO

-- MOVIE
CREATE PROC createMovie
	@Title NVARCHAR(200),
	@PublishedDate NVARCHAR(100),
	@MovieDescription NVARCHAR(max),
	@MovieLength int,
	@PicturePath NVARCHAR(100),
	@Link NVARCHAR(200),
	@IDMovie int OUTPUT
AS
BEGIN
	INSERT INTO Movie (Title, PublishedDate, MovieDescription, MovieLength, PicturePath, Link) VALUES (@Title, @PublishedDate, @MovieDescription, @MovieLength, @PicturePath, @Link)
	SET @IDMovie = SCOPE_IDENTITY()
END
GO

CREATE PROC selectMovies
AS
BEGIN
SELECT * FROM Movie
END
GO

CREATE PROC selectMovie
	@IDMovie INT
AS
BEGIN
	SELECT * FROM Movie
	WHERE IDMovie = @IDMovie
END
GO

CREATE PROC deleteMovie
	@IDMovie INT
AS
BEGIN
	DELETE FROM MovieDirector WHERE MovieDirector.MovieID = @IDMovie
	DELETE FROM MovieActor WHERE MovieActor.MovieID = @IDMovie
	DELETE FROM MovieGenre WHERE MovieGenre.MovieID = @IDMovie
	DELETE FROM Movie WHERE IDMovie = @IDMovie
END
GO


CREATE PROCEDURE updateMovie
	@Title NVARCHAR(200),
	@PublishedDate NVARCHAR(100),
	@MovieDescription NVARCHAR(max),
	@MovieLength int,
	@PicturePath NVARCHAR(100),
	@Link NVARCHAR(200),
	
	@IDMovie INT
AS 
BEGIN 
	UPDATE Movie SET 
		Title = @Title,
		PublishedDate = @PublishedDate,
		MovieDescription = @MovieDescription,
		MovieLength = @MovieLength,
		PicturePath = @PicturePath,
		Link = @Link				
	WHERE 
		IDMovie = @IDMovie
END
GO

CREATE PROC deleteActorsGenresAndDirectorsForMovie 
	@IDMovie int
AS
BEGIN
	DELETE FROM MovieDirector WHERE MovieDirector.MovieID = @IDMovie
	DELETE FROM MovieActor WHERE MovieActor.MovieID = @IDMovie
	DELETE FROM MovieGenre WHERE MovieGenre.MovieID = @IDMovie
END
GO

-- PERSON/ACTOR/DIRECTOR
CREATE PROC selectPerson
	@IDPerson int
AS
BEGIN
	SELECT * FROM Person WHERE IDPerson = @IDPerson
END
GO

--CREATE PROC selectActor
--	@IDActor int
--AS
--BEGIN
--	SELECT * FROM Person WHERE IDPerson = @IDActor
--END
--GO

--CREATE PROC selectDirector
--	@IDDirector int
--AS
--BEGIN
--	SELECT * FROM Person WHERE IDPerson = @IDDirector
--END
--GO

CREATE PROC selectAllPersons
AS
BEGIN
SELECT * FROM Person
END
GO

CREATE PROC createActorForMovie
	@MovieID int,
	@ActorID int
AS
BEGIN
	INSERT INTO MovieActor (MovieID, PersonID) VALUES (@MovieID, @ActorID)
END
GO

CREATE PROC createDirectorForMovie
	@MovieID int,
	@DirectorID int
AS
BEGIN
	INSERT INTO MovieDirector(MovieID, PersonID) VALUES (@MovieID, @DirectorID)
END
GO


CREATE PROC selectActorsForMovie
	@IDMovie int
AS
BEGIN
	SELECT * FROM MovieActor as ma 
	INNER JOIN Person as p
	ON ma.PersonID = p.IDPerson
	WHERE MovieID = @IDMovie
END
GO

CREATE PROC selectDirectorsForMovie
	@IDMovie int
AS
BEGIN
	SELECT * FROM MovieDirector as md 
	INNER JOIN Person as p
	ON md.PersonID = p.IDPerson
	WHERE MovieID = @IDMovie
END
GO

CREATE PROC createOrGetPersonID
	@FirstName NVARCHAR(100),
	@LastName NVARCHAR(100),
	@IDActor INT OUTPUT
AS
BEGIN
	IF NOT EXISTS(SELECT * FROM Person WHERE FirstName = @FirstName AND LastName = @LastName)
	BEGIN
		INSERT INTO Person(FirstName,LastName) VALUES (@FirstName, @LastName)
		SET @IDActor = SCOPE_IDENTITY()
	END
	ELSE
	BEGIN
		SELECT @IDActor = IDPerson FROM Person WHERE FirstName = @FirstName AND LastName = @LastName
	END
END
GO

CREATE PROC createOrGetPersonIDBool
	@FirstName NVARCHAR(100),
	@LastName NVARCHAR(100),
	@IDPerson INT OUTPUT
AS
BEGIN
	IF NOT EXISTS(SELECT * FROM Person WHERE FirstName = @FirstName AND LastName = @LastName)
	BEGIN
		INSERT INTO Person(FirstName,LastName) VALUES (@FirstName, @LastName)
		SET @IDPerson = 1
	END
	ELSE
	BEGIN
		SET @IDPerson = 0
	END
END
GO

CREATE PROC updatePerson
	@FirstName NVARCHAR(100),
	@LastName NVARCHAR(100),
	@IDPerson int
AS
BEGIN
	UPDATE Person
	SET FirstName = @FirstName, LastName = @LastName
	WHERE IDPerson = @IDPerson
END
GO

CREATE PROC deletePerson
	@IDPerson int
AS
BEGIN
	DELETE FROM MovieActor WHERE PersonID = @IDPerson
	DELETE FROM MovieDirector WHERE PersonID = @IDPerson
	DELETE FROM Person WHERE IDPerson = @IDPerson
END
GO

--USER
CREATE PROC createUser
	@Email NVARCHAR(50),
	@PasswordHash NVARCHAR(50),
	@IsAdministrator int,
	@Rezultat int output
AS
BEGIN
	IF EXISTS (SELECT * FROM AppUser WHERE Email = @Email)
	BEGIN
		SET @Rezultat = 0
	END
	ELSE
	BEGIN
		INSERT INTO AppUser (Email, PasswordHash, IsAdministrator) VALUES (@Email, @PasswordHash, @IsAdministrator)
		SET @Rezultat = 1
	END
END
GO

CREATE PROC checkUser
	@Email NVARCHAR(50),
	@PasswordHash NVARCHAR(50)
AS
BEGIN
	IF((SELECT COUNT(*) FROM AppUser WHERE Email = @Email AND PasswordHash = @PasswordHash) IS NOT NULL)
	BEGIN
		SELECT * FROM AppUser WHERE Email = @Email AND PasswordHash = @PasswordHash
	END
END
GO

--GENERAL
CREATE PROC deleteEverything
AS 
BEGIN
 
DELETE FROM MovieActor
DELETE FROM MovieDirector
DELETE FROM Person
DELETE FROM MovieGenre
DELETE FROM Genre
DELETE FROM Movie
END
GO

DECLARE @rezultat int
EXEC createUser 'admin','admin',1,@rezultat
PRINT @rezultat
GO
