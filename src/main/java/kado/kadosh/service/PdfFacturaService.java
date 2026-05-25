package kado.kadosh.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import kado.kadosh.entities.DetalleFactura;
import kado.kadosh.entities.Factura;
import kado.kadosh.entities.Persona;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PdfFacturaService {

    // Datos de la empresa
    private static final String EMPRESA_NOMBRE = "KADOSH ÓPTICA";
    private static final String EMPRESA_RUC    = "RUC: 20601847392";
    private static final String EMPRESA_SLOGAN = "Óptica & Salud Visual";
    private static final String EMPRESA_EMAIL  = "sistemas.kadosh@gmail.com";
    private static final String EMPRESA_WEB    = "www.kadosh-optica.com";

    // Paleta morada principal
    private static final Color MORADO        = new Color(137, 26, 171);
    private static final Color MORADO_OSCURO = new Color(90,  10, 120);
    private static final Color MORADO_CLARO  = new Color(220, 180, 235);
    private static final Color MORADO_FONDO  = new Color(250, 243, 255);
    private static final Color GRIS_FILA     = new Color(248, 248, 248);
    private static final Color GRIS_TEXTO    = new Color(90,  90,  90);
    private static final Color NEGRO         = new Color(30,  30,  30);
    private static final Color VERDE         = new Color(39,  174,  96);
    private static final Color ROJO          = new Color(192,  57,  43);

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ── Punto de entrada ──────────────────────────────────────────────────────

    public byte[] generar(Factura factura) {
        try {
            Document doc = new Document(PageSize.A4, 36, 36, 36, 36);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter.getInstance(doc, out);
            doc.open();

            seccionEncabezado(doc);
            gap(doc, 8);
            seccionInfoBoxes(doc, factura);
            gap(doc, 12);
            seccionCliente(doc, factura);
            gap(doc, 12);
            seccionItems(doc, factura);
            gap(doc, 6);
            seccionTotales(doc, factura);
            gap(doc, 16);
            seccionPie(doc);

            doc.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF: " + e.getMessage(), e);
        }
    }

    // ── Secciones ─────────────────────────────────────────────────────────────

    private void seccionEncabezado(Document doc) throws DocumentException {
        PdfPTable t = new PdfPTable(2);
        t.setWidthPercentage(100);
        t.setWidths(new float[]{55f, 45f});

        // Columna izquierda: empresa
        PdfPCell izq = libre();
        izq.setPaddingBottom(4);
        Paragraph empresa = new Paragraph(EMPRESA_NOMBRE, f(FontFactory.HELVETICA_BOLD, 24, MORADO));
        empresa.setSpacingAfter(3);
        izq.addElement(empresa);
        izq.addElement(new Paragraph(EMPRESA_SLOGAN, f(FontFactory.HELVETICA, 10, GRIS_TEXTO)));
        izq.addElement(new Paragraph(EMPRESA_RUC,    f(FontFactory.HELVETICA_BOLD, 9, MORADO_OSCURO)));
        izq.addElement(new Paragraph(EMPRESA_EMAIL,  f(FontFactory.HELVETICA, 9, GRIS_TEXTO)));
        t.addCell(izq);

        // Columna derecha: "FACTURA" grande alineado a la derecha
        PdfPCell der = libre();
        der.setHorizontalAlignment(Element.ALIGN_RIGHT);
        Paragraph tit = new Paragraph("FACTURA", f(FontFactory.HELVETICA_BOLD, 30, MORADO));
        tit.setAlignment(Element.ALIGN_RIGHT);
        der.addElement(tit);
        t.addCell(der);

        doc.add(t);

        // Línea morada bajo el encabezado
        doc.add(lineaSeparadora(MORADO, 2.5f));
    }

    private void seccionInfoBoxes(Document doc, Factura factura) throws DocumentException {
        PdfPTable t = new PdfPTable(4);
        t.setWidthPercentage(100);
        t.setWidths(new float[]{28f, 24f, 24f, 24f});

        Font fLbl = f(FontFactory.HELVETICA_BOLD, 7, Color.WHITE);
        Font fVal = f(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
        Font fLbl2 = f(FontFactory.HELVETICA_BOLD, 7, MORADO_OSCURO);
        Font fVal2 = f(FontFactory.HELVETICA_BOLD, 10, NEGRO);

        String fecha = factura.getFechaEmision() != null ? factura.getFechaEmision().format(FMT) : "-";

        t.addCell(boxColor("N.º DE FACTURA", factura.getNumero() != null ? factura.getNumero() : "-",
                fLbl, fVal, MORADO));
        t.addCell(boxColor("FECHA DE EMISIÓN", fecha,
                fLbl, fVal, MORADO_OSCURO));
        t.addCell(boxSuave("CONDICIONES", "Contado",
                fLbl2, fVal2, MORADO_FONDO));

        Color estadoCol = "EMITIDA".equals(factura.getEstado()) ? VERDE : ROJO;
        t.addCell(boxColor("ESTADO", factura.getEstado() != null ? factura.getEstado() : "-",
                fLbl, fVal, estadoCol));

        doc.add(t);
    }

    private void seccionCliente(Document doc, Factura factura) throws DocumentException {
        // Título sección
        PdfPTable tit = new PdfPTable(1);
        tit.setWidthPercentage(100);
        PdfPCell tc = new PdfPCell(new Phrase("FACTURAR A:", f(FontFactory.HELVETICA_BOLD, 9, MORADO)));
        tc.setBorder(Rectangle.NO_BORDER);
        tc.setBorderWidthBottom(1.5f);
        tc.setBorderColorBottom(MORADO_CLARO);
        tc.setPaddingBottom(5);
        tit.addCell(tc);
        doc.add(tit);

        gap(doc, 5);

        String nombre = "-", dni = "-", telefono = "-", email = "-";
        if (factura.getUsuario() != null && factura.getUsuario().getPersona() != null) {
            Persona p = factura.getUsuario().getPersona();
            nombre = p.getNombre() + " " + p.getApellido();
            if (p.getDni()      != null) dni      = p.getDni();
            if (p.getTelefono() != null) telefono = p.getTelefono();
            if (p.getEmail()    != null) email    = p.getEmail();
        }

        Font lbl = f(FontFactory.HELVETICA_BOLD, 9, GRIS_TEXTO);
        Font val = f(FontFactory.HELVETICA,      9, NEGRO);

        PdfPTable datos = new PdfPTable(4);
        datos.setWidthPercentage(100);
        datos.setWidths(new float[]{18f, 38f, 18f, 26f});

        datoCell(datos, "Nombre:", nombre,   lbl, val);
        datoCell(datos, "DNI:",    dni,      lbl, val);
        datoCell(datos, "Tel.:",   telefono, lbl, val);
        datoCell(datos, "Email:",  email,    lbl, val);

        doc.add(datos);
    }

    private void seccionItems(Document doc, Factura factura) throws DocumentException {
        Font thFont = f(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
        Font tdFont = f(FontFactory.HELVETICA,      9, NEGRO);
        Font tdVacio = f(FontFactory.HELVETICA,     9, new Color(230, 220, 235));

        PdfPTable tabla = new PdfPTable(4);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{48f, 14f, 18f, 20f});

        // Cabecera morada
        thCell(tabla, "DESCRIPCIÓN",       thFont, Element.ALIGN_LEFT);
        thCell(tabla, "CANTIDAD",          thFont, Element.ALIGN_CENTER);
        thCell(tabla, "PRECIO UNITARIO",   thFont, Element.ALIGN_RIGHT);
        thCell(tabla, "MONTO",             thFont, Element.ALIGN_RIGHT);

        List<DetalleFactura> items = factura.getDetalles() == null ? List.of() :
                factura.getDetalles().stream()
                        .filter(d -> Boolean.TRUE.equals(d.getActivo()))
                        .collect(Collectors.toList());

        boolean par = false;
        for (DetalleFactura d : items) {
            Color bg    = par ? GRIS_FILA : Color.WHITE;
            String nom  = d.getProducto() != null ? d.getProducto().getNombre()
                        : (d.getDescripcion() != null ? d.getDescripcion() : "-");
            String pu   = d.getPrecioUnitario() != null ? String.format("%.2f", d.getPrecioUnitario()) : "-";
            String sub  = d.getSubtotal()       != null ? String.format("%.2f", d.getSubtotal())       : "-";
            tdCell(tabla, nom,                            tdFont, bg, Element.ALIGN_LEFT);
            tdCell(tabla, String.valueOf(d.getCantidad()), tdFont, bg, Element.ALIGN_CENTER);
            tdCell(tabla, pu,                             tdFont, bg, Element.ALIGN_RIGHT);
            tdCell(tabla, sub,                            tdFont, bg, Element.ALIGN_RIGHT);
            par = !par;
        }

        // Filas vacías de relleno (mínimo 7 filas visibles como en la imagen)
        for (int i = items.size(); i < 7; i++) {
            Color bg = (i % 2 == 0) ? Color.WHITE : GRIS_FILA;
            tdCell(tabla, "  ", tdVacio, bg, Element.ALIGN_LEFT);
            tdCell(tabla, "  ", tdVacio, bg, Element.ALIGN_CENTER);
            tdCell(tabla, "  ", tdVacio, bg, Element.ALIGN_RIGHT);
            tdCell(tabla, "  ", tdVacio, bg, Element.ALIGN_RIGHT);
        }

        doc.add(tabla);
    }

    private void seccionTotales(Document doc, Factura factura) throws DocumentException {
        Font lbl   = f(FontFactory.HELVETICA_BOLD, 9, GRIS_TEXTO);
        Font val   = f(FontFactory.HELVETICA,      9, NEGRO);
        Font total = f(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);

        PdfPTable t = new PdfPTable(3);
        t.setWidthPercentage(52);
        t.setHorizontalAlignment(Element.ALIGN_RIGHT);
        t.setWidths(new float[]{38f, 32f, 30f});

        totalFila(t, "SUBTOTAL:",  fmt(factura.getSubtotal()), lbl, val, Color.WHITE);
        totalFila(t, "IGV (18%):", fmt(factura.getIgv()),      lbl, val, MORADO_FONDO);

        // Fila TOTAL — morada y grande
        PdfPCell sp = libre();
        sp.setBackgroundColor(Color.WHITE);
        t.addCell(sp);

        PdfPCell tLbl = new PdfPCell(new Phrase("TOTAL", total));
        tLbl.setBackgroundColor(MORADO);
        tLbl.setBorder(Rectangle.NO_BORDER);
        tLbl.setPadding(9);
        tLbl.setHorizontalAlignment(Element.ALIGN_RIGHT);
        t.addCell(tLbl);

        String totalStr = "S/ " + (factura.getTotal() != null
                ? String.format("%.2f", factura.getTotal()) : "0.00");
        PdfPCell tVal = new PdfPCell(new Phrase(totalStr, total));
        tVal.setBackgroundColor(MORADO);
        tVal.setBorder(Rectangle.NO_BORDER);
        tVal.setPadding(9);
        tVal.setHorizontalAlignment(Element.ALIGN_RIGHT);
        t.addCell(tVal);

        doc.add(t);
    }

    private void seccionPie(Document doc) throws DocumentException {
        doc.add(lineaSeparadora(MORADO_CLARO, 1f));
        gap(doc, 6);

        PdfPTable pie = new PdfPTable(2);
        pie.setWidthPercentage(100);
        pie.setWidths(new float[]{50f, 50f});

        PdfPCell izq = libre();
        Paragraph gracias = new Paragraph("GRACIAS", f(FontFactory.HELVETICA_BOLD, 16, MORADO));
        gracias.setSpacingAfter(2);
        izq.addElement(gracias);
        izq.addElement(new Paragraph("Que tengas un excelente día.",
                f(FontFactory.HELVETICA, 9, GRIS_TEXTO)));
        pie.addCell(izq);

        PdfPCell der = libre();
        der.setHorizontalAlignment(Element.ALIGN_RIGHT);
        Paragraph contacto = new Paragraph(
                EMPRESA_EMAIL + "  ·  " + EMPRESA_NOMBRE,
                f(FontFactory.HELVETICA, 8, GRIS_TEXTO));
        contacto.setAlignment(Element.ALIGN_RIGHT);
        der.addElement(contacto);
        Paragraph web = new Paragraph(EMPRESA_WEB,
                f(FontFactory.HELVETICA, 8, MORADO));
        web.setAlignment(Element.ALIGN_RIGHT);
        der.addElement(web);
        pie.addCell(der);

        doc.add(pie);
    }

    // ── Fábricas de celdas ────────────────────────────────────────────────────

    private PdfPCell boxColor(String label, String valor, Font fLbl, Font fVal, Color bg) {
        PdfPCell c = new PdfPCell();
        c.setBackgroundColor(bg);
        c.setBorder(Rectangle.NO_BORDER);
        c.setPadding(8);
        Paragraph p = new Paragraph(label, fLbl);
        p.setSpacingAfter(2);
        c.addElement(p);
        c.addElement(new Paragraph(valor, fVal));
        return c;
    }

    private PdfPCell boxSuave(String label, String valor, Font fLbl, Font fVal, Color bg) {
        PdfPCell c = new PdfPCell();
        c.setBackgroundColor(bg);
        c.setBorder(Rectangle.NO_BORDER);
        c.setBorderWidthLeft(2f);
        c.setBorderColorLeft(MORADO_CLARO);
        c.setPadding(8);
        Paragraph p = new Paragraph(label, fLbl);
        p.setSpacingAfter(2);
        c.addElement(p);
        c.addElement(new Paragraph(valor, fVal));
        return c;
    }

    private void datoCell(PdfPTable t, String label, String valor, Font fLbl, Font fVal) {
        PdfPCell lc = new PdfPCell(new Phrase(label, fLbl));
        lc.setBorder(Rectangle.NO_BORDER);
        lc.setPadding(4);
        lc.setPaddingRight(8);
        lc.setHorizontalAlignment(Element.ALIGN_RIGHT);
        t.addCell(lc);

        PdfPCell vc = new PdfPCell(new Phrase(valor, fVal));
        vc.setBorder(Rectangle.NO_BORDER);
        vc.setBorderWidthBottom(0.5f);
        vc.setBorderColorBottom(new Color(220, 200, 230));
        vc.setPadding(4);
        t.addCell(vc);
    }

    private void thCell(PdfPTable t, String txt, Font font, int align) {
        PdfPCell c = new PdfPCell(new Phrase(txt, font));
        c.setBackgroundColor(MORADO);
        c.setBorder(Rectangle.NO_BORDER);
        c.setPadding(7);
        c.setPaddingLeft(8);
        c.setHorizontalAlignment(align);
        t.addCell(c);
    }

    private void tdCell(PdfPTable t, String txt, Font font, Color bg, int align) {
        PdfPCell c = new PdfPCell(new Phrase(txt, font));
        c.setBackgroundColor(bg);
        c.setBorder(Rectangle.NO_BORDER);
        c.setBorderWidthBottom(0.3f);
        c.setBorderColorBottom(new Color(225, 210, 230));
        c.setPadding(6);
        c.setPaddingLeft(8);
        c.setHorizontalAlignment(align);
        t.addCell(c);
    }

    private void totalFila(PdfPTable t, String label, String valor,
                           Font fLbl, Font fVal, Color bg) {
        PdfPCell sp = libre();
        sp.setBackgroundColor(bg);
        t.addCell(sp);

        PdfPCell lc = new PdfPCell(new Phrase(label, fLbl));
        lc.setBackgroundColor(bg);
        lc.setBorder(Rectangle.NO_BORDER);
        lc.setPadding(6);
        lc.setHorizontalAlignment(Element.ALIGN_RIGHT);
        t.addCell(lc);

        PdfPCell vc = new PdfPCell(new Phrase(valor, fVal));
        vc.setBackgroundColor(bg);
        vc.setBorder(Rectangle.NO_BORDER);
        vc.setPadding(6);
        vc.setHorizontalAlignment(Element.ALIGN_RIGHT);
        t.addCell(vc);
    }

    private PdfPTable lineaSeparadora(Color color, float grosor) {
        PdfPTable t = new PdfPTable(1);
        t.setWidthPercentage(100);
        PdfPCell c = new PdfPCell(new Phrase(" "));
        c.setBorder(Rectangle.NO_BORDER);
        c.setBorderWidthBottom(grosor);
        c.setBorderColorBottom(color);
        c.setPadding(0);
        c.setPaddingTop(2);
        t.addCell(c);
        return t;
    }

    private PdfPCell libre() {
        PdfPCell c = new PdfPCell();
        c.setBorder(Rectangle.NO_BORDER);
        return c;
    }

    private Font f(String nombre, float size, Color color) {
        return FontFactory.getFont(nombre, size, color);
    }

    private void gap(Document doc, float pts) throws DocumentException {
        Paragraph p = new Paragraph(" ");
        p.setSpacingBefore(pts);
        doc.add(p);
    }

    private String fmt(Double v) {
        return "S/ " + (v != null ? String.format("%.2f", v) : "0.00");
    }
}
