package com.configure.entities;

public class Configure { // класс описывающий конфигурацию компоненты для подключения
    public Boolean require; // обязательный компонент
    public SocketType type; // тип компонента
    public Boolean input; // первичное свойство для подключения
    public PcComponent connect; // подключённый компонент по указанному сокету
    public Configure(Configure copy){
        this.require = copy.require;
        this.type = copy.type;
        this.input = copy.input;
        this.connect = copy.connect;
    }
    public Configure(){

    }
}
