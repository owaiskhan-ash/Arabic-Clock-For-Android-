"""Usage: python tools/prepare_fonts.py /path/to/amiri.ttf /path/to/scheherazadenew.ttf
Requires fonttools. Sources: google/fonts ofl/amiri and ofl/scheherazadenew.
Map Latin and Persian digit codepoints to Arabic-Indic outlines so TextClock
keeps native minute/time-zone updates regardless of the device's locale.
Modified fonts use new family names as required by their OFL licenses.
"""
import sys
from fontTools.ttLib import TTFont
from fontTools import subset
for source, output, family in zip(sys.argv[1:], ['long_arabic', 'classic_arabic'],
                                  ['Arabic Clock Long', 'Arabic Clock Classic']):
    font = TTFont(source)
    cmap = font.getBestCmap()
    digits = [cmap[0x660 + n] for n in range(10)]
    for table in font['cmap'].tables:
        if table.isUnicode():
            for n, glyph in enumerate(digits):
                table.cmap[48 + n] = glyph
                table.cmap[0x6f0 + n] = glyph
    for record in font['name'].names:
        if record.nameID in (1, 2, 3, 4, 6, 16, 17):
            value = 'Regular' if record.nameID in (2, 17) else family
            if record.nameID == 6: value = family.replace(' ', '') + '-Regular'
            record.string = value.encode(record.getEncoding())
    options = subset.Options()
    options.name_IDs = ['*']
    options.name_legacy = True
    sub = subset.Subsetter(options=options)
    sub.populate(unicodes=list(range(48, 58)) + list(range(0x660, 0x66a)) +
                 list(range(0x6f0, 0x6fa)) + [32, 58])
    sub.subset(font)
    font.save('app/src/main/res/font/' + output + '.ttf')
