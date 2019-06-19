package org.iota.jota;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class MamJs implements Mam {
	
	private Object lib;
	private ScriptEngine jsEngine;

	public MamJs() {
		ScriptEngineManager factory = new ScriptEngineManager();
        this.jsEngine = factory.getEngineByName("nashorn");
		Object mamLib = executeScript(jsEngine, "mam.js");
		
		if (null == mamLib) {
			// :(
		} else {
			this.lib = mamLib;
		}
	}
	
	private Object executeScript(ScriptEngine jsEngine, String scriptName) {
	    try {
	    	InputStream stream = this.getClass().getResourceAsStream(scriptName);
	        Reader script = new InputStreamReader(stream);

	        return jsEngine.eval(script);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	return null;
	    }
	}
	
	private Object call(String method, Object... params) {
		if (null == lib) {
			return null;
		}
		
		Invocable inv = (Invocable) jsEngine;
		try {
			return inv.invokeFunction(method, params);
		} catch (NoSuchMethodException | ScriptException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String wat() {
		return call("wat").toString();
	}
}
