package com.football_club.Scouting.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.football_club.Scouting.repository.PlayerReportRepository;
import com.football_club.Scouting.service.IPlayerReportService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.awt.Color;

@Service
@RequiredArgsConstructor
public class PlayerReportService implements IPlayerReportService {

    private final PlayerReportRepository repository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] generatePlayerPdfReport(Long playerId) throws Exception {
        String jsonRaw = repository.getPlayerReportJson(playerId);
        JsonNode root = objectMapper.readTree(jsonRaw);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 54, 36);
        PdfWriter.getInstance(document, out);

        document.open();

        // Fonts styles
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, Color.DARK_GRAY);
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, new Color(37, 99, 235));
        Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
        Font boldBodyFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.BLACK);

        // Header Title
        JsonNode profile = root.path("profile");
        String fullName = profile.path("name").asText() + " " + profile.path("surname").asText();
        Paragraph title = new Paragraph("SKAUTING DOSIJE: " + fullName.toUpperCase(), titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // --- SECTION 1: BIO PROFILE ---
        document.add(new Paragraph("1. Generalne informacije", sectionFont));
        document.add(new Paragraph("Pozicija: " + profile.path("position").asText(), bodyFont));
        document.add(new Paragraph("Datum Rodjenja: " + profile.path("dateOfBirth").asText(), bodyFont));
        document.add(new Paragraph("------------------------------------------------------------------------------------------------------------------------", bodyFont));

        // --- SECTION 2: CAREER TIMELINE ---
        document.add(new Paragraph("2. Hronologija karijere", sectionFont));
        PdfPTable historyTable = new PdfPTable(3);
        historyTable.setWidthPercentage(100);
        historyTable.setSpacingBefore(10);
        historyTable.setSpacingAfter(15);

        historyTable.addCell(new PdfPCell(new Phrase("Klub", boldBodyFont)));
        historyTable.addCell(new PdfPCell(new Phrase("Broj Dresa", boldBodyFont)));
        historyTable.addCell(new PdfPCell(new Phrase("Trajanje ugovora", boldBodyFont)));

        JsonNode history = root.path("history");
        if (history.isArray()) {
            for (JsonNode node : history) {
                historyTable.addCell(new PdfPCell(new Phrase(node.path("clubName").asText("N/A"), bodyFont)));
                historyTable.addCell(new PdfPCell(new Phrase(node.path("jerseyNumber").asText("-"), bodyFont)));
                String term = node.path("contractStart").asText().substring(0, 4) + " - " +
                        (node.path("contractEnd").isNull() ? "Present" : node.path("contractEnd").asText().substring(0, 4));
                historyTable.addCell(new PdfPCell(new Phrase(term, bodyFont)));
            }
        }
        document.add(historyTable);

        // --- SECTION 3: SCOUTING PERFORMANCE ANALYSIS ---
        document.add(new Paragraph("3. Performanse po skauting metrikama", sectionFont));
        JsonNode scouting = root.path("scoutingSummary");
        document.add(new Paragraph("Ukupno evaluacija pronadjeno: " + scouting.path("totalReports").asInt(), bodyFont));

        PdfPTable metricsTable = new PdfPTable(2);
        metricsTable.setWidthPercentage(100);
        metricsTable.setSpacingBefore(10);
        metricsTable.setSpacingAfter(10);
        metricsTable.addCell(new PdfPCell(new Phrase("Kategorija", boldBodyFont)));
        metricsTable.addCell(new PdfPCell(new Phrase("Istorijska srednja vrednost (od 0 do 100)", boldBodyFont)));

        JsonNode averages = scouting.path("categoryAverages");
        averages.fields().forEachRemaining(entry -> {
            metricsTable.addCell(new PdfPCell(new Phrase(entry.getKey(), bodyFont)));
            metricsTable.addCell(new PdfPCell(new Phrase(entry.getValue().asText(), bodyFont)));
        });
        document.add(metricsTable);

        Paragraph remarksHeader = new Paragraph("Poslednje taktičke primedbe:", boldBodyFont);
        remarksHeader.setSpacingBefore(5);
        document.add(remarksHeader);
        Paragraph remarksText = new Paragraph("\"" + scouting.path("latestCommentary").asText("Poslednje takticke primedbe nisu pronadjene.") + "\"", bodyFont);
        remarksText.setIndentationLeft(15);
        remarksText.setSpacingAfter(20);
        document.add(remarksText);

        // --- SECTION 4: RECENT GAME PERFORMANCES ---
        document.add(new Paragraph("4. Performanse na poslednjim utakmicama", sectionFont));

        PdfPTable gamesTable = new PdfPTable(3);
        gamesTable.setWidthPercentage(100);
        gamesTable.setSpacingBefore(10);
        gamesTable.setSpacingAfter(15);
        gamesTable.setWidths(new float[]{4.5f, 2.0f, 3.5f});

        gamesTable.addCell(new PdfPCell(new Phrase("Utakmica", boldBodyFont)));
        gamesTable.addCell(new PdfPCell(new Phrase("Datum", boldBodyFont)));
        gamesTable.addCell(new PdfPCell(new Phrase("Zabelezene metrike", boldBodyFont)));

        JsonNode recentGames = root.path("recentGames");
        if (recentGames.isArray()) {
            for (JsonNode gameNode : recentGames) {
                gamesTable.addCell(new PdfPCell(new Phrase(gameNode.path("matchInfo").asText("N/A"), bodyFont)));

                String matchDate = gameNode.path("matchDate").asText("N/A");
                if (matchDate.length() > 10) {
                    matchDate = matchDate.substring(0, 10);
                }
                gamesTable.addCell(new PdfPCell(new Phrase(matchDate, bodyFont)));

                PdfPCell metricsCell = new PdfPCell();
                JsonNode gameMetrics = gameNode.path("metrics");

                if (gameMetrics.isArray() && gameMetrics.size() > 0) {
                    for (JsonNode metricNode : gameMetrics) {
                        String metricName = metricNode.path("metricName").asText("N/A");
                        String metricValue = metricNode.path("value").asText("0");

                        Paragraph metricRow = new Paragraph(metricName + ": " + metricValue, bodyFont);
                        metricsCell.addElement(metricRow);
                    }
                } else {
                    metricsCell.addElement(new Paragraph("Nema unetih metrika", bodyFont));
                }
                gamesTable.addCell(metricsCell);
            }
        }
        document.add(gamesTable);

        document.close();
        return out.toByteArray();
    }
}