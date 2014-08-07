package de.prob.tools.coreasm;

import com.github.mustachejava.DefaultMustacheFactory
import com.github.mustachejava.Mustache
import com.google.common.base.Function
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject

import de.prob.statespace.OpInfo
import de.prob.ui.api.ITool
import de.prob.bmotion.*

class CoreAsmToolObserver implements IBMotionGroovyObserver {

	def BMotionStudioSession session
	def JsonElement json

	def CoreAsmToolObserver(BMotionStudioSession session, JsonElement json) {
		this.session = session
		this.json = json
	}

	public void update(ITool tool) {
		System.out.println("Update " + session);
	}
}