package it.albertus.acodec.gui.listener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Text;

import it.albertus.jface.SwtUtils;
import it.albertus.jface.closeable.CloseableClipboard;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TextCopyKeyListener extends KeyAdapter {

	private final Text text;

	@Override
	public void keyPressed(final KeyEvent event) {
		// Supporto CTRL+C per "Copia"...
		if (event.stateMask == SWT.MOD1 && event.keyCode == SwtUtils.KEY_COPY) {
			try (final CloseableClipboard cc = new CloseableClipboard(new Clipboard(text.getDisplay()))) {
				cc.getClipboard().setContents(new String[] { text.getText() }, new Transfer[] { TextTransfer.getInstance() });
			}
		}
	}

}