# Font attribution

The application source is MIT-licensed. The Amiri and Scheherazade subsets are separately licensed
under the SIL Open Font License 1.1. Janna LT Bold is proprietary; see below.

| Bundled family | Original | Source |
| --- | --- | --- |
| Arabic Clock Long | Amiri Regular | https://github.com/google/fonts/tree/main/ofl/amiri |
| Arabic Clock Classic | Scheherazade New Regular | https://github.com/google/fonts/tree/main/ofl/scheherazadenew |

These are modified, digit-only subsets. ASCII and Persian digit codepoints are
mapped to Arabic-Indic glyphs, and font family/PostScript names are changed.
The original copyright and license metadata are retained. The app applies
horizontal scaling at rendering time; the font outlines themselves are not stretched.

To regenerate, download each original Regular TTF from the directories above,
install Python `fonttools`, and run:

```
python tools/prepare_fonts.py /path/to/Amiri-Regular.ttf /path/to/ScheherazadeNew-Regular.ttf
```

## Janna LT Bold (proprietary)

The repository owner supplied `Janna LT Bold.ttf` and confirmed Android app
embedding and redistribution permission. It is bundled **without modification**
as `res/font/janna_lt_bold.ttf`. It is not an OFL font, and the app's MIT license
does not apply to it. See [the preserved notices](Janna-LT-Bold-NOTICE.txt).
The owner's confirmation does not grant downstream users a font license;
independent reuse or redistribution requires applicable permission.
