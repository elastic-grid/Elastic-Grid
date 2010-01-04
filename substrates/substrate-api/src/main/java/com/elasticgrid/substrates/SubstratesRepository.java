/**
 * Elastic Grid
 * Copyright (C) 2008-2010 Elastic Grid, LLC.
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
package com.elasticgrid.substrates;

import java.util.List;
import java.util.LinkedList;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.net.URLClassLoader;
import java.net.URL;
import java.io.*;

public class SubstratesRepository {
    private static final String SUBSTRATES_DIRECTORY = System.getProperty("EG_HOME") + File.separator + "lib" + File.separator + "elastic-grid";
    private static final String SUBSTRATE_DSL_LOCATION = "META-INF/com/elasticgrid/substrates/SubstrateClass";
    private static final Logger logger = Logger.getLogger(SubstratesRepository.class.getName());

    public static List<Substrate> findSubstrates() throws Exception {
        logger.info("Searching for available substrates...");
        File substratesDirectory = new File(SUBSTRATES_DIRECTORY);
        File[] substratesJars = substratesDirectory.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith("substrate-") && !name.startsWith("substrate-api");
            }
        });
        if (substratesJars == null)
            substratesJars = new File[0];
        URL[] urls = new URL[substratesJars.length];
        for (int i = 0; i < substratesJars.length; i++) {
            File substratesJar = substratesJars[i];
            logger.fine("Detected JAR " + substratesJar.getName());
            urls[i] = substratesJar.toURI().toURL();
        }
        URLClassLoader cl = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());
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
