package com.configure.service;

import com.configure.entities.ComponentType;
import com.configure.entities.Configure;
import com.configure.entities.PcComponent;
import com.configure.entities.SocketType;
import org.springframework.stereotype.Service;

import java.util.LinkedList;


@Service
public class ComponentValidator {
    static final public SocketType[] cpuSockets= {SocketType.INTEL_H470,SocketType.LGA_771, SocketType.LGA_775, SocketType.LGA_1567, SocketType.LGA_1366, SocketType.LGA_1156, SocketType.LGA_1155, SocketType.LGA_2011, SocketType.LGA_1356, SocketType.LGA_1150, SocketType.LGA_2011_3, SocketType.LGA_1151, SocketType.LGA_2066, SocketType.LGA_1200, SocketType.LGA_1700, SocketType.AMD_AM2, SocketType.AMD_AM2P, SocketType.AMD_AM3, SocketType.AMD_AM3P, SocketType.AMD_FM1, SocketType.AMD_FM2, SocketType.AMD_FM2P, SocketType.AMD_AM1, SocketType.AMD_AM4, SocketType.AMD_TR4, SocketType.AMD_TRX4,};
    static final public SocketType[] sataVersion= {SocketType.SATA3, SocketType.SATA2, SocketType.SATA};
    static final public SocketType[] gpuSockets= {SocketType.PCI_E_V1, SocketType.PCI_E_V2, SocketType.PCI_E_V3};
    static final public SocketType[] ramSockets= {SocketType.DDR, SocketType.DDR2, SocketType.DDR3, SocketType.DDR4};;

    public ValidateResult validate(PcComponent component, Boolean checkConnect){ // функция валидации
        return switch (component.getType()){
            case MOTHERBOARD -> getMotherboard(component, checkConnect);
            case CPU -> getCpu(component, checkConnect);
            case GPU -> getGpu(component, checkConnect);
            case RAM -> getRam(component, checkConnect);
            case PSU -> getPsu(component, checkConnect);
            case HDD, SSD -> getPeriphery(component, checkConnect);
        };
    }

    private ValidateResult getPeriphery(PcComponent component, Boolean checkConnect) {
        return switch (component.getType()) {
            case HDD -> validateHdd(component,checkConnect);
            case SSD -> validateSsd(component,checkConnect);
            default -> new ValidateResult(new ValidateError[0], new ValidateWarning[0]);
        };
    }

    private ValidateResult validateSsd(PcComponent component, Boolean checkConnect) {
        return new ValidateResult(new ValidateError[0], new ValidateWarning[0]);
    }

    private ValidateResult validateHdd(PcComponent component, Boolean checkConnect) { //  валидация для жёского диска
        LinkedList<ValidateError> errors = new LinkedList<>();
        LinkedList<ValidateWarning> warns = new LinkedList<>();
        Configure cfg = getSataConfig(component);
        if(cfg == null){ // если у жёского диска нету сата подключения это ошибка
            errors.add(new ValidateError("Интерфейс для HDD не сконфигурирован"));
            return new ValidateResult(errors.toArray(new ValidateError[0]), new ValidateWarning[0]);
        }

        if(checkConnect && cfg.require && cfg.connect == null){ //  если требуется проверка подключения конфиг обязательный и нет подключения это ошибка
            errors.add(new ValidateError("HDD не к чему не подключён"));
        }

        if(checkConnect && cfg.connect != null) { //  проверка подключений
            if(cfg.connect.getType() == component.getType()){ //  2 хдд между собой плохая идея подключать
                errors.add(new ValidateError("Нельзя подключать одинаковые компоненты"));
            }
            Configure connectCfg = getSataConfig(cfg.connect);// взять сата подключённое устройство
            if(connectCfg == null) {
                errors.add(new ValidateError("У подключённого оборудования нету SATA входа"));
            }else if (connectCfg.type != cfg.type){ //  если SATA1 подключено к SATA2
                warns.add(new ValidateWarning("Подключение осуществленно не оптимально."));
            }
        }

        return new ValidateResult(errors.toArray(new ValidateError[0]), warns.toArray(new ValidateWarning[0]));
    }

    private Configure getSataConfig(PcComponent component){
        return component.getAnyConfig(new SocketType[]{SocketType.SATA3, SocketType.SATA2, SocketType.SATA});
    }

