/**
 * Elastic Grid
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.rioproject.bean;

import com.elasticgrid.Associated;
import com.elasticgrid.ServiceBean;
import com.elasticgrid.ServiceInterface;
import javassist.*;
import org.rioproject.boot.ServiceClassLoader;
import org.rioproject.core.*;
import org.rioproject.core.jsb.ServiceBeanContext;
import org.rioproject.jsb.JSBContext;
import org.rioproject.jsb.ServiceElementUtil;
import org.rioproject.opstring.OpStringLoader;
import org.rioproject.associations.AssociationDescriptor;
import org.rioproject.associations.AssociationType;
import org.scannotation.AnnotationDB;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Jerome Bernard
 */
public class AnnotationBeanFactory extends BeanFactory {
    private Class beanClass;
    private final Logger logger = Logger.getLogger(getClass().getName());

    public Created create(ServiceBeanContext context) throws JSBInstantiationException {
        try {
            final ClassPool pool = new ClassPool();

            URL[] urls = ((ServiceClassLoader) Thread.currentThread().getContextClassLoader()).getSearchPath();
            for (URL url : urls)
                logger.log(Level.INFO, "Scanning URL: {0}", url.toString());
            AnnotationDB db = new AnnotationDB();
            db.scanArchives(urls);
            Set<String> beans = db.getAnnotationIndex().get(ServiceBean.class.getName());
            pool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
            pool.importPackage("org.rioproject.core.jsb");

            String beanClassName = beans.iterator().next();
            ServiceBeanConfig config = context.getServiceBeanConfig();
            beanClass = getBeanClass(beanClassName, config, pool);
            logger.log(Level.INFO, "Using implementation class {0}", beanClass.getName());
            ServiceElement serviceElement = createServiceElement(beanClassName, context, pool);
            ServiceBeanContext annotationContext = new JSBContext(serviceElement, context.getServiceBeanManager(),
                    ((JSBContext) context).getComputeResource(), context.getConfiguration());
            return super.create(annotationContext);
        } catch (Exception e) {
            throw new JSBInstantiationException(e.getMessage(), e);
        }
    }

    @Override
    protected Object getBean(ServiceBeanContext context) throws Exception {
        return beanClass.newInstance();
    }

    private Class getBeanClass(String beanClassName, ServiceBeanConfig config, ClassPool pool) throws Exception {
        logger.log(Level.INFO, "Loading bean {0}", beanClassName);
        CtClass beanClass = pool.get(beanClassName);

        // inject ServiceBeanContext as a field
        logger.info("Injecting service bean context as field...");
        CtField ctxField = CtField.make(
                "private ServiceBeanContext __service_bean_context;", beanClass);
        beanClass.addField(ctxField);
        logger.info("Injecting method void setServiceBeanContext(ServiceBeanContext ctx)...");
        CtMethod ctxMethod = CtMethod.make("" +
                "public void setServiceBeanContext(ServiceBeanContext ctx) {" +
                "this.__service_bean_context = ctx;" +
                "}", beanClass);
        beanClass.addMethod(ctxMethod);

        logger.info("Generating bytecode...");
        return beanClass.toClass();
    }

    private ServiceElement createServiceElement(String bean, ServiceBeanContext context, ClassPool pool) throws Exception {
        logger.log(Level.INFO, "Loading bean {0}", bean);

        CtClass beanClass = pool.get(bean);

        String serviceName = null;
        String serviceComment = null;
        String group = null;
        Class serviceInterface = null;
        boolean autoAdvertise = true;
        List<AssociationDescriptor> associations = new ArrayList<AssociationDescriptor>();

        ServiceBeanConfig config = context.getServiceBeanConfig();
        String opstring = config.getOperationalStringName();

        for (Object annotation : beanClass.getAnnotations()) {
            logger.log(Level.INFO, "Processing annotation {0}", annotation.toString());
            if (annotation instanceof ServiceBean) {
                ServiceBean serviceAnnotation = (ServiceBean) annotation;
                serviceName = serviceAnnotation.name();
                serviceComment = serviceAnnotation.comment();
                group = serviceAnnotation.group();
            } else if (annotation instanceof ServiceInterface) {
                serviceInterface = ((ServiceInterface) annotation).value();
            }
        }

        String codebase = context.getExportCodebase();
        logger.log(Level.INFO, "Setting codebase to {0}", codebase);

        URL[] implUrls = ((ServiceClassLoader) Thread.currentThread().getContextClassLoader()).getSearchPath();
        List<String> implJars = new ArrayList<String>();
        for (URL implUrl : implUrls) {
            String implJar = implUrl.toString().substring(codebase.length());
            logger.log(Level.INFO, "Adding implementation JAR {0}", implJar);
            implJars.add(implJar);
        }
        URL[] downloadUrls = context.getServiceElement().getExportURLs();
        List<String> downloadJars = new ArrayList<String>();
        for (URL downloadUrl : downloadUrls) {
            String downloadJar = downloadUrl.toString().substring(codebase.length());
            logger.log(Level.INFO, "Adding download JAR {0}", downloadJar);
            downloadJars.add(downloadJar);
        }

        // scan fields ...
        CtField[] fields = beanClass.getDeclaredFields();
        for (CtField field : fields) {
            logger.log(Level.INFO, "Scanning field {0} for injection...", field.getName());
            Object[] annotations = field.getAnnotations();

            // ... for associations
            Associated associationAnnotation = null;
            for (Object annotation : annotations)
                if (annotation instanceof Associated)
                    associationAnnotation = (Associated) annotation;

            if (associationAnnotation == null)
                continue;

            String name = associationAnnotation.name();
            AssociationType type = associationAnnotation.type();
            if (AssociationType.REQUIRES.equals(type))
                autoAdvertise = false;
            String property = field.getName();
            AssociationDescriptor association = new AssociationDescriptor(type, name, opstring, property);
            if (associationAnnotation.groups() != null && associationAnnotation.groups().length > 0
                    && !"".equals(associationAnnotation.groups()[0]))
                association.setGroups(associationAnnotation.groups());
            else
                association.setGroups(group);
            association.setMatchOnName(associationAnnotation.matchOnName());
            association.setProxyType(associationAnnotation.proxyType());
            association.setServiceSelectionStrategy(associationAnnotation.serviceSelectionStrategy().getName());
            association.setInterfaceNames(associationAnnotation.serviceInterface().getName());
            association.setFaultDetectionHandlerBundle(getDefaultFDH());
            logger.log(Level.INFO, "Adding association {0}", association);
            associations.add(association);
        }

        List<String> interfaceClassNames = Arrays.asList(serviceInterface.getName());
        ServiceElement serviceElement = ServiceElementUtil.create(
                serviceName, bean, interfaceClassNames, implJars, downloadJars, codebase, group);
        serviceElement.setAutoAdvertise(autoAdvertise);
        ServiceBeanConfig serviceConfig = serviceElement.getServiceBeanConfig();
        serviceConfig.setOperationalStringName(opstring);
        serviceConfig.getConfigurationParameters().put(ServiceBeanConfig.COMMENT, serviceComment);
        serviceElement.setAssociationDescriptors(associations.toArray(new AssociationDescriptor[associations.size()]));
        return serviceElement;
    }

    private static ClassBundle getDefaultFDH() {
        ClassBundle fdhBundle = new ClassBundle(OpStringLoader.DEFAULT_FDH);
        String[] empty = new String[]{"-"};
        fdhBundle.addMethod("setConfiguration", new Object[]{empty});
        return fdhBundle;
    }
}
