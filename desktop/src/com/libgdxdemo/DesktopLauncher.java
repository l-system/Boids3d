package com.libgdxdemo;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;


public class DesktopLauncher {
	public static void main(String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("libgdx-opengl");
		config.setWindowedMode(2560, 1600);

		config.setOpenGLEmulation(Lwjgl3ApplicationConfiguration.GLEmulation.GL30, 3, 0);
		config.setBackBufferConfig(8, 8, 8, 8, 16, 0, 4); // 4x MSAA

		new Lwjgl3Application(new BoidsApp(), config);

	}
}