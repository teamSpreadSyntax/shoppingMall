package home.project.service.util;

import home.project.service.util.PageUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;

class PageUtilTest {

    private final PageUtil pageUtil = new PageUtil();

    @Test
    @DisplayName("페이지 번호를 1 감소시킨다")
    void decreasePageNumberByOne() {
        // given
        Pageable originalPage = PageRequest.of(1, 10); // page 1, size 10

        // when
        Pageable result = pageUtil.pageable(originalPage);

        // then
        assertThat(result.getPageNumber()).isEqualTo(0);
        assertThat(result.getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("페이지 사이즈는 유지된다")
    void maintainPageSize() {
        // given
        Pageable originalPage = PageRequest.of(2, 20); // page 2, size 20

        // when
        Pageable result = pageUtil.pageable(originalPage);

        // then
        assertThat(result.getPageNumber()).isEqualTo(1);
        assertThat(result.getPageSize()).isEqualTo(20);
    }

}