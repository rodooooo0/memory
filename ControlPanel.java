 import java.applet.*;
import java.awt.*;
import java.util.*;

public class ControlPanel extends Frame {
    Kernel kernel;

    // Conservamos las variables que usabas para botones de control
    Button runButton = new Button("run");
    Button stepButton = new Button("step");
    Button resetButton = new Button("reset");
    Button exitButton = new Button("exit");

    // Reemplazo mas compacto para los 64 botones/labels:
    private static final int PAGE_COUNT = 64;
    Button[] pageButtons = new Button[PAGE_COUNT];
    Label[] physicalLabels = new Label[PAGE_COUNT];

    // Labels de estado (con nombres identicos a los tuyos)
    Label statusValueLabel = new Label("STOP", Label.LEFT);
    Label timeValueLabel = new Label("0", Label.LEFT);
    Label instructionValueLabel = new Label("NONE", Label.LEFT);
    Label addressValueLabel = new Label("NULL", Label.LEFT);
    Label pageFaultValueLabel = new Label("NO", Label.LEFT);
    Label virtualPageValueLabel = new Label("x", Label.LEFT);
    Label physicalPageValueLabel = new Label("0", Label.LEFT);
    Label RValueLabel = new Label("0", Label.LEFT);
    Label MValueLabel = new Label("0", Label.LEFT);
    Label inMemTimeValueLabel = new Label("0", Label.LEFT);
    Label lastTouchTimeValueLabel = new Label("0", Label.LEFT);
    Label lowValueLabel = new Label("0", Label.LEFT);
    Label highValueLabel = new Label("0", Label.LEFT);

    // Segmento y pagina dentro del segmento
    Label segmentLabel = new Label("", Label.LEFT);
    Label pageInSegmentLabel = new Label("", Label.LEFT);

    public ControlPanel() {
        super();
    }

    public ControlPanel(String title) {
        super(title);
    }

