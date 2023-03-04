package raster;

public interface Raster<E> {

    void clear();

    void setClearElement(E element);

    int getWidth();

    int getHeight();

    E getElement(int x, int y);

    void setElement(int x, int y, E element);

}
