package it.albertus.codec.gui.listener;

import it.albertus.codec.engine.CodecAlgorithm;
import it.albertus.codec.engine.CodecMode;
import it.albertus.codec.gui.CodecGui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;

public class AlgorithmComboSelectionListener extends SelectionAdapter {

	private final CodecGui gui;

	public AlgorithmComboSelectionListener(final CodecGui gui) {
		this.gui = gui;
	}

	@Override
	public void widgetSelected(final SelectionEvent se) {
		final CodecAlgorithm algorithm = CodecAlgorithm.values()[(gui.getAlgorithmCombo().getSelectionIndex())];
		gui.getEngine().setAlgorithm(algorithm);

		/* Gestione radio */
		final Button encodeRadio = gui.getModeRadios().get(CodecMode.ENCODE);
		final Button decodeRadio = gui.getModeRadios().get(CodecMode.DECODE);

		if (algorithm.getModes().contains(CodecMode.DECODE)) {
			if (!gui.getCharsetCombo().getEnabled()) {
				gui.getCharsetCombo().setEnabled(true);
			}
			if (!gui.getCharsetLabel().getEnabled()) {
				gui.getCharsetLabel().setEnabled(true);
			}
			if (!decodeRadio.getEnabled()) {
				decodeRadio.setEnabled(true);
			}
		}
		else {
			if (gui.getCharsetCombo().getEnabled()) {
				gui.getCharsetCombo().setEnabled(false);
			}
			if (gui.getCharsetLabel().getEnabled()) {
				gui.getCharsetLabel().setEnabled(false);
			}
			if (decodeRadio.getEnabled()) {
				decodeRadio.setSelection(false);
				decodeRadio.setEnabled(false);
				encodeRadio.setSelection(true);
				encodeRadio.notifyListeners(SWT.Selection, null);
			}
		}

		gui.getInputText().notifyListeners(SWT.Modify, null);
	}

}
