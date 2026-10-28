# Font attribution

The application source is MIT-licensed. The bundled fonts are separately licensed
under the SIL Open Font License 1.1; see the accompanying full license files.

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
