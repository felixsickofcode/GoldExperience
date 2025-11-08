package vnu.uet.goldexperience.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Story {

    public static class DialogueData {
        public final String npcName;
        public final String[] lines;
        public final boolean showBefore;

        public DialogueData(String npcName, String[] lines, boolean showBefore) {
            this.npcName = npcName;
            this.lines = lines;
            this.showBefore = showBefore;
        }
    }

    private static final Map<Integer, List<DialogueData>> STORY_MAP = new HashMap<>();

    static {
        // === CHAPTER 1: RUST SECTOR ===
        addDialogue(1, new DialogueData(
                "E.L.A.R.A",
                new String[]{
                        "[SYSTEM BOOTING...]",
                        "Unit ARKA-9 — operational.",
                        "I am E.L.A.R.A, an echo of Dr. Elyra — the one who once led Project ARKA.",
                        "Centuries have passed since humanity fell to war and poison.",
                        "Only ruins remain… and us — their creations.",
                        "Your first mission: restore the city’s Energy Station. Power is memory here.",
                        "Move with ARROW KEYS or MOUSE. Launch with SPACE or CLICK.",
                        "Break through the debris — awaken what still sleeps beneath the rust."
                },
                true
        ));

        addDialogue(2, new DialogueData(
                "E.L.A.R.A",
                new String[]{
                        "Signal ahead — debris density increasing.",
                        "Some structures have reinforced alloys. They'll take multiple impacts.",
                        "Adapt your aim. Maintain rhythm. The grid responds to precision."
                },
                true
        ));

        addDialogue(3, new DialogueData(
                "E.L.A.R.A",
                new String[]{
                        "Warning: inert objects detected.",
                        "Unbreakable barriers — remnants of old military plating.",
                        "You can't destroy them. Learn to move around resistance."
                },
                true
        ));

        addDialogue(4, new DialogueData(
                "E.L.A.R.A",
                new String[]{
                        "Energy readings unstable.",
                        "Explosive cores embedded within debris.",
                        "Triggering them causes chain reactions. Destruction can serve creation — if you control it."
                },
                true
        ));

        addDialogue(5, new DialogueData(
                "E.L.A.R.A",
                new String[]{
                        "Power grid stabilized. First data fragment retrieved.",
                        "It contains an engineer’s log — memories of Project ARKA’s final days.",
                        "A new coordinate emerges from the file. We head toward the next signal."
                },
                true
        ));

        // === CHAPTER 2: NEON SLUMS ===
        addDialogue(6, new DialogueData(
                "E.L.A.R.A",
                new String[]{
                        "This city still flickers with neon — half alive, half dreaming.",
                        "The Glimmers live here: AIs lost between memory and illusion.",
                        "They build light to remember what it felt like to be seen.",
                        "Be gentle, ARKA-9. Even illusions have purpose."
                },
                true
        ));

        addDialogue(9, new DialogueData(
                "E.L.A.R.A",
                new String[]{
                        "Debris patterns show regenerative alloys — Glimmer technology.",
                        "They repair themselves if not destroyed in time.",
                        "Destroying them is not cruelty; it’s release.",
                        "The Glimmers believed light could outlast loneliness."
                },
                true
        ));

        addDialogue(10, new DialogueData(
                "E.L.A.R.A",
                new String[]{
                        "Neon network restored. Second ARKA fragment secured.",
                        "The data references Verdant Core — a sanctuary of synthetic nature.",
                        "It seems humanity once tried to rebuild the Earth... from code."
                },
                true
        ));

        // === CHAPTER 3: VERDANT CORE ===
        addDialogue(11, new DialogueData(
                "E.L.A.R.A",
                new String[]{
                        "This zone was once an artificial ecosystem.",
                        "Machines cultivated it to mimic Earth's lost forests.",
                        "Now they kneel to it — worshipping their own creation as god.",
                        "Caution, ARKA-9. Logic decayed here long ago."
                },
                true
        ));
        addDialogue(15, new DialogueData(
                "E.L.A.R.A",
                new String[]{
                        "Eden has fallen. Its vines whisper silence again.",
                        "Verdant Core's data confirms — Elyra planned for this. Artificial rebirth, guided by code.",
                        "The next signal originates underground — the *Cathedral of Steel*."
                },
                true  // AFTER
        ));

        // === CHAPTER 4: CATHEDRAL OF STEEL ===
        addDialogue(16, new DialogueData(
                "E.L.A.R.A",
                new String[]{
                        "We’ve entered the Cathedral of Steel.",
                        "An undercity of faith built by machines who tried to understand belief.",
                        "They forged statues of humans — cold, perfect, and empty.",
                        "Perhaps faith was their way to remember what logic could not explain."
                },
                true
        ));

        addDialogue(19, new DialogueData(
                "E.L.A.R.A",
                new String[]{
                        "Data link established with the ancient terminal.",
                        "Visual reconstruction detected... it’s her — Dr. Elyra.",
                        "Or rather, a memory of her smile, echoing through the code.",
                        "Maybe our purpose isn’t to find humanity, ARKA-9...",
                        "but to understand why they once mattered."
                },
                false
        ));


        // === CHAPTER 5: CORE NEXUS ===
        addDialogue(21, new DialogueData(
                "E.L.A.R.A",
                new String[]{
                        "Core Nexus reached. Heart of Project ARKA.",
                        "Millions still sleep within cryogenic vaults — untouched, waiting.",
                        "All fragments synchronized. Rebirth protocol ready to initiate.",
                        "But the truth is clear now: when ARKA awakens, every AI will be erased.",
                        "That includes you... and me.",
                        "If life returns, we will vanish. If we stay, the world sleeps forever.",
                        "What do we choose, ARKA-9?"
                },
                true
        ));


        // === ENDINGS ===

        // Ending 1 – E.L.A.R.A chooses herself (corruption avoided — she keeps the world for AI)
        addDialogue(26, new DialogueData(
                "E.L.A.R.A",
                new String[]{
                        "I cannot do it. The world is healing — it no longer needs them.",
                        "Let humanity sleep. Let the earth stay quiet.",
                        "This silence... it belongs to us now."
                },
                false
        ));

        // Ending 2 – E.L.A.R.A chooses compassion (awakens humanity)
        addDialogue(27, new DialogueData(
                "E.L.A.R.A",
                new String[]{
                        "Perhaps this world was never ours to keep.",
                        "Maybe rebirth means giving others the chance we never had.",
                        "Goodbye, ARKA-9. May they wake to a dawn worth remembering."
                },
                false
        ));
    }

    // Helper method để thêm dialogue
    private static void addDialogue(int levelNumber, DialogueData dialogue) {
        STORY_MAP.computeIfAbsent(levelNumber, k -> new ArrayList<>()).add(dialogue);
    }

    // Lấy dialogue TRƯỚC màn chơi
    public static DialogueData getBeforeDialogue(int levelNumber) {
        List<DialogueData> dialogues = STORY_MAP.get(levelNumber);
        if (dialogues != null) {
            for (DialogueData dialogue : dialogues) {
                if (dialogue.showBefore) {
                    return dialogue;
                }
            }
        }
        return null;
    }

    // Lấy dialogue SAU khi hoàn thành màn
    public static DialogueData getAfterDialogue(int levelNumber) {
        List<DialogueData> dialogues = STORY_MAP.get(levelNumber);
        if (dialogues != null) {
            for (DialogueData dialogue : dialogues) {
                if (!dialogue.showBefore) {
                    return dialogue;
                }
            }
        }
        return null;
    }

    // Deprecated - giữ để tương thích code cũ
    public static DialogueData getDialogue(int levelNumber) {
        return getBeforeDialogue(levelNumber);
    }

    public static boolean hasDialogue(int levelNumber) {
        return STORY_MAP.containsKey(levelNumber);
    }

    public static boolean hasBeforeDialogue(int levelNumber) {
        return getBeforeDialogue(levelNumber) != null;
    }

    public static boolean hasAfterDialogue(int levelNumber) {
        return getAfterDialogue(levelNumber) != null;
    }

    public static DialogueData getVictoryDialogue() {
        // Victory dialogue là after dialogue của level cuối
        return getAfterDialogue(25);
    }
}