    private Configure getCpuSocketConfig(PcComponent component) {
        return component.getAnyConfig(cpuSockets);
    }

    private Configure getGpuSocketConfig(PcComponent component) {
        SocketType[] gpuSockets= {SocketType.PCI_E_V1, SocketType.PCI_E_V2, SocketType.PCI_E_V3};
        return component.getAnyConfig(gpuSockets);
    }

    private Configure getMemorySocketConfig(PcComponent component) {
        SocketType[] cpuSockets= {SocketType.DDR, SocketType.DDR2, SocketType.DDR3, SocketType.DDR4};
        return component.getAnyConfig(cpuSockets);
    }

    private ValidateResult getPsu(PcComponent component, Boolean checkConnect) {
        return new ValidateResult(new ValidateError[0], new ValidateWarning[0]);
    }

    private ValidateResult getRam(PcComponent component, Boolean checkConnect) {
        LinkedList<ValidateError> errors = new LinkedList<>();

        Configure cfg = getMemorySocketConfig(component);

        if(cfg == null){
            errors.add(new ValidateError("Интерфейс для ОЗУ не сконфигурирован"));
            return new ValidateResult(errors.toArray(new ValidateError[0]), new ValidateWarning[0]);
        }

        if(checkConnect && cfg.require && cfg.connect == null) {
            errors.add(new ValidateError("ОЗУ не к чему не подключён"));
        }

        return new ValidateResult(errors.toArray(new ValidateError[0]), new ValidateWarning[0]);
    }

    private ValidateResult getGpu(PcComponent component, Boolean checkConnect) {
        LinkedList<ValidateError> errors = new LinkedList<>();
        LinkedList<ValidateWarning> warns = new LinkedList<>();

        Configure cfg = getGpuSocketConfig(component);

        if(cfg == null){
            errors.add(new ValidateError("Интерфейс для GPU не сконфигурирован"));
            return new ValidateResult(errors.toArray(new ValidateError[0]), new ValidateWarning[0]);
        }

        if(checkConnect && cfg.require && cfg.connect == null) {
            errors.add(new ValidateError("GPU не к чему не подключён"));
        }

        return new ValidateResult(errors.toArray(new ValidateError[0]), new ValidateWarning[0]);
    }

    private ValidateResult getCpu(PcComponent component, Boolean checkConnect) {
        LinkedList<ValidateError> errors = new LinkedList<>();
        LinkedList<ValidateWarning> warns = new LinkedList<>();

        Configure cfg = getCpuSocketConfig(component);

        if(cfg == null) {
            errors.add(new ValidateError("Интерфейс для CPU не сконфигурирован"));
            return new ValidateResult(errors.toArray(new ValidateError[0]), new ValidateWarning[0]);
        }

        if(checkConnect && cfg.require && cfg.connect == null) {
            errors.add(new ValidateError("CPU не к чему не подключён"));
        }

        if(checkConnect && cfg.connect != null) {

            if(cfg.connect.getType() != ComponentType.MOTHERBOARD){
                errors.add(new ValidateError("Процесс должен быть подключёк к Материнской палте"));
            } else {
                Configure cfgMemory = getMemorySocketConfig(cfg.connect);
                Configure cfgMemoryCpu = getMemorySocketConfig(component);
                if(cfgMemory != null && cfgMemoryCpu != null && cfgMemory.type != cfgMemoryCpu.type){
                    errors.add(new ValidateError("Процесс не поддерживает ОЗУ материнкой платы"));
                }
            }

            Configure connectCfg = getCpuSocketConfig(cfg.connect);

            if(connectCfg == null) {
                errors.add(new ValidateError("У подключённого оборудования нету Нужного интерфейса входа"));
            }else if (connectCfg.type != cfg.type) {
                errors.add(new ValidateError("Выбранны несовместимые компоненты"));
            }

        }

        return new ValidateResult(errors.toArray(new ValidateError[0]), warns.toArray(new ValidateWarning[0]));
    }

