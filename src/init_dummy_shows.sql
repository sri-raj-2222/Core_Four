INSERT IGNORE INTO
    movies (
        MOVIE_ID,
        MOVIE_NAME,
        MOVIE_DURATION,
        MOVIE_GENRE,
        MOVIE_LANGUAGE
    )
VALUES (
        2,
        'The Grand Test',
        140,
        'Sci-Fi',
        'English'
    );

INSERT INTO
    shows (
        movie_ID,
        theater_id,
        show_time,
        price,
        approval_status
    )
VALUES (
        2,
        1,
        '2026-04-10 18:30:00',
        250.00,
        'PENDING'
    ),
    (
        2,
        1,
        '2026-04-10 21:00:00',
        300.00,
        'PENDING'
    );