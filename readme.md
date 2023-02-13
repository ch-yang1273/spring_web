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