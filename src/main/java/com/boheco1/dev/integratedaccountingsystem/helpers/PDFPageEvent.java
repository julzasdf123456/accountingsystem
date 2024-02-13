package com.boheco1.dev.integratedaccountingsystem.helpers;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEvent;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.IOException;

public class PDFPageEvent implements PdfPageEvent {
    private String mctNumber;
    public PDFPageEvent(String mctNumber){
        this.mctNumber = mctNumber;
    }
    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte cb = writer.getDirectContent();
        BaseFont baseFont = null;
        try {
            baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Set font and size for the page number
        cb.setFontAndSize(baseFont, 10);

        // Calculate the x and y coordinates for the page number
        float x = (document.right() + document.left()) / 2;
        float y = document.bottom() - 10;

        // Add the page number to the center bottom of the page
        cb.showTextAligned(Element.ALIGN_CENTER, "MCT No: "+mctNumber +" | Page " + writer.getPageNumber(), x, y, 0);
    }

    @Override
    public void onOpenDocument(PdfWriter pdfWriter, Document document) {

    }

    @Override
    public void onStartPage(PdfWriter writer, Document document) {
        // Not used in this example
    }

    @Override
    public void onChapter(PdfWriter writer, Document document, float paragraphPosition, Paragraph title) {
        // Not used in this example
    }

    @Override
    public void onChapterEnd(PdfWriter writer, Document document, float paragraphPosition) {
        // Not used in this example
    }

    @Override
    public void onSection(PdfWriter writer, Document document, float paragraphPosition, int depth, Paragraph title) {
        // Not used in this example
    }

    @Override
    public void onSectionEnd(PdfWriter writer, Document document, float paragraphPosition) {
        // Not used in this example
    }

    @Override
    public void onGenericTag(PdfWriter pdfWriter, Document document, Rectangle rectangle, String s) {

    }

    @Override
    public void onCloseDocument(PdfWriter writer, Document document) {
        // Not used in this example
    }

    @Override
    public void onParagraph(PdfWriter pdfWriter, Document document, float v) {

    }

    @Override
    public void onParagraphEnd(PdfWriter pdfWriter, Document document, float v) {

    }
}