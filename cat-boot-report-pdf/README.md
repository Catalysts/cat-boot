# Reporting engine

This module gives an easy-to-use abstraction layer of the the PDF library PDFBox to generate simple PDF reports
from Java code.

This module has been extracted from previous projects and wide parts of it are still subject to change, so
interfaces might change.

More documentation will also follow, this module is yet work-in-progress.

## Integration

With Gradle

```
compile('cc.catalysts.boot:cat-boot-report-pdf:' + catBootVersion)
```

## Example

```
final DefaultPdfStyleSheet styleSheet = new DefaultPdfStyleSheet();
styleSheet.setLineDistance(1.1f);
styleSheet.setHeading1Text(new PdfTextStyle(9, PDType1Font.HELVETICA_BOLD, Color.BLACK));

PdfReportService pdfReportService = new PdfReportServiceImpl(styleSheet);
final PdfReportBuilder builder = pdfReportService.createBuilder();

builder
  .addHeading("This is the first heading")
  .addText("Some arbitrary text that can be very very long")
  .startTable()
    .addColumn("COL1", 2).addColumn("COL2", 2).addColumn("COL3", 4)
    .createRow()
    .addValue("val1").addValue("val2").addValue("val3").endRow()
    .createRow().withValues("x1", "x2", "x3")
    .createRow().withValues("y1", "y2", "y3")
    .endTable()
  .withHeaderOnAllPages("Left header", "Middle Header", "Right Header")
  .withFooterOnAllPages("left", "center", "right: " + PdfFooterGenerator.PAGE_TEMPLATE_CURR + "/"
                          + PdfFooterGenerator.PAGE_TEMPLATE_TOTAL);

builder.printToFile(new File(outDirectory, "example-header-footer.pdf"), PdfPageLayout.getPortraitA4Page(), null);
```