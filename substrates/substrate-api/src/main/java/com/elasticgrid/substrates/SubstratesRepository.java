package com.elasticgrid.substrates;

import java.util.List;
import java.util.LinkedList;
import java.util.Enumeration;
import java.net.URLClassLoader;
import java.net.URL;
import java.io.*;

public class SubstratesRepository {
    private static final String SUBSTRATE_DSL_LOCATION = "META-INF/com/elasticgrid/substrates/SubstrateClass";

    public static List<Substrate> findSubstrates() throws Exception {
        URLClassLoader cl = (URLClassLoader) Thread.currentThread().getContextClassLoader();
        List<Substrate> substrates = new LinkedList<Substrate>();
        // search for all JARs having some substrates declared
        Enumeration<URL> resources = cl.getResources(SUBSTRATE_DSL_LOCATION);
        // extract substrate class names and build them
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            String substrateClassName;
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new InputStreamReader(url.openStream()));
                substrateClassName = reader.readLine();
            } finally {
                if (reader != null)
                    reader.close();
            }
            Substrate substrate = (Substrate) cl.loadClass(substrateClassName).newInstance();
            substrates.add(substrate);
        }
        return substrates;
    }
}
