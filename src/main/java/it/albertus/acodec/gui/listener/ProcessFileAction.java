package it.albertus.acodec.gui.listener;

import static it.albertus.acodec.engine.CodecMode.DECODE;
import static it.albertus.acodec.engine.CodecMode.ENCODE;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import it.albertus.acodec.engine.Cancelable;
import it.albertus.acodec.engine.CodecAlgorithm;
import it.albertus.acodec.engine.ProcessFileTask;
import it.albertus.acodec.gui.CodecGui;
import it.albertus.acodec.gui.Images;
import it.albertus.acodec.gui.ProcessFileRunnable;
import it.albertus.acodec.resources.Messages;
import it.albertus.jface.EnhancedErrorDialog;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

@Log
@RequiredArgsConstructor
public class ProcessFileAction {

	private static final String MSG_APPLICATION_NAME = "msg.application.name";

	protected final CodecGui gui;

	protected String getSourceFile() {
		final FileDialog openDialog = new FileDialog(gui.getShell(), SWT.OPEN);
		if (DECODE.equals(gui.getConfig().getMode())) {
			openDialog.setFilterExtensions(buildFilterExtensions(gui.getConfig().getAlgorithm()));
		}
		return openDialog.open();
	}

	protected String getDestinationFile(final String sourceFileName) {
		final FileDialog saveDialog = new FileDialog(gui.getShell(), SWT.SAVE);
		saveDialog.setOverwrite(true);
		final File sourceFile = new File(sourceFileName);
		saveDialog.setFilterPath(sourceFile.getParent());
		if (ENCODE.equals(gui.getConfig().getMode())) {
			final CodecAlgorithm algorithm = gui.getConfig().getAlgorithm();
			saveDialog.setFilterExtensions(buildFilterExtensions(algorithm));
			saveDialog.setFileName(sourceFile.getName() + '.' + algorithm.getFileExtension().toLowerCase());
		}
		else {
			if (sourceFile.getName().indexOf('.') != -1) {
				saveDialog.setFileName(sourceFile.getName().substring(0, sourceFile.getName().lastIndexOf('.')));
			}
		}
		return saveDialog.open();
	}

	static String[] buildFilterExtensions(final CodecAlgorithm algorithm) {
		final String extension = algorithm.getFileExtension();
		return new String[] { "*." + extension.toLowerCase() + ";*." + extension.toUpperCase(), "*.*" };
	}

	protected void execute(final String sourceFileName, final String destinationFileName) {
		if (sourceFileName == null || destinationFileName == null) {
			throw new NullPointerException("File names cannot be null.");
		}
		try {
			final File inputFile = new File(sourceFileName);
			final File outputFile = new File(destinationFileName);
			final ProcessFileTask task = new ProcessFileTask(gui.getConfig(), inputFile, outputFile);
			final ProcessFileRunnable runnable = new ProcessFileRunnable(task);
			new LocalizedProgressMonitorDialog(gui.getShell(), task).run(true, true, runnable); // execute in separate thread
			if (runnable.getResult() != null) { // result can be null in certain cases
				gui.setDirty(false);
				gui.getInputText().setText(inputFile.getName());
				gui.getOutputText().setText(runnable.getResult());
				gui.setDirty(true);
			}
			final MessageBox box = new MessageBox(gui.getShell(), SWT.ICON_INFORMATION);
			box.setMessage(Messages.get("msg.file.process.ok.message"));
			box.setText(Messages.get(MSG_APPLICATION_NAME));
			box.open();
		}
		catch (final InterruptedException e) { // NOSONAR
			final MessageBox box = new MessageBox(gui.getShell(), SWT.ICON_WARNING);
			box.setMessage(Messages.get("msg.file.process.cancel.message"));
			box.setText(Messages.get(MSG_APPLICATION_NAME));
			box.open();
		}
		catch (final InvocationTargetException e) {
			log.log(Level.WARNING, e.toString(), e);
			final String message;
			final Throwable throwable;
			if (e.getCause() != null) {
				message = e.getCause().getMessage() != null ? e.getCause().getMessage() : e.getCause().toString();
				throwable = e.getCause();
			}
			else {
				message = e.getMessage() != null ? e.getMessage() : e.toString();
				throwable = e;
			}
			EnhancedErrorDialog.openError(gui.getShell(), Messages.get(MSG_APPLICATION_NAME), message, IStatus.WARNING, throwable, Images.getMainIconArray());
		}
		catch (final Exception e) {
			log.log(Level.SEVERE, e.toString(), e);
			EnhancedErrorDialog.openError(gui.getShell(), Messages.get(MSG_APPLICATION_NAME), e.toString(), IStatus.ERROR, e, Images.getMainIconArray());
		}
	}

	private static class LocalizedProgressMonitorDialog extends ProgressMonitorDialog {

		private final Cancelable cancelable;

		private LocalizedProgressMonitorDialog(final Shell shell, final Cancelable cancelable) {
			super(shell);
			this.cancelable = cancelable;
		}

		@Override // improved localization
		public void create() {
			super.create();
			getShell().setText(Messages.get("lbl.process.file.dialog.title"));
			final Button cancelButton = getButton(IDialogConstants.CANCEL_ID);
			if (cancelButton != null && !cancelButton.isDisposed()) {
				cancelButton.setText(Messages.get("lbl.process.file.dialog.button.cancel"));
			}
		}

		@Override
		protected void cancelPressed() {
			super.cancelPressed();
			progressIndicator.showError();
			cancelable.cancel();
		}
	}

}