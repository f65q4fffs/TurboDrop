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

    // Drop de case unique
    public enum SlotDropBehavior {
        ALL,
        VANILLA_STACK,
        CUSTOM_LIMIT
    }
    public static final ModConfigSpec.EnumValue<SlotDropBehavior> SLOT_DROP_BEHAVIOR;
    public static final ModConfigSpec.IntValue CUSTOM_SLOT_DROP_LIMIT;

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

        SLOT_DROP_BEHAVIOR = BUILDER
                .comment("Comportement lors du drop d'une seule case survolée. Valeurs possibles :\n" +
                         "ALL - Vide tout d'un coup (même si > 64)\n" +
                         "VANILLA_STACK - Vide au maximum la taille d'un stack d'origine (ex: 16 pour enderpearls, 64 pour de la terre)\n" +
                         "CUSTOM_LIMIT - Vide au maximum la quantite de customSlotDropLimit")
                .translation("config.turbodrop.slot_drop_behavior")
                .defineEnum("slotDropBehavior", SlotDropBehavior.ALL);

        CUSTOM_SLOT_DROP_LIMIT = BUILDER
                .comment("Nombre maximum d'items a drop si slotDropBehavior est a CUSTOM_LIMIT.")
                .translation("config.turbodrop.custom_slot_drop_limit")
                .defineInRange("customSlotDropLimit", 64, 1, 1000000);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }


}

