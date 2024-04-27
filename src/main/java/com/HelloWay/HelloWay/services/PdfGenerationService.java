package com.HelloWay.HelloWay.services;

import com.HelloWay.HelloWay.entities.User;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;


@Service
public class PdfGenerationService {

    @Autowired
    CommandService commandService;

    public ByteArrayInputStream generatePdf(List<User> serverDataList) throws DocumentException {
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, outputStream);

        document.open();

        PdfPTable table = new PdfPTable(4);
        addTableHeader(table);
        addRows(table, serverDataList);

        document.add(table);
        document.close();

        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    private void addTableHeader(PdfPTable table) {
        table.addCell("Server Name");
        table.addCell("Date");
        table.addCell("CommandsCount");
        table.addCell("Sum");
    }

    private void addRows(PdfPTable table, List<User> servers) {
        for (User server : servers) {
            table.addCell(server.getName());
            table.addCell(LocalDate.now().toString());
            table.addCell(Integer.toString(commandService.getServerCommandsCountPerDay(server, LocalDate.now())));
            table.addCell(Double.toString(commandService.getServerSumCommandsPerDay(server, LocalDate.now())));
        }
    }
}
