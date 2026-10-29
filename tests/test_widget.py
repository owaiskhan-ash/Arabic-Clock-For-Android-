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
        for path in [RES / 'font/long_arabic.ttf', RES / 'font/classic_arabic.ttf']:
            with self.subTest(font=path.name):
                font = TTFont(path)
                cmap = font.getBestCmap()
                for i in range(10):
                    self.assertEqual(cmap[48+i], cmap[0x660+i])
                    self.assertEqual(cmap[0x6f0+i], cmap[0x660+i])
                self.assertTrue(font['name'].getDebugName(1).startswith('Arabic Clock'))

    def test_janna_is_unmodified_and_has_arabic_digits(self):
        path = RES / 'font/janna_lt_bold.ttf'
        self.assertEqual(path.read_bytes(), (ROOT / 'Fonts For clock/Janna LT Bold.ttf').read_bytes())
        font = TTFont(path)
        self.assertEqual(font['name'].getDebugName(1), 'Janna LT')
        self.assertEqual(font['name'].getDebugName(2), 'Bold')
        cmap = font.getBestCmap()
        for codepoint in range(0x660, 0x66a):
            self.assertIn(codepoint, cmap)
            self.assertNotEqual(cmap[codepoint], '.notdef')
        settings = (ROOT / 'app/src/main/java/com/arabicclock/app/WidgetSettings.java').read_text()
        self.assertIn('case 2: return R.font.janna_lt_bold;', settings)

    def test_new_fonts_are_unmodified_and_support_arabic(self):
        fonts = {
            'arslan_wessam': '(A) Arslan Wessam A (A) Arslan Wessam A.ttf',
            'khebrat_musamim': '18 Khebrat Musamim Regular.ttf',
            'avenir_heavy': 'Avenir Heavy.ttf',
        }
        for resource, filename in fonts.items():
            with self.subTest(font=resource):
                path = RES / 'font' / (resource + '.ttf')
                self.assertEqual(path.read_bytes(), (ROOT / 'Fonts For clock' / filename).read_bytes())
                cmap = TTFont(path).getBestCmap()
                for codepoint in range(0x660, 0x66a):
                    self.assertIn(codepoint, cmap)
                    self.assertNotEqual(cmap[codepoint], '.notdef')

    def test_widget_uses_bitmap_not_host_fonts(self):
        root = ET.parse(RES / 'layout/widget_bitmap.xml').getroot()
        self.assertEqual(root.get(A+'background'), '@android:color/transparent')
        self.assertIsNotNone(root.find('ImageView'))
        self.assertEqual(root.findall('.//TextClock'), [])
        provider = (ROOT / 'app/src/main/java/com/arabicclock/app/ClockWidgetProvider.java').read_text()
        self.assertIn('setImageViewBitmap', provider)
        renderer = (ROOT / 'app/src/main/java/com/arabicclock/app/ClockRenderer.java').read_text()
        self.assertIn('ClockTime.of(now', renderer)
        self.assertIn('getResources().getFont', renderer)
        time = (ROOT / 'app/src/main/java/com/arabicclock/app/ClockTime.java').read_text()
        self.assertIn('ArabicDigits.convert', time)
        self.assertIn('dots.setTextScaleX(1f)', renderer)

    def test_outline_and_opacity_rendering_contract(self):
        renderer = (ROOT / 'app/src/main/java/com/arabicclock/app/ClockRenderer.java').read_text()
        self.assertIn('canvas.saveLayerAlpha', renderer)
        self.assertIn('canvas.restoreToCount(layer)', renderer)
        self.assertIn('Paint.Style.STROKE', renderer)
        self.assertIn('Paint.Join.ROUND', renderer)
        self.assertIn('float extra = stroke / 2', renderer)
        self.assertIn('!settings.outline() || !settings.outlineOnly()', renderer)
        settings = (ROOT / 'app/src/main/java/com/arabicclock/app/WidgetSettings.java').read_text()
        self.assertIn('Math.max(0, Math.min(100, prefs.getInt("opacity", 100)))', settings)
        self.assertIn('prefs.getBoolean("outline", false)', settings)
        self.assertIn('prefs.getInt("outlineWidth", 2)', settings)

    def test_twelve_hour_option_contract(self):
        settings = (ROOT / 'app/src/main/java/com/arabicclock/app/WidgetSettings.java').read_text()
        self.assertIn('prefs.getInt("hourFormat", 0)', settings)
        self.assertIn('prefs.getInt("marker", ClockTime.MARKER_ARABIC)', settings)
        activity = (ROOT / 'app/src/main/java/com/arabicclock/app/MainActivity.java').read_text()
        self.assertIn('"Time format"', activity)
        self.assertIn('"AM/PM marker (12-hour only)"', activity)
        self.assertIn('"hourFormat"', activity)
        self.assertIn('"marker"', activity)
        time = (ROOT / 'app/src/main/java/com/arabicclock/app/ClockTime.java').read_text()
        # The formatter must stay pure so it is unit-testable and time-zone correct.
        self.assertNotIn('import android.', time)
        self.assertIn('ArabicDigits.convert(hours)', time)
        renderer = (ROOT / 'app/src/main/java/com/arabicclock/app/ClockRenderer.java').read_text()
        self.assertIn('ClockTime.of(now, settings.hour24(), settings.marker())', renderer)
        # The AM/PM marker must not use the numeral font: the Amiri subsets are
        # digit-only and lack ص/م, which would render as tofu.
        self.assertIn('marks.setTypeface(c.getResources().getFont(R.font.janna_lt_bold))', renderer)
        font = TTFont(RES / 'font/janna_lt_bold.ttf')
        cmap = font.getBestCmap()
        for codepoint in (0x635, 0x645, 0x41, 0x4D, 0x50):  # ص م A M P
            self.assertIn(codepoint, cmap)
            self.assertNotEqual(cmap[codepoint], '.notdef')

    def test_provider_hosts_and_refresh_permissions(self):
        root = ET.parse(RES / 'xml/clock_widget.xml').getroot()
        self.assertEqual(root.get(A+'widgetCategory'), 'home_screen|keyguard')
        self.assertEqual(root.get(A+'initialLayout'), '@layout/widget_bitmap')
        manifest = ET.parse(ROOT / 'app/src/main/AndroidManifest.xml').getroot()
        permissions = {e.get(A+'name') for e in manifest.findall('uses-permission')}
        self.assertEqual(permissions, {'android.permission.RECEIVE_BOOT_COMPLETED',
                                       'android.permission.SCHEDULE_EXACT_ALARM'})

if __name__ == '__main__':
    unittest.main()
