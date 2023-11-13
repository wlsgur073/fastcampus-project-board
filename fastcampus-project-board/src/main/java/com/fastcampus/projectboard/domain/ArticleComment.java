package com.fastcampus.projectboard.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;


@Getter
@ToString
@Table(indexes = {
        @Index(columnList = "content")
        , @Index(columnList = "createdAt")
        , @Index(columnList = "createdBy")
})
//@EntityListeners(AuditingEntityListener.class)
@Entity
public class ArticleComment extends AuditiongFields{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
    * Article과 연관관계를 주기 위해서 @ManyToOne를 설정한다. (N개의 댓글이 1개의 게시글을 바라보기 때문에)
    * 연관관계 매핑이란 객체의 참조와 테이블의 외래 키를 매핑하는 것을 의미한다.
    * JPA에서는 연관 관계에 있는 상대 테이블의 PK를 멤버 변수로 갖지 않고, 엔티티 객체 자체를 통째로 참조한다.
    * cascade 기본 값은 none이다. cascade 옵션은 댓글을 수정/삭제했을 때, 관련있는 게시글에 영향을 줄 수 있는 옵션이다.
    * */
    @Setter @ManyToOne(optional = false) private Article article; // 게시글 (ID)
    @Setter @Column(nullable = false, length = 500) private String content; // 본문

//    @CreatedDate @Column(nullable = false) private LocalDateTime createdAt; // 생성일시
//    @CreatedBy @Column(nullable = false, length = 100) private String createdBy; // 생성자
//    @LastModifiedDate @Column(nullable = false) private LocalDateTime modifiedAt; // 수정일시
//    @LastModifiedBy @Column(nullable = false, length = 100) private String modifiedBy; // 수정자

    /*
    * article 과 articleComment 에서 id 및 생성일자, 생성자, 수정일자, 수정자의 컬럼이
    * 중복되기에 이것을 한 클래스로 묶을수도 있다.
    * @MappedSuperclass 라는 에노테이션이나, @Embedded 라는 방식으로 접근이 가능하다.
    * */
    /*
    첫 번째 방법 : @Embedded 는 해당 컬럼들을 묶는 클래스를 만들어서 거기에 추가하는 방법이다.
    @Embedded AAA aa;
    class AAA{중복되는 컬럼 등록}
    */


    // lombok 으로도 class 위에 @NoArgsConstructor(access = AccessLevel.PROTECTED) 설정하여 같은 동작을 수행할수도 있다.
    protected ArticleComment() {}

    private ArticleComment(Article article, String content) {
        this.article = article;
        this.content = content;
    }
    public static ArticleComment of (Article article, String content) {
        return new ArticleComment(article, content);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArticleComment that)) return false;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