    public void init(Kernel useKernel, String commands, String config) {
        kernel = useKernel;
        kernel.setControlPanel(this);

        setLayout(null);
        setBackground(Color.white);
        setForeground(Color.black);
        resize(635, 545);
        setFont(new Font("Courier", 0, 12));

        // Configurar botones de control con helper
        configureControlButton(runButton, 0, 25);
        configureControlButton(stepButton, 70, 25);
        configureControlButton(resetButton, 140, 25);
        configureControlButton(exitButton, 210, 25);

        // Crear y posicionar los botones de pagina (2 columnas de 32)
        int colX[] = {0, 140}; // x para columna 0 y columna 1
        for (int i = 0; i < PAGE_COUNT; i++) {
            int col = (i < 32) ? 0 : 1;
            int row = (i % 32);
            Button b = new Button("page " + i);
            b.setForeground(Color.magenta);
            b.setBackground(Color.lightGray);
            b.setBounds(colX[col], (row + 2) * 15 + 25, 70, 15);
            pageButtons[i] = b;
            add(b);
        }

        // Crear labels que muestran el mapeo fisico
        for (int i = 0; i < PAGE_COUNT; i++) {
            int x = (i < 32) ? 70 : 210;
            int row = (i % 32);
            Label l = new Label("", Label.CENTER);
            l.setForeground(Color.red);
            l.setFont(new Font("Courier", Font.PLAIN, 10));
            l.setBounds(x, (row + 2) * 15 + 25, 60, 15);
            physicalLabels[i] = l;
            add(l);
        }

        // Labels de estado en panel derecho
        statusValueLabel.setBounds(345, 0 + 25, 100, 15);
        add(statusValueLabel);

        timeValueLabel.setBounds(345, 15 + 25, 100, 15);
        add(timeValueLabel);

        instructionValueLabel.setBounds(385, 45 + 25, 100, 15);
        add(instructionValueLabel);

        addressValueLabel.setBounds(385, 60 + 25, 230, 15);
        add(addressValueLabel);

        pageFaultValueLabel.setBounds(385, 90 + 25, 100, 15);
        add(pageFaultValueLabel);

        virtualPageValueLabel.setBounds(395, 120 + 25, 200, 15);
        add(virtualPageValueLabel);

        physicalPageValueLabel.setBounds(395, 135 + 25, 200, 15);
        add(physicalPageValueLabel);

        RValueLabel.setBounds(395, 150 + 25, 200, 15);
        add(RValueLabel);

        MValueLabel.setBounds(395, 165 + 25, 200, 15);
        add(MValueLabel);

        inMemTimeValueLabel.setBounds(395, 180 + 25, 200, 15);
        add(inMemTimeValueLabel);

        lastTouchTimeValueLabel.setBounds(395, 195 + 25, 200, 15);
        add(lastTouchTimeValueLabel);

        lowValueLabel.setBounds(395, 210 + 25, 230, 15);
        add(lowValueLabel);

        highValueLabel.setBounds(395, 225 + 25, 230, 15);
        add(highValueLabel);

        // Titulos encima de columnas virtual/physical
        Label virtualOneLabel = new Label("virtual", Label.CENTER);
        virtualOneLabel.setBounds(0, 15 + 25, 70, 15);
        add(virtualOneLabel);

        Label virtualTwoLabel = new Label("virtual", Label.CENTER);
        virtualTwoLabel.setBounds(140, 15 + 25, 70, 15);
        add(virtualTwoLabel);

        Label physicalOneLabel = new Label("physical", Label.CENTER);
        physicalOneLabel.setBounds(70, 15 + 25, 70, 15);
        add(physicalOneLabel);

        Label physicalTwoLabel = new Label("physical", Label.CENTER);
        physicalTwoLabel.setBounds(210, 15 + 25, 70, 15);
        add(physicalTwoLabel);

        // Labels fijos (etiquetas a la izquierda del bloque de estado)
        addFixedLabel("status:", 285, 0 + 25, 65, 15);
        addFixedLabel("time:", 285, 15 + 25, 50, 15);
        addFixedLabel("instruction:", 285, 45 + 25, 100, 15);
        addFixedLabel("address:", 285, 60 + 25, 85, 15);
        addFixedLabel("page fault:", 285, 90 + 25, 100, 15);
        addFixedLabel("virtual page:", 285, 120 + 25, 110, 15);
        addFixedLabel("physical page:", 285, 135 + 25, 110, 15);
        addFixedLabel("R:", 285, 150 + 25, 110, 15);
        addFixedLabel("M:", 285, 165 + 25, 110, 15);
        addFixedLabel("inMemTime:", 285, 180 + 25, 110, 15);
        addFixedLabel("lastTouchTime:", 285, 195 + 25, 110, 15);
        addFixedLabel("low:", 285, 210 + 25, 110, 15);
        addFixedLabel("high:", 285, 225 + 25, 110, 15);

        // Segmento + pagina dentro del segmento (nueva etiqueta)
        segmentLabel.setBounds(395, 240 + 25, 150, 15);
        segmentLabel.setFont(new Font("Courier", Font.BOLD, 14));
        add(segmentLabel);

        pageInSegmentLabel.setBounds(395, 260 + 25, 150, 15);
        pageInSegmentLabel.setFont(new Font("Courier", Font.PLAIN, 12));
        add(pageInSegmentLabel);

        // Colorear botones segun segmentos (colores simples)
        Color[] segmentColors = {
            new Color(255, 200, 200), // S1
            new Color(200, 255, 200), // S2
            new Color(200, 200, 255), // S3
            new Color(255, 255, 200), // S4
            new Color(200, 255, 255)  // S5
        };

        // Aplicar color a cada boton segun su segmento
        for (int i = 0; i < PAGE_COUNT; i++) {
            int seg = findSegmentForPage(i);
            Color c = (seg >= 0 && seg < segmentColors.length) ? segmentColors[seg] : Color.lightGray;
            pageButtons[i].setBackground(c);
        }

        // Finalmente, inicializar kernel (igual que antes)
        kernel.init(commands, config);

        show();
    }

