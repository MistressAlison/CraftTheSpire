package CraftTheSpire;

import CraftTheSpire.components.AbstractComponent;
import CraftTheSpire.rewards.AbstractRewardLogic;
import CraftTheSpire.ui.InventoryButton;
import CraftTheSpire.util.InventoryManager;
import CraftTheSpire.util.TextureLoader;
import basemod.*;
import basemod.abstracts.CustomSavable;
import basemod.interfaces.EditKeywordsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostCreateStartingRelicsSubscriber;
import basemod.interfaces.PostInitializeSubscriber;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.evacipated.cardcrawl.mod.stslib.Keyword;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.localization.UIStrings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@SpireInitializer
public class CraftTheSpireMod implements
        EditStringsSubscriber,
        PostInitializeSubscriber,
        EditKeywordsSubscriber,
        PostCreateStartingRelicsSubscriber {
    // Make sure to implement the subscribers *you* are using (read basemod wiki). Editing cards? EditCardsSubscriber.
    // Making relics? EditRelicsSubscriber. etc., etc., for a full list and how to make your own, visit the basemod wiki.
    public static final Logger logger = LogManager.getLogger(CraftTheSpireMod.class.getName());
    private static String modID;

    // Mod-settings settings. This is if you want an on/off savable button
    public static SpireConfig CTSConfig;
    public static String FILE_NAME = "CraftTheSpireConfig";

    public static final String ENABLE_MODS_SETTING = "enableMods";
    public static boolean enableMods = true; // The boolean we'll be setting on/off (true/false)

    public static final String DROP_PROBABILITY = "dropChance";
    public static int dropProbability = 50;

    public static final String COMMON_WEIGHT = "commonWeight";
    public static int commonWeight = 5;

    public static final String UNCOMMON_WEIGHT = "uncommonWeight";
    public static int uncommonWeight = 3;

    public static final String RARE_WEIGHT = "rareWeight";
    public static int rareWeight = 1;

    public static final String DROP_WHEN_REMOVED = "dropWhenRemoved";
    public static boolean dropOnMasterDeckRemoval = true;

    //Cardmod Lists
    public static final ArrayList<AbstractComponent> commonComponents = new ArrayList<>();
    public static final ArrayList<AbstractComponent> uncommonComponents = new ArrayList<>();
    public static final ArrayList<AbstractComponent> rareComponents = new ArrayList<>();
    public static final HashMap<String, AbstractComponent> componentMap = new HashMap<>();

    //List of orbies
    public static final ArrayList<AbstractPlayer.PlayerClass> ORB_CHARS = new ArrayList<>(Collections.singletonList(AbstractPlayer.PlayerClass.DEFECT));


    //This is for the in-game mod settings panel.
    public static UIStrings uiStrings;
    public static String[] TEXT;
    public static String[] EXTRA_TEXT;
    private static final String AUTHOR = "Mistress Alison";
    
    // =============== INPUT TEXTURE LOCATION =================
    
    //Mod Badge - A small icon that appears in the mod settings menu next to your mod.
    public static final String BADGE_IMAGE = "CraftTheSpireResources/images/Badge.png";
    
    // =============== /INPUT TEXTURE LOCATION/ =================
    
    
    // =============== SUBSCRIBE, CREATE THE COLOR_GRAY, INITIALIZE =================
    
    public CraftTheSpireMod() {
        logger.info("Subscribe to BaseMod hooks");
        
        BaseMod.subscribe(this);
      
        setModID("CraftTheSpire");
        
        logger.info("Done subscribing");

        // This loads the mod settings.
        // The actual mod Button is added below in receivePostInitialize()
        logger.info("Adding mod settings");
        // This loads the mod settings.
        // The actual mod Button is added below in receivePostInitialize()
        Properties CTSDefaultSettings = new Properties();
        CTSDefaultSettings.setProperty(ENABLE_MODS_SETTING, Boolean.toString(enableMods));
        CTSDefaultSettings.setProperty(DROP_PROBABILITY, String.valueOf(dropProbability));
        CTSDefaultSettings.setProperty(COMMON_WEIGHT, String.valueOf(commonWeight));
        CTSDefaultSettings.setProperty(UNCOMMON_WEIGHT, String.valueOf(uncommonWeight));
        CTSDefaultSettings.setProperty(RARE_WEIGHT, String.valueOf(rareWeight));
        CTSDefaultSettings.setProperty(DROP_WHEN_REMOVED, Boolean.toString(dropOnMasterDeckRemoval));
        try {
            CTSConfig = new SpireConfig(modID, FILE_NAME, CTSDefaultSettings);
            enableMods = CTSConfig.getBool(ENABLE_MODS_SETTING);
            dropProbability = CTSConfig.getInt(DROP_PROBABILITY);
            commonWeight = CTSConfig.getInt(COMMON_WEIGHT);
            uncommonWeight = CTSConfig.getInt(UNCOMMON_WEIGHT);
            rareWeight = CTSConfig.getInt(RARE_WEIGHT);
            dropOnMasterDeckRemoval = CTSConfig.getBool(DROP_WHEN_REMOVED);
        } catch (IOException e) {
            logger.error("Card Augments SpireConfig initialization failed:");
            e.printStackTrace();
        }
        logger.info("Card Augments CONFIG OPTIONS LOADED:");

        logger.info("Done adding mod settings");
        
    }

    public static void registerOrbCharacter(AbstractPlayer.PlayerClass clz) {
        ORB_CHARS.add(clz);
    }

    public static void registerComponents(AbstractComponent c) {
        componentMap.put(c.ID, c);
        switch (c.rarity) {
            case COMMON:
                commonComponents.add(c);
                break;
            case UNCOMMON:
                uncommonComponents.add(c);
                break;
            case RARE:
                rareComponents.add(c);
                break;
        }
        AbstractRewardLogic logic = c.spawnReward(1);
        BaseMod.registerCustomReward(logic.type, logic, logic);
    }

    public static void setModID(String ID) {
        modID = ID;
    }
    
    public static String getModID() {
        return modID;
    }
    
    public static void initialize() {
        logger.info("========================= Initializing Card Augments. =========================");
        CraftTheSpireMod craftTheSpireMod = new CraftTheSpireMod();
        logger.info("========================= /Card Augments Initialized/ =========================");
    }
    
    // ============== /SUBSCRIBE, CREATE THE COLOR_GRAY, INITIALIZE/ =================
    
    // =============== POST-INITIALIZE =================
    
    @Override
    public void receivePostInitialize() {
        logger.info("Loading badge image and mod options");

        //Grab the strings
        uiStrings = CardCrawlGame.languagePack.getUIString(makeID("ModConfigs"));
        EXTRA_TEXT = uiStrings.EXTRA_TEXT;
        TEXT = uiStrings.TEXT;
        // Create the Mod Menu
        ModPanel settingsPanel = new ModPanel();

        // Load the Mod Badge
        Texture badgeTexture = TextureLoader.getTexture(BADGE_IMAGE);
        BaseMod.registerModBadge(badgeTexture, EXTRA_TEXT[0], AUTHOR, EXTRA_TEXT[1], settingsPanel);

        //Get the longest slider text for positioning
        ArrayList<String> labelStrings = new ArrayList<>(Arrays.asList(TEXT));
        float sliderOffset = getSliderPosition(labelStrings.subList(2,3));
        labelStrings.clear();
        float currentYposition = 740f;
        float spacingY = 55f;

        /* //Used to set the enable the mod
        ModLabeledToggleButton enableModsButton = new ModLabeledToggleButton(TEXT[0],400.0f - 40f, currentYposition - 10f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                CTSConfig.getBool(ENABLE_MODS_SETTING), settingsPanel, (label) -> {}, (button) -> {
            CTSConfig.setBool(ENABLE_MODS_SETTING, button.enabled);
            enableMods = button.enabled;
            try {
                CTSConfig.save();} catch (IOException e) {e.printStackTrace();}
        });
        currentYposition -= spacingY;
        */

        //Used to allow component drops on master deck removal
        ModLabeledToggleButton dropOnMasterDeckRemovalButton = new ModLabeledToggleButton(TEXT[1],400.0f - 40f, currentYposition - 10f, Settings.CREAM_COLOR, FontHelper.charDescFont,
                CTSConfig.getBool(DROP_WHEN_REMOVED), settingsPanel, (label) -> {}, (button) -> {
            CTSConfig.setBool(DROP_WHEN_REMOVED, button.enabled);
            dropOnMasterDeckRemoval = button.enabled;
            try {
                CTSConfig.save();} catch (IOException e) {e.printStackTrace();}
        });
        currentYposition -= spacingY;

        //Used for probability of a mod being applied
        ModLabel probabilityLabel = new ModLabel(TEXT[2], 400f, currentYposition, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel, modLabel -> {});
        ModMinMaxSlider probabilitySlider = new ModMinMaxSlider("",
                400f + sliderOffset,
                currentYposition + 7f,
                0, 100, CTSConfig.getInt(DROP_PROBABILITY), "%.0f", settingsPanel, slider -> {
            CTSConfig.setInt(DROP_PROBABILITY, Math.round(slider.getValue()));
            dropProbability = Math.round(slider.getValue());
            try {
                CTSConfig.save();} catch (IOException e) {e.printStackTrace();}
        });
        currentYposition -= spacingY;

        /*//Used for common mod weight
        ModLabel commonLabel = new ModLabel(TEXT[2], 400f, currentYposition, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel, modLabel -> {});
        ModMinMaxSlider commonSlider = new ModMinMaxSlider("",
                400f + sliderOffset,
                currentYposition + 7f,
                1, 10, CTSConfig.getInt(COMMON_WEIGHT), "%.0f", settingsPanel, slider -> {
            CTSConfig.setInt(COMMON_WEIGHT, Math.round(slider.getValue()));
            commonWeight = Math.round(slider.getValue());
            try {
                CTSConfig.save();} catch (IOException e) {e.printStackTrace();}
        });
        currentYposition -= spacingY;

        //Used for uncommon mod weight
        ModLabel uncommonLabel = new ModLabel(TEXT[3], 400f, currentYposition, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel, modLabel -> {});
        ModMinMaxSlider uncommonSlider = new ModMinMaxSlider("",
                400f + sliderOffset,
                currentYposition + 7f,
                1, 10, CTSConfig.getInt(UNCOMMON_WEIGHT), "%.0f", settingsPanel, slider -> {
            CTSConfig.setInt(UNCOMMON_WEIGHT, Math.round(slider.getValue()));
            uncommonWeight = Math.round(slider.getValue());
            try {
                CTSConfig.save();} catch (IOException e) {e.printStackTrace();}
        });
        currentYposition -= spacingY;

        //Used for rare mod weight
        ModLabel rareLabel = new ModLabel(TEXT[4], 400f, currentYposition, Settings.CREAM_COLOR, FontHelper.charDescFont, settingsPanel, modLabel -> {});
        ModMinMaxSlider rareSlider = new ModMinMaxSlider("",
                400f + sliderOffset,
                currentYposition + 7f,
                1, 10, CTSConfig.getInt(RARE_WEIGHT), "%.0f", settingsPanel, slider -> {
            CTSConfig.setInt(RARE_WEIGHT, Math.round(slider.getValue()));
            rareWeight = Math.round(slider.getValue());
            try {
                CTSConfig.save();} catch (IOException e) {e.printStackTrace();}
        });
        currentYposition -= spacingY;*/

        settingsPanel.addUIElement(dropOnMasterDeckRemovalButton);
        //settingsPanel.addUIElement(enableModsButton);
        settingsPanel.addUIElement(probabilityLabel);
        settingsPanel.addUIElement(probabilitySlider);
        //settingsPanel.addUIElement(commonLabel);
        //settingsPanel.addUIElement(commonSlider);
        //settingsPanel.addUIElement(uncommonLabel);
        //settingsPanel.addUIElement(uncommonSlider);
        //settingsPanel.addUIElement(rareLabel);
        //settingsPanel.addUIElement(rareSlider);


        logger.info("Done loading badge Image and mod options");

        logger.info("Loading components...");

        new AutoAdd(modID)
                .packageFilter("CraftTheSpire.components")
                .any(AbstractComponent.class, (info, component) -> registerComponents(component));

        logger.info("Done loading components");

        BaseMod.addTopPanelItem(new InventoryButton());

        BaseMod.addSaveField(InventoryManager.saveString, new CustomSavable<TreeMap<String, Integer>>() {
            @Override
            public TreeMap<String, Integer> onSave() {
                return InventoryManager.items;
            }

            @Override
            public void onLoad(TreeMap<String, Integer> stringIntegerTreeMap) {
                InventoryManager.items.clear();
                InventoryManager.items.putAll(stringIntegerTreeMap);
            }
        });

        /*logger.info("Setting up dev commands");

        ConsoleCommand.addCommand("chimera", Chimera.class);

        logger.info("Done setting up dev commands");*/


        logger.info("Done");

    }

    //Get the longest text so all sliders are centered
    private float getSliderPosition (List<String> stringsToCompare) {
        float longest = 0;
        for (String s : stringsToCompare) {
            longest = Math.max(longest, FontHelper.getWidth(FontHelper.charDescFont, s, 1f /Settings.scale));
        }
        return longest + 40f;
    }
    
    // =============== / POST-INITIALIZE/ =================

    // ================ LOAD THE LOCALIZATION ===================

    private String loadLocalizationIfAvailable(String fileName) {
        if (!Gdx.files.internal(getModID() + "Resources/localization/" + Settings.language.toString().toLowerCase()+ "/" + fileName).exists()) {
            logger.info("Language: " + Settings.language.toString().toLowerCase() + ", not currently supported for " +fileName+".");
            return "eng" + "/" + fileName;
        } else {
            logger.info("Loaded Language: "+ Settings.language.toString().toLowerCase() + ", for "+fileName+".");
            return Settings.language.toString().toLowerCase() + "/" + fileName;
        }
    }

    // ================ /LOAD THE LOCALIZATION/ ===================

    // ================ LOAD THE TEXT ===================
    
    @Override
    public void receiveEditStrings() {
        logger.info("Beginning to edit strings for mod with ID: " + getModID());

        // UIStrings
        BaseMod.loadCustomStringsFile(UIStrings.class,
                getModID() + "Resources/localization/"+loadLocalizationIfAvailable("CraftTheSpire-UI-Strings.json"));

        // UIStrings
        BaseMod.loadCustomStringsFile(UIStrings.class,
                getModID() + "Resources/localization/"+loadLocalizationIfAvailable("CraftTheSpire-Component-Strings.json"));

        logger.info("Done editing strings");
    }
    
    // ================ /LOAD THE TEXT/ ===================
    
    // this adds "ModName:" before the ID of any card/relic/power etc.
    // in order to avoid conflicts if any other mod uses the same ID.
    public static String makeID(String idText) {
        return getModID() + ":" + idText;
    }

    public static String makeUIPath(String resourcePath) {
        return getModID() + "Resources/images/ui/" + resourcePath;
    }

    @Override
    public void receiveEditKeywords() {
        // Keywords on cards are supposed to be Capitalized, while in Keyword-String.json they're lowercase
        //
        // Multiword keywords on cards are done With_Underscores
        //
        // If you're using multiword keywords, the first element in your NAMES array in your keywords-strings.json has to be the same as the PROPER_NAME.
        // That is, in Card-Strings.json you would have #yA_Long_Keyword (#y highlights the keyword in yellow).
        // In Keyword-Strings.json you would have PROPER_NAME as A Long Keyword and the first element in NAMES be a long keyword, and the second element be a_long_keyword

        Gson gson = new Gson();
        String json = Gdx.files.internal(getModID()+"Resources/localization/"+loadLocalizationIfAvailable("CraftTheSpire-Keyword-Strings.json")).readString(String.valueOf(StandardCharsets.UTF_8));
        com.evacipated.cardcrawl.mod.stslib.Keyword[] keywords = gson.fromJson(json, com.evacipated.cardcrawl.mod.stslib.Keyword[].class);

        if (keywords != null) {
            for (Keyword keyword : keywords) {
                BaseMod.addKeyword(getModID().toLowerCase(), keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);
                //  getModID().toLowerCase() makes your keyword mod specific (it won't show up in other cards that use that word)
            }
        }
    }

    @Override
    public void receivePostCreateStartingRelics(AbstractPlayer.PlayerClass playerClass, ArrayList<String> arrayList) {
        InventoryManager.items.clear();
    }
}
