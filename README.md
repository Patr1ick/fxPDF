# fxPDF
This is a PDF viewer written in JavaFX. I used icons from [MaterialDesign](https://material.io/resources/icons/) 
and the [Apache PDFBox®](https://pdfbox.apache.org/) library.
## How does it work?
The PDFRenderer render a page as an Image and this is displayed in an ImageView. One problem that results from this is that you cannot copy the text, for example.
## Integrate it into your own projects
You can use the viewer with more options (`PDFViewer`) or the smaller version, where you can disable the controls (`Viewer`).
### PDFViewer
```java
PDF pdf = new PDF(file); // Create a PDF 
PDFViewer v = new PDFViewer(pdf); // The PDFViewer object
root.getChildren().add(v); // Add the Viewer to the root-Pane
```
### Viewer
```java
PDF pdf = new PDF(file); // Create a PDF 
Viewer v = new Viewer(pdf); // The Viewer object
v.setDisableZoomButtons(true); // By default true
v.setDisableNextPageButtons(true); //By default true
root.getChildren().add(v); // Add the Viewer to the root-Pane
```
```java
//Methods
v.updatePage(); // Refresh the page with the given page number
v.leftPage(); // Switch to the previous page
v.rightPage(); // Switch to the next page
v.getCurrentPageNumber(); // Returns the number of the current visible page
v.getScaleFactor(); Returns the scale factor (float)
```
