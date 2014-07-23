package de.prob.tools.coreasm;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.coreasm.engine.CoreASMEngine;
import org.coreasm.engine.Engine;
import org.coreasm.engine.absstorage.AbstractUniverse;
import org.coreasm.engine.absstorage.BackgroundElement;
import org.coreasm.engine.absstorage.BooleanElement;
import org.coreasm.engine.absstorage.Element;
import org.coreasm.engine.absstorage.FunctionElement;
import org.coreasm.engine.absstorage.Location;
import org.coreasm.engine.absstorage.State;
import org.coreasm.engine.absstorage.UniverseElement;
import org.coreasm.engine.plugin.Plugin;
import org.coreasm.engine.plugins.BasicASMPlugins;
import org.coreasm.engine.plugins.StandardPlugins;
import org.coreasm.engine.plugins.abstraction.AbstractionPlugin;
import org.coreasm.engine.plugins.bag.BagPlugin;
import org.coreasm.engine.plugins.blockrule.BlockRulePlugin;
import org.coreasm.engine.plugins.blockrule.TabBlocksPlugin;
import org.coreasm.engine.plugins.caserule.CaseRulePlugin;
import org.coreasm.engine.plugins.chooserule.ChooseRulePlugin;
import org.coreasm.engine.plugins.collection.CollectionPlugin;
import org.coreasm.engine.plugins.conditionalrule.ConditionalRulePlugin;
import org.coreasm.engine.plugins.debuginfo.DebugInfoPlugin;
import org.coreasm.engine.plugins.extendrule.ExtendRulePlugin;
import org.coreasm.engine.plugins.forallrule.ForallRulePlugin;
import org.coreasm.engine.plugins.grammar.GrammarPlugin;
import org.coreasm.engine.plugins.io.IOPlugin;
import org.coreasm.engine.plugins.kernelextensions.KernelExtensionsPlugin;
import org.coreasm.engine.plugins.letrule.LetRulePlugin;
import org.coreasm.engine.plugins.list.ListPlugin;
import org.coreasm.engine.plugins.map.MapPlugin;
import org.coreasm.engine.plugins.modularity.ModularityPlugin;
import org.coreasm.engine.plugins.number.NumberPlugin;
import org.coreasm.engine.plugins.options.OptionsPlugin;
import org.coreasm.engine.plugins.plotter.PlotterPlugin;
import org.coreasm.engine.plugins.predicatelogic.PredicateLogicPlugin;
import org.coreasm.engine.plugins.property.PropertyPlugin;
import org.coreasm.engine.plugins.queue.QueuePlugin;
import org.coreasm.engine.plugins.sandbox.SandboxPlugin;
import org.coreasm.engine.plugins.schedulingpolicies.SchedulingPoliciesPlugin;
import org.coreasm.engine.plugins.set.SetPlugin;
import org.coreasm.engine.plugins.signature.SignaturePlugin;
import org.coreasm.engine.plugins.stack.StackPlugin;
import org.coreasm.engine.plugins.step.StepPlugin;
import org.coreasm.engine.plugins.string.StringPlugin;
import org.coreasm.engine.plugins.time.TimePlugin;
import org.coreasm.engine.plugins.tree.TreePlugin;
import org.coreasm.engine.plugins.turboasm.TurboASMPlugin;

import de.prob.ui.api.ITool;

public class CoreAsmTool implements ITool {
	private CoreASMEngine e;

	private class AsmToolEngine extends Engine {
		@Override
		protected void loadCatalog() throws IOException {
			Plugin[] p2Load = { new AbstractionPlugin(), // AbstractionPlugin
					new ModularityPlugin(), // ModularityPlugin
					// BaarunPlugin
					new NumberPlugin(), // NumberPlugin
					new BagPlugin(), // BagPlugin
					new BasicASMPlugins(), // BasicASMPlugins
					new OptionsPlugin(), // OptionsPlugin
					new BlockRulePlugin(), // BlockRulePlugin
					new PlotterPlugin(), // PlotterPlugin
					new CaseRulePlugin(), // CaseRulePlugin
					new PredicateLogicPlugin(), // PredicateLogicPlugin
					new ChooseRulePlugin(), // ChooseRulePlugin
					new PropertyPlugin(), // PropertyPlugin
					new CollectionPlugin(), // CollectionPlugin
					new QueuePlugin(), // QueuePlugin
					new ConditionalRulePlugin(), // ConditionalRulePlugin
					new SandboxPlugin(), // SandboxPlugin
					new DebugInfoPlugin(), // DebugInfoPlugin
					new SchedulingPoliciesPlugin(), // SchedulingPolicies
					new ExtendRulePlugin(), // ExtendRulePlugin
					new SetPlugin(), // SetPlugin
					new ForallRulePlugin(), // ForallRulePlugin
					// SignalsPlugin
					new GrammarPlugin(), // GrammarPlugin
					new SignaturePlugin(), // SignaturePlugin
					// GraphPlugin
					new StackPlugin(), // StackPlugin
					new IOPlugin(), // IOPlugin
					new StandardPlugins(), // StandardPlugins
					// JasminePlugin
					new StepPlugin(), // StepPlugin
					new KernelExtensionsPlugin(), // KernelExtensionsPlugin
					new StringPlugin(), // StringPlugin
					new LetRulePlugin(), // LetRulePlugin
					new TabBlocksPlugin(), // TabBlocksPlugin
					new ListPlugin(), // ListPlugin
					new TimePlugin(), // TimePlugin
					new MapPlugin(), // MapPlugin
					new TreePlugin(), // TreePlugin
					// MathPlugin
					new TurboASMPlugin() // TurboASMPlugin
			};

			for (Plugin p : p2Load) {
				allPlugins.put(p.getName(), p);
			}

		}
	}

