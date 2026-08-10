package ru.yandex.practicum.filmorate.dal.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.ReviewRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Primary
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;
    private final ReviewRowMapper reviewRowMapper;

    @Override
    public Review create(Review review) {
        String sql = """
                INSERT INTO reviews (content, is_positive, user_id, film_id, useful)
                VALUES (?, ?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setLong(3, review.getUserId());
            ps.setLong(4, review.getFilmId());
            ps.setInt(5, 0);
            return ps;
        }, keyHolder);

        review.setReviewId(keyHolder.getKey().longValue());
        review.setUseful(0);

        return review;
    }

    @Override
    public Review update(Review review) {
        String sql = """
                UPDATE reviews
                SET content = ?, is_positive = ?
                WHERE review_id = ?
                """;

        int rows = jdbcTemplate.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());

        if (rows == 0) {
            throw new NotFoundException("Отзыв не найден");
        }

        return findById(review.getReviewId()).orElseThrow();
    }

    @Override
    public void delete(Long id) {
        jdbcTemplate.update(
                "DELETE FROM review_likes WHERE review_id = ?",
                id
        );

        jdbcTemplate.update(
                "DELETE FROM reviews WHERE review_id = ?",
                id
        );
    }

    @Override
    public Optional<Review> findById(Long id) {
        String sql = "SELECT * FROM reviews WHERE review_id = ?";

        List<Review> reviews = jdbcTemplate.query(sql, reviewRowMapper, id);

        return reviews.stream().findFirst();
    }

    @Override
    public List<Review> findAll(Long filmId, Integer count) {

        if (filmId == null) {
            return jdbcTemplate.query(
                    """
                            SELECT *
                            FROM reviews
                            ORDER BY useful DESC
                            LIMIT ?
                            """,
                    reviewRowMapper,
                    count
            );
        }

        return jdbcTemplate.query(
                """
                        SELECT *
                        FROM reviews
                        WHERE film_id = ?
                        ORDER BY useful DESC
                        LIMIT ?
                        """,
                reviewRowMapper,
                filmId,
                count
        );
    }

    @Override
    public void addLike(Long reviewId, Long userId) {
        jdbcTemplate.update("DELETE FROM review_likes WHERE review_id = ? AND user_id = ?", reviewId, userId);

        jdbcTemplate.update(
                """
                        INSERT INTO review_likes(review_id,user_id,is_like)
                        VALUES (?,?,TRUE)
                        """,
                reviewId,
                userId
        );

        jdbcTemplate.update(
                "UPDATE reviews SET useful = useful + 1 WHERE review_id = ?",
                reviewId
        );
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {
        jdbcTemplate.update("DELETE FROM review_likes WHERE review_id = ? AND user_id = ?", reviewId, userId);

        jdbcTemplate.update(
                """
                        INSERT INTO review_likes(review_id,user_id,is_like)
                        VALUES (?,?,FALSE)
                        """,
                reviewId,
                userId
        );

        jdbcTemplate.update("""
                UPDATE reviews
                SET useful = (
                    SELECT COALESCE(SUM(CASE WHEN is_like = TRUE THEN 1 WHEN is_like = FALSE THEN -1 ELSE 0 END), 0)
                    FROM review_likes
                    WHERE review_id = ?
                )
                WHERE review_id = ?
                """, reviewId, reviewId
        );
    }

    @Override
    public void removeLike(Long reviewId, Long userId) {

        jdbcTemplate.update(
                """
                        DELETE FROM review_likes
                        WHERE review_id = ?
                        AND user_id = ?
                        """,
                reviewId,
                userId
        );

        jdbcTemplate.update(
                "UPDATE reviews SET useful = useful - 1 WHERE review_id = ?",
                reviewId
        );
    }

    @Override
    public void removeDislike(Long reviewId, Long userId) {

        jdbcTemplate.update(
                """
                        DELETE FROM review_likes
                        WHERE review_id = ?
                        AND user_id = ?
                        """,
                reviewId,
                userId
        );

        jdbcTemplate.update(
                "UPDATE reviews SET useful = useful + 1 WHERE review_id = ?",
                reviewId
        );
    }
}