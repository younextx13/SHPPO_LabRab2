package com.configure.factory;

import com.configure.entities.CharacteristicType;
import com.configure.entities.ComponentType;
import com.configure.entities.Parameters;
import com.configure.entities.PcComponent;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class FactoryMethodComponent { // фабрика

    public PcComponent getComponent(String componentType, Map<Parameters, String> params) { // метод для генерации объекта
        ComponentType type = ComponentType.valueOf(componentType); // получить тип
        return switch (type){ // по типу сгенерить объект
            case MOTHERBOARD -> getMotherboard(params);
            case CPU -> getCpu(params);
            case GPU -> getGpu(params);
            case RAM -> getRam(params);
            case PSU -> getPsu(params);
            case HDD, SSD -> getPeriphery(params);
        };
    }

    static public PcComponent getCpu(Map<Parameters, String> params) { // вернёт компоненту процессора
        PcComponent.PcComponentBuilder builder = new PcComponent.PcComponentBuilder(ComponentType.CPU);
        setDefaultParameter(builder, params);

        if(params.containsKey(Parameters.cpuSocket)){ // если есть сокет
            builder.addConfigure(params.get(Parameters.cpuSocket), true, true); // добавить в конфиг
        }

        if(params.containsKey(Parameters.pcie_v)){ // если есть pci_v версия
            builder.addConfigure(params.get(Parameters.pcie_v), true); // добавить в конфиг
        }

        if(params.containsKey(Parameters.memoryType)){
            builder.addConfigure(params.get(Parameters.memoryType), true);
        }

        return builder.build();
    }

    static public PcComponent getMotherboard(Map<Parameters, String> params) {
        PcComponent.PcComponentBuilder builder = new PcComponent.PcComponentBuilder(ComponentType.MOTHERBOARD);

        if(params.containsKey(Parameters.cpuSocket)){
            builder.addConfigure(params.get(Parameters.cpuSocket), true, true);
        }

        if(params.containsKey(Parameters.memoryType)){
            builder.addConfigure(params.get(Parameters.memoryType), true, true);
        }

        if(params.containsKey(Parameters.pcie_v)){
            builder.addConfigure(params.get(Parameters.pcie_v), true, true);
        }

        if(params.containsKey(Parameters.sataVersion)){
            builder.addConfigure(params.get(Parameters.sataVersion), false);
        }

        setDefaultParameter(builder, params);
        return builder.build();
    }

    static public PcComponent getGpu(Map<Parameters, String> params) {
        PcComponent.PcComponentBuilder builder = new PcComponent.PcComponentBuilder(ComponentType.GPU);

        if(params.containsKey(Parameters.pcie_v)){
            builder.addConfigure(params.get(Parameters.pcie_v), true, true);
        }

        setDefaultParameter(builder, params);
        return builder.build();
    }

    static public PcComponent getRam(Map<Parameters, String> params) {
        PcComponent.PcComponentBuilder builder = new PcComponent.PcComponentBuilder(ComponentType.RAM);

        if(params.containsKey(Parameters.memoryFrequency)){
            builder.addCharacteristic(CharacteristicType.MEMORY_FREQUENCY, params.get(Parameters.memoryFrequency));
        }

        if(params.containsKey(Parameters.memoryType)){
            builder.addConfigure(params.get(Parameters.memoryType), true, true);
        }

        setDefaultParameter(builder, params);
        return builder.build();
    }

    static public PcComponent getPsu(Map<Parameters, String> params) {
        PcComponent.PcComponentBuilder builder = new PcComponent.PcComponentBuilder(ComponentType.PSU);
        setDefaultParameter(builder, params);
        return builder.build();
    }

    static public PcComponent getPeriphery(Map<Parameters, String> params) {
        PcComponent.PcComponentBuilder builder = new PcComponent.PcComponentBuilder(ComponentType.HDD);
        if(params.containsKey(Parameters.sataVersion)){
            builder.addConfigure(params.get(Parameters.sataVersion), true, true);
        }

        setDefaultParameter(builder, params);
        return builder.build();
    }

    static private void setDefaultParameter(PcComponent.PcComponentBuilder builder, Map<Parameters, String> params){
        if(params.containsKey(Parameters.id)){
            builder.setId(params.get(Parameters.id));
        }
        if(params.containsKey(Parameters.name)){
            builder.setName(params.get(Parameters.name));
        }
        if(params.containsKey(Parameters.brand)){
            builder.setBrand(params.get(Parameters.brand));
        }

    }
}
