# fxPDF
This is a PDF viewer libary written in JavaFX. I used icons from [MaterialDesign](https://material.io/resources/icons/) 
and the [Apache PDFBox®](https://pdfbox.apache.org/) library.
## How does it work?
The PDFRenderer render a page as an Image and this is displayed in an ImageView. One problem that results from this is that you cannot copy the text, for example.
## Integrate it into your own projects
You can download the fxPDF.jar [here](https://github.com/Patr1ick/fxPDF/releases/). You can integrate them into your project via your preferred IDE. When you start the file, you can open a PDF and see the `PDFViewer`.  
There are two Versions to show the PDF. You can use the more advanced viewer (`PDFViewer`) or the smaller or cleaner version, where you have more custom control (`Viewer`).
### PDFViewer
```java
PDF pdf = new PDF(file); // Create a PDF 
PDFViewer v = new PDFViewer(pdf); // The PDFViewer object
root.getChildren().add(v); // Add the Viewer to the root-Pane
```
#### Screenshot of PDFViewer
![Screenshot of example](https://github.com/Patr1ick/fxPDF/blob/master/pdfviewer.png "PDFViewer")
### Viewer
#### ViewerType LIST and IMAGE
There are two types of viewer to display the PDF differently.
##### LIST
The ViewType LIST displays the pages of the PDF below each other in a ScrollPane.
##### Image
The ViewType IMAGE displays each page of the PDF individually. You can navigate using buttons.
```java
PDF pdf = new PDF(file); // Create a PDF 
Viewer v = new Viewer(pdf); // The Viewer object
root.getChildren().add(v); // Add the Viewer to the root-Pane
``` 
#### Methods
<table>
  <tr>
    <th>Method</th>
    <th>ViewerType IMAGE</th>
    <th>ViewerType LIST</th>
  </tr>
  <tr>
    <td><code>v.setDisableZoomButtons(true);</code></td>
    <td colspan=2>true - disable the zoom buttons (default: false)</td>
  </tr>
  <tr>
    <td><code>v.setDisableNextPageButtons(true);</code></td>
    <td colspan=2 >true - disable the buttons to navigate through the PDF(default false)</td>
  </tr> 
  <tr>
    <td><code>v.setViewerType(ViewerType.LIST);</code></td>
    <td colspan=2 >ViewerType.IMAGE (default) or ViewerType.LIST (for more information see above)</td>
  </tr>
  <tr>
    <td><code>viewer.updateViewer();</code></td>
    <td colspan=2>Refresh the current Page</td>
  </tr>
  <tr>
    <td><code>v.loadPage(int pgaeNumber);</code></td>
    <td>Load the page of the PDF</td>
    <td>not implemented yet</td>
  </tr>
  <tr>
    <td><code>v.leftPage();</code></td>
    <td>Switch to the previous page</td>
    <td>Does not affect the Viewer</td>
  </tr>
  <tr>
    <td><code>v.rightPage();</code></td>
    <td>Switch to the next page</td>
    <td>Does not affect the Viewer</td>
  </tr>
  <tr>
    <td><code>v.getCurrentPageNumber();</code></td>
    <td colspan=2>Returns the number of the current visible page</td>
  </tr>
  <tr>
    <td><code>v.getScaleFactor();</code></td>
    <td colspan=2>Returns the scale factor (float)</td>
  </tr>
  <tr>
    <td><code>v.setScaleFactor(float scaleFactor);</code></td>
    <td>Set the scale factor and refresh the Viewer</td>
    <td>Scale is currently not implemented for ViewerType.LIST.</td>
  </tr>
</table>

```java
//Other Methods
//Add an PageSwitchEvent
viewer.addEventHandler(CustomEvent.CUSTOM_EVENT_TYPE, new PageSwitchEventHandler() {
  @Override
  public void onPageSwitch(String param) {
    // param can be "LEFT", "RIGHT" or "LOADED"
  }
});
```
#### Screenshot of Viewer
![Screenshot of viewer](https://github.com/Patr1ick/fxPDF/blob/master/viewer.png "Viewer")
- blue: buttons to navigate through the PDF
- green: buttons to zoom in/out
- red: the `ImageView` to show the current page 
## Hotkeys
### PDFViewer
- `Control + O` Open a FileDialog to load a new PDF.
- `Control + Q` Close the Window
- `F11` Toggle Fullscreen
### Viewer
- `Control + Left` Left Page
- `Control + Right` Right Page
- `Control + +` Zoom In
- `Control + -` Zoom Out
### PagePreview
The PagePreview shows a list of all pages of the PDF. If you click on a page, the viewer will load it.
#### Example
`PagePreview p = new PagePreview(pdf, viewer);`
### PageChooser
With the PageChooser you can see the current page number and the total number of pages. You can also select which page the viewer should display.
#### Example
`PageChooser p = new PageChooser(pdf, viewer);`
#### Screenshot
![Screenshot of PageChooser](https://github.com/Patr1ick/fxPDF/blob/master/pagechooser.png "PageChooser")
## Upcoming Features / Not implemented
- When viewerType is LIST:
  - Zooming
  - loadPage: scroll to the given pageNumber
- More Function for the class `PDF`
- More functionality for the classes `EditablePDF`, `EditablePage`, etc 
## License
[Apache PDFBox®](https://pdfbox.apache.org/) and [MaterialDesign Icons](https://material.io/resources/icons/) are licensed under the [Apache License v2.0](https://www.apache.org/licenses/LICENSE-2.0).
