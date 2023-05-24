package matachi.mapeditor;

import matachi.mapeditor.editor.Controller;

public class Main {
	public static void main(String[] args) {
		Controller controller;
		if (args.length > 0) {
			controller = new Controller(args[0]);
		}
		else {
			controller = new Controller();
		}
	}
}
