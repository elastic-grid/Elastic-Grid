/**
 * Copyright (C) 2007-2008 Elastic Grid, LLC.
 * 
 * This file is part of Elastic Grid.
 * 
 * Elastic Grid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * 
 * Elastic Grid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Elastic Grid.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.elasticgrid.model.internal.jibx;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * XML Utilities (mostly in order to use JiBX).
 * @author Jerome Bernard
 */
public class XmlUtils {
    private static final Logger logger = Logger.getLogger(XmlUtils.class.getName());

    /**
     * Uses JiBX in order to marshall the object into its XML representation suitable for storage.
     * @param binding the name of the binding
     * @param object the object to marshall
     * @return the marshalled form (XML) of the object
     * @throws ObjectXmlMappingException if there is a problem when marshalling
     */
    @Deprecated
    public static String convertObjectToXml(String binding, Object object) throws ObjectXmlMappingException {
        return convertObjectToXml(binding, object, "UTF-8");
    }

    /**
     * Uses JiBX in order to marshall the object into its XML representation suitable for storage.
     * @param binding the name of the binding
     * @param object the object to marshall
     * @param encoding the encoding used
     * @return the marshalled form (XML) of the object
     * @throws ObjectXmlMappingException if there is a problem when marshalling
     */
    public static String convertObjectToXml(String binding, Object object, String encoding) throws ObjectXmlMappingException {
        StringWriter sw = new StringWriter();
        convertObjectToXml(binding, object, sw, encoding);
        return sw.toString();
    }

    /**
     * Uses JiBX in order to marshall the object into its XML representation suitable for storage.
     * @param binding the name of the binding
     * @param object the object to marshall
     * @param writer the writer to use for marshalling
     * @param encoding the encoding used
     * @throws ObjectXmlMappingException if there is a problem when marshalling
     */
    public static void convertObjectToXml(String binding, Object object, Writer writer, String encoding) throws ObjectXmlMappingException {
        try {
            // create the XML bindings
            IBindingFactory bfact = BindingDirectory.getFactory(binding, object.getClass());
            IMarshallingContext mctx = bfact.createMarshallingContext();
            mctx.setIndent(4);
            // marshall the object to XML
            StringWriter sw = new StringWriter();
            mctx.marshalDocument(object, encoding, null, writer);
            if (logger.isLoggable(Level.INFO)) {
                logger.fine("Saving: " + object);
                logger.fine("Marshalled to XML: " + sw.toString());
            }
        } catch (JiBXException e) {
            throw new ObjectXmlMappingException(e);
        }
    }

    /**
     * Uses JiBX in order to unmarshall the object from its XML representaton suitable for storage.
     * @param binding the name of the binding
     * @param clazz the class of the unmarshalled object
     * @param xml the marshalled form (XML) of the object
     * @return the object unmarshalled
     * @throws ObjectXmlMappingException if there is a problem when unmarshalling
     */
    @Deprecated
    public static <T> T convertXmlToObject(String binding, Class<T> clazz, String xml) throws ObjectXmlMappingException {
        return convertXmlToObject(binding, clazz, new StringReader(xml));
    }

    /**
     * Uses JiBX in order to unmarshall the object from its XML representaton suitable for storage.
     * @param binding the name of the binding
     * @param clazz the class of the unmarshalled object
     * @param reader the reader to the marshalled form (XML) of the object
     * @return the object unmarshalled
     * @throws ObjectXmlMappingException if there is a problem when unmarshalling
     */
    public static <T> T convertXmlToObject(String binding, Class<T> clazz, Reader reader) throws ObjectXmlMappingException {
        try {
            // create the XML bindings
            IBindingFactory bfact = binding == null ? BindingDirectory.getFactory(clazz) : BindingDirectory.getFactory(binding, clazz);
            IUnmarshallingContext mctx = bfact.createUnmarshallingContext();
            // unmarshall the XML to object
            Object object = mctx.unmarshalDocument(reader);
            if (logger.isLoggable(Level.INFO)) {
                logger.fine("Unmarshalled to: " + object);
            }
            return (T) object;
        } catch (JiBXException e) {
            throw new ObjectXmlMappingException(e);
        }
    }

    /**
     * Uses JiBX in order to unmarshall the object from its XML representaton suitable for storage.
     * @param binding the name of the binding
     * @param clazz the class of the unmarshalled object
     * @param stream the stream to the marshalled form (XML) of the object
     * @param encoding the encoding of the stream
     * @return the object unmarshalled
     * @throws ObjectXmlMappingException if there is a problem when unmarshalling
     */
    public static <T> T convertXmlToObject(String binding, Class<T> clazz, InputStream stream, String encoding) throws ObjectXmlMappingException {
        try {
            // create the XML bindings
            IBindingFactory bfact = binding == null ? BindingDirectory.getFactory(clazz) : BindingDirectory.getFactory(binding, clazz);
            IUnmarshallingContext mctx = bfact.createUnmarshallingContext();
            // unmarshall the XML to object
            Object object = mctx.unmarshalDocument(stream, encoding);
            if (logger.isLoggable(Level.INFO)) {
                logger.fine("Unmarshalled to: " + object);
            }
            return (T) object;
        } catch (JiBXException e) {
            throw new ObjectXmlMappingException(e);
        }
    }

}
