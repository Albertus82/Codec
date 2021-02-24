package it.albertus.acodec.gui.listener;

import java.util.logging.Level;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;

import it.albertus.acodec.common.engine.CodecConfig;
import it.albertus.acodec.common.engine.StringCodec;
import it.albertus.acodec.common.resources.Messages;
import it.albertus.acodec.gui.CodecGui;
import it.albertus.acodec.gui.CodecGui.GuiStatus;
import it.albertus.acodec.gui.resources.GuiMessages;
import lombok.extern.java.Log;

@Log
public class InputTextModifyListener implements ModifyListener {

	private static final Messages messages = GuiMessages.INSTANCE;

	private final CodecGui gui;

	public InputTextModifyListener(final CodecGui gui) {
		this.gui = gui;
	}

	@Override
	public void modifyText(final ModifyEvent event) {
		String result;
		if (GuiStatus.DIRTY.equals(gui.getStatus())) {
			gui.setStatus(GuiStatus.OK);
			gui.getInputText().setText("");
			gui.getInputText().setForeground(gui.getDefaultTextColor());
		}

		// Preliminary checks
		if (gui.getAlgorithm() == null) {
			gui.print(messages.get("gui.message.missing.algorithm.banner"), GuiStatus.ERROR);
			return;
		}
		if (gui.getInputText().getText().isEmpty()) {
			gui.print(messages.get("gui.message.missing.input.banner"), GuiStatus.ERROR);
			return;
		}

		// Process
		final CodecConfig codecConfig = new CodecConfig(gui.getMode(), gui.getAlgorithm(), gui.getCharset());
		try {
			result = new StringCodec(codecConfig).run(gui.getInputText().getText());
		}
		catch (final EncoderException e) {
			gui.print(messages.get("gui.error.cannot.encode.banner", codecConfig.getAlgorithm().getName()), GuiStatus.ERROR);
			log.log(Level.INFO, messages.get("gui.error.cannot.encode", codecConfig.getAlgorithm().getName()), e);
			return;
		}
		catch (final DecoderException e) {
			gui.print(messages.get("gui.error.cannot.decode.banner", codecConfig.getAlgorithm().getName()), GuiStatus.ERROR);
			log.log(Level.INFO, messages.get("gui.error.cannot.decode", codecConfig.getAlgorithm().getName()), e);
			return;
		}
		catch (final Exception e) {
			gui.print(messages.get("gui.error.unexpected.error.banner"), GuiStatus.ERROR);
			log.log(Level.SEVERE, messages.get("gui.error.unexpected.error"), e);
			return;
		}
		gui.print(result, GuiStatus.OK);
	}

}
