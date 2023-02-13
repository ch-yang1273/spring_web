package com.jojoldu.book.study.domain.posts;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

//@RunWith(SpringRunner.class)
@SpringBootTest
class PostsRepositoryTest {

    @Autowired
    PostsRepository postsRepository;

    //@After
    @AfterEach
    public void cleanup() {
        postsRepository.deleteAllInBatch();
    }

    @Test
    public void 게시글저장_불러오기() {
        //given
        String title = "테스트 게시글";
        String content = "테스트 본문";

        Posts posts = Posts.builder()
                .title(title)
                .content(content)
                .author("ych1273@gmail.com")
                .build();
        postsRepository.save(posts);

        //when
        List<Posts> postsList = postsRepository.findAll();

        //then
        Posts one = postsList.get(0);
        assertThat(one.getTitle()).isEqualTo(title);
        assertThat(one.getContent()).isEqualTo(content);
    }

    @Test
    public void BaseTimeEntity_등록() {
        //given
        LocalDateTime now = LocalDateTime.of(2019, 6, 4, 0, 0, 0);

        String title = "테스트 게시글";
        String content = "테스트 본문";

        Posts posts = Posts.builder()
                .title(title)
                .content(content)
                .author("ych1273@gmail.com")
                .build();

        postsRepository.save(posts);

        //when
        List<Posts> postsList = postsRepository.findAll();

        //then
        Posts one = postsList.get(0);
        System.out.println(">>> createDate=" + one.getCreatedDate() + ", modifiedDate=" + one.getModifiedDate());

        assertThat(one.getCreatedDate()).isAfter(now);
        assertThat(one.getModifiedDate()).isAfter(now);
    }
}