    // Helper para configurar botones de control (evita repetir)
    private void configureControlButton(Button b, int x, int y) {
        b.setForeground(Color.blue);
        b.setBackground(Color.lightGray);
        b.setBounds(x, y, 70, 15);
        add(b);
    }

    private void addFixedLabel(String text, int x, int y, int w, int h) {
        Label l = new Label(text, Label.LEFT);
        l.setBounds(x, y, w, h);
        add(l);
    }

    // Busca el segmento para una pagina (mismo comportamiento que Kernel.SEGMENTS)
    private int findSegmentForPage(int pageNum) {
        for (int s = 0; s < Kernel.SEGMENTS.length; s++) {
            for (int p = 0; p < Kernel.SEGMENTS[s].length; p++) {
                if (Kernel.SEGMENTS[s][p] == pageNum) return s;
            }
        }
        return -1;
    }

    public void paintPage(Page page) {
        if (page == null) return;
        virtualPageValueLabel.setText(Integer.toString(page.id));
        physicalPageValueLabel.setText(Integer.toString(page.physical));
        RValueLabel.setText(Integer.toString(page.R));
        MValueLabel.setText(Integer.toString(page.M));
        inMemTimeValueLabel.setText(Integer.toString(page.inMemTime));
        lastTouchTimeValueLabel.setText(Integer.toString(page.lastTouchTime));
        lowValueLabel.setText(Long.toString(page.low, Kernel.addressradix));
        highValueLabel.setText(Long.toString(page.high, Kernel.addressradix));

        int segnum = kernel.getSegmentForPage(page.id);
        if (segnum != -1) {
            segmentLabel.setText("Segmento: S" + (segnum + 1));
        } else {
            segmentLabel.setText("Sin segmento");
        }

        pageInSegmentLabel.setText("Pagina: " + page.id);
    }

    public void setStatus(String status) {
        statusValueLabel.setText(status);
    }

    // A ade texto en la etiqueta fisica correspondiente, si el indice es valido.
    public void addPhysicalPage(int pageNum, int physicalPage) {
        if (physicalPage >= 0 && physicalPage < physicalLabels.length) {
            physicalLabels[physicalPage].setText("page " + pageNum);
        }
    }

    // Limpia la etiqueta fisica correspondiente, si el indice es valido.
    public void removePhysicalPage(int physicalPage) {
        if (physicalPage >= 0 && physicalPage < physicalLabels.length) {
            physicalLabels[physicalPage].setText(null);
        }
    }

    // Manejo de eventos simplificado: detecta botones de control y botones de pagina por arreglo
    public boolean action(Event e, Object arg) {
        Object target = e.target;

        if (target == runButton) {
            setStatus("RUN");
            runButton.disable();
            stepButton.disable();
            resetButton.disable();
            // Usar start() en Kernel si esta implementado como Thread; si tu Kernel
            // usa run() directamente puedes cambiarlo, aqui se respeta tu antigua llamada
            try {
                kernel.run();
            } catch (Throwable t) {
                // si kernel es Thread real podria usar kernel.start(); dejamos sin cambios de logica
            }
            setStatus("STOP");
            resetButton.enable();
            return true;
        } else if (target == stepButton) {
            setStatus("STEP");
            kernel.step();
            if (kernel.runcycles == kernel.runs) {
                stepButton.disable();
                runButton.disable();
            }
            setStatus("STOP");
            return true;
        } else if (target == resetButton) {
            kernel.reset();
            runButton.enable();
            stepButton.enable();
            return true;
        } else if (target == exitButton) {
            System.exit(0);
            return true;
        } else {
            // buscar si es alguno de los botones de pagina
            for (int i = 0; i < pageButtons.length; i++) {
                if (target == pageButtons[i]) {
                    kernel.getPage(i);
                    return true;
                }
            }
        }

        return false;
    }
}
