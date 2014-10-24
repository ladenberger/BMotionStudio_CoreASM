import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.UUID;

import de.prob.Main;
import de.prob.ui.api.ToolRegistry;


public class Start {

	private static Thread prob;

	public static void runProB(final Main m, final String[] args) {
		prob = new Thread(new Runnable() {
			public void run() {
				m.main(args);
			}
		});
		prob.start();
	}

	public static void main(String[] args) {

		// Get tool registry
		ToolRegistry toolRegistry = Main.getInjector().getInstance(
				ToolRegistry.class);

		// Create a new CoreASM ITool instance and register
		InputStream resourceAsStream = Start.class
				.getResourceAsStream("examples/extrema.casm");
		Reader reader = new InputStreamReader(resourceAsStream);
		String toolId = UUID.randomUUID().toString();

		CoreAsmTool coreAsmTool = new CoreAsmTool(toolRegistry, toolId, reader);
		toolRegistry.register(toolId, coreAsmTool);

		// Start ProB2
		Main m = Main.getInjector().getInstance(Main.class);
		runProB(m,
				new String[] {
						"-browser",
						"bms/?template=/home/lukas/GIT/bmotion-case-studies/prob2/CoreASM/Extrema/extrema.html&tool="
								+ toolId, "-local", "-s" });

	}

}
