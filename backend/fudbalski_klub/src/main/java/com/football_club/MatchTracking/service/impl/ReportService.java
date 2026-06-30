package com.football_club.MatchTracking.service.impl;

import com.football_club.MatchTracking.repository.jpa.AppearanceRepository;
import com.football_club.MatchTracking.service.IReportService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService implements IReportService {
    private final AppearanceRepository appearanceRepository;

    @Override
    public byte[] generateMatchReportPdf(Long gameId) {
        List<Object[]> reportData = appearanceRepository.getAdvancedMatchReportData(gameId);

        try(ByteArrayOutputStream out = new ByteArrayOutputStream()){
            Document document = new Document(PageSize.A4, 36, 36, 50, 36);
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 11, Color.DARK_GRAY);

            Paragraph title = new Paragraph("Izvestaj o performansama igraca", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10);
            document.add(title);

            Paragraph subtitle = new Paragraph("Utakmica ID: " + gameId, bodyFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(30);
            document.add(subtitle);

            PdfPTable table = new PdfPTable(8); // Sada imamo 8 kolona
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2.5f, 2f, 1.2f, 1f, 1f, 1.2f, 2f, 1.8f});

            String[] headers = {"Igrac", "Uloga", "Min", "Gol", "Asist", "Ocena", "+/- Tim", "Risk Indeks"};
            for (String headerTitle : headers) {
                PdfPCell header = new PdfPCell(new Phrase(headerTitle, headerFont));
                header.setBackgroundColor(new Color(31, 58, 86)); // Tamno plava moderna nijansa
                header.setHorizontalAlignment(Element.ALIGN_CENTER);
                header.setPadding(8);
                table.addCell(header);
            }

            for (Object[] row : reportData) {
                table.addCell(createCell(row[0] != null ? row[0].toString() : "-", bodyFont));
                table.addCell(createCell(row[1] != null ? row[1].toString() : "-", bodyFont));
                table.addCell(createCell(row[2] != null ? row[2].toString() : "0", bodyFont));
                table.addCell(createCell(row[3] != null ? row[3].toString() : "0", bodyFont));
                table.addCell(createCell(row[4] != null ? row[4].toString() : "0", bodyFont));
                table.addCell(createCell(row[5] != null ? row[5].toString() : "0.0", bodyFont));

                String deviation = row[6] != null ? row[6].toString() : "0.0";
                double val = Double.parseDouble(deviation);
                Color tColor = val >= 0 ? new Color(0, 128, 64) : Color.RED;
                table.addCell(createCell(val >= 0 ? "+" + deviation : deviation, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, tColor)));

                table.addCell(createCell(row[7] != null ? row[7].toString() : "0", bodyFont));
            }

            document.add(table);
            document.close();

            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private PdfPCell createCell(String content, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(content, font));
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBorderColor(Color.LIGHT_GRAY);
        return cell;
    }
}
