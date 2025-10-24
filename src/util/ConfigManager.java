/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import datastructures.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Gestor de configuración para guardar y cargar parámetros de simulación.
 *
 * @author Santiago
 */
public class ConfigManager {

    /**
     * Clase contenedora para la configuración completa de la simulación.
     */
    public static class SimulationConfig {
        private long cycleDurationMillis;
        private ArrayList<ProcessData> processes;

        public SimulationConfig() {
            this.processes = new ArrayList<>();
        }

        public SimulationConfig(long cycleDurationMillis, ArrayList<ProcessData> processes) {
            this.cycleDurationMillis = cycleDurationMillis;
            this.processes = processes;
        }

        public long getCycleDurationMillis() {
            return cycleDurationMillis;
        }

        public void setCycleDurationMillis(long cycleDurationMillis) {
            this.cycleDurationMillis = cycleDurationMillis;
        }

        public ArrayList<ProcessData> getProcesses() {
            return processes;
        }

        public void setProcesses(ArrayList<ProcessData> processes) {
            this.processes = processes;
        }
    }

    /**
     * Guarda la configuración en un archivo JSON.
     *
     * @param config Configuración a guardar
     * @param file Archivo destino
     * @throws IOException Si hay error al escribir el archivo
     */
    public static void save(SimulationConfig config, File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("{\n");
            writer.write("  \"cycleDurationMillis\": " + config.getCycleDurationMillis() + ",\n");
            writer.write("  \"processes\": [\n");

            ArrayList<ProcessData> processes = config.getProcesses();
            for (int i = 0; i < processes.size(); i++) {
                ProcessData p = processes.get(i);
                writer.write("    {\n");
                writer.write("      \"processName\": \"" + escapeJson(p.getProcessName()) + "\",\n");
                writer.write("      \"totalInstructions\": " + p.getTotalInstructions() + ",\n");
                writer.write("      \"isIOBound\": " + p.isIOBound() + ",\n");
                writer.write("      \"ioExceptionCycle\": " + p.getIoExceptionCycle() + ",\n");
                writer.write("      \"ioDuration\": " + p.getIoDuration() + ",\n");
                writer.write("      \"arrivalCycle\": " + p.getArrivalCycle() + "\n");
                writer.write("    }");
                if (i < processes.size() - 1) {
                    writer.write(",");
                }
                writer.write("\n");
            }

            writer.write("  ]\n");
            writer.write("}\n");
        }
    }

    /**
     * Carga la configuración desde un archivo JSON.
     *
     * @param file Archivo fuente
     * @return Configuración cargada
     * @throws IOException Si hay error al leer el archivo
     * @throws IllegalArgumentException Si el formato JSON es inválido
     */
    public static SimulationConfig load(File file) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append(" ");
            }
        }

        String json = content.toString();
        SimulationConfig config = new SimulationConfig();

        // Parsear cycleDurationMillis
        long cycleDuration = parseLongValue(json, "cycleDurationMillis");
        if (cycleDuration == 0L) {
            cycleDuration = 100L;
        }
        config.setCycleDurationMillis(cycleDuration);

        // Parsear array de procesos
        String processesArray = extractArrayValue(json, "processes");
        if (processesArray != null && !processesArray.isEmpty()) {
            ArrayList<ProcessData> processes = parseProcessArray(processesArray);
            config.setProcesses(processes);
        }

        return config;
    }

    /**
     * Extrae un valor long de una clave JSON.
     */
    private static long parseLongValue(String json, String key) {
        int keyIndex = json.indexOf("\"" + key + "\"");
        if (keyIndex == -1) {
            return 0L; // valor por defecto
        }
        int colonIndex = json.indexOf(":", keyIndex);
        if (colonIndex == -1) {
            return 0L;
        }
        int startIndex = colonIndex + 1;
        while (startIndex < json.length() && Character.isWhitespace(json.charAt(startIndex))) {
            startIndex++;
        }
        int endIndex = startIndex;
        while (endIndex < json.length() &&
               (Character.isDigit(json.charAt(endIndex)) || json.charAt(endIndex) == '-')) {
            endIndex++;
        }
        if (startIndex >= endIndex) {
            return 0L;
        }
        String valueStr = json.substring(startIndex, endIndex).trim();
        try {
            return Long.parseLong(valueStr);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    /**
     * Extrae el contenido de un array JSON.
     */
    private static String extractArrayValue(String json, String key) {
        int keyIndex = json.indexOf("\"" + key + "\"");
        if (keyIndex == -1) {
            return "";
        }

        int colonIndex = json.indexOf(":", keyIndex);
        if (colonIndex == -1) {
            return "";
        }

        int startIndex = json.indexOf("[", colonIndex);
        if (startIndex == -1) {
            return "";
        }

        startIndex++; // Avanzar después del '['
        int depth = 1;
        int endIndex = startIndex;

        while (depth > 0 && endIndex < json.length()) {
            if (json.charAt(endIndex) == '[') {
                depth++;
            } else if (json.charAt(endIndex) == ']') {
                depth--;
            }
            endIndex++;
        }

        return json.substring(startIndex, endIndex - 1);
    }

    /**
     * Parsea un array JSON de procesos.
     */
    private static ArrayList<ProcessData> parseProcessArray(String arrayContent) {
        ArrayList<ProcessData> processes = new ArrayList<>();

        // Dividir en objetos individuales
        int depth = 0;
        int objStart = -1;
        for (int i = 0; i < arrayContent.length(); i++) {
            char c = arrayContent.charAt(i);
            if (c == '{') {
                if (depth == 0) {
                    objStart = i;
                }
                depth++;
            } else if (c == '}') {
                depth--;
                if (depth == 0 && objStart != -1) {
                    String objStr = arrayContent.substring(objStart, i + 1);
                    ProcessData pd = parseProcessObject(objStr);
                    if (pd != null) {
                        processes.add(pd);
                    }
                    objStart = -1;
                }
            }
        }

        return processes;
    }

    /**
     * Parsea un objeto JSON individual de proceso.
     */
    private static ProcessData parseProcessObject(String objStr) {
        ProcessData pd = new ProcessData();

        pd.setProcessName(parseStringValue(objStr, "processName"));
        pd.setTotalInstructions(parseIntValue(objStr, "totalInstructions"));
        pd.setIOBound(parseBooleanValue(objStr, "isIOBound"));
        pd.setIoExceptionCycle(parseIntValue(objStr, "ioExceptionCycle"));
        pd.setIoDuration(parseIntValue(objStr, "ioDuration"));
        pd.setArrivalCycle(parseLongValue(objStr, "arrivalCycle"));

        return pd;
    }

    /**
     * Parsea un valor String de JSON.
     */
    private static String parseStringValue(String json, String key) {
        int keyIndex = json.indexOf("\"" + key + "\"");
        if (keyIndex == -1) {
            return "";
        }
        int colonIndex = json.indexOf(":", keyIndex);
        if (colonIndex == -1) {
            return "";
        }
        int startIndex = json.indexOf("\"", colonIndex);
        if (startIndex == -1) {
            return "";
        }
        startIndex++;
        int endIndex = startIndex;
        while (endIndex < json.length() && json.charAt(endIndex) != '\"') {
            if (json.charAt(endIndex) == '\\') {
                endIndex += 2;
            } else {
                endIndex++;
            }
        }
        return json.substring(startIndex, endIndex);
    }

    /**
     * Parsea un valor int de JSON.
     */
    private static int parseIntValue(String json, String key) {
        int keyIndex = json.indexOf("\"" + key + "\"");
        if (keyIndex == -1) {
            return 0;
        }
        int colonIndex = json.indexOf(":", keyIndex);
        if (colonIndex == -1) {
            return 0;
        }
        int startIndex = colonIndex + 1;
        while (startIndex < json.length() && Character.isWhitespace(json.charAt(startIndex))) {
            startIndex++;
        }
        int endIndex = startIndex;
        while (endIndex < json.length() &&
               (Character.isDigit(json.charAt(endIndex)) || json.charAt(endIndex) == '-')) {
            endIndex++;
        }
        if (startIndex >= endIndex) {
            return 0;
        }
        String valueStr = json.substring(startIndex, endIndex).trim();
        try {
            return Integer.parseInt(valueStr);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Parsea un valor boolean de JSON.
     */
    private static boolean parseBooleanValue(String json, String key) {
        int keyIndex = json.indexOf("\"" + key + "\"");
        if (keyIndex == -1) {
            return false;
        }
        int colonIndex = json.indexOf(":", keyIndex);
        if (colonIndex == -1) {
            return false;
        }
        int startIndex = colonIndex + 1;
        while (startIndex < json.length() && Character.isWhitespace(json.charAt(startIndex))) {
            startIndex++;
        }
        int endIndex = startIndex;
        while (endIndex < json.length() &&
               Character.isLetter(json.charAt(endIndex))) {
            endIndex++;
        }
        if (startIndex >= endIndex) {
            return false;
        }
        String valueStr = json.substring(startIndex, endIndex).trim();
        return Boolean.parseBoolean(valueStr);
    }

    /**
     * Escapa caracteres especiales para JSON.
     */
    private static String escapeJson(String str) {
        if (str == null) {
            return "";
        }
        return str.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
