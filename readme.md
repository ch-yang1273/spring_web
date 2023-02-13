## 02. 스프링 부투에서 테스트 코드를 작성하자

### TDD와 단위 테스트(Unit Test)의 차이

- TDD : 테스트가 주도하는 개발. 테스트 코드를 먼저 작성하는 것부터 시작
- 단위 테스트 : TDD의 첫 번째 단계인 기능 단위의 테스트 코드를 작성하는 것

### 테스트 코드 작성으로 얻는 이점

- 자동 검증
- 기존 기능이 올바르게 작동하는지 확인 할 수 있음

### Hello Controller Test

```java
package com.jojoldu.book.study.web;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = HelloController.class)
class HelloControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void hello가_리턴된다() throws Exception {
        String hello = "hello";

        mvc.perform(MockMvcRequestBuilders.get("/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string(hello));
    }

    @Test
    public void helloDto가_리턴된다() throws Exception {
        String name = "hello";
        int amount = 1000;

        mvc.perform(
                        MockMvcRequestBuilders.get("/hello/dto")
                                .param("name", name)
                                .param("amount", String.valueOf(amount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", Matchers.is(name)))
                .andExpect(jsonPath("$.amount", Matchers.is(amount)));

    }
}
```

[[백기선] ****AssertJ가 JUnit의 assertThat 보다 편리한 이유****](https://www.notion.so/AssertJ-JUnit-assertThat-ef63aa31b07a49a8a12b518a7e568467)

- @Runwith

  책의 내용과 다르게 JUnit4가 아닌 JUnit5를 사용하기 위해

  @RunWith(SpringRunner.class)을 @ExtendWith(SpringExtension.class)로 변경

- jsonPath

  JSON 응답값을 필드별로 검증할 수 있는 메소드로 “$”를 기준으로 필드명을 명시한다.
## 03. JPA로 데이터베이스 다뤄보자

h2 database 대신 MySQL으로 변경해서 진행함

[Windows Docker MySQL 사용](https://www.notion.so/Windows-Docker-MySQL-01bd104d256f4716892e131c45c30417)

### 소개

```
JPA는 서로 지향하는 바가 다른 2개 영역(객체지향 프로그래밍 언어와 관계형 데이터베이스)을 **중간에서 패러다임 일치**를 시켜주기 위한 기술입니다.

즉, 개발자는 객체지향적으로 프로그래밍을 하고, JPA가 이를 관계형 데이터베이스에 맞게 SQL을 대신 생성해서 실행합니다.
```

- JPA는 인터페이스로서 자바 표준 명세서이고, 사용하기 위해서는 구현체가 필요하다. 대표적인 구현체로 Hibernate, Eclipse Link가 있다.
- 구현체들을 좀 더 쉽게 사용하고자 추상화시킨 **Spring Data JPA**라는 모듈을 이용하여 JPA 기술을 다룬다.
- JPA < Hibernate < Spring Data JPA

### Spring Data JPA의 이점

1. 구현체 교체의 용이성
   - 언젠가 Hibernate의 수명이 다해 새로운 JPA 구현체를 사용해야 할 때 Spring Data JPA를 사용 중이라면 쉽게 교체가 가능하다.
2. 저장소 교체의 용이성
   - 관계형 데이터베이스 외에 다른 저장소로 쉽게 교체할 수 있다.
   - 점점 트래픽이 많아져 RDB로 감당이 안되어 Mongo DB로 교체가 필요할 때, Spring Data JPA에서 Spring Data MongoDB로 의존성만 교체하면 된다.

Spring Data의 하위 프로젝트들은 save(), findAll(), findOne() 등을 인터페이스로 갖고, 저장소가 교체되어도 기본적인 기능은 변경할 것이 없을 것이다.

### Domain Entity 생성

```powershell
@Getter
@NoArgsConstructor
@Entity
public class Posts {
    //~
}
```

- 필자는 주요 어노테이션을 클래스에 가깝게 두는 편이다. Lombok 어노테이션을 필수 어노테이션이 아니여서 위쪽에 두었다.
- Entity 클래스에서는 절대 Setter 메서드를 만들지 않는다.
- JpaRepository 생성 후 테스트

    ```powershell
    //table 생성
    Hibernate: 
        
        create table posts (
           id bigint not null auto_increment,
            author varchar(255),
            content TEXT not null,
            title varchar(500) not null,
            primary key (id)
        ) engine=InnoDB
    
    //데이터 삽입
    Hibernate: 
        insert 
        into
            posts
            (author, content, title) 
        values
            (?, ?, ?)
    
    //데이터 조회
    Hibernate: 
        select
            posts0_.id as id1_0_,
            posts0_.author as author2_0_,
            posts0_.content as content3_0_,
            posts0_.title as title4_0_ 
        from
            posts posts0_
    ```


### 등록/수정/조회 API 만들기

[비즈니스 로직 처리의 주체 (도메인 모델)](https://www.notion.so/c110ec9b53d24d20a8d803e4e1547f06)

- 이 책에서는 도메인 모델을 다룬다.

[JPA Auditing](https://www.notion.so/JPA-Auditing-6d5385827f64412abd49dc91233b03f5)