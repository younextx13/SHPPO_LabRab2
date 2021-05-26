package com.configure.service;

import com.configure.entities.CharacteristicType;
import com.configure.entities.ComponentType;
import com.configure.entities.Parameters;
import com.configure.entities.PcComponent;
import com.configure.factory.FactoryMethodComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ComponentWriter {
    @Autowired
    FactoryMethodComponent factoryMethodComponent;

    Scanner scanner = new Scanner(System.in);

    public void startMenu(Map<ComponentType, LinkedList<PcComponent>> components) {// старт

        System.out.println("""
                 
                	-Ну и зачем ты мне!? Что ты можешь???
                	-Что я могу? Я могу собрать для тебя ПК из комплектующих, которые ты сам добавишь) 
                """);
        var ComponentTypeValues = ComponentType.values();// список типов компонентов
        int ordinal; //  порядковый номер
        String input; //  ввод
        do {
            System.out.println("Выберите комплектующую:");//  выводим текст
            try {
                for (ComponentType value : ComponentTypeValues) { //  выводим список
                    System.out.println((value.ordinal() + 1) + ") " + value.toString());
                }
                System.out.println("Введите 's', чтобы просмотреть текущий список.");
                System.out.println("Введите 'q', чтобы прекратить ввод и увидеть варианты сборки.");
                System.out.print("Ввод: ");
                input = scanner.nextLine();
                if (input.equals("s")) {
                    displayAllDevices(components);//  выводим все компоненты
                } else if (input.equals("q")) {
                    break; // выходим из меню
                } else {
                    ordinal = Integer.parseInt(input); //  парсим ввод
                    ComponentType type = ComponentTypeValues[ordinal - 1]; //  берём выбранный компонент
                    RequiredParameter requiredParams = getParams(type); //  получаем параметры для него
                    if (!components.containsKey(type)) { //  если список для компоненты пуст создаём пустой список
                        components.put(type, new LinkedList<>());
                    }
                    menuForTypeDevice(type, components.get(type), requiredParams); //  переходим к меню заполнения конфигурации
                }

            } catch (Exception e) {
                System.out.println("Ошибка в вводе, попробуйте ещё раз");
            }
        } while (true);

    }

    private void menuForTypeDevice(ComponentType type, LinkedList<PcComponent> list, RequiredParameter requireParams) {
        boolean exit = false; // ф
        String input; //  ввод
        do {
            System.out.println("\nСписок " + type + ": ");
            for (PcComponent cmp : list) { //  выводим компоненты из списка
                System.out.println(cmp);
            }

            System.out.println("\n\nВведите 'a', чтобы добавить в список новый " + type + ".");
            System.out.println("Введите 'b', чтобы вернуться к выбору компонентов.");
            System.out.println("Введите 'd', чтобы удалить последний " + type + ".");
            System.out.print("Ввод: ");

            try {
                input = scanner.nextLine();
                switch (input) {
                    case ("a") -> list.add(addNewComponent(type, requireParams)); //  заполняем новую компоненту и добавляем в список
                    case ("b") -> exit = true;// выходим из меню
                    case ("d") -> list.removeLast(); //  удаляем последнюю
                }
            } catch (Exception e) {
                System.out.println("Ошибка в вводе, попробуйте ещё раз");

            }
        } while (!exit);
    }

    private PcComponent addNewComponent(ComponentType type, RequiredParameter requireParams) {
        PcComponent.PcComponentBuilder builder = new PcComponent.PcComponentBuilder(type); // создаём новый объект

        System.out.print("\nВведите Название: ");
        builder.setName(scanner.nextLine());

        System.out.print("\nВведите Производителя: ");
        builder.setBrand(scanner.nextLine());

        for (Map.Entry<Parameters, Object[]> parametersEntry : requireParams.configurationRequired.entrySet()) {// проходимся по всем парам конфигураций
            System.out.println("Выберите " + parametersEntry.getKey() + "");
            Object[] objs = parametersEntry.getValue();
            for (int i = 0; i < objs.length; i++) {
                System.out.println((i + 1) + "=(" + objs[i].toString() + ") ,");// выводим конфигурации
            }
            System.out.print("Ввод:");

            boolean exit = false;
            int input;
            do {
                try {
                    input = Integer.parseInt(scanner.nextLine());// получаем число
                    builder.addConfigure(objs[input - 1].toString(), true, Arrays.asList(requireParams.primal).contains(parametersEntry.getKey())); // добавляем в конфигурацию как обязательный
                    exit = true;
                } catch (Exception e) {
                    System.out.println("Ошибка в вводе, попробуйте ещё раз");
                }
            } while (!exit);
        }
        if (requireParams.configurationOptional.size() > 0) {// тоже самое для необязательных параметров если они есть
            System.out.println("Выберите опциональные параметры:");
            List<Parameters> parameters = new ArrayList<>(requireParams.configurationOptional.keySet());
            String input;
            int index;
            do {
                for (int i = 0; i < parameters.size(); i++) {
                    System.out.println((i + 1) + ") " + parameters.get(i));
                }
                System.out.println(("s) Пропустить"));
                System.out.print("Ввод:");
                try {
                    input = (scanner.nextLine());
                    if (input.equals("s")) {
                        break;
                    }
                    index = Integer.parseInt(input)-1;
                    Parameters select = parameters.get(index);
                    Object[] objs = requireParams.configurationOptional.get(select);
                    for (int i = 0; i < objs.length; i++) {
                        System.out.println((i + 1) + "=(" + objs[i].toString() + ") ,");
                    }
                    System.out.print("Ввод:");
                    boolean exit = false;
                    int inputParameterValue = Integer.parseInt(scanner.nextLine());
                    builder.addConfigure(objs[inputParameterValue - 1].toString());

                    parameters.remove(index);
                } catch (Exception e) {
                    System.out.println("Ошибка в вводе, попробуйте ещё раз");
                }
            } while (parameters.size() != 0);

        }

        if(requireParams.characteristic.size() > 0) { //  и для характеристик
            System.out.println("Введите характеристики:");
            do {
                for (int i = 0; i < requireParams.characteristic.size(); i++) {
                    System.out.println((i + 1) + ") " + requireParams.characteristic.get(i));
                }
                System.out.println(("s) Пропустить"));
                System.out.print("Ввод:");
                String input;
                int index;
                try {
                    input = (scanner.nextLine());
                    if (input.equals("s")) {
                        break;
                    }
                    index = Integer.parseInt(input)-1;
                    CharacteristicType select = requireParams.characteristic.get(index);
                    System.out.print("Описание характеристики:");
                    input = (scanner.nextLine());

                    builder.addCharacteristic(select, input);
                    requireParams.characteristic.remove(index);
                } catch (Exception e) {
                    System.out.println("Ошибка в вводе, попробуйте ещё раз");
                }
            } while (requireParams.characteristic.size() != 0);
        }


        return builder.build();
    }

    private void displayAllDevices(Map<ComponentType, LinkedList<PcComponent>> components) {//  выводит все устройства
        System.out.println("\nТекущий список комплектующих: ");

        for (Map.Entry<ComponentType, LinkedList<PcComponent>> componentTypeLinkedListEntry : components.entrySet()) {

            System.out.println("\nСписок " + componentTypeLinkedListEntry.getKey() + ": ");
            for (PcComponent cmp : componentTypeLinkedListEntry.getValue()) {
                System.out.println(cmp);
            }
        }
    }


    public RequiredParameter getParams(ComponentType type) {//  возвращает параметры для генерации

        return switch (type) {
            case MOTHERBOARD -> getParamsMotherboard();
            case CPU -> getParamsCpu();
            case GPU -> getParamsGpu();
            case RAM -> getParamsRam();
            case PSU -> null;
            case HDD -> getParamsHdd();
            case SSD -> getParamsSsd();
        };
    }

    public RequiredParameter getParamsMotherboard() { //  для материнки
        RequiredParameter params = new RequiredParameter();
        params.primal = new Parameters[]{Parameters.cpuSocket,Parameters.pcie_v,Parameters.memoryType};
        params.configurationRequired.put(Parameters.cpuSocket, ComponentValidator.cpuSockets);
        params.configurationRequired.put(Parameters.pcie_v, ComponentValidator.gpuSockets);
        params.configurationRequired.put(Parameters.memoryType, ComponentValidator.ramSockets);
        params.configurationOptional.put(Parameters.sataVersion, ComponentValidator.sataVersion);
        params.characteristic.add(CharacteristicType.CHIPSET);
        params.characteristic.add(CharacteristicType.MEMORY_CHANNEL);
        params.characteristic.add(CharacteristicType.SATA_COUNT);
        params.characteristic.add(CharacteristicType.MEMORY_FREQUENCY);
        return params;
    }
    public RequiredParameter getParamsHdd() {
        RequiredParameter params = new RequiredParameter();
        params.primal = new Parameters[]{Parameters.sataVersion};
        params.configurationOptional.put(Parameters.sataVersion, ComponentValidator.sataVersion);
        return params;
    }
    public RequiredParameter getParamsSsd() {
        RequiredParameter params = new RequiredParameter();
        params.primal = new Parameters[]{Parameters.sataVersion};
        params.configurationOptional.put(Parameters.sataVersion, ComponentValidator.sataVersion);
        return params;
    }
    public RequiredParameter getParamsRam() {
        RequiredParameter params = new RequiredParameter();
        params.primal = new Parameters[]{Parameters.memoryType};
        params.configurationRequired.put(Parameters.memoryType, ComponentValidator.ramSockets);
        params.characteristic.add(CharacteristicType.MEMORY_FREQUENCY);
        return params;
    }
    public RequiredParameter getParamsGpu() {
        RequiredParameter params = new RequiredParameter();
        params.primal = new Parameters[]{Parameters.pcie_v};
        params.configurationRequired.put(Parameters.pcie_v, ComponentValidator.gpuSockets);
        params.characteristic.add(CharacteristicType.MEMORY_FREQUENCY);
        return params;
    }
    public RequiredParameter getParamsCpu() {
        RequiredParameter params = new RequiredParameter();
        params.primal = new Parameters[]{Parameters.cpuSocket};
        params.configurationRequired.put(Parameters.cpuSocket, ComponentValidator.cpuSockets);
        params.configurationRequired.put(Parameters.pcie_v, ComponentValidator.gpuSockets);
        params.configurationRequired.put(Parameters.memoryType, ComponentValidator.ramSockets);
        params.characteristic.add(CharacteristicType.CHIPSET);
        params.characteristic.add(CharacteristicType.MEMORY_CHANNEL);
        params.characteristic.add(CharacteristicType.MEMORY_FREQUENCY);
        return params;
    }


    private static class RequiredParameter { //  класс с параметрами
        public Parameters[] primal;
        public HashMap<Parameters, Object[]> configurationRequired = new HashMap<>();
        public HashMap<Parameters, Object[]> configurationOptional = new HashMap<>();
        public LinkedList<CharacteristicType> characteristic = new LinkedList<>();

    }

}
