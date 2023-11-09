package com.fastcampus.projectboard.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString
@Table(indexes = {
        @Index(columnList = "title")
        , @Index(columnList = "hashtag")
        , @Index(columnList = "createdAt")
        , @Index(columnList = "createdBy")
})
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Article {

    /*
    * PK를 설정하기 위해 @Id 어노테이션 사용
    * PK의 auto increment 를 위한 @GeneratedValue 어노테이션
    * strategy 의 기본 값은 GenerationType.AUTO 이지만 MySql 에서는 IDENTITY 로 설정해줘야 된다.
    * */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
    * @Setter 를 전체 클래스로 설정하지 않은 이유는 PK는 자동으로 부여하는 고유번호여야 하기에
    * 각 변수마다 setter 를 설정해서 set 이 되는 변수만 독립적으로 둔다.
    * */
    @Setter @Column(nullable = false) private String title; // 제목
    @Setter @Column(nullable = false, length = 10000) private String content; // 본문

    //  엔티티 클래스에 있는 모든 필드는 따로 @Transient 가 언급되지 않는 이상 모두 @Column(nullable = true)로 인지한다.
    @Setter private String hashtag; // 해시태그

    /* mappedBy를 하지 않으면 default 이름을 사용하는데 두 엔티티 이름을 합친다.
    * 그것을 방지하기 위해서 mappedBy로 명시한다.
    * 해당 강좌에서는 cascade 를 설정하지만,
    * 실제 업무에서 운영에서 게시글을 삭제하더라도 게시글의 댓글 정보는 백업해두고 싶을수도 있기에
    * 양방향 바인딩을 일부로 풀고 디자인 하는 경우도 많다.
    *
    * circular referencing 이슈 :
    * Article 은 Set<ArticleComment> 를 참조하고, ArticleComment 는 다시 Article 을 참조하고
    * 이것을 계속 반복하는 것을 순환 참조라고 한다. 이 때, 계속 클래스에 선언한 @ToString 이 반복되기에 OOM 이 발생할 수 있다.
    * 이것을 방지하기 위해서 @ToString.Exclude 를 사용하여 ToString 을 수동적으로 끊는다.
    * 댓글로부터 글을 참조하는 경우는 정사적인 경우이지만, 게시글에서 댓글 리스트를 다 뽑아보는 것은 굳이 하지 않아도 되기 때문이다.
    * */
    @ToString.Exclude
    @OrderBy("id")
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();


    //  JpaAuditing 이 해당 어노테이션들을 제공해준다.
    @CreatedDate @Column(nullable = false) private LocalDateTime createdAt; // 생성일시
    @CreatedBy @Column(nullable = false, length = 100) private String createdBy; // 생성자
    @LastModifiedDate @Column(nullable = false) private LocalDateTime modifiedAt; // 수정일시
    @LastModifiedBy @Column(nullable = false, length = 100) private String modifiedBy; // 수정자

    //  모든 JPA Entity 들은 Hibernate 구현체를 사용하는 경우를 기준으로 기본 생성자를 가지고 있어야 한다.
    //  평소에는 오픈하지 않는 것이기에 protected 사용
    protected Article() {}

    //  private 으로 생성자 오픈을 막고 factory method 패턴으로 해당 생성자를 우회해서 열 수 있게 디자인한다
    private Article(String title, String content, String hashtag) {
        this.title = title;
        this.content = content;
        this.hashtag = hashtag;
    }
    //  도메인 Article 를 생성하고자 할 때 어떤 값이 필요로 하는지 가이드 해주는 코드
    public static Article of(String title, String content, String hashtag) {
        return new Article(title, content, hashtag);
    }

    /*
    * 만약에 Article 를 리스트나 컬렉션에서 중복 요소를 제거하거나, 혹은 정렬을 해야 할 때
    * 동일성, 동등성 검사를 할 수 있어야 한다. 이 때 lombok 의 @EqualsAndHashCode 를 선언하면 간단하게 할 수 있다.
    * 한편, @EqualsAndHashCode 를 쓰면 Entity 의 모든 필드를 비교해서 표준 방식으로 equals, hashcode 를 구현하게 된다.
    * 제목, 타이틀, 작성자, 해시태그가 모두 동일한 게시글이 있을 수 있기에 우리는 PK 값인 id만 제대로 확인해주면 된다.
    * 따라서 해당 Entity 의 모든 필드가 아닌, id 만을 위한 equals, hashCode 를 작성해주면 퍼포먼스를 향상시킬 수 있다.
    * */

    /*
    not null 를 체크해주지 않으면 return 할 때, Object 를 이용해서 검사를 한다.
    해당 Objects.equals(...)는 null 인 경우에도 대비가 되어 있어 NPE(NullPointerException)가 발생하지 않겠금 방어해주고 있다.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Article article)) return false;
        return Objects.equals(id, article.id);
    }*/

    //  id가 not null 이기 때문에 바로 return id.equals()를 한다.
    //  id != null 이 의미하는 바는 아직 영속화 되지 않는 엔티티는 모두 동등성 검사에서 탈락시키겠다는 뜻이다.
    //  영속화라는게 말이 어렵지, 데이터베이스와 아직 연동시키지 않은 상태에서
    //  작성된 row 는 id가 당연히 없을테니 동등성 검사에서 아예 탈락시키겠다는 의미이다.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Article article)) return false; // Java 14에서 처음 소개된 pattern matching 기법이다.
        return id != null && id.equals(article.id); // id가 null 일 수 있기에 체크해준다.
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
