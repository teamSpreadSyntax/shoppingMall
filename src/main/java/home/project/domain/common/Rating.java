package home.project.domain.common;

public enum Rating {
    HALF(0.5f),
    ONE(1.0f),
    ONE_AND_HALF(1.5f),
    TWO(2.0f),
    TWO_AND_HALF(2.5f),
    THREE(3.0f),
    THREE_AND_HALF(3.5f),
    FOUR(4.0f),
    FOUR_AND_HALF(4.5f),
    FIVE(5.0f);

    private final float value;

    Rating(float value) {
        this.value = value;
    }

}
