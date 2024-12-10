package home.project.domain.common;

public enum RatingType {
    ONE(1f), TWO(2f), THREE(3f), FOUR(4f), FIVE(5f);

    private final float value;

    RatingType(float value) {
        this.value = value;
    }

}
