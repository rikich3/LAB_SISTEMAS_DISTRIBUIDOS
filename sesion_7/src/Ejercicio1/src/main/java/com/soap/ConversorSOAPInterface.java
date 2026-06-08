package com.soap;

import jakarta.jws.WebService;
import jakarta.jws.WebMethod;

@WebService
public interface ConversorSOAPInterface {
    @WebMethod
    double cToF(double c);

    @WebMethod
    double fToC(double f);
}
