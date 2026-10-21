/*
 * Copyright 2026 Morphe.
 * https://github.com/MorpheApp/morphe-patches
 *
 * See the included NOTICE file for GPLv3 Section 7 terms that apply to this code.
 */

package app.morphe.extension.youtube.patches;

import app.morphe.extension.youtube.settings.Settings;

@SuppressWarnings("unused")
public class CaptionsInPipPatch {

    private static final String CAPTIONS_OVERLAY = "player_overlay_caption";

    /**
     * Injection point. Called while in picture-in-picture, where YouTube hides most player overlays.
     */
    public static boolean isPipActive(String overlayName) {
        return !(Settings.CAPTIONS_IN_PIP.get() && CAPTIONS_OVERLAY.equals(overlayName));
    }

    /**
     * Injection point. Called while not in picture-in-picture.
     *
     * @return If the overlay is shown in the miniplayer, where YouTube hides the captions overlay.
     */
    public static boolean showInMiniplayer(String overlayName) {
        return Settings.CAPTIONS_IN_MINIPLAYER.get() && CAPTIONS_OVERLAY.equals(overlayName);
    }

    /**
     * Injection point.
     *
     * @return The player layout state to give the captions overlay, maximized instead of minimized.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Enum<?> getLayoutState(Enum<?> state) {
        String name = state.name();
        if (name.equals("MINIMIZED") || name.equals("SLIDING_MINIMIZED_MAXIMIZED")) {
            return Enum.valueOf((Class) state.getDeclaringClass(), "MAXIMIZED");
        }
        return state;
    }

    /**
     * Injection point. Used by versions that give overlays the player type instead.
     *
     * @return If the overlay must be shown, even though the player type hides it.
     */
    public static boolean showOverlay(String overlayName, Enum<?> playerType) {
        return switch (playerType.name()) {
            case "WATCH_WHILE_PICTURE_IN_PICTURE" -> !isPipActive(overlayName);
            case "WATCH_WHILE_MINIMIZED", "WATCH_WHILE_SLIDING_MINIMIZED_MAXIMIZED" -> showInMiniplayer(overlayName);
            default -> false;
        };
    }

    /**
     * Injection point. Older versions can set the current caption track again when entering or leaving
     * picture-in-picture, which clears the caption on screen until the next one.
     */
    public static boolean isCurrentTrack(Object track, Object currentTrack) {
        return Settings.CAPTIONS_IN_PIP.get() && track != null && track.equals(currentTrack);
    }
}
