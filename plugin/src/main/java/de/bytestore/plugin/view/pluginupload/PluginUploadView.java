package de.bytestore.plugin.view.pluginupload;


import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.bytestore.plugin.service.PluginService;
import io.jmix.core.Messages;
import io.jmix.flowui.Dialogs;
import io.jmix.flowui.UiComponents;
import io.jmix.flowui.component.codeeditor.CodeEditor;
import io.jmix.flowui.component.upload.FileUploadField;
import io.jmix.flowui.kit.component.upload.event.FileUploadSucceededEvent;
import io.jmix.flowui.view.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Route(value = "plugin-upload-view", layout = DefaultMainViewParent.class)
@ViewController(id = "plugin_PluginUploadView")
@ViewDescriptor(path = "plugin-upload-view.xml")
public class PluginUploadView extends StandardView {
    private static final Logger log = LoggerFactory.getLogger(PluginUploadView.class);
    @Autowired
    protected UiComponents uiComponents;
    @Autowired
    private PluginService pluginService;
    @Autowired
    private Dialogs dialogs;
    @ViewComponent
    private MessageBundle messageBundle;
    @Autowired
    private Messages messages;

    @Subscribe("pluginArchive")
    public void onPluginArchiveFileUploadSucceeded(final FileUploadSucceededEvent<FileUploadField> event) {
        // Access the uploaded file information:
        String fileName = event.getFileName();
        byte[] fileContent = event.getSource().getValue();

        // Perform your logic to handle the fileContent:
        assert fileContent != null;

        // Write Plugin to Temp Directory to Test Archive.
        pluginService.writeTemp(fileName, fileContent);

        try {
            // Load Uploaded Plugin.
            pluginService.checkPlugin(fileName);

            // If no exception occurs, move to home directory.
            pluginService.moveOutOfTemp(fileName);

            log.info("Successfully Uploaded Plugin Archive: {}", fileName);
        } catch (Exception e) {
            log.error("Unable to Upload Plugin Archive: {}. {}", fileName, e.getMessage());

            // Remove from Temp directory.
            pluginService.removeTemp(fileName);

            Dialogs.MessageDialogBuilder dialogIO = dialogs.createMessageDialog().withHeader(messages.getMessage("de.bytestore.plugin.view.plugin/upload"));

            VerticalLayout layoutIO = uiComponents.create(VerticalLayout.class);

            CodeEditor editorIO = uiComponents.create(CodeEditor.class);
            editorIO.setValue(e.getMessage());
            editorIO.setReadOnly(true);

            layoutIO.add(new Paragraph(messageBundle.formatMessage("uploadFailed", fileName)));
            layoutIO.add(editorIO);

            dialogIO.withContent(layoutIO);
            dialogIO.open();
        }

    }

}