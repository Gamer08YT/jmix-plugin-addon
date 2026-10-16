package de.bytestore.plugin.view.pluginupload;


import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.bytestore.plugin.service.PluginService;
import de.bytestore.plugin.service.InstallationTermsContent;
import de.bytestore.plugin.service.PluginArtifactIdentity;
import io.jmix.core.Messages;
import io.jmix.flowui.Dialogs;
import io.jmix.flowui.UiComponents;
import io.jmix.flowui.component.codeeditor.CodeEditor;
import io.jmix.flowui.component.upload.FileUploadField;
import io.jmix.flowui.kit.component.button.JmixButton;
import io.jmix.flowui.kit.component.upload.event.FileUploadSucceededEvent;
import io.jmix.flowui.view.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

/**
 * PluginUploadView is a user interface class responsible for managing the plugin upload process.
 * It handles the upload, validation, and storage of plugin files while providing user feedback
 * throughout the process.
 *
 * Key functionality includes:
 * - Handling file upload events to process plugin archives.
 * - Validating uploaded plugin files and providing appropriate feedback in case of issues.
 * - Enabling or disabling actions based on plugin validation results.
 * - Providing a user interface for completing the plugin upload process.
 *
 * This class interacts with several services and components:
 * - {@link PluginService} for plugin-related operations such as writing to temporary directories,
 *   validating plugins, and moving plugins to their final destination.
 * - {@link UiComponents} for dynamically creating user interface components.
 * - {@link Dialogs} for displaying feedback dialogs to the user.
 * - {@link MessageBundle} and {@link Messages} for localized messaging.
 *
 * Event subscriptions:
 * - Processes file upload success events to handle the plugin archive.
 * - Handles button click events for finalizing the plugin submission process.
 */
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
    @ViewComponent
    private JmixButton pluginSubmit;

    private String fileName;
    private PluginArtifactIdentity artifactIdentity;

    @Subscribe("pluginArchive")
    public void onPluginArchiveFileUploadSucceeded(final FileUploadSucceededEvent<FileUploadField, byte[]> event) {
        // Access the uploaded file information:
        fileName = event.getFileName();
        byte[] fileContent = event.getSource().getValue();

        // Perform your logic to handle the fileContent:
        assert fileContent != null;

        // Write Plugin to Temp Directory to Test Archive.
        pluginService.writeTemp(fileName, fileContent);

        try {
            // Load Uploaded Plugin.
            artifactIdentity = pluginService.checkPlugin(fileName);
            Optional<InstallationTermsContent> customTerms = pluginService.renderInstallationTerms(fileName);
            Optional<String> termsFile = pluginService.readInstallationTerms(fileName);
            if (customTerms.isPresent()) {
                showTermsDialog(customTerms.get());
            } else if (termsFile.isPresent()) {
                showTermsDialog(termsFile.get());
            } else {
                finishValidatedUpload();
            }
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

            pluginSubmit.setEnabled(false);
        }

    }

    @Subscribe(id = "pluginSubmit", subject = "clickListener")
    public void onPluginSubmitClick(final ClickEvent<JmixButton> event) {
        try {
            pluginService.loadMovedPlugin(fileName);
            this.close(StandardOutcome.SAVE);
        } catch (RuntimeException e) {
            log.error("Unable to install plugin archive {}", fileName, e);
            pluginSubmit.setEnabled(false);
            dialogs.createMessageDialog().withHeader(messageBundle.getMessage("installationTermsTitle"))
                    .withText(messageBundle.getMessage("installationTermsSaveFailed")).open();
        }
    }

    private void showTermsDialog(String termsHtml) {
        showTermsDialog(new InstallationTermsContent(messageBundle.getMessage("installationTermsTitle"),
                java.util.List.of(new Html(termsHtml))));
    }

    private void showTermsDialog(InstallationTermsContent terms) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle(terms.title());
        dialog.setWidth("min(720px, 90vw)");
        dialog.setMaxHeight("85vh");
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);

        Div scrollableTerms = new Div();
        terms.components().forEach(scrollableTerms::add);
        scrollableTerms.getStyle().set("max-height", "60vh");
        scrollableTerms.getStyle().set("overflow", "auto");
        scrollableTerms.getStyle().set("padding", "var(--lumo-space-m)");

        Checkbox acceptance = new Checkbox(messageBundle.getMessage("acceptInstallationTerms"));
        Button cancel = new Button(messageBundle.getMessage("cancelInstallation"), click -> {
            dialog.close();
            pluginService.removeTemp(fileName);
            pluginSubmit.setEnabled(false);
            fileName = null;
        });
        Button accept = new Button(messageBundle.getMessage("acceptAndContinue"), click -> {
            try {
                pluginService.acceptInstallationTerms(artifactIdentity);
                dialog.close();
                finishValidatedUpload();
            } catch (RuntimeException e) {
                log.error("Could not store acceptance for plugin {}", artifactIdentity.pluginId(), e);
                pluginService.removeTemp(fileName);
                pluginSubmit.setEnabled(false);
                dialog.close();
                dialogs.createMessageDialog().withHeader(messageBundle.getMessage("installationTermsTitle"))
                        .withText(messageBundle.getMessage("installationTermsSaveFailed")).open();
            }
        });
        accept.setEnabled(false);
        acceptance.addValueChangeListener(event -> accept.setEnabled(event.getValue()));

        dialog.add(scrollableTerms, acceptance);
        dialog.getFooter().add(cancel, accept);
        dialog.open();
    }

    private void finishValidatedUpload() {
        pluginService.moveOutOfTemp(fileName);
        log.info("Successfully Uploaded Plugin Archive: {}", fileName);
        pluginSubmit.setEnabled(true);
    }


}
