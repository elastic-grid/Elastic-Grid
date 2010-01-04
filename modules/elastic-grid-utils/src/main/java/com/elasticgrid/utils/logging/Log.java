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
package com.elasticgrid.utils.logging;

import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.Map;
import java.util.HashMap;

public final class Log {
    private static final Map<String, Logger> loggers = new HashMap<String, Logger>();

    private Log() {
    }

    public static void debug(String message, Object... params) {
        log(Level.FINE, message, params);
    }

    public static void info(String message, Object... params) {
        log(Level.INFO, message, params);
    }

    public static void fine(String message, Object... params) {
        log(Level.FINE, message, params);
    }

    public static void finer(String message, Object... params) {
        log(Level.FINER, message, params);
    }

    public static void finest(String message, Object... params) {
        log(Level.FINEST, message, params);
    }

    public static void warn(String message, Object... params) {
        log(Level.WARNING, message, params);
    }

    public static void error(String message, Object... params) {
        log(Level.SEVERE, message, params);
    }

    public static void log(Level level, String message, Object... params) {
        final int stackPositionOfCaller = 2;
        StackTraceElement caller = new Throwable().getStackTrace()[stackPositionOfCaller];
        String className = caller.getClassName();
        Logger logger;
        synchronized (loggers) {
            logger = loggers.get(className);
            if (logger == null) {
                logger = Logger.getLogger(className);
                loggers.put(className, logger);
            }
        }
        if (logger.isLoggable(level)) {
            String formattedMessage;
            Throwable thrown = null;
            if (params.length == 0) {
                formattedMessage = message;
            } else {
                Object last = params[params.length - 1];
                if (last instanceof Throwable) {
                    Object[] subParams = new Object[params.length - 1];
                    System.arraycopy(params, 0, subParams, 0, subParams.length);
                    formattedMessage = String.format(message, subParams);
                    thrown = (Throwable) last;
                } else {
                    formattedMessage = String.format(message, params);
                }
            }
            LogRecord record = new LogRecord(level, formattedMessage);
            record.setLoggerName(logger.getName());
            record.setSourceClassName(className);
            record.setSourceMethodName(caller.getMethodName());
            record.setThrown(thrown);
            record.setParameters(params);
            logger.log(record);
        }
    }
}
