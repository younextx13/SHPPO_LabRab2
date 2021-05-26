package com.configure.entities;

import java.util.LinkedList;

public class Component { //  базовый класс для компоненты с полями
    protected String id;
    protected ComponentType type;
    protected String name;
    protected LinkedList<Configure> configures = new LinkedList<>();
    protected LinkedList<Characteristic> characteristics = new LinkedList<>();

    public ComponentType getType() {
        return type;
    }
}
