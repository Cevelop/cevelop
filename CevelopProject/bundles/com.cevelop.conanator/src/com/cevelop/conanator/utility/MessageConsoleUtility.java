package com.cevelop.conanator.utility;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.MessageConsole;

import com.cevelop.conanator.Activator;


public class MessageConsoleUtility {

    public static final String CONSOLE_NAME = "Conan";

    public static MessageConsole getConsole(String name) {
        IConsoleManager conMan = ConsolePlugin.getDefault().getConsoleManager();

        for (IConsole console : conMan.getConsoles()) {
            if (name.equals(console.getName())) {
                return (MessageConsole) console;
            }
        }

        //no console found, so create a new one
        MessageConsole myConsole = new MessageConsole(name, null);
        conMan.addConsoles(new IConsole[] { myConsole });

        return myConsole;
    }

    public static void showConsole(IConsole console) {
        String id = IConsoleConstants.ID_CONSOLE_VIEW;
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

        try {
            IConsoleView view = (IConsoleView) window.getActivePage().showView(id);
            view.display(console);
        } catch (PartInitException e) {
            Activator.log(e);
        }
    }
}
