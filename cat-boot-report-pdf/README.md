# Reporting engine

This module gives an easy-to-use abstraction layer of the the PDF library PDFBox to generate simple PDF reports
from Java code.

This module has been extracted from previous projects and wide parts of it are still subject to change, so
interfaces might change.


## Example

Which framework would you use if you had the requirement to create a PDF report like this: 

![ReportScreenshot](https://github.com/Catalysts/cat-boot/tree/master/cat-boot-report-pdf/reportScreenshot.png)

How much code and configuration files would you expect?

Well, it's as simple as those few lines of code:

```java
// these objects can also be injected via Spring:
final PdfReportService pdfReportService = new PdfReportServiceImpl(new DefaultPdfStyleSheet());
final PdfReportFilePrinter pdfReportFilePrinter = new PdfReportFilePrinter();

DefaultPdfStyleSheet styleSheet = new DefaultPdfStyleSheet();
styleSheet.setBodyText(new PdfTextStyle(10, PDType1Font.HELVETICA, Color.BLACK));

final PdfReport pdfReport = pdfReportService.createBuilder(styleSheet)
        .addHeading("Dear Github users")
        .addText("In this simple showcase you see most of the cool features that you can do with cat-boot-report.pdf. " +
                "This framework is not a full-fledged reporting engine, but it should help you in printing simple reports " +
                "for your Java apps without digging into complicated reporting engines.")
        .beginNewSection("Table", false)
        .addText("You can not only add text, but also tables:")
        .addPadding(10)
        .startTable()
        .addColumn("COL1", 2).addColumn("COL2", 2).addColumn("COL3", 4)
        .createRow().withValues("x1", "x2", "x3")
        .createRow().withValues("y1", "y2", "y3")
        .endTable()
        .beginNewSection("Formatting", false)
        .addText("You can also format text as you can see here.", new PdfTextStyle(13, PDType1Font.TIMES_BOLD_ITALIC, Color.BLUE))
        .beginNewSection("Images", false)
        .addText("Images are also supported out-of-the-box:")
        .addPadding(10)
        .addElement(new ReportImage(ImageIO.read(new ClassPathResource("github_icon.png").getFile()), 100, 100))
        .withFooterOnAllPages("Demo-PDF", "cat-boot-report-pdf", PdfFooterGenerator.PAGE_TEMPLATE_CURR + "/"
                + PdfFooterGenerator.PAGE_TEMPLATE_TOTAL)
        .buildReport("demo.pdf",
                PdfPageLayout.getPortraitA4Page(),
                new ClassPathResource("demo-template.pdf"));


pdfReportFilePrinter.print(pdfReport, new File("pdf-out"));
```

All you need is the [demo-template.pdf](https://github.com/Catalysts/cat-boot/tree/master/cat-boot-report-pdf/src/test/resources/demo-template.pdf) and
 as a result you will get this [demo.pdf](https://github.com/Catalysts/cat-boot/tree/master/cat-boot-report-pdf/demo.pdf).
 
You can also browse and execute the [DemoTest.java](https://github.com/Catalysts/cat-boot/tree/master/cat-boot-report-pdf/src/test/java/cc/catalysts/boot/report/pdf/impl/DemoTest.java) yourself. 
 
The intention of this small library is not to build a full report engine, but if you are using a smart template.pdf behind with your company
 header and footer, then this is enough to write PDF for invoices, simple letters, company reports and much more.

## Integration

Wanna use that cool library? It's easy. You can embed cat-boot-report-pdf with Maven or Gradle from MavenCentral or JCenter,
here is the code for Gradle:

```groovy
compile('cc.catalysts.boot:cat-boot-report-pdf:' + catBootVersion)
```

And that's it. If you're running in a Spring-Boot-Application with AutoConfiguration-Support, then the PdfReportService
and PdfReportFilePrinter will be created automatically for you and you can inject them in your beans.
