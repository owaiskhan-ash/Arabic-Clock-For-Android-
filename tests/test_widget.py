"""Structural regression checks; Android/device behavior is tested separately."""
import unittest
from pathlib import Path
import xml.etree.ElementTree as ET
from fontTools.ttLib import TTFont

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / 'app/src/main/res'
A = '{http://schemas.android.com/apk/res/android}'

class WidgetTests(unittest.TestCase):
    def test_fonts_always_render_arabic_indic_digits(self):
        for path in (RES / 'font').glob('*.ttf'):
            with self.subTest(font=path.name):
                font = TTFont(path)
                cmap = font.getBestCmap()
                for i in range(10):
                    self.assertEqual(cmap[48+i], cmap[0x660+i])
                    self.assertEqual(cmap[0x6f0+i], cmap[0x660+i])
                self.assertTrue(font['name'].getDebugName(1).startswith('Arabic Clock'))

    def test_all_layouts_transparent_and_only_digits_stretch(self):
        layouts = list((RES / 'layout').glob('widget_*.xml'))
        self.assertEqual(len(layouts), 6)
        for path in layouts:
            with self.subTest(layout=path.name):
                root = ET.parse(path).getroot()
                self.assertEqual(root.get(A+'background'), '@android:color/transparent')
                clocks = root.findall('TextClock')
                self.assertEqual(len(clocks), 2)
                self.assertEqual([c.get(A+'format24Hour') for c in clocks], ['HH', 'mm'])
                self.assertEqual([c.get(A+'format12Hour') for c in clocks], ['HH', 'mm'])
                self.assertEqual(root.find('TextView').get(A+'text'), ':')
                self.assertIsNone(root.find('TextView').get(A+'textScaleX'))
                expected = ['1.0', '1.15', '1.3'][int(path.stem[-1])]
                for clock in clocks:
                    self.assertEqual(clock.get(A+'textScaleX'), expected)

    def test_provider_is_eligible_for_both_hosts_without_polling(self):
        root = ET.parse(RES / 'xml/clock_widget.xml').getroot()
        self.assertEqual(root.get(A+'widgetCategory'), 'home_screen|keyguard')
        self.assertEqual(root.get(A+'updatePeriodMillis'), '0')
        manifest = ET.parse(ROOT / 'app/src/main/AndroidManifest.xml').getroot()
        self.assertEqual(manifest.findall('uses-permission'), [])

if __name__ == '__main__':
    unittest.main()
