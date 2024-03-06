package com.at.rpc.spring.reference;

import com.at.rpc.constants.RpcConstant;
import com.at.rpc.spring.annotation.GpRemoteReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SpringRpcReferencePostProcessor implements ApplicationContextAware, BeanClassLoaderAware, BeanFactoryPostProcessor {
    private ApplicationContext context;
    private ClassLoader classLoader;
    private RpcClientProperties clientProperties;

    //保存发布的引用bean信息
    private final Map<String, BeanDefinition> rpcRefBeanDefinitions=new ConcurrentHashMap<>();

    public SpringRpcReferencePostProcessor(RpcClientProperties clientProperties) {
        this.clientProperties = clientProperties;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader=classLoader;
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context=applicationContext;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        for (String beanDefinitionname:beanFactory.getBeanDefinitionNames()){
            //遍历bean定义，然后获取到加载的bean，遍历这些bean中的字段，是否携带GpRemoteReference注解
            //如果有，则需要构建一个动态代理实现
            BeanDefinition beanDefinition=beanFactory.getBeanDefinition(beanDefinitionname);
            String beanClassName=beanDefinition.getBeanClassName();
            if(beanClassName!=null){
                //和forName方法相同，内部就是直接调用的forName方法
                Class<?> clazz= ClassUtils.resolveClassName(beanClassName,this.classLoader);
                //针对当前类中的指定字段，动态创建一个Bean
                ReflectionUtils.doWithFields(clazz,this::parseRpcReference);
            }
        }
        //将@GpRemoteReference注解的bean，构建一个动态代理对象
        BeanDefinitionRegistry registry=(BeanDefinitionRegistry)beanFactory;
        this.rpcRefBeanDefinitions.forEach((beanName,beanDefinition)->{
            if(context.containsBean(beanName)){
                log.warn("SpringContext already register bean {}",beanName);
                return;
            }
            //把动态创建的bean注册到容器中
            registry.registerBeanDefinition(beanName,beanDefinition);
            log.info("registered RpcReferenceBean {} success.",beanName);
        });
    }
    private void parseRpcReference(Field field){
        GpRemoteReference gpRemoteReference= AnnotationUtils.getAnnotation(field,GpRemoteReference.class);
        if(gpRemoteReference!=null) {
            BeanDefinitionBuilder builder=BeanDefinitionBuilder.genericBeanDefinition(SpringRpcReferenceBean.class);
            builder.setInitMethodName(RpcConstant.INIT_METHOD_NAME);
            builder.addPropertyValue("interfaceClass",field.getType());
//            builder.addPropertyValue("serviceAddress",clientProperties.getServiceAddress());
//            builder.addPropertyValue("servicePort",clientProperties.getServicePort());
            builder.addPropertyValue("registryType",clientProperties.getRegistryType());
            builder.addPropertyValue("registryAddress",clientProperties.getRegistryAddress());
            BeanDefinition beanDefinition=builder.getBeanDefinition();
            rpcRefBeanDefinitions.put(field.getName(),beanDefinition);
        }
    }

}
