package com.fastcampus.projectboard.repository;

import com.fastcampus.projectboard.config.JpaConfig;
import com.fastcampus.projectboard.domain.Article;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JPA 연결 테스트")
@Import(JpaConfig.class) // 해당 test 는 해당 config 을 모르기 때문에 import 한다.
@DataJpaTest
class JpaRepositoryTest {

    /*
        해당 repository 들을 @Autowired 해서 읽어와도 되지만,
        junit5와 최신 버전 스프링 부트를 이용하면 테스트에서도 생성자 주입 패턴이 가능하다.
        DataJpaTest 안에 들어가보면, @ExtendWith(SpringExtension.class) 안에 Autowired 로직이 들어가 있다.
        그것 덕분에 생성자 주입 패턴으로 field 를 만들 수 있다.
    */
    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;

    //  각 생성자 argument 들에게 @Autowired 를 사용할 수 있다.
    public JpaRepositoryTest(
            @Autowired ArticleRepository articleRepository
            , @Autowired ArticleCommentRepository articleCommentRepository) {
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
    }

    @DisplayName("select 테스트")
    @Test
    void givenTestData_whenSelecting_thenWorksFine(){
        //  Given

        //  When
        List<Article> articles = articleRepository.findAll();

        //  Then
        assertThat(articles).isNotNull().hasSize(123);
    }

    @DisplayName("insert 테스트")
    @Test
    void givenTestData_whenInserting_thenWorksFine(){
        //  Given
        long previousCount = articleRepository.count();

        //  When
        Article savedArticle = articleRepository.save(Article.of("new article", "new content", "#spring"));

        //  Then
        assertThat(articleRepository.count()).isEqualTo(previousCount + 1);
    }

    @DisplayName("update 테스트")
    @Test
    void givenTestData_whenUpdating_thenWorksFine(){
        //  Given

        // 아이의 1L는 무조건 있기에 설정한거고, 없으면 throw 던져서 테스트를 종료시키게 함
        Article article = articleRepository.findById(1L).orElseThrow();
        String updatedHashtag = "#springboot";
        article.setHashtag(updatedHashtag);

        //  When
        Article savedArticle = articleRepository.saveAndFlush(article);

        //  Then
        assertThat(savedArticle).hasFieldOrPropertyWithValue("hashtag", updatedHashtag);
    }

    @DisplayName("delete 테스트")
    @Test
    void givenTestData_whenDeleting_thenWorksFine(){
        //  Given
        Article article = articleRepository.findById(1L).orElseThrow();
        long previousArticleCount = articleRepository.count();
        long previousArticleCommentCount = articleCommentRepository.count();
        int deletedCommentsSize = article.getArticleComments().size();

        //  When
        articleRepository.delete(article);

        //  Then
        assertThat(articleRepository.count()).isEqualTo(previousArticleCount -1);
        assertThat(articleCommentRepository.count()).isEqualTo(previousArticleCommentCount - deletedCommentsSize);
    }

}
