package fr.foxelia.proceduraldungeon.gui;

import fr.foxelia.proceduraldungeon.utilities.DungeonManager;
import fr.foxelia.tools.minecraft.ui.gui.GUI;

public interface DungeonInterface {

    public DungeonManager getDungeon();
    public GUI getGUI();

}
