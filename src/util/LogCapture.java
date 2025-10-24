/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.io.PrintStream;
import java.io.OutputStream;
import java.io.IOException;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 * Captura la salida estándar y de error (System.out y System.err)
 * y la redirige a un JTextArea de forma thread-safe.
 */
public class LogCapture {
    private final JTextArea logTextArea;
    private final PrintStream originalOut;
    private final PrintStream originalErr;
    private final PrintStream capturedStream;

    /**
     * Constructor que inicializa la captura de logs.
     *
     * @param logTextArea el componente JTextArea donde se mostrarán los logs
     */
    public LogCapture(JTextArea logTextArea) {
        this.logTextArea = logTextArea;
        this.originalOut = System.out;
        this.originalErr = System.err;

        // Crear un OutputStream personalizado que captura la salida
        this.capturedStream = new PrintStream(new OutputStream() {
            private StringBuilder buffer = new StringBuilder();

            @Override
            public void write(int b) throws IOException {
                buffer.append((char) b);

                // Cuando se encuentra un salto de línea, actualizar el texto área
                if ((char) b == '\n') {
                    String line = buffer.toString();
                    buffer = new StringBuilder();

                    // Actualizar en el Event Dispatch Thread
                    SwingUtilities.invokeLater(() -> {
                        logTextArea.append(line);
                        // Desplazar hacia el final del documento
                        logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
                    });
                }
            }

            @Override
            public void flush() throws IOException {
                if (buffer.length() > 0) {
                    String content = buffer.toString();
                    buffer = new StringBuilder();
                    SwingUtilities.invokeLater(() -> {
                        logTextArea.append(content);
                        logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
                    });
                }
            }
        });
    }

    /**
     * Inicia la captura de System.out y System.err.
     * Redirige ambos flujos al JTextArea.
     */
    public void start() {
        System.setOut(capturedStream);
        System.setErr(capturedStream);
    }

    /**
     * Detiene la captura y restaura los flujos originales.
     */
    public void stop() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    /**
     * Añade un mensaje al log de forma manual.
     * Útil para mensajes que no vienen de System.out/err.
     *
     * @param message el mensaje a añadir
     */
    public void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logTextArea.append(message);
            if (!message.endsWith("\n")) {
                logTextArea.append("\n");
            }
            logTextArea.setCaretPosition(logTextArea.getDocument().getLength());
        });
    }

    /**
     * Limpia el contenido del área de logs.
     */
    public void clear() {
        SwingUtilities.invokeLater(() -> {
            logTextArea.setText("");
        });
    }
}

