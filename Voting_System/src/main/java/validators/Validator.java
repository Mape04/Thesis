package validators;

public interface Validator<T> {
    void validate(T object);
}
