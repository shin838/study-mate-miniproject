package com.example.studymate.studypost.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.studymate.studypost.entity.StudyPost;

public interface StudyPostRepository
        extends JpaRepository<StudyPost, Integer> {

    @Query(
            value = """
                    SELECT post
                    FROM StudyPost post
                    JOIN FETCH post.member
                    WHERE post.study.studyId = :studyId
                    ORDER BY post.createdAt DESC, post.postId DESC
                    """,
            countQuery = """
                    SELECT COUNT(post)
                    FROM StudyPost post
                    WHERE post.study.studyId = :studyId
                    """
    )
    Page<StudyPost> findAllByStudyIdWithMember(
            @Param("studyId") Integer studyId,
            Pageable pageable
    );

    Optional<StudyPost> findByPostIdAndStudy_StudyId(
            Integer postId,
            Integer studyId
    );
}