	public CoreAsmTool(Reader specification) {
		if (e == null)
			e = new AsmToolEngine();

		if (specification != null)
			try {
				e.loadSpecification(specification);
				e.waitWhileBusy();
				e.initialize();
				e.waitWhileBusy();
			} catch (Exception e) {
				// TODO:
			}
	}

	// requires a coreAsm specification to work
	public CoreAsmTool(String specification) {
		this(new StringReader(specification));
	}

	@Override
	public boolean canBacktrack() {
		return false;
	}

	@Override
	public String doStep(String arg0, String arg1, String... arg2) {
		e.step();
		e.waitWhileBusy();

		StringBuffer output = new StringBuffer();
		String state = getCurrentState();

		output.append("{ \"mode\": \"" + e.getEngineMode().toString()
				+ "\", \"state\": " + state);

		return output.toString();
	}

	/**
	 * Returns the value of a location
	 */
	@Override
	public String evaluate(String stateref, String lname) {
		State state = e.getState();
		Map<String, FunctionElement> funcs = state.getFunctions();

		String value = "";

		FunctionElement f = funcs.get(lname);

		String name = lname;
		HashMap<String, String> val = new HashMap<String, String>();

		for (Location l : f.getLocations(name)) {

			String tempStr = "";
			for (Element arg : l.args)
				tempStr = tempStr + arg.denotation() + ", ";
			if (tempStr.length() > 0)
				tempStr = tempStr.substring(0, tempStr.length() - 2);

			value = (f.getValue(l.args).denotation());

			val.put(tempStr, value);

		}

		String json = "{";
		for (String k : val.keySet()) {
			json += "\"" + (k == "" ? lname : k) + "\":" + "\"" + val.get(k)
					+ "\", ";

		}
		if (val.size() > 0)
			json = json.substring(0, json.length() - 2);
		json += "}";

		return json;
	}

	@Override
	public String getCurrentState() {
		Map<String, AbstractUniverse> universeEntries = e.getState()
				.getUniverses();

		/*
		 * { "backgrounds" : [ "b1", ..., "bn" ], "universes" : {"u1" : ["val",
		 * ...]}, ..., {"un" ...}, "functions" : {"f1" : { "params": ["p1", ..,
		 * "pn"], "value": "val" } , ..., {"fn" ...} }
		 */

		StringWriter backgroundsWriter = new StringWriter();
		StringWriter universesWriter = new StringWriter();
		StringWriter functionsWriter = new StringWriter();

		backgroundsWriter.write("\"backgrounds\": [");
		universesWriter.write("\"universes\" : {");

		for (String key : universeEntries.keySet()) {
			AbstractUniverse o = universeEntries.get(key);
			// bg elements
			if (o instanceof BackgroundElement) {
				backgroundsWriter.write("\"" + key + "\", ");
			}
			// universes elements
			if (o instanceof UniverseElement) {
				universesWriter.write("\"" + key + "\" : [");
				UniverseElement ue = (UniverseElement) o;

				StringBuffer str = new StringBuffer();
				String lname = o.toString();
				Set<Location> locations = ue.getLocations(lname);

				for (Location l : locations) {
					if (ue.getValue(l.args).equals(BooleanElement.TRUE)) {
						if (l.args.size() > 0) {
							str.append("\"" + l.args.get(0).denotation()
									+ "\", ");
						}
					}
				}

				universesWriter.write(str.length() > 4 ? str.substring(0,
						str.length() - 2) : "");
				universesWriter.write("], ");
			}
		}

		// functions
		Map<String, FunctionElement> felems = e.getState().getFunctions();

		functionsWriter.write("\"functions\" : {");
		for (String key : felems.keySet()) {
			FunctionElement f = felems.get(key);

			if (f.isModifiable() && key != "output") {
				String params = "[";
				String value = "[";
				Set<Location> locs = f.getLocations(key);

				for (Location l : locs) {

					for (Element arg : l.args) {
						params += "\"" + arg.denotation() + "\", ";
					}

					value += "\"" + f.getValue(l.args).denotation() + "\", ";
				}

				if (params.endsWith(", "))
					params = params.substring(0, params.length() - 2);
				if (value.endsWith(", "))
					value = value.substring(0, value.length() - 2);

				value += "]";
				params += "]";

				functionsWriter.write("\"" + key + "\": { \"params\": "
						+ params + ", \"value\": " + value + " }, ");
			}
		}

		backgroundsWriter.write("]");
		universesWriter.write("}");
		functionsWriter.write("}");

		String bg = backgroundsWriter.toString();
		if (bg.length() > 0)
			bg = bg.substring(0, bg.length() - 3) + "]";

		String univ = universesWriter.toString();
		if (univ.endsWith(", }"))
			univ = univ.substring(0, univ.length() - 3) + "}";

		String func = functionsWriter.toString();
		if (func.endsWith(", }"))
			func = func.substring(0, func.length() - 3) + "}";

		try {
			backgroundsWriter.close();
			universesWriter.close();
			functionsWriter.close();
		} catch (IOException e1) {
			// TODO check how to handle properly
			e1.printStackTrace();
		}

		return "{" + bg + ", " + univ + ", " + func + "}";

	}

	@Override
	public List<String> getErrors(String arg0, String arg1) {
		List<String> errors = new ArrayList<String>();
		errors.add(e.getEngineMode().toString());

		// TODO: get better errors
		return errors;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
