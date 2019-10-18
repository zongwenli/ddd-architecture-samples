package study.huhao.demo.adapters.restapi.resources.publishedblog;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import study.huhao.demo.adapters.restapi.resources.ResourceTest;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static study.huhao.demo.adapters.restapi.resources.BasePath.PUBLISHED_BLOG_BASE_PATH;
import static study.huhao.demo.adapters.restapi.resources.BaseRequestSpecification.createBlog;
import static study.huhao.demo.adapters.restapi.resources.BaseRequestSpecification.publishBlog;
import static study.huhao.demo.adapters.restapi.resources.BaseResponseSpecification.*;

@DisplayName(PUBLISHED_BLOG_BASE_PATH)
class PublishedBlogResourceTest extends ResourceTest {

    @Nested
    @DisplayName("POST /published-blog")
    class post {

        @Test
        void should_publish_blog() {
            var authorId = UUID.randomUUID();

            var createdBlogId = createBlog("Test Blog", "Something...", authorId)
                    .jsonPath()
                    .getUUID("id");

            publishBlog(createdBlogId)
                    .then()
                    .spec(CREATED_SPEC)
                    .header("Location", containsString(PUBLISHED_BLOG_BASE_PATH + "/" + createdBlogId))
                    .body("id", is(createdBlogId.toString()))
                    .body("title", is("Test Blog"))
                    .body("body", is("Something..."))
                    .body("authorId", is(authorId.toString()))
                    .body("publishedAt", notNullValue());
        }

        @Test
        void should_return_404_when_blog_not_found() {
            var blogId = UUID.randomUUID();
            publishBlog(blogId)
                    .then()
                    .spec(NOT_FOUND_SPEC)
                    .body("message", is("cannot find the blog with id " + blogId));
        }

        @Test
        void should_return_409_when_no_need_to_publish() {
            var authorId = UUID.randomUUID();
            var createdBlogId = createBlog("Test Blog", "Something...", authorId)
                    .jsonPath()
                    .getUUID("id");

            publishBlog(createdBlogId);

            publishBlog(createdBlogId)
                    .then()
                    .spec(CONFLICT_SPEC)
                    .body("message", is("no need to publish"));
        }
    }

}
