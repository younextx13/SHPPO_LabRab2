package com.configure.entities;

public class Characteristic { // класс с характеристиками, просто описывает дополнительные свойства компоненты
    public CharacteristicType type; //  тип характеристики
    public String value;//  значение
    public Characteristic(){

    }
    public Characteristic(Characteristic clone){
        this.type = clone.type;
        this.value= clone.value;
    }
}
