package vn.hoidanit.jobhunter.util;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import jakarta.servlet.http.HttpServletResponse;
import vn.hoidanit.jobhunter.domain.RestResponse;

public class FormatRestRespone implements ResponseBodyAdvice<Object>{

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    @Nullable
    public Object beforeBodyWrite(
            @Nullable Object body,
            MethodParameter returnType, 
            MediaType selectedContentType,
            Class selectedConverterType, 
            ServerHttpRequest request,
            ServerHttpResponse response) {
        // TODO Auto-generated method stub
        HttpServletResponse servletResponse= ((ServletServerHttpResponse) response).getServletResponse();
        int status =servletResponse.getStatus();
        RestResponse<Object> res=new RestResponse<Object>();
        res.setStatusCode(status);

        if (body instanceof String) {
            return body;
            
        }
        if (status >=400) {
            //case err
            return body;
        }else{
            // case success
            res.setData(body);
            res.setMessage("Api success");
        }
                return res;
    }
    
}
