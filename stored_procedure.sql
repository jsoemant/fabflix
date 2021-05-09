use moviedb;

DROP PROCEDURE IF EXISTS add_movie;

DELIMITER $$

CREATE PROCEDURE add_movie(
	IN title VARCHAR(100),
    IN year INTEGER,
	IN director VARCHAR(100),
    IN star VARCHAR(100),
    IN birthYear INTEGER,
	IN genre VARCHAR(32),
    OUT result VARCHAR(1000)
)
BEGIN 
	declare old_movie_id VARCHAR(10);
    declare tmp_movie_id INT;
    declare new_movie_id VARCHAR(10);
    
    declare old_star_id VARCHAR(10);
    declare tmp_star_id INT;
    declare new_star_id VARCHAR(10);
    
    declare genre_id INT;
    
    declare movie_count INT;
    declare genre_count INT;
    declare star_count INT;

    set movie_count = (SELECT COUNT(*) FROM movies M WHERE M.title = title AND M.year = year and M.director = director);
    set genre_count = (SELECT COUNT(*) FROM genres G WHERE G.name = genre);
    set star_count = (SELECT COUNT(*) FROM stars S WHERE S.name = star LIMIT 1);
    
	IF (movie_count = 0) THEN
		-- MOVIE
		set old_movie_id = (SELECT max(id) from movies);
		set tmp_movie_id = (CONVERT(SUBSTRING(old_movie_id, 3), UNSIGNED));
		set new_movie_id = CONCAT('tt', tmp_movie_id + 1);
        
        INSERT INTO movies VALUES(new_movie_id, title, year, director);
        SET result = 'movie added with id ';
        SET result = CONCAT(result, new_movie_id);
        
        -- GENRE
        IF (genre_count = 0) THEN
			INSERT INTO genres VALUES(null, genre);
			set genre_id = (SELECT max(id) from genres) + 1;
			SET result = CONCAT(result, ', genre added with id ');
            SET result = CONCAT(result, genre_id);
        ELSE
			set genre_id = (SELECT id from genres G WHERE G.name = genre); 
            SET result = CONCAT(result, ', genre found with id ');
            SET result = CONCAT(result, genre_id);
        END IF;
        
        INSERT INTO genres_in_movies VALUES(genre_id, new_movie_id);
		SET result = CONCAT(result, ', genre-movie relation added');
        
        -- STAR
        IF (star_count = 0) THEN
			set old_star_id = (SELECT max(id) from stars);
			set tmp_star_id = (CONVERT(SUBSTRING(old_star_id, 3), UNSIGNED));
			set new_star_id = CONCAT('nm', tmp_star_id + 1);
			INSERT INTO stars VALUES(new_star_id, star, birthYear);
            SET result = CONCAT(result, ', star added with id ');
            SET result = CONCAT(result, new_star_id);
        ELSE
			set new_star_id = (SELECT id from stars S WHERE S.name = star LIMIT 1);
			SET result = CONCAT(result, ', star found with id ');
            SET result = CONCAT(result, new_star_id);
        END IF;
        
		INSERT INTO stars_in_movies VALUES(new_star_id, new_movie_id);
		SET result = CONCAT(result, ', star-movie relation added');
        
    ELSE
		set old_movie_id = (SELECT M.id FROM movies M WHERE M.title = title AND M.year = year and M.director = director);
		SET result = 'movie already exists with id ';
        SET result = CONCAT(result, old_movie_id);
    END IF;
END 
$$

DELIMITER ;
