/**
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * Licensed under the GNU Lesser General Public License, Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *         http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.elasticgrid;

import org.rioproject.boot.ServiceClassLoader;
import org.rioproject.core.jsb.ServiceBeanContext;
import org.rioproject.core.ServiceElement;
import org.rioproject.core.ServiceBeanConfig;
import org.rioproject.core.OperationalStringManager;
import org.rioproject.core.ClassBundle;
import org.rioproject.jsb.ServiceBeanAdapter;
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

import javassist.*;

/**
 * {@link ServiceBeanAdapter} taking most of its configuration from annotations.
 *
 * @author Jerome Bernard
 */
public class AnnotationBeanAdapter extends ServiceBeanAdapter {
    private ClassPool pool = new ClassPool();
    private final Logger logger = Logger.getLogger(getClass().getName());

    public void initialize(ServiceBeanContext context) throws Exception {
        super.initialize(context);
        URL[] urls = ((ServiceClassLoader) Thread.currentThread().getContextClassLoader()).getSearchPath();
        for (URL url : urls)
            logger.log(Level.INFO, "Scanning URL: {0}", url.toString());
        AnnotationDB db = new AnnotationDB();
        db.scanArchives(urls);
        Set<String> beans = db.getAnnotationIndex().get(ServiceBean.class.getName());
        pool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
        pool.importPackage("org.rioproject.core.jsb");
        for (String bean : beans) {
            ServiceElement serviceElement = createServiceElement(bean);
            loadServiceElement(serviceElement);
        }
    }

    private ServiceElement createServiceElement(String bean) throws Exception {
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

        // inject ServiceBeanContext as a field
        /*
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
        */

        // scan fields for injection
        CtField[] fields = beanClass.getDeclaredFields();
        for (CtField field : fields) {
            logger.log(Level.INFO, "Scanning field {0} for injection...", field.getName());
            Object[] annotations = field.getAnnotations();
            scanInjection(field);

            // scan for associations
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

//        logger.info("Generating bytecode...");
//        beanClass.writeFile();

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

    private void loadServiceElement(ServiceElement serviceElement) throws Exception {
        logger.log(Level.INFO, "Loading service element {0}", serviceElement);
        OperationalStringManager opstringManager = context.getServiceBeanManager().getOperationalStringManager();
        opstringManager.addServiceElement(serviceElement);
    }

    private void scanInjection(CtField field) throws NotFoundException, ClassNotFoundException {
//        Object[] objects = field.getAnnotations();
//        for (Object o : objects) {
//            if (o instanceof Inject) {
//                CtClass classToInject = field.getType();
//                if (WatchRegistry.class.getName().equals(classToInject.getName())) {
//                }
//            }
//        }
    }

    private static ClassBundle getDefaultFDH() {
        ClassBundle fdhBundle = new ClassBundle(OpStringLoader.DEFAULT_FDH);
        String[] empty = new String[]{"-"};
        fdhBundle.addMethod("setConfiguration", new Object[]{empty});
        return fdhBundle;
    }
}
