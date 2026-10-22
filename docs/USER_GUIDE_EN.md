# Remote controls

- Dock: Left/Right selects an icon; OK launches. Focus magnifies the icon and neighbors; collapse is scheduled after 3 seconds. Width is 60% of display pixels.
- Hold OK on a favorite, or open Launchpad → Edit Dock. Add/Remove and Left/Right buttons change favorites; changes save immediately. Done/Back exits. At least one favorite is retained; see the on-screen maximum.
- Launchpad lists apps with launcher/TV launcher activities; directional buttons select, OK opens, Back returns.
- Clock button opens the full-screen clock. Home idle entry depends on configuration. The system screensaver is separately selected in TV settings. The clock format is 12-hour; digits and AM/PM follow Android locale, so English display is not guaranteed on Persian-localized devices.
- News top-bar button: enable/disable, choose sources, topics, manual refresh. Topic selection intersects selected sources. Economy filters ISNA/Khabar titles by keywords; other topics use source categories.
- Focus the ticker: Left next, Right previous, OK opens the HTTPS article (TV Bro preferred; other browsers are a fallback), Up news settings, Down/Back Dock. Automatic advance is 13 seconds while unfocused. Refresh is attempted on resume and every five minutes while active and enabled.

These are code-reviewed behaviors, not a successful physical-remote test of this commit. Feed/browser/device compatibility remains to be tested. See [privacy and RSS limitations](PRIVACY_AND_RSS.md).
