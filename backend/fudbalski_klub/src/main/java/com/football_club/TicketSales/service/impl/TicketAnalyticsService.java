package com.football_club.TicketSales.service.impl;

import com.football_club.MatchTracking.model.Game;
import com.football_club.MatchTracking.repository.jpa.GameRepository;
import com.football_club.TicketSales.dto.GameAnalyticsDTO;
import com.football_club.TicketSales.dto.TicketTypeAnalyticsDTO;
import com.football_club.TicketSales.dto.ZoneAnalyticsDTO;
import com.football_club.TicketSales.model.Zone;
import com.football_club.TicketSales.repository.PriceChangeLogRepository;
import com.football_club.TicketSales.repository.SeatRepository;
import com.football_club.TicketSales.repository.TicketRepository;
import com.football_club.TicketSales.repository.ZoneRepository;
import com.football_club.TicketSales.service.ITicketAnalyticsService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketAnalyticsService implements ITicketAnalyticsService {

    private final GameRepository gameRepository;
    private final TicketRepository ticketRepository;
    private final SeatRepository seatRepository;
    private final ZoneRepository zoneRepository;
    private final PriceChangeLogRepository priceChangeLogRepository;

    @Override
    @Transactional(readOnly = true)
    public GameAnalyticsDTO getGameAnalytics(Long gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found: " + gameId));

        String gameLabel = game.getHomeClub().getName() + " - " + game.getAwayClub().getName();
        long totalTicketsSold = ticketRepository.countByGameId(gameId);
        BigDecimal totalRevenue = ticketRepository.sumPriceByGameId(gameId);
        long priceChangeCount = priceChangeLogRepository.countByGameId(gameId);

        List<Zone> zones = zoneRepository.findAll();
        long totalSeatsAll = zones.stream().mapToLong(z -> seatRepository.countByZoneId(z.getId())).sum();
        BigDecimal overallOccupancy = totalSeatsAll == 0 ? BigDecimal.ZERO :
                BigDecimal.valueOf(totalTicketsSold)
                        .divide(BigDecimal.valueOf(totalSeatsAll), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP);

        List<ZoneAnalyticsDTO> zoneAnalytics = zones.stream().map(zone -> {
            long total = seatRepository.countByZoneId(zone.getId());
            long sold = ticketRepository.countByGameIdAndSeat_ZoneId(gameId, zone.getId());
            BigDecimal revenue = ticketRepository.sumPriceByGameIdAndZoneId(gameId, zone.getId());
            BigDecimal occupancy = total == 0 ? BigDecimal.ZERO :
                    BigDecimal.valueOf(sold)
                            .divide(BigDecimal.valueOf(total), 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .setScale(2, RoundingMode.HALF_UP);
            return ZoneAnalyticsDTO.builder()
                    .zoneId(zone.getId())
                    .zoneName(zone.getName())
                    .zoneColor(zone.getColor())
                    .totalSeats(total)
                    .ticketsSold(sold)
                    .occupancyPercent(occupancy)
                    .revenue(revenue)
                    .build();
        }).collect(Collectors.toList());

        List<TicketTypeAnalyticsDTO> ticketTypeAnalytics = ticketRepository.countAndSumByTicketType(gameId)
                .stream().map(row -> TicketTypeAnalyticsDTO.builder()
                        .typeName((String) row[0])
                        .ticketsSold((Long) row[1])
                        .revenue((BigDecimal) row[2])
                        .build())
                .collect(Collectors.toList());

        return GameAnalyticsDTO.builder()
                .gameId(gameId)
                .gameLabel(gameLabel)
                .totalTicketsSold(totalTicketsSold)
                .totalRevenue(totalRevenue)
                .overallOccupancyPercent(overallOccupancy)
                .priceChangeCount(priceChangeCount)
                .zoneAnalytics(zoneAnalytics)
                .ticketTypeAnalytics(ticketTypeAnalytics)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<GameAnalyticsDTO> getAllGamesAnalytics() {
        return gameRepository.findAll().stream()
                .map(game -> getGameAnalytics(game.getId()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] generatePdfReport(Long gameId) {
        GameAnalyticsDTO a = getGameAnalytics(gameId);
        try {
            return buildPdf(a);
        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed", e);
        }
    }

    private byte[] buildPdf(GameAnalyticsDTO a) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 36, 36, 36, 54);
        PdfWriter.getInstance(doc, out);
        doc.open();

        Color navyDark   = new Color(26,  26,  46);
        Color blue       = new Color(26,  115, 232);
        Color green      = new Color(46,  125, 50);
        Color lightGray  = new Color(245, 245, 245);
        Color rowBorder  = new Color(220, 220, 220);
        Color tableHdrBg = new Color(40,  40,  70);
        Color mutedGray  = new Color(120, 120, 120);
        Color darkText   = new Color(50,  50,  50);
        Color subtitleC  = new Color(180, 180, 210);
        Color footerC    = new Color(150, 150, 150);

        String enc = "Cp1250";
        Font titleFont    = FontFactory.getFont(FontFactory.HELVETICA_BOLD, enc, false, 20, Font.BOLD,   Color.WHITE);
        Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA,      enc, false, 11, Font.NORMAL, subtitleC);
        Font sectionFont  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, enc, false, 10, Font.BOLD,   navyDark);
        Font tblHdrFont   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, enc, false,  9, Font.BOLD,   Color.WHITE);
        Font cellFont     = FontFactory.getFont(FontFactory.HELVETICA,      enc, false,  9, Font.NORMAL, darkText);
        Font cellBoldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, enc, false,  9, Font.BOLD,   darkText);
        Font statValFont  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, enc, false, 16, Font.BOLD,   blue);
        Font statLblFont  = FontFactory.getFont(FontFactory.HELVETICA,      enc, false,  8, Font.NORMAL, mutedGray);
        Font greenFont    = FontFactory.getFont(FontFactory.HELVETICA_BOLD, enc, false,  9, Font.BOLD,   green);
        Font footerFont   = FontFactory.getFont(FontFactory.HELVETICA,      enc, false,  8, Font.ITALIC, footerC);

        // ── HEADER ──────────────────────────────────────────────────────────
        PdfPTable header = new PdfPTable(1);
        header.setWidthPercentage(100);
        header.setSpacingAfter(14);

        PdfPCell hCell = new PdfPCell();
        hCell.setBackgroundColor(navyDark);
        hCell.setPadding(18);
        hCell.setBorderWidth(0);

        Paragraph titleP = new Paragraph("IZVESTAJ O PRODAJI KARATA", titleFont);
        titleP.setAlignment(Element.ALIGN_CENTER);
        hCell.addElement(titleP);

        Paragraph gameP = new Paragraph(a.getGameLabel(), subtitleFont);
        gameP.setAlignment(Element.ALIGN_CENTER);
        gameP.setSpacingBefore(5);
        hCell.addElement(gameP);

        header.addCell(hCell);
        doc.add(header);

        // ── SUMMARY STATS ────────────────────────────────────────────────────
        addSectionTitle(doc, "UKUPNI PREGLED", sectionFont);

        PdfPTable stats = new PdfPTable(4);
        stats.setWidthPercentage(100);
        stats.setSpacingAfter(14);

        addStatCard(stats, String.valueOf(a.getTotalTicketsSold()), "Prodatih karata", statValFont, statLblFont);
        addStatCard(stats, money(a.getTotalRevenue()) + " RSD", "Ukupan prihod", statValFont, statLblFont);
        addStatCard(stats, pct(a.getOverallOccupancyPercent()), "Popunjenost stadiona", statValFont, statLblFont);
        addStatCard(stats, String.valueOf(a.getPriceChangeCount()), "Promena cena", statValFont, statLblFont);

        doc.add(stats);

        // ── ZONE TABLE ───────────────────────────────────────────────────────
        addSectionTitle(doc, "ANALITIKA PO ZONAMA", sectionFont);

        PdfPTable zoneTable = new PdfPTable(5);
        zoneTable.setWidthPercentage(100);
        zoneTable.setWidths(new float[]{3f, 1.5f, 1.5f, 2f, 2.5f});
        zoneTable.setSpacingAfter(14);

        addTableHeader(zoneTable, tableHdrBg, tblHdrFont,
                "Zona", "Sedista", "Prodato", "Popunjenost", "Prihod (RSD)");

        boolean alt = false;
        for (ZoneAnalyticsDTO z : a.getZoneAnalytics()) {
            Color bg = alt ? lightGray : Color.WHITE;

            PdfPCell nameCell = new PdfPCell();
            nameCell.setBackgroundColor(bg);
            nameCell.setPadding(6);
            nameCell.setBorderColor(rowBorder);

            Chunk dot = new Chunk("   ", cellFont);
            dot.setBackground(parseHex(z.getZoneColor()), 1f, 2f, 1f, 2f);
            Paragraph namePara = new Paragraph();
            namePara.add(dot);
            namePara.add(new Chunk("  " + z.getZoneName(), cellBoldFont));
            nameCell.addElement(namePara);
            zoneTable.addCell(nameCell);

            addCell(zoneTable, String.valueOf(z.getTotalSeats()), bg, cellFont,  Element.ALIGN_CENTER, rowBorder);
            addCell(zoneTable, String.valueOf(z.getTicketsSold()),  bg, cellFont,  Element.ALIGN_CENTER, rowBorder);
            addCell(zoneTable, pct(z.getOccupancyPercent()),        bg, cellFont,  Element.ALIGN_CENTER, rowBorder);
            addCell(zoneTable, money(z.getRevenue()),               bg, greenFont, Element.ALIGN_RIGHT,  rowBorder);

            alt = !alt;
        }
        doc.add(zoneTable);

        // ── TICKET TYPE TABLE ────────────────────────────────────────────────
        addSectionTitle(doc, "ANALITIKA PO TIPU KARTE", sectionFont);

        PdfPTable typeTable = new PdfPTable(3);
        typeTable.setWidthPercentage(100);
        typeTable.setWidths(new float[]{3f, 2f, 3f});
        typeTable.setSpacingAfter(14);

        addTableHeader(typeTable, tableHdrBg, tblHdrFont,
                "Tip karte", "Prodato", "Prihod (RSD)");

        alt = false;
        for (TicketTypeAnalyticsDTO t : a.getTicketTypeAnalytics()) {
            Color bg = alt ? lightGray : Color.WHITE;
            addCell(typeTable, t.getTypeName(),                    bg, cellFont,  Element.ALIGN_LEFT,   rowBorder);
            addCell(typeTable, String.valueOf(t.getTicketsSold()), bg, cellFont,  Element.ALIGN_CENTER, rowBorder);
            addCell(typeTable, money(t.getRevenue()),              bg, greenFont, Element.ALIGN_RIGHT,  rowBorder);
            alt = !alt;
        }
        doc.add(typeTable);

        // ── FOOTER ───────────────────────────────────────────────────────────
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy. u HH:mm"));
        Paragraph footer = new Paragraph("Generisano: " + ts, footerFont);
        footer.setAlignment(Element.ALIGN_RIGHT);
        footer.setSpacingBefore(4);
        doc.add(footer);

        doc.close();
        return out.toByteArray();
    }

    private void addSectionTitle(Document doc, String text, Font font) throws DocumentException {
        Paragraph p = new Paragraph(text, font);
        p.setSpacingBefore(8);
        p.setSpacingAfter(5);
        doc.add(p);
    }

    private void addStatCard(PdfPTable table, String value, String label, Font valFont, Font lblFont) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.WHITE);
        cell.setPadding(12);
        cell.setBorderColor(new Color(220, 220, 220));
        cell.setBorderWidth(1);

        Paragraph val = new Paragraph(value, valFont);
        val.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(val);

        Paragraph lbl = new Paragraph(label, lblFont);
        lbl.setAlignment(Element.ALIGN_CENTER);
        lbl.setSpacingBefore(3);
        cell.addElement(lbl);

        table.addCell(cell);
    }

    private void addTableHeader(PdfPTable table, Color bg, Font font, String... cols) {
        for (String col : cols) {
            PdfPCell cell = new PdfPCell(new Phrase(col, font));
            cell.setBackgroundColor(bg);
            cell.setPadding(8);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorderWidth(0);
            table.addCell(cell);
        }
    }

    private void addCell(PdfPTable table, String text, Color bg, Font font, int align, Color border) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(bg);
        cell.setPadding(6);
        cell.setHorizontalAlignment(align);
        cell.setBorderColor(border);
        table.addCell(cell);
    }

    private String money(BigDecimal value) {
        if (value == null) return "0,00";
        DecimalFormatSymbols s = new DecimalFormatSymbols();
        s.setGroupingSeparator('.');
        s.setDecimalSeparator(',');
        return new DecimalFormat("#,##0.00", s).format(value);
    }

    private String pct(BigDecimal value) {
        if (value == null) return "0,0%";
        return value.setScale(1, RoundingMode.HALF_UP).toPlainString().replace('.', ',') + "%";
    }

    private Color parseHex(String hex) {
        if (hex == null || hex.length() < 6) return new Color(100, 100, 100);
        String h = hex.startsWith("#") ? hex.substring(1) : hex;
        try {
            return new Color(
                    Integer.parseInt(h.substring(0, 2), 16),
                    Integer.parseInt(h.substring(2, 4), 16),
                    Integer.parseInt(h.substring(4, 6), 16));
        } catch (Exception e) {
            return new Color(100, 100, 100);
        }
    }
}
