package org.mydrive.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.mydrive.annotation.GlobalInterceptor;
import org.mydrive.annotation.VerifyParam;
import org.mydrive.entity.enums.ResponseCodeEnum;
import org.mydrive.exception.BusinessException;
import org.mydrive.utils.StringTools;
import org.mydrive.utils.VerifyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;


@Aspect
@Component("globalOperatcionAspect")
public class GlobalOperatcionAspect {

    public static final Logger logger = LoggerFactory.getLogger(GlobalOperatcionAspect.class);

    public static final String TYPE_STRING = "java.lang.String";
    public static final String TYPE_INTEGER = "java.lang.Integer";
    public static final String TYPE_LONG = "java.lang.Long";

    @Pointcut("@annotation(org.mydrive.annotation.GlobalInterceptor)")
    private void requestInterceptor() {

    }


    @Before("requestInterceptor()")
    public void interceptorDo(JoinPoint point) throws BusinessException{
        try{
            Object target = point.getTarget();
            Object[] args = point.getArgs();
            String methodName = point.getSignature().getName();
            Class<?>[] parameterTypes = ((MethodSignature) point.getSignature()).getMethod().getParameterTypes();
            Method method = target.getClass().getMethod(methodName, parameterTypes);
            GlobalInterceptor interceptor = method.getAnnotation(GlobalInterceptor.class);
            if (null == interceptor){
                return;
            }
            /**
             * 校验登录
             */
//            if (interceptor.checkLogin() || interceptor.checkAdmin()){
//                checkLogin(interceptor.checkAdmin());
//            }
            /**
             * 校验参数
             */
            if (interceptor.checkParams()){
                validateParams(method, args);
            }


        } catch (BusinessException e){
            logger.error("全局拦截器异常", e);
            throw e;
        } catch (Exception e){
            logger.error("全局拦截器异常", e);
            throw new BusinessException(ResponseCodeEnum.CODE_500);
        } catch (Throwable e) {
            logger.error("全局拦截器异常", e);
            throw new BusinessException(ResponseCodeEnum.CODE_500);
        }

    }

    private void validateParams(Method m, Object[] args) throws BusinessException{
        Parameter[] parameters = m.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Object value = args[i];
            VerifyParam verifyParam = parameter.getAnnotation(VerifyParam.class);
            if (verifyParam == null){
                continue;
            }
            // 基本数据类型
            if (TYPE_STRING.equals(parameter.getParameterizedType().getTypeName()) || TYPE_LONG.equals(parameter.getParameterizedType().getTypeName())){
                checkValue(value, verifyParam);
            } else {
                checkObjValue(parameter, value);
            }
        }
    }

    public void checkObjValue(Parameter parameter, Object value) {
         try{
             String typeName = parameter.getParameterizedType().getTypeName();
             Class<?> classz = Class.forName(typeName);
             Field[] fields = classz.getDeclaredFields();
             for (Field field : fields) {
                 VerifyParam fieldVerifyParam = field.getAnnotation(VerifyParam.class);
                 if (null == fieldVerifyParam){
                     continue;
                 }
                 field.setAccessible(true);
                 Object resultValue = field.get(value);
                checkValue(resultValue, fieldVerifyParam);
             }
         } catch (BusinessException e){
             logger.error("参数校验失败", e);
             throw e;
         } catch (Exception e){
             logger.error("参数校验失败", e);
             throw new BusinessException(ResponseCodeEnum.CODE_600);
         }
    }

    private void checkValue(Object value, VerifyParam verifyParam) throws BusinessException{
        Boolean isEmpty = value == null || StringTools.isEmpty(value.toString());
        Integer length = value == null ? 0 : value.toString().length();
        /**
         * 校验空
         */
        if (isEmpty && verifyParam.required()) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        /**
         * 校验长度
         */
        if (!isEmpty && (verifyParam.max() != -1 && verifyParam.max() < length || verifyParam.min() != -1 && verifyParam.min() > length)) {
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
        /**
         * 校验正则
         */
        if (!isEmpty && !StringTools.isEmpty(verifyParam.regex().getRegex()) && !VerifyUtils.verify(verifyParam.regex(), String.valueOf(value))){
            throw new BusinessException(ResponseCodeEnum.CODE_600);
        }
    }

}
