package com.ichorpowered.clavier;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.data.type.InstrumentType;
import org.spongepowered.api.data.type.InstrumentTypes;
import org.spongepowered.api.effect.Viewer;
import org.spongepowered.api.effect.sound.PitchModulation;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Plugin(
        id = "clavier",
        name = "Clavier",
        version = "0.1.1",
        description = "Allows you to play music through a chat interface.",
        url = "http://ichorpowered.org",
        authors = {
                "Meronat"
        }
)
public class Clavier {

    private static final Text SPACE = Text.of("         ");

    private final Map<UUID, InstrumentType> instrumentChoice = new HashMap<>();
    private final Set<UUID> aloud = new HashSet<>();

    private final Text instruments = Text.builder()
            .append(SPACE)
            .append(SPACE)
            .append(Text.builder("[Piano]").color(TextColors.GOLD).onClick(TextActions.executeCallback(s -> setInstrument(s, InstrumentTypes.HARP))).onHover(TextActions.showText(Text.of(TextColors.BLUE, "Set your instrument to piano!"))).build())
            .append(Text.of("            "))
            .append(Text.builder("[String Bass]").color(TextColors.GOLD).onClick(TextActions.executeCallback(s -> setInstrument(s, InstrumentTypes.BASS_ATTACK))).onHover(TextActions.showText(Text.of(TextColors.BLUE, "Set your instrument to string bass!"))).build())
            .append(Text.NEW_LINE)
            .append(SPACE)
            .append(SPACE)
            .append(Text.builder("[Snare Drum]").color(TextColors.GOLD).onClick(TextActions.executeCallback(s -> setInstrument(s, InstrumentTypes.SNARE))).onHover(TextActions.showText(Text.of(TextColors.BLUE, "Set your instrument to snare drum!"))).build())
            .append(Text.of("    "))
            .append(Text.builder("[Clicks and Sticks]").color(TextColors.GOLD).onClick(TextActions.executeCallback(s -> setInstrument(s, InstrumentTypes.HIGH_HAT))).onHover(TextActions.showText(Text.of(TextColors.BLUE, "Set your instrument to sticks and clicks!"))).build())
            .append(Text.NEW_LINE)
            .append(SPACE)
            .append(SPACE)
            .append(Text.builder("[Base Drum]").color(TextColors.GOLD).onClick(TextActions.executeCallback(s -> setInstrument(s, InstrumentTypes.BASS_DRUM))).onHover(TextActions.showText(Text.of(TextColors.BLUE, "Set your instrument to bass drum!"))).build())
            .append(Text.of("      "))
            .append(Text.builder("[Bell]").color(TextColors.GOLD).onClick(TextActions.executeCallback(s -> setInstrument(s, InstrumentTypes.BELL))).onHover(TextActions.showText(Text.of(TextColors.BLUE, "Set your instrument to bell!"))).build())
            .append(Text.NEW_LINE)
            .append(SPACE)
            .append(SPACE)
            .append(Text.builder("[Flute]").color(TextColors.GOLD).onClick(TextActions.executeCallback(s -> setInstrument(s, InstrumentTypes.FLUTE))).onHover(TextActions.showText(Text.of(TextColors.BLUE, "Set your instrument to flute!"))).build())
            .append(Text.of("            "))
            .append(Text.builder("[Chime]").color(TextColors.GOLD).onClick(TextActions.executeCallback(s -> setInstrument(s, InstrumentTypes.CHIME))).onHover(TextActions.showText(Text.of(TextColors.BLUE, "Set your instrument to chime!"))).build())
            .append(Text.NEW_LINE)
            .append(SPACE)
            .append(SPACE)
            .append(Text.builder("[Guitar]").color(TextColors.GOLD).onClick(TextActions.executeCallback(s -> setInstrument(s, InstrumentTypes.GUITAR))).onHover(TextActions.showText(Text.of(TextColors.BLUE, "Set your instrument to guitar!"))).build())
            .append(Text.of("           "))
            .append(Text.builder("[Xylophone]").color(TextColors.GOLD).onClick(TextActions.executeCallback(s -> setInstrument(s, InstrumentTypes.XYLOPHONE))).onHover(TextActions.showText(Text.of(TextColors.BLUE, "Set your instrument to xylophone!"))).build())
            .build();

