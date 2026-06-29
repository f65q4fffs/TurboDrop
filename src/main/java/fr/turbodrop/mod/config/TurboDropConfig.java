package fr.turbodrop.mod.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class TurboDropConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.IntValue MAX_STACKS_PER_TICK;
    public static final ModConfigSpec.BooleanValue ENABLE_CONFIRMATION;

    // Raccourcis Souris et Clavier
    public static final ModConfigSpec.BooleanValue ENABLE_MOUSE_SHORTCUT;
    public static final ModConfigSpec.ConfigValue<String> MOUSE_SHORTCUT_MODIFIER;

    // Drop de case unique (Option A)
    public static final ModConfigSpec.BooleanValue DROP_ENTIRE_SLOT;
    public static final ModConfigSpec.BooleanValue USE_VANILLA_MAX_STACK;
    public static final ModConfigSpec.IntValue CUSTOM_DROP_LIMIT;

    static {
        BUILDER.push("General");

        MAX_STACKS_PER_TICK = BUILDER
                .comment("Nombre maximum d'items (stacks) ejectes par tick de jeu.")
                .translation("config.turbodrop.max_stacks_per_tick")
                .defineInRange("maxStacksPerTick", 10, 1, 1000);

        ENABLE_CONFIRMATION = BUILDER
                .comment("Activer l'overlay de confirmation pour les stockages volumineux.")
                .translation("config.turbodrop.enable_confirmation")
                .define("enableConfirmation", true);

        ENABLE_MOUSE_SHORTCUT = BUILDER
                .comment("Activer le raccourci de clic souris pour vider les inventaires.")
                .translation("config.turbodrop.enable_mouse_shortcut")
                .define("enableMouseShortcut", true);

        MOUSE_SHORTCUT_MODIFIER = BUILDER
                .comment("Modificateur requis pour le raccourci souris. Valeurs possibles : ALT, CONTROL, SHIFT, NONE")
                .translation("config.turbodrop.mouse_shortcut_modifier")
                .define("mouseShortcutModifier", "ALT");

        DROP_ENTIRE_SLOT = BUILDER
                .comment("Jeter l'integralite de la case (mode ALL). Si False, utilise les options ci-dessous.")
                .translation("config.turbodrop.drop_entire_slot")
                .define("dropEntireSlot", true);

        USE_VANILLA_MAX_STACK = BUILDER
                .comment("Jeter au maximum la taille d'un stack d'origine (ex: 16 pour enderpearls, 64 pour de la terre).")
                .translation("config.turbodrop.use_vanilla_max_stack")
                .define("useVanillaMaxStack", false);

        CUSTOM_DROP_LIMIT = BUILDER
                .comment("Quantite maximale a jeter si dropEntireSlot et useVanillaMaxStack sont a False.")
                .translation("config.turbodrop.custom_drop_limit")
                .defineInRange("customDropLimit", 64, 1, 1000000);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }




}

