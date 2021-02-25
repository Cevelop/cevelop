package com.cevelop.templator.plugin.view.components;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.cevelop.templator.plugin.view.tree.TreeTemplateView;


public class ScrollAnimator {

    private static final int   AUTO_SCROLL_MAX_SPEED    = 32;
    private static final float AUTO_SCROLL_ACCELERATION = 1.14f;

    private static final int TIMER_INTERVAL = 20;

    private ScrolledForm form;

    private Point scrollDestination = new Point(0, 0);

    private boolean animationRunning = false;
    private boolean stopScrolling    = false;

    private float speed = 0;

    public ScrollAnimator(ScrolledForm form) {
        this.form = form;
    }

    public void scrollTo(Composite composite) {

        if (composite.getSize().x + TreeTemplateView.BORDER_MARGIN * 2 > form.getClientArea().width) {
            scrollDestination.x = composite.getLocation().x - TreeTemplateView.BORDER_MARGIN;
        } else {
            int rightViewEdge = form.getOrigin().x + form.getClientArea().width;
            int rightEntryEdge = composite.getLocation().x + composite.getSize().x;
            int scrollX = rightEntryEdge - rightViewEdge + TreeTemplateView.BORDER_MARGIN;
            scrollDestination.x = form.getOrigin().x + scrollX;
        }

        if (composite.getSize().y + TreeTemplateView.BORDER_MARGIN * 2 > form.getClientArea().height) {
            scrollDestination.y = composite.getLocation().y - TreeTemplateView.BORDER_MARGIN;
        } else {
            int botViewEdge = form.getOrigin().y + form.getClientArea().height;
            int botEntryEdge = composite.getLocation().y + composite.getSize().y;
            int scrollY = botEntryEdge - botViewEdge + TreeTemplateView.BORDER_MARGIN;
            scrollDestination.y = form.getOrigin().y + scrollY;
        }

        scrollDestination.x = scrollDestination.x < 0 ? 0 : scrollDestination.x;
        scrollDestination.y = scrollDestination.y < 0 ? 0 : scrollDestination.y;

        speed = 0;

        startAnimation();
    }

    private void startAnimation() {
        if (animationRunning) {
            return;
        }
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                if (stopScrolling) {
                    stopScrolling = false;
                    animationRunning = false;
                    return;
                }

                Point origin = form.getOrigin();

                if (speed < AUTO_SCROLL_MAX_SPEED) {
                    speed += 10 * AUTO_SCROLL_ACCELERATION;
                }

                int newX = calcCoordinate(origin.x, scrollDestination.x);
                int newY = calcCoordinate(origin.y, scrollDestination.y);

                form.setOrigin(newX, newY);

                boolean animationFinishedX = origin.x == newX;
                boolean animationFinishedY = origin.y == newY;

                if (!animationFinishedX || !animationFinishedY) {
                    form.getDisplay().timerExec(TIMER_INTERVAL, this);
                } else {
                    animationRunning = false;
                }
            }
        };
        animationRunning = true;
        form.getDisplay().timerExec(TIMER_INTERVAL, runnable);
    }

    private int calcCoordinate(int origin, int dest) {

        int scroll = dest - origin;
        int delta = (int) speed;

        if (scroll > 0) {
            if (origin + delta > dest) {
                delta = dest - origin;
            }
            origin += delta;
        } else {
            if (origin - delta < dest) {
                delta = (dest - origin) * -1;
            }
            origin -= delta;
        }
        return origin;
    }

    public void stopScrolling() {
        if (animationRunning) {
            stopScrolling = true;
        }

    }
}