    private ValidateResult getMotherboard(PcComponent component, Boolean checkConnect) {
        LinkedList<ValidateError> errors = new LinkedList<>();
        LinkedList<ValidateWarning> warns = new LinkedList<>();
        Configure cfgSata = getSataConfig(component);

        if(checkConnect && cfgSata != null && cfgSata.require && cfgSata.connect == null){
            errors.add(new ValidateError("В Sata не подключён диск"));
        }

        //подклбючённый HDD
        if(checkConnect && cfgSata != null && cfgSata.connect != null){ // Проверяем
            if(cfgSata.connect.getType() != ComponentType.HDD || cfgSata.connect.getType() != ComponentType.SSD){
                errors.add(new ValidateError("В SATA слот подключён не накопитель данных"));
            }
            Configure connectCfg = getSataConfig(cfgSata.connect);
            if(connectCfg == null) {
                errors.add(new ValidateError("У подключённого оборудования нету SATA входа"));
            }else if (connectCfg.type != cfgSata.type){
                warns.add(new ValidateWarning("Подключение осуществленно не оптимально."));
            }
        }

        ///// Проверка CPU
        Configure cfgCpu = getCpuSocketConfig(component);

        if(cfgCpu == null){
            errors.add(new ValidateError("Процессор для Материнской платы не сконфигурирован."));
        } else {
            if(checkConnect && cfgCpu.require && cfgCpu.connect == null) {
                errors.add(new ValidateError("Интерфейс для CPU не сконфигурирован"));
            }

            if(checkConnect && cfgCpu.connect != null) {
                Configure cfgCpuConnected = getCpuSocketConfig(cfgCpu.connect);
                if(cfgCpuConnected == null){
                    errors.add(new ValidateError("Прцоессор не подключён"));
                }
                else if(cfgCpu.type != cfgCpuConnected.type){
                    errors.add(new ValidateError("Подключён не корректный процессор"));
                }
            }
        }

        ///// Проверка GPU

        Configure cfgGpu = getGpuSocketConfig(component);

        if(cfgGpu == null){
            errors.add(new ValidateError("Графическая карта для Материнской платы не сконфигурирована."));
        } else {
            if(checkConnect && cfgGpu.require && cfgGpu.connect == null) {
                errors.add(new ValidateError("Интерфейс для GPU не сконфигурирован"));
            }

            if(checkConnect && cfgGpu.connect != null) {
                Configure cfgGpuConnected = getGpuSocketConfig(cfgGpu.connect);
                if(cfgGpuConnected == null){
                    errors.add(new ValidateError("Графическая карта не подключёна"));
                }
                else if(cfgGpu.type != cfgGpuConnected.type){
                    errors.add(new ValidateError("Подключёна не корректный Графическая карта"));
                }
            }
        }
        ///// Проверка RAM

        Configure cfgRam = getMemorySocketConfig(component);

        if(cfgRam == null){
            errors.add(new ValidateError("ОЗУ для Материнской платы не сконфигурирована."));
        } else {
            if(checkConnect && cfgRam.require && cfgRam.connect == null) {
                errors.add(new ValidateError("Интерфейс для ОЗУ не сконфигурирован"));
            }

            if(checkConnect && cfgRam.connect != null) {
                Configure cfgRamConnected = getMemorySocketConfig(cfgRam.connect);
                if(cfgRamConnected == null){
                    errors.add(new ValidateError("ОЗУ не подключёна"));
                }
                else if(cfgRam.type != cfgRamConnected.type){
                    errors.add(new ValidateError("Подключён не корректный ОЗУ"));
                }
            }
        }

        return new ValidateResult(errors.toArray(new ValidateError[0]), warns.toArray(new ValidateWarning[0]));
    }


    public static class ValidateError {// rkfcc jib,rb
        private String message = "";
        public ValidateError(String msg){
            message = msg;
        }

        public String getMessage() {
            return message;
        }
    }
    public static class ValidateWarning {// rkfcc ghtleght;ltybz
        private String message = "";
        public ValidateWarning(String msg){
            message = msg;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class ValidateResult{
        private ValidateError[] errors;
        private ValidateWarning[] warning;

        public ValidateResult(ValidateError[] errors, ValidateWarning[] warning) {
            this.errors = errors;
            this.warning = warning;
            if(this.errors == null){
                this.errors = new ValidateError[0];
            }
            if(this.warning == null){
                this.warning = new ValidateWarning[0];
            }
        }

        public ValidateError[] getErrors() {
            return errors;
        }

        public ValidateWarning[] getWarning() {
            return warning;
        }
    }
}
