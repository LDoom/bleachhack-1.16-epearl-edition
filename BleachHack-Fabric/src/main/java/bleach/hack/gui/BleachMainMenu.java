package bleach.hack.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.fabricmc.loader.api.FabricLoader;
import org.lwjgl.opengl.GL11;

import bleach.hack.BleachHack;
import bleach.hack.gui.particle.ParticleManager;
import bleach.hack.gui.widget.BleachCheckbox;
import bleach.hack.gui.window.AbstractWindowScreen;
import bleach.hack.gui.window.Window;
import bleach.hack.utils.LoginManager;
import bleach.hack.utils.file.BleachFileMang;
import bleach.hack.utils.file.BleachGithubReader;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

public class BleachMainMenu extends AbstractWindowScreen {
	
	private ParticleManager particleMang = new ParticleManager();
	public static boolean customTitleScreen = true;

	public static String splash = "";
	public static List<String> versions = new ArrayList<>();
	
	/* login manager variables */
	public TextFieldWidget userField;
	public TextFieldWidget passField;
	public BleachCheckbox checkBox;
	
	public String loginResult = "";
	
	private List<List<String>> entries = new ArrayList<>();
	
	public BleachMainMenu() {
		super(new TranslatableText("narrator.screen.title"));
	}
	
	public void init() {
		windows.clear();
		windows.add(new Window(width / 8,
				height / 8,
				width / 8 + (width - width / 4),
				height / 8 + (height - height / 4), "BleachHack", new ItemStack(Items.MUSIC_DISC_CAT)));
		windows.add(new Window(width / 8 + 15,
				height / 8 + 15,
				width / 8 + 15 + (width - width / 2),
				height / 8 + 15 + (height - height / 2), "Login Manager", new ItemStack(Items.PAPER), true));
		windows.add(new Window(width / 8 + 30,
				height / 8 + 30,
				width / 8 + 30 + (width - width / 2),
				height / 8 + 30 + (height - height / 2), "Accounts", new ItemStack(Items.WRITABLE_BOOK), true));
	    
	    if(versions.isEmpty()) {
	    	versions.clear();
		    versions.addAll(BleachGithubReader.readFileLines("latestversion.txt"));
	    }
	    
	    if(splash == "") {
	    	List<String> sp = BleachGithubReader.readFileLines("splashes.txt");
		    splash = !sp.isEmpty() ? sp.get(new Random().nextInt(sp.size())) : "";
	    }
	    
	    entries.clear();
		BleachFileMang.createFile("logins.txt");
		
		for(String s: BleachFileMang.readFileLines("logins.txt")) {
			entries.add(new ArrayList<>(Arrays.asList(s.split(":"))));
		}
		
		int x = windows.get(1).x1,
				y = windows.get(1).y1 - 10,
				w = width - width / 2,
				h = height - height / 2;
		
		if(userField == null) userField = new TextFieldWidget(font, x + w / 2 - 98, y + h / 4 + 10, 196, 18, "");
		if(passField == null) passField = new TextFieldWidget(font, x + w / 2 - 98, y + h / 4 + 40, 196, 18, "");
		userField.x = x + w / 2 - 98;
		userField.y = y + h / 4 + 10;
		passField.x = x + w / 2 - 98;
		passField.y = y + h / 4 + 40;
		if(checkBox == null) checkBox = new BleachCheckbox(x + w / 2 - 99, y + h / 4 + 63, "Save Login", false);
		checkBox.x = x + w / 2 - 99;
		checkBox.y = y + h / 4 + 63;
		userField.setMaxLength(32767);
		passField.setMaxLength(32767);
		
		
	}
	
	public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
		//if(windows.get(0).x1 != width / 8 || windows.get(0).y1 != height / 8) minecraft.openScreen(new BleachMainMenu());
		
		this.renderBackground();
		fill(0, 0, width, height, 0xff008080);
		
		int copyWidth = this.font.getStringWidth("Copyright Mojang AB. Do not distribute!") + 2;
		
		font.drawWithShadow("Copyright Mojang AB. Do not distribute!", width - copyWidth, height - 24, -1);
		font.drawWithShadow("Fabric: " + FabricLoader.getInstance().getModContainer("fabricloader").get().getMetadata().getVersion().getFriendlyString(), 4, height - 44, -1);
		font.drawWithShadow("Minecraft " + SharedConstants.getGameVersion().getName(), 4, height - 34, -1);
		font.drawWithShadow("Logged in as: §a" + minecraft.getSession().getUsername(), 4, height - 24, -1);
		
