package com.configure.service;

import com.configure.entities.Parameters;
import com.configure.entities.PcComponent;
import com.configure.factory.FactoryMethodComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;


@Service
public class ComponentReader {
    @Autowired
    FactoryMethodComponent factoryMethodComponent;
    @Autowired
    ComponentValidator componentValidator;

    public LinkedList<PcComponent> readCpu() throws FileNotFoundException { //
        LinkedList<PcComponent> cpus = new LinkedList<>(); // спсиок для проца
        try (Scanner sc = new Scanner(new File("src\\main\\resources\\cpu.txt"))) { //  читаем файл
            int counter = 1;
            while(sc.hasNext()){// строчку
                String[] paramCpu = sc.nextLine().split(", ");// разбираем по запятым
                Map<Parameters, String> params = new HashMap<>(); //  карту для параметров
                setDefaultParams(params, counter++, paramCpu); //  записываем в параметры значеиня
                params.put(Parameters.cpuSocket,     paramCpu[2]);
                params.put(Parameters.pcie_v,     paramCpu[3]);
                params.put(Parameters.memoryType, paramCpu[4]);
                PcComponent cmp = factoryMethodComponent.getComponent("CPU", params); //  Запрашиваем объект из фабрики
                validateComponent(cpus, cmp); //  проверяем валидацией
            }
            return cpus;// вернуть список
        } catch (FileNotFoundException e) {
            System.out.println("Неудалось прочесть БД процессоров");
            throw e;
        }
    }
    private void validateComponent(LinkedList<PcComponent> list, PcComponent cmp){
        ComponentValidator.ValidateResult errors = componentValidator.validate(cmp, false);// проверяем валидацию
        System.out.println("load: "+cmp.toString()); //  выводим текст
        if(errors.getErrors().length == 0){ // если нет ошибкок
            list.add(cmp);// добавляем в список
            if(errors.getWarning().length > 0){ //  выводим предупреждения
                System.out.println("WARNING:");
                for (ComponentValidator.ValidateWarning validateWarning : errors.getWarning()) {
                    System.out.println(validateWarning.getMessage());
                }
            }
        } else {
            System.out.println("SKIP ELEMENT, ERRORS:");
            for (ComponentValidator.ValidateError validateError : errors.getErrors()) {
                System.out.println(validateError.getMessage());
            }
        }
    }
    public LinkedList<PcComponent> readMotherboard() throws FileNotFoundException {
        LinkedList<PcComponent> moBos = new LinkedList<>();
        try (Scanner sc = new Scanner(new File("src\\main\\resources\\motherBoard.txt"))) {
            int counter = 1;
            while(sc.hasNext()){
                String[] paramMoBo = sc.nextLine().split(", ");
                Map<Parameters, String> params = new HashMap<>();
                setDefaultParams(params, counter++, paramMoBo);
                params.put(Parameters.cpuSocket,     paramMoBo[2]);
                params.put(Parameters.memoryType,     paramMoBo[3]);
                params.put(Parameters.pcie_v,     paramMoBo[4]);
                params.put(Parameters.sataVersion,     paramMoBo[5]);
                PcComponent cmp = (factoryMethodComponent.getComponent("MOTHERBOARD", params));
                validateComponent(moBos, cmp);
            }
            return moBos;
        } catch (FileNotFoundException e) {
            System.out.println("Неудалось прочесть БД материнский плат");
            throw e;
        }
    }
    public LinkedList<PcComponent> readGraphiccards() throws FileNotFoundException {
        LinkedList<PcComponent> grCas = new LinkedList<>();
        try (Scanner sc = new Scanner(new File("src\\main\\resources\\graphicCard.txt"))) {
            int counter = 1;
            while(sc.hasNext()){
                String[] stringParam = sc.nextLine().split(", ");
                Map<Parameters, String> params = new HashMap<>();
                setDefaultParams(params, counter++, stringParam);
                params.put(Parameters.pcie_v,     stringParam[2]);
                PcComponent cmp = (factoryMethodComponent.getComponent("GPU", params));
                validateComponent(grCas, cmp);
            }
            return grCas;
        } catch (FileNotFoundException e) {
            System.out.println("Неудалось прочесть БД графических карт");
            throw e;
        }
    }
    public LinkedList<PcComponent> readRam() throws FileNotFoundException {
        LinkedList<PcComponent> rams = new LinkedList<>();
        try (Scanner sc = new Scanner(new File("src\\main\\resources\\ram.txt"))) {
            int counter = 1;
            while(sc.hasNext()){
                String[] stringParam = sc.nextLine().split(", ");
                Map<Parameters, String> params = new HashMap<>();
                setDefaultParams(params, counter++, stringParam);
                params.put(Parameters.memoryFrequency,     stringParam[2]);
                params.put(Parameters.memoryType,     stringParam[3]);
                PcComponent cmp = (factoryMethodComponent.getComponent("RAM", params));
                validateComponent(rams, cmp);
            }
            return rams;
        } catch (FileNotFoundException e) {
            System.out.println("Неудалось прочесть БД оперативных карт");
            throw e;
        }
    }

    public LinkedList<PcComponent> readRom() throws FileNotFoundException {
        LinkedList<PcComponent> roms = new LinkedList<>();
        try (Scanner sc = new Scanner(new File("src\\main\\resources\\rom.txt"))) {
            int counter = 1;
            while(sc.hasNext()){
                String[] stringParam = sc.nextLine().split(", ");
                Map<Parameters, String> params = new HashMap<>();
                setDefaultParams(params, counter++, stringParam);
                params.put(Parameters.sataVersion,     stringParam[2]);
                PcComponent cmp = (factoryMethodComponent.getComponent("HDD", params));
                validateComponent(roms, cmp);
            }
        } catch (FileNotFoundException e) {
            System.out.println("Неудалось прочесть БД ЖД");
        }
        return roms;
    }

    private static void setDefaultParams(Map<Parameters, String> pMaps, Integer counter, String[] params) {
        pMaps.put(Parameters.id,         Integer.toString(counter));
        pMaps.put(Parameters.name,       params[0]);
        pMaps.put(Parameters.brand,      params[1]);
    }

}
