/*
 * This file is part of ViaFabricPlus - https://github.com/FlorianMichael/ViaFabricPlus
 * Copyright (C) 2021-2023 FlorianMichael/EnZaXD and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.florianmichael.viafabricplus.screen.classicube;

import com.mojang.blaze3d.systems.RenderSystem;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.ClassiCubeAccountHandler;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.auth.ClassiCubeAccount;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.auth.ClassiCubeError;
import de.florianmichael.viafabricplus.definition.c0_30.classicube.auth.process.ILoginProcessHandler;
import de.florianmichael.viafabricplus.screen.ProtocolSelectionScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class ClassiCubeMFAScreen extends Screen {
    public final static ClassiCubeMFAScreen INSTANCE = new ClassiCubeMFAScreen();
    public Screen prevScreen;

    public ClassiCubeMFAScreen() {
        super(Text.literal("ClassiCube MFA"));
    }

    public static ClassiCubeMFAScreen get(final Screen prevScreen) {
        ClassiCubeMFAScreen.INSTANCE.prevScreen = prevScreen;
        ClassiCubeMFAScreen.INSTANCE.status = ClassiCubeError.LOGIN_CODE.description;
        return ClassiCubeMFAScreen.INSTANCE;
    }

    private TextFieldWidget mfaField;

    private Text status;

    @Override
    protected void init() {
        super.init();

        this.addDrawableChild(mfaField = new TextFieldWidget(textRenderer, width / 2 - 150, 70 + 10, 300, 20, Text.empty()));

        mfaField.setPlaceholder(Text.literal("MFA"));

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Login"), button -> {
            status = Text.translatable("classicube.viafabricplus.loading");
            ClassiCubeAccountHandler.INSTANCE.getAccount().login(new ILoginProcessHandler() {
                @Override
                public void handleMfa(ClassiCubeAccount account) {
                    // Not implemented in this case
                }

                @Override
                public void handleSuccessfulLogin(ClassiCubeAccount account) {
                    RenderSystem.recordRenderCall(() -> ClassiCubeServerListScreen.open(MinecraftClient.getInstance().currentScreen, this));
                }

                @Override
                public void handleException(Throwable throwable) {
                    status = Text.literal(throwable.getMessage());
                }
            }, mfaField.getText());
        }).position(width / 2 - 75, mfaField.getY() + (20 * 4) + 5).size(150, 20).build());
    }

    @Override
    public void tick() {
        super.tick();

        mfaField.tick();
    }

    @Override
    public void close() {
        ProtocolSelectionScreen.open(new MultiplayerScreen(new TitleScreen()));
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        drawCenteredTextWithShadow(matrices, this.textRenderer, this.title, this.width / 2, 70, 16777215);
        drawCenteredTextWithShadow(matrices, this.textRenderer, this.status, this.width / 2, 1, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
    }
}
