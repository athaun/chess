package ui.element;

import org.lwjgl.glfw.GLFW;

import event.Events;
import graphics.Color;
import ui.EventHandler;
import ui.Frame;
import ui.RenderableElement;
import ui.Text;
import ui.EventHandler.Event;
import ui.fonts.Font;
import util.Engine;

/**
 * @author Juyas
 * @version 07.11.2021
 * @since 07.11.2021
 */
public class TextField extends RenderableElement implements TextHolder {

    /**
     * The text displayed in the TextField, that can be edited.
     */
    private Text label;
    private String overflowText;

    public TextField() {
        this("", new Frame(100, 100, 200, 25));
    }

    public TextField(String text, Frame frame) {
        super(Color.WHITE, frame);
        this.setCursor(GLFW.GLFW_IBEAM_CURSOR);
        //this component requests focus when clicked on it for receiving input
        this.getEventHandler().registerListener(EventHandler.Event.MOUSE_CLICK, eventHandler -> {
            if (eventHandler.isMouseButtonClicked(GLFW.GLFW_MOUSE_BUTTON_LEFT))
                requestFocus();
        });

        Engine.scenes().currentScene().addUIElement(this);
        Engine.scenes().currentScene().uiRenderer.add(this);

        this.label = new Text(text,  new Font("src/assets/fonts/OpenSans.ttf", frame.getHeight() - 7, true), Color.BLACK, 0, 0);
        this.cursor = GLFW.GLFW_IBEAM_CURSOR;

        getEventHandler().registerListener(Event.MOUSE_CLICK, e -> {
            if (e.isMouseButtonClicked(GLFW.GLFW_MOUSE_BUTTON_LEFT)) {
                requestFocus();
            }
        });

        Events.charEvent.subscribe(c -> {
            if (isFocused()) {
                if (label.getLongestLineWidth() < frame.getWidth() - 15) {
                    label.addChar(c.character);
                }
            }
        });

        Events.keyEvent.subscribe(k -> {
            if (isFocused()) {
                // Backspace
                if (k.keycode == GLFW.GLFW_KEY_BACKSPACE && (k.action == GLFW.GLFW_PRESS || k.action == GLFW.GLFW_REPEAT)) {
                    if (getText().length() > 0) {
                        label.removeChar();
                    }
                }
                 
                // Space
                if (k.keycode == GLFW.GLFW_KEY_SPACE && (k.action == GLFW.GLFW_PRESS || k.action == GLFW.GLFW_REPEAT)) {
                    if (label.getLongestLineWidth() < frame.getWidth() - 15) {
                        label.addChar(' ');
                    }
                }
            }
        });
    }

    @Override
    public String getText() {
        return label.getText();
    }

    @Override
    public void setText(String text) {
        this.label.change(text);
    }

    @Override
    public void update () {
        super.update();

        label.setPosition(getX() + 3, getY() + frame.getHeight() / 2 - label.getHeight() / 2);

        if (isMouseOnThis()) {
            if (tintColor != null) {
                this.setColor(tintColor);
            }
        } else {
            this.setColor(defaultColor);
        }
    }
}