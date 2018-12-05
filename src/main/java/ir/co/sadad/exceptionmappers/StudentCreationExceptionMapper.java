package ir.co.sadad.exceptionmappers;

import ir.co.sadad.exception.StudentCreationException;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;


@Provider
public class StudentCreationExceptionMapper implements ResponseExceptionMapper<StudentCreationException> {

    @Inject
    private Logger log;



    @Override
    public StudentCreationException toThrowable(Response response) {
        return new StudentCreationException();
    }

    @Override
    public boolean handles(int status, MultivaluedMap<String, Object> headers) {
        log.error("status = " + status);
        return status == 500;
    }



}
