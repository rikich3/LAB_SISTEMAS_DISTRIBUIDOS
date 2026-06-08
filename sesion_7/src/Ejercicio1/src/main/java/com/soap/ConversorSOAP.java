package com.soap;

import jakarta.jws.WebService;

@WebService(endpointInterface = "com.soap.ConversorSOAPInterface")
public class ConversorSOAP implements ConversorSOAPInterface {

    @Override
    public double cToF(double c){
        return (c*9/5)+32;
    }

    @Override
    public double fToC(double f){
        return (f-32)*5/9;
    }
}
