package com.aluracursos.literalura.model;

public class EstadoBusqueda {

    private boolean busquedaEnCurso;
    private int cantidadResultados;
    private int cantidadResultadosParcial;
    private String tipoBusqueda;

    public EstadoBusqueda(boolean busquedaEnCurso, int cantidadResultados, int cantidadResultadosParcial, String tipoBusqueda) {
        this.busquedaEnCurso = busquedaEnCurso;
        this.cantidadResultados = cantidadResultados;
        this.cantidadResultadosParcial = cantidadResultadosParcial;
        this.tipoBusqueda = tipoBusqueda;
    }

    public boolean isBusquedaEnCurso() {
        return busquedaEnCurso;
    }

    public void setBusquedaEnCurso(boolean busquedaEnCurso) {
        this.busquedaEnCurso = busquedaEnCurso;
    }

    public int getCantidadResultados() {
        return cantidadResultados;
    }

    public void setCantidadResultados(int cantidadResultados) {
        this.cantidadResultados = cantidadResultados;
    }

    public int getCantidadResultadosParcial() {
        return cantidadResultadosParcial;
    }

    public void setCantidadResultadosParcial(int cantidadResultadosParcial) {
        this.cantidadResultadosParcial = cantidadResultadosParcial;
    }

    public String getTipoBusqueda() {
        return tipoBusqueda;
    }

    public void setTipoBusqueda(String tipoBusqueda) {
        this.tipoBusqueda = tipoBusqueda;
    }

}
