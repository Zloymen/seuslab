package ru.seuslab.service.fluxservice1.error;

public class Service1Error extends RuntimeException {

    public Service1Error(){
        super("Service application 1 error");
    }
}