		try {
			if(Integer.parseInt(versions.get(1)) > BleachHack.INTVERSION) {
				drawCenteredString(this.font, "§cOutdated BleachHack Version!", width/2, 2, -1);
				drawCenteredString(this.font,"§4[" + versions.get(0) + " > " + BleachHack.VERSION + "]", width/2, 11, -1);
			}
		}catch(Exception e) {}
		
		drawButton("", 0, height - 14, width, height);
		drawButton("§cX", 0, height - 13, 20, height - 1);
		
		int wid = 20;
		for(Window w: windows) {
			if(w.closed) continue;
			Screen.fill(wid, height - 13, wid + 80 - 1, height - 1 - 1, 0xffb0b0b0);
			Screen.fill(wid + 1, height - 13 + 1, wid + 80, height - 1, 0xff000000);
			Screen.fill(wid + 1, height - 13 + 1, wid + 80 - 1, height - 1 - 1, (w.selected ? 0xffb0b0b0 : 0xff858585));
			font.draw(w.title, wid + 2, height - 11, 0x000000);
			wid += 80;
		}
		
		super.render(p_render_1_, p_render_2_, p_render_3_);
		
		particleMang.addParticle(p_render_1_, p_render_2_);
		particleMang.renderParticles();
		
	}
	
	public void onRenderWindow(int window, int mX, int mY) {
		super.onRenderWindow(window, mX, mY);
		
		if(window == 0) {
			int x = windows.get(0).x1,
					y = windows.get(0).y1 - 10,
					w = width - width / 4,
					h = height - height / 4;
			
			int maxY = MathHelper.clamp(y + h / 4 + 129, 0, y + h - 12);
			drawButton(I18n.translate("menu.singleplayer"), x + w / 2 - 100, y + h / 4 + 48, x + w / 2 + 100, y + h / 4 + 68);
			drawButton(I18n.translate("menu.multiplayer"), x + w / 2 - 100, y + h / 4 + 72, x + w / 2 + 100, y + h / 4 + 92);
			drawButton("MC Menu", x + w / 2 - 100, y + h / 4 + 96, x + w / 2 - 2, y + h / 4 + 116);
			drawButton("Login Manager", x + w / 2 + 2, y + h / 4 + 96, x + w / 2 + 100, y + h / 4 + 116);
			drawButton(I18n.translate("menu.options"), x + w / 2 - 100, maxY, x + w / 2 - 2, maxY + 20);
			drawButton(I18n.translate("menu.quit"), x + w / 2 + 2, maxY, x + w / 2 + 100, maxY + 20);
			
			GL11.glPushMatrix();
			GL11.glScaled(3, 3, 0);
			drawString(this.font, "BleachHack", (x + w/2 - 81)/3, (y + h/4 - 15)/3, 0xffc0e0);
			GL11.glScaled(1d/3d, 1d/3d, 0);
			
			GL11.glScaled(1.5, 1.5, 0);
			drawCenteredString(this.font, BleachHack.VERSION, (int)((x + w/2)/1.5), (int)((y + h/4 + 6)/1.5), 0xffc050);
			GL11.glScaled(1d/1.5d, 1d/1.5d, 0);
			GL11.glPopMatrix();
			
			GL11.glPushMatrix();
			GL11.glTranslated(x + w / 2 + 80, y + h/4 + 8, 0.0F);
			GL11.glRotatef(-20.0F, 0.0F, 0.0F, 1.0F);
	        float float_4 = 1.8F - MathHelper.abs(MathHelper.sin((float)(Util.getMeasuringTimeMs() % 1000L) / 1000.0F * 6.2831855F) * 0.1F);
	        float_4 = float_4 * 60.0F / (float)(font.getStringWidth(splash) + 32);
	        GL11.glScalef(float_4, float_4, float_4);
	        this.drawCenteredString(font, splash, 0, -8, 16776960);
	        GL11.glPopMatrix();
		}else if(window == 1) {
			int x = windows.get(1).x1,
					y = windows.get(1).y1 - 10,
					w = width - width / 2,
					h = height - height / 2;
			
			drawString(font, "Email: ", x + w / 2 - 130, y + h / 4 + 15, 0xC0C0C0);
			drawString(font, "Password: ", x + w / 2 - 154, y + h / 4 + 45, 0xC0C0C0);
			
			drawString(font, loginResult == "" ? "" : "|  " + loginResult, x + w / 2 - 24, y + h / 4 + 65, 0xC0C0C0);
			
			userField.x = x + w / 2 - 98;
			userField.y = y + h / 4 + 10;
			passField.x = x + w / 2 - 98;
			passField.y = y + h / 4 + 40;
			checkBox.x = x + w / 2 - 99;
			checkBox.y = y + h / 4 + 63;
			
			drawButton("Done", x + w / 2 - 100, y + h / 3 + 84, x + w / 2 + 100, y + h / 3 + 104);
			drawButton("Accounts", x + w / 2 - 100, y + h / 3 + 62, x + w / 2 - 2, y + h / 3 + 82);
			drawButton("Login", x + w / 2 + 2, y + h / 3 + 62, x + w / 2 + 100, y + h / 3 + 82);
			
			userField.render(mX, mY, 1f);
			passField.render(mX, mY, 1f);
			checkBox.render(mX, mY, 1f);
		}else if(window == 2) {
			int x = windows.get(2).x1,
					y = windows.get(2).y1 - 10,
					w = width - width / 2,
					h = height - height / 2;
			
			drawCenteredString(font, "§cTemprary™ alt manager", x + w / 2, y + h / 4 - 30, -1);
			drawCenteredString(font, "§4(accounts stored in plaintext for now)", x + w / 2, y + h / 4 - 20, -1);
			
			int c = 0;
			for(List<String> e: entries) {
				String text = (e.size() > 1 ? "§a" + e.get(0) + ":***" : "§6" + e.get(0));
				int length = minecraft.textRenderer.getStringWidth(text);
				
				fill(x + w / 2 - length / 2 - 1, y + h / 4 + c - 2, x + w / 2 + length / 2 + 1, y + h / 4 + c - 1, 0xFF303030);
				fill(x + w / 2 - length / 2 - 1, y + h / 4 + c + 9, x + w / 2 + length / 2 + 1, y + h / 4 + c + 10, 0xFF303030);
				fill(x + w / 2 - length / 2 - 2, y + h / 4 + c - 2, x + w / 2 - length / 2 - 1, y + h / 4 + c + 10, 0xFF303030);
				fill(x + w / 2 + length / 2 + 1, y + h / 4 + c - 2, x + w / 2 + length / 2 + 2, y + h / 4 + c + 10, 0xFF303030);
				drawCenteredString(font, "§cx", x + w / 2 + length / 2 + 9, y + h / 4 + c, -1);
				drawCenteredString(font, text, x + w / 2, y + h / 4 + c, -1);
				c += 14;
			}
		}
	}
	
	public boolean mouseClicked(double double_1, double double_2, int int_1) {
		if(double_1 > 0 && double_1 < 20 && double_2 > height - 14 && double_2 < height) {
			minecraft.openScreen(this);
		}
		
		if(double_2 > height - 14 && double_2 < height) {
			int count = 0;
			for(Window w: windows) {
				if(!w.closed) count++;
				if(count == (int) ((double_1 + 60) / 80)) {
					selectWindow(windows.indexOf(w));
					//w.selected = true;
					break;
				}
			}
		}
		
		if(!windows.get(0).closed && windows.get(0).selected) {
			int x = windows.get(0).x1,
					y = windows.get(0).y1 - 10,
					w = width - width / 4,
					h = height - height / 4;
			
			if(double_1 > x + w / 2 - 100 && double_1 < x + w / 2 + 100 && double_2 > y + h / 4 + 48 && double_2 < y + h / 4 + 68) {
				minecraft.openScreen(new SelectWorldScreen(this));
			}
			if(double_1 > x + w / 2 - 100 && double_1 < x + w / 2 + 100 && double_2 > y + h / 4 + 72 && double_2 < y + h / 4 + 92) {
				minecraft.openScreen(new MultiplayerScreen(this));
			}
			if(double_1 > x + w / 2 - 100 && double_1 < x + w / 2 - 2 && double_2 > y + h / 4 + 96 && double_2 < y + h / 4 + 116) {
				customTitleScreen = !customTitleScreen;
				minecraft.openScreen(new TitleScreen(false));
			}
			if(double_1 > x + w / 2 + 2 && double_1 < x + w / 2 + 100 && double_2 > y + h / 4 + 96 && double_2 < y + h / 4 + 116) {
				windows.get(1).closed = false;
				selectWindow(1);
			}
			int maxY = MathHelper.clamp(y + h / 4 + 129, 0, y + h - 12);
			if(double_1 > x + w / 2 - 100 && double_1 < x + w / 2 - 2 && double_2 > maxY && double_2 < maxY + 20) {
				minecraft.openScreen(new SettingsScreen(this, minecraft.options));
			}
			if(double_1 > x + w / 2 + 2 && double_1 < x + w / 2 + 100 && double_2 > maxY && double_2 < maxY + 20) {
				minecraft.close();
			}
		}
		
		if(!windows.get(1).closed && windows.get(1).selected) {
			int x = windows.get(1).x1,
					y = windows.get(1).y1 - 10,
					w = width - width / 2,
					h = height - height / 2;
			
			if(double_1 > userField.x && double_1 < userField.x+userField.getWidth() && double_2 > userField.y && double_2 < userField.y+20) {
				userField.changeFocus(true);
				if(passField.isFocused()) passField.changeFocus(true);
			}
			if(double_1 > passField.x && double_1 < passField.x+passField.getWidth() && double_2 > passField.y && double_2 < passField.y+20) {
				passField.changeFocus(true);
				if(userField.isFocused()) userField.changeFocus(true);
			}
			if(double_1 > checkBox.x && double_1 < checkBox.x+10 && double_2 > checkBox.y && double_2 < checkBox.y+10) {
				checkBox.checked = !checkBox.checked;
			}
			if(double_1 > x + w / 2 - 100 && double_1 < x + w / 2 + 100 && double_2 > y + h / 3 + 84 && double_2 < y + h / 3 + 104) {
				windows.get(1).closed = true;
				selectWindow(1);
			}
			if(double_1 > x + w / 2 - 100 && double_1 < x + w / 2 - 2 && double_2 > y + h / 3 + 62 && double_2 < y + h / 3 + 82) {
				windows.get(2).closed = false;
				selectWindow(2);
			}
			if(double_1 > x + w / 2 + 2 && double_1 < x + w / 2 + 100 && double_2 > y + h / 3 + 62 && double_2 < y + h / 3 + 82) {
				for(String s: BleachFileMang.readFileLines("logins.txt")) {
					entries.add(new ArrayList<>(Arrays.asList(s.split(":"))));
				}
				
				loginResult = LoginManager.login(userField.getText(), passField.getText());
				String text = userField.getText() + ":" + passField.getText();
				if(checkBox.checked && (loginResult.equals("§aLogin Successful")
						|| loginResult.equals("§6Logged in as an unverified account"))
						&& !entries.contains(new ArrayList<>(Arrays.asList(text.split(":"))))) {
					entries.add(new ArrayList<>(Arrays.asList(text.split(":"))));
					BleachFileMang.createFile("logins.txt");
					BleachFileMang.appendFile(text, "logins.txt");
				}
			}
		}
		
		if(!windows.get(2).closed && windows.get(2).selected) {
			int x = windows.get(2).x1,
					y = windows.get(2).y1 - 10,
					w = width - width / 2,
					h = height - height / 2;
			
			int c = 0;
			for(List<String> e: new ArrayList<>(entries)) {
				String text = (e.size() > 1 ? "§a" + e.get(0) + ":***" : "§6" + e.get(0));
				int lenght = minecraft.textRenderer.getStringWidth(text);
				
				if(double_1 > x + w/2-lenght/2-1 && double_1 < x + w/2+lenght/2+1 && double_2 > y + h/4+c*14-2 && double_2 < y + h/4+c*14+11) {
					try{ userField.setText(e.get(0));
					}catch(Exception e1) { userField.setText(""); }
					try{ passField.setText(e.get(1));
					}catch(Exception e1) { passField.setText(""); }
					windows.get(2).closed = true;
					windows.get(1).closed = false;
					selectWindow(1);
				}
				
				if(double_1 > x + w/2+lenght/2+4 && double_1 < x + w/2+lenght/2+14 && double_2 > y + h/4+c*14-2 && double_2 < y + h/4+c*14+11) {
					int c1 = 0;
					String lines = "";
					for(String l: BleachFileMang.readFileLines("logins.txt")) {
						if(l.trim().replace("\r", "").replace("\n", "").isEmpty()) continue;
						if (c1 != c) lines += l + "\r\n";
						c1++;
					}
					BleachFileMang.createEmptyFile("logins.txt");
					BleachFileMang.appendFile(lines, "logins.txt");
					break;
				}
				c++;
			}
		}
		
		
		return super.mouseClicked(double_1, double_2, int_1);
	}
	
	public boolean charTyped(char char_1, int int_1) {
		if(!windows.get(1).closed) {
			if(userField.isFocused()) userField.charTyped(char_1, int_1);
			if(passField.isFocused()) passField.charTyped(char_1, int_1);
		}
		
		return super.charTyped(char_1, int_1);
	}
	
	public void tick() {
		if(!windows.get(1).closed) {
			userField.tick();
			passField.tick();
		}
	}
	
	public boolean keyPressed(int int_1, int int_2, int int_3) {
		if(!windows.get(1).closed) {
			if(userField.isFocused()) userField.keyPressed(int_1, int_2, int_3);
			if(passField.isFocused()) passField.keyPressed(int_1, int_2, int_3);
		}
		
		return super.keyPressed(int_1, int_2, int_3);
	}
}