    private final Text keyboard = Text.builder()
            .append(SPACE)
            .append(Text.builder("█").color(TextColors.BLACK).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.FSHARP0))).onHover(TextActions.showText(Text.of("F#"))).build())
            .append(Text.builder("█").color(TextColors.WHITE).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.G0))).onHover(TextActions.showText(Text.of("G"))).build())
            .append(Text.builder("█").color(TextColors.BLACK).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.GSHARP0))).onHover(TextActions.showText(Text.of("G#"))).build())
            .append(Text.builder("█").color(TextColors.WHITE).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.A0))).onHover(TextActions.showText(Text.of("A"))).build())
            .append(Text.builder("█").color(TextColors.BLACK).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.ASHARP0))).onHover(TextActions.showText(Text.of("A#"))).build())
            .append(Text.builder("█").color(TextColors.WHITE).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.B0))).onHover(TextActions.showText(Text.of("B"))).build())
            .append(Text.builder("█").color(TextColors.WHITE).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.C1))).onHover(TextActions.showText(Text.of("C"))).build())
            .append(Text.builder("█").color(TextColors.BLACK).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.CSHARP1))).onHover(TextActions.showText(Text.of("C#"))).build())
            .append(Text.builder("█").color(TextColors.WHITE).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.D1))).onHover(TextActions.showText(Text.of("D"))).build())
            .append(Text.builder("█").color(TextColors.BLACK).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.DSHARP1))).onHover(TextActions.showText(Text.of("D#"))).build())
            .append(Text.builder("█").color(TextColors.WHITE).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.E1))).onHover(TextActions.showText(Text.of("E"))).build())
            .append(Text.builder("█").color(TextColors.WHITE).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.F1))).onHover(TextActions.showText(Text.of("F"))).build())
            .append(Text.builder("█").color(TextColors.BLACK).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.FSHARP1))).onHover(TextActions.showText(Text.of("F#"))).build())
            .append(Text.builder("█").color(TextColors.WHITE).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.G1))).onHover(TextActions.showText(Text.of("G"))).build())
            .append(Text.builder("█").color(TextColors.BLACK).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.GSHARP1))).onHover(TextActions.showText(Text.of("G#"))).build())
            .append(Text.builder("█").color(TextColors.WHITE).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.A1))).onHover(TextActions.showText(Text.of("A"))).build())
            .append(Text.builder("█").color(TextColors.BLACK).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.ASHARP1))).onHover(TextActions.showText(Text.of("A#"))).build())
            .append(Text.builder("█").color(TextColors.WHITE).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.B1))).onHover(TextActions.showText(Text.of("B"))).build())
            .append(Text.builder("█").color(TextColors.WHITE).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.C2))).onHover(TextActions.showText(Text.of("C"))).build())
            .append(Text.builder("█").color(TextColors.BLACK).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.CSHARP2))).onHover(TextActions.showText(Text.of("C#"))).build())
            .append(Text.builder("█").color(TextColors.WHITE).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.D2))).onHover(TextActions.showText(Text.of("D"))).build())
            .append(Text.builder("█").color(TextColors.BLACK).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.DSHARP2))).onHover(TextActions.showText(Text.of("D#"))).build())
            .append(Text.builder("█").color(TextColors.WHITE).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.E2))).onHover(TextActions.showText(Text.of("E"))).build())
            .append(Text.builder("█").color(TextColors.WHITE).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.F2))).onHover(TextActions.showText(Text.of("F"))).build())
            .append(Text.builder("█").color(TextColors.BLACK).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.FSHARP2))).onHover(TextActions.showText(Text.of("F#"))).build())
            .build();

    private final Text shortKeyboard = Text.builder()
            .append(SPACE)
            .append(Text.builder("▀").color(TextColors.BLACK).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.FSHARP0))).onHover(TextActions.showText(Text.of("F#"))).build())
            .append(Text.builder("█").color(TextColors.WHITE).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.G0))).onHover(TextActions.showText(Text.of("G"))).build())
            .append(Text.builder("▀").color(TextColors.BLACK).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.GSHARP0))).onHover(TextActions.showText(Text.of("G#"))).build())
            .append(Text.builder("█").color(TextColors.WHITE).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.A0))).onHover(TextActions.showText(Text.of("A"))).build())
            .append(Text.builder("▀").color(TextColors.BLACK).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.ASHARP0))).onHover(TextActions.showText(Text.of("A#"))).build())
            .append(Text.builder("█").color(TextColors.WHITE).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.B0))).onHover(TextActions.showText(Text.of("B"))).build())
            .append(Text.builder("█").color(TextColors.WHITE).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.C1))).onHover(TextActions.showText(Text.of("C"))).build())
            .append(Text.builder("▀").color(TextColors.BLACK).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.CSHARP1))).onHover(TextActions.showText(Text.of("C#"))).build())
            .append(Text.builder("█").color(TextColors.WHITE).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.D1))).onHover(TextActions.showText(Text.of("D"))).build())
            .append(Text.builder("▀").color(TextColors.BLACK).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.DSHARP1))).onHover(TextActions.showText(Text.of("D#"))).build())
            .append(Text.builder("█").color(TextColors.WHITE).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.E1))).onHover(TextActions.showText(Text.of("E"))).build())
            .append(Text.builder("█").color(TextColors.WHITE).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.F1))).onHover(TextActions.showText(Text.of("F"))).build())
            .append(Text.builder("▀").color(TextColors.BLACK).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.FSHARP1))).onHover(TextActions.showText(Text.of("F#"))).build())
            .append(Text.builder("█").color(TextColors.WHITE).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.G1))).onHover(TextActions.showText(Text.of("G"))).build())
            .append(Text.builder("▀").color(TextColors.BLACK).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.GSHARP1))).onHover(TextActions.showText(Text.of("G#"))).build())
            .append(Text.builder("█").color(TextColors.WHITE).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.A1))).onHover(TextActions.showText(Text.of("A"))).build())
            .append(Text.builder("▀").color(TextColors.BLACK).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.ASHARP1))).onHover(TextActions.showText(Text.of("A#"))).build())
            .append(Text.builder("█").color(TextColors.WHITE).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.B1))).onHover(TextActions.showText(Text.of("B"))).build())
            .append(Text.builder("█").color(TextColors.WHITE).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.C2))).onHover(TextActions.showText(Text.of("C"))).build())
            .append(Text.builder("▀").color(TextColors.BLACK).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.CSHARP2))).onHover(TextActions.showText(Text.of("C#"))).build())
            .append(Text.builder("█").color(TextColors.WHITE).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.D2))).onHover(TextActions.showText(Text.of("D"))).build())
            .append(Text.builder("▀").color(TextColors.BLACK).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.DSHARP2))).onHover(TextActions.showText(Text.of("D#"))).build())
            .append(Text.builder("█").color(TextColors.WHITE).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.E2))).onHover(TextActions.showText(Text.of("E"))).build())
            .append(Text.builder("█").color(TextColors.WHITE).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.F2))).onHover(TextActions.showText(Text.of("F"))).build())
            .append(Text.builder("▀").color(TextColors.BLACK).onClick(TextActions.executeCallback(s -> play(s, PitchModulation.FSHARP2))).onHover(TextActions.showText(Text.of("F#"))).build())
            .build();



    @Listener
    public void onGameInit(final GameInitializationEvent event) {
        // A musician is a person who goes around forcing their will upon unsuspecting air molecules -- Frank Zappa
        // todo pull quotes from file
        // todo play particles with keyboard
        // todo import from nbs, store those songs, play those
        // record people playing songs, give option to speed up or slow done
        // support storing as nbs
        // explore other sounds

        final CommandSpec play = CommandSpec.builder()
                .permission("clavier.play.use")
                .description(Text.of("Let's you play music through chat"))
                .extendedDescription(Text.of(TextColors.BLUE, "Allows you to play music through a keyboard in chat."))
                .arguments(
                        GenericArguments.optionalWeak(GenericArguments.onlyOne(
                                GenericArguments.catalogedElement(Text.of("instrument"), InstrumentType.class))),
                        GenericArguments.flags().permissionFlag("clavier.aloud", "o").buildWith(GenericArguments.none()))
                .executor((src, args) -> {
                    final Player player = isPlayer(src);

                    args.<InstrumentType>getOne("instrument").ifPresent(t-> this.instrumentChoice.put(player.getUniqueId(), t));

                    if (args.hasAny("o")) {
                        this.aloud.add(player.getUniqueId());
                    }

                    PaginationList.builder()
                            .title(Text.of(TextColors.GOLD, "Clavier"))
                            .padding(Text.of(TextColors.BLUE, "="))
                            .linesPerPage(18)
                            .contents(Text.EMPTY, Text.EMPTY, Text.EMPTY, this.instruments, Text.EMPTY, Text.EMPTY, Text.EMPTY, this.keyboard, this.keyboard, this.keyboard, this.shortKeyboard, Text.EMPTY, Text.EMPTY, Text.EMPTY)
                            .sendTo(player);

                    return CommandResult.success();
                })
                .build();

        final CommandSpec setInstrument = CommandSpec.builder()
                .permission("clavier.play.instrument")
                .description(Text.of("Set the instrument you are using to play with"))
                .extendedDescription(Text.of(TextColors.BLUE, "Sets the instrument you are using to play the chat keyboard with."))
                .arguments(GenericArguments.onlyOne(GenericArguments.catalogedElement(Text.of("instrument"), InstrumentType.class)))
                .executor((src, args) -> {
                    final Player player = isPlayer(src);

                    final Optional<InstrumentType> instrumentType = args.getOne("instrument");

                    if (!instrumentType.isPresent()) {
                        throw new CommandException(Text.of(TextColors.RED, "You must specify a proper instrument type."));
                    }

                    this.instrumentChoice.put(player.getUniqueId(), instrumentType.get());

                    player.sendMessage(Text.of(TextColors.BLUE, "You have successfully set your instrument type to ",
                            TextColors.GOLD, instrumentType.get().getName(), TextColors.BLUE, "!"));

                    return CommandResult.success();
                })
                .build();

        final CommandSpec aloud = CommandSpec.builder()
                .permission("clavier.aloud")
                .description(Text.of("Makes it so your music played with clavier is played out loud"))
                .extendedDescription(Text.of(TextColors.GOLD, "Makes it so all music you play with clavier is played out loud instead of just to you."))
                .executor((src, args) -> {
                    final UUID uuid = isPlayer(src).getUniqueId();

                    if (this.aloud.contains(uuid)) {
                        this.aloud.remove(uuid);
                        src.sendMessage(Text.of(TextColors.BLUE, "The music you play will no longer be played out loud."));
                    } else {
                        this.aloud.add(uuid);
                        src.sendMessage(Text.of(TextColors.BLUE, "The music you play will now be played out loud."));
                    }

                    return CommandResult.success();
                })
                .build();

        Sponge.getCommandManager().register(this, CommandSpec.builder()
                .permission("clavier")
                .description(Text.of("The base clavier command"))
                .extendedDescription(Text.of(TextColors.BLUE, "The base clavier command used to play instruments."))
                .child(play, "play", "keyboard")
                .child(setInstrument, "instrument")
                .child(aloud, "aloud", "aloud")
                .executor((src, args) -> {
                    src.sendMessage(Text.of(TextColors.BLUE, "\"Where words fail, music speaks.\"", TextColors.GOLD, " - Hans Christian Andersen"));

                    return CommandResult.success();
                })
                .build(), "clavier");
    }

    private void play(CommandSource commandSource, double pitch) {
        final Player player = (Player) commandSource;
        final Viewer viewer;
        if (this.aloud.contains(player.getUniqueId())) {
            viewer = player;
        } else {
            viewer = player.getWorld();
        }

        viewer.playSound(this.instrumentChoice.getOrDefault(player.getUniqueId(), InstrumentTypes.HARP).getSound(), player.getLocation().getPosition(), 1.75, pitch);
    }

    private Player isPlayer(CommandSource commandSource) throws CommandException {
        if (commandSource instanceof Player) {
            return (Player) commandSource;
        } else {
            throw new CommandException(Text.of(TextColors.RED, "You must be a player to execute this command!"));
        }
    }

    private void setInstrument(CommandSource s, InstrumentType instrumentType) {
        final Player player = (Player) s;
        if (player.hasPermission("clavier.play.instrument")) {
            this.instrumentChoice.put(player.getUniqueId(), instrumentType);
        }
    }

}
