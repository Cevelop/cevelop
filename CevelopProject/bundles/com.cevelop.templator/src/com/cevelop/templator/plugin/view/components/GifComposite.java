package com.cevelop.templator.plugin.view.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;


public class GifComposite extends Canvas {

    private Image image;
    private int   imageNumber;

    private boolean continueRendering = true;

    public GifComposite(Composite parent, int style, String filename) {
        super(parent, style | SWT.DOUBLE_BUFFERED);

        setBounds(10, 10, 300, 300);

        final ImageLoader loader = new ImageLoader();
        loader.load(getClass().getResourceAsStream(filename));

        image = new Image(getDisplay(), loader.data[0]);

        final GC gc = new GC(image);

        addPaintListener(event -> event.gc.drawImage(image, 0, 0));

        addDisposeListener(e -> continueRendering = false);

        Thread thread = new Thread() {

            @Override
            public void run() {
                while (continueRendering) {
                    getDisplay().asyncExec(() -> {
                        if (!continueRendering) {
                            return;
                        }

                        imageNumber = imageNumber == loader.data.length - 1 ? 0 : imageNumber + 1;
                        ImageData nextFrameData = loader.data[imageNumber];
                        Image frameImage = new Image(getDisplay(), nextFrameData);
                        gc.drawImage(frameImage, nextFrameData.x, nextFrameData.y);
                        frameImage.dispose();
                        redraw();
                    });

                    long currentTime = System.currentTimeMillis();
                    int delayTime = loader.data[imageNumber].delayTime;
                    while (currentTime + delayTime * 10 > System.currentTimeMillis()) {
                        Thread.yield();
                    }
                }
            }
        };

        thread.start();
    